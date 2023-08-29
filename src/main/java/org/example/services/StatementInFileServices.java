package org.example.services;

import org.example.db.DBConnector;
import org.example.pojo.Account;
import org.example.pojo.Bank;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDBanks.getBankById;

public class StatementInFileServices {


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


        saveCheckFile(check, transaction.getId());

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

    private File createFolder() {
        File folder = new File(System.getProperty("user.dir") + File.separatorChar + "checks");
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folder;
    }

    private void saveCheckFile(String check, Integer id) {
        File folder = createFolder();
        File checkFile = new File(folder.getPath() + File.separatorChar + "check_" + id + ".txt");
        try (FileWriter fileWriter = new FileWriter(checkFile)){
            fileWriter.append(check).flush();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
