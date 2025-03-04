package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);

}
