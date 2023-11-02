package sales;

import java.io.*;
import java.nio.file.*;
import java.util.stream.Stream;
import java.util.zip.*;

// Class to unzip the SalesData.zip file. Run this class before running SalesCalculator.java
public class Setup {

    private static final String ZIP_FILE_PATH = "data/SalesData.zip";
    private static final String DEST_DIR = "data/";
    private static final String UNZIP_DIR = "data/SalesData";
    private static final int EXPECTED_FILE_COUNT = 45;

    public static void main(String[] args) {
        try {
            unzipSalesData(ZIP_FILE_PATH, DEST_DIR);
            if (verifyUnzippedFiles(UNZIP_DIR, EXPECTED_FILE_COUNT)) {
                System.out.println("Unzip successful and all files are present.");
            } else {
                System.out.println("Unzip failed or some files are missing.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void unzipSalesData(String zipFilePath, String destDir) throws IOException {
        System.out.println("Starting to unzip the file: " + zipFilePath);
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(destDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        System.out.println("Unzipping completed.");
    }

    private static boolean verifyUnzippedFiles(String unzippedDirPath, int expectedFileCount) throws IOException {
        try (Stream<Path> files = Files.walk(Paths.get(unzippedDirPath))) {
            long count = files.filter(Files::isRegularFile).count();
            System.out.println("Expected file count: " + expectedFileCount + ", Actual file count: " + count);
            return count == expectedFileCount;
        }
    }
}
