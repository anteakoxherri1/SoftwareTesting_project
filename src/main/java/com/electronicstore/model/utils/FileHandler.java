package com.electronicstore.model.utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Directory 
    public static final Path DATA_DIRECTORY = Paths.get("data");

    static {
        try {
            if (!Files.exists(DATA_DIRECTORY)) {
                Files.createDirectories(DATA_DIRECTORY);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create data directory", e);
        }
    }

    // ==============================
    // GENERIC SAVE
    // ==============================
    public static <T> void saveListToFile(String fileName, List<T> list) {
        Path filePath = DATA_DIRECTORY.resolve(fileName);

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(filePath))) {

            oos.writeObject(list);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==============================
    // GENERIC READ
    // ==============================
    @SuppressWarnings("unchecked")
    public static <T> List<T> readListFromFile(String fileName) {
        Path filePath = DATA_DIRECTORY.resolve(fileName);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois =
                     new ObjectInputStream(Files.newInputStream(filePath))) {

            Object obj = ois.readObject();
            return (List<T>) obj;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ==============================
    // EXPORT BILL AS TXT
    // ==============================
    public static void exportBillToTextFile(String billId, String content) {
        Path billsDir = DATA_DIRECTORY.resolve("bills");

        try {
            if (!Files.exists(billsDir)) {
                Files.createDirectories(billsDir);
            }

            Path billFile = billsDir.resolve(billId + ".txt");
            Files.writeString(billFile, content,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
