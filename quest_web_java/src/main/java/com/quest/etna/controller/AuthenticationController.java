package com.quest.etna.controller;

import com.quest.etna.config.service.UserService;
import com.quest.etna.response.AuthResponse;
import com.quest.etna.response.ErrorResponse;
import com.quest.etna.response.Response;
import com.quest.etna.dto.UserDTO;
import com.quest.etna.model.JwtUserDetails;
import com.quest.etna.model.User;
import com.quest.etna.model.UserDetails;
import com.quest.etna.model.UserRole;
import com.quest.etna.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.quest.etna.config.token.JwtTokenUtil;
import java.util.logging.Logger;

@RestController
public class AuthenticationController {

    private final UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Autowired
    public AuthenticationController(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = Logger.getLogger(AuthenticationController.class.getName());

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            user.setUsername(user.getUsername().toLowerCase());
            // Si l'utilisateur tente de s'enregistrer sans mot de passe ou nom d'utilisateur
            if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Mot de passe requis / Username requis"));
            }

            // Vérifier si l'utilisateur existe déjà
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("L'utilisateur existe déjà."));
            }

            // Si le rôle n'est pas ROLE_ADMIN ou ROLE_SELLER, utiliser ROLE_USER par défaut
            if (!user.getRole().equalsIgnoreCase("ROLE_ADMIN") && !user.getRole().equalsIgnoreCase("ROLE_SELLER")) {
                user.setRole(UserRole.ROLE_USER.toString()); // Définir le rôle par défaut si ce n'est pas ROLE_ADMIN ou ROLE_SELLER
            }

            // Enregistrer l'utilisateur
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            User savedUser = userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(new UserDetails(Math.toIntExact(savedUser.getId()), savedUser.getUsername(), savedUser.getRole()));
        } catch (Exception e) {
            // Retourner une réponse avec le code HTTP 400 (Bad Request) en cas d'erreur
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Erreur lors de l'enregistrement de l'utilisateur : " + e.getMessage()));
        }
    }


    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!(authentication.getPrincipal() instanceof JwtUserDetails jwtUserDetails)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Erreur dans les détails de l'utilisateur"));
        }

        UserDetails userDetails = new UserDetails(Math.toIntExact(jwtUserDetails.getId()), jwtUserDetails.getUsername(), jwtUserDetails.getRole());
        return ResponseEntity.status(HttpStatus.OK).body(userDetails);
    }

    @PostMapping("/out")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout(HttpServletRequest request) {
            SecurityContextHolder.clearContext();
            Response response = new Response("Vous êtes bien déconnecté");
            return ResponseEntity.ok(response);

    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody User user) {
        try {
            user.setUsername(user.getUsername().toLowerCase());
            // Vérifie si l'utilisateur a fourni à la fois un nom d'utilisateur et un mot de passe
            if (user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Nom d'utilisateur et mot de passe requis"));
            }

            System.out.println("Tentative d'authentification pour l'utilisateur : " + user.getUsername());

            User foundUser = userRepository.findByUsernameCaseSensitive(user.getUsername());
            if (foundUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Nom d'utilisateur ou mot de passe incorrect"));
            }

            // Authentification de l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            // Création du UserDTO à partir de l'utilisateur authentifié
            UserDTO userDTO = convertToUserDTO(foundUser);

            // Génération du token JWT
            String token = jwtTokenUtil.generateToken(user.getUsername());
            //System.out.println("Le token JWT généré est : " + token);

            // Retourne le token avec le code HTTP 200 (OK)
            return ResponseEntity.ok(new AuthResponse(token, userDTO));
            //return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (AuthenticationException e) {
            // En cas d'échec de l'authentification, retourne une réponse avec le code HTTP 401 (Unauthorized)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Nom d'utilisateur ou mot de passe incorrect"));
        }
    }


    private UserDTO convertToUserDTO(User user) {
        if (user == null) {
            System.out.println("L'utilisateur fourni est null");
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole() != null ? user.getRole() : "ROLE_USER"); // Initialiser le rôle avec une valeur par défaut si null
        return userDTO;
    }
}
