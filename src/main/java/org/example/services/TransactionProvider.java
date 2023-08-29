package org.example.services;

import org.example.CustomBreakException;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import static org.example.db.CRUDAccounts.getAccountById;

public interface TransactionProvider {


    default boolean isHaveCash(Transaction transaction) {
        if (transaction.getType() == TypeTransaction.CASH_OUT
                || transaction.getType() == TypeTransaction.TRANSFER) {
            return getAccountById(transaction.getAccount1Id()).getAmount().compareTo(transaction.getAmountOperation()) >= 0;
        } else {
            return true;
        }
    }

    boolean transfer();

    Integer saveCurrentTransactionInBD();

    Integer transferAndSaveStatementInDB() throws CustomBreakException;
}
