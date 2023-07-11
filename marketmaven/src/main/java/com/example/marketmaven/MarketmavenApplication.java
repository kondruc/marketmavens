package com.example.marketmaven;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;


@SpringBootApplication
@ComponentScan(basePackages = "com.example.controller")
//@Import(SwaggerConfig.class)
public class MarketmavenApplication {

	public static void main(String[] args) {
		/*HttpClient httpClient = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=10");
		request.addHeader("X-CMC_PRO_API_KEY", "336d40d3-77e0-480a-91cc-c89e338b0c8d");

		try {
			HttpResponse response = httpClient.execute(request);
			String responseBody = EntityUtils.toString(response.getEntity());

			// Process the response body
			System.out.println(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		run(MarketmavenApplication.class, args);
	}


}
