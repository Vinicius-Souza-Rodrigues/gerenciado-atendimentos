-- Tabela de Prestadores
CREATE TABLE IF NOT EXISTS prestador (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome_negocio VARCHAR(255) NOT NULL,
    telefone_whatsapp VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Clientes
CREATE TABLE IF NOT EXISTS cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Agendamentos
CREATE TABLE IF NOT EXISTS agendamento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prestador_id UUID NOT NULL REFERENCES prestador(id) ON DELETE CASCADE,
    cliente_id UUID NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    data_hora TIMESTAMP NOT NULL,
    servico VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDENTE', 'CONFIRMADO', 'CANCELADO', 'CONCLUIDO')),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para melhor performance
CREATE INDEX IF NOT EXISTS idx_agendamento_prestador_data ON agendamento(prestador_id, data_hora);
CREATE INDEX IF NOT EXISTS idx_agendamento_cliente ON agendamento(cliente_id);
CREATE INDEX IF NOT EXISTS idx_cliente_telefone ON cliente(telefone);
