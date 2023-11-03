package sales;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.*;

/**
 * This class is responsible for extracting the contents of a ZIP file containing sales data.
 * It should be executed before running the SalesCalculator.java to ensure the data is available.
 * The class also verifies the number of files extracted against an expected count.
 */
public class Setup {

    // Constants for file paths and expected file counts
    private static final String ZIP_FILE_PATH = "data/SalesData.zip";
    private static final String DESTINATION_DIRECTORY = "data/";
    private static final String UNZIPPED_DIRECTORY_PATH = "data/SalesData";
    private static final int EXPECTED_FILE_COUNT = 45;

    /**
     * The main method starts the unzip process and verifies the file count.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            unzipSalesDataArchive(ZIP_FILE_PATH, DESTINATION_DIRECTORY);
            if (verifyUnzippedFiles(UNZIPPED_DIRECTORY_PATH, EXPECTED_FILE_COUNT)) {
                System.out.println("Unzip successful and all files are present.");
            } else {
                System.out.println("Unzip failed or some files are missing.");
            }
        } catch (IOException e) {
            System.out.println("Unzip failed: "+ e);
        }
    }

    /**
     * Unzips the specified ZIP file into the given destination directory.
     *
     * @param zipFilePath Path to the ZIP file to be unzipped.
     * @param destinationDirectory Directory to unzip the files into.
     * @throws IOException if an I/O error occurs.
     */
    private static void unzipSalesDataArchive(String zipFilePath, String destinationDirectory) throws IOException {
        System.out.println("Starting to unzip the file: " + zipFilePath);
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destinationDirectory, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    File parentDirectory = newFile.getParentFile();
                    if (!parentDirectory.isDirectory() && !parentDirectory.mkdirs()) {
                        throw new IOException("Failed to create directory " + parentDirectory);
                    }
                    System.out.println("Extracting file: "+ newFile.getPath());
                    try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                        int length;
                        while ((length = zipInputStream.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, length);
                        }
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
        }
        System.out.println("Unzipping completed.");
    }

    /**
     * Verifies the number of files unzipped against the expected count.
     *
     * @param unzippedDirectoryPath Path to the directory where files were unzipped.
     * @param expectedFileCount The expected number of files to validate.
     * @return true if the actual file count matches the expected count, false otherwise.
     * @throws IOException if an I/O error occurs.
     */
    private static boolean verifyUnzippedFiles(String unzippedDirectoryPath, int expectedFileCount) throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(unzippedDirectoryPath))) {
            long actualFileCount = files.filter(Files::isRegularFile).count();
            System.out.println("Expected file count: " + expectedFileCount + ", Actual file count: " + actualFileCount);
            return actualFileCount == expectedFileCount;
        }
    }
}
