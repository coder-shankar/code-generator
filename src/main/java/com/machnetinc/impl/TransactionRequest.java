package com.machnetinc.impl;

import java.math.BigDecimal;


public class TransactionRequest {

    private String id;
    private String affilixateId;
    private String userId;
    private TransactionType type;
    private TransactionSubType subType;
    private BigDecimal transactionMoney;
    private TransactionStatus status;

    public enum TransactionStatus {
        NEW,
        PROCESSED,
        CANCELLED
    }

    public enum TransactionType {
        ACH,
        CARD
    }

    public enum TransactionSubType {
        LOAD,
        UNLOAD
    }
}
