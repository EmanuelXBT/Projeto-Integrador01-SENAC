-- ============================================
-- QAwler - Projeto Integrador I (UC05) - Atividade 03
-- Script de criação, povoamento, consulta, edição e exclusão
-- MySQL 8 | InnoDB | utf8mb4
-- Responsável: Emanuel Filipe da Silva
-- ============================================

-- ============================================
-- 1. CRIAÇÃO DA BASE DE DADOS
-- ============================================
CREATE DATABASE IF NOT EXISTS qawler
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE qawler;

-- Limpeza para permitir reexecução segura do script
-- (ordem: tabela filha primeiro, tabela pai por último)
DROP TABLE IF EXISTS tb_resultado;
DROP TABLE IF EXISTS tb_varredura;
DROP TABLE IF EXISTS tb_site;
DROP TABLE IF EXISTS tb_usuario;

-- ============================================
-- 2. CRIAÇÃO DAS TABELAS (conforme Diagrama ER)
-- ============================================

-- 2.1 tb_usuario
--     tipo: ADMINISTRADOR | ANALISTA | VISUALIZADOR
CREATE TABLE tb_usuario (
  id     INT AUTO_INCREMENT,
  nome   VARCHAR(100) NOT NULL,
  login  VARCHAR(50)  NOT NULL,
  senha  VARCHAR(255) NOT NULL,
  tipo   ENUM('ADMINISTRADOR', 'ANALISTA', 'VISUALIZADOR') NOT NULL,
  CONSTRAINT pk_usuario PRIMARY KEY (id),
  CONSTRAINT uk_usuario_login UNIQUE (login)
) ENGINE = InnoDB;

-- 2.2 tb_site
--     Sites alvo cadastrados para varredura (RF001).
CREATE TABLE tb_site (
  id            INT AUTO_INCREMENT,
  nome          VARCHAR(100)  NOT NULL,
  url           VARCHAR(255)  NOT NULL,
  data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_site PRIMARY KEY (id),
  CONSTRAINT uk_site_url UNIQUE (url)
) ENGINE = InnoDB;

-- 2.3 tb_varredura
--     Execuções de varredura (RF002, RF003, RF004, RF005, RF008, RF010).
--     status: EM_ANDAMENTO | CONCLUIDA | CANCELADA | FALHOU
--     duracao: tempo total em segundos.
CREATE TABLE tb_varredura (
  id         INT AUTO_INCREMENT,
  id_site    INT NOT NULL,
  id_usuario INT NOT NULL,
  data_hora  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status     VARCHAR(20) NOT NULL,
  duracao    INT         NOT NULL,
  CONSTRAINT pk_varredura PRIMARY KEY (id),
  CONSTRAINT fk_varredura_site
    FOREIGN KEY (id_site) REFERENCES tb_site (id)
    ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT fk_varredura_usuario
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario (id)
    ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- 2.4 tb_resultado
--     Resultados individuais de cada verificação (RF006, RF007).
--     tipo_erro: HTTP_ERROR | LINK_QUEBRADO | HTML_INVALIDO
CREATE TABLE tb_resultado (
  id           INT AUTO_INCREMENT,
  id_varredura INT NOT NULL,
  tipo_erro    VARCHAR(50)  NOT NULL,
  descricao    TEXT         NOT NULL,
  url_afetada  VARCHAR(255) NOT NULL,
  CONSTRAINT pk_resultado PRIMARY KEY (id),
  CONSTRAINT fk_resultado_varredura
    FOREIGN KEY (id_varredura) REFERENCES tb_varredura (id)
    ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB;

-- ============================================
-- 3. INSERÇÃO DE DADOS (pelo menos 5 registros por tabela)
-- ============================================

-- 3.1 tb_usuario (6 registros)
INSERT INTO tb_usuario (nome, login, senha, tipo) VALUES
  ('Emanuel Filipe da Silva', 'emanuel.silva', 'admin123',   'ADMINISTRADOR'),
  ('Mariana Costa',           'mariana.costa', 'analista123', 'ANALISTA'),
  ('João Pedro Almeida',      'joao.almeida',  'visual123',   'VISUALIZADOR'),
  ('Carla Souza',             'carla.souza',   'analista456', 'ANALISTA'),
  ('Rafael Lima',             'rafael.lima',   'visual456',   'VISUALIZADOR'),
  ('Ana Beatriz Rocha',       'ana.rocha',     'admin456',    'ADMINISTRADOR');

-- 3.2 tb_site (6 registros)
INSERT INTO tb_site (nome, url, data_cadastro) VALUES
  ('SENAC EAD',        'https://www.ead.senac.br',   '2026-07-15 09:00:00'),
  ('SENAC Minas',      'https://www.mg.senac.br',    '2026-07-16 10:30:00'),
  ('Portal SENAC',     'https://www.senac.br',       '2026-07-17 14:00:00'),
  ('SENAC São Paulo',  'https://www.sp.senac.br',    '2026-07-18 08:45:00'),
  ('SENAC Rio',        'https://www.rj.senac.br',    '2026-07-19 11:20:00'),
  ('SENAC Bahia',      'https://www.ba.senac.br',    '2026-07-20 16:10:00');

-- 3.3 tb_varredura (6 registros)
INSERT INTO tb_varredura (id_site, id_usuario, data_hora, status, duracao) VALUES
  (1, 1, '2026-07-20 10:15:00', 'CONCLUIDA',   95),
  (2, 2, '2026-07-20 11:00:00', 'CONCLUIDA',  120),
  (3, 1, '2026-07-21 09:30:00', 'EM_ANDAMENTO', 60),
  (4, 3, '2026-07-21 14:45:00', 'CONCLUIDA',   88),
  (5, 4, '2026-07-22 08:20:00', 'FALHOU',      30),
  (6, 5, '2026-07-22 15:05:00', 'CANCELADA',   15);

-- 3.4 tb_resultado (6 registros)
INSERT INTO tb_resultado (id_varredura, tipo_erro, descricao, url_afetada) VALUES
  (1, 'LINK_QUEBRADO', 'Link retornou HTTP 404 na página inicial',                'https://www.ead.senac.br/cursos/inexistente'),
  (1, 'HTTP_ERROR',    'Resposta HTTP 500 ao acessar o portal do aluno',         'https://www.ead.senac.br/aluno'),
  (2, 'HTML_INVALIDO', 'Tag <div> sem fechamento na seção de notícias',           'https://www.mg.senac.br/noticias'),
  (4, 'HTTP_ERROR',    'Resposta HTTP 403 ao acessar área restrita',             'https://www.sp.senac.br/area-restrita'),
  (5, 'LINK_QUEBRADO', 'Link retornou HTTP 404 no rodapé da página',             'https://www.rj.senac.br/contato'),
  (6, 'HTML_INVALIDO', 'Atributo sem aspas em tag <img> no banner institucional', 'https://www.ba.senac.br/institucional');

-- ============================================
-- 4. EXIBIÇÃO DOS DADOS
-- ============================================

-- 4.1 Busca por todos os registros de cada tabela
SELECT * FROM tb_usuario;
SELECT * FROM tb_site;
SELECT * FROM tb_varredura;
SELECT * FROM tb_resultado;

-- 4.2 Busca por registros específicos (cláusula WHERE)
SELECT * FROM tb_usuario   WHERE tipo = 'ADMINISTRADOR';
SELECT * FROM tb_site      WHERE url LIKE '%senac%';
SELECT * FROM tb_varredura WHERE status = 'CONCLUIDA';
SELECT * FROM tb_resultado WHERE tipo_erro = 'LINK_QUEBRADO';

-- ============================================
-- 5. EDIÇÃO DOS DADOS (pelo menos 1 registro por tabela)
-- ============================================

-- 5.1 tb_usuario: promove a analista para administradora
UPDATE tb_usuario
   SET tipo = 'ADMINISTRADOR'
 WHERE id = 2;

-- 5.2 tb_site: corrige o nome cadastrado do site
UPDATE tb_site
   SET nome = 'SENAC Minas Gerais'
 WHERE id = 2;

-- 5.3 tb_varredura: conclui a varredura que estava em andamento
UPDATE tb_varredura
   SET status  = 'CONCLUIDA',
       duracao = 132
 WHERE id = 3;

-- 5.4 tb_resultado: detalha a descrição do erro encontrado
UPDATE tb_resultado
   SET descricao = 'Link retornou HTTP 404 - página não encontrada (recurso removido)'
 WHERE id = 1;

-- ============================================
-- 6. EXCLUSÃO DOS DADOS (pelo menos 1 registro por tabela)
--    Ordem respeitando as chaves estrangeiras:
--    filhas (tb_resultado) antes das pais (tb_varredura, tb_site, tb_usuario).
-- ============================================

-- 6.1 tb_resultado: remove um resultado específico
DELETE FROM tb_resultado WHERE id = 6;

-- 6.2 tb_varredura: remove a varredura cancelada
--     (os resultados vinculados a ela são removidos pelo ON DELETE CASCADE)
DELETE FROM tb_varredura WHERE id = 6;

-- 6.3 tb_site: remove o site sem varreduras vinculadas
DELETE FROM tb_site WHERE id = 6;

-- 6.4 tb_usuario: remove o usuário sem varreduras vinculadas
DELETE FROM tb_usuario WHERE id = 6;

-- ============================================
-- 7. VERIFICAÇÃO FINAL DOS DADOS
-- ============================================

-- 7.1 Confirma as alterações de edição
SELECT * FROM tb_usuario;
SELECT * FROM tb_site;
SELECT * FROM tb_varredura;
SELECT * FROM tb_resultado;

-- 7.2 Consulta de relacionamento entre as tabelas
--     (demonstra as chaves estrangeiras do Diagrama ER)
SELECT v.id        AS id_varredura,
       s.nome      AS site,
       u.nome      AS usuario,
       v.status,
       v.duracao,
       r.tipo_erro,
       r.url_afetada
  FROM tb_varredura v
 INNER JOIN tb_site      s ON s.id = v.id_site
 INNER JOIN tb_usuario   u ON u.id = v.id_usuario
  LEFT JOIN tb_resultado r ON r.id_varredura = v.id
 ORDER BY v.id;
