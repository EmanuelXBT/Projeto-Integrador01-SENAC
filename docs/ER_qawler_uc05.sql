-- ============================================
-- QAwler - Projeto Integrador I (UC05) - Etapa 01
-- Diagrama ER | MySQL 8 | InnoDB | utf8mb4
-- Responsável: Emanuel Filipe da Silva
-- ============================================

CREATE DATABASE IF NOT EXISTS qawler
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE qawler;

-- ============================================
-- 1. tb_usuario
--    Persistência dos usuários do sistema.
--    tipo: ADMINISTRADOR | ANALISTA | VISUALIZADOR
-- ============================================
CREATE TABLE tb_usuario (
  id     INT AUTO_INCREMENT,
  nome   VARCHAR(100) NOT NULL,
  login  VARCHAR(50)  NOT NULL,
  senha  VARCHAR(255) NOT NULL,
  tipo   ENUM('ADMINISTRADOR', 'ANALISTA', 'VISUALIZADOR') NOT NULL,
  CONSTRAINT pk_usuario PRIMARY KEY (id),
  CONSTRAINT uk_usuario_login UNIQUE (login)
) ENGINE = InnoDB;

-- ============================================
-- 2. tb_site
--    Sites alvo cadastrados para varredura (RF001).
-- ============================================
CREATE TABLE tb_site (
  id            INT AUTO_INCREMENT,
  nome          VARCHAR(100)  NOT NULL,
  url           VARCHAR(255)  NOT NULL,
  data_cadastro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_site PRIMARY KEY (id),
  CONSTRAINT uk_site_url UNIQUE (url)
) ENGINE = InnoDB;

-- ============================================
-- 3. tb_varredura
--    Execuções de varredura (RF002, RF003, RF004,
--    RF005, RF008, RF010).
--    status: EM_ANDAMENTO | CONCLUIDA | CANCELADA | FALHOU
--    duracao: tempo total em segundos.
-- ============================================
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

-- ============================================
-- 4. tb_resultado
--    Resultados individuais de cada verificação
--    (RF006, RF007).
--    tipo_erro: HTTP_ERROR | LINK_QUEBRADO | HTML_INVALIDO
-- ============================================
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
