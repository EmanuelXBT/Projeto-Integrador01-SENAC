# QA Autopilot - DER Completo
## 8 Tabelas | MySQL 8 | InnoDB

usuario 1--N sistema 1--N teste 1--N pagina_visitada
                 1              1
                 N              N
            agendamento       bug 1--N screenshot
usuario 1--N notificacao N--1 teste

---
TABELA: usuario
---
id              BIGINT       PK AUTO_INCREMENT
nome            VARCHAR(100) NOT NULL
email           VARCHAR(150) NOT NULL UNIQUE
senha_hash      VARCHAR(255) NOT NULL (bcrypt)
ativo           BOOLEAN      NOT NULL DEFAULT TRUE
criado_em       DATETIME     NOT NULL DEFAULT NOW()
atualizado_em   DATETIME     ON UPDATE NOW()
INDEX: UNIQUE(email)

---
TABELA: sistema
---
id                   BIGINT       PK AUTO_INCREMENT
usuario_id           BIGINT       FK usuario(id) NOT NULL
nome                 VARCHAR(150) NOT NULL
url_base             VARCHAR(500) NOT NULL
ambiente             ENUM(DEV, STAGING, PROD) NOT NULL
credenciais_login    VARCHAR(255) NULL (AES-256)
credenciais_senha    VARCHAR(255) NULL (AES-256)
dominios_autorizados JSON         NOT NULL
profundidade_crawl   INT          NOT NULL DEFAULT 2
modo_crawler         ENUM(FULL, READ_ONLY) NOT NULL DEFAULT FULL
autorizado_producao  BOOLEAN      NOT NULL DEFAULT FALSE
ativo                BOOLEAN      NOT NULL DEFAULT TRUE
criado_em            DATETIME     NOT NULL DEFAULT NOW()
atualizado_em        DATETIME     ON UPDATE NOW()
INDEX: INDEX(usuario_id)
REGRA: ambiente=PROD + autorizado=FALSE => bloqueia crawl
REGRA: ambiente=PROD => modo_crawler forca READ_ONLY

---
TABELA: teste
---
id                BIGINT  PK AUTO_INCREMENT
sistema_id        BIGINT  FK sistema(id) NOT NULL
status            ENUM(QUEUED, RUNNING, COMPLETED, FAILED) DEFAULT QUEUED
tipo_disparo      ENUM(MANUAL, AGENDADO) NOT NULL
agendamento_id    BIGINT  FK agendamento(id) NULL
paginas_visitadas INT     DEFAULT 0
total_bugs        INT     DEFAULT 0
duracao_ms        INT     NULL
erro_mensagem     TEXT    NULL
inicio_em         DATETIME NULL
fim_em            DATETIME NULL
criado_em         DATETIME NOT NULL DEFAULT NOW()
INDEX: INDEX(sistema_id), INDEX(status), INDEX(criado_em)

---
TABELA: pagina_visitada
---
id                     BIGINT       PK AUTO_INCREMENT
teste_id               BIGINT       FK teste(id) NOT NULL
url                    VARCHAR(500) NOT NULL
http_status            INT          NOT NULL
tempo_carregamento_ms  INT          NOT NULL
titulo                 VARCHAR(255) NULL
visitada_em            DATETIME     NOT NULL
INDEX: INDEX(teste_id), INDEX(url)

---
TABELA: bug
---
id                  BIGINT       PK AUTO_INCREMENT
teste_id            BIGINT       FK teste(id) NOT NULL
pagina_visitada_id  BIGINT       FK pagina_visitada(id) NULL
tipo                ENUM(HTTP_ERROR, JS_ERROR, BROKEN_IMAGE) NOT NULL
severidade          ENUM(CRITICAL, HIGH, MEDIUM, LOW) NOT NULL
url                 VARCHAR(500) NOT NULL
mensagem            TEXT         NOT NULL
linha               INT          NULL
coluna              INT          NULL
detectado_em        DATETIME     NOT NULL
INDEX: INDEX(teste_id), INDEX(tipo), INDEX(severidade)
SEVERIDADE AUTO: HTTP 500=CRITICAL / 4xx=HIGH / JS=MEDIUM / Img=LOW

---
TABELA: screenshot
---
id                  BIGINT       PK AUTO_INCREMENT
bug_id              BIGINT       FK bug(id) NULL
pagina_visitada_id  BIGINT       FK pagina_visitada(id) NULL
caminho_arquivo     VARCHAR(500) NOT NULL
largura             INT          NOT NULL
altura              INT          NOT NULL
criado_em           DATETIME     NOT NULL DEFAULT NOW()
INDEX: INDEX(bug_id), INDEX(pagina_visitada_id)
REGRA: bug_id OU pagina_visitada_id deve ser NOT NULL

---
TABELA: agendamento
---
id               BIGINT  PK AUTO_INCREMENT
sistema_id       BIGINT  FK sistema(id) NOT NULL
frequencia       ENUM(DIARIO, SEMANAL) NOT NULL
horario          TIME    NOT NULL
dia_semana       TINYINT NULL (1=Seg..7=Dom)
ativo            BOOLEAN NOT NULL DEFAULT TRUE
proxima_execucao DATETIME NOT NULL
ultima_execucao  DATETIME NULL
criado_em        DATETIME NOT NULL DEFAULT NOW()
INDEX: INDEX(sistema_id), INDEX(proxima_execucao), INDEX(ativo)

---
TABELA: notificacao
---
id          BIGINT  PK AUTO_INCREMENT
usuario_id  BIGINT  FK usuario(id) NOT NULL
teste_id    BIGINT  FK teste(id) NOT NULL
tipo        ENUM(TESTE_COM_BUGS, TESTE_FALHOU) NOT NULL
enviado     BOOLEAN NOT NULL DEFAULT FALSE
enviado_em  DATETIME NULL
criado_em   DATETIME NOT NULL DEFAULT NOW()
INDEX: INDEX(usuario_id), INDEX(teste_id), INDEX(enviado)
