package com.dev.james.Demo.Bank.service;

import com.dev.james.Demo.Bank.dto.EmailDetails;
import com.dev.james.Demo.Bank.entity.Transaction;
import com.dev.james.Demo.Bank.entity.User;
import com.dev.james.Demo.Bank.repository.TransactionRepository;
import com.dev.james.Demo.Bank.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.NioEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@Slf4j
public class BankStatement {
    /**
     * retrieve list of transactions within a date range given account number
     * generate a pdf file for transaction
     * send file via email
     **/

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\user\\Documents\\my_bank_statement.pdf";
    public List<Transaction> generateStatement(String accountNumber, String startDtae, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDtae, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction ->
                transaction.getAccountNumber().equals(accountNumber)
        ).filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        Rectangle rectangleDimens = new Rectangle(PageSize.A4);
        Document document = new Document(rectangleDimens);
        log.info("Setting size of document..");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        //add content to document
        PdfPTable bankInfoTab = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("Bank XYZ"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("88005-80100, Keino Street, Mombasa Kenya"));
        bankAddress.setBorder(0);
        bankInfoTab.addCell(bankName);
        bankInfoTab.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell statementStartDate = new PdfPCell(new Phrase("Start date" + startDtae));
        statementStartDate.setBorder(0);

        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder(0);

        PdfPCell statementEndDate = new PdfPCell(new Phrase("End date: " + endDate ));
        statementEndDate.setBorder(0);

        PdfPCell savedCustomerName = new PdfPCell(new Phrase("Customer Name: " + customerName));
        savedCustomerName.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        PdfPCell savedCustomerAddress = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        savedCustomerAddress.setBorder(0);

        PdfPTable transactionsTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);

        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);

        PdfPCell transactionDate = new PdfPCell(new Phrase("DATE"));
        transactionDate.setBackgroundColor(BaseColor.BLUE);
        transactionDate.setBorder(0);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("AMOUNT"));
        transactionAmount.setBackgroundColor(BaseColor.BLUE);
        transactionAmount.setBorder(0);

        PdfPCell transactionStatus = new PdfPCell(new Phrase("STATUS"));
        transactionStatus.setBackgroundColor(BaseColor.BLUE);
        transactionStatus.setBorder(0);

        transactionsTable.addCell(transactionDate);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(transactionStatus);

        transactionList.forEach(
                transaction -> {
                  transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
                  transactionsTable.addCell(new Phrase(transaction.getTransactionType().toString()));
                  transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
                  transactionsTable.addCell(new Phrase(transaction.getStatus().toString()));
                }
        );

        statementInfo.addCell(statementStartDate);
        statementInfo.addCell(statement);
        statementInfo.addCell(endDate);
        statementInfo.addCell(customerName);
        statementInfo.addCell(space);
        statementInfo.addCell(savedCustomerAddress);


        document.add(bankInfoTab);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();

        //send doc via email
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Hello " + customerName + ". Please find your bank statement attached below. Have a great day.")
                .attachment(FILE)
                .build();
        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }

}
