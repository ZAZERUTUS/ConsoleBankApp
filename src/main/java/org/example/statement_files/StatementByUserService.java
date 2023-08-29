package org.example.statement_files;

import lombok.AllArgsConstructor;
import org.example.pojo.Account;
import org.example.pojo.Customer;
import org.example.pojo.ExtensionStatement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDCustomers.getCustomerById;

@AllArgsConstructor
public class StatementByUserService {
    protected int lenTitleInHeader = 30;
    protected String headerText = "Выписка";
    protected String headerRow = " ".repeat(lenTitleInHeader) + headerText + " ".repeat(lenTitleInHeader);
    protected Integer accountId;
    protected LocalDate fromStatement;
    protected ExtensionStatement extensionStatement;

    public void saveStatement() {

    }

    protected void createStatementTXT() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(headerRow).append("\n");

        Account account = getAccountById(accountId);
        Customer customer = getCustomerById(account.customerId);

        stringBuilder.append(getRowInHeader("Клиент", customer.name + customer.lastName));
        stringBuilder.append(getRowInHeader("Счёт", account.number));
        stringBuilder.append(getRowInHeader("Валюта", "ADD!!!"));
        stringBuilder.append(getRowInHeader("Дата открытия",
                account.timeCreateAccount.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        stringBuilder.append(getRowInHeader("Период", "ADD!!!"));
        stringBuilder.append(getRowInHeader("Дата и время формирования", "" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm"))));


    }

    protected String getRowInHeader(String title, String content) {
        return title + " ".repeat(lenTitleInHeader - title.length()) + "|" +
                " ".repeat(headerRow.length() - content.length()) + content+ "\n";
    }


}
