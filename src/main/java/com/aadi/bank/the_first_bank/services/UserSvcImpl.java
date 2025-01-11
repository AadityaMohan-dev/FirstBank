package com.aadi.bank.the_first_bank.services;

//import com.aadi.bank.the_first_bank.config.JwtTokenProvider;
import com.aadi.bank.the_first_bank.dto.*;
import com.aadi.bank.the_first_bank.entity.Roles;
import com.aadi.bank.the_first_bank.entity.User;
import com.aadi.bank.the_first_bank.repository.UserRepository;
import com.aadi.bank.the_first_bank.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class UserSvcImpl implements UserSvc{
    @Autowired
    UserRepository userRepository;
    @Autowired
    EmailSvc emailSvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TrnsSvc trnsSvc;

    @Autowired
    AuthenticationManager authenticationManager;
//    @Autowired
//    JwtTokenProvider jwtTokenProvider;

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
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Roles.ROLE_USER)
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
    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null ;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        EmailDetail loginAlert = EmailDetail.builder()
                .subject("YOU ARE LOGGED IN")
                .messageBody("You logged into your account ,If you didn't initiate this please contact you bank immediately.")
                .recipient(loginDto.getEmail())
                .build();
        emailSvc.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("LOGIN SUCCESS")
//                .responseMessage(jwtTokenProvider.generateToken(authentication))
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

        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(savedUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDITED")
                .status("SUCCESS")
                .build();
        trnsSvc.savedTransactions(transactionDto);

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

    @Override
    public BankResponse transfer(TransferRequest request) {
        Boolean isDestinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User destinationAccount = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) < 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        User savedUser = userRepository.save(sourceAccountUser);

        destinationAccount.setAccountBalance(request.getAmount().add(destinationAccount.getAccountBalance()));
        User saveCRDUser = userRepository.save(destinationAccount);


        EmailDetail emailDetail = EmailDetail.builder()
                .recipient(savedUser.getEmail())
                .subject("Amount Debited Successfully.\n")
                .messageBody("Account Name : " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() + "\n" +
                        "Account number : " + savedUser.getAccountNumber() + "\n" +
                        "Amount Debited : " + request.getAmount() +
                        "Sent to : " + sourceAccountUser.getAccountNumber() +
                        "Remaining Balance : " + savedUser.getAccountBalance() +
                        "\n Thanks For Choosing Us . . .")
                .build();
        emailSvc.sendEmailAlert(emailDetail);

        EmailDetail receiverMail = EmailDetail.builder()
                .recipient(saveCRDUser.getEmail())
                .subject("Amount credited Successfully.\n")
                .messageBody("Account Name : " + saveCRDUser.getFirstName() + " " + saveCRDUser.getLastName() + " " + saveCRDUser.getOtherName() + "\n" +
                        "Account number : " + saveCRDUser.getAccountNumber() + "\n" +
                        "Amount credited : " + request.getAmount() +
                        "Send By : " + sourceAccountUser.getAccountNumber() +
                        "Remaining Balance : " + saveCRDUser.getAccountBalance() +
                        "\n Thanks For Choosing Us . . .")
                .build();
        emailSvc.sendEmailAlert(receiverMail);
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(saveCRDUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("CREDITED")
                .status("SUCCESS")
                .build();
        TransactionDto transactionDebitDto = TransactionDto.builder()
                .accountNumber(savedUser.getAccountNumber())
                .amount(request.getAmount())
                .transactionType("DEBITED")
                .status("SUCCESS")
                .build();
        trnsSvc.savedTransactions(transactionDto);
        trnsSvc.savedTransactions(transactionDto);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MESSAGE + " " + request.getAmount())
                .accountInfo(AccountInfo.builder()
                        .accountNumber(sourceAccountUser.getAccountNumber())
                        .accountName(sourceAccountUser.getFirstName()+ " "+ sourceAccountUser.getLastName()+ " " + sourceAccountUser.getOtherName())
                        .accountBalance(sourceAccountUser.getAccountBalance())
                        .build())
                .build();
    }
}
