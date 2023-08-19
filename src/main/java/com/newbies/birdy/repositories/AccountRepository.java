package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    Optional<Account> findByPhoneNumberAndStatus(String phoneNumber, Boolean status);
}
