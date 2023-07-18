package com.example.controller;

import com.example.CryptoData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static java.sql.DriverManager.getConnection;


@RestController
@RequestMapping("/api")
public class CryptoController {

    private List<CryptoData> parseResponse(String responseBody) {
       // Use a JSON parsing library (e.g., Gson or Jackson) to parse the response into objects
       // Example code using Gson:
       Gson gson = new Gson();
       Type listType = new TypeToken<List<CryptoData>>(){}.getType();
       return gson.fromJson(responseBody, listType);
   }
    @GetMapping("/coins/latest")
    public void getLatestCryptocurrencies() {
        // Implementation to retrieve latest cryptocurrencies
        HttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=10");
        request.addHeader("X-CMC_PRO_API_KEY", "336d40d3-77e0-480a-91cc-c89e338b0c8d");
        try {
            HttpResponse response = httpClient.execute(request);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Parse the JSON string
        if (responseBody != null) {
            JSONObject json = new JSONObject(responseBody);
            JSONArray dataArray = json.getJSONArray("data");

            // Save data into the database
            saveDataToDatabase(dataArray);
        }
    }

    private static void saveDataToDatabase(JSONArray dataArray) {
        // Database connection parameters
      /*  String dbUrl = "jdbc:mysql://localhost:3306/your_database_name";
        String dbUser = "your_database_username";
        String dbPassword = "your_database_password";*/

        try {
            // Establish the database connection
            Connection connection = getConnection("jdbc:sqlite:identifier.sqlite");

            // Prepare the INSERT query
            String insertQuery = "INSERT INTO coinmarketapi (id, name, symbol, price, rank) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            // Insert each cryptocurrency entry into the database
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject cryptocurrency = dataArray.getJSONObject(i);
                int id = cryptocurrency.getInt("id");
                String name = cryptocurrency.getString("name");
                String symbol = cryptocurrency.getString("symbol");
                int rank = cryptocurrency.getInt("cmc_rank");
                double price = cryptocurrency.getJSONObject("quote").getJSONObject("USD").getDouble("price");

                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, symbol);
                preparedStatement.setDouble(4, price);
                preparedStatement.setInt(5, rank);


                preparedStatement.executeUpdate();
            }

            // Close the resources
            preparedStatement.close();
            connection.close();

            System.out.println("Data saved to the database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
