package org.example.console_ui;

import lombok.extern.log4j.Log4j;

import lombok.extern.log4j.Log4j2;
import org.example.CustomBreakException;
import org.example.pojo.Account;
import org.example.pojo.Transaction;
import org.example.pojo.TypeTransaction;
import org.example.services.CashInAndOutService;
import org.example.services.StatementInFileServices;
import org.example.services.TransferService;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Logger;

import static org.example.db.CRUDAccounts.getAccountById;
import static org.example.db.CRUDTransactions.getTransactionById;


@Log4j2
public class OperationWorker implements CustomWorkerInConsole {

    TypeTransaction typeTransaction;
    Integer accountId1 = null;
    Account accountForOperation1;
    Integer accountId2 = null;
    Account accountForOperation2;
    BigDecimal sumOperation = null;


    protected Account getAccount(Integer id) throws IllegalArgumentException {
        Account accountForRet = getAccountById(id);
        if (accountForRet == null) {
            throw new IllegalArgumentException("Not fount account with id - " + id);
        } else {
            return accountForRet;
        }
    }

    protected Integer setAccountId(String descriptionTargetAccount) throws CustomBreakException {

        Integer accountId = null;
        while (accountId == null) {
            Scanner scanner = new Scanner(System.in);
            accountId = null;
            try {
                System.out.print("Введите id " + descriptionTargetAccount + " или введите '0' для выхода: ");
                accountId = scanner.nextInt();
                if (accountId == 0) {
                    throw new CustomBreakException("Exit from enter id account");
                }
                getAccount(accountId);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста вводите чисто в целочисленном формате");
            } catch (IllegalArgumentException g) {
                log.error(g.getMessage());
            }
        }
        return accountId;
    }

    protected BigDecimal setSumOperationByType(TypeTransaction typeTransaction) throws CustomBreakException{
        BigDecimal sumOperation = null;
        while (sumOperation == null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Введите сумму операции для " + typeTransaction + " или введите '0' для выхода:");
            try {
                sumOperation = getRoundedBigDecimal(scanner);
                if (sumOperation.compareTo(new BigDecimal(0)) <= 0) {
                    throw new CustomBreakException("Отмена ввода суммы операции");
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Пожалуйста, введите сумму операции в формате числа");
                sumOperation = null;
            }
        }
        return sumOperation;
    }

    @Override
    public Transaction getCurrentTransaction() {
        return null;
    }

    protected boolean transferSum(Transaction transaction) {
        System.out.println("INPUTED - " + transaction);
        Integer idTransaction;
        try {
            if (transaction.getType() == TypeTransaction.TRANSFER) {
                idTransaction = new TransferService(transaction).transferAndSaveStatementInDB();
            } else {
                idTransaction = new CashInAndOutService(transaction).transferAndSaveStatementInDB();
            }
            System.out.println("Операция " + transaction.getType().getType() + " успешно совершена");
            new StatementInFileServices().createStatementByTransaction(getTransactionById(idTransaction));
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("Operation not executed");
            return false;
        }
    }
}
