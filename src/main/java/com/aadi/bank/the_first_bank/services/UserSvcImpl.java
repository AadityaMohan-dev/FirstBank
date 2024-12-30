package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.*;
import com.aadi.bank.the_first_bank.entity.User;
import com.aadi.bank.the_first_bank.repository.UserRepository;
import com.aadi.bank.the_first_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class UserSvcImpl implements UserSvc{
    @Autowired
    UserRequest userRequest;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailSvc emailSvc;

    @Override
    public BankResponse createAccount(UserRequest userRequest) {
        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        // email sending . . .
        EmailDetail emailDetail = EmailDetail.builder()
                .recipient(savedUser.getEmail())
                .subject("Congratulation your account has been created successfully.\n" +
                "Account Name : " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                "Account number : " + savedUser.getAccountNumber() + "\n" +
                "\n Thanks For Choosing Us . . .")
                .messageBody("Account Creation Confirmation.")
                .build();
        emailSvc.sendEmailAlert(emailDetail);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountBalance(savedUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse bankEnquiry(EnquiryRequest request) {
        Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(foundUser.getAccountNumber())
                        .accountName(foundUser.getFirstName()+ " "+ foundUser.getLastName()+ " " + foundUser.getOtherName())
                        .accountBalance(foundUser.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {

        Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName()+ " "+ foundUser.getLastName()+ " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigDecimal total_balance = userToCredit.getAccountBalance().add(request.getAmount());
        userToCredit.setAccountBalance(total_balance);
        User savedUser = userRepository.save(userToCredit);

        EmailDetail emailDetail = EmailDetail.builder()
                .recipient(savedUser.getEmail())
                .subject("Amount Credited Successfully.\n")
                .messageBody("Account Name : " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account number : " + savedUser.getAccountNumber() + "\n" +
                        "Amount Credited : " + request.getAmount() +
                        "Total Balance : " + savedUser.getAccountBalance() +
                        "\n Thanks For Choosing Us . . .")
                .build();
        emailSvc.sendEmailAlert(emailDetail);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_SUCCESS_MESSAGE + " " + request.getAmount() + "\n" +"Total Balance : "+ total_balance)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(userToCredit.getAccountNumber())
                        .accountName(userToCredit.getFirstName()+ " "+ userToCredit.getLastName()+ " " + userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        Boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        int currentBalance = Integer.parseInt(String.valueOf(userToDebit.getAccountBalance()));
        int requestedAmount = Integer.parseInt(String.valueOf(request.getAmount()));
        if(requestedAmount > currentBalance){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE + "\nCurrent Balance : " + currentBalance + "\nRequested Amount : " + requestedAmount)
                    .accountInfo(null)
                    .build();
        }
        BigDecimal total_balance = userToDebit.getAccountBalance().subtract(request.getAmount());
        userToDebit.setAccountBalance(total_balance);
        User savedUser = userRepository.save(userToDebit);

        EmailDetail emailDetail = EmailDetail.builder()
                .recipient(savedUser.getEmail())
                .subject("Amount Debited Successfully.\n")
                .messageBody("Account Name : " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account number : " + savedUser.getAccountNumber() + "\n" +
                        "Amount Debited : " + request.getAmount() +
                        "Remaining Balance : " + savedUser.getAccountBalance() +
                        "\n Thanks For Choosing Us . . .")
                .build();
        emailSvc.sendEmailAlert(emailDetail);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MESSAGE + " " + request.getAmount() + "\n" +"Remaining Balance : "+ total_balance)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(userToDebit.getAccountNumber())
                        .accountName(userToDebit.getFirstName()+ " "+ userToDebit.getLastName()+ " " + userToDebit.getOtherName())
                        .accountBalance(userToDebit.getAccountBalance())
                        .build())
                .build();
    }
}
