package com.csv.mapper;

public interface CsvMapper <T, R> {
    R toObj(T input);

    String getType();
}
