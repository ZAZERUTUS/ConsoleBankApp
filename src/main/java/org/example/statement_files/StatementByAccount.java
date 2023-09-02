package org.example.statement_files;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.example.pojo.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDBanks.getBankById;
import static org.example.db.CRUDCustomers.getCustomerById;
import static org.example.db.CRUDTransactions.getTransactionsByUserId;

@AllArgsConstructor
@RequiredArgsConstructor
public class StatementByAccount {
    protected int lenTitleInHeader = 30;
    protected String headerText = "Выписка";
    protected String headerRow = " ".repeat(lenTitleInHeader) + headerText + " ".repeat(lenTitleInHeader);

    protected String column1 = "     Дата     ";
    protected String column2 = "             Примечание                                  ";
    protected String column3 = "Сумма     ";
    protected String headerStatement = String.join("|", column1, column2, column3) + "\n";

    protected String nameFolder = "statements";

    protected Integer accountId;
    protected PeriodStatement periodStatement;
    @NonNull
    protected ExtensionStatement extensionStatement;

    public void saveStatement() {
        accountId = selectAccountId();
        if (accountId == 0) {
            return;
        }

        periodStatement = selectPeriodStatement();
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

    protected Integer selectAccountId() {
        while (true) {
            int selectID = 999;
            Scanner in = new Scanner(System.in);
            System.out.print("Введите id счёта по которму желаете получить выписку или 0 что бы выйти: ");
            try {
                selectID = in.nextInt();
                if (selectID == 0 || getAccountById(selectID) != null) {
                    return selectID;
                }
                throw new InputMismatchException();
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
    }

    protected PeriodStatement selectPeriodStatement() {
        while (true) {
            int selectPoint = 999;
            Scanner in = new Scanner(System.in);
            System.out.print("За какой период желаете получить выписку?\n" +
                    "Доступны:\n" +
                    "1 - с начала месяца\n" +
                    "2 - с начала года\n" +
                    "3 - за всё время\n" +
                    "Выберите период выписки (введите номер позиции):");
            try {
                selectPoint = in.nextInt();
                return PeriodStatement.getPeriodType(String.valueOf(selectPoint));
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите корректное число");
            }
        }
    }

    protected String createStatementString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(headerRow).append("\n");
        Bank bank = getBankById(getAccountById(accountId).bankId);
        stringBuilder.append(" ".repeat((headerRow.length() - bank.getName().length()) / 2 - bank.getName().length() / 2 ) + bank.getName()).append("\n");

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
        Timestamp localPeriod = getLimitForStatement(periodStatement);
        for (Transaction transaction: getTransactionsByUserId(account.id)) {
            if (transaction.getTimeTransaction().compareTo(localPeriod) < 0) {
                break;
            }
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
        String description = transaction.getType().getType() + (
                transaction.getAccount1Id() == this.accountId ?
                " пользователю " + getCustomerById(getAccountById(transaction.getAccount2Id()).customerId).lastName :
                " от пользователя " + getCustomerById(getAccountById(transaction.getAccount1Id()).customerId).lastName);

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

    protected Timestamp getLimitForStatement(PeriodStatement period) {
        switch (period) {
            case MONTH -> {
                LocalDate initial = LocalDate.now(),
                        firstDay = initial.withDayOfMonth(1);

                return Timestamp.valueOf(firstDay.atStartOfDay());
            }
            case YEAR -> {
                return Timestamp.valueOf(LocalDate.now().getYear() +"-01-01" + " 00:00:00");
            }
            default -> {
                return Timestamp.valueOf(LocalDateTime.MAX);
            }
        }
    }


}
