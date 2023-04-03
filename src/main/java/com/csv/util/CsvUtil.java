package com.csv.util;

import com.csv.mapper.CsvMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class CsvUtil {

    public static <T> List<T> readCsvFile(String fileName, CsvMapper<String[], T> mapFunction, String splitCharacter) throws IOException {
        List<T> result = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(splitCharacter);
                T obj = mapFunction.toObj(values);
                result.add(obj);
            }
        }

        return result;
    }

    public static <T> void toCsv(List<T> objects, Function<T, String[]> mapFunction, String path, String splitCharacter) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(path))) {
            // Write the data rows
            for (T obj : objects) {
                String[] values = mapFunction.apply(obj);
                writer.println(String.join(splitCharacter, values));
            }
        } catch (IOException ioException) {
            log.error("Export objets to csv have exception: " + ioException.getMessage(), ioException);
        }
    }

}
