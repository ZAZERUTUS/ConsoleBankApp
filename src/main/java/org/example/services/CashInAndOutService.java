package org.example.services;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
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
public class CashInAndOutService implements TransactionProvider{

    @NonNull
    public final Transaction transaction;
    private Account account;

    @Override
    public boolean transfer() {
        boolean ishHaveCash = isHaveCash(transaction);
        account = getAccountById(transaction.getAccount1Id());
        if (transaction.getType() == TypeTransaction.CASH_IN) {
            this.account.amount = account.amount.add(transaction.getAmountOperation());
            log.info("CASH IN");
            return true;
        } else if (transaction.getType() == TypeTransaction.CASH_OUT && ishHaveCash) {
            this.account.amount = this.account.amount.subtract(transaction.getAmountOperation());
            log.info("CASH OUT");
            return true;
        }
        return false;
    }

    @Override
    public Integer saveCurrentTransactionInBD() {
        updateAccount(account);
        return addTransaction(transaction);
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
