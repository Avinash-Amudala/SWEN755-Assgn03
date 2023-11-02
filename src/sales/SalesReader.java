package sales;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SalesReader implements Runnable {
    private final Path filePath;
    private final SalesDataStore salesDataStore;

    public SalesReader(Path filePath, SalesDataStore salesDataStore) {
        this.filePath = filePath;
        this.salesDataStore = salesDataStore;
    }

    @Override
    public void run() {
        String storeName = filePath.getParent().getFileName().toString();
        String month = filePath.getFileName().toString().split("_")[2];

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            double totalSales = 0;
            while ((line = reader.readLine()) != null) {
                if (line.contains("SalesAmount")) continue; // Skip header line
                String[] data = line.split(",");
                totalSales += Double.parseDouble(data[2]); // Assuming SalesAmount is in the third column
            }
            salesDataStore.addSales(storeName, month, totalSales);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
