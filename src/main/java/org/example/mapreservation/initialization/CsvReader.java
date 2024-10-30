package org.example.mapreservation.initialization;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvReader {

    private final String fileName = "hairshops/output.csv";
    private final String encoding = "UTF-8";

    public List<String[]> readLines() {
        List<String[]> data = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(fileName);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), encoding))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split("\",\"");
                data.add(values);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        data.remove(0); // 헤드 제거
        return data;
    }
}
