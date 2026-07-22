# QA Autopilot — Requisitos do Sistema

## Visão

SaaS de QA automatizado para pequenas empresas de software.
O cliente cadastra a URL do sistema web → o crawler roda automaticamente → gera relatório de bugs com evidências.

---

## Requisitos Funcionais (RF)

### Módulo: Autenticação e Usuário

**RF-01** — Cadastro de conta
O usuário deve poder se cadastrar com e-mail e senha.

**RF-02** — Autenticação
O usuário deve poder fazer login com e-mail e senha, obtendo um token JWT.

**RF-03** — Recuperação de senha
O usuário deve poder solicitar redefinição de senha via e-mail.

---

### Módulo: Gestão de Sistemas

**RF-04** — Cadastro de sistema
O usuário deve poder cadastrar um sistema informando: nome, URL base e credenciais de acesso (login/senha, opcionais).

**RF-05** — Listagem de sistemas
O usuário deve poder visualizar todos os seus sistemas cadastrados com status do último teste.

**RF-06** — Edição e remoção de sistema
O usuário deve poder editar ou remover um sistema cadastrado.

---

### Módulo: Execução de Testes

**RF-07** — Execução manual de teste
O usuário deve poder disparar manualmente um teste para um sistema cadastrado.

**RF-08** — Execução agendada de teste
O usuário deve poder configurar agendamento de testes (diário, semanal) para cada sistema.

**RF-09** — Crawler automático de páginas
Ao executar um teste, o sistema deve navegar automaticamente pelas páginas do site cadastrado, seguindo links internos até o limite de profundidade configurado (padrão: 2 níveis).

**RF-10** — Detecção de erros HTTP
O crawler deve detectar e registrar respostas HTTP com status 4xx e 5xx.

**RF-11** — Detecção de erros JavaScript
O crawler deve capturar erros de JavaScript (console.error) das páginas visitadas.

**RF-12** — Captura de screenshots
O crawler deve gerar screenshot de cada página visitada e de páginas com erros detectados.

---

### Módulo: Resultados e Relatórios

**RF-13** — Visualização de resultados por teste
O usuário deve poder visualizar o resultado de cada execução: páginas visitadas, erros encontrados, screenshots.

**RF-14** — Histórico de testes
O usuário deve poder visualizar o histórico de execuções de um sistema com tendencia de bugs ao longo do tempo.

**RF-15** — Detalhe do bug
O usuário deve poder visualizar o detalhe de cada bug: tipo (HTTP/JS), URL, mensagem de erro, screenshot, timestamp.

**RF-16** — Exportação de relatório
O usuário deve poder exportar o relatório de uma execução em PDF.

---

### Módulo: Notificações

**RF-17** — Notificação por e-mail
O sistema deve enviar notificação por e-mail ao usuário quando um teste for concluído com erros.

---

## Requisitos Não Funcionais (RNF)

### Performance

**RNF-01** — Tempo de resposta da API
Endpoints da API devem responder em até 500ms para operações de leitura e até 2s para operações de escrita.

**RNF-02** — Execução paralela de testes
O sistema deve suportar a execução de até 5 testes simultâneos sem degradação.

---

### Segurança

**RNF-03** — Criptografia de senhas
Senhas devem ser armazenadas com hash bcrypt (cost ≥ 10).

**RNF-04** — Autenticação JWT
A API deve exigir token JWT válido em todas as rotas protegidas, com expiração de 24h.

**RNF-05** — Criptografia de credenciais de sistemas
Credenciais de acesso aos sistemas dos clientes devem ser armazenadas criptografadas (AES-256).

**RNF-06** — Isolamento de dados
Cada usuário só pode acessar seus próprios sistemas e resultados.

---

### Escalabilidade

**RNF-07** — Arquitetura desacoplada
A execução do crawler deve ser desacoplada da API principal via fila de mensagens, permitindo escalar os workers independentemente.

---

### Disponibilidade

**RNF-08** — Persistência de dados
O banco de dados MySQL deve operar com InnoDB e garantir ACID nas transações de resultados de teste.

---

### Manutenibilidade

**RNF-09** — Containerização
O sistema deve ser executável via docker-compose com um único comando (API + MySQL + Worker).

**RNF-10** — Logs estruturados
O sistema deve gerar logs em formato JSON com nível, timestamp, serviço e mensagem.

---

### Compatibilidade

**RNF-11** — Navegador do crawler
O crawler deve utilizar Chromium headless (via Selenium WebDriver) compatível com sites modernos (SPA, HTTPS).

---

## Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Backend API | Java 17 + Spring Boot 3 |
| Banco de Dados | MySQL 8 (InnoDB) |
| ORM | Spring Data JPA + Hibernate |
| Auth | Spring Security + JWT (jjwt) |
| Crawler | Selenium WebDriver 4 + Chromium |
| Fila | RabbitMQ (mensageria entre API e Worker) |
| Build | Maven |
| Deploy | Docker + docker-compose |
| Geração PDF | OpenPDF ou iText |
| Envio de e-mail | Spring Boot Mail + SMTP |

---

## Fora do Escopo (v2+)

- Testes de carga/stress
- Autenticação OAuth (Google, GitHub)
- Integração com CI/CD (GitHub Actions, etc.)
- Dashboard em tempo real (WebSocket)
- API pública para integração
- Testes em aplicações mobile
- Multi-idiomas
