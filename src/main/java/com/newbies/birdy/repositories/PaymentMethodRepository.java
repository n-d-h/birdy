package com.newbies.birdy.repositories;

import com.newbies.birdy.entities.PaymentMethod;
import com.newbies.birdy.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {

    List<PaymentMethod> findByUserPaymentMethodAndStatus(User user, Boolean status);

    PaymentMethod findByIdAndStatus(Integer id, Boolean status);

    List<PaymentMethod> findByUserPaymentMethodInAndStatus(List<User> userList, Boolean status);
}
