package test.org.example.services;

import lombok.SneakyThrows;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.example.services.StatementInFileServices;
import org.example.services.TransferService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.example.db.CRUDAccounts.*;
import static org.example.db.CRUDAccounts.deleteAccount;
import static org.example.db.CRUDTransactions.getTransactionById;

public class StatementInFileServicesTest  {


    private static Account account1;
    private static Account account2;

    @BeforeAll
    @SneakyThrows
    public static void addDataForTest() {
        Account acc1 = new Account("testNumAddedTransfer", new BigDecimal(2200), 2, 2);
        account1 = getAccountById(addAccount(acc1));

        Account acc2 = new Account("testNumAddedTransfer2", new BigDecimal(1100), 3, 1);
        account2 = getAccountById(addAccount(acc2));
    }

    @AfterAll
    public static void deleteDataForTest() {
        deleteAccount(account1);
        deleteAccount(account2);
    }

    @Test
    @SneakyThrows
    public void testSaveTransferStatement() {
        StatementInFileServices statementInFileServices = new StatementInFileServices();
        Transaction transaction = Transaction.builder()
                .id(243)
                .account1Id(account2.id)
                .account2Id(account2.id)
                .type(TypeTransaction.TRANSFER)
                .amountOperation(new BigDecimal("23.44"))
                .build();
        statementInFileServices.createStatementByTransaction(transaction);
        //todo добавить проверку файла
    }
}
