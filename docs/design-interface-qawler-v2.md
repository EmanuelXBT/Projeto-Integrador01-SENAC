# QAwler — Design da Interface (Revisão v2)

> **Versão:** v2 — 21/09/2026
> **Substitui:** `design-interface-google-stitch.md` (v1, 04/08/2026, baseado na etapa 1 / v1.0 do PI-1)
> **Base atual:** `REQUISITOS.md` (RF-01…RF-17 · RNF-01…RNF-11), telas implementadas (Thymeleaf/Bootstrap), enums do domínio e a família visual QAwler (tokens compartilhados com o desktop da UC15).
> **Decisão de 21/09/2026:** conceito "teia de aranha" **descartado** — nova direção: interface sóbria de ferramenta técnica de QA.
> **Uso:** esta é a especificação de design. A versão pronta para upload no Stitch está em `.stitch/DESIGN.md`.

---

## 1. Melhorias aplicadas (v1 → v2)

| # | v1 (04/08, base etapa 1 v1.0) | v2 (21/09, sistema atual) |
|---|---|---|
| 1 | Conceito "teia de aranha viva" (elemento assinatura decorativo) | Painel técnico sóbrio: hierarquia por layout, tipografia e cor funcional — sem metáfora decorativa |
| 2 | "Testador universal" de uma única URL | SaaS completo: conta → sistemas cadastrados → execuções (manual/agendada) → resultados com evidências → relatórios |
| 3 | Scanners: HTML · Responsividade · Banco de Dados (aspiracional) | Scanners implementados: **links quebrados (HTTP 4xx/5xx)** · **erros de JavaScript** · **imagens quebradas** |
| 4 | Referências RF001–RF013 (numeração antiga) | **RF-01…RF-17** e **RNF-01…RNF-11** (documento atual de requisitos) |
| 5 | Tela única (home com input central) | **5 telas**: Login · Dashboard · Sistemas · Detalhe do teste · Relatório (+ criar conta, recuperar senha, modal de sistema, agendamento) |
| 6 | — | Estados e domínio reais na interface: fila/executando/concluída/falhou, severidades, ambientes, tipos de bug |
| 7 | Paleta em sugestão ("Modo Aranha") | Tokens **formalizados na família QAwler/UC15** (mesmos hexadecimais), com papéis definidos |
| 8 | Prompt único (para colar) | **Prompt base + prompts por tela** no formato do Stitch, prontos para geração |
| 9 | Acessibilidade e micro-interações | Mantidas (WCAG AA, foco visível, `prefers-reduced-motion`) e reancoradas em estados reais de sistema |

Itens mantidos da v1: tema escuro técnico (dark-first), tipografia Inter + JetBrains Mono, atenção a estados de campo, contraste AA, animações discretas.

---

## 2. Visão geral do sistema

**QAwler** é um SaaS de QA automatizado para pequenas equipes de software: o usuário cadastra a URL de um sistema web, dispara ou agenda uma varredura, e recebe um relatório de bugs com evidências (screenshots).

**Fluxo do usuário:**
1. Cria a conta / entra (RF-01, RF-02, RF-03).
2. Cadastra um **sistema** (nome, URL base, ambiente, credenciais opcionais) (RF-04).
3. Dispara um **teste manual** (RF-07) ou configura **agendamento** (diário/semanal) (RF-08).
4. O **crawler** percorre as páginas (profundidade padrão: 2 níveis) (RF-09) e os scanners registram: links quebrados (RF-10), erros de JavaScript (RF-11) e imagens quebradas; com screenshots (RF-12).
5. Acompanha a **execução** (fila → executando → concluída/falhou) e vê os **resultados** (RF-13), o **detalhe de cada bug** (RF-15) e o **histórico** (RF-14).
6. **Exporta o relatório em PDF** (RF-16) e recebe **notificação por e-mail** quando há bugs ou falha (RF-17).

**Domínio visível na interface (valores reais do sistema):**

| Conceito | Valores | Rótulos na interface |
|---|---|---|
| Status da execução | `QUEUED` `RUNNING` `COMPLETED` `FAILED` | Em fila · Executando · Concluída · Falhou |
| Severidade do bug | `CRITICAL` `HIGH` `MEDIUM` `LOW` | Crítica · Alta · Média · Baixa |
| Tipo de bug | `HTTP_ERROR` `JS_ERROR` `BROKEN_IMAGE` | HTTP · JavaScript · Imagem quebrada |
| Ambiente | `DEV` `STAGING` `PROD` | DEV · STAGING · PROD |
| Agendamento | `DIARIO` `SEMANAL` | Diário · Semanal |
| Disparo | `MANUAL` `AGENDADO` | Manual · Agendado |

---

## 3. Tema e atmosfera

- **Escuro técnico, sóbrio.** Ferramenta de QA = ambiente de trabalho dev; o tema escuro é o padrão da família QAwler (herdado do PI-1 e consolidado na UC15).
- **Camadas de superfície** em vez de sombras: `#0A0E14` (fundo) → `#101722` (barras/cabeçalhos/campos) → `#141B26` (cartões) → `#1B2431` (elevado/hover). Bordas sutis `#2A3446` fazem a separação.
- **Destaque ciano `#22D3EE` com parcimônia**: ação primária, foco, link e o estado "Executando". Nada de brilhos ou gradientes decorativos.
- **Densidade média**: tela de ferramenta — informação organizada, mas com respiro (espaçamento base de 8px; seções de 24–32px).
- **Cantos discretos**: campos/botões 6px, cartões 10px.
- Sem elementos gimmick: sem teia, sem animações de fundo; movimento só para comunicar estado.

---

## 4. Cores e papéis (tokens da família QAwler)

| Token | Hex | Uso na interface web |
|---|---|---|
| `fundo_janela` | `#0A0E14` | Fundo da página |
| `superficie_cabecalho` | `#101722` | Barra superior, cabeçalho de tabela, fundo de campos |
| `superficie` | `#141B26` | Cartões e painéis |
| `superficie_alt` | `#1B2431` | Hover de linha/cartão, superfície elevada |
| `borda` | `#2A3446` | Bordas padrão |
| `borda_forte` | `#3A4557` | Bordas em hover/seleção |
| `borda_campo` | `#606E8E` | Contorno de campos de formulário |
| `grade_tabela` | `#212B3B` | Linhas de grade de tabelas |
| `linha_alternada` | `#172030` | Zebra de tabelas |
| `texto_primario` | `#E5EAF2` | Texto principal |
| `texto_secundario` | `#A6AEC2` | Texto de apoio, rótulos |
| `texto_desabilitado` | `#7C8799` | Estados desabilitados |
| `destaque` | `#22D3EE` | Ação primária, foco, links, estado "Executando" |
| `sobre_destaque` | `#06272E` | Texto/ícone sobre superfícies de destaque |
| `foco` | `#22D3EE` | Anel de foco (2px) |
| `sucesso` | `#34D399` | Concluída, confirmações |
| `alerta` | `#FBBF24` | Avisos |
| `erro` | `#F87171` | Erros, falhas, ações destrutivas |
| `info` | `#60A5FA` | Informação, links neutros |
| `severidade_critica` | `#F87171` | Chip Crítica |
| `severidade_alta` | `#FB923C` | Chip Alta |
| `severidade_media` | `#FBBF24` | Chip Média |
| `severidade_baixa` | `#60A5FA` | Chip Baixa |
| `selecao` | `#1E3A5F` | Item ativo de menu / seleção de texto |

*Contraste: os pares texto×fundo da família foram verificados em WCAG AA (21/21 aprovados no desktop; os mesmos pares valem para a web).*

---

## 5. Tipografia

- **Interface:** Inter (400/500/600) · **Mono:** JetBrains Mono — para URLs, IDs de teste, trechos de código e dados técnicos.
- **Escala web:**

| Papel | px | Peso |
|---|---|---|
| Título de página | 22 | 600 |
| Título de seção | 15 | 600 |
| Corpo / rótulo | 14 | 400 |
| Tabela | 13 | 400 |
| Auxiliar / metadado | 12 | 400 |
| Mono (URL, ID) | 13 | 400 |

- Entrelinha 1.45; `letter-spacing` normal; números tabulares em colunas de dados.

---

## 6. Componentes

1. **Botões** — Primário: fundo `#22D3EE`, texto `#06272E`, raio 6px, altura 36–40px. Secundário: contorno `#2A3446`, texto `#E5EAF2`. Discreto/ghost: só texto. Destrutivo: contorno `#F87171` (confirmar em modal). Foco: anel 2px `#22D3EE`. Desabilitado: `#7C8799`, sem hover.
2. **Campos** — Fundo `#101722`, contorno `#606E8E` (hover `#3A4557` → foco `#22D3EE`), raio 6px, altura 38–40px, rótulo 13px `#A6AEC2`. URLs e IDs em mono. Mensagem de erro abaixo do campo (`#F87171`, `role="alert"`).
3. **Chips/badges** — Formato pílula, texto 12px. Status: Em fila (`#8B93A7` neutro), Executando (`#22D3EE`, pulsação sutil), Concluída (`#34D399`), Falhou (`#F87171`). Severidade: cores da tabela §4. Ambiente: contorno neutro. Tipo de bug: neutro com ícone.
4. **Tabela técnica** — Cabeçalho `#101722` (texto 12px, maiúsculas, espaçamento leve), linhas zebra `#172030` + `#141B26`, grade `#212B3B`, hover `#1B2431`, altura de linha ~40px. Coluna de ações alinhada à direita.
5. **Cartões KPI** — Número grande (28px/600) + rótulo 12px `#A6AEC2`; barra de status/severidade como acento fino quando fizer sentido.
6. **Cabeçalho e navegação (shell)** — Barra de 64px: marca QAwler (símbolo geométrico simples + wordmark) à esquerda; abas **Dashboard · Sistemas · Testes · Relatórios**; à direita, menu do usuário (nome, sair). Aba ativa com indicador `#22D3EE`.
7. **Modais** — Fundo `#141B26`, raio 10px, título 15px/600, ações no rodapé (Cancelar à esquerda, primária à direita). Confirmação obrigatória para ações destrutivas (Desativar sistema).
8. **Alertas/toasts** — Mensagem inline no topo do conteúdo para erros de operação; toast discreto para confirmações ("Teste #12 enfileirado"). Cores funcionais §4.
9. **Estados vazios** — Cartão central com frase curta + ação primária ("Nenhuma execução ainda — cadastre um sistema").
10. **Micro-interações** (sem teia) — Transições de 120–200ms em hover/foco; chip "Executando" com pulsação lenta; esqueleto de carregamento em tabelas; respeito a `prefers-reduced-motion`.

---

## 7. Telas

### 7.1 Login (+ conta)
- **Objetivo:** entrar na conta (RF-02); atalhos para criar conta (RF-01) e recuperar senha (RF-03).
- **Estrutura:** cartão centralizado (~420px) sobre o fundo; marca no topo; "Entrar na sua conta"; campos E-mail e Senha (com mostrar/ocultar); botão primário "Entrar" em largura total; links "Esqueci minha senha" e "Criar conta"; rodapé discreto com versão.
- **Estados:** carregando (spinner no botão), credenciais inválidas (alerta `role="alert"`), sucesso → Dashboard.

### 7.2 Dashboard
- **Objetivo:** visão geral (RF-13, RF-14).
- **Estrutura:** título "Dashboard" + subtítulo; **4 cartões KPI**: Em fila · Em execução · Concluídas · Falhas; seção "Execuções recentes" — tabela: ID (mono), Sistema, Status (chip), Iniciado em, Duração, Ações ("Ver detalhes"); link "Ver todas".
- **RFs:** RF-07 (últimas execuções), RF-13, RF-14.
- **Estados:** vazio (chamada para cadastrar sistema); carregando (esqueleto).

### 7.3 Sistemas
- **Objetivo:** gerenciar sistemas monitorados (RF-04, RF-05, RF-06) e disparar/agendar testes (RF-07, RF-08).
- **Estrutura:** título "Sistemas monitorados" + busca + botão primário "Novo sistema"; tabela: Nome, URL (mono), Ambiente (chip), Último teste (data + chip de status), Ações ("Executar teste" primário discreto, "Agendar", "Editar", "Desativar").
- **Modal "Novo sistema":** Nome, URL base, Ambiente (seleção), Descrição; seção avançada "Credenciais de acesso" (login/senha opcionais — RF-04) e "Domínios autorizados"; rodapé Cancelar/Salvar.
- **Agendamento (RF-08):** diálogo com frequência (Diário/Semanal), horário, dia da semana (quando semanal) e próxima execução calculada.

### 7.4 Detalhe do teste
- **Objetivo:** resultado de uma execução (RF-13) com evidências (RF-12, RF-15).
- **Estrutura:** cabeçalho "Teste #12 — <Sistema>" + chip de status + metadados (iniciado em, duração, disparo manual/agendado); ações "Reexecutar" (RF-07) e "Baixar PDF" (RF-16); cartões-resumo: Páginas visitadas · Bugs por severidade (contadores por chip); seção "Páginas visitadas": URL (mono), HTTP (chip 200/404/500), Tempo, Título; seção "Bugs encontrados": Tipo, Severidade (chip), URL (mono), Mensagem, Linha/Coluna, Detectado em, miniatura da evidência com ação "Ver" (RF-12).
- **Estados:** em execução (progresso + atualização), falhou (mensagem `erroMensagem`), concluída.

### 7.5 Relatório
- **Objetivo:** visão consolidada e exportação (RF-13, RF-14, RF-16).
- **Estrutura:** título "Relatório de teste" + resumo (sistema, período, totais por severidade); filtros (tipo, severidade); tabela: #, Tipo, Severidade, Descrição, Página, Detectado em; ações "Baixar PDF" (RF-16) e exportação estruturada; estado de geração do PDF.
- **Notificações (RF-17):** não é tela — e-mail transacional (bugs encontrados / falha). Documentar no rodapé do relatório a origem do envio quando aplicável.

---

## 8. Layout e responsividade

```
┌──────────────────────────────────────────────────────────┐
│ QAwler    Dashboard · Sistemas · Testes · Relatórios  👤 │  64px
├──────────────────────────────────────────────────────────┤
│  Título da página                          [ ação primária ]
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐          │
│  │  KPI    │ │  KPI    │ │  KPI    │ │  KPI    │          │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘          │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  Tabela (largura total, ações à direita)             │ │
│  └──────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

- **Desktop (≥1200px):** container de 1200px; grade de 12 colunas; KPIs em 4 colunas.
- **Tablet (768–1199px):** KPIs em 2 colunas; tabelas com rolagem horizontal se necessário.
- **Mobile (<768px):** navegação em menu recolhido; KPIs empilhados; tabelas viram listas de cartões; ações em menu de contexto.

---

## 9. Acessibilidade

- Contraste **WCAG 2.1 AA** em todos os pares texto×fundo (família verificada).
- Foco visível (anel 2px `#22D3EE`) em todos os interativos; navegação por teclado completa.
- Formulários com `<label>` associado; erros com `role="alert"`; chips de status com texto (nunca só cor).
- Imagens de evidência com texto alternativo ("Captura da página X em 21/09/2026").
- `prefers-reduced-motion` respeitado em todas as animações.

---

## 10. Prompts para geração no Stitch

**Regra de ouro:** os prompts descrevem **somente estrutura, conteúdo e intenção** — nunca cores, fontes ou cantos (o sistema de design do projeto já os aplica). Idioma: português do Brasil.

**Prompt base (contexto do produto):**
> "QAwler é um SaaS de QA automatizado para pequenas equipes de software. O usuário cadastra a URL de um sistema web, dispara ou agenda uma varredura e recebe um relatório de bugs — links quebrados, erros de JavaScript e imagens quebradas — com screenshots como evidência. A interface é uma ferramenta técnica, densa em informação, em português do Brasil, com estética escura e sóbria definida pelo sistema de design do projeto. Todas as telas são web, desktop-first e responsivas."

**10.1 Login:** "Tela de login centralizada: cartão compacto com a marca QAwler no topo, título 'Entrar na sua conta', campos E-mail e Senha (com ação de mostrar/ocultar), botão primário 'Entrar' em largura total, links 'Esqueci minha senha' e 'Criar conta', rodapé discreto. Sem elementos decorativos."

**10.2 Dashboard:** "Página inicial pós-login: barra superior com navegação (Dashboard, Sistemas, Testes, Relatórios) e menu do usuário. Conteúdo: título 'Dashboard' e subtítulo; quatro cartões de estatística — 'Em fila', 'Em execução', 'Concluídas', 'Falhas'; seção 'Execuções recentes' com tabela de ID, Sistema, Status, Iniciado em, Duração e ação 'Ver detalhes'; link 'Ver todas'."

**10.3 Sistemas:** "Página de gestão de sistemas monitorados: título com botão primário 'Novo sistema' e campo de busca; tabela com Nome, URL em fonte mono, Ambiente, Último teste (data e status) e ações Executar teste, Agendar, Editar, Desativar. Modal 'Novo sistema' com Nome, URL base, Ambiente, Descrição e seção avançada de credenciais de acesso e domínios autorizados."

**10.4 Detalhe do teste:** "Página de resultado de uma execução: cabeçalho 'Teste #12 — Sistema' com status e metadados (início, duração, disparo); botões 'Reexecutar' e 'Baixar PDF'; cartões-resumo com páginas visitadas e contagem de bugs por severidade; tabela 'Páginas visitadas' (URL em mono, status HTTP, tempo, título); lista 'Bugs encontrados' com tipo, severidade, URL, mensagem, linha e miniatura de evidência."

**10.5 Relatório:** "Página de relatório consolidado: título 'Relatório de teste' com resumo por severidade; filtros por tipo e severidade; tabela numerada de bugs (tipo, severidade, descrição, página, data); botões 'Baixar PDF' e exportar; indicador de geração do PDF."

---

## 11. Fluxo de produção (Stitch → Figma)

1. **Upload** do `.stitch/DESIGN.md` no projeto Stitch (script oficial `screens:batchCreate`, sem passar pelo limite de tokens do modelo).
2. **Criação do design system** (`create_design_system_from_design_md`) — ⚠️ **checkpoint: requer sua aprovação** (nome, cores, fontes, raio) antes do upload.
3. **Geração das telas** com os prompts §10 (consome a cota mensal do Stitch).
4. Ajustes com `edit_screens` / `generate_variants` se necessário.
5. **Estruturação no Figma** (MCP do Figma): telas como referência editável, estilos e protótipo.
6. Documentação e propagação (vault / junção) após aprovação.

---

## 12. Pendências e decisões abertas

- [x] Aprovação do design system pelo Emanuel (checkpoint do fluxo Stitch) — aprovado em 21/09/2026.
- [x] Criar o projeto "QAwler" no Stitch e executar upload + geração — executado em 21/09/2026 (§13).
- [ ] Definir se o relatório mantém o nome "Relatório de teste" ou um formato com período consolidado por sistema.
- [x] Variante em inglês do DESIGN.md — não necessária (interpretação fiel em PT-BR).
- [ ] Atualizar `senac/QAwler.md` no vault (ainda descreve o conceito v1) e propagar esta revisão.

---

## 13. Registro de geração (Stitch → Figma — 21/09/2026)

- **Stitch**: projeto `QAwler` (`9770563991880524861`) — design system "QAwler Technical System" (`assets/a973402662734980b442c98b016ca473`; fidelidade DARK · Inter + JetBrains Mono · ciano #22D3EE) criado a partir do `.stitch/DESIGN.md`.
- **Telas (5)**: Login · Dashboard · Sistemas monitorados · Detalhe do teste · Relatório — PNGs + HTMLs em `.stitch/screens/`; IDs e hashes em `.stitch/metadata.json`. Login re-editado (removidas telemetria/textos em inglês; rodapé "QAwler · v1.0").
- **Figma**: "QAwler — Interface Web (v2)" — páginas **Telas** (5 pranchas + reações de protótipo em cadeia), **Sistema de Design** (tokens + tipografia + fundamentos) e **Fluxo** (miniaturas + sequência): https://www.figma.com/design/vChq2ocLkh97zAkq2IZJtK
- **Revisão local (Mac)**: `/opt/data/dev/senac/uc05/design_stitch/` (junção devbox).

---

*Revisão v2 preparada em 21/09/2026 (atualizada no mesmo dia com o registro de geração — §13). v1 (`design-interface-google-stitch.md`) permanece como registro histórico.*
