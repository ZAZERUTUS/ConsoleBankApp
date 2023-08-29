package org.example.db;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Logger;

@Log4j2
public class CRUDTransactions extends DBConnector {

    protected static String formatQueryForGetTransactionById = "SELECT * FROM test.transactions where id = %s;";

    protected static String formatQueryForAddTransaction = "INSERT INTO test.transactions (account_id_from, account_id_to, transaction_type, sum_operation) VALUES (%s, %s, '%s', %s) RETURNING id";


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
}
