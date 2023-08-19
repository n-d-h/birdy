package com.newbies.birdy.services;

import com.newbies.birdy.dto.AccountDTO;

public interface AccountService {

    AccountDTO getByPhoneNumber(String phoneNumber, Boolean status);

    Boolean updatePassword(String phoneNumber, String password);
    AccountDTO getById(Integer id);

    Boolean updateAccountBalance(Integer accountId, Double amount);
}
