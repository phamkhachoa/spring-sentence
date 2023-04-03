package com.csv.config.batch.processor;

import com.csv.entity.TranslationEntity;
import org.springframework.batch.item.ItemProcessor;


public class CsvImportProcessor implements ItemProcessor<TranslationEntity, TranslationEntity> {

    @Override
    public TranslationEntity process(TranslationEntity translationEntity) throws Exception {
        return translationEntity;
    }
}
