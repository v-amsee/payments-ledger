package com.vamsee.ledger.domain;

public record InsufficientFunds(long shortfallPence) implements TransferResult {}