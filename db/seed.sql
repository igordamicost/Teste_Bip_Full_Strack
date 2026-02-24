INSERT INTO BENEFICIO (NOME, DESCRICAO, VALOR, ATIVO) VALUES
('Beneficio A', 'Descrição A', 1000.00, TRUE),
('Beneficio B', 'Descrição B', 500.00, TRUE),
('Beneficio C - Saldo Baixo', 'Usado para testar saldo insuficiente', 50.00, TRUE),
('Beneficio D - Inativo', 'Usado para testar benefício inativo', 1000.00, FALSE),
('Beneficio E - 404', 'Sempre deve resultar em 404', 200.00, TRUE),
('Beneficio F - 403', 'Sempre deve resultar em 403', 300.00, TRUE);
