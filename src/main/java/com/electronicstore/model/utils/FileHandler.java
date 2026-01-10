package com.electronicstore.model.utils;

import com.electronicstore.model.sales.Bill;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class FileHandler {
    public static final String DATA_DIRECTORY = "store_data";

    // Avoid hard-coded delimiters by using Path/resolve
    private static final Path DATA_PATH = Paths.get(DATA_DIRECTORY);
    private static final Path BILLS_PATH = DATA_PATH.resolve("bills");

    static {
        try {
            // Create necessary directories 
            Files.createDirectories(DATA_PATH);
            Files.createDirectories(BILLS_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Generic method to save object to binary file
    public static <T extends Serializable> void saveToFile(T object, String filename)
            throws IOException {
        Path filepath = DATA_PATH.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath.toFile()))) {
            oos.writeObject(object);
        }
    }

    // Generic method to read object from binary file
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFromFile(String filename)
            throws IOException, ClassNotFoundException {
        Path filepath = DATA_PATH.resolve(filename);
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filepath.toFile()))) {
            return (T) ois.readObject();
        }
    }

    // Method to save list of objects to binary file
    public static <T extends Serializable> void saveListToFile(List<T> list, String filename)
            throws IOException {
        Path filepath = DATA_PATH.resolve(filename);
        System.out.println("Saving to file: " + filepath);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filepath.toFile()))) {
            oos.writeObject(list);
            System.out.println("Successfully saved " + list.size() + " items");
        }
    }

    // Method to read list of objects from binary file
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> readListFromFile(String filename)
            throws IOException, ClassNotFoundException {
        return (List<T>) readFromFile(filename);
    }

    // Method to export bill to text file
    public static void exportBill(Bill bill) throws IOException {
        String fileName = bill.getBillNumber() + "_" + bill.getDateTime().toLocalDate() + ".txt";
        Path billPath = BILLS_PATH.resolve(fileName);

        String billContent = bill.generatePrintableFormat();
        Files.write(
                billPath,
                billContent.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    // Method to save data to text file
    public static void saveToTextFile(String content, String filename) throws IOException {
        Path filepath = DATA_PATH.resolve(filename);
        Files.write(
                filepath,
                content.getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );
    }

    // Method to read data from text file
    public static String readFromTextFile(String filename) throws IOException {
        Path filepath = DATA_PATH.resolve(filename);
        return Files.readString(filepath);
    }

    // Method to list all files in a directory
    public static List<String> listFiles(String directory) {
        Path dirPath = DATA_PATH.resolve(directory);
        try {
            return Files.list(dirPath)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
