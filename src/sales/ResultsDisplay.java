package sales;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * This class is responsible for displaying the processed sales data results.
 * It handles the presentation of total sales by month, and also identifies the top-performing store
 * for each month and for the entire quarter.
 */
public class ResultsDisplay {

    // Mapping from file names (used as month identifiers) to human-readable month names.
    private static final Map<String, String> MONTH_NAMES = new HashMap<>();
    static {
        MONTH_NAMES.put("07.csv", "July");
        MONTH_NAMES.put("08.csv", "August");
        MONTH_NAMES.put("09.csv", "September");
    }

    // Formatter to display currency in a readable format.
    private static final DecimalFormat CURRENCY_FORMATTER = new DecimalFormat("$#,##0.00");

    /**
     * Displays the sales results in a readable format.
     *
     * @param salesDataStore The store containing the aggregated sales data.
     */
    public static void displayResults(SalesDataStore salesDataStore) {
        Map<String, Map<String, Double>> aggregatedSalesData = salesDataStore.getSalesData();

        AtomicReference<String> topStoreForQuarter = new AtomicReference<>("");
        AtomicReference<Double> topSalesForQuarter = new AtomicReference<>(0.0);
        Map<String, String> topStoreForMonth = new HashMap<>();
        Map<String, Double> topSalesForMonth = new HashMap<>();

        // Obtain a sorted list of months from the sales data.
        List<String> sortedMonths = aggregatedSalesData.values().stream()
                .flatMap(monthlySales -> monthlySales.keySet().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Process each store's sales data.
        aggregatedSalesData.forEach((storeName, monthlySales) -> {
            double totalSalesForQuarter = monthlySales.values().stream().mapToDouble(Double::doubleValue).sum();

            monthlySales.forEach((month, sales) -> {
                // Update the top store for each month.
                topSalesForMonth.compute(month, (currentMonth, highestSales) -> {
                    if (highestSales == null || sales > highestSales) {
                        topStoreForMonth.put(month, storeName);
                        return sales;
                    }
                    return highestSales;
                });
            });

            // Update the top store for the quarter.
            if (totalSalesForQuarter > topSalesForQuarter.get()) {
                topSalesForQuarter.set(totalSalesForQuarter);
                topStoreForQuarter.set(storeName);
            }
        });

        // Display sales totals for each month for all stores.
        printMonthlySalesTotals(sortedMonths, aggregatedSalesData);

        // Display the top-performing store for each month.
        printTopStoreForMonth(topStoreForMonth);

        // Display the top-performing store for the quarter.
        printTopStoreForQuarter(topStoreForQuarter.get());
    }

    /**
     * Prints the monthly sales totals for each store.
     *
     * @param sortedMonths       A list of months sorted in chronological order.
     * @param aggregatedSalesData The aggregated sales data for all stores.
     */
    private static void printMonthlySalesTotals(List<String> sortedMonths, Map<String, Map<String, Double>> aggregatedSalesData) {
        System.out.println("\nSales totals for the months:");
        System.out.print("Store \t\t\t");
        sortedMonths.forEach(month -> System.out.print(MONTH_NAMES.getOrDefault(month, month) + "\t\t\t"));
        System.out.println();
        aggregatedSalesData.forEach((storeName, monthlySales) -> {
            System.out.print(storeName + "    ");
            sortedMonths.forEach(month -> {
                Double sales = monthlySales.get(month);
                System.out.print((sales != null ? CURRENCY_FORMATTER.format(sales) : "$0.00") + "\t\t");
            });
            System.out.println();
        });
    }

    /**
     * Prints the top-performing store for each month.
     *
     * @param topStoreForMonth A map of the top store for each month.
     */
    private static void printTopStoreForMonth(Map<String, String> topStoreForMonth) {
        System.out.println("\nTop store for each month:");
        System.out.println("Month\t\tTop Store");
        topStoreForMonth.forEach((month, storeName) -> {
            String readableMonth = MONTH_NAMES.getOrDefault(month, month);
            System.out.println(readableMonth + "\t\t" + storeName);
        });
    }

    /**
     * Prints the top-performing store for the quarter.
     *
     * @param topStoreForQuarter The name of the top store for the quarter.
     */
    private static void printTopStoreForQuarter(String topStoreForQuarter) {
        System.out.println("\nTop store for the quarter:");
        System.out.println("┌────────────────────┐");
        System.out.println("│ " + topStoreForQuarter + " │");
        System.out.println("└────────────────────┘");
    }
}
