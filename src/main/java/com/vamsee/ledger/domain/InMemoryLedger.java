package com.vamsee.ledger.domain;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryLedger {
    private final Set<String> accounts = new HashSet<>();
    private final List<Entry> entries = new ArrayList<>();

    public void openAccount(String id) {
        accounts.add(id);
    }

    private Optional<String> findAccount(String id) {
        return accounts.contains(id) ? Optional.of(id) : Optional.empty();
    }

    public void deposit(String accountId, long pence) {
        UUID txId = UUID.randomUUID();
        entries.add(new Entry(txId, "EXTERNAL", -pence));
        entries.add(new Entry(txId, accountId, +pence));
    }

    public long balance(String accountId) {
        return entries.stream()
                .filter(e -> e.accountId().equals(accountId))
                .mapToLong(e -> e.pence())
                .sum();
    }

    public Map<String, Long> allBalances() {
        return entries.stream()
                .collect(Collectors.groupingBy(
                        e -> e.accountId(),
                        Collectors.summingLong(e -> e.pence())
                ));
    }

    public TransferResult transfer(String fromId, String toId, long pence) {
        if (findAccount(fromId).isEmpty()) return new AccountNotFound(fromId);
        if (findAccount(toId).isEmpty()) return new AccountNotFound(toId);
        if (fromId.equals(toId)) return new SameAccount();

        long available = balance(fromId);
        if (available < pence) return new InsufficientFunds(pence - available);

        UUID txId = UUID.randomUUID();
        entries.add(new Entry(txId, fromId, -pence));
        entries.add(new Entry(txId, toId, +pence));
        return new Success(txId);
    }
    public boolean isLedgerBalanced() {
        long total = entries.stream()
                .mapToLong(e -> e.pence())          // Entry → its pence
                .reduce(0L, Math::addExact);        // fold them together, overflow-safe
        return total == 0;
    }
}


