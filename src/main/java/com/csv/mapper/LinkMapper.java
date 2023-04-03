package com.csv.mapper;

import com.csv.model.Link;
import org.springframework.stereotype.Component;

public class LinkMapper implements CsvMapper<String[], Link> {
    @Override
    public Link toObj(String[] str) {
        if (str == null || str.length != 2) {
            return null;
        }
        Link link = new Link();
        link.setSentenceId(str[0]);
        link.setTranslationId(str[1]);

        return link;
    }

    @Override
    public String getType() {
        return "linkMapper";
    }
}
