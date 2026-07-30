# 🕷️ QAwler — Projeto Integrador I · SENAC

> ⚠️ **Etapa 1 de 3 — Planejamento e Documentação.** Este repositório contém apenas a especificação do sistema (requisitos, DER, stack). O código-fonte (`src/`, `docker-compose.yml`, `pom.xml`) será adicionado na Etapa 2 (Desenvolvimento). Veja o [Roadmap](#-roadmap) para detalhes.

> **Varredura automatizada de QA para ambientes web · Java 17 + Spring Boot + MySQL + Selenium**

[![Java](https://img.shields.io/badge/Java-17-%23ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-%236DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-%234479A1?logo=mysql&logoColor=white)](https://dev.mysql.com/doc/)
[![Selenium](https://img.shields.io/badge/Selenium-4.x-%2343B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Docker](https://img.shields.io/badge/Docker-✓-%232496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Status](https://img.shields.io/badge/Status-Etapa_1_de_3-8A2BE2)](.)

---

## 📌 Sobre o Projeto

**QAwler** é um sistema de varredura automatizada para páginas web em ambientes de **desenvolvimento (DEV)** e **homologação (STAGING)**. O usuário informa a URL de um site e o QAwler percorre a página em busca de **erros de HTML, JavaScript, HTTP e imagens quebradas** — tudo sem exigir configuração complexa.

A grande inovação está na geração de **relatórios em JSON estruturado de baixo nível**, projetados para serem consumidos por ferramentas automatizadas de análise. Empresas podem anexar esses relatórios como documento adicional em seus fluxos de trabalho, permitindo que sistemas externos interpretem e auxiliem na correção dos bugs identificados.

> *"Um erro é detectado, documentado e contextualizado em formato legível por máquina — eliminando a necessidade de relatórios manuais extensos."*

---

## 🎯 O Problema

- **Para times de QA:** Testes manuais são repetitivos, demorados e sujeitos a falha humana
- **Para times de desenvolvimento:** Bugs chegam mal documentados, sem evidências visuais ou contexto
- **Para pequenas empresas:** Ferramentas de QA são caras, complexas e exigem infraestrutura dedicada

## ✅ A Solução

Um **crawler inteligente** que:
- Navega automaticamente pelo site alvo (seguindo links internos)
- Detecta erros HTTP (4xx/5xx), JS, HTML e imagens quebradas
- Gera screenshots como evidência visual
- Entrega relatórios prontos para consumo automatizado

---

## 🏗️ Arquitetura do Sistema

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

### Módulos

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

## 👥 Perfis de Usuário

| Perfil | Permissões |
|---|---|
| **Administrador** | Acesso total: CRUD de usuários, sistemas, parâmetros globais, todos os relatórios |
| **Analista** | Cadastro de sistemas, execução de varreduras, download de relatórios próprios |
| **Visualizador** | Leitura de relatórios, download de relatórios (sem permissão de executar varreduras) |

---

## ⚙️ Requisitos Funcionais

| ID | Nome | Descrição |
|---|---|---|
| **RF001** | Cadastro de Sistema | Cadastrar novo sistema alvo com nome, URL base e credenciais (opcionais) |
| **RF002** | Execução Manual de Teste | Disparar varredura sob demanda em um sistema cadastrado |
| **RF003** | Execução Agendada | Configurar agendamento diário/semanal para varreduras automáticas |
| **RF004** | Crawler Automático | Navegação automática seguindo links internos até profundidade configurável |
| **RF005** | Scanner HTTP | Detecção de respostas com status 4xx e 5xx |
| **RF006** | Scanner JavaScript | Captura de erros do console (`console.error`) |
| **RF007** | Scanner de Imagens | Identificação de imagens quebradas (404, CORS) |
| **RF008** | Captura de Screenshots | Screenshot de cada página visitada e de páginas com erros |
| **RF009** | Geração de Relatório | Compilação de todos os erros com timestamp, duração e resultados |
| **RF010** | Armazenamento em Banco | Persistência de relatórios no MySQL com integridade referencial |
| **RF011** | Histórico por Sistema | Visualização de histórico completo com tendência de bugs ao longo do tempo |
| **RF012** | Download de Relatório (JSON) | Exportação em JSON estruturado para consumo por ferramentas automatizadas |
| **RF013** | Download de Relatório (PDF) | Exportação em PDF para documentação e compartilhamento |
| **RF014** | Notificação por E-mail | Envio automático de e-mail quando um teste conclui com erros |
| **RF015** | Cancelamento de Varredura | Interrupção de varreduras em andamento |
| **RF016** | Dashboard de Métricas | Métricas agregadas: total de varreduras, erros por tipo, sistemas mais problemáticos |

---

## 🛡️ Requisitos Não Funcionais

| ID | Nome | Descrição |
|---|---|---|
| **RNF001** | Interface Intuitiva | Interface limpa com a área de inserção de URL como elemento central |
| **RNF002** | Segurança e Ética | Operações exclusivamente de leitura — nunca modifica o ambiente testado |
| **RNF003** | Performance | Throttling de requisições e timeouts configuráveis para não sobrecarregar o alvo |
| **RNF004** | Disponibilidade Web | Aplicação web acessível via Chrome, Firefox e Edge |
| **RNF005** | Criptografia de Credenciais | Credenciais de sistemas armazenadas com AES-256 |
| **RNF006** | Autenticação JWT | Token JWT com expiração de 24h em todas as rotas protegidas |
| **RNF007** | Isolamento de Dados | Cada usuário acessa apenas seus próprios sistemas e resultados |
| **RNF008** | Execução Paralela | Suporte a até 5 varreduras simultâneas sem degradação |
| **RNF009** | Portabilidade de Relatório | Schema JSON padronizado, independente de tecnologia |
| **RNF010** | Containerização | Executável via `docker-compose up` com um único comando |
| **RNF011** | Logs Estruturados | Logs em formato JSON com nível, timestamp, serviço e mensagem |
| **RNF012** | Manutenibilidade | Código modular, documentado, seguindo Clean Code e SOLID |

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

> Documento completo do DER: [`docs/DER.md`](./docs/DER.md)

---

## 🚀 Começando (Docker)

```bash
# 1. Clone o repositório
git clone https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC.git
cd Projeto-Integrador01-SENAC

# 2. Suba os containers (API + MySQL + RabbitMQ + Worker)
docker-compose up -d

# 3. Acesse a aplicação
# Frontend: http://localhost:8080
# API Docs:  http://localhost:8080/swagger-ui.html
```

### Pré-requisitos

- Docker 24+ e docker-compose 2+
- Java 17 (apenas para desenvolvimento local)
- Maven 3.9+ (apenas para desenvolvimento local)

---

## 📁 Estrutura do Projeto

```
Projeto-Integrador01-SENAC/
├── docs/
│   ├── DER.md              # Diagrama Entidade-Relacionamento completo
│   └── REQUISITOS.md       # Requisitos detalhados do sistema
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
├── README.md
└── LICENSE
```

---

## 📝 Regras de Negócio

1. **Leitura apenas** — O sistema nunca modifica o ambiente testado
2. **Autenticação assistida** — Se um captcha ou login for detectado, o scanner pausa e aguarda intervenção humana
3. **Timeout configurável** — Cada varredura tem timeout máximo (padrão: 5 minutos)
4. **Retenção** — Relatórios são armazenados por no mínimo 90 dias
5. **Relatório autocontido** — Deve ser possível entender o problema sem consultar fontes externas
6. **Bloqueio de produção** — Sistemas em ambiente PROD exigem autorização explícita e forçam modo READ_ONLY

---

## 🔮 Diferenciais

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

## 🗺️ Roadmap

### Etapa 1 ✅ — Planejamento e Requisitos *(atual)*
- [x] Documento de visão e escopo
- [x] Requisitos funcionais e não funcionais
- [x] Modelagem do banco de dados (DER)
- [x] Stack tecnológica definida

### Etapa 2 🔜 — Desenvolvimento
- [ ] API REST com Spring Boot
- [ ] Implementação dos scanners (HTTP, JS, Imagens)
- [ ] Frontend Thymeleaf + Bootstrap
- [ ] Integração Selenium + Chromium headless
- [ ] Docker Compose funcional

### Etapa 3 📅 — Entrega e Apresentação
- [ ] Testes automatizados
- [ ] Deploy de demonstração
- [ ] Documentação final
- [ ] Apresentação para banca

---

## 👤 Autor

**Emanuel Filipe da Silva** — [@EmanuelXBT](https://github.com/EmanuelXBT)

- 📚 Curso Técnico em Desenvolvimento de Sistemas — SENAC Minas Gerais
- 📅 Projeto Integrador I (UC1 a UC5)
- 📧 contato.emanuel2002@gmail.com

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT — veja o arquivo [LICENSE](./LICENSE) para detalhes.

---

> 🔒 **Aviso:** Todo o conteúdo deste repositório, incluindo a arquitetura proposta e documentos anexos, é de autoria própria. A reprodução sem os devidos créditos é considerada plágio.
