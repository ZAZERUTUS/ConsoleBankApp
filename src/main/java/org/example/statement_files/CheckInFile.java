package org.example.statement_files;

import org.example.db.DBConnector;
import org.example.pojo.Account;
import org.example.pojo.Bank;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import java.io.IOException;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDBanks.getBankById;

public class CheckInFile {


    protected static final String headerStatement = "|                         Банковский чек                         |";
    protected static final int widthStatement = headerStatement.length();

    public String getStringForStatementTransaction(String start, String end) {
        String builder = start +
                " ".repeat(widthStatement - end.length() - start.length() - 1) + end + "|" + "\n";
        return builder;
    }

    public void createStatementByTransaction(Transaction transaction) throws IOException {
        String check;
        if (transaction.getType() == TypeTransaction.TRANSFER) {
            check = getStatementForTransfer(transaction);
        } else {
            check = getStatementForCashInOut(transaction);
        }

        new FileWriterPdfTxt("checks", check, "check_" + transaction.getId()).saveCheckFileTXT();

    }

    protected String getStatementForTransfer(Transaction transaction) {
        Account account1;
        Account account2;
        Bank bank1;
        Bank bank2;
        synchronized (DBConnector.class) {
            account1 = getAccountById(transaction.getAccount1Id());
            account2 = getAccountById(transaction.getAccount2Id());
            bank1 = getBankById(account1.getBankId());
            bank2 = getBankById(account2.getBankId());
        }
        StringBuilder builder = new StringBuilder();
        builder.append("-".repeat(widthStatement)).append("\n");
        builder.append(headerStatement).append("\n");

        String value = transaction.getId().toString();
        builder.append(getStringForStatementTransaction("|Чек:", value));

//        value = transaction.getType().getType();
//        builder.append(getStringForStatementTransaction("|Тип транзакции:", value));//todo - добавить дату в чек

        value = transaction.getType().getType();
        builder.append(getStringForStatementTransaction("|Тип транзакции:", value));

        value = bank1.getName();
        builder.append(getStringForStatementTransaction("|Банк отправителя:", value));

        value = bank2.getName();
        builder.append(getStringForStatementTransaction("|Банк получателя:", value));

        value = account1.getNumber();
        builder.append(getStringForStatementTransaction("|Счёт отправителя:", value));

        value = account2.getNumber();
        builder.append(getStringForStatementTransaction("|Счёт получателя:", value));


        value = transaction.getAmountOperation().toString();
        builder.append(getStringForStatementTransaction("|Сумма:", value));

        builder.append("-".repeat(widthStatement));
        return builder.toString();
    }

    protected String getStatementForCashInOut(Transaction transaction) {
        Account account1;
        Bank bank1;
        synchronized (DBConnector.class) {
            account1 = getAccountById(transaction.getAccount1Id());
            bank1 = getBankById(account1.getBankId());
        }
        StringBuilder builder = new StringBuilder();
        builder.append("-".repeat(widthStatement)).append("\n");
        builder.append(headerStatement).append("\n");

        String value = transaction.getId().toString();
        builder.append(getStringForStatementTransaction("|Чек:", value));

//        value = transaction.getType().getType();
//        builder.append(getStringForStatementTransaction("|Тип транзакции:", value));//todo - добавить дату в чек

        value = transaction.getType().getType();
        builder.append(getStringForStatementTransaction("|Тип транзакции:", value));

        value = bank1.getName();
        builder.append(getStringForStatementTransaction("|Банк операции:", value));

        value = account1.getNumber();
        builder.append(getStringForStatementTransaction("|Счёт операции:", value));

        value = transaction.getAmountOperation().toString();
        builder.append(getStringForStatementTransaction("|Сумма:", value));

        builder.append("-".repeat(widthStatement));
        return builder.toString();
    }


}
