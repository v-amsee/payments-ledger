package com.vamsee.ledger.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryLedgerTest {

    @Test
    void transferMovesMoneyAndKeepsLedgerBalanced() {
        InMemoryLedger ledger = new InMemoryLedger();
        ledger.openAccount("Alice");
        ledger.openAccount("Bob");
        ledger.deposit("Alice", 10_000);

        TransferResult result = ledger.transfer("Alice", "Bob", 2_500);

        assertInstanceOf(Success.class, result);
        assertEquals(7_500, ledger.balance("Alice"));
        assertEquals(2_500, ledger.balance("Bob"));
        long total = ledger.allBalances().values().stream().mapToLong(v -> v).sum();
        assertEquals(0, total);                  // double entry: EVERYTHING sums to zero
    }

    @Test
    void insufficientFundsReportsShortfall() {
        InMemoryLedger ledger = new InMemoryLedger();
        ledger.openAccount("Alice");
        ledger.openAccount("Bob");
        ledger.deposit("Alice", 3_000);

        TransferResult result = ledger.transfer("Alice", "Bob", 5_000);

        InsufficientFunds f = assertInstanceOf(InsufficientFunds.class, result);
        assertEquals(2_000, f.shortfallPence());
        assertEquals(3_000, ledger.balance("Alice"));   // nothing moved
    }

    @Test
    void moneyRejectsDifferentCurrency() {
        Money gbp = new Money(500, "GBP");
        assertThrows(IllegalArgumentException.class, () -> gbp.plus(new Money(500, "USD")));
    }

    @Test
    void sameAccountIsRejected() {
        InMemoryLedger ledger = new InMemoryLedger();
        ledger.openAccount("Alice");
        ledger.deposit("Alice", 3_000);

        TransferResult result = ledger.transfer("Alice", "Alice", 3_000);

        assertInstanceOf(SameAccount.class, result);
        assertEquals(3_000, ledger.balance("Alice"));   // nothing moved
    }

    @Test
    void unknownAccountIsReported() {
        InMemoryLedger ledger = new InMemoryLedger();
        ledger.openAccount("Alice");
        ledger.deposit("Alice", 3_000);

        TransferResult result = ledger.transfer("Alice", "Ghost", 1_000);

        AccountNotFound notFound = assertInstanceOf(AccountNotFound.class, result);
        assertEquals("Ghost", notFound.accountId());    // tells the caller WHICH account was missing
        assertEquals(3_000, ledger.balance("Alice"));   // nothing moved
    }
}