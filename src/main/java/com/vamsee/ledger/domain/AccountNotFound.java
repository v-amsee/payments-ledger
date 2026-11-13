package com.vamsee.ledger.domain;

public record AccountNotFound(String accountId) implements TransferResult {}