# Google Analytics 4 Reporting Sample

This Java program demonstrates the usage of the Google Analytics Data API to execute a simple report on a specified Google Analytics 4 property. The primary focus is on retrieving data, such as active users by city, within a specified date range.

## Instructions

Before running the sample, make sure to replace the `propertyId` variable with your actual Google Analytics 4 property ID.

## Prerequisites

1. **Google Analytics 4 Property ID:**
   - Obtain your Google Analytics 4 property ID and substitute the placeholder in the code with your real property ID.

2. **Service Account Credentials:**
   - Ensure you possess a service account key file (`credentials.json`) associated with your Google Cloud project.

## Usage

1. Open the `QuickstartSample` class and replace `"YOUR-GA4-PROPERTY-ID"` with your actual Google Analytics 4 property ID.

2. Run the program to execute the `sampleRunReport` method.

## Sample Output

The program queries the Google Analytics Data API for active user data, filtered by city, over the last 7 days. The result is displayed on the console, presenting city names alongside corresponding active user counts.

**Note:** If no data is available for the specified parameters, the program will output "No data available."

Feel free to tailor the code to align with your specific reporting requirements.

---
## Important Note

The Google Analytics Reporting API v4 does not support Google Analytics 4 properties and will throw a 403 error for GA4 properties. As a result, this sample utilizes the [Google Analytics Data API](https://developers.google.com/analytics/devguides/reporting/data/v1) to access the new reporting features specifically designed for GA4 properties.

For more information, refer to the [official documentation](https://developers.google.com/analytics/devguides/reporting/core/v4):

Ensure that you have included the following dependency in your project:

```xml
<dependency>
    <groupId>com.google.analytics</groupId>
    <artifactId>google-analytics-data</artifactId>
    <version>0.43.0</version>
</dependency>
```
