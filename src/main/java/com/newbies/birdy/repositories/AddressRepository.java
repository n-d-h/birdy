package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Address;
import com.newbies.birdy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByUserAddressAndStatus(User userAddress, Boolean status);
}
