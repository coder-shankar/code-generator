package com.machnetinc.annotation.processor;

import java.math.BigDecimal;


public class TransactionRequest {

     public String id;
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
