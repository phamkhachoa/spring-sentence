package com.csv.mapper;

import com.csv.model.SentenceWithAudio;

public class SentenceWithAudioMapper implements CsvMapper<String[], SentenceWithAudio> {
    @Override
    public SentenceWithAudio toObj(String[] str) {
        if (str == null || str.length != 4) {
            return null;
        }
        SentenceWithAudio sentenceWithAudio = new SentenceWithAudio();
        sentenceWithAudio.setSentenceId(str[0]);
        sentenceWithAudio.setUsername(str[1]);
        sentenceWithAudio.setLicense(str[2]);
        sentenceWithAudio.setAttributionUrl(str[3]);

        return sentenceWithAudio;
    }

    @Override
    public String getType() {
        return null;
    }
}
