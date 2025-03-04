package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.config.JwtTokenProvider;
import com.dev.james.Demo.Bank.dto.*;
import com.dev.james.Demo.Bank.entity.Role;
import com.dev.james.Demo.Bank.entity.User;
import com.dev.james.Demo.Bank.repository.UserRepository;
import com.dev.james.Demo.Bank.util.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        /*
        * creating account - adds new user to db
        * check if user already exists or has an account*/

        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }

        System.out.println("User request:" + userRequest.toString());

        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternatePhoneNumber(userRequest.getAlternatePhoneNumber())
                .status("ACTIVE")
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(newUser);

        System.out.println("Saved user email: " + savedUser.getEmail());

        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("Account creation")
                .messageBody("Hello " + savedUser.getFirstName() + "welcome to bank XYZ. Your account was successfully created. Here are your account details: \n" +
                        "Account name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account number: " + savedUser.getAccountNumber())
                .build();

        emailService.sendEmailAlert(emailDetails);


        return BankResponse.builder()
                .responseCode(AccountUtils.SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_SUCCESS_MESSAGE)
                .accountInfo(
                        AccountInfo.builder()
                                .accountBalance(
                                        savedUser.getAccountBalance()
                                )
                                .accountNumber(
                                        savedUser.getAccountNumber()
                                )
                                .accountName(
                                        savedUser.getFirstName() + " " + savedUser.getLastName()
                                )
                                .build()
                )
                .build();
    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        if(!userRepository.existsByAccountNumber(request.getAccountNumber())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User user = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                                .accountNumber(request.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .responseMessage(AccountUtils.ACCOUNT_BALANCE_SUCCESS_MESSAGE)
                .responseCode(AccountUtils.SUCCESS_CODE)
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean accountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!accountExists){
            return AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE;
        }
        User user = userRepository.findByAccountNumber(request.getAccountNumber());
        return  user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

    }

    @Override
    public BankResponse creditAccount(DebitCreditRequest request) {
        boolean accountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User user = userRepository.findByAccountNumber(request.getAccountNumber());
        user.setAccountBalance(
                user.getAccountBalance().add(BigDecimal.valueOf(request.getAmount()))
        );
        userRepository.save(user);

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("CREDIT")
                .accountNumber(request.getAccountNumber())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .status("COMPLETE")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                                .accountNumber(request.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .responseMessage("Success! " + "Ksh." + request.getAmount() + " was credited into your account. Your new account balance is " + "Ksh." + user.getAccountBalance())
                .responseCode(AccountUtils.SUCCESS_CODE)
                .build();

    }

    @Override
    public BankResponse debitAccount(DebitCreditRequest request) {
        boolean accountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!accountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User user = userRepository.findByAccountNumber(request.getAccountNumber());
        boolean isAmountEnough = user.getAccountBalance().compareTo(BigDecimal.valueOf(request.getAmount())) < 0;
        if(isAmountEnough){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        user.setAccountBalance(
                user.getAccountBalance().subtract(BigDecimal.valueOf(request.getAmount()))
        );

        userRepository.save(user);

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("DEBIT")
                .accountNumber(request.getAccountNumber())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .status("COMPLETE")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .accountInfo(
                        AccountInfo.builder()
                                .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
                                .accountNumber(request.getAccountNumber())
                                .accountBalance(user.getAccountBalance())
                                .build()
                )
                .responseMessage("Success! " + "Ksh." + request.getAmount() + " was debited from your account. Your new account balance is " + "Ksh." + user.getAccountBalance())
                .responseCode(AccountUtils.SUCCESS_CODE)
                .build();

    }

    @Override
    public BankResponse transferMoney(TransferRequest request) {
        //check if accounts exist in db
        boolean recipientAccountExists = userRepository.existsByAccountNumber(request.getRecipientAccountNum());
        boolean senderAccountExists = userRepository.existsByAccountNumber(request.getSenderAccountNum());
        if(!recipientAccountExists ){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        if(!senderAccountExists ){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //if they do retrieve the accounts
        User senderUser = userRepository.findByAccountNumber(request.getSenderAccountNum());
        User recepientUser = userRepository.findByAccountNumber(request.getRecipientAccountNum());

        //check whether the sender's account has sufficient balance
        boolean isAmountEnough = senderUser.getAccountBalance().compareTo(BigDecimal.valueOf(request.getAmount())) < 0;
        if(isAmountEnough){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        //debit from sender's account and add to recipient account
        senderUser.setAccountBalance(
                senderUser.getAccountBalance().subtract(BigDecimal.valueOf(request.getAmount()))
        );
        userRepository.save(senderUser);



        //send email to source
        EmailDetails senderEmailDetails = EmailDetails.builder()
                .recipient(senderUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("Transaction successful! Ksh." + request.getAmount() + " has been sent to " + recepientUser.getFirstName() + " " + recepientUser.getLastName() + " account number " + recepientUser.getAccountNumber() + ". Your new account balance is " +senderUser.getAccountBalance() )
                .build();
        emailService.sendEmailAlert(senderEmailDetails);


        recepientUser.setAccountBalance(
                recepientUser.getAccountBalance().add(BigDecimal.valueOf(request.getAmount()))
        );
        userRepository.save(recepientUser);

        //send email to recipient
        EmailDetails recipientEmailDetails = EmailDetails.builder()
                .recipient(senderUser.getEmail())
                .subject("CREDIT ALERT")
                .messageBody("Transaction successful! Your have received ksh." + request.getAmount() + " from " + senderUser.getFirstName() + " " + senderUser.getLastName() + " account number " + senderUser.getAccountNumber() + ". Your new account balance is Ksh." + recepientUser.getAccountBalance() )
                .build();
        emailService.sendEmailAlert(recipientEmailDetails);

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .transactionType("TRANSFER")
                .accountNumber(request.getSenderAccountNum())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .status("COMPLETE")
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .accountInfo(null)
                .responseMessage("Success! " + "Ksh." + request.getAmount() + " was transferred to account " + request.getRecipientAccountNum() + ". From account " + senderUser.getAccountNumber())
                .responseCode(AccountUtils.SUCCESS_CODE)
                .build();

    }

    @Override
    public BankResponse login(LoginDto loginDto) {
        Authentication authentication = null;
        Date loginTime = new Date();
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail() ,
                        loginDto.getPassword()
                )
        );
        //send email to recipient
        /*EmailDetails recipientEmailDetails = EmailDetails.builder()
                .recipient(loginDto.getEmail())
                .subject("LOGIN ALERT")
                .messageBody("New login detected at " + loginTime.getTime() )
                .build();
        emailService.sendEmailAlert(recipientEmailDetails);*/


        return BankResponse.builder()
                .responseCode(AccountUtils.SUCCESS_CODE)
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }
}
