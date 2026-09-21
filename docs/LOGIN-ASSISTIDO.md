# 🔐 QAwler — Login assistido (varredura em sessão autenticada)

> **Especificação v0.1 — 21/09/2026 · aguardando OK do Emanuel antes de implementar.**
> Decisão de projeto: o QAwler **não armazena credenciais**. Ele abre uma **janela do
> navegador do usuário** para o login manual; dessa sessão autenticada o **crawler**
> parte, e os resultados aparecem **dentro da aplicação**.
> A janela abre na **máquina do usuário do sistema** — a *estação de autenticação* — que
> **pode ser diferente** da máquina que roda o QAwler (ver §5: modos M1 local e M2 remoto).

---

## 1. Objetivo

Permitir varrer sistemas que exigem login **sem guardar senha nenhuma** e sem quebrar
com captcha/2FA: quem autentica é a pessoa, em uma janela visível; a aplicação usa a
sessão resultante para o crawl e mostra os achados nas telas que já existem
(`teste-detalhe` / `relatorio`).

**História de usuário:** como analista, quero cadastrar um sistema que exige login,
abrir a janela de autenticação, entrar manualmente e mandar o QAwler varrer essa sessão,
vendo as páginas e os defeitos aparecerem na aplicação.

**Fora de escopo (v0.1):** gravar credenciais; fazer login automático por formulário;
importar sessão de outro navegador/perfil; agendar varredura de alvo **nunca** autenticado.

---

## 2. Fluxo

```
 [Qawler UI]      POST /sistemas/{id}/sessao/abrir
      │            → abre Chrome (perfil dedicado) na URL base do sistema
      ▼
 [janela do usuário]  login manual (captcha/2FA ficam com o humano)
      │
      │  a app faz polling em http://127.0.0.1:<porta>/json/version
      ▼
 [Qawler UI]      botão "Concluí o login"  → POST /sistemas/{id}/sessao/confirmar
      │            → Selenium ANEXA à janela (debuggerAddress) e roda o crawl
      ▼
 [Teste]  AGUARDANDO_LOGIN → RUNNING → COMPLETED/FAILED
      │            páginas, screenshots e bugs persistidos
      ▼
 [Qawler UI]      teste-detalhe / relatorio exibem os achados da área autenticada
```

Estado novo no ciclo do `Teste`: **`AGUARDANDO_LOGIN`** (intervenção humana, regra de
negócio 2 — “autenticação assistida”).

---

## 3. Decisões técnicas (com fontes)

| Decisão | Por quê |
|---|---|
| **Anexar** o Selenium a um Chrome aberto com `--remote-debugging-port` + `--user-data-dir` dedicado (`ChromeOptions.setExperimentalOption("debuggerAddress", "127.0.0.1:<porta>")`) | A janela é do usuário, não do driver: sobrevive a restart da aplicação e **não** é fechada no fim do crawl (na sessão anexada o app **nunca** chama `driver.quit()`). |
| Perfil dedicado `~/.qawler/chrome-profile` | Chrome 136+ **ignora** `--remote-debugging-port` no perfil padrão (`DevTools remote debugging requires a non-default data directory`) — mudança de segurança de 29/04/2025 (blog Chrome for Developers). O próprio Chromium recomenda nosso fluxo: criar `user-data-dir` separado, **logar interativamente** nele e usá-lo na automação (chromium issue 422518918, *Won't Fix — comportamento esperado*). |
| Perfil **persistente** | Os cookies ficam no diretório: execução seguinte reaproveita a sessão (inclusive agendada, RF-08). Quando expira, a app detecta o muro de login e pede nova janela. |
| Porta de debug só em `127.0.0.1`, porta livre escolhida pela app (padrão 9222) | Superfície mínima. |
| Nada de credencial em banco | Os campos `credenciais_login`/`credenciais_senha` da entidade `Sistema` ficam **depreciados** (ver §7). |

---

## 4. Mudanças no código (proposta)

**Novo**
- `service/SessaoAssistidaService` — abrir/verificar/anexar/fechar sessão; guarda
  `sistemaId → modo (local/remoto) + endereço da estação + porta + status`; nunca `quit()`
  em sessão anexada.
- `dto/SessaoResponse` — `aberta`, `modo`, `estacao`, `porta`, `urlAtual`, `aguardandoLogin`.
- `tools/qawler-login.sh` (helper da estação) — abre o navegador com perfil dedicado e
  porta de debug, imprime o endereço a informar no QAwler e encerra com `--stop`.
- Endpoints em `SistemaController` (ou novo `SessaoController`):
  `POST /sistemas/{id}/sessao/abrir` · `GET /sistemas/{id}/sessao` ·
  `POST /sistemas/{id}/sessao/testar` (checa `/json/version` da estação) ·
  `POST /sistemas/{id}/sessao/confirmar` · `POST /sistemas/{id}/sessao/encerrar`.

**Alterado**
- `CrawlerService`: `crawl(...)` passa a receber o `WebDriver` (ou a sessão) em vez de
  sempre criar um driver próprio; `createDriver()` vira `createDriver(Modo driver)` com
  três modos — `HEADLESS` (atual), `ASSISTIDO_LOCAL` (anexa em `127.0.0.1:<porta>`) e
  `ASSISTIDO_REMOTO` (anexa em `<estação>:<porta>` ou no túnel local, via `RemoteWebDriver`).
- `TesteService` + entidade `Teste`: estado `AGUARDANDO_LOGIN`.
- `templates/teste-detalhe.html` e `sistemas.html`: botão **“Abrir janela de login”**,
  status ao vivo (polling leve) e botão **“Concluí o login”**.
- `application.yml`: `crawler.chromium-path` deixa de ser fixo em `/usr/bin/chromium`
  (Linux); no macOS usar o binário do Chrome instalado ou deixar o Selenium Manager
  resolver. Novas chaves: `crawler.assistido.perfil`, `crawler.assistido.porta`.

**Depreciado**
- `credenciais_login` / `credenciais_senha` (entidade + DTO): permanecem por
  compatibilidade, sem uso, e saem na próxima migração — a decisão é **não** guardar senha.

---

## 5. Onde a janela de login abre — **a máquina do usuário do sistema**

Premissa corrigida (21/09/2026): *“navegador padrão do usuário” = o navegador do
**usuário do sistema** (quem opera), **independente de qual computador ele esteja
usando**.* A janela de autenticação abre **no cliente**, não no servidor do QAwler.
Isso separa dois papéis:

- **Servidor do QAwler** — onde a aplicação roda (Mac local, devbox, Umbrel…), com o
  crawler/Selenium.
- **Estação de autenticação** — a máquina do usuário no momento da varredura, com o
  navegador visível. É nela que o login acontece; **pode não ser a mesma máquina**.

### Dois modos de conexão

**M1 — Local (mesma máquina)** — servidor e estação iguais. A aplicação abre a janela:

```bash
open -na "Google Chrome" --args --remote-debugging-port=9222 \
  --user-data-dir="$HOME/.qawler/chrome-profile" "$URL_BASE"
curl -s http://127.0.0.1:9222/json/version      # sanidade
```

**M2 — Remoto (máquinas diferentes)** — a estação abre a janela e o servidor **anexa
pela rede** (porta CDP da estação), sem precisar de tela no servidor:

```
# na ESTAÇÃO (máquina do usuário) — helper qawler-login
/Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome \
  --remote-debugging-port=9222 --remote-debugging-address=0.0.0.0 \
  --user-data-dir="$HOME/.qawler/chrome-profile" "$URL_BASE"

# no SERVIDOR — a app anexa no endereço da estação
#  => ChromeOptions.setExperimentalOption("debuggerAddress", "<estação>:9222")
curl -s http://<estação>:9222/json/version
```

Onde `<estação>` é o endereço da máquina do usuário na rede que já existe (na casa do
Emanuel: a **tailnet** — ex.: `100.81.89.63`). Detalhes:

- A estação é informada no cadastro/na tela do teste (campo “endereço da estação”),
  com botão **“Testar conexão”** (`/json/version`) antes de abrir o crawl.
- **Túnel como alternativa mais restrita:** em vez de expor a porta, a estação sobe
  `ssh -R 9222:127.0.0.1:9222 <servidor>` e o servidor anexa em `127.0.0.1:9222`
  (nada exposto na rede; exige SSH na estação).
- **Selenium Grid (variante mais pesada):** a estação roda um node Selenium com Chrome
  visível e o servidor manda a sessão (`RemoteWebDriver`) — mais infra, só se M2 via CDP
  não bastar.
- **Modo `HEADLESS` atual continua** para alvos públicos (sem janela, sem estação).

### Consequências

- Se o servidor não tiver tela (Docker/Umbrel), **M2 é o caminho** — o assistido deixa de
  exigir rodar tudo no Mac.
- O perfil dedicado (`~/.qawler/chrome-profile`) passa a viver **na estação**; é ele que
  guarda os cookies para reuso/agendamento.
- **Segurança:** porta de debug = controle total do navegador. Exigir vínculo à interface
  da rede privada (tailnet), porta efêmera quando possível, encerrar a janela/porta depois
  do crawl e nunca expor na internet. Aviso explícito na tela.

### Helper na estação (proposto)

Script pequeno (`qawler-login.sh` / atalho no Mac) que: abre o Chrome com o perfil
dedicado e a porta de debug, imprime o endereço da estação para colar no QAwler e
encerra tudo com um comando (`qawler-login --stop`).

---

## 6. Critérios de sucesso (testáveis)

- [ ] Com `crawler.assistido` ativo, `POST /sistemas/{id}/sessao/abrir` abre o Chrome no
      alvo e `GET /sistemas/{id}/sessao` responde `aberta=true`.
- [ ] Login manual em `the-internet.herokuapp.com/login` (`tomsmith`) + “Concluí o login”
      → o crawl visita `/secure` e a aplicação lista as páginas/issues autenticadas.
- [ ] A janela **continua aberta** depois do crawl (sessão anexada não é encerrada).
- [ ] Segunda execução sem novo login reaproveita a sessão (perfil persistente).
- [ ] Sessão expirada → o teste vai para `AGUARDANDO_LOGIN` e a app pede nova janela.
- [ ] Nenhuma credencial gravada: `select credenciais_login, credenciais_senha from
      tb_sistema` retorna vazio após todo o fluxo.
- [ ] O modo `HEADLESS` (atual) continua funcionando para alvos públicos.
- [ ] **M2 (remoto):** com o QAwler em uma máquina e a **estação** em outra (ex.: QAwler na
      devbox, estação no Mac pela tailnet), o fluxo abre a janela na estação, o servidor
      confirma `/json/version`, anexa e conclui o crawl autenticado — sem tela no servidor.

---

## 7. Fronteiras

- **Sempre:** porta de debug em `127.0.0.1`; perfil dedicado (nunca o perfil real do
  usuário); `dominios_autorizados` + `autorizado_producao` respeitados; registrar no
  teste que houve intervenção humana.
- **Perguntar antes:** mudar schema (novo estado do `Teste`); adicionar dependência;
  trocar navegador/porte do fluxo.
- **Nunca:** gravar senha; usar o navegador/perfil pessoal; varrer PROD sem autorização
  explícita; `driver.quit()` em sessão anexada.

---

## 8. Perguntas abertas (preciso da sua decisão)

1. **Qual é a topologia?** Onde fica o QAwler e onde fica a estação — por exemplo QAwler
   no Mac + estação no Mac (M1), ou QAwler na devbox/Umbrel + estação no Mac (M2)? E no
   M2, a conexão deve ser **porta CDP na tailnet** (simples) ou **túnel SSH reverso**
   (nada exposto, mais restrito)?
2. **Navegador:** Chrome (recomendado — mesmo binário que você usa, com perfil dedicado
   do QAwler) ou Firefox?
3. **Fim do login:** botão **“Concluí o login”** (simples e à prova de SPA) ou detecção
   automática (some o formulário / muda a URL) — ou automática com o botão como escape?
4. **Campos de credencial do cadastro:** depreciar agora (recomendado, coerente com a
   decisão) ou manter só como anotação no cadastro?
5. **Escopo da v0.1:** só o fluxo manual (abrir → logar → varrer) ou já incluir o
   reaproveitamento de sessão para **agendamento** (RF-08)?

---

## 9. Etapas propostas (uma por vez, com OK)

1. **E1 — Sessão assistida no backend:** `SessaoAssistidaService` (abrir/verificar/anexar)
   + endpoints + estado `AGUARDANDO_LOGIN`.
2. **E2 — UI:** botões e status ao vivo nas telas `sistemas` e `teste-detalhe`;
   achados aparecendo na aplicação.
3. **E3 — Crawl autenticado:** `CrawlerService` aceitando a sessão anexada; detecção de
   muro de login (sessão expirada).
4. **E4 — Reuso para agendamento (RF-08):** perfil persistente + revalidação de sessão.
