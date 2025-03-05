package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.TransactionDto;
import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;

    @Transactional
    @Override
    public Transaction saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .amount(transactionDto.getAmount())
                .accountNumber(transactionDto.getAccountNumber())
                .status(transactionDto.getStatus())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved! transaction id:" + savedTransaction.getTransactionId());
        return savedTransaction;
    }

}
