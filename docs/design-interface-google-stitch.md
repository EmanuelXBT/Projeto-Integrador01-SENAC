# QAwler — Design da Interface (Prompt para Google Stitch)

> **⚠️ Superado — revisão v2 (21/09/2026):** ver `design-interface-qawler-v2.md` (sem o conceito de teia; baseada nos requisitos e telas atuais do sistema). Este arquivo permanece como registro histórico da v1.
> **Fonte:** `etapa1_novoprojeto_QAwler_EmanuelFilipeSilva.pdf` (Projeto Integrador I, v1.0)
> **Sistema:** QAwler — varredor automatizado de páginas web (HTML, JavaScript, responsividade, banco de dados) para ambientes DEV/homologação.
> **Objetivo deste arquivo:** servir de **prompt/spec de design** para gerar a interface visual do QAwler em ferramenta de design por IA (Google Stitch).

---

## 1. Conceito Visual Central

**A interface é uma teia de aranha viva.**

- O **input de URL** é o **centro absoluto da tela** — o coração da aplicação (RNF001: a inserção de URL é o elemento central da página inicial).
- A partir do **ponto central do input**, uma **teia de aranha (spider web / network graph)** se expande e **preenche toda a tela**, com linhas radiais e arcos concêntricos visíveis do fundo ao topo, bordas e cantos.
- O input fica **posicionado exatamente sobre o centro da teia** — o centro do input coincide com o centro geométrico da tela e com o ponto de origem de todas as linhas da teia.
- As **configurações do sistema** (scanners, viewports, timeout, autenticação) ficam **ao redor do centro**, organizadas como **nós da teia** ou painéis flutuantes dispostos simetricamente em volta do input.
- Metáfora: o QAwler é a aranha no centro da teia; cada linha leva a um ponto de verificação (nó da teia = scanner/configuração).

---

## 2. Layout Master (Tela Inicial / Home)

```
┌────────────────────────────────────────────────────────────────┐
│  ┌─── LOGO (topo esquerdo) ───┐        ┌─── USER MENU ───┐     │
│  QAwler · spider icon         │        avatar | histórico      │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │  ┌───────────────────────────────────────────────────┐  │   │
│  │  │                                                   │  │   │
│  │  │          ╱  ╲    (TEIA — linhas radiais + arcos)   │  │   │
│  │  │         ╱  NÓ  ╲        preenchendo a tela        │  │   │
│  │  │        │  *  *  │                                 │  │   │
│  │  │         ╲      ╱                                  │  │   │
│  │  │          ╲    ╱                                   │  │   │
│  │  │           ──●──  ← CENTRO: INPUT DE URL          │  │   │
│  │  │                                                   │  │   │
│  │  │     (configurações em nós ao redor do centro)     │  │   │
│  │  │                                                   │  │   │
│  │  └───────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                          │     │
│        [ status bar / métricas rápidas (rodapé) ]              │
└────────────────────────────────────────────────────────────────┘
```

**Hierarquia de atenção (do maior para o menor):**
1. **Input de URL** — elemento maior, mais brilhante, mais contrastado da tela.
2. **Nós de configuração ao redor** — visíveis, agrupados, mas visualmente secundários.
3. **Header (logo + usuário)** e **rodapé (status)** — discretos, não competem com o centro.

---

## 3. A Teia de Fundo (Elemento Assinatura)

### 3.1 Geometria
- **Origem:** exatamente no centro do input de URL (centro geométrico da viewport).
- **Linhas radiais:** 8 a 16 linhas retas partindo do centro em direção às bordas, distribuídas uniformemente (0°, 45°, 90°, 135°, 180°, 225°, 270°, 315° — ou subdivididas).
- **Arcos concêntricos:** 5 a 8 círculos/elipses concêntricos conectando as linhas radiais, espaçados progressivamente (mais densos perto do centro, mais abertos na periferia).
- **Preenchimento:** a teia ultrapassa as bordas da viewport (continua fora da tela) — sensação de que a teia é infinita e a viewport é apenas uma janela sobre ela.
- **Nós (intersecções):** pequenos pontos/pulsos nos cruzamentos linha×arco. Os nós mais próximos do centro são um pouco maiores.

### 3.2 Nós da teia = Scanners/Configurações
- Cada **módulo de scanner** (HTML, JavaScript, Responsividade, Banco de Dados) e cada **configuração principal** (timeout, viewports, autenticação assistida) é representado por um **nó destacado da teia**.
- Os nós destacados ficam **em um anel ao redor do input** (raio ~25–35% da tela), cada um conectado ao centro por sua própria linha radial.
- Nó ativo = **aceso** (cor de destaque); nó inativo = **apagado/cinza**.

### 3.3 Renderização e estética
- Renderizar preferencialmente como **SVG inline** (não imagem raster) para nitidez e animação.
- Estilo de linha: `stroke` fino (1–2px), cor em gradiente radial (mais opaca no centro, esmaecendo na periferia).
- **Brilho central:** um halo/glow suave atrás do input (radial-gradient blur) que dá a impressão de energia concentrada no centro.
- **Animação sutil (idle):** pulsação lenta dos nós (opacidade 0.3→1, ciclo 3–4s), linhas com leve "respiro" (stroke-opacity 0.2→0.5). Nunca frenético — a teia deve ser hipnótica, não distrair.
- **Durante varredura:** ondas de energia percorrem as linhas radiais do centro para a periferia (dashoffset animado), como se a aranha estivesse "enviando sinais" pelos fios. O nó do scanner em execução acende em sequência.

---

## 4. Componente Central — Input de URL

### 4.1 Posição e tamanho
- **Perfeitamente centralizado** (horizontal e verticalmente).
- Largura: ~720–760px em desktop (ou 40–50% da viewport, o que for menor). Altura: 64–72px.
- Raio de borda generoso: 16–20px (pill/rounded), coerente com o design.

### 4.2 Estrutura visual
- Campo de texto com **placeholder**: `https://site-para-testar.com.br` (ou "Cole o link do site a ser testado").
- **Ícone de globo/link** no lado esquerdo interno do campo.
- **Botão de ação** integrado no lado direito do campo: "**Varrer**" (ou ícone de aranha/play) com cor de destaque — preenche o canto direito do campo (padrão de barra de busca, estilo Google/Spotify).
- Enviar com **Enter** também dispara a varredura (fluxo do PDF: "Ao pressionar Enter, o sistema inicia automaticamente o processo de varredura").

### 4.3 Estados do input
| Estado | Visual |
|---|---|
| **Idle** | Campo em vidro fosco (glassmorphism) sobre a teia, borda 1px sutil, placeholder cinza-claro, fundo escuro translúcido |
| **Hover** | Borda clareia, glow leve |
| **Foco** | Borda e glow na cor de destaque (ciano/verde), halo do centro da teia intensifica (a teia parece "acordar") |
| **Preenchido** | URL em texto claro legível, ícone de cadeado para HTTPS |
| **Validando URL** | Spinner fino dentro do campo |
| **Varredura em andamento** | Barra de progresso linear fina na borda inferior do campo; botão "Varrer" vira "Cancelar" (RF012) |
| **Erro (URL inválida/inacessível)** | Borda vermelha, glow vermelho, mensagem de erro discreta abaixo do campo com shake sutil |

### 4.4 Textos auxiliares
- Subtítulo logo abaixo do input (1 linha): "**Informe a URL e o QAwler varre HTML, JavaScript, responsividade e banco de dados.**"
- Hint minimalista acima do input: "**Testador universal de páginas web**" (tagline).

---

## 5. Configurações ao Redor (Organizadas)

As configurações ficam **em volta** do input, nunca dentro dele. Duas camadas de organização:

### 5.1 Anel de Scanners (primeiro anel, mais próximo do input)
Quatro **nós da teia clicáveis**, em cruz ou em losango ao redor do input (topo, direita, baixo, esquerda — ou nos 45°):

| Nó | Scanner | Ícone |
|---|---|---|
| ↑ | **HTML** — estrutura, tags, acessibilidade | `</>` |
| → | **JavaScript** — console, exceções, warnings | `JS` |
| ↓ | **Responsividade** — 320/768/1024/1440px | `▭▭` (múltiplos retângulos) |
| ← | **Banco de Dados** — vazamentos SQL/debug | `🛢` / `DB` |

- Cada nó é um **badge circular** (~56–64px) com ícone + label curto abaixo.
- **Toggle on/off:** clique alterna ativo/inativo (nó aceso vs apagado).
- Tooltip ao passar o mouse com descrição do scanner (RF004–RF007).
- **Nós padrão: todos ativos** (varredura completa por padrão — RNF008 "mínimo de configuração").

### 5.2 Painéis de Configuração (segundo anel / cantos da tela)
Configurações avançadas em **painéis compactos flutuantes**, posicionados simetricamente nos cantos ou nas laterais médias, sempre com título curto:

| Painel | Conteúdo |
|---|---|
| **Viewports** | Checkboxes: 320px · 768px · 1024px · 1440px (padrão: todos marcados) |
| **Limites** | Timeout (padrão 5 min — regra de negócio do PDF), profundidade de crawl, limite de páginas |
| **Throttling** | Slider de requisições/segundo (RNF003 — não sobrecarregar o site alvo) |
| **Autenticação** | Toggle "Autenticação assistida" (captcha/login — RF003), descrição: "pausa e aguarda intervenção manual" |
| **Saída** | Formato do relatório (JSON estruturado — RF011, padrão; PDF opcional) |

- Painéis **colapsáveis** (seta de expandir/recolher), nunca sobrepõem o input central.
- Estilo consistente com o input: glassmorphism, borda sutil, cantos arredondados.
- No mobile, os painéis colapsam em um **botão "Configurações"** que abre um sheet/accordion (a tela nunca fica poluída).

### 5.3 Ordem visual
`Header` → `Anel de scanners` → `INPUT CENTRAL` → `Painéis de limites/avançado` → `Rodapé de status`.

---

## 6. Paleta de Cores (Dark Theme — "Modo Aranha")

Tema escuro por padrão (ferramenta de QA/crawler = ambiente técnico, dark-first).

| Uso | Cor (sugestão) |
|---|---|
| Fundo principal | `#0A0E14` (quase preto azulado) |
| Fundo da teia (linhas) | gradiente `#1E3A5F` → `#0A0E14` (azul-profundo esmaecendo) |
| Linhas da teia | `rgba(90, 180, 255, 0.25)` — azul-gelo, fino |
| Nós da teia (ativos) | `#22D3EE` (ciano) / `#34D399` (verde-ácido) |
| Nós inativos | `#3A4250` (cinza-azulado) |
| Cor de destaque (CTA, foco) | `#22D3EE` (ciano) + glow `rgba(34, 211, 238, 0.35)` |
| Acento secundário | `#34D399` (verde — "scan ok") |
| Erro | `#F87171` (vermelho suave) |
| Texto primário | `#E5EAF2` |
| Texto secundário | `#8B93A7` |
| Superfícies (glass) | `rgba(255, 255, 255, 0.06)` + borda `rgba(255, 255, 255, 0.10)` + backdrop-blur |

> Alternativa de tema claro (opcional): fundo `#F4F7FB`, teia azul `rgba(30, 90, 160, 0.12)`, destaque `#0284C7`.

---

## 7. Tipografia

- **Interface:** fonte técnica e limpa — **Inter** ou **Space Grotesk** (títulos) + **Inter** (corpo).
- **Mono (URLs e relatórios):** **JetBrains Mono** ou **IBM Plex Mono** — a URL digitada e dados técnicos aparecem em mono, reforçando o caráter de ferramenta dev.
- Escala: título/logo 18–20px · tagline 14px · input 18–20px · labels de nó 12px · textos auxiliares 12–13px.

---

## 8. Componentes Complementares (mesma linguagem visual)

1. **Header:** logo QAwler (ícone de aranha estilizada + wordmark) à esquerda; à direita, menu do usuário (avatar, nome, perfil — Administrador/Analista/Visualizador, histórico).
2. **Rodapé/status bar:** contador de varreduras em execução, últimas varreduras (RF010), link para dashboard de métricas (RF013).
3. **Modal de autenticação assistida (RF003):** janela/iframe sobre a teia (a teia continua visível desfocada ao fundo), para login manual/captcha — com texto "Pausamos a varredura: o site exige autenticação. Complete o login e a varredura continua."
4. **Tela de resultados/relatório:** herda a teia como header decorativo (centro = resumo do scan: `scan_id`, `target_url`, `duration_ms`, total de erros por categoria) e lista de erros em mono com severidade (erro/warning/info).
5. **Histórico por site:** lista com data, horário, status (sucesso/erro/cancelado) e botão de download JSON (RF011).
6. **Dashboard de métricas (RF013):** cards com totais (varreduras, erros por tipo, sites mais problemáticos).

---

## 9. Micro-interações / Animações

| Trigger | Animação |
|---|---|
| Página carrega | Teia "tece" da periferia para o centro em ~1.2s (linhas aparecem do exterior em direção ao input), input faz fade+scale-in por último |
| Foco no input | Halo central acende; nós do anel de scanners ganham brilho sequencial (um a um, no sentido horário) |
| Scanner ativado/desativado | Nó pisca 1x e muda de cor |
| Enter / clique em "Varrer" | Pulso expansivo no centro (ripple da teia); onda de energia percorre as linhas radiais; o input "vira" barra de progresso |
| Scan concluído | Nós piscam em verde; aparecem números-voando (erros por scanner) até os painéis laterais |
| Erro | Shake sutil no input + glow vermelho no centro da teia |
| Preferência: **reduzir movimento** | Respeitar `prefers-reduced-motion` — sem animações, apenas transições de estado |

---

## 10. Responsividade

- **Desktop (≥1200px):** teia completa, input 720px, anel de scanners completo, painéis laterais visíveis.
- **Tablet (768–1199px):** input 520px; painéis laterais colapsam em ícones; teia mantém 12 raios.
- **Mobile (<768px):** input ocupa ~90% da largura; anel de scanners vira **carrossel horizontal** abaixo do input; demais configurações atrás do botão "Configurações" (bottom sheet); teia reduzida a 8 raios como textura de fundo sutil (nunca poluir o contraste do input).

---

## 11. Acessibilidade

- Contraste AA (texto primário sobre fundo escuro).
- Foco visível (outline 2px na cor de destaque) em todos os interativos.
- Input com `label` visível ou `aria-label="URL do site a ser testado"`.
- Nós de scanner como `<button>` reais, com `aria-pressed` para estado ativo/inativo.
- Todas as animações com alternativa estática (via `prefers-reduced-motion`).
- Mensagens de erro com `role="alert"`.

---

## 12. Prompt Resumido (para colar na ferramenta)

> Crie a interface web inicial do **QAwler**, um varredor automatizado de sites para QA (testa HTML, JavaScript, responsividade e banco de dados em páginas web de dev/homologação). Dark theme, visual tech/clean, glassmorphism.
>
> **Elemento central:** um grande campo de input de URL (ex.: "https://site-para-testar.com.br") perfeitamente centralizado na tela, com ícone de globo à esquerda e botão "Varrer" integrado à direita, cantos arredondados e glow ciano.
>
> **Fundo:** uma **teia de aranha** (spider web) feita de linhas radiais e arcos concêntricos em SVG, **originando-se exatamente no centro do input** e preenchendo toda a tela até as bordas, esmaecendo na periferia, com nós pulsantes nos cruzamentos e um halo de luz atrás do input. O input é o centro absoluto da teia.
>
> **Ao redor do input, de forma organizada:** 4 nós de scanner clicáveis (HTML, JavaScript, Responsividade, Banco de Dados) dispostos simetricamente em anel ao redor do centro, com ícones e toggle on/off; painéis compactos flutuantes nas laterais/cantos para viewports (320/768/1024/1440px), timeout e limites, throttling de requisições, toggle de autenticação assistida e formato de relatório (JSON/PDF). Header discreto com logo QAwler e menu do usuário; rodapé com status das varreduras.
>
> **Cores:** fundo #0A0E14, teia azul-gelo, destaque ciano #22D3EE, verde #34D399 para sucesso, vermelho #F87171 para erro. Tipografia Inter + JetBrains Mono para URLs. Animações sutis: pulsação dos nós, onda de energia nas linhas durante a varredura, respeitando prefers-reduced-motion. Responsivo: desktop com teia completa, mobile com input largo e configurações em sheet.

---

*Arquivo gerado para servir de spec de design. Ajustes de cores, proporções e posicionamento podem ser refinados após a primeira renderização.*
