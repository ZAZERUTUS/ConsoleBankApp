package org.example.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.CustomBreakException;
import org.example.db.DBConnector;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;

import java.util.logging.Logger;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDAccounts.updateAccount;
import static org.example.db.CRUDTransactions.addTransaction;

@Log4j2
@RequiredArgsConstructor
public class TransferService implements TransactionProvider{

    @NonNull
    final Transaction transaction;
    private Account account1;
    private Account account2;

    @Override
    public boolean transfer() {
        boolean ishHaveCash = isHaveCash(transaction);
        account1 = getAccountById(transaction.getAccount1Id());
        account2 = getAccountById(transaction.getAccount2Id());
        if (this.transaction.getType() == TypeTransaction.TRANSFER && ishHaveCash) {
            this.account1.amount = this.account1.amount.subtract(transaction.getAmountOperation());
            this.account2.amount = this.account2.amount.add(transaction.getAmountOperation());
            log.info("TRANSFER");
            return true;
        }
        return false;
    }

    @Override
    public Integer saveCurrentTransactionInBD() {
        updateAccount(account1);
        updateAccount(account2);
        return addTransaction(this.transaction);
    }

    @Override
    public Integer transferAndSaveStatementInDB() {
        synchronized (DBConnector.class) {
            if (this.transfer()){
                return this.saveCurrentTransactionInBD();
            }
        }
        return null;
    }

}
