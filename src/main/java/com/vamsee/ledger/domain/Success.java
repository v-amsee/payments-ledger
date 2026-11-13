package com.vamsee.ledger.domain;

import java.util.UUID;

public record Success(UUID txId) implements TransferResult {}