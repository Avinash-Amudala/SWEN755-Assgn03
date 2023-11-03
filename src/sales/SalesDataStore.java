package sales;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * This class serves as a thread-safe storage for sales data. It allows concurrent
 * modification and retrieval of sales records by store name and month.
 */
public class SalesDataStore {

    // Store the sales data in a nested ConcurrentHashMap for thread safety
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Double>> storeSales;

    public SalesDataStore() {
        storeSales = new ConcurrentHashMap<>();
    }

    /**
     * Adds sales data for a store for a specific month.
     *
     * @param storeName The name of the store.
     * @param month     The month for which sales data is added.
     * @param sales     The sales amount to add.
     */
    public void addSales(String storeName, String month, double sales) {
        // Compute the data in a thread-safe way
        storeSales.computeIfAbsent(storeName, k -> new ConcurrentHashMap<>())
                .merge(month, sales, Double::sum);
    }

    /**
     * Retrieves the sales data for all stores.
     *
     * @return A Map with store names as keys and their monthly sales data as values.
     */
    public Map<String, Map<String, Double>> getSalesData() {
        // Returning a copy of the data to maintain encapsulation and thread safety
        Map<String, Map<String, Double>> copy = new HashMap<>();
        storeSales.forEach((store, monthlySales) -> copy.put(store, new HashMap<>(monthlySales)));
        return copy;
    }
}
