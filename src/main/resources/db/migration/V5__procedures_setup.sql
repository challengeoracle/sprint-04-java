-- PROCEDURE 1: Junção Complexa + Saída JSON
CREATE OR REPLACE PROCEDURE SP_GET_HISTORICO_JSON(
    p_paciente_id IN NUMBER,
    p_saida OUT VARCHAR2 -- Parâmetro para o Java ler
) AS
    v_nome VARCHAR2(100);
BEGIN
    SELECT p.nome INTO v_nome FROM TB_PACIENTE p WHERE p.usuario_id = p_paciente_id;

    p_saida := '{"paciente": "' || v_nome || '", "auth": ' || FN_CONVERTE_USUARIO_JSON(p_paciente_id) || '}';
    DBMS_OUTPUT.PUT_LINE(p_saida);
EXCEPTION
    WHEN NO_DATA_FOUND THEN p_saida := '{"erro": "Inexistente"}';
    WHEN OTHERS THEN p_saida := '{"erro": "Erro: ' || SQLERRM || '"}';
END;
/

-- PROCEDURE 2: Relatório Analítico (LAG/LEAD)
CREATE OR REPLACE PROCEDURE SP_RELATORIO_NAVEGACAO(p_saida OUT VARCHAR2) AS
    v_acumulado VARCHAR2(4000) := 'ID_AGE | PAC | ANT | ATU | PROX' || CHR(10);
BEGIN
    FOR r IN (
        SELECT id, paciente_id,
               NVL(TO_CHAR(LAG(id) OVER (ORDER BY data_hora_inicio)), 'Vazio') as anterior,
               TO_CHAR(id) as atual,
               NVL(TO_CHAR(LEAD(id) OVER (ORDER BY data_hora_inicio)), 'Vazio') as proximo
        FROM TB_AGENDAMENTO
    ) LOOP
        v_acumulado := v_acumulado || r.id || ' | ' || r.paciente_id || ' | ' || r.anterior || ' | ' || r.atual || ' | ' || r.proximo || CHR(10);
    END LOOP;

    p_saida := v_acumulado;
    DBMS_OUTPUT.PUT_LINE(p_saida);
EXCEPTION
    WHEN OTHERS THEN p_saida := 'Erro ao gerar relatório: ' || SQLERRM;
END;
/