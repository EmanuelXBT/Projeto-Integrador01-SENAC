# 🕷️ QAwler — Projeto Integrador I · SENAC

> ⚠️ **Etapa 2 de 3 — Desenvolvimento** O código-fonte está em implementação ativa. Veja o [Roadmap](#-roadmap) para detalhes.

> **Varredura automatizada de QA para ambientes web · Java 17 + Spring Boot + MySQL + Selenium**

[![Java](https://img.shields.io/badge/Java-17-%23ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-%236DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-%234479A1?logo=mysql&logoColor=white)](https://dev.mysql.com/doc/)
[![Selenium](https://img.shields.io/badge/Selenium-4.x-%2343B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/)
[![Docker](https://img.shields.io/badge/Docker-✓-%232496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](./LICENSE)
[![Status](https://img.shields.io/badge/Etapa-1_de_3_(Planejamento)-blue)](https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC#-roadmap)

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