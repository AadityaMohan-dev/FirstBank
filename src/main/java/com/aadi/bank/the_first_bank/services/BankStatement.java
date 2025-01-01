package com.aadi.bank.the_first_bank.services;

import com.aadi.bank.the_first_bank.dto.EmailDetail;
import com.aadi.bank.the_first_bank.entity.Transaction;
import com.aadi.bank.the_first_bank.entity.User;
import com.aadi.bank.the_first_bank.repository.TransactionRepository;
import com.aadi.bank.the_first_bank.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class BankStatement {

    private static final Logger log = LoggerFactory.getLogger(BankStatement.class);
    private TransactionRepository transactionRepository;
    private EmailSvc emailSvc;
    private UserRepository userRepository;
    private static final String FILE = "path/to/your/file.pdf"; // Update with your desired file path

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws DocumentException {

        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        // Fetch transactions for the provided account number and within the date range
        List<Transaction> transactionsList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start) || transaction.getCreatedAt().isAfter(start))
                .filter(transaction -> transaction.getModifiedAt().isEqual(end) || transaction.getModifiedAt().isBefore(end))
                .toList();

        // Fetch user information
        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        // Set document size and create PDF document
        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of document");

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(FILE);
        } catch (FileNotFoundException e) {
            log.error("Error creating output file", e);
            return transactionsList;
        }
        PdfWriter.getInstance(document, outputStream);
        document.open();

        // Set up fonts
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.WHITE);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

        // Add bank information section
        PdfPTable bankInfoTable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("FIRST BANK", headerFont));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.DARK_GRAY);
        bankName.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("007, xyzStreet, LostCityWakanda", regularFont));
        bankAddress.setBorder(0);
        bankAddress.setHorizontalAlignment(Element.ALIGN_CENTER);
        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        // Add statement info section
        PdfPTable statementInfo = new PdfPTable(2);
        statementInfo.setWidthPercentage(100);

        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate, regularFont));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT", subHeaderFont));
        statement.setBorder(0);
        statement.setHorizontalAlignment(Element.ALIGN_CENTER);
        statement.setColspan(2);  // Make this cell span two columns

        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate, regularFont));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName, regularFont));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();  // Empty cell for spacing
        space.setBorder(0);
        PdfPCell address = new PdfPCell(new Phrase("Address: " + user.getAddress(), regularFont));
        address.setBorder(0);

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        // Add transactions table
        PdfPTable transactionsTable = new PdfPTable(4);
        transactionsTable.setWidthPercentage(100);
        float[] columnWidths = {1f, 2f, 2f, 1f}; // Adjust column widths
        transactionsTable.setWidths(columnWidths);

        // Table headers
        PdfPCell dateHeader = new PdfPCell(new Phrase("DATE", tableHeaderFont));
        dateHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        dateHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell transactionTypeHeader = new PdfPCell(new Phrase("TRANSACTION TYPE", tableHeaderFont));
        transactionTypeHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        transactionTypeHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell transactionAmountHeader = new PdfPCell(new Phrase("TRANSACTION AMOUNT", tableHeaderFont));
        transactionAmountHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        transactionAmountHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell statusHeader = new PdfPCell(new Phrase("STATUS", tableHeaderFont));
        statusHeader.setBackgroundColor(BaseColor.DARK_GRAY);
        statusHeader.setHorizontalAlignment(Element.ALIGN_CENTER);

        transactionsTable.addCell(dateHeader);
        transactionsTable.addCell(transactionTypeHeader);
        transactionsTable.addCell(transactionAmountHeader);
        transactionsTable.addCell(statusHeader);

        // Add transaction rows
        boolean isAlternateRow = false;
        for (Transaction transaction : transactionsList) {
            // Alternate row colors
            PdfPCell dateCell = new PdfPCell(new Phrase(transaction.getCreatedAt().toString(), regularFont));
            PdfPCell typeCell = new PdfPCell(new Phrase(transaction.getTransactionType(), regularFont));
            PdfPCell amountCell = new PdfPCell(new Phrase(transaction.getAmount().toString(), regularFont));
            PdfPCell statusCell = new PdfPCell(new Phrase(transaction.getStatus(), regularFont));

            if (isAlternateRow) {
                dateCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                typeCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                amountCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                statusCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            }

            transactionsTable.addCell(dateCell);
            transactionsTable.addCell(typeCell);
            transactionsTable.addCell(amountCell);
            transactionsTable.addCell(statusCell);

            isAlternateRow = !isAlternateRow;  // Toggle row color for next iteration
        }

        // Add all tables to document
        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        // Close the document
        document.close();

        EmailDetail emailDetail =  EmailDetail.builder()
                .recipient(user.getEmail())
            .subject("STATEMENT OF ACCOUNT")
                .messageBody("kindly find your account statement attached.")
                .attachment(FILE)
                .build();
        emailSvc.sendEmailWithAttachment(emailDetail);
        return transactionsList;
    }
}
