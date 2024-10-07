package com.quest.etna.controller;

import com.quest.etna.config.token.JwtTokenUtil;
import com.quest.etna.config.token.JwtUserDetailsService;
import com.quest.etna.config.service.UserService;
import com.quest.etna.dto.AddressDTO;
import com.quest.etna.dto.OrderDTO;
import com.quest.etna.dto.UserDTO;
import com.quest.etna.dto.UserDetailsDTO;
import com.quest.etna.model.Address;
import com.quest.etna.model.Order;
import com.quest.etna.model.User;
import com.quest.etna.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUserDetailsService JwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping({"", "/{id}"})
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == principal.id)")
    public ResponseEntity<?> getUser(@PathVariable(required = false) Long id) {
        if (id == null) {
            List<User> users = userService.findAll();
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> {
                        UserDTO userDTO = new UserDTO();
                        userDTO.setId(user.getId());
                        userDTO.setUsername(user.getUsername());
                        userDTO.setRole(user.getRole());
                        return userDTO;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(userDTOs);
        } else {
            Optional<User> userOptional = userService.findById(Math.toIntExact(id));
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                //UserDTO userDTO = new UserDTO();
                UserDTO responseDTO = convertToUserDTO(user);
                //userDTO.setUsername(user.getUsername());
                //userDTO.setRole(user.getRole());

                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == principal.id)")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<User> userOptional = userRepository.findById(Math.toIntExact(id));
        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        if (!user.getUsername().equals(currentUsername) && !authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only update your own information.");
        }

        user.setUsername(userDetails.getUsername());

        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        User updatedUser = userRepository.save(user);

        // Mettre à jour le contexte de sécurité
        String newToken = jwtTokenUtil.generateToken(updatedUser.getUsername());

        UserDTO responseDTO = convertToResponseDTO(updatedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", responseDTO);
        response.put("token", newToken);

        return ResponseEntity.ok(response);
    }


    private UserDTO convertToResponseDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }

    private Integer getUserIdFromPrincipal(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    // DELETE : Suppression d'un utilisateur
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #id == principal.id)")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Principal principal) {
        if (!hasRole("ROLE_ADMIN")) {
            Optional<User> user = userService.findById(Math.toIntExact(id));
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Address not found."));
            }
            int userId = getUserIdFromPrincipal(principal);
            if (userId != id) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", "You can only delete your own account."));
            }
        }
        boolean success = userService.delete(Math.toIntExact(id));
        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }
    }


    private UserDetailsDTO convertToUserDTO(User user) {
        UserDetailsDTO dto = new UserDetailsDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());

        Set<Address> addresses = user.getAddress();
        if (addresses != null && !addresses.isEmpty()) {
            Set<AddressDTO> addressDTOs = addresses.stream()
                    .map(this::convertToAddressDTO)
                    .collect(Collectors.toSet());
            dto.setAddress(addressDTOs);
        }

        return dto;
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        return dto;
    }
}
