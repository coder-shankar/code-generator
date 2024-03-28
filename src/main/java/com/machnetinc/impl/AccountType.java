package com.machnetinc.impl;

public enum AccountType {

    //platform
    FBO,
    REMITTANCE_HOLD,
    MERCHANT_ACCOUNT,
    MACHNET_FEE,
    FX_CONVERSION,
    CARD_TEMP_HOLDING,
    WIRE_FEE,
    MACHPAY_BANK_ACCOUNT,

    //Client
    CLIENT_FEE,
    CLIENT_EXTERNAL,
    CLIENT_RESERVE,
    CLIENT_CARD_INTERCHANGE_FEE,
    CLIENT_WIRE_FEE,
    CLIENT_PREFUND,

    //Payout
    PAYOUT_PREFUND,
    PAYOUT_FEE,

    //End user
    USER_DEPOSIT,
    USER_DEPOSIT_CARD,
    USER_EXTERNAL,
    USER_VIRTUAL_ACCOUNT,
    USER_WALLET;
}
