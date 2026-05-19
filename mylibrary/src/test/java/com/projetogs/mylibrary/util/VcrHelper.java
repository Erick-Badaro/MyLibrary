package com.projetogs.mylibrary.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * VCR-like utility for recording and playing back HTTP interactions.
 * This mimics the Ruby VCR gem behavior for Java tests.
 */
public class VcrHelper {

    private MockWebServer mockWebServer;
    private final String cassettePath;
    private final String cassetteName;
    private final boolean recordMode;
    private final ObjectMapper objectMapper;
    private String lastRecordedBody = ""; // Guarda o corpo real capturado na gravação
    private int lastRecordedStatus = 200; // Guarda o status real capturado

    public VcrHelper(String cassettePath, String cassetteName, boolean recordMode) {
        this.cassettePath = cassettePath;
        this.cassetteName = cassetteName;
        this.recordMode = recordMode;
        this.mockWebServer = new MockWebServer();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Start the VCR helper and either load recorded responses or prepare for recording
     */
    public void start() throws IOException {
        mockWebServer.start();
        
        if (!recordMode) {
            // Playback mode: load recorded responses from cassette
            loadCassette();
        }
    }

    /**
     * Stop the VCR helper and save recordings if in record mode
     */
    public void stop() throws IOException {
        if (recordMode) {
            // IMPLEMENTADO: Agora salva as interações reais capturadas
            saveCassette();
        }
        mockWebServer.shutdown();
    }

    /**
     * IMPLEMENTADO AUXILIAR: Método para você usar no teste quando estiver gravando (recordMode = true).
     * Ele faz a requisição real na internet e prepara o MockWebServer com o dado real.
     */
    public void recordFromUrl(String realUrl) throws IOException {
        if (!recordMode) return;
        
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder().url(realUrl).build();
        try (Response response = client.newCall(request).execute()) {
            this.lastRecordedStatus = response.code();
            this.lastRecordedBody = response.body() != null ? response.body().string() : "";
            
            // Coloca na fila do MockWebServer para o seu Service consumir durante o teste
            enqueueJsonResponse(lastRecordedStatus, lastRecordedBody);
        }
    }

    /**
     * Get the URL to use for API calls (points to mock server)
     */
    public String getUrl(String path) {
        return mockWebServer.url(path).toString();
    }

    /**
     * Get an OkHttpClient configured to use the mock server
     */
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Load recorded responses from cassette file
     */
    @SuppressWarnings("unused")
    private void loadCassette() throws IOException {
        Path cassetteFile = Paths.get(cassettePath, cassetteName + ".json");
        if (Files.exists(cassetteFile)) {
            String cassetteContent = Files.readString(cassetteFile);
            // Parse cassette and enqueue responses
            enqueueResponseFromCassette(cassetteContent);
        } else {
            throw new IOException("Cassette file not found: " + cassetteFile);
        }
    }

    /**
     * Save recorded interactions to cassette file
     */
    private void saveCassette() throws IOException {
    Path cassetteDir = Paths.get(cassettePath);
    if (!Files.exists(cassetteDir)) {
        Files.createDirectories(cassetteDir);
    }
    Path cassetteFile = cassetteDir.resolve(cassetteName + ".json");
    
    // Se temos o conteúdo armazenado em memória, grava direto!
    if (this.lastRecordedBody != null && !this.lastRecordedBody.isEmpty()) {
        Files.writeString(cassetteFile, generateCassetteContent());
    }
}

    /**
     * Enqueue a mock response for testing
     */
    public void enqueueResponse(int statusCode, String body, String contentType) {
        MockResponse response = new MockResponse()
                .setResponseCode(statusCode)
                .setHeader("Content-Type", contentType)
                .setBody(body);
        mockWebServer.enqueue(response);
    }

    /**
     * Enqueue a JSON response
     */
    public void enqueueJsonResponse(int statusCode, String jsonBody) {
        enqueueResponse(statusCode, jsonBody, "application/json");
    }

    /**
     * Take a recorded request from the mock server
     */
    public RecordedRequest takeRequest() throws InterruptedException {
        return mockWebServer.takeRequest(5, TimeUnit.SECONDS);
    }

    private void enqueueResponseFromCassette(String cassetteContent) {
        try {
            // Parse the cassette JSON
            JsonNode cassette = objectMapper.readTree(cassetteContent);
            
            // Extract the response body from the first interaction
            JsonNode interactions = cassette.get("http_interactions");
            if (interactions != null && interactions.isArray() && interactions.size() > 0) {
                JsonNode firstInteraction = interactions.get(0);
                JsonNode response = firstInteraction.get("response");
                
                if (response != null) {
                    int statusCode = response.get("status").get("code").asInt();
                    
                    // CORREÇÃO DE SEGURANÇA: Se o body for um objeto JSON no arquivo, lê como String correta
                    JsonNode bodyNode = response.get("body");
                    String body = bodyNode.isContainerNode() ? bodyNode.toString() : bodyNode.asText();
                    
                    String contentType = "application/json";
                    
                    if (response.get("headers") != null && response.get("headers").get("Content-Type") != null) {
                        contentType = response.get("headers").get("Content-Type").asText();
                    }
                    
                    // Enqueue the extracted response
                    mockWebServer.enqueue(new MockResponse()
                            .setResponseCode(statusCode)
                            .setHeader("Content-Type", contentType)
                            .setBody(body));
                } else {
                    // Fallback: use entire cassette as body
                    mockWebServer.enqueue(new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(cassetteContent));
                }
            }
        } catch (Exception e) {
            // If parsing fails, enqueue the cassette content as-is
            mockWebServer.enqueue(new MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(cassetteContent));
        }
    }

    // IMPLEMENTADO: Monta dinamicamente a estrutura exata de árvore JSON esperada pelo loadCassette
    private String generateCassetteContent() {
        try {
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode interactions = objectMapper.createArrayNode();
            ObjectNode interaction = objectMapper.createObjectNode();
            ObjectNode responseNode = objectMapper.createObjectNode();
            
            // Configura o status
            ObjectNode statusNode = objectMapper.createObjectNode();
            statusNode.put("code", this.lastRecordedStatus);
            responseNode.set("status", statusNode);
            
            // Configura os headers
            ObjectNode headersNode = objectMapper.createObjectNode();
            headersNode.put("Content-Type", "application/json");
            responseNode.set("headers", headersNode);
            
            // Configura o body como um nó estruturado em vez de String pura para evitar quebras de escape
            try {
                JsonNode jsonBodyNode = objectMapper.readTree(this.lastRecordedBody);
                responseNode.set("body", jsonBodyNode);
            } catch (Exception e) {
                responseNode.put("body", this.lastRecordedBody);
            }
            
            interaction.set("response", responseNode);
            interactions.add(interaction);
            rootNode.set("http_interactions", interactions);
            
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (Exception e) {
            return "{\"http_interactions\":[]}";
        }
    }

    /**
     * Get the mock web server for advanced configurations
     */
    public MockWebServer getMockWebServer() {
        return mockWebServer;
    }
}