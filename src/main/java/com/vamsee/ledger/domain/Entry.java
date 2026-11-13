package com.vamsee.ledger.domain;

import java.util.UUID;

public record Entry(UUID txId, String accountId, long pence) {}