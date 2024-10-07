package com.quest.etna.repositories;

import com.quest.etna.model.Address;
import com.quest.etna.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    // Trouver des adresses par nom de rue
    List<Address> findByStreet(String street);

    // Trouver des adresses par code postal
    List<Address> findByPostalCode(String postalCode);

    // Trouver des adresses par ville
    List<Address> findByCity(String city);

    // Trouver des adresses par pays
    List<Address> findByCountry(String country);

    // Recherche d'adresses avec une requête JPQL personnalisée
    @Query("SELECT a FROM Address a WHERE a.city = ?1 AND a.country = ?2")
    List<Address> findByCityAndCountry(String city, String country);

    List<Address> findByUser(User user);

    Optional<Address> findById(int id);

    List<Address> findByUserId(int userId);

}
