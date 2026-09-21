# 🕷️ QAwler — Projeto Integrador I · SENAC

> ⚠️ **Etapa 2 de 3 — Desenvolvimento.** Código implementado e **build validado** (`mvn -DskipTests package`, 21/09/2026). Em andamento: execução ponta-a-ponta (MySQL/RabbitMQ) e o **login assistido** — ver [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md).

> **Varredura automatizada de QA para ambientes web · Java 17 + Spring Boot + MySQL + Selenium**

[![Java](https://img.shields.io/badge/Java-17-%23ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-%236DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-%234479A1?logo=mysql&logoColor=white)](https://dev.mysql.com/doc/)
[![Selenium](https://img.shields.io/badge/Selenium-4.x-%2343B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Docker](https://img.shields.io/badge/Docker-✓-%232496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Status](https://img.shields.io/badge/Etapa-2_de_3_(Desenvolvimento)-orange)](https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC#-roadmap)

---

## 📌 Sobre o Projeto

**QAwler** é um sistema de varredura automatizada para páginas web em ambientes de **desenvolvimento (DEV)** e **homologação (STAGING)**. O usuário informa a URL de um site e o QAwler percorre a página em busca de **erros de HTML, JavaScript, HTTP e imagens quebradas** — tudo sem exigir configuração complexa.

A grande inovação está na geração de **relatórios em JSON estruturado de baixo nível**, projetados para serem consumidos por ferramentas automatizadas de análise. Empresas podem anexar esses relatórios como documento adicional em seus fluxos de trabalho, permitindo que sistemas externos interpretem e auxiliem na correção dos bugs identificados.

> *"Um erro é detectado, documentado e contextualizado em formato legível por máquina — eliminando a necessidade de relatórios manuais extensos."*

Na **revisão v2 do escopo (Etapa 2)**, o QAwler passa a ser organizado por **sistemas monitorados** (cadastro de alvos, testes, agendamentos e relatórios por sistema) e inclui a **varredura de áreas autenticadas por login assistido**: a aplicação abre a janela do navegador **na máquina do usuário** (a estação de autenticação), a pessoa faz o login — inclusive captcha/2FA — e o crawler segue **naquela sessão**, sem armazenar credenciais. Especificação: [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md).

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

## 👥 Perfis de Usuário

| Perfil | Permissões |
|---|---|
| **Administrador** | Acesso total: CRUD de usuários, sistemas, parâmetros globais, todos os relatórios |
| **Analista** | Cadastro de sistemas, execução de varreduras, download de relatórios próprios |
| **Visualizador** | Leitura de relatórios, download de relatórios (sem permissão de executar varreduras) |

---

## 📚 Documentação

| Documento | Conteúdo |
|---|---|
| [`docs/REQUISITOS.md`](./docs/REQUISITOS.md) | Requisitos funcionais (RF-01 a RF-17) e não funcionais (RNF-01 a RNF-11) |
| [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md) | Diagrama de arquitetura, módulos, stack, DER, regras de negócio, schema JSON |
| [`docs/DER.md`](./docs/DER.md) | Diagrama Entidade-Relacionamento completo — 8 tabelas, colunas, tipos, índices |
| [`docs/AMBIENTES-DE-TESTE.md`](./docs/AMBIENTES-DE-TESTE.md) | Ambientes com autenticação para testar o QAwler (públicos verificados, fixtures de defeito, ambiente próprio) |
| [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md) | Especificação do login assistido — janela do navegador do usuário para autenticar, crawl na sessão e achados na aplicação |
| [`CONTRIBUTING.md`](./CONTRIBUTING.md) | Guia de contribuição, padrões de commit, setup local |

---

## 🚀 Começando (Docker) — Etapa 2

```bash
# 1. Clone o repositório
git clone https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC.git
cd Projeto-Integrador01-SENAC

# 2. Configure o ambiente
cp .env.example .env
# Preencha as variáveis no .env

# 3. Suba os containers (API + MySQL + RabbitMQ + Worker)
docker-compose up -d

# 4. Acesse a aplicação
# Frontend: http://localhost:8080
# API Docs:  http://localhost:8080/swagger-ui.html
```

### Login assistido (alvos que exigem autenticação)

O QAwler **não guarda credenciais**: ele abre uma janela do navegador **na máquina do
usuário** (a *estação de autenticação*), você faz o login manualmente — captcha e 2FA
ficam com você — e o crawler varre **naquela sessão**. Detalhes: [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md).

```bash
# 1. Na estação (a máquina onde o navegador deve abrir)
./tools/qawler-login.sh --url https://alvo.dev.local/login          # mesma máquina do QAwler
./tools/qawler-login.sh --bind 100.x.y.z --url https://alvo.dev.local/login   # outra máquina (rede privada)

# 2. Na aplicação (http://localhost:8080 → telas → Sistemas)
#    informe o endereço da estação, clique em “Concluí o login” e depois em “Executar Testes”

# 3. Encerrar a sessão de login quando terminar
./tools/qawler-login.sh --stop
```

Windows: `powershell -ExecutionPolicy Bypass -File .\tools\qawler-login.ps1 -Url <url> [-Bind <ip>]`.

### Pré-requisitos

- Docker 24+ e docker-compose 2+
- Java 17 (apenas para desenvolvimento local)
- Maven 3.9+ (apenas para desenvolvimento local)

---

## 🗺️ Roadmap

### Etapa 1 ✅ — Planejamento e Requisitos *(atual)*
- [x] Documento de visão e escopo
- [x] Requisitos funcionais e não funcionais
- [x] Modelagem do banco de dados (DER)
- [x] Stack tecnológica definida

### Etapa 2 🔄 — Desenvolvimento *(atual)*
- [x] API REST com Spring Boot (controllers, DTOs, autenticação JWT)
- [x] Modelo de dados em JPA (8 entidades + repositórios)
- [x] Implementação dos scanners (HTTP, JS, imagens quebradas)
- [x] Frontend Thymeleaf + Bootstrap (5 telas)
- [x] Integração Selenium + navegador headless (código; execução ponta-a-ponta ainda não validada)
- [x] Docker Compose (API + MySQL + RabbitMQ + Worker) — arquivo pronto, ainda não exercitado
- [x] **Build validado:** `mvn -DskipTests package` → BUILD SUCCESS (21/09/2026)
- [x] **Login assistido — E1 (backend):** sessão assistida (`/api/sistemas/{id}/sessao/*`), estado `AGUARDANDO_LOGIN`, modos local (M1) e estação remota (M2) — ver [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md)
- [x] **Login assistido — E2:** helper `qawler-login` (shell com `--bind/--status/--stop` + PowerShell) e telas com painel da sessão (abrir janela, testar conexão, “Concluí o login”, encerrar)
- [x] **Camada web:** login por cookie HttpOnly + telas `/sistemas`, `/dashboard`, `/testes/{id}` renderizando dados reais
- [x] **Testes de integração:** 10 testes (H2 + MockMvc) — `mvn test`
- [ ] Login assistido — E3: varredura anexada à sessão autenticada
- [ ] Execução ponta-a-ponta validada (app + MySQL + RabbitMQ + alvo real)

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