package com.quest.etna.config.service;

import com.quest.etna.model.User;
import com.quest.etna.model.UserDetails;
import com.quest.etna.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public boolean isCurrentUser(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return false; // Si l'authentification n'est pas définie ou le principal n'est pas une instance de UserDetails
        }

        String currentUsername = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<User> userOptional = userRepository.findByUsername(currentUsername);

        if (userOptional.isEmpty()) {
            return false; // L'utilisateur n'est pas trouvé, donc retourner false
        }

        User user = userOptional.get(); // Obtient l'utilisateur s'il est présent
        System.out.println("L'user ID est " + user.getId());
        System.out.println("L'ID est  " + id);
        return Objects.equals(user.getId(), id); // Compare les IDs
    }

    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean delete(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public void logout() {
        System.out.println("Est-ce que j'use cette fonction ?");
        System.out.println("Le SecurityContext est " + SecurityContextHolder.getContext());
        SecurityContextHolder.clearContext();
        System.out.println("Puis le SecurityContext est " + SecurityContextHolder.getContext());

    }


}

