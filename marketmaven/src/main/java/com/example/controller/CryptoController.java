package com.example.controller;

import com.example.model.LoginRequest;
import com.example.model.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.sql.*;

import static java.sql.DriverManager.getConnection;


@RestController
@RequestMapping("/api")
public class CryptoController {


    @GetMapping("/coins/latest")
    public void getLatestCryptocurrencies() {
        // Implementation to retrieve latest cryptocurrencies
        HttpClient httpClient = HttpClientBuilder.create().build();
        String responseBody = null;
        HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=40");
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

            // prepare select query
            String selectQuery = "SELECT id FROM coinmarketapi WHERE id = ?";

            // Prepare the INSERT query
            String insertQuery = "INSERT INTO coinmarketapi (id, name, symbol, price, rank) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            // Insert each cryptocurrency entry into the database
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject cryptocurrency = dataArray.getJSONObject(i);
                int id = cryptocurrency.getInt("id");
                String name = cryptocurrency.getString("name");
                String symbol = cryptocurrency.getString("symbol");
                int rank = cryptocurrency.getInt("cmc_rank");
                double price = cryptocurrency.getJSONObject("quote").getJSONObject("USD").getDouble("price");

                // Check if the entry exists based on the unique identifier (cryptocurrency ID)
                selectStatement.setInt(1, id);
                if (selectStatement.executeQuery().next()) {
                    System.out.println("Entry with ID " + id + " already exists. Skipping insertion.");
                    continue; // Entry already exists, skip this iteration
                }

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

    @PostMapping("/user/add")
    public void addUserToDatabase(@RequestBody UserInfo userInfo) {
        try {
            // Establish the database connection
            Connection connection = getConnection("jdbc:sqlite:identifier.sqlite");

            // Prepare the INSERT query
            String insertQuery = "INSERT INTO userdata (username, password, fullname, email, dob, image, bio) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

            // Insert user information into the database
            if (userInfo.getUserName() != null) {
                preparedStatement.setString(1, userInfo.getUserName());
            } else {
                preparedStatement.setNull(1, Types.VARCHAR);
            }

            if (userInfo.getPassword() != null) {
                preparedStatement.setString(2, userInfo.getPassword());
            } else {
                preparedStatement.setNull(2, Types.VARCHAR);
            }

            if (userInfo.getFullName() != null) {
                preparedStatement.setString(3, userInfo.getFullName());
            } else {
                preparedStatement.setNull(3, Types.VARCHAR);
            }

            if (userInfo.getEmailId() != null) {
                preparedStatement.setString(4, userInfo.getEmailId());
            } else {
                preparedStatement.setNull(4, Types.VARCHAR);
            }

            if (userInfo.getDob() != null) {
                preparedStatement.setDate(5, (Date) userInfo.getDob());
            } else {
                preparedStatement.setNull(5, Types.DATE);
            }

            if (userInfo.getImage() != null) {
                preparedStatement.setString(6, userInfo.getImage());
            } else {
                preparedStatement.setNull(6, Types.VARCHAR);
            }

            if (userInfo.getBio() != null) {
                preparedStatement.setString(7, userInfo.getBio());
            } else {
                preparedStatement.setNull(7, Types.VARCHAR);
            }

           /* if (userInfo.getEmailId() != null) {
                preparedStatement.setInt(3, userInfo.getEmailId());
            } else {
                preparedStatement.setNull(3, Types.INTEGER);
            }*/
            preparedStatement.executeUpdate();

            // Close the resources
            preparedStatement.close();
            connection.close();

            System.out.println("User data saved to the database successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/login")
        public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest) {
            try {
                // Validate the username and password
                if (!validateUserCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
                    // Authentication failed
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
                }

                // Authentication successful, generate and return the JWT
                String jwtToken = generateJwtToken(loginRequest.getUsername());
                return ResponseEntity.ok(jwtToken);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred during authentication");
            }
        }

        private boolean validateUserCredentials(String username, String password) {
            try {
                // Establish the database connection
                Connection connection = getConnection("jdbc:sqlite:identifier.sqlite");

                // Prepare the SELECT query to check if the user exists with the given username and password
                String selectQuery = "SELECT id FROM userdata WHERE username = ? AND password = ?";
                PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                selectStatement.setString(1, username);
                selectStatement.setString(2, password);

                ResultSet resultSet = selectStatement.executeQuery();
                boolean userExists = resultSet.next();

                // Close the resources
                resultSet.close();
                selectStatement.close();
                connection.close();

                return userExists;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        private String generateJwtToken(String username) {
            // Your JWT generation logic goes here
            // Use a library like 'io.jsonwebtoken' to create a signed JWT with the user details (e.g., username)
            // and return the generated token as a string.
            int keyLengthBytes = 64; // 512 bits
            String randomSecretKey = generateRandomSecretKey(keyLengthBytes);

            // Example using 'io.jsonwebtoken' library:
            String secretKey = randomSecretKey; // Replace this with your actual secret key
            String jwtToken = Jwts.builder()
                    .setSubject(username)
                    .setExpiration(new Date(System.currentTimeMillis() + System.currentTimeMillis()+(60*60*1000))) // Set token expiration time
                    .signWith(SignatureAlgorithm.HS512, secretKey)
                    .compact();
            return jwtToken;
        }

        private static String generateRandomSecretKey(int keyLengthBytes) {
            SecureRandom secureRandom = new SecureRandom();
            byte[] keyBytes = new byte[keyLengthBytes];
            secureRandom.nextBytes(keyBytes);
            return Base64.getEncoder().encodeToString(keyBytes);
        }
        // Additional code for getConnection() and other methods (if needed)
 }




