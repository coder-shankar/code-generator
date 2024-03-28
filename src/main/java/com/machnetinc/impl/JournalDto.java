package com.machnetinc.impl;

public class JournalDto {
     String groupId;
     AccountType accountType;
    JournalDto(String id, String accountType){
        this.groupId = id;
        this.accountType = AccountType.valueOf(accountType);
    }
}
