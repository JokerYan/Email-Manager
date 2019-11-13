package com.manager.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A helper class for all file io operations.
 */
public class StorageUtil {

    /**
     * Saves content to a designated path.
     *
     * @param path    where the content is stored to
     * @param content the content of the file to be saved
     * @throws IOException when error occurs at file io
     */
    public static void saveToFile(Path path, String content) throws IOException {
        if (!fileExists(path)) {
            createFileIfNotExist(path);
        }
        Files.writeString(path, content, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    /**
     * Reads content from a designated path.
     *
     * @param path where content is read from
     * @return the content of the file being read
     * @throws IOException when error occurs at file io
     */
    public static String readFromFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static List<String> readLinesFromFile(Path path) throws IOException {
        String content = readFromFile(path);
        return content.lines().collect(Collectors.toList());
    }

    /**
     * Checks whether the file already exists at the given path.
     *
     * @param path where the existence of the file is to be checked
     * @return the existence of the file
     */
    public static boolean fileExists(Path path) {
        return Files.exists(path);
    }

    /**
     * Creates a file at the designated path when the file does not already exist.
     *
     * @param path where the file is to be checked and saved
     * @return whether a new file is created
     */
    public static boolean createFileIfNotExist(Path path) {
        try {
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Prepares the path of a data folder with the given filename.
     *
     * @return the path of the data file
     */
    public static Path prepareDataFolderPath() {
        return Path.of(".", "data");
    }

    /**
     * Prepares the path of a log folder with the given filename.
     *
     * @return the path of the data file
     */
    public static Path prepareLogFolderPath() {
        return Path.of(".", "data", "logs");
    }

    /**
     * Prepares the path of a data file with the given filename.
     *
     * @param filename the filename of the data file
     * @return the path of the data file
     */
    public static Path prepareDataPath(String filename) {
        return Path.of(".", "data", filename);
    }

    /**
     * Prepares the path of a email file with the given filename.
     *
     * @param filename the filename of the email file
     * @return the path of the email file
     */
    public static Path prepareEmailPath(String filename) {
        return Path.of(".", "data", "emails", filename);
    }

    private static final String[] directories = {"emails", "logs"};

    private static final String[] dataFilenames = {"email.txt", "keywords.txt", "task.txt", "user.txt"};

    private static void createDirectories() throws IOException {
        for (String directory : directories) {
            Path path = prepareDataPath(directory);
            Files.createDirectories(path);
        }
    }

    private static void createDataFiles() {
        for (String filename : dataFilenames) {
            Path path = prepareDataPath(filename);
            createFileIfNotExist(path);
        }
    }

    /**
     * Creates all the necessary directories and files in data folder, including the data folder itself.
     *
     * @return whether this operation is successful.
     */
    public static boolean constructDataDirectory() {
        try {
            createDirectories();
            createDataFiles();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Read content from a file through the input stream.
     *
     * @param in the input stream of the file
     * @return content of the file
     * @author Viacheslav Vedenin from https://stackoverflow.com/questions/309424/how-do-i-read-convert-an-
     *     inputstream-into-a-string-in-java
     */
    public static String readFromInputStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
