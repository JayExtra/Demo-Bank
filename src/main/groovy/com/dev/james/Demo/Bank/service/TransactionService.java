package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.TransactionDto;
import com.dev.james.Demo.Bank.entity.Transaction;


public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);

}
