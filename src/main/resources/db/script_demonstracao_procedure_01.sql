-- Forçar um agendamento para o passado para testar a limpeza (Procedure 1)
UPDATE TB_AGENDAMENTO
SET data_hora_inicio = TIMESTAMP '2026-01-01 10:00:00',
    data_hora_fim = TIMESTAMP '2026-01-01 10:30:00',
    status = 'AGENDADO'
WHERE id = 1;

COMMIT;

SELECT status FROM TB_AGENDAMENTO WHERE id = 1;