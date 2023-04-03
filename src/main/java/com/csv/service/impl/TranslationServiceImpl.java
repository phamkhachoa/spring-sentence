package com.csv.service.impl;

import com.csv.constant.Constants;
import com.csv.entity.TranslationEntity;
import com.csv.model.Link;
import com.csv.model.Sentence;
import com.csv.model.SentenceWithAudio;
import com.csv.model.Translation;
import com.csv.repository.TranslationRepository;
import com.csv.service.TranslationService;
import com.csv.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TranslationServiceImpl implements TranslationService {

    private final TranslationRepository translationRepository;

    @Override
    public List<Translation> aggregateTranslation(List<Link> links, Map<String, Sentence> sentenceMap,
                                                  Map<String, SentenceWithAudio> sentenceWithAudioMap, String fromLang, String toLang) {
        List<Translation> translations = new ArrayList<>();
        Map<String, Sentence> sentenceMapFromLang = sentenceMap.values().stream()
                .filter(e -> fromLang.equals(e.getLang()))
                .collect(Collectors.toMap(Sentence::getSentenceId, Function.identity()));

        Map<String, Sentence> sentenceMapToLang = sentenceMap.values().stream()
                .filter(e -> toLang.equals(e.getLang()))
                .collect(Collectors.toMap(Sentence::getSentenceId, Function.identity()));

        Map<String, String> linkMap = links.stream().filter(e ->
                sentenceMapFromLang.containsKey(e.getSentenceId()) && sentenceMapToLang.containsKey(e.getTranslationId())
        ).collect(Collectors.toMap(Link::getSentenceId, Link::getTranslationId, (r1, r2) -> r1));

        sentenceMapFromLang.values().stream()
                .parallel()
                .forEach(e -> {
                    String translationId = linkMap.get(e.getSentenceId());
                    if (sentenceMapToLang.containsKey(translationId) && sentenceWithAudioMap.containsKey(e.getSentenceId())) {
                        Sentence sentenceTo = sentenceMapToLang.get(translationId);
                        SentenceWithAudio sentenceWithAudio = sentenceWithAudioMap.get(e.getSentenceId());
                        translations.add(this.createTranslation(e, sentenceTo, sentenceWithAudio));
                    }
                });

        return translations;
    }

    @Override
    public void exportTranslationToCsv(List<Translation> translations, String path) {
        CsvUtil.toCsv(translations,
                (translation) -> new String[]{translation.getId(), translation.getText(), translation.getAudioUrl(), translation.getTranslateId(), translation.getTranslateText()},
                path, Constants.CSV_SPLIT_CHARACTER);
    }

    @Override
    public Page<TranslationEntity> getAll(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);
        return translationRepository.findAll(pageRequest);
    }

    private Translation createTranslation(Sentence from, Sentence to, SentenceWithAudio sentenceWithAudio) {
        Translation translation = new Translation();
        translation.setId(from.getSentenceId());
        translation.setText(from.getText());
        translation.setAudioUrl(sentenceWithAudio.getAttributionUrl() + "/" + from.getLang() + "/" + from.getSentenceId() + ".mp3");
        translation.setTranslateId(to.getSentenceId());
        translation.setTranslateText(to.getText());
        return translation;
    }
}
