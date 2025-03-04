package com.dev.james.Demo.Bank.util;

import java.time.Year;

public class AccountUtils {

    public static final String SUCCESS_CODE = "000";
    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_DOES_NOT_EXIST_CODE = "002";
    public static final String ACCOUNT_BALANCE_NOT_ENOUGH_CODE = "003";
    public static final String ACCOUNT_BALANCE_NOT_ENOUGH_MESSAGE = "Insufficient balance in your account to complete this transaction. Please top up and try again.";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created.";
    public static final String ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "There is no user associated with this account number";
    public static final String RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "There is no user associated with the recipient's account number";
    public static final String SENDER_ACCOUNT_DOES_NOT_EXISTS_MESSAGE = "There is no user associated with the sender's account number";
    public static final String ACCOUNT_BALANCE_SUCCESS_MESSAGE = "Account balance successfully retrieved";

    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE = "Account successfully created";

    public static  String generateAccountNumber(){
        /*
         * 2025 + randomSixDigits*/
        Year currentYear  = Year.now();
        int min = 100000;
        int max = 999999;

        //generate random num between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1));

        //convert to current year and random number to strings
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        //final account number
        StringBuilder accountNumber = new StringBuilder();


        return accountNumber.append(year).append(randomNumber).toString();
    }

}
