# 🏗️ QAwler — Arquitetura do Sistema

> Documento extraído do README principal. Descreve a arquitetura, stack tecnológica, modelo de dados e regras de negócio do QAwler.

---

## Diagrama de Arquitetura

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Frontend   │────▶│  Spring Boot │────▶│   MySQL 8    │
│  (Thymeleaf) │     │   (REST)     │     │   (InnoDB)   │
└──────────────┘     └──────┬───────┘     └──────────────┘
                            │
                   ┌────────▼───────┐
                   │   RabbitMQ     │
                   │  (mensageria)  │
                   └────────┬───────┘
                            │
                   ┌────────▼───────┐
                   │  Selenium      │
                   │  Worker        │
                   │  (Chromium)    │
                   └────────────────┘
```

O desacoplamento via **RabbitMQ** entre a API REST e os workers Selenium permite escalar os crawlers independentemente. A API recebe a requisição, publica na fila, e workers consomem as tarefas de varredura.

---

## Módulos do Sistema

| Módulo | Responsabilidade |
|---|---|
| **Scanner HTML** | Validação de estrutura, tags malformadas, acessibilidade |
| **Scanner JavaScript** | Captura de `console.error`, exceções não tratadas, warnings |
| **Scanner HTTP** | Detecção de status 4xx e 5xx em todas as páginas visitadas |
| **Scanner de Imagens** | Identificação de imagens quebradas (404, CORS, lazy-load) |
| **Gestão de Sistemas** | Cadastro de sistemas com credenciais criptografadas (AES-256) |
| **Agendamento** | Testes automáticos diários/semanais por sistema |
| **Relatórios** | Geração, armazenamento e exportação em JSON + PDF |
| **Notificações** | E-mail automático quando bugs são detectados |

---

## 🧱 Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| **Backend API** | Java 17 + Spring Boot 3 |
| **Banco de Dados** | MySQL 8 (InnoDB) |
| **ORM** | Spring Data JPA + Hibernate |
| **Autenticação** | Spring Security + JWT (jjwt) |
| **Crawler** | Selenium WebDriver 4 + Chromium headless |
| **Mensageria** | RabbitMQ (desacoplamento API ↔ Worker) |
| **Frontend** | Thymeleaf + Bootstrap 5 |
| **Build** | Maven |
| **Deploy** | Docker + docker-compose |
| **Geração PDF** | OpenPDF |
| **Envio de E-mail** | Spring Boot Mail + SMTP |

---

## 📊 Modelo de Dados (DER)

O banco é composto por **8 tabelas** normalizadas com InnoDB:

```
usuario (1)──(N) sistema (1)──(N) teste (1)──(N) pagina_visitada
                 │                  │
                 │                  ├──(N) bug (1)──(N) screenshot
                 │                  │
                 ├──(N) agendamento  │
                 │                  │
usuario (1)──(N) notificacao (N)──(1) teste
```

### Tabelas principais

| Tabela | Função |
|---|---|
| `usuario` | Contas com bcrypt, JWT auth |
| `sistema` | Sites cadastrados com credenciais AES-256 |
| `teste` | Execuções com status (QUEUED → RUNNING → COMPLETED/FAILED) |
| `pagina_visitada` | URLs percorridas com HTTP status e tempo de carga |
| `bug` | Erros classificados (HTTP_ERROR, JS_ERROR, BROKEN_IMAGE) com severidade |
| `screenshot` | Evidências visuais com dimensões |
| `agendamento` | Configuração de recorrência (DIARIO/SEMANAL) |
| `notificacao` | Disparo de e-mails por evento |

> DER completo com colunas, tipos e índices: [`docs/DER.md`](./DER.md)

---

## 📝 Regras de Negócio

1. **Leitura apenas** — O sistema nunca modifica o ambiente testado
2. **Autenticação assistida** — Se um captcha ou login for detectado, o scanner pausa e aguarda intervenção humana
3. **Timeout configurável** — Cada varredura tem timeout máximo (padrão: 5 minutos)
4. **Retenção** — Relatórios são armazenados por no mínimo 90 dias
5. **Relatório autocontido** — Deve ser possível entender o problema sem consultar fontes externas
6. **Bloqueio de produção** — Sistemas em ambiente PROD exigem autorização explícita e forçam modo READ_ONLY

---

## 🔮 Diferenciais Técnicos

- **Relatório de Baixo Nível** — JSON estruturado compatível com ferramentas automatizadas (schema padronizado)
- **Detecção Inteligente de Autenticação** — Reconhece padrões comuns de login e captcha automaticamente
- **Crawler Multi-nível** — Segue links internos até profundidade configurável
- **Screenshots como Evidência** — Cada erro é acompanhado de screenshot no momento da detecção
- **Zero Config** — O usuário insere a URL, o sistema faz o resto

### Schema do Relatório JSON

```json
{
  "scan_id": "uuid",
  "target_url": "https://...",
  "timestamp": "2026-07-22T16:00:00Z",
  "duration_ms": 12345,
  "results": {
    "http_errors": [
      { "url": "...", "status": 500, "message": "..." }
    ],
    "js_errors": [
      { "url": "...", "message": "...", "line": 42, "column": 12 }
    ],
    "broken_images": [
      { "url": "...", "reason": "404" }
    ]
  },
  "context_summary": "texto narrativo do contexto da varredura",
  "analysis_suggestion": "comando sugerido para ferramentas automatizadas"
}
```

---

## 📁 Estrutura do Projeto (Planejada — Etapa 2)

```
Projeto-Integrador01-SENAC/
├── docs/
│   ├── DER.md              # Diagrama Entidade-Relacionamento completo
│   ├── REQUISITOS.md       # Requisitos detalhados do sistema
│   └── ARCHITECTURE.md     # Este documento
├── src/
│   ├── main/
│   │   ├── java/br/com/qawler/
│   │   │   ├── controller/     # REST endpoints
│   │   │   ├── service/        # Lógica de negócio
│   │   │   ├── repository/     # Spring Data JPA
│   │   │   ├── model/          # Entidades JPA
│   │   │   ├── scanner/        # Módulos de varredura
│   │   │   ├── report/         # Geração de relatórios
│   │   │   └── config/         # Security, RabbitMQ, Selenium
│   │   └── resources/
│   │       ├── application.yml
│   │       └── templates/      # Thymeleaf views
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── .env.example
├── README.md
├── CONTRIBUTING.md
└── LICENSE
```

> ⚠️ A estrutura `src/`, `docker-compose.yml`, `Dockerfile` e `pom.xml` será implementada na **Etapa 2** (Desenvolvimento).
