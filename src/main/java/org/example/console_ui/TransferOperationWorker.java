package org.example.console_ui;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.example.CustomBreakException;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.example.services.CashInAndOutService;
import org.example.services.TransferService;

import java.math.BigDecimal;

public class TransferOperationWorker extends OperationWorker implements CustomWorkerInConsole {

    protected TypeTransaction typeTransaction;
    protected Integer accountId1;
    protected Integer accountId2;

    protected BigDecimal sumOperation;

    public TransferOperationWorker(TypeTransaction typeTransaction) throws CustomBreakException {
        if (typeTransaction.compareTo(TypeTransaction.TRANSFER) != 0) {
            throw new CustomBreakException("Incorrect type operation");
        }
        this.typeTransaction = typeTransaction;
    }

    @Override
    public Transaction getCurrentTransaction() {
        return Transaction.builder()
                .type(typeTransaction)
                .account1Id(accountId1)
                .account2Id(accountId2)
                .amountOperation(sumOperation)
                .build();
    }

    public boolean executeOperation() throws CustomBreakException {
        this.accountId1 = setAccountId("счёта списания для типа перевода - " + typeTransaction.getType());
        this.accountId2 = setAccountId("счёта зачисления для типа перевода - " + typeTransaction.getType());

        sumOperation = setSumOperationByType(typeTransaction);
        return transferSum(this.getCurrentTransaction());
    }


}
