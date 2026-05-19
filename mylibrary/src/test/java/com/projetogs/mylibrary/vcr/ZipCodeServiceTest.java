package com.projetogs.mylibrary.vcr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.projetogs.mylibrary.service.ZipCodeService;
import com.projetogs.mylibrary.util.VcrHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestTemplate;

import com.projetogs.mylibrary.dto.ZipCodeResponseDTO;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZipCodeServiceTest {

        private MockWebServer mockWebServer;
        private ZipCodeService zipCodeService;
        private VcrHelper vcrHelper;

        @BeforeEach
        void setUp() throws IOException {
                mockWebServer = new MockWebServer();
                mockWebServer.start();

                String baseUrl = mockWebServer.url("/ws/").toString();
                zipCodeService = new ZipCodeService(new RestTemplate(), baseUrl);
        }

        @AfterEach
        void tearDown() throws IOException {
                mockWebServer.shutdown();

                if (vcrHelper != null) {
                        vcrHelper.stop();
                        vcrHelper = null;
                }
        }

        @Test
        @Order(1)
        @DisplayName("VCR - Deve retornar dados do CEP válido gravados da API ViaCEP")
        void shouldReturnValidZipCode() throws IOException, InterruptedException {
                vcrHelper = new VcrHelper("src/test/resources/vcr-cassettes", "cep", false);
                vcrHelper.start();

                String baseUrl = vcrHelper.getUrl("/");
                zipCodeService = new ZipCodeService(new RestTemplate(), baseUrl);

                ZipCodeResponseDTO response = zipCodeService.fetchZipCode("01310100");

                assertNotNull(response);
                assertEquals("01310-100", response.getZipCode());
                assertEquals("Avenida Paulista", response.getStreet());
                assertEquals("Bela Vista", response.getNeighborhood());
                assertEquals("São Paulo", response.getCity());
                assertEquals("SP", response.getState());

                RecordedRequest request = vcrHelper.getMockWebServer().takeRequest(2,
                                java.util.concurrent.TimeUnit.SECONDS);

                assertNotNull(request, "A requisição deveria ter sido capturada pelo VCR");
                assertTrue(request.getPath().contains("01310100"),
                                "A URL da requisição deve conter o CEP sem hífen");
        }

        @Test
        @Order(2)
        @DisplayName("VCR - Deve aceitar CEP formatado com hífen e normalizar antes da chamada")
        void ShouldAcceptZipCodeHifen() throws IOException, InterruptedException {
                vcrHelper = new VcrHelper("src/test/resources/vcr-cassettes", "cep", false);
                vcrHelper.start();

                String baseUrl = vcrHelper.getUrl("/");
                zipCodeService = new ZipCodeService(new RestTemplate(), baseUrl);

                ZipCodeResponseDTO response = zipCodeService.fetchZipCode("01310-100");

                assertNotNull(response);
                assertEquals("01310-100", response.getZipCode());

                RecordedRequest request = vcrHelper.getMockWebServer().takeRequest(2,
                                java.util.concurrent.TimeUnit.SECONDS);
                assertTrue(request.getPath().contains("01310100"),
                                "O CEP deve ser enviado sem hífen para a API");
        }

        @Test
        @Order(3)
        @DisplayName("VCR - Deve lançar exceção para CEP não encontrado na API")
        void shouldThrowExceptionWhenZipCodeNotFound() throws IOException {
                vcrHelper = new VcrHelper("src/test/resources/vcr-cassettes", "cepNotFound", false);
                vcrHelper.start();

                String baseUrl = vcrHelper.getUrl("/");
                zipCodeService = new ZipCodeService(new RestTemplate(), baseUrl);

                assertThrows(IllegalArgumentException.class, () -> zipCodeService.fetchZipCode("00000000"),
                                "Deve lançar exceção para CEP inexistente");
        }

        @ParameterizedTest
        @Order(4)
        @ValueSource(strings = { "123", "1234567", "123456789", "abcdefgh" })
        @DisplayName("VCR - Deve lançar exceção para CEP com quantidade de dígitos inválida")
        void shouldThrowExceptionWhenZipCodeFormatIsInvalid(String cepInvalido) {
                assertThrows(IllegalArgumentException.class, () -> zipCodeService.fetchZipCode(cepInvalido),
                                "Deve lançar exceção para CEP inválido: " + cepInvalido);
        }

        @Test
        @Order(5)
        @DisplayName("VCR - Deve confirmar que exatamente uma requisição foi feita para a API")
        void shouldConfirmOnlyOneRequestIsMade() throws InterruptedException, IOException {
                vcrHelper = new VcrHelper("src/test/resources/vcr-cassettes", "cep", false);
                vcrHelper.start();

                String baseUrl = vcrHelper.getUrl("/");
                zipCodeService = new ZipCodeService(new RestTemplate(), baseUrl);

                zipCodeService.fetchZipCode("01310100");

                assertEquals(1, vcrHelper.getMockWebServer().getRequestCount(),
                                "Deve ter feito exatamente 1 requisição para a API");
        }
}