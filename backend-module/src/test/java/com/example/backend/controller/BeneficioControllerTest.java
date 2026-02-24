package com.example.backend.controller;

import com.example.backend.dto.BeneficioDto;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.entity.Beneficio;
import com.example.backend.security.JwtService;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficioController.class)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioService service;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/v1/beneficios retorna lista")
    @WithMockUser
    void listar() throws Exception {
        Beneficio b = new Beneficio();
        b.setId(1L);
        b.setNome("A");
        b.setValor(BigDecimal.TEN);
        when(service.listar()).thenReturn(List.of(b));

        mvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("A"))
                .andExpect(jsonPath("$[0].valor").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/beneficios sem auth retorna 401")
    void listarUnauthorized() throws Exception {
        mvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/beneficios cria e retorna 201")
    @WithMockUser
    void criar() throws Exception {
        BeneficioDto dto = new BeneficioDto(null, "Novo", "Desc", BigDecimal.ONE, true, null);
        Beneficio saved = new Beneficio();
        saved.setId(1L);
        saved.setNome("Novo");
        saved.setValor(BigDecimal.ONE);
        when(service.criar(any(Beneficio.class))).thenReturn(saved);

        mvc.perform(post("/api/v1/beneficios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo"));
    }

    @Test
    @DisplayName("POST /api/v1/beneficios/transfer retorna 204")
    @WithMockUser
    void transferir() throws Exception {
        TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("100"));
        doNothing().when(service).transferir(1L, 2L, new BigDecimal("100"));

        mvc.perform(post("/api/v1/beneficios/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNoContent());
        verify(service).transferir(1L, 2L, new BigDecimal("100"));
    }
}
