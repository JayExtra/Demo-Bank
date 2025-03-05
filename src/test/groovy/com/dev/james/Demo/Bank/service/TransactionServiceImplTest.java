package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.TransactionDto;
import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.repository.TransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;

    @InjectMocks
    TransactionService transactionService = new TransactionServiceImpl();


    @Test
    void addTransactionInsertsNewTransaction(){
        //Given
        TransactionDto transactionDto = TransactionDto.builder()
                .status("COMPLETE")
                .amount(BigDecimal.valueOf(1000.00))
                .transactionType("CREDIT")
                .accountNumber("1122334455")
                .build();

        Transaction dummyTransaction = Transaction.builder()
                .transactionId("aodijad-sa7ydasdhasd")
                .status("COMPLETE")
                .transactionType("CREDIT")
                .accountNumber("1122334455")
                .amount(BigDecimal.valueOf(1000.00))
                .createdAt(LocalDate.now())
                .build();

        //when
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(dummyTransaction);
        Transaction savedTransaction = transactionService.saveTransaction(transactionDto);
        System.out.println("Saved Transaction ID: " + savedTransaction.getTransactionId()); // Debugging


        //then
        Assertions.assertEquals( "aodijad-sa7ydasdhasd" , savedTransaction.getTransactionId());
    }

}