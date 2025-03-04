package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.TransactionDto;
import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status(transactionDto.getStatus())
                .build();

        transactionRepository.save(transaction);
        System.out.println("Transaction saved successfully!");
    }

}
