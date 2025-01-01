package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.*;

public interface UserSvc {
    BankResponse createAccount(UserRequest userRequest);
    BankResponse bankEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);

    BankResponse login(LoginDto loginDto);
}
