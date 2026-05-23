-- FUNÇÃO 1: Conversão Manual para JSON (Sem built-ins)
CREATE OR REPLACE FUNCTION FN_CONVERTE_USUARIO_JSON(p_id NUMBER) RETURN VARCHAR2 IS
    v_email TB_USUARIO.email%TYPE;
    v_role  TB_USUARIO.role%TYPE;
BEGIN
    SELECT email, role INTO v_email, v_role FROM TB_USUARIO WHERE id = p_id;
    -- Concatenação manual para provar lógica
    RETURN '{"id": ' || p_id || ', "email": "' || v_email || '", "role": "' || v_role || '"}';
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN '{"erro": "Não encontrado"}';
    WHEN VALUE_ERROR THEN RETURN '{"erro": "Erro de conversão"}';
    WHEN OTHERS THEN RETURN '{"erro": "Falha interna"}';
END;
/

-- FUNÇÃO 2: Cálculo Matemático (Duração Ocupada)
CREATE OR REPLACE FUNCTION FN_CALCULA_DURACAO_TOTAL(p_unid NUMBER) RETURN NUMBER IS
    v_total NUMBER;
BEGIN
    SELECT SUM(EXTRACT(HOUR FROM (data_hora_fim - data_hora_inicio)) * 60 +
               EXTRACT(MINUTE FROM (data_hora_fim - data_hora_inicio)))
    INTO v_total FROM TB_AGENDAMENTO WHERE unidade_id = p_unid AND status != 'CANCELADO';
    RETURN NVL(v_total, 0);
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN -1;
    WHEN INVALID_NUMBER THEN RETURN -2;
    WHEN OTHERS THEN RETURN -3;
END;
/