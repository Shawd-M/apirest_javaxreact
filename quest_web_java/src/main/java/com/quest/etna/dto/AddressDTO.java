package com.quest.etna.dto;

public class AddressDTO {
        private int id;
        private String street;
        private String postalCode;
        private String city;
        private String country;
        private UserDTO user;
        private String error;

    public AddressDTO() { }

    // Getters

    public  int getId() { return id; }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }


    // Setters

    public void setId(int id) { this.id = id; }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getError() { return error; }

    public void setError(String error) { this.error = error; }


}
