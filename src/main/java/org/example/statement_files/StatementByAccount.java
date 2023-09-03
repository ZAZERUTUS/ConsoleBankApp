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
import static org.example.db.CRUDTransactions.getInAccountByPeriod;
import static org.example.db.CRUDTransactions.getTransactionsByUserId;

@AllArgsConstructor
@RequiredArgsConstructor
public class StatementByAccount {
    protected int lenTitleInHeader = 30;
    protected String headerText = "Выписка";
    protected String headerRow = " ".repeat(lenTitleInHeader) + headerText + " ".repeat(lenTitleInHeader);

    protected String headerTextMoneyStatement = "Money statement";
    protected String headerRowMoneyStatement = " ".repeat(lenTitleInHeader) + headerTextMoneyStatement + " ".repeat(lenTitleInHeader);

    protected String column1 = "     Дата     ";
    protected String column2 = "             Примечание                                  ";
    protected String column3 = "Сумма     ";
    protected String headerStatement = String.join("|", column1, column2, column3) + "\n";

    protected String nameFolder = "statements";
    protected String nameMoneyStatementFolder = "statement-money,";

    protected Integer accountId;
    protected PeriodStatement periodStatement;
    protected Timestamp startDate;
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

    public void saveMoneyStatement() {
        accountId = selectAccountId();
        if (accountId == 0) {
            return;
        }

        String statementContent = createStatementStringForSumOperation();
        FileWriterPdfTxt writer = new FileWriterPdfTxt(nameMoneyStatementFolder,
                statementContent,
                "money_statement_" + accountId);
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

    protected Timestamp selectPeriodMoneyStatement() {
        while (true) {
            Timestamp selectDate = Timestamp.valueOf(LocalDateTime.of(1970, 1,1,0, 0));
            Scanner in = new Scanner(System.in);
            System.out.print("Введите дату в формате yyyy-mm-dd или 0 для получения за весь период:");
            try {
                String strDateIn = in.next();
                if (Objects.equals(strDateIn, "0")) {return selectDate;}
                String[] strDate = strDateIn.split("-");
                return Timestamp.valueOf(LocalDateTime.of(Integer.parseInt(strDate[0]), Integer.parseInt(strDate[1]),Integer.parseInt(strDate[2]),0, 0));
            } catch (Exception e) {
                System.out.println("Пожалуйста, введите корректное число или 0 для получения за весь период");
            }
        }
    }

    protected String createStatementString() {
        startDate = getLimitForStatement(periodStatement);

        StringBuilder stringBuilder = getHeaderStatement(headerRow);
        stringBuilder.append(headerStatement);

        for (Transaction transaction: getTransactionsByUserId(accountId)) {
            if (transaction.getTimeTransaction().compareTo(startDate) < 0) {
                break;
            }
            stringBuilder.append(getRowInStatement(transaction, accountId));
        }

        return stringBuilder.toString();
    }

    protected String createStatementStringForSumOperation() {
        startDate = selectPeriodMoneyStatement();

        StringBuilder stringBuilder = getHeaderStatement(headerRowMoneyStatement + "\n");

        String column1 = "Приход";
        String column2 = "Уход";
        int space = 5;
        stringBuilder.append(
                getRowInHeaderAccountSumStatement(column1, column2, space)
        );

        stringBuilder.append(
                getRowInHeaderAccountSumStatement(getInAccountByPeriod(accountId, true, startDate).toString(),
                        "- " + getInAccountByPeriod(accountId, false, startDate).toString(), space)
        );


        return stringBuilder.toString();
    }

    protected StringBuilder getHeaderStatement(String header) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(header).append("\n");
        Bank bank = getBankById(getAccountById(accountId).bankId);
        stringBuilder.append(" ".repeat((header.length() - bank.getName().length()) / 2 - bank.getName().length() / 2 ) + bank.getName()).append("\n");

        Account account = getAccountById(accountId);
        Customer customer = getCustomerById(account.customerId);

        stringBuilder.append(getRowInHeader("Клиент", String.join(" ", customer.name,customer.lastName)));
        stringBuilder.append(getRowInHeader("Счёт", account.number));
        stringBuilder.append(getRowInHeader("Валюта", account.currencyType.name()));
        stringBuilder.append(getRowInHeader("Дата открытия",
                account.timeCreateAccount.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        stringBuilder.append(getRowInHeader("Период",
                startDate.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        stringBuilder.append(getRowInHeader("Дата и время формирования",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH.mm"))));
        stringBuilder.append(getRowInHeader("Остаток", account.amount + " " + account.currencyType));
        return stringBuilder;
    }

    protected String getRowInHeader(String title, String content) {
        return title + " ".repeat(lenTitleInHeader - title.length()) + "|" +
                " ".repeat((headerRow.length() - headerText.length())/2- content.length()) + content+ "\n";
    }

    protected String getRowInHeaderAccountSumStatement(String column1, String column2, int space) {
        return  " ".repeat(headerRowMoneyStatement.length()/2 - space - column1.length()) +
                column1 + " ".repeat(space) + "|" + " ".repeat(space) +  column2 + "\n";
    }

    protected String getRowInStatement(Transaction transaction, Integer accountId) {
        Account account = getAccountById(accountId);
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
                return Timestamp.valueOf(LocalDateTime.of(1970, 1,1,0, 0));
            }
        }
    }




}
