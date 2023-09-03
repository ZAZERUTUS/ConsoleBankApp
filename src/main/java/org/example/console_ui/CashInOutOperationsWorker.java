package org.example.console_ui;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.CustomBreakException;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.example.services.CashInAndOutService;
import org.example.services.TransactionProvider;
import org.example.services.TransferService;


public class CashInOutOperationsWorker extends OperationWorker {

    public CashInOutOperationsWorker(TypeTransaction typeTransaction) throws CustomBreakException {
        if (typeTransaction.compareTo(TypeTransaction.TRANSFER) == 0) {
            throw new CustomBreakException("Incorrect type operation");
        }
        this.typeTransaction = typeTransaction;
    }


    @Override
    public Transaction getCurrentTransaction() {
        return Transaction.builder()
                .type(typeTransaction)
                .account1Id(accountId1)
                .amountOperation(sumOperation)
                .build();
    }



    public boolean executeOperation() throws CustomBreakException {
        this.accountId1 = setAccountId("для " + typeTransaction.getType());
        sumOperation = this.setSumOperationByType(typeTransaction);
        return transferSum(getCurrentTransaction());
    }
}
