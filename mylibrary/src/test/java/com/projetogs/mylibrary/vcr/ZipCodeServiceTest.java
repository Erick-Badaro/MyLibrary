package com.projetogs.mylibrary.vcr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import com.projetogs.mylibrary.service.ZipCodeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.web.client.RestTemplate;

import com.projetogs.mylibrary.dto.ZipCodeResponseDTO;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class ZipCodeServiceTest {

    private MockWebServer mockWebServer;
    private ZipCodeService zipCodeService;

    private static final String VIACEP_RESPONSE_VALIDO = """
            {
                "cep": "01310-100",
                "logradouro": "Avenida Paulista",
                "complemento": "de 1 a 610 - lado par",
                "bairro": "Bela Vista",
                "localidade": "São Paulo",
                "uf": "SP"
            }
            """;

    private static final String VIACEP_RESPONSE_CEP_INEXISTENTE = """
            {
                "erro": true
            }
            """;

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
    }

    @Test
    @DisplayName("VCR - Deve retornar dados do CEP válido gravados da API ViaCEP")
    void deveRetornarDadosParaCepValido() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(VIACEP_RESPONSE_VALIDO));

        ZipCodeResponseDTO response = zipCodeService.fetchZipCode("01310100");

        assertNotNull(response);
        assertEquals("01310-100", response.getZipCode());
        assertEquals("Avenida Paulista", response.getStreet());
        assertEquals("Bela Vista", response.getNeighborhood());
        assertEquals("São Paulo", response.getCity());
        assertEquals("SP", response.getState());

        RecordedRequest request = mockWebServer.takeRequest();
        assertTrue(request.getPath().contains("01310100"),
                "A URL da requisição deve conter o CEP sem hífen");
    }

    @Test
    @DisplayName("VCR - Deve aceitar CEP formatado com hífen e normalizar antes da chamada")
    void deveAceitarCepFormatadoComHifen() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(VIACEP_RESPONSE_VALIDO));

        ZipCodeResponseDTO response = zipCodeService.fetchZipCode("01310-100");

        assertNotNull(response);
        assertEquals("01310-100", response.getZipCode());

        RecordedRequest request = mockWebServer.takeRequest();
        assertTrue(request.getPath().contains("01310100"),
                "O CEP deve ser enviado sem hífen para a API");
    }

    @Test
    @DisplayName("VCR - Deve lançar exceção para CEP não encontrado na API")
    void deveLancarExcecaoParaCepNaoEncontrado() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(VIACEP_RESPONSE_CEP_INEXISTENTE));

        assertThrows(IllegalArgumentException.class, () ->
                        zipCodeService.fetchZipCode("00000000"),
                "Deve lançar exceção para CEP inexistente");
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "1234567", "123456789", "abcdefgh" })
    @DisplayName("VCR - Deve lançar exceção para CEP com quantidade de dígitos inválida")
    void deveLancarExcecaoParaCepComFormatoInvalido(String cepInvalido) {
        assertThrows(IllegalArgumentException.class, () ->
                        zipCodeService.fetchZipCode(cepInvalido),
                "Deve lançar exceção para CEP inválido: " + cepInvalido);
    }

    @Test
    @DisplayName("VCR - Deve lançar RuntimeException quando a API retorna erro HTTP 503")
    void deveLancarExcecaoQuandoApiRetornaErroHttp() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(503));

        assertThrows(RuntimeException.class, () ->
                        zipCodeService.fetchZipCode("01310100"),
                "Deve lançar RuntimeException para erro HTTP da API");
    }

    @Test
    @DisplayName("VCR - Deve confirmar que exatamente uma requisição foi feita para a API")
    void deveConfirmarUmaRequisicaoFeita() throws InterruptedException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(VIACEP_RESPONSE_VALIDO));

        zipCodeService.fetchZipCode("01310100");

        assertEquals(1, mockWebServer.getRequestCount(),
                "Deve ter feito exatamente 1 requisição para a API");
    }
}
