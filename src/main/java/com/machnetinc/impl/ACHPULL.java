package com.machnetinc.impl;


import com.machnetinc.annotation.processor.CSVToObject;


import java.util.List;

@CSVToObject("a.csv")
public class ACHPULL extends A {

    @Override
    public List<JournalDto> create(TransactionRequest request) {
        final List<JournalDto> journalDtos = super.create(request);
        //add extra logic here

        return journalDtos;
    }

    public void mapping(JournalDto journalDto) {
        //convert journal dto to entity
    }


}
