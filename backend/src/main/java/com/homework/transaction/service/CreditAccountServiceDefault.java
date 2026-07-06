package com.homework.transaction.service;

import com.homework.account.domain.Account;
import com.homework.account.domain.LedgerCode;
import com.homework.account.service.AccountService;
import com.homework.transaction.domain.AccountTransaction;
import com.homework.transaction.domain.TransactionType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreditAccountServiceDefault implements CreditAccountService {

    private final AccountService accountService;
    private final LedgerHandlerService ledgerHandler;
    private final TsidTransactionIdGenerator trxIdGenerator;

    public CreditAccountServiceDefault(AccountService accountService, LedgerHandlerService ledgerHandler, TsidTransactionIdGenerator trxIdGenerator) {
        this.accountService = accountService;
        this.ledgerHandler = ledgerHandler;
        this.trxIdGenerator = trxIdGenerator;
    }

    @Transactional
    @Override
    public List<AccountTransaction> credit(String username, Long accountCode, BigDecimal amount, String description) {
        Account customer = accountService.getCustomerAccount(username, accountCode);
        Account external = accountService.getLedgerAccountForCurrency(LedgerCode.EXTERNAL, customer.currency());
        String text = description == null || description.isBlank() ? "Deposit" : description;
        String txnId = trxIdGenerator.newTransactionId();
        List<AccountTransaction> legs = List.of(
                AccountTransaction.newLeg(txnId, customer.accountCode(), external.accountCode(),
                        TransactionType.CREDIT, amount, customer.currency(), text),
                AccountTransaction.newLeg(txnId, external.accountCode(), customer.accountCode(),
                        TransactionType.DEBIT, amount, customer.currency(), text));
        return ledgerHandler.handleAllTxLegs(legs, Set.of(customer.accountCode()));
    }
}
