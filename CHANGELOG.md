# Changelog

Todas as mudanças notáveis deste projeto são documentadas neste arquivo.

O formato segue [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/),
e o versionamento segue [Semantic Versioning](https://semver.org/lang/pt-BR/).

---

## [1.0.0] — 2026-07-30

### Added
- Documento de visão e escopo do QAwler
- Requisitos funcionais (RF-01 a RF-17) em [`docs/REQUISITOS.md`](./docs/REQUISITOS.md)
- Requisitos não funcionais (RNF-01 a RNF-11)
- Modelagem do banco de dados — DER com 8 tabelas em [`docs/DER.md`](./docs/DER.md)
- Documento de arquitetura em [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md)
- Stack tecnológica definida (Java 17, Spring Boot 3, MySQL 8, Selenium, RabbitMQ, Docker)
- `.env.example` com variáveis de ambiente documentadas
- `CONTRIBUTING.md` com guia de contribuição e conventional commits
- `CHANGELOG.md` (este arquivo)
- GitHub Actions: workflow `build.yml` com verificação de docs + build Maven condicional
- Templates de Issue (bug report, feature request) e Pull Request
- Badges no README (Java, Spring Boot, MySQL, Selenium, Docker, licença, etapa)

### Changed
- README reestruturado: conteúdo de arquitetura/stack/DER extraído para `docs/`
- Títulos de `docs/DER.md` e `docs/REQUISITOS.md` corrigidos de "QA Autopilot" para "QAwler"
- `.gitignore` limpo: removidos resquícios da migração BeyondApp

---

## [Unreleased]

### Planned (Etapa 2 — Desenvolvimento)
- API REST com Spring Boot 3
- Implementação dos scanners (HTTP, JS, Imagens, HTML)
- Frontend Thymeleaf + Bootstrap 5
- Integração Selenium WebDriver + Chromium headless
- Docker Compose funcional (API + MySQL + RabbitMQ + Worker)

### Planned (Etapa 3 — Entrega)
- Testes automatizados
- Deploy de demonstração
- Documentação final
- Apresentação para banca SENAC

---

[1.0.0]: https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC/releases/tag/v1.0.0
