package com.vamsee.ledger.domain;

public sealed interface TransferResult
        permits Success, InsufficientFunds, SameAccount, AccountNotFound {}