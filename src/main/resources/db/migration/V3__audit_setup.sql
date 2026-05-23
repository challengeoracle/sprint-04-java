CREATE TABLE TB_AUDITORIA (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tabela_nome VARCHAR2(30),
    operacao VARCHAR2(10),
    usuario_db VARCHAR2(100),
    data_evento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dados_antigos VARCHAR2(4000),
    dados_novos VARCHAR2(4000)
);

CREATE OR REPLACE TRIGGER TRG_AUDIT_USUARIO
AFTER INSERT OR UPDATE OR DELETE ON TB_USUARIO
FOR EACH ROW
DECLARE
    v_op VARCHAR2(10);
BEGIN
    IF INSERTING THEN v_op := 'INSERT';
    ELSIF UPDATING THEN v_op := 'UPDATE';
    ELSE v_op := 'DELETE';
    END IF;

    INSERT INTO TB_AUDITORIA (tabela_nome, operacao, usuario_db, dados_antigos, dados_novos)
    VALUES (
        'TB_USUARIO',
        v_op,
        USER,
        CASE WHEN v_op IN ('UPDATE', 'DELETE') THEN 'ID: ' || :OLD.id || ' | Email: ' || :OLD.email ELSE NULL END,
        CASE WHEN v_op IN ('INSERT', 'UPDATE') THEN 'ID: ' || :NEW.id || ' | Email: ' || :NEW.email ELSE NULL END
    );
END;
/

CREATE OR REPLACE TRIGGER TRG_AUDIT_UNIDADE
AFTER INSERT OR UPDATE OR DELETE ON TB_UNIDADE_SAUDE
FOR EACH ROW
DECLARE
    v_op VARCHAR2(10);
BEGIN
    IF INSERTING THEN v_op := 'INSERT';
    ELSIF UPDATING THEN v_op := 'UPDATE';
    ELSE v_op := 'DELETE';
    END IF;

    INSERT INTO TB_AUDITORIA (tabela_nome, operacao, usuario_db, dados_antigos, dados_novos)
    VALUES (
        'TB_UNIDADE_SAUDE',
        v_op,
        USER,
        :OLD.nome,
        :NEW.nome
    );
END;
/

COMMIT;