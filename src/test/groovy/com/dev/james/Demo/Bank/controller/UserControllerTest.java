package com.dev.james.Demo.Bank.controller;

import com.dev.james.Demo.Bank.config.JwtTokenProvider;
import com.dev.james.Demo.Bank.dto.*;
import com.dev.james.Demo.Bank.service.UserService;
import com.dev.james.Demo.Bank.util.AccountUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    ObjectMapper objectMapper;


    UserRequest userRequest = null;
    BankResponse bankResponse = null;
    BankResponse userExistsResponse = null;

    AccountInfo accountInfo = null;

    LoginDto loginDto = null;

    public static EnquiryRequest enquiryRequest =  null;
    public static DebitCreditRequest debitCreditRequest =  null;
    public static TransferRequest transferRequest =  null;


    @BeforeEach
    void setUp() {
        loginDto = LoginDto.builder()
                .password("12345678")
                .email("testemail@gmail.com")
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

        userExistsResponse = BankResponse.builder()
                .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                .accountInfo(null)
                .build();

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
    }

    @Test
    public void userController_createAccount_returnsOkayAndBankResponse() throws Exception {
        Mockito.when(userService.createAccount(userRequest)).thenReturn(bankResponse);

        ResultActions response = mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.SUCCESS_CODE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountInfo.accountName", CoreMatchers.is(bankResponse.getAccountInfo().getAccountName())))
                .andDo(MockMvcResultHandlers.print());


    }

    @Test
    public void userController_createAccount_withAlreadyExistingCredentials_returnsErrorMessage() throws Exception {

        Mockito.when(userService.createAccount(userRequest)).thenReturn(userExistsResponse);

        ResultActions response = mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.ACCOUNT_EXISTS_CODE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseMessage", CoreMatchers.is(AccountUtils.ACCOUNT_EXISTS_MESSAGE)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void userController_login_withCorrectCredentials_returnsSuccessMessage() throws Exception {
        String testToken = "972424k23bnkl4m2l34j234m23l4m23l4nnklmlrmmfyrire87a8dad";
        bankResponse.setResponseCode(AccountUtils.SUCCESS_CODE);
        bankResponse.setAccountInfo(null);
        bankResponse.setResponseMessage(testToken);

        Mockito.when(userService.login(loginDto)).thenReturn(bankResponse);

        ResultActions response = mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.SUCCESS_CODE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseMessage", CoreMatchers.is(testToken)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void userController_getAccountName_returnsAccountHolderName() throws Exception {
        Mockito.when(userService.nameEnquiry(enquiryRequest)).thenReturn(userRequest.getFirstName());

        ResultActions response = mockMvc.perform(get("/api/user/account_name")
                .content(objectMapper.writeValueAsString(enquiryRequest))
                .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(userRequest.getFirstName()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void userController_creditAccount_withCorrectParameters_returnsSuccessMessage() throws Exception {

        bankResponse.setResponseCode(AccountUtils.SUCCESS_CODE);
        bankResponse.setResponseMessage(AccountUtils.SUCCESS_CODE);
        bankResponse.getAccountInfo().setAccountBalance(BigDecimal.valueOf(20.00));

        Mockito.when(userService.creditAccount(debitCreditRequest)).thenReturn(bankResponse);

        ResultActions response = mockMvc.perform(post("/api/user/credit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debitCreditRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.SUCCESS_CODE)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.accountInfo.accountName", CoreMatchers.is(bankResponse.getAccountInfo().getAccountName())));

    }

    @Test
    public void userController_debitAccount_withCorrectParameters_returnsSuccessMessage() throws Exception {

        bankResponse.setResponseCode(AccountUtils.SUCCESS_CODE);
        bankResponse.setResponseMessage(AccountUtils.SUCCESS_CODE);
        bankResponse.getAccountInfo().setAccountBalance(BigDecimal.valueOf(20.00));

        Mockito.when(userService.debitAccount(debitCreditRequest)).thenReturn(bankResponse);

        ResultActions response = mockMvc.perform(post("/api/user/debit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debitCreditRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.SUCCESS_CODE)));

    }

    @Test
    public void userController_transferAmount_withCorrectParameters_returnsSuccessMessage() throws Exception {

        bankResponse.setResponseCode(AccountUtils.SUCCESS_CODE);
        bankResponse.setResponseMessage("Successful money transfer");
        bankResponse.setAccountInfo(null);

        Mockito.when(userService.transferMoney(transferRequest)).thenReturn(bankResponse);

        ResultActions response = mockMvc.perform(post("/api/user/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest))
        );

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode", CoreMatchers.is(AccountUtils.SUCCESS_CODE)));

    }



}