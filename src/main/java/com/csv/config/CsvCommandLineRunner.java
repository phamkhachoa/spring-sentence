package com.csv.config;

import com.csv.constant.Constants;
import com.csv.entity.TranslationEntity;
import com.csv.mapper.LinkMapper;
import com.csv.mapper.SentenceMapper;
import com.csv.mapper.SentenceWithAudioMapper;
import com.csv.mapper.TranslationMapper;
import com.csv.model.Link;
import com.csv.model.Sentence;
import com.csv.model.SentenceWithAudio;
import com.csv.model.Translation;
import com.csv.repository.TranslationRepository;
import com.csv.service.TranslationService;
import com.csv.util.CsvUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvCommandLineRunner implements CommandLineRunner {

    @Value("${csv.import}")
    private boolean runImportDB;

    private final TranslationService translationService;
    private final TranslationRepository translationRepository;
    private final TranslationMapper translationMapper;

    private final JobLauncher jobLauncher;

    private final Job readCSVFileJob;

    @Override
    public void run(String... args) throws Exception {
        if (runImportDB) {
            log.info("=========================================");
            log.info("            READ CSV            ");
            log.info("=========================================");

            Path path = FileSystems.getDefault().getPath("").toAbsolutePath();

            // load link file
            CompletableFuture<List<Link>> linkCf = CompletableFuture.supplyAsync(() -> {
                try {
                    return CsvUtil.readCsvFile(path + Constants.CSV_LINKS_PATH, new LinkMapper(), Constants.CSV_SPLIT_CHARACTER)
                            .stream().filter(Objects::nonNull).collect(Collectors.toList());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // load sentence file
            CompletableFuture<Map<String, Sentence>> sentenceCf = CompletableFuture.supplyAsync(() -> {
                try {
                    return CsvUtil.readCsvFile(path + Constants.CSV_SENTENCES_PATH, new SentenceMapper(), Constants.CSV_SPLIT_CHARACTER)
                            .stream()
                            .parallel()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(Sentence::getSentenceId, Function.identity(), (r1, r2) -> r1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // load sentence with audio file
            CompletableFuture<Map<String, SentenceWithAudio>> sentenceWithAudioCf = CompletableFuture.supplyAsync(() -> {
                try {
                    return CsvUtil.readCsvFile(path + Constants.CSV_SENTENCE_WITH_AUDIO_PATH, new SentenceWithAudioMapper(), Constants.CSV_SPLIT_CHARACTER)
                            .stream()
                            .parallel()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(SentenceWithAudio::getSentenceId, Function.identity(), (r1, r2) -> r1));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            // export to csv
            List<Translation> translationList = CompletableFuture.allOf(linkCf, sentenceCf, sentenceWithAudioCf)
                    .thenApply((e) -> {
                        List<Link> links = linkCf.join();
                        Map<String, Sentence> sentenceMap = sentenceCf.join();
                        Map<String, SentenceWithAudio> sentenceWithAudioMap = sentenceWithAudioCf.join();
                        return translationService.aggregateTranslation(links, sentenceMap, sentenceWithAudioMap, "eng", "vie");
                    }).join();

            translationService.exportTranslationToCsv(translationList, path + "/" + Constants.CSV_OUTPUT_PATH);

            // truncate before import
//            translationRepository.truncateTable();

            // import csv db
            jobLauncher.run(readCSVFileJob, new JobParameters());
        }

    }
}
