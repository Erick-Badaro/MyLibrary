package com.projetogs.mylibrary.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.projetogs.mylibrary.dto.ZipCodeResponseDTO;

@Service
public class ZipCodeService {

    private final RestTemplate restTemplate;
    private static final String VIACEP_URL = "https://viacep.com.br/ws/";

    public ZipCodeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ZipCodeResponseDTO fetchZipCode(String zipCode) {
        try {
            String cleanZipCode = zipCode.replaceAll("[^0-9]", "");

            if (cleanZipCode.length() != 8) {
                throw new IllegalArgumentException("CEP deve conter 8 dígitos");
            }

            String url = VIACEP_URL + cleanZipCode + "/json/";
            ZipCodeResponseDTO response = restTemplate.getForObject(url, ZipCodeResponseDTO.class);

            if (response != null && response.getZipCode() != null && !response.getZipCode().isEmpty()) {
                return response;
            } else {
                throw new IllegalArgumentException("CEP não encontrado");
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Erro ao consultar API de CEP: " + e.getMessage());
        }
    }
}
