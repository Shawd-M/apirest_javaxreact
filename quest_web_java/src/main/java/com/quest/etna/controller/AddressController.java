package com.quest.etna.controller;

import com.quest.etna.config.service.AddressService;
import com.quest.etna.config.exception.ForbiddenAccessException;
import com.quest.etna.dto.AddressDTO;
import com.quest.etna.dto.UserDTO;
import com.quest.etna.model.Address;
import com.quest.etna.config.service.UserService;
import com.quest.etna.model.User;
import com.quest.etna.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.quest.etna.config.exception.ResourceNotFoundException;


import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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

    @GetMapping({"", "/{id}"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> getAddress(@PathVariable(required = false) Long id, Principal principal) {
        if (id != null) {
            Address address = addressService.findById(id.intValue()).orElseThrow(() -> new ResourceNotFoundException("Adresse non trouvée"));
            // Vérifier d'abord les permissions de l'utilisateur
            if (!hasRole("ROLE_ADMIN") && !Long.valueOf(address.getUser().getId()).equals(Long.valueOf(getUserIdFromPrincipal(principal)))) {
                throw new ForbiddenAccessException("Vous n'avez pas accès à cette adresse");
            }

            AddressDTO addressDTO = convertToAddressDTO(address);
            return ResponseEntity.ok(addressDTO);
        } else {
            List<Address> addresses = hasRole("ROLE_ADMIN") ? addressService.findAll() : addressService.findAddressesForUser(principal);
            List<AddressDTO> addressDTOs = addresses.stream()
                    .map(this::convertToAddressDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(addressDTOs);
        }
    }

    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setUser(convertToUserResponseDTO(address.getUser()));

        return dto;
    }

    private UserDTO convertToUserResponseDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }

    @PostMapping("")
    public ResponseEntity<?> createAddress(@RequestBody AddressDTO addressDTO, Principal principal) {
        Address newAddress = addressService.createAddress(addressDTO, principal);
        AddressDTO newAddressDTO = convertToAddressDTO(newAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAddressDTO);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable Long id, @RequestBody AddressDTO addressDTO, Principal principal) {
        Address address = addressService.findById(Math.toIntExact(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!hasRole("ROLE_ADMIN")) {
            Long userId = Long.valueOf(getUserIdFromPrincipal(principal));
            if (address.getUser().getId() != userId) {
                AddressDTO errorResponse = new AddressDTO();
                errorResponse.setError("You can only update your own addresses.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
        }

        if (addressDTO.getStreet() != null && !addressDTO.getStreet().equals(address.getStreet())) {
            address.setStreet(addressDTO.getStreet());
        }
        if (addressDTO.getPostalCode() != null && !addressDTO.getPostalCode().equals(address.getPostalCode())) {
            address.setPostalCode(addressDTO.getPostalCode());
        }
        if (addressDTO.getCity() != null && !addressDTO.getCity().equals(address.getCity())) {
            address.setCity(addressDTO.getCity());
        }
        if (addressDTO.getCountry() != null && !addressDTO.getCountry().equals(address.getCountry())) {
            address.setCountry(addressDTO.getCountry());
        }
        address = addressService.save(address);

        AddressDTO updatedAddressDTO = convertToAddressDTO(address);
        return ResponseEntity.ok(updatedAddressDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id, Principal principal) {
        if (!hasRole("ROLE_ADMIN")) {
            Address address = addressService.findById(Math.toIntExact(id)).orElse(null);
            if (address == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "Address not found."));
            }
            int userId = getUserIdFromPrincipal(principal);
            if (address.getUser().getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("error", "You can only delete your own addresses."));
            }
        }

        boolean success = addressService.delete(Math.toIntExact(id));
        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("success", false));
        }
    }
}
