INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (1, 'admin1@medix.com', '123456', 'ADMIN');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (2, 'admin2@medix.com', '123456', 'ADMIN');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (3, 'admin3@medix.com', '123456', 'ADMIN');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (4, 'admin4@medix.com', '123456', 'ADMIN');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (5, 'admin5@medix.com', '123456', 'ADMIN');

INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (6, 'medico1@medix.com', '123456', 'COLABORADOR');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (7, 'medico2@medix.com', '123456', 'COLABORADOR');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (8, 'recepcao1@medix.com', '123456', 'COLABORADOR');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (9, 'medico3@medix.com', '123456', 'COLABORADOR');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (10, 'recepcao2@medix.com', '123456', 'COLABORADOR');

INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (11, 'paciente1@gmail.com', '123456', 'PACIENTE');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (12, 'paciente2@gmail.com', '123456', 'PACIENTE');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (13, 'paciente3@gmail.com', '123456', 'PACIENTE');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (14, 'paciente4@gmail.com', '123456', 'PACIENTE');
INSERT INTO TB_USUARIO (id, email, senha, role) VALUES (15, 'paciente5@gmail.com', '123456', 'PACIENTE');

INSERT INTO TB_ADMIN (usuario_id) VALUES (1);
INSERT INTO TB_ADMIN (usuario_id) VALUES (2);
INSERT INTO TB_ADMIN (usuario_id) VALUES (3);
INSERT INTO TB_ADMIN (usuario_id) VALUES (4);
INSERT INTO TB_ADMIN (usuario_id) VALUES (5);

INSERT INTO TB_COLABORADOR (usuario_id, nome, cpf, tipo_colaborador) VALUES (6, 'Dr. Mateus Lima', '11122233344', 'OPERACIONAL');
INSERT INTO TB_COLABORADOR (usuario_id, nome, cpf, tipo_colaborador) VALUES (7, 'Dra. Ana Costa', '22233344455', 'OPERACIONAL');
INSERT INTO TB_COLABORADOR (usuario_id, nome, cpf, tipo_colaborador) VALUES (8, 'Carlos Souza', '33344455566', 'ADMINISTRATIVO');
INSERT INTO TB_COLABORADOR (usuario_id, nome, cpf, tipo_colaborador) VALUES (9, 'Dr. Ricardo Oliveira', '44455566677', 'OPERACIONAL');
INSERT INTO TB_COLABORADOR (usuario_id, nome, cpf, tipo_colaborador) VALUES (10, 'Mariana Santos', '55566677788', 'ADMINISTRATIVO');

INSERT INTO TB_PACIENTE (usuario_id, nome, cpf, tipo_sanguineo, altura) VALUES (11, 'Joao Silva', '12312312311', 'O_POSITIVO', 1.75);
INSERT INTO TB_PACIENTE (usuario_id, nome, cpf, tipo_sanguineo, altura) VALUES (12, 'Maria Oliveira', '23423423422', 'A_NEGATIVO', 1.62);
INSERT INTO TB_PACIENTE (usuario_id, nome, cpf, tipo_sanguineo, altura) VALUES (13, 'Pedro Rocha', '34534534533', 'B_POSITIVO', 1.80);
INSERT INTO TB_PACIENTE (usuario_id, nome, cpf, tipo_sanguineo, altura) VALUES (14, 'Juliana Lima', '45645645644', 'AB_POSITIVO', 1.68);
INSERT INTO TB_PACIENTE (usuario_id, nome, cpf, tipo_sanguineo, altura) VALUES (15, 'Lucas Mendes', '56756756755', 'O_NEGATIVO', 1.72);

INSERT INTO TB_PACIENTE_ALERGIAS (paciente_id, alergia) VALUES (11, 'Dipirona');
INSERT INTO TB_PACIENTE_ALERGIAS (paciente_id, alergia) VALUES (11, 'Poeira');
INSERT INTO TB_PACIENTE_ALERGIAS (paciente_id, alergia) VALUES (12, 'Lactose');
INSERT INTO TB_PACIENTE_ALERGIAS (paciente_id, alergia) VALUES (13, 'Amendoim');
INSERT INTO TB_PACIENTE_ALERGIAS (paciente_id, alergia) VALUES (14, 'Glúten');

INSERT INTO TB_UNIDADE_SAUDE (nome, endereco) VALUES ('Unidade Paulista', 'Av. Paulista, 1106');
INSERT INTO TB_UNIDADE_SAUDE (nome, endereco) VALUES ('Unidade Moema', 'Rua Inhambu, 500');
INSERT INTO TB_UNIDADE_SAUDE (nome, endereco) VALUES ('Unidade Centro', 'Rua Direita, 10');
INSERT INTO TB_UNIDADE_SAUDE (nome, endereco) VALUES ('Unidade Sul', 'Av. Interlagos, 2000');
INSERT INTO TB_UNIDADE_SAUDE (nome, endereco) VALUES ('Unidade Norte', 'Av. Brás Leme, 300');

INSERT INTO TB_SALA (numero, nome, disponibilidade, unidade_id) VALUES ('101', 'Consultório 1', '08:00 - 18:00', 1);
INSERT INTO TB_SALA (numero, nome, disponibilidade, unidade_id) VALUES ('102', 'Consultório 2', '08:00 - 18:00', 1);
INSERT INTO TB_SALA (numero, nome, disponibilidade, unidade_id) VALUES ('201', 'Sala de Exames A', '07:00 - 19:00', 2);
INSERT INTO TB_SALA (numero, nome, disponibilidade, unidade_id) VALUES ('202', 'Sala de Exames B', '07:00 - 19:00', 2);
INSERT INTO TB_SALA (numero, nome, disponibilidade, unidade_id) VALUES ('301', 'Triagem Geral', '24 Horas', 3);

INSERT INTO TB_AGENDAMENTO (paciente_id, medico_id, unidade_id, sala_id, data_hora_inicio, data_hora_fim, tipo, especialidade) VALUES (11, 6, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1' HOUR, 'CONSULTA', 'Cardiologia');
INSERT INTO TB_AGENDAMENTO (paciente_id, medico_id, unidade_id, sala_id, data_hora_inicio, data_hora_fim, tipo, especialidade) VALUES (12, 7, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '30' MINUTE, 'CONSULTA', 'Clínica Geral');
INSERT INTO TB_AGENDAMENTO (paciente_id, medico_id, unidade_id, sala_id, data_hora_inicio, data_hora_fim, tipo, especialidade) VALUES (13, 9, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '45' MINUTE, 'EXAME', 'Pediatria');
INSERT INTO TB_AGENDAMENTO (paciente_id, medico_id, unidade_id, sala_id, data_hora_inicio, data_hora_fim, tipo, especialidade) VALUES (14, 6, 1, 1, CURRENT_TIMESTAMP + INTERVAL '2' HOUR, CURRENT_TIMESTAMP + INTERVAL '3' HOUR, 'CONSULTA', 'Cardiologia');
INSERT INTO TB_AGENDAMENTO (paciente_id, medico_id, unidade_id, sala_id, data_hora_inicio, data_hora_fim, tipo, especialidade) VALUES (15, 7, 3, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '1' HOUR, 'CONSULTA', 'Geral');

-- Ajuste final da sequence para evitar erros em novas inserções automáticas
ALTER TABLE TB_USUARIO MODIFY (id GENERATED BY DEFAULT AS IDENTITY (START WITH 16));

COMMIT;