package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.*;

public interface UserService {
     BankResponse createAccount(UserRequest userRequest);
     BankResponse balanceEnquiry(EnquiryRequest request);

     String nameEnquiry(EnquiryRequest request);

     BankResponse creditAccount(DebitCreditRequest request);
     BankResponse debitAccount(DebitCreditRequest request);

     BankResponse transferMoney(TransferRequest request);

     BankResponse login(LoginDto loginDto);

}
