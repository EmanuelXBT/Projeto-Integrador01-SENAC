# 🧪 QAwler — Ambientes de teste com autenticação

> **Verificado em 21/09/2026.** Cada alvo marcado **✅ verificado** teve o login
> executado de fato (navegador headless), e as respostas das fixtures de defeito
> foram conferidas por requisição HTTP. Alvos não verificados estão sinalizados.

---

## ⚖️ Regra de ouro

1. Testar **apenas** sistemas próprios ou ambientes públicos **designados para teste**
   (os desta lista).
2. Ambientes em **PROD exigem autorização explícita** e entram em modo `READ_ONLY`
   (regra de negócio 6 do projeto). Nada de produção de terceiros.
3. **Nunca** cadastrar credenciais reais (pessoais, do trabalho ou de terceiros).
   Use contas de demonstração ou um e-mail dedicado.

---

## 1. Alvos públicos de prática — **login por formulário**

| # | Alvo | URL de login | Usuário | Senha | O que exercita |
|---|---|---|---|---|---|
| 1 | the-internet (Elemental Selenium) | `https://the-internet.herokuapp.com/login` | `tomsmith` | `SuperSecretPassword!` | ✅ Login por formulário + redirecionamento para `/secure`. Alvo pequeno: ideal para validar o fluxo ponta a ponta primeiro. |
| 2 | Swag Labs (Sauce Demo) | `https://www.saucedemo.com` | `standard_user` | `secret_sauce` | ✅ Catálogo com navegação interna. A mesma senha vale para `locked_out_user`, `problem_user` e `performance_glitch_user` — úteis para testar falha de login e lentidão. |
| 3 | Practice Test Automation | `https://practicetestautomation.com/practice-test-login/` | `student` | `Password123` | ✅ Tela mínima de login (teste isolado de autenticação). |
| 4 | Expand Testing | `https://practice.expandtesting.com/login` | `practice` | `SuperSecretPassword!` | ✅ Login + aplicação de prática (formulários, tabelas, upload, pop-ups). |
| 5 | OrangeHRM demo | `https://opensource-demo.orangehrmlive.com/web/index.php/auth/login` | `Admin` | `admin123` | ✅ ERP com dashboard, menu e dezenas de páginas internas — bom para o crawler em profundidade 2. É SPA (erros de JS acontecem). |
| 6 | ParaBank (Parasoft) | `https://parabank.parasoft.com/parabank/index.htm` | `john` | `demo` | ✅ Banco demo com muitos links internos (crawler + links quebrados + tabelas). |
| 7 | OWASP Juice Shop (demo público) | `https://demo.owasp-juice.shop/#/login` | `admin@juice-sh.op` | `admin123` | ✅ SPA Angular com rota em *hash* (`#/`) e falhas intencionais — exercita o scanner de JavaScript e a limitação de rota em hash. |
| 8 | Moodle sandbox | `https://sandbox.moodledemo.net/login/index.php` | `student` | `sandbox24` | ✅ LMS grande (cursos, atividades, imagens). A própria tela lista as contas `admin`, `manager`, `teacher`, `student` — todas com a senha `sandbox24`. |
| 9 | the-internet — HTTP Basic | `https://the-internet.herokuapp.com/basic_auth` | `admin` | `admin` | ✅ Autenticação **HTTP Basic** (não é formulário) — confirma que o scanner precisa tratar os dois tipos. |

---

## 2. Massa de defeito (o QAwler precisa **encontrar** algo)

| Alvo | URL | Verificação | Serve para |
|---|---|---|---|
| Imagens quebradas | `https://the-internet.herokuapp.com/broken_images` | ✅ página 200; `asdf.jpg` e `hjkl.jpg` respondem **404** | Scanner de Imagens |
| Códigos de status | `https://the-internet.herokuapp.com/status_codes` | ✅ página 200 | Scanner HTTP (4xx/5xx) |
| reCAPTCHA (demonstração do Google) | `https://www.google.com/recaptcha/api2/demo` | ✅ página carrega com o widget | Regra de **autenticação assistida**: detecção de captcha e pausa para intervenção humana |

---

## 3. Alvos que exigem **cadastro próprio**

Úteis para exercitar RF-01…RF-03 (conta, login, recuperação) e o ciclo completo —
você cria a conta com um e-mail dedicado: `automationexercise.com`, `demoqa.com/login`,
`buggy.justtestit.org`, `demoblaze.com`, `advantageonlineshopping.com`.
*(não verificados nesta rodada; alguns podem exigir captcha no cadastro)*

---

## 4. Ambiente próprio (recomendado: controle total, sem limite de uso)

**A. Na Umbrel** (App Store — servidor sempre ligado, acessível na LAN):

- **WordPress** (app `wordpress`, v7.1.1 no catálogo) — `wp-admin` com login; o usuário
  administrador é criado na instalação. Páginas, mídia e temas dão material de sobra
  para o crawler.
- **Ghost** (app `ghost`, v6.64.0) — painel de administração com login.
- *(verificado no catálogo da Umbrel em 21/09/2026 via MCP)*

**B. Local, via Docker** (no Mac ou em outra máquina — **não** dentro do container Hermes):

| Imagem (Docker Hub) | Observação |
|---|---|
| `bkimminich/juice-shop` | Falhas intencionais; login semeado `admin@juice-sh.op` / `admin123` |
| `vulnerables/web-dvwa` | Login `admin` / `password` (última atualização da imagem: 2018-10-12) — exige *Create/Reset Database* |
| `grafana/grafana` | Login `admin` no primeiro acesso, com troca obrigatória de senha |
| `wordpress` (oficial) ou `bitnami/wordpress` | Instalação guiada na primeira abertura |
| `ghost`, `keycloak/keycloak`, `jenkins/jenkins`, `metabase/metabase` | Sistemas reais com login (credenciais iniciais conforme a documentação de cada imagem) |

*(existência das imagens verificada no Docker Hub em 21/09/2026)*

---

## 5. Como cadastrar no QAwler

1. Painel → **Sistemas** → novo sistema: **nome**, **URL base**, **ambiente**
   (`DEV`/`STAGING`) e **credenciais** (login/senha, opcionais) — RF-04
   (**atenção:** ver §7 sobre o que já está implementado).
2. **Executar teste** manualmente (RF-07) ou configurar **agendamento** diário/semanal (RF-08).
3. Ordem sugerida: **(1) the-internet** (valida o fluxo) → **(6) ParaBank** ou **(5) OrangeHRM**
   (site grande, profundidade 2) → **(7) Juice Shop** (SPA/JS) → **(9) HTTP Basic**
   (tipo de autenticação diferente).

---

## 6. Cuidados

- Logins de demonstração mudam sem aviso: se um alvo ✅ parar de autenticar, a
  credencial foi trocada pelo mantenedor — conferir a página do projeto.
- Cadastre um **e-mail dedicado** para contas criadas por você; nada de senha pessoal.
- Registro de data/método: verificação de 21/09/2026; método = login real em navegador
  headless + requisições HTTP para as fixtures de defeito.

---

## 7. ⚠️ Estado da implementação — o login ainda **não** é usado na varredura

Conferido no código em 21/09/2026 (commit `b9455bd`):

- **Existe:** os campos `credenciais_login`, `credenciais_senha`, `dominios_autorizados`
  e `autorizado_producao` na entidade `Sistema` e no `SistemaRequest` (DTO).
- **Não existe:** nenhum serviço consome esses campos. O `CrawlerService.crawl(baseUrl,
  modo, allowedHosts)` **não recebe credenciais** e não faz login — ele abre o Chrome e
  segue os links. A tela `sistemas.html` também **não coleta** login/senha ainda.
- **Consequência prática:** hoje a varredura é **anônima**. Nos alvos da §1 que exigem
  login, o crawler vai parar na tela de autenticação — o que se testa, na prática, é a
  navegação pública e a detecção do *muro de autenticação*, não a sessão autenticada.

Para exercitar de verdade os alvos com login, faltam (na ordem):

1. **Coletar e persistir** as credenciais no cadastro do sistema (`sistemas.html` +
   `SistemaController`/`SistemaService`), com criptografia AES-256 antes de gravar (RNF).
2. **Fazer login no Selenium** antes do crawl: localizar usuário/senha, submeter o
   formulário e **reutilizar a sessão** (cookies) nas páginas seguintes
   (`CrawlerService.crawl` receber o `Sistema`/credenciais).
3. **Detecção de captcha/2FA** com **pausa para intervenção humana** (regra de negócio 2)
   — a página do reCAPTCHA da §2 serve de fixture para essa detecção.
4. **Tratar HTTP Basic** (§1.9) como tipo de autenticação alternativo.
5. Respeitar `dominios_autorizados` e `autorizado_producao` (regra 6 — bloqueio de PROD).

