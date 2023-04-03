package com.csv.mapper;

import com.csv.model.Sentence;

public class SentenceMapper implements CsvMapper<String[], Sentence> {
    @Override
    public Sentence toObj(String[] str) {
        if (str == null || str.length != 3) {
            return null;
        }
        Sentence sentence = new Sentence();
        sentence.setSentenceId(str[0]);
        sentence.setLang(str[1]);
        sentence.setText(str[2]);

        return sentence;
    }

    @Override
    public String getType() {
        return null;
    }
}
