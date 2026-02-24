package com.example.backend.integration;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BeneficioApiIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BeneficioRepository repository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        repository.deleteAll();
        token = obtainToken();
    }

    private String obtainToken() throws Exception {
        ResultActions result = mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("admin", "admin123"))))
                .andExpect(status().isOk());
        String body = result.andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @DisplayName("E2E: criar benefícios, listar, transferir e verificar saldos")
    void e2eCriarListarTransferir() throws Exception {
        BeneficioDto dtoA = new BeneficioDto(null, "Beneficio A", "Desc A", new BigDecimal("1000.00"), true, null);
        BeneficioDto dtoB = new BeneficioDto(null, "Beneficio B", "Desc B", new BigDecimal("500.00"), true, null);

        String jsonA = mvc.perform(post("/api/v1/beneficios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoA)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mvc.perform(post("/api/v1/beneficios")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoB)))
                .andExpect(status().isCreated());

        mvc.perform(get("/api/v1/beneficios").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        Long idA = objectMapper.readTree(jsonA).get("id").asLong();
        Long idB = repository.findAll().stream().filter(b -> !b.getId().equals(idA)).findFirst().orElseThrow().getId();

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferenciaRequest(idA, idB, new BigDecimal("300")))))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/beneficios/" + idA).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(700.0));
        mvc.perform(get("/api/v1/beneficios/" + idB).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valor").value(800.0));
    }

    @Test
    @DisplayName("E2E: transferência com saldo insuficiente retorna 409")
    void e2eTransferenciaSaldoInsuficiente() throws Exception {
        Beneficio a = new Beneficio();
        a.setNome("A");
        a.setValor(new BigDecimal("100"));
        a.setAtivo(true);
        a = repository.save(a);
        Beneficio b = new Beneficio();
        b.setNome("B");
        b.setValor(BigDecimal.ZERO);
        b.setAtivo(true);
        b = repository.save(b);

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new TransferenciaRequest(a.getId(), b.getId(), new BigDecimal("200")))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value(containsString("Saldo insuficiente")));
    }
}
