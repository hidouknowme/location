import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class Main {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000; // 1 second

    public static void main(String[] args) {
        try {
            // Step 1: Collect IP Address
            String ipAddress = getIPAddressWithRetry();

            // Step 2: Query Geolocation Service
            JsonObject locationData = queryGeolocationService(ipAddress);

            // Step 3: Parse Geolocation Data
            double latitude = locationData.get("latitude").getAsDouble();
            double longitude = locationData.get("longitude").getAsDouble();

            // Step 4: Display Location
            System.out.println("Estimated Location:");
            System.out.println("Latitude: " + latitude);
            System.out.println("Longitude: " + longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve the IP address of the device with retry mechanism
    private static String getIPAddressWithRetry() throws IOException {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return getIPAddress();
            } catch (IOException e) {
                if (attempt < MAX_RETRIES) {
                    System.err.println("Failed to retrieve IP address. Retrying...");
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ignored) {
                    }
                } else {
                    throw e;
                }
            }
        }
        return null; // unreachable, added to satisfy compiler
    }

    // Method to retrieve the IP address of the device
    @SuppressWarnings("deprecation")
    private static String getIPAddress() throws IOException {
        URL url = new URL("https://api.ipify.org");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.readLine();
        }
    }

    // Method to query a geolocation service and retrieve location data based on IP address
    private static JsonObject queryGeolocationService(String ipAddress) throws IOException {
        String apiUrl = "https://ipapi.co/" + ipAddress + "/json/";
        @SuppressWarnings("deprecation")
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            @SuppressWarnings("deprecation")
            JsonParser parser = new JsonParser();
            @SuppressWarnings("deprecation")
            JsonElement jsonElement = parser.parse(reader);
            return jsonElement.getAsJsonObject();
        }
    }
}
