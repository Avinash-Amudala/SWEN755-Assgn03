import java.util.*;
import java.util.concurrent.*;

public class SalesAggregator {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int CHUNK_SIZE = 10000; // Hypothetical chunk size
    private ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    // Splits the data into chunks for parallel processing
    public List<List<Integer>> splitDataIntoChunks(List<Integer> data) {
        List<List<Integer>> chunks = new ArrayList<>();
        for (int i = 0; i < data.size(); i += CHUNK_SIZE) {
            chunks.add(data.subList(i, Math.min(i + CHUNK_SIZE, data.size())));
        }
        return chunks;
    }

    // Sums up a chunk of transaction amounts
    public Integer sumChunk(List<Integer> chunk) {
        return chunk.stream().mapToInt(Integer::intValue).sum();
    }

    // Aggregates sales data by dispatching chunks to threads for parallel summing
    public int aggregateSales(List<Integer> salesData) throws InterruptedException, ExecutionException {
        List<List<Integer>> chunks = splitDataIntoChunks(salesData);
        List<Future<Integer>> results = new ArrayList<>();

        for (List<Integer> chunk : chunks) {
            Future<Integer> result = threadPool.submit(() -> sumChunk(chunk));
            results.add(result);
        }

        int totalSales = 0;
        for (Future<Integer> result : results) {
            totalSales += result.get();
        }

        threadPool.shutdown();
        return totalSales;
    }

    // Generates a hypothetical dataset of sales transactions
    public static List<Integer> generateDataset(int size) {
        Random rand = new Random();
        List<Integer> dataset = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            dataset.add(rand.nextInt(1000) + 1);  // Random number between 1 and 1000
        }
        return dataset;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Integer> salesData = generateDataset(1_000_000);  // 1 million transactions
        SalesAggregator aggregator = new SalesAggregator();
        int totalSales = aggregator.aggregateSales(salesData);
        System.out.println("Total Sales: " + totalSales);
    }
}
