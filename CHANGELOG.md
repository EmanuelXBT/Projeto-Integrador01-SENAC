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

### Added
- Revisão v2 do escopo e do design da interface web (5 telas) em [`docs/design-interface-qawler-v2.md`](./docs/design-interface-qawler-v2.md)
- [`docs/AMBIENTES-DE-TESTE.md`](./docs/AMBIENTES-DE-TESTE.md) — 9 alvos públicos com autenticação verificados, fixtures de defeito e opções de ambiente próprio
- [`docs/LOGIN-ASSISTIDO.md`](./docs/LOGIN-ASSISTIDO.md) — especificação do login assistido (janela do navegador na estação do usuário → varredura na sessão autenticada)
- **Login assistido — E1 (backend):** `SessaoAssistidaService`, `SessaoController` (`GET/POST /api/sistemas/{id}/sessao/*`), `SessaoResponse`, estado `AGUARDANDO_LOGIN` em `StatusTeste` e configuração `crawler.assistido.*`
- Scripts SQL da Atividade 3 do UC05 em [`docs/`](./docs/)
- **Login assistido — E2:** helper da estação [`tools/qawler-login.sh`](./tools/qawler-login.sh) (macOS/Linux: `--url`, `--porta`, `--perfil`, `--bind`, `--navegador`, `--status`, `--stop`) e [`tools/qawler-login.ps1`](./tools/qawler-login.ps1) (Windows)
- **UI da sessão assistida:** [`static/js/qawler-sessao.js`](./src/main/resources/static/js/qawler-sessao.js) + painel nas telas `sistemas` e `teste-detalhe` (abrir janela, testar conexão, “Concluí o login”, encerrar, status ao vivo) e card “Aguardando login” no dashboard
- **Camada web:** `WebController` (login por cookie HttpOnly, `/sistemas`, `/dashboard`, `/testes/{id}`) e `AuthCookie`

### Changed
- Portabilidade: `crawler.chromium-path` é opcional — vazio, o Selenium Manager resolve o navegador do sistema (macOS/Windows/Linux)
- README: badge de etapa, status e roadmap alinhados ao estado real da Etapa 2

### Fixed
- Login assistido: encerrar ou trocar de sessão finaliza a árvore do processo lançado (não deixa processo órfão)
- `AuthService` ainda lia `jwt.secret`/`jwt.expiration-ms` depois da renomeação para `qawler.jwt.*` — a aplicação **não subia** (placeholder não resolvido); corrigido e coberto por teste
- `.env.example` e `.env.example.docker` alinhados às variáveis atuais (`QAWLER_JWT_SECRET`)
- Helper da estação: a leitura do JSON do CDP não funcionava (o heredoc do Python consumia o stdin)

### Verified
- `mvn -B -DskipTests package` → **BUILD SUCCESS** (21/09/2026, JDK 21 · alvo Java 17)
- `mvn -B test` → **10/10 testes** de integração (H2 + MockMvc): contexto sobe, login web por cookie, API com Bearer, fluxo da sessão assistida no modo remoto e renderização das telas
- Verificação funcional da E1 contra um endpoint CDP simulado: **21/21 checagens** (harness em `/opt/data/scripts/smoke_e1/`)
- Helper da estação verificado contra CDP simulado: `--status`, `--stop` e os argumentos de lançamento do navegador

### Planned (Etapa 2 — restante)
- E3: varredura anexada à sessão (`crawl` com o `WebDriver` da sessão, sem `quit()`)
- E4: reuso de sessão para agendamento (RF-08) e detecção de muro de login
- Execução ponta-a-ponta validada (aplicação + MySQL + RabbitMQ + alvo real)

### Planned (Etapa 3 — Entrega)
- Testes automatizados
- Deploy de demonstração
- Documentação final
- Apresentação para banca SENAC

---

[1.0.0]: https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC/releases/tag/v1.0.0
