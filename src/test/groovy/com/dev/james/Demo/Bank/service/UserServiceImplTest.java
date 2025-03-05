package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.config.JwtTokenProvider;
import com.dev.james.Demo.Bank.dto.*;
import com.dev.james.Demo.Bank.entity.Role;
import com.dev.james.Demo.Bank.entity.User;
import com.dev.james.Demo.Bank.repository.UserRepository;
import com.dev.james.Demo.Bank.util.AccountUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    EmailService emailService;

    @Mock
    TransactionService transactionService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    UserService userService = new UserServiceImpl();


    public static BankResponse bankResponse =  null;
    public static AccountInfo accountInfo =  null;
    public static UserRequest userRequest =  null;
    public static User testUser =  null;

    public static User testUser2 = null;
    public static EnquiryRequest enquiryRequest =  null;
    public static DebitCreditRequest debitCreditRequest =  null;
    public static TransferRequest transferRequest =  null;

    @BeforeAll
    public static void  setUp(){

        transferRequest = TransferRequest.builder()
                .amount(20.00)
                .recipientAccountNum("2025112233")
                .senderAccountNum("2025771722")
                .build();

        debitCreditRequest = DebitCreditRequest.builder()
                .amount(20.00)
                .accountNumber("2025112233")
                .build();

        enquiryRequest = EnquiryRequest.builder()
                .accountNumber("2025112233")
                .build();

        testUser = User.builder()
                .accountNumber("2025112233")
                .accountBalance(BigDecimal.valueOf(11.11))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plus(60000L , ChronoUnit.SECONDS))
                .role(Role.ROLE_USER)
                .status("CREATED")
                .firstName("John")
                .lastName("Doe")
                .otherName("Summer")
                .gender("Male")
                .phoneNumber("+254700000000")
                .alternatePhoneNumber("+254711111111")
                .stateOfOrigin("Mombasa, Kenya")
                .password("stayliquid11")
                .build();

        testUser2 = User.builder()
                .accountNumber("2025771722")
                .accountBalance(BigDecimal.valueOf(100.00))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plus(60000L , ChronoUnit.SECONDS))
                .role(Role.ROLE_USER)
                .status("CREATED")
                .firstName("Jane")
                .lastName("Doe")
                .otherName("Winter")
                .gender("Female")
                .phoneNumber("+254700111111")
                .alternatePhoneNumber("+254700222222")
                .stateOfOrigin("Kwale, Kenya")
                .password("stayliquid11")
                .build();


        userRequest = UserRequest.builder()
                .address("555, Keino Street")
                .email("testemail123@gmail.com")
                .firstName("John")
                .lastName("Doe")
                .otherName("Summer")
                .gender("Male")
                .phoneNumber("+254700000000")
                .alternatePhoneNumber("+254711111111")
                .stateOfOrigin("Mombasa, Kenya")
                .password("stayliquid11")
                .build();

        accountInfo = AccountInfo.builder()
                .accountName("Justin")
                .accountNumber("0123456789")
                .accountBalance(BigDecimal.valueOf(11.11))
                .build();

        bankResponse = BankResponse.builder()
                .responseMessage("My test success message")
                .responseCode(AccountUtils.SUCCESS_CODE)
                .accountInfo(accountInfo)
                .build();
        System.out.print("Setting up: " + "\n" + "BankResponse: " + bankResponse.toString() + "\n" +userRequest + "\n" + testUser );

    }

    @BeforeEach
    public void resetUserAccountBalance(){
        testUser.setAccountBalance(BigDecimal.valueOf(11.11));
        testUser2.setAccountBalance(BigDecimal.valueOf(100.00));
    }

    @Test
    public void creatingNewUserWithNoRecord_returnsSuccessMessage(){
        Mockito.when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        Mockito.doNothing().when(emailService).sendEmailAlert(Mockito.any(EmailDetails.class));

        BankResponse successResponse = userService.createAccount(userRequest);

        Assertions.assertEquals(AccountUtils.SUCCESS_CODE , successResponse.getResponseCode());

    }

    @Test
    public void creatingNewUserWithExistingRecord_returnsAccountExistsError(){
        Mockito.when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        BankResponse successResponse = userService.createAccount(userRequest);

        Assertions.assertEquals(AccountUtils.ACCOUNT_EXISTS_CODE , successResponse.getResponseCode());

    }

    @Test
    public void checkSendEmailIsCalled() throws NoSuchMethodException {

        Mockito.when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        userService.createAccount(userRequest);

        Mockito.verify(emailService , Mockito.times(1)).sendEmailAlert(Mockito.any(EmailDetails.class));

    }

    @Test
    public void accountBalanceEnquiry_withExistingUserAccountNum_returnsBalance(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        Mockito.when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(testUser);

        BankResponse response = userService.balanceEnquiry(enquiryRequest);

        double accountBal = response.getAccountInfo().getAccountBalance().doubleValue();

        System.out.println("Account balance: " + accountBal);

        Assertions.assertTrue(accountBal >= 0.00);


    }

    @Test
    public void accountBalanceEnquiry_withNonExistingUserAccountNum_returnsError(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(false);

        BankResponse response = userService.balanceEnquiry(enquiryRequest);

        Assertions.assertEquals(response.getResponseCode() , AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE);

    }

    @Test
    public void accountNameEnquiry_withExistingAccountNumber_returnsAccountName(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        Mockito.when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(testUser);

        String accountName = userService.nameEnquiry(enquiryRequest);
        String firstName = Arrays.stream(accountName.split(" ")).findFirst().orElse(" ");

        Assertions.assertEquals(firstName , testUser.getFirstName());

    }

    @Test
    public void accountNameEnquiry_withNonExistingAccountNumber_returnsAccountName(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(false);

        String accountName = userService.nameEnquiry(enquiryRequest);

        Assertions.assertEquals(accountName , AccountUtils.ACCOUNT_DOES_NOT_EXISTS_MESSAGE);

    }

    @Test
    public void credit_toExistingAccountNumber_increasesAccountBalance(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        Mockito.when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(testUser);

        double initialResponse = testUser.getAccountBalance().doubleValue();

        BankResponse response = userService.creditAccount(debitCreditRequest);

        double newResponse = response.getAccountInfo().getAccountBalance().doubleValue();

        System.out.println("Initial balance: " + initialResponse);
        System.out.println("New balance: " + newResponse);


        Assertions.assertTrue(newResponse > initialResponse);
    }

    @Test
    public void credit_toNonExistingAccountNumber_returnsErrorMessage(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(false);

        BankResponse response = userService.creditAccount(debitCreditRequest);

        Assertions.assertEquals(response.getResponseCode() , AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE);
    }


    @Test
    public void debit_toExistingAccountNumber_withEnoughBalance_decreasesAccountBalance(){
        testUser.setAccountBalance(BigDecimal.valueOf(50.00));
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        Mockito.when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(testUser);

        double initialBalance = testUser.getAccountBalance().doubleValue();

        BankResponse response = userService.debitAccount(debitCreditRequest);

        double newBalance = response.getAccountInfo().getAccountBalance().doubleValue();

        System.out.println("Initial balance: " + initialBalance);
        System.out.println("New balance: " + newBalance);


        Assertions.assertTrue(newBalance < initialBalance);
    }

    @Test
    public void debit_toExistingAccountNumber_withLowerBalance_returnsErrorMessage(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(true);
        Mockito.when(userRepository.findByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(testUser);

        BankResponse response = userService.debitAccount(debitCreditRequest);

        Assertions.assertEquals(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_CODE , response.getResponseCode());
    }

    @Test
    public void debit_toNonExistingAccountNumber_returnsErrorMessage(){
        Mockito.when(userRepository.existsByAccountNumber(enquiryRequest.getAccountNumber())).thenReturn(false);

        BankResponse response = userService.debitAccount(debitCreditRequest);

        Assertions.assertEquals(response.getResponseCode() , AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE);
    }




    @Test
    public void cashTransfer_fromValidSourceAccount_toValidDestinationAccount_withSourceHavingEnoughMoney_returnsSuccess(){

        Mockito.when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(testUser);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(testUser2);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getSenderAccountNum());


        Assertions.assertEquals(AccountUtils.SUCCESS_CODE, response.getResponseCode());

    }

    @Test
    public void cashTransfer_fromValidSourceAccount_toValidDestinationAccount_withSourceHavingLessMoney_returnsFailure(){

        testUser2.setAccountBalance(BigDecimal.valueOf(0.00));

        Mockito.when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(testUser);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(testUser2);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getSenderAccountNum());


        Assertions.assertEquals(AccountUtils.ACCOUNT_BALANCE_NOT_ENOUGH_CODE, response.getResponseCode());

    }

    @Test
    public void cashTransfer_fromInvalidSourceAccount_toValidDestinationAccount_returnsFailure(){

        Mockito.when(userRepository.existsByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(false);
        Mockito.when(userRepository.existsByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(true);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).existsByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).existsByAccountNumber(transferRequest.getSenderAccountNum());


        Assertions.assertEquals(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.SENDER_ACCOUNT_DOES_NOT_EXISTS_MESSAGE, response.getResponseMessage());

    }

    @Test
    public void cashTransfer_fromValidSourceAccount_toInvalidDestinationAccount_returnsFailure(){

        Mockito.when(userRepository.existsByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(true);
        Mockito.when(userRepository.existsByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(false);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).existsByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).existsByAccountNumber(transferRequest.getSenderAccountNum());


        Assertions.assertEquals(AccountUtils.ACCOUNT_DOES_NOT_EXIST_CODE, response.getResponseCode());
        Assertions.assertEquals(AccountUtils.RECIPIENT_ACCOUNT_DOES_NOT_EXISTS_MESSAGE, response.getResponseMessage());

    }

    @Test
    public void successfulTransfer_sendsEmailToParties(){

        Mockito.when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(testUser);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(testUser2);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getSenderAccountNum());


        Mockito.verify(emailService , Mockito.times(2)).sendEmailAlert(Mockito.any(EmailDetails.class));

    }

    @Test
    public void successfulTransfer_savesTransaction(){

        Mockito.when(userRepository.existsByAccountNumber(Mockito.any(String.class))).thenReturn(true);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getRecipientAccountNum())).thenReturn(testUser);


        Mockito.when(userRepository.findByAccountNumber(transferRequest.getSenderAccountNum())).thenReturn(testUser2);


        BankResponse response= userService.transferMoney(transferRequest);

        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getRecipientAccountNum());
        Mockito.verify(userRepository).findByAccountNumber(transferRequest.getSenderAccountNum());


        Mockito.verify(transactionService , Mockito.times(1)).saveTransaction(Mockito.any(TransactionDto.class));

    }







}