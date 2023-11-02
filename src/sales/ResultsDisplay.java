package sales;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ResultsDisplay {

    // A simple mapping from file name to month names
    private static final Map<String, String> monthNames = new HashMap<>();
    static {
        monthNames.put("07.csv", "July");
        monthNames.put("08.csv", "August");
        monthNames.put("09.csv", "September");
    }

    private static final DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    /**
     * Displays the results based on the sales data.
     *
     * @param salesDataStore The store containing the sales data.
     */
    public static void displayResults(SalesDataStore salesDataStore) {
        Map<String, Map<String, Double>> storeSales = salesDataStore.getSalesData();

        AtomicReference<String> topStoreQuarter = new AtomicReference<>("");
        AtomicReference<Double> topSalesQuarter = new AtomicReference<>(0.0);
        Map<String, String> topStoreEachMonth = new HashMap<>();
        Map<String, Double> topSalesEachMonth = new HashMap<>();

        // Sort months
        List<String> sortedMonths = storeSales.values().stream()
                .flatMap(m -> m.keySet().stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        storeSales.forEach((store, monthlySales) -> {
            double totalQuarterSales = 0;
            for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
                String month = entry.getKey();
                double sales = entry.getValue();
                totalQuarterSales += sales;

                // Check top store for each month
                topSalesEachMonth.compute(month, (k, v) -> {
                    if (v == null || sales > v) {
                        topStoreEachMonth.put(month, store);
                        return sales;
                    }
                    return v;
                });
            }

            // Check top store for the quarter
            if (totalQuarterSales > topSalesQuarter.get()) {
                topSalesQuarter.set(totalQuarterSales);
                topStoreQuarter.set(store);
            }
        });

        // Sales totals for the months
        System.out.println("\nSales totals for the months:");
        System.out.print("Store \t\t\t");
        sortedMonths.forEach(month -> System.out.print(monthNames.getOrDefault(month, month) + "\t\t\t"));
        System.out.println();
        storeSales.forEach((store, monthlySales) -> {
            System.out.print(store + "    ");
            sortedMonths.forEach(month -> {
                Double sales = monthlySales.get(month);
                System.out.print((sales != null ? currencyFormat.format(sales) : "$0.00") + "\t\t");
            });
            System.out.println();
        });

        // Top store for each month:
        System.out.println("\nTop store for each month:");
        System.out.println("Month\t\tTop Store");
        topStoreEachMonth.forEach((month, store) -> {
            // Translate filename to month name for display
            String readableMonth = monthNames.getOrDefault(month, month);
            System.out.println(readableMonth + "\t\t" + store);
        });


        // Top store for the quarter
        System.out.println("\nTop store for the quarter:");
        System.out.println("┌────────────────────┐");
        System.out.println("  " + topStoreQuarter.get());
        System.out.println("└────────────────────┘");
    }
}
