package com.sirma.academy.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class FileService {


    public <T> List<T> loadData(String filePath, Function<String, T> parser) {
        List<T> data = new ArrayList<>();
        if (!Files.exists(Paths.get(filePath))) {
            System.out.println("File not found: " + filePath + ". Returning empty list.");
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine();
            if (line == null) {
                System.out.println("File " + filePath + " is empty.");
                return data;
            }

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    try {
                        data.add(parser.apply(line));
                    } catch (Exception e) {
                        System.err.println("Error parsing line: '" + line + "' in " + filePath + ". Error: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }


    public <T> boolean saveData(String filePath, String header, List<T> data, Function<T, String> formatter) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(header);
            bw.newLine();
            for (T item : data) {
                bw.write(formatter.apply(item));
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}