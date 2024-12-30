package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.EmailDetail;

public interface EmailSvc {
    void sendEmailAlert(EmailDetail emailDetail);
}
