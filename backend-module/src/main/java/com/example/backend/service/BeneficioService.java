package com.example.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.Beneficio;
import com.example.backend.repository.BeneficioRepository;

@Service
public class BeneficioService {

    private final BeneficioRepository repository;

    public BeneficioService(BeneficioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Beneficio> listar() {
        return repository.findByAtivoTrueOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Beneficio> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Beneficio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado: " + id));
    }

    @Transactional
    public Beneficio criar(Beneficio beneficio) {
        if (beneficio.getValor() == null || beneficio.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor deve ser maior ou igual a zero");
        }
        return repository.save(beneficio);
    }

    @Transactional
    public Beneficio atualizar(Long id, Beneficio dados) {
        Beneficio existente = buscarPorId(id);
        existente.setNome(dados.getNome());
        existente.setDescricao(dados.getDescricao());
        existente.setValor(dados.getValor() != null ? dados.getValor() : existente.getValor());
        existente.setAtivo(dados.isAtivo());
        return repository.save(existente);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Benefício não encontrado: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void transferir(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Origem e destino não podem ser iguais");
        }

        Beneficio from = repository.findByIdForUpdate(fromId);
        Beneficio to = repository.findByIdForUpdate(toId);

        if (from == null) {
            throw new IllegalArgumentException("Benefício de origem não encontrado: " + fromId);
        }
        if (to == null) {
            throw new IllegalArgumentException("Benefício de destino não encontrado: " + toId);
        }
        if (!from.isAtivo() || !to.isAtivo()) {
            throw new IllegalStateException("Benefícios devem estar ativos para transferência");
        }

        BigDecimal saldoOrigem = from.getValor();
        if (saldoOrigem.compareTo(amount) < 0) {
            throw new IllegalStateException(
                    "Saldo insuficiente. Disponível: " + saldoOrigem + ", solicitado: " + amount);
        }

        from.setValor(saldoOrigem.subtract(amount));
        to.setValor(to.getValor().add(amount));

        repository.save(from);
        repository.save(to);
    }
}
