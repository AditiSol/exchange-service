package com.solstice.exchangeservice;

import com.solstice.exchangeservice.model.ExchangeRateResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
public class ExchangeServiceIntegrationTest {

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	MockMvc mockMvc;


	@Test
	public void success() {
		//arrange
		//act
		ResponseEntity<ExchangeRateResponse> exchangeRateResponse = restTemplate
				.getForEntity("http://localhost:8080/exchange-rate?from=USD&to=INR", ExchangeRateResponse.class);

		//assert
		Assert.assertEquals(HttpStatus.OK, exchangeRateResponse.getStatusCode());
		Assert.assertEquals("USD", exchangeRateResponse.getBody().getFromCurrency());
		Assert.assertEquals("INR", exchangeRateResponse.getBody().getToCurrency());
		Assert.assertEquals(86.00, exchangeRateResponse.getBody().getConversion(), 0);
	}


	@Test
	public void notFound() throws Exception{

		//act

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/exchange-rate?from=USA&to=INR"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("message").value("Exchange Rate Not Found"))
				.andExpect(jsonPath("fromCurrency").value("USA"))
				.andExpect(jsonPath("toCurrency").value("INR"));

	}

	@Test
	public void missingRequestParam() throws Exception{

		//act

		mockMvc.perform(MockMvcRequestBuilders.get("http://localhost:8080/exchange-rate?from=AUD"))
				.andExpect(status().isBadRequest());
	}


}
