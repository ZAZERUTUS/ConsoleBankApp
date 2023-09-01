package org.example.statement_files;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.example.pojo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDCustomers.getCustomerById;
import static org.example.db.CRUDTransactions.getTransactionsByUserId;

@AllArgsConstructor
@RequiredArgsConstructor
public class StatementByAccount {
    protected int lenTitleInHeader = 30;
    protected String headerText = "Выписка";
    protected String headerRow = " ".repeat(lenTitleInHeader) + headerText + " ".repeat(lenTitleInHeader);

    protected String column1 = "     Дата     ";
    protected String column2 = "      Примечание                    ";
    protected String column3 = "Сумма     ";
    protected String headerStatement = String.join("|", column1, column2, column3) + "\n";

    protected String nameFolder = "statements";

    @NonNull
    protected Integer accountId;
    protected LocalDate fromStatement;
    @NonNull
    protected ExtensionStatement extensionStatement;

    public void saveStatement() {
        String statementContent = createStatementString();
        FileWriterPdfTxt writer = new FileWriterPdfTxt(nameFolder,
                statementContent,
                "statement_" + accountId);
        switch (extensionStatement) {
            case TXT -> writer.saveCheckFileTXT();
            case PDF -> writer.saveStatementPDF();
            default -> {
                writer.saveCheckFileTXT();
                writer.saveStatementPDF();
            }
        }



    }


    protected String createStatementString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(headerRow).append("\n");

        Account account = getAccountById(accountId);
        Customer customer = getCustomerById(account.customerId);

        stringBuilder.append(getRowInHeader("Клиент", String.join(" ", customer.name,customer.lastName)));
        stringBuilder.append(getRowInHeader("Счёт", account.number));
        stringBuilder.append(getRowInHeader("Валюта", account.currencyType.name()));
        stringBuilder.append(getRowInHeader("Дата открытия",
                account.timeCreateAccount.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        stringBuilder.append(getRowInHeader("Период", "ADD!!!"));
        stringBuilder.append(getRowInHeader("Дата и время формирования",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm"))));
        stringBuilder.append(getRowInHeader("Остаток", account.amount + " " + account.currencyType));

        stringBuilder.append(headerStatement);
        for (Transaction transaction: getTransactionsByUserId(account.id)) {
            stringBuilder.append(getRowInStatement(transaction, account));
        }

        return stringBuilder.toString();
    }

    protected String getRowInHeader(String title, String content) {
        return title + " ".repeat(lenTitleInHeader - title.length()) + "|" +
                " ".repeat((headerRow.length() - headerText.length())/2- content.length()) + content+ "\n";
    }

    protected String getRowInStatement(Transaction transaction, Account account) {
        String date = transaction.getTimeTransaction().toLocalDateTime()
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String description = transaction.getType().getType();
        String sum = getAmountWithSymbol(transaction) + " " + account.currencyType;
        return date + " ".repeat(column1.length() - date.length()) + "|" +
                description + " ".repeat(column2.length() - description.length()) + "|" +
                sum + "\n";
    }

    protected String getAmountWithSymbol(Transaction transaction) {
        if (transaction.getType().compareTo(TypeTransaction.CASH_OUT) == 0) {
            return "- " + transaction.getAmountOperation();
        } else if (transaction.getType().compareTo(TypeTransaction.TRANSFER) == 0 &&(Objects.equals(transaction.getAccount1Id(), accountId))) {
            return "- " + transaction.getAmountOperation();
        } else {
            return "+ " + transaction.getAmountOperation();
        }
    }


}
