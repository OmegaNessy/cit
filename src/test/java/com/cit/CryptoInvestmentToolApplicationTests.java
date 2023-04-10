package com.cit;

import com.cit.controller.CryptoInfoController;
import com.cit.entity.Rate;
import com.cit.service.CryptoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class CryptoInvestmentToolApplicationTests {
	@InjectMocks
	private CryptoInfoController cryptoInfoController;

	private MockMvc mockMvc;

	@Mock
	private CryptoService cryptoService;

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(cryptoInfoController).build();
	}

	@Test
	void testGetCryptoCurrencyInfoByParameters() throws Exception {
		Map<String, Rate> rates = Map.of(
				"min",new Rate(1643022000000L, BigDecimal.valueOf(33276.59))
//				"max",new Rate(1641081600000L, BigDecimal.valueOf(47722.66)),
//				"oldest",new Rate(1641009600000L, BigDecimal.valueOf(46813.21)),
//				"newest",new Rate(1643659200000L, BigDecimal.valueOf(38415.79))
			);
		List<String> searchType = List.of("min");
		String cryptoName = "BTC";
		when(cryptoService.getCryptoByParameters(searchType,cryptoName)).thenReturn(rates);

		mockMvc.perform(MockMvcRequestBuilders.get("/crypto?cryptoName=BTC&searchType=min")
//											  .requestAttr("cryptoName",cryptoName)
//											  .requestAttr("searchType","min"))
				)
				.andExpect(status().isOk())
				.andExpect(content().json("{\"min\": {\"timestamp\": 1643022000000,\"value\": 33276.59}}"));
		verify(cryptoService).getCryptoByParameters(searchType,cryptoName);
	}
}
