package com.example.backend.dto;

import com.example.backend.entity.Beneficio;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BeneficioDto(
        Long id,
        @NotNull @Size(min = 1, max = 100) String nome,
        @Size(max = 255) String descricao,
        @NotNull @DecimalMin("0") BigDecimal valor,
        boolean ativo,
        Long version
) {
    public static BeneficioDto from(Beneficio b) {
        return new BeneficioDto(
                b.getId(),
                b.getNome(),
                b.getDescricao(),
                b.getValor(),
                b.isAtivo(),
                b.getVersion()
        );
    }

    public Beneficio toEntity() {
        Beneficio b = new Beneficio();
        b.setId(id);
        b.setNome(nome);
        b.setDescricao(descricao);
        b.setValor(valor);
        b.setAtivo(ativo);
        if (version != null) b.setVersion(version);
        return b;
    }
}
