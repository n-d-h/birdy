package com.newbies.birdy.services.impl;

import com.newbies.birdy.dto.AccountDTO;
import com.newbies.birdy.entities.Account;
import com.newbies.birdy.exceptions.entity.EntityNotFoundException;
import com.newbies.birdy.mapper.AccountMapper;
import com.newbies.birdy.repositories.AccountRepository;
import com.newbies.birdy.services.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AccountDTO getByPhoneNumber(String phoneNumber, Boolean status) {
        Account account = accountRepository.findByPhoneNumberAndStatus(phoneNumber, status).orElse(null);
        return AccountMapper.INSTANCE.toDTO(account);
    }

    @Override
    public AccountDTO getById(Integer id) {
        return AccountMapper.INSTANCE.toDTO(accountRepository.findById(id).orElseThrow(() -> new RuntimeException("ID account not found")));
    }

    @Override
    public Boolean updateAccountBalance(Integer accountId, Double amount) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("ID account not found"));
        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account).getId() != null;
    }

    @Override
    public Boolean updatePassword(String phoneNumber, String password) {
        Account account = accountRepository
                .findByPhoneNumberAndStatus(phoneNumber, true)
                .orElseThrow(() -> new EntityNotFoundException("Account not found!"));
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account).getId() != null;
    }
}
