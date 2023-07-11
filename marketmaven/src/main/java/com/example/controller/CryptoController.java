package com.example.controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;


@RestController
@RequestMapping("/api")
public class CryptoController {

    @GetMapping("/coins/latest")
    public String getLatestCryptocurrencies() {
        // Implementation to retrieve latest cryptocurrencies
        HttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=10");
        request.addHeader("X-CMC_PRO_API_KEY", "336d40d3-77e0-480a-91cc-c89e338b0c8d");

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");



            HttpResponse response = httpClient.execute(request);
            responseBody = EntityUtils.toString(response.getEntity());

           // List<CryptoData> cryptoDataList = parseResponse(responseBody);

            // Process the response body
            /*System.out.println(responseBody);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

   /* private List<CryptoData> parseResponse(String responseBody) {
        // Use a JSON parsing library (e.g., Gson or Jackson) to parse the response into objects
        // Example code using Gson:
        Gson gson = new Gson();
        Type listType = new TypeToken<List<CryptoData>>(){}.getType();
        return gson.fromJson(responseBody, listType);
    }*/
    @GetMapping("/coins/trending")
    public String getTrendingCryptocurrencies() {
        // Implementation to retrieve trending cryptocurrencies
        HttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/trending/latest?limit=10");
        request.addHeader("X-CMC_PRO_API_KEY", "336d40d3-77e0-480a-91cc-c89e338b0c8d");

        try {
            HttpResponse response = httpClient.execute(request);
            responseBody = EntityUtils.toString(response.getEntity());

            // Process the response body
            /*System.out.println(responseBody);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    @GetMapping("/coins/trending/mostvisited")
    public String getTrendingMostvisitedCryptocurrencies() {
        // Implementation to retrieve top cryptocurrencies
        HttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/trending/most-visited?limit=10");
        request.addHeader("X-CMC_PRO_API_KEY", "336d40d3-77e0-480a-91cc-c89e338b0c8d");

        try {
            HttpResponse response = httpClient.execute(request);
            responseBody = EntityUtils.toString(response.getEntity());

            // Process the response body
            /*System.out.println(responseBody);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }


/*    @PostMapping("/coins")
    public String createCryptocurrency(@RequestBody CryptoRequest request) {
        // Implementation to create a new cryptocurrency
        return "Cryptocurrency created";
    }

    @PutMapping("/coins/{id}")
    public String updateCryptocurrency(@PathVariable String id, @RequestBody CryptoRequest request) {
        // Implementation to update an existing cryptocurrency
        return "Cryptocurrency updated";
    }

    @DeleteMapping("/coins/{id}")
    public String deleteCryptocurrency(@PathVariable String id) {
        // Implementation to delete a cryptocurrency
        return "Cryptocurrency deleted";
    }*/
}
