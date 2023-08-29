package test.org.example.services;

import lombok.SneakyThrows;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.example.services.CashInAndOutService;
import org.example.services.TransferService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Objects;

import static org.example.db.CRUDAccounts.*;
import static org.example.db.CRUDAccounts.deleteAccount;
import static org.example.db.CRUDTransactions.getTransactionById;

public class TransferServiceTest {

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
    public void testTransactionIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> new TransferService(null));
    }

    @ParameterizedTest(name = "{index} -  From {0} operation {1} success operation is {2}")
//    @ValueSource(ints = {32, 33, 222, 1})
    @CsvSource(value = {
            "200, true",
            "20000, false",
            "2.32, true"
    })
    public void testAssertionCash(float operationSum,  boolean isSuccess) {
        TypeTransaction typeTransaction = TypeTransaction.TRANSFER;
        Transaction transaction = Transaction.builder()
                .account1Id(account1.id)
                .account2Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal(operationSum))
                .build();
        TransferService transferService = new TransferService(transaction);
        Assertions.assertEquals(transferService.transfer(), isSuccess);
    }

    @ParameterizedTest(name = "{index} -  Transaction on sum {0} and type {1} is saved - {2}")
//    @ValueSource(ints = {32, 33, 222, 1})
    @CsvSource(value = {
            "23, transfer",
            "23, TRAnsfer",
            "21, traNSfer",
            "2, Перевод",
            "28, пеРевод"

    })
    public void testSaveDataByAccountAndTransaction(float operationSum, String type) {
        TypeTransaction typeTransaction = TypeTransaction.getTypeByValue(type);
        Transaction transaction = Transaction.builder()
                .account1Id(account1.id)
                .account2Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal(operationSum))
                .build();

        TransferService transferService = new TransferService(transaction);
        transferService.transfer();

        Integer idTransaction = transferService.saveCurrentTransactionInBD();
        Transaction transactionFromDb = getTransactionById(idTransaction);
        Assertions.assertTrue(Objects.equals(transaction.getAccount1Id(), transactionFromDb.getAccount1Id()) &&
                Objects.equals(transaction.getAccount2Id(), transactionFromDb.getAccount2Id()) &&
                transaction.getType() == transactionFromDb.getType() &&
                Objects.equals(transaction.getAmountOperation(), transactionFromDb.getAmountOperation()));
    }

    @Test
    public void testTransferAndSaveStatementInDBIncorrectTransaction() {
        TypeTransaction typeTransaction = TypeTransaction.CASH_IN;
        Transaction transaction = Transaction.builder()
                .account1Id(account1.id)
                .account2Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal("23.44"))
                .build();
        Assertions.assertEquals(null,
                new TransferService(transaction).transferAndSaveStatementInDB());
    }

    @Test
    public void testTransferAndSaveStatementInDBCorrectTransaction() {
        BigDecimal sumTransfer = new BigDecimal("23.44");
        TypeTransaction typeTransaction = TypeTransaction.TRANSFER;
        Transaction transaction = Transaction.builder()
                .account1Id(account2.id)
                .account2Id(account2.id)
                .type(typeTransaction)
                .amountOperation(new BigDecimal("23.44"))
                .build();
        Integer idTransaction = new TransferService(transaction).transferAndSaveStatementInDB();
        Assertions.assertNotNull(idTransaction);

        Transaction fullTransaction = getTransactionById(idTransaction);
        Assertions.assertEquals(sumTransfer, fullTransaction.getAmountOperation());
        Assertions.assertEquals(account2.id, fullTransaction.getAccount1Id());
    }
}
