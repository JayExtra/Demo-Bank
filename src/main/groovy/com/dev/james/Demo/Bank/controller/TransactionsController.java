package com.dev.james.Demo.Bank.controller;

import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.service.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {


    @Autowired
    private BankStatement bankStatement;


    @GetMapping("bank_statement")
    public List<Transaction> generateBankStatement(
            @RequestParam String accountNumber ,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(
                accountNumber, startDate, endDate);

    }
}
