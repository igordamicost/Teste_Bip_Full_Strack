package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Transfere valor entre dois benefícios com validação de saldo, locking pessimista
     * e rollback em caso de falha (transação EJB).
     */
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }
        if (fromId != null && fromId.equals(toId)) {
            throw new IllegalArgumentException("Origem e destino não podem ser iguais");
        }

        Beneficio from = em.find(Beneficio.class, fromId, LockModeType.PESSIMISTIC_WRITE);
        Beneficio to = em.find(Beneficio.class, toId, LockModeType.PESSIMISTIC_WRITE);

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

        em.merge(from);
        em.merge(to);
    }
}
