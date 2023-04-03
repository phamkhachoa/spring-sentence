package com.csv.mapper;

import com.csv.entity.TranslationEntity;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

public class TranslationFieldSetMapper implements FieldSetMapper<TranslationEntity> {
    @Override
    public TranslationEntity mapFieldSet(FieldSet fieldSet) throws BindException {
        TranslationEntity entity = new TranslationEntity();
        entity.setId(fieldSet.readString(0));
        entity.setText(fieldSet.readString(1));
        entity.setAudioUrl(fieldSet.readString(2));
        entity.setTranslateId(fieldSet.readString(3));
        entity.setTranslateText(fieldSet.readString(4));
        return entity;
    }
}
