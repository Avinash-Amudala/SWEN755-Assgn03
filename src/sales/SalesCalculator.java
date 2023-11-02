package sales;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class SalesCalculator {

    private static final String ROOT_DIR = "data/SalesData";
    private static final int THREAD_POOL_SIZE = 10;

    private final SalesDataStore salesDataStore = new SalesDataStore();

    public void calculateSales() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try (Stream<Path> paths = Files.walk(Paths.get(ROOT_DIR))) {
            paths.filter(Files::isRegularFile).forEach(file -> {
                executorService.submit(new SalesReader(file, salesDataStore));
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        ResultsDisplay.displayResults(salesDataStore);
    }

    public static void main(String[] args) {
        new SalesCalculator().calculateSales();
    }
}
