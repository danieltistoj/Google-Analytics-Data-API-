package org.example;
import com.google.analytics.data.v1beta.*;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Google Analytics Data API sample quickstart application.
 *
 * <p>This application demonstrates the usage of the Analytics Data API using service account
 * credentials.
 *
 * <p>Before you start the application, please review the comments starting with "TODO(developer)"
 * and update the code to use correct values.
 *
 * <p>To run this sample using Maven:
 *
 * <pre>{@code
 * cd google-analytics-data
 * mvn compile exec:java -Dexec.mainClass="com.google.analytics.data.samples.QuickstartSample"
 * }</pre>
 */

public class QuickstartSample {
    private static final String KEY_FILE_LOCATION = "credentials.json";

    public static void main(String... args) throws IOException, InterruptedException {
        /**
         * TODO(developer): Replace this variable with your Google Analytics 4 property ID before
         * running the sample.
         */
        String propertyId = "YOUR-GA4-PROPERTY-ID";
        sampleRunReport(propertyId);
    }

    // This is an example snippet that calls the Google Analytics Data API and runs a simple report
    // on the provided GA4 property id.
    static void sampleRunReport(String propertyId) throws IOException, InterruptedException {
        // Using a default constructor instructs the client to use the credentials
        // specified in GOOGLE_APPLICATION_CREDENTIALS environment variable.

        InputStream fileInputStream = new FileInputStream(KEY_FILE_LOCATION);
        GoogleCredentials credential = GoogleCredentials.fromStream(fileInputStream);
        BetaAnalyticsDataSettings betaAnalyticsDataSettings = BetaAnalyticsDataSettings
                .newHttpJsonBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credential))
                .build();


        BetaAnalyticsDataClient analyticsData = null;
        try {
            analyticsData = BetaAnalyticsDataClient.create(betaAnalyticsDataSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RunReportRequest request =
                RunReportRequest.newBuilder()
                        .setProperty("properties/" + propertyId)
                        .addDimensions(Dimension.newBuilder().setName("city"))
                        .addMetrics(Metric.newBuilder().setName("activeUsers"))
                        //.addMetrics(Metric.newBuilder().setName("totalRevenue"))
                        .addDateRanges(DateRange.newBuilder().setStartDate("7daysAgo").setEndDate("today"))
                        .build();

        // Make the request.
        RunReportResponse response = analyticsData.runReport(request);

        // Iterate through every row of the API response.
        // Verifica si la lista de filas está vacía
        if (response.getRowsList().isEmpty()) {
            System.out.println("No data available.");
        } else {
            System.out.println("Report result:");
            // Itera a través de cada fila de la respuesta
            for (Row row : response.getRowsList()) {
                System.out.printf(
                        "%s, %s%n", row.getDimensionValues(0).getValue(), row.getMetricValues(0).getValue());
            }
        }

    }


}