package com.quest.etna.config.service;

import com.quest.etna.dto.AddressDTO;
import com.quest.etna.model.Address;
import com.quest.etna.model.User;
import com.quest.etna.repositories.AddressRepository;
import com.quest.etna.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    public Optional<Address> findById(int id) {
        return addressRepository.findById(id);
    }

    public Address save(Address address) {
        return addressRepository.save(address);
    }

    public Address update(int id, Address addressDetails) {
        Address address = addressRepository.findById(id).orElseThrow(() -> new RuntimeException("Address not found"));
        address.setStreet(addressDetails.getStreet());
        address.setPostalCode(addressDetails.getPostalCode());
        address.setCity(addressDetails.getCity());
        address.setCountry(addressDetails.getCountry());
        return addressRepository.save(address);
    }

    public boolean delete(int id) {
        if (addressRepository.existsById(id)) {
            addressRepository.deleteById((id));
            return true;
        } else {
            return false;
        }
    }

    public Address createAddress(AddressDTO addressDto, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setUser(user);
        return addressRepository.save(address);
    }

    public List<Address> findAddressesForUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return addressRepository.findByUserId(user.getId());
    }
}

