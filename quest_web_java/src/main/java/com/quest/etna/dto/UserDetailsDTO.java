package com.quest.etna.dto;

import java.util.Set;

public class UserDetailsDTO extends UserDTO {

    private Set<AddressDTO> address;

    private Set<OrderDTO> orders;

    public UserDetailsDTO() { super(); }

    public Set<AddressDTO> getAddress() {
        return address;
    }

    public void setAddress(Set<AddressDTO> address) {
        this.address = address;
    }

    public Set<OrderDTO> getOrder() { return orders; }

    public void setOrders(Set<OrderDTO> orders) { this.orders = orders; }

}
