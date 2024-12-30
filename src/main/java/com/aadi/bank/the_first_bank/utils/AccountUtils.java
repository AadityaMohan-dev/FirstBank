package com.aadi.bank.the_first_bank.utils;

import java.time.Year;

public class AccountUtils {


    public static final String ACCOUNT_EXISTS_CODE = "200";
    public static final String ACCOUNT_EXISTS_MESSAGE = "The user already had an account created.";
    public static final String ACCOUNT_CREATION_SUCCESS = "201";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account created Successfully.";
    public static final String ACCOUNT_NOT_EXISTS_CODE = "404";
    public static final String ACCOUNT_NOT_EXISTS_MESSAGE = "User with provided Account Number Does not Exists.";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_MESSAGE = "Account Found Successfully.";
    public static final String ACCOUNT_CREDIT_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDIT_SUCCESS_MESSAGE = "Amount Credited Successfully. \n-> AMOUNT : ";
    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "006";
    public static final String ACCOUNT_DEBIT_SUCCESS_MESSAGE = "Amount Debited \n -> AMOUNT : ";
    public static final String INSUFFICIENT_BALANCE_CODE = "007";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Account does not have sufficient balance. ";


    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        int randNumber = (int) Math.floor(Math.random()*(max - min + 1) + min);
        String year = String.valueOf(currentYear);

        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();

    }
}
