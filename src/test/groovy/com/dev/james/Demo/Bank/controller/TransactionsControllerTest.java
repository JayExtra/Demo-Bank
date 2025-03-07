package com.dev.james.Demo.Bank.controller;

import com.dev.james.Demo.Bank.config.JwtTokenProvider;
import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.service.BankStatement;
import com.dev.james.Demo.Bank.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = TransactionsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class TransactionsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private BankStatement bankStatement;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;



    @Autowired
    ObjectMapper objectMapper;

    List<Transaction> transactionsList;
    String accountNum = "1234567890";
    String startDate = "2025-03-07";
    String endDate = "2025-03-08";

    @BeforeEach
    public void setUpBeforeEach(){

        transactionsList = List.of(
                Transaction.builder()
                        .status("COMPLETE")
                        .createdAt(LocalDate.now())
                        .accountNumber("1234567890")
                        .transactionType("CREDIT")
                        .amount(BigDecimal.valueOf(1000.00))
                        .transactionId("q808qeqweqweqweq-qweqwewqje")
                        .build(),
                Transaction.builder()
                        .status("COMPLETE")
                        .createdAt(LocalDate.now().minus(1 ,ChronoUnit.DAYS))
                        .accountNumber("1234567890")
                        .transactionType("CREDIT")
                        .amount(BigDecimal.valueOf(100.00))
                        .transactionId("80983J4L3RL3M-asdkdasdm")
                        .build(),
                Transaction.builder()
                        .status("COMPLETE")
                        .createdAt(LocalDate.now().plus(1 , ChronoUnit.DAYS))
                        .accountNumber("1234567890")
                        .transactionType("DEBIT")
                        .amount(BigDecimal.valueOf(10.00))
                        .transactionId("38024kreknfkajaefaefaef-fadnalf")
                        .build()

        );
    }

    @Test
    public void transactionsController_getAccountStatement_returnAccountStatementList() throws Exception {
        Mockito.when(bankStatement.generateStatement(accountNum, startDate, endDate)).thenReturn(transactionsList);

        ResultActions response = mockMvc.perform(get("/api/transactions/bank_statement")
                .param("accountNumber", accountNum)
                .param("startDate", startDate)
                .param("endDate", endDate)
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(transactionsList)))
                .andDo(MockMvcResultHandlers.print());
    }


}