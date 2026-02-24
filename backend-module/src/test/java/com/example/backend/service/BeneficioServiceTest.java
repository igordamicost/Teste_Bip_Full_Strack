package com.example.backend.service;

import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficioServiceTest {

    @Mock
    private BeneficioRepository repository;

    @InjectMocks
    private BeneficioService service;

    private Beneficio beneficioA;
    private Beneficio beneficioB;

    @BeforeEach
    void setUp() {
        beneficioA = new Beneficio();
        beneficioA.setId(1L);
        beneficioA.setNome("Beneficio A");
        beneficioA.setValor(new BigDecimal("1000.00"));
        beneficioA.setAtivo(true);

        beneficioB = new Beneficio();
        beneficioB.setId(2L);
        beneficioB.setNome("Beneficio B");
        beneficioB.setValor(new BigDecimal("500.00"));
        beneficioB.setAtivo(true);
    }

    @Test
    @DisplayName("listar retorna apenas ativos ordenados por nome")
    void listar() {
        when(repository.findByAtivoTrueOrderByNomeAsc()).thenReturn(List.of(beneficioA, beneficioB));
        List<Beneficio> result = service.listar();
        assertThat(result).hasSize(2).extracting(Beneficio::getNome).containsExactly("Beneficio A", "Beneficio B");
    }

    @Test
    @DisplayName("buscarPorId lança quando não existe")
    void buscarPorIdNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("buscarPorId retorna benefício")
    void buscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(beneficioA));
        Beneficio result = service.buscarPorId(1L);
        assertThat(result.getNome()).isEqualTo("Beneficio A");
    }

    @Test
    @DisplayName("criar com valor negativo lança")
    void criarValorNegativo() {
        Beneficio invalido = new Beneficio();
        invalido.setNome("X");
        invalido.setValor(new BigDecimal("-1"));
        assertThatThrownBy(() -> service.criar(invalido))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor");
    }

    @Test
    @DisplayName("criar salva e retorna")
    void criar() {
        when(repository.save(any(Beneficio.class))).thenAnswer(i -> i.getArgument(0));
        Beneficio novo = new Beneficio();
        novo.setNome("Novo");
        novo.setValor(BigDecimal.TEN);
        Beneficio saved = service.criar(novo);
        assertThat(saved.getNome()).isEqualTo("Novo");
        verify(repository).save(any(Beneficio.class));
    }

    @Test
    @DisplayName("transferir com mesmo origem e destino lança")
    void transferirMesmoId() {
        assertThatThrownBy(() -> service.transferir(1L, 1L, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("iguais");
    }

    @Test
    @DisplayName("transferir com valor zero lança")
    void transferirValorZero() {
        assertThatThrownBy(() -> service.transferir(1L, 2L, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("transferir com saldo insuficiente lança")
    void transferirSaldoInsuficiente() {
        when(repository.findByIdForUpdate(1L)).thenReturn(beneficioA);
        when(repository.findByIdForUpdate(2L)).thenReturn(beneficioB);
        assertThatThrownBy(() -> service.transferir(1L, 2L, new BigDecimal("2000")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    @DisplayName("transferir debita e credita corretamente")
    void transferirOk() {
        when(repository.findByIdForUpdate(1L)).thenReturn(beneficioA);
        when(repository.findByIdForUpdate(2L)).thenReturn(beneficioB);
        when(repository.save(any(Beneficio.class))).thenAnswer(i -> i.getArgument(0));

        service.transferir(1L, 2L, new BigDecimal("300"));

        assertThat(beneficioA.getValor()).isEqualByComparingTo("700.00");
        assertThat(beneficioB.getValor()).isEqualByComparingTo("800.00");
        verify(repository, times(2)).save(any(Beneficio.class));
    }
}
