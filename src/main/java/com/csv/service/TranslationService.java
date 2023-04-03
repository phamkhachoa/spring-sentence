package com.csv.service;

import com.csv.entity.TranslationEntity;
import com.csv.model.Link;
import com.csv.model.Sentence;
import com.csv.model.SentenceWithAudio;
import com.csv.model.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;

public interface TranslationService {
    List<Translation> aggregateTranslation(List<Link> links, Map<String, Sentence> sentenceMap, Map<String, SentenceWithAudio> sentenceWithAudioMap, String fromLang, String toLang);

    void exportTranslationToCsv(List<Translation> translations, String path);

    Page<TranslationEntity> getAll(Integer pageNumber, Integer pageSize);
}
