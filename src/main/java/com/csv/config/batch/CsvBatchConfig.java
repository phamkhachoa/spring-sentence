package com.csv.config.batch;

import com.csv.config.batch.processor.CsvImportProcessor;
import com.csv.constant.Constants;
import com.csv.entity.TranslationEntity;
import com.csv.mapper.TranslationFieldSetMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class CsvBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Value("classPath:/output/output.csv")
    private Resource inputResource;

    @Bean
    public Job readCSVFileJob() {
        return jobBuilderFactory
                .get("readCSVFileJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(4);
        executor.setCorePoolSize(2);
        executor.setQueueCapacity(1000);
        executor.afterPropertiesSet();
        return stepBuilderFactory
                .get("step")
                .<TranslationEntity, TranslationEntity>chunk(50)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(executor)
                .build();
    }

    @Bean
    public ItemProcessor<TranslationEntity, TranslationEntity> processor() {
        return new CsvImportProcessor();
    }

    @Bean
    public FlatFileItemReader<TranslationEntity> reader() {
        FlatFileItemReader<TranslationEntity> itemReader = new FlatFileItemReader<TranslationEntity>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(new FileSystemResource(Constants.CSV_OUTPUT_PATH));
        return itemReader;
    }

    @Bean
    public LineMapper<TranslationEntity> lineMapper() {
        // configure line mapper
        DefaultLineMapper<TranslationEntity> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer("\t");
        lineTokenizer.setIncludedFields(new int[] { 0, 1, 2, 3, 4 });
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(new TranslationFieldSetMapper());

        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<TranslationEntity> writer() {
        JdbcBatchItemWriter<TranslationEntity> itemWriter = new JdbcBatchItemWriter<TranslationEntity>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into translation (id, text, audio_url, translate_id, translate_text) VALUES (:id, :text, :audioUrl, :translateId, :translateText)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<TranslationEntity>());
        return itemWriter;
    }

}
