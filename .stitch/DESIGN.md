# Design System: QAwler

**Project ID:** a definir (registrado no upload ao Stitch)

## 1. Visual Theme & Atmosphere

O QAwler é uma ferramenta técnica de QA — e a interface assume isso. A atmosfera é **sóbria, precisa e profissional**: um painel de trabalho escuro, com hierarquia clara e movimento contido. Nada decorativo compete com a informação: os dados (páginas visitadas, bugs, severidades, evidências) são os protagonistas, apresentados em camadas de superfície discretas, com bordas sutis no lugar de sombras.

A sensação é de **controle e confiabilidade** — um instrumento de engenharia, não um produto de consumo. O azul-noite profundo do fundo cria foco; o ciano técnico marca com parcimônia apenas o que importa: a ação primária, o foco de teclado e o estado "executando".

**Características principais:**
- Tema escuro em quatro camadas de superfície, com bordas sutis como separação
- Destaque ciano reservado para ação, foco e estado ativo — nunca decorativo
- Densidade média: informação organizada com respiro (base de 8px)
- Cantos discretos (6–10px) e ausência de sombras fortes
- Tipografia técnica: Inter para interface, JetBrains Mono para URLs, IDs e dados
- Movimento só para comunicar estado (120–200ms), com `prefers-reduced-motion` respeitado

## 2. Color Palette & Roles

### Fundação (superfícies)
- **Azul-noite absoluto** (#0A0E14) – Fundo principal da página.
- **Azul-noite de barra** (#101722) – Barra superior, cabeçalhos de tabela e fundo de campos.
- **Ardósia profunda** (#141B26) – Superfície padrão de cartões e painéis.
- **Ardósia elevada** (#1B2431) – Superfície elevada: hover de linhas/cartões.

### Contornos e separação
- **Borda sutil** (#2A3446) – Bordas padrão.
- **Borda média** (#3A4557) – Bordas em hover/seleção.
- **Borda de campo** (#606E8E) – Contorno de campos de formulário.
- **Grade de tabela** (#212B3B) – Linhas de grade internas.
- **Linha alternada** (#172030) – Fundo alternado (zebra) de tabelas.

### Texto
- **Branco-neblina** (#E5EAF2) – Texto principal.
- **Cinza-azulado claro** (#A6AEC2) – Texto secundário, rótulos, metadados.
- **Cinza-azulado apagado** (#7C8799) – Texto desabilitado.

### Acento e interação
- **Ciano técnico** (#22D3EE) – Ação primária, foco de teclado, links ativos e estado "Executando".
- **Ciano profundo** (#06272E) – Texto e ícones sobre superfícies ciano.
- **Azul de seleção** (#1E3A5F) – Item ativo de navegação e seleção de texto.

### Estados funcionais
- **Verde-menta** (#34D399) – Sucesso: execução concluída, confirmações.
- **Âmbar** (#FBBF24) – Avisos.
- **Coral** (#F87171) – Erros, falhas e ações destrutivas.
- **Azul informativo** (#60A5FA) – Informação neutra.

### Severidade de bugs
- **Crítica** (#F87171) · **Alta** (#FB923C) · **Média** (#FBBF24) · **Baixa** (#60A5FA)

## 3. Typography Rules

**Família principal:** Inter (interface) · **Mono:** JetBrains Mono (URLs, IDs de teste, trechos de código).

**Caráter:** sans-serif neutra e legível, com números tabulares para colunas de dados; a mono reforça o caráter de ferramenta técnica nos dados.

### Hierarquia e pesos
- **Título de página:** peso 600, 22px, entrelinha 1.3.
- **Título de seção:** peso 600, 15px.
- **Corpo e rótulos:** peso 400–500, 14px, entrelinha 1.45.
- **Tabelas:** peso 400, 13px; cabeçalho 12px em caixa alta com espaçamento leve.
- **Metadados/auxiliares:** peso 400, 12px.
- **Dados técnicos (mono):** peso 400, 13px — URLs, IDs, códigos.

**Princípios de espaçamento:** ritmo vertical consistente (8/12/16/24/32/48); títulos próximos do conteúdo; seções separadas por 24–32px.

## 4. Component Stylings

### Botões
- **Forma:** cantos discretamente arredondados (6px), altura 36–40px.
- **Primário:** fundo ciano técnico (#22D3EE) com texto ciano profundo (#06272E); hover com leve clareamento; foco com anel de 2px.
- **Secundário:** fundo transparente com contorno de borda padrão (#2A3446) e texto principal; hover eleva a superfície.
- **Destrutivo:** contorno coral (#F87171), exige confirmação em modal.
- **Desabilitado:** texto apagado (#7C8799), sem hover.

### Cartões e contêineres
- **Cantos:** 10px de raio.
- **Fundo:** ardósia profunda (#141B26) com borda sutil de 1px (#2A3446).
- **Profundidade:** plana por padrão; hover apenas eleva o fundo (#1B2431) — sem sombras pesadas.

### Campos e formulários
- **Contorno:** 1px em borda de campo (#606E8E); foco em ciano (#22D3EE).
- **Fundo:** azul-noite de barra (#101722); raio de 6px; altura 38–40px.
- **Rótulos:** 13px em texto secundário; erros abaixo do campo em coral com aviso acessível.
- **Dados técnicos em mono:** campos de URL usam JetBrains Mono.

### Tabelas
- Cabeçalho em superfície #101722, texto 12px em caixa alta espaçada.
- Linhas zebradas (#172030 sobre #141B26), grade sutil (#212B3B), hover (#1B2431).
- Coluna de ações alinhada à direita.

### Chips e badges
- Formato pílula, texto 12px. Status sempre com texto (nunca só cor): "Em fila" neutro, "Executando" ciano com pulsação lenta, "Concluída" verde-menta, "Falhou" coral. Severidade com as cores dedicadas; ambiente com contorno neutro.

## 5. Layout Principles

- **Contêiner:** 1200px máximo, centralizado; grade de 12 colunas.
- **Shell:** barra superior de 64px com marca, abas de navegação (Dashboard · Sistemas · Testes · Relatórios) e menu do usuário; indicador ciano na aba ativa.
- **Ritmo:** espaçamento base de 8px; seções separadas por 24–32px; títulos próximos ao conteúdo que descrevem.
- **Densidade média:** alinhamento à esquerda para leitura; ações à direita; hierarquia por tamanho e peso, não por caixas.
- **Responsivo:** KPIs em 4 colunas no desktop e 2 no tablet; no mobile, navegação recolhida e tabelas convertidas em listas de cartões.

## 6. Design System Notes for Stitch Generation

- **Atmosfera para os prompts:** "ferramenta técnica escura e sóbria, densa em informação, confiável".
- **Não repetir nos prompts:** os tokens de cor, fonte e raio acima já ficam aplicados no nível do projeto — os prompts de geração devem descrever apenas estrutura, conteúdo e intenção, em português do Brasil.
- **Referências de linguagem:** "camadas de superfície com bordas sutis", "destaque ciano com parcimônia", "mono para URLs e IDs", "chips de status/severidade com texto".
- **Iteração:** ajustes pontuais por tela (uma mudança por vez, citando o elemento exato), mantendo a consistência desta linguagem.
