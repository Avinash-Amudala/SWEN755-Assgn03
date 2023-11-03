package sales;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * This class is responsible for calculating sales from data files.
 * It processes files within a specified directory using multiple threads
 * to improve performance. The processed sales data is stored and can be
 * displayed upon completion of calculations.
 */
public class SalesCalculator {

    // The root directory where sales data files are located.
    private static final String ROOT_DIR = "data/SalesData";
    // The number of threads to use for reading and processing the sales data files.
    private static final int THREAD_POOL_SIZE = 10;

    // An instance of SalesDataStore to hold the calculated sales data.
    private final SalesDataStore salesDataStore = new SalesDataStore();

    /**
     * Executes the sales calculation by processing each sales data file found in the root directory.
     * It creates a fixed thread pool to read the sales data concurrently, speeding up the process.
     * After processing, the results are displayed using the ResultsDisplay class.
     */
    public void calculateSales() {
        // Create a thread pool with a fixed number of threads to process files.
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        long startTime = System.currentTimeMillis();
        try (Stream<Path> paths = Files.walk(Paths.get(ROOT_DIR))) {
            // Filter out non-regular files (like directories) and process each file.
            paths.filter(Files::isRegularFile).forEach(file -> {
                // Submit a new task for reading and processing the sales data file.
                executorService.submit(new SalesReader(file, salesDataStore));
            });
        } catch (IOException e) {
            // Handle exceptions related to file IO operations.
            e.printStackTrace();
        }

        // Initiate a graceful shutdown of the executor service.
        executorService.shutdown();
        try {
            // Wait for the currently executing tasks to finish within the given timeout.
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // If the tasks did not finish in time, force them to stop.
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            // If the waiting thread is interrupted, stop all executing tasks immediately.
            executorService.shutdownNow();
            // Preserve the interrupt status of the thread.
            Thread.currentThread().interrupt();
        }

        // Stop the timer
        long endTime = System.currentTimeMillis();



        // Once all data has been processed, display the results.
        ResultsDisplay.displayResults(salesDataStore);

        long totalTime = endTime - startTime;
        System.out.println("Processed all files in " + totalTime + " ms");
    }

    /**
     * The main method to run the SalesCalculator. It starts the sales calculation process.
     * @param args Command-line arguments (not used here).
     */
    public static void main(String[] args) {
        // Create an instance of SalesCalculator and start the sales calculation.
        new SalesCalculator().calculateSales();
    }
}
