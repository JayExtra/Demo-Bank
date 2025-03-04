package com.dev.james.Demo.Bank.controller;

import com.dev.james.Demo.Bank.dto.*;
import com.dev.james.Demo.Bank.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Use this endpoint to create a new user account."
    )
    @ApiResponse(
            responseCode = "201" ,
            description = "Http Status 201 User created."
    )
    @PostMapping("create")
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.createAccount(userRequest);
    }

    @Operation(
            summary = "Login User",
            description = "Use this endpoint to login a user into their account."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 Okay."
    )
    @PostMapping("login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Use this endpoint to get a users account balance."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("account_balance")
    public BankResponse getAccountBalance(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @Operation(
            summary = "Account Holder Name",
            description = "Use this endpoint to get the name of the account holder."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("account_name")
    public String getAccountName(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @Operation(
            summary = "Credit Account",
            description = "Use this endpoint to credit money into a users account."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 SUCCESS"
    )

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody DebitCreditRequest request) {
        return userService.creditAccount(request);
    }

    @Operation(
            summary = "Debit Account",
            description = "Use this endpoint to debit money out of an account of a user."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 SUCCESS"
    )

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody DebitCreditRequest request) {
        return userService.debitAccount(request);
    }

    @Operation(
            summary = "Transfer Money",
            description = "Use this endpoint to transfer money from one account to another."
    )
    @ApiResponse(
            responseCode = "200" ,
            description = "Http Status 200 SUCCESS"
    )
    @PostMapping("transfer")
    public BankResponse transferAmount(@RequestBody TransferRequest request) {
        return userService.transferMoney(request);
    }

}
