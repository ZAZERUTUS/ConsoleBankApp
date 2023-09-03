package org.example.db;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Log4j2
public class CRUDTransactions extends DBConnector {

    protected static String formatQueryForGetTransactionById = "SELECT * FROM test.transactions where id = %s;";
    protected static String formatQueryForGetTransactionsByAccountId = "SELECT id FROM test.transactions WHERE account_id_from = %s or account_id_to = %s order by time_transaction desc;";

    protected static String formatQueryForAddTransaction = "INSERT INTO test.transactions (account_id_from, account_id_to, transaction_type, sum_operation) VALUES (%s, %s, '%s', %s) RETURNING id";
    protected static String formatQueryForSumOperationsIn = "SELECT sum(sum_operation) FROM test.transactions where ((transaction_type='TRANSFER' and account_id_to=%s) or (transaction_type='CASH_IN' and account_id_from=%s)) and time_transaction > '%s';";
    protected static String formatQueryForSumOperationsOut = "SELECT sum(sum_operation) FROM test.transactions where (transaction_type='TRANSFER' and account_id_from=%s) or (transaction_type='CASH_OUT' and account_id_from=%s) and time_transaction > '%s';";

    public static synchronized List<Transaction> getTransactionsByUserId(Integer accountId) {
        List<Integer> ids = getTransactionsIdByAccountId(accountId);
        List<Transaction> transactions = new ArrayList<>();
        for (Integer transactionId: ids) {
            transactions.add(getTransactionById(transactionId));
        }
        log.info("Transactions for return - " + transactions);
        return transactions;
    }

    @SneakyThrows
    public static synchronized List<Integer> getTransactionsIdByAccountId(Integer accountId) {
        List<Integer> accountsId = new ArrayList<>();
        new DBConnector();
        Statement statement = getStatementPostgres();
        ResultSet resultSet = getResultSetBySQLQuery(statement, String.format(formatQueryForGetTransactionsByAccountId, accountId, accountId));
        try {
            while (resultSet.next()) {
                accountsId.add(resultSet.getInt(1));
            }
        } catch (NullPointerException e) {
            System.out.println("Not fount account in DB with id - " + accountId);
        }
        closeDbConnection();
        log.info("Transactions ids for return - " + accountsId);
        return accountsId;
    }

    @SneakyThrows
    public static synchronized Transaction getTransactionById(Integer id) {
        Statement statement = getStatementPostgres();
        ResultSet resultSet = getResultSetBySQLQuery(statement, String.format(formatQueryForGetTransactionById, id));
        Transaction transaction = null;
        try {
            if (resultSet.next()) {
                transaction = Transaction.builder()
                        .id(resultSet.getInt(1))
                        .account1Id(resultSet.getInt(2))
                        .account2Id(resultSet.getInt(3))
                        .type(TypeTransaction.getTypeByValue(resultSet.getString(4)))
                        .amountOperation(resultSet.getBigDecimal(5))
                        .timeTransaction(resultSet.getTimestamp(6))
                        .build();
            }
        } catch (NullPointerException e) {
            log.error("Not fount account in DB with id - " + id);
        }
        closeDbConnection();
        log.info("Transaction for return - " + transaction);
        return transaction;
    }

    @SneakyThrows
    public static synchronized Integer addTransaction(Transaction transaction) {
        log.info("Start add transaction - " + transaction);
        Statement statement = getStatementPostgres();
        String query = String.format(formatQueryForAddTransaction,
                transaction.getAccount1Id(),
                transaction.getAccount2Id() == null ? "NULL" : transaction.getAccount2Id(),
                transaction.getType(),
                transaction.getAmountOperation());

        ResultSet resultSet = getResultSetBySQLQuery(statement, query);
        Integer idAddedTransaction = null;
        if (resultSet.next()) {
            idAddedTransaction = resultSet.getInt(1);
        }
        log.info("ID transaction for return - " + idAddedTransaction);
        closeDbConnection();
        return idAddedTransaction;
    }

    @SneakyThrows
    public static synchronized BigDecimal getInAccountByPeriod(Integer accountId, boolean isIn, Timestamp limit) {

        Statement statement = getStatementPostgres();
        String query = String.format(isIn ? formatQueryForSumOperationsIn : formatQueryForSumOperationsOut, accountId, accountId, limit);
        ResultSet resultSet = getResultSetBySQLQuery(statement, query);
        BigDecimal sum = new BigDecimal(0);

        if (resultSet == null) {return sum;}
        if (resultSet.next()) {
            try {
                sum = resultSet.getBigDecimal(1).setScale(2, RoundingMode.HALF_UP);;
            } catch (NullPointerException e) {
                sum = new BigDecimal(0);
            }
        }
        return sum;
    }
}
