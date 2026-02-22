# 🗺️ BeyondApp - Projeto Integrador SENAC

> **Status do Projeto:** 🚧 Em Desenvolvimento (Etapa 1 de 3 - Planejamento e Requisitos)

## 📌 Nota sobre a Metodologia de Estruturação
*A concepção do modelo de negócios, a identificação da dor de mercado e a definição do escopo deste projeto foram idealizadas de forma autoral. Como a visão do sistema se expandiu rapidamente, utilizei Inteligência Artificial Generativa atuando como um "Copiloto de Engenharia de Prompt". A IA foi operada estritamente para traduzir a minha visão de produto para os padrões técnicos de documentação (Requisitos Funcionais e Não Funcionais), auxiliando na organização lógica das ideias e me permitindo antecipar a estruturação de conceitos arquiteturais de forma clara e padronizada para as próximas etapas do curso.*

---

## 📖 1. O Projeto (Elevator Pitch)
O **BeyondApp** visa unir a precisão, organização e usabilidade do Google Maps com a experiência visual e técnicas de marketing dos influencers digitais no Youtube, Instagram e Tik Tok.

### O Problema
* **Para o consumidor:** O Google Maps é excelente em geolocalização, mas entrega fotos desatualizadas e opiniões frias. Já o Instagram e TikTok têm conteúdo vibrante e recente, mas falham na busca por proximidade geográfica.
* **Para o comerciante:** É custoso e trabalhoso manter cardápios e vitrines atualizados em múltiplas plataformas.

### A Solução
Um **"Mapa Vivo"**: um aplicativo onde o usuário explora a cidade através de vídeos curtos (Reels/Shorts) geolocalizados. O sistema utiliza a base do Maps para distribuição, mas entrega a experiência visual das redes sociais.

### O Diferencial (Automação Zero-Esforço)
O dono do estabelecimento conecta suas redes sociais apenas uma vez. O sistema puxa automaticamente os vídeos e fotos novos para o mapa, sem necessidade de re-postagem manual.

---

## 👥 2. Atores do Sistema e Permissões

O sistema possui arquitetura multiplataforma (Mobile/PC App e Extensão Web):

* 🟢 **Consumidor (B2C):** Usuário final. Busca locais, assiste ao feed de vídeos, traça rotas e faz avaliações.
* 🔵 **Comerciante (B2B):** Dono do local. Gerencia a sincronização das redes sociais (Instagram/TikTok) e visualiza métricas.
* 🔴 **Administrador (SysAdmin):** Equipe interna. Modera conteúdos sinalizados pela IA e gerencia usuários.

---

## ⚙️ 3. Requisitos Funcionais (RF)

### Módulo do Consumidor
* `RF-01` **Geolocalização:** Captura a posição do usuário para busca local.
* `RF-02` **Filtros:** Permite filtrar por raio de distância (ex: 1km a 50km) e categorias.
* `RF-03` **Feed Discovery:** Exibição de vídeos curtos em formato "swipe" ou grade.
* `RF-04` **Detalhes:** Exibe dados do Google Places somados à galeria de mídia sincronizada.
* `RF-05` **Rotas:** Botão "Como Chegar" integrado ao GPS nativo.
* `RF-06` **Avaliação:** Upload de fotos/vídeos próprios com nota.
* `RF-07` **Cross-posting:** Compartilhamento automático da avaliação no Instagram do usuário (via OAuth).

### Módulo do Comerciante
* `RF-08` **Propriedade:** Validação via Google Business Profile API.
* `RF-09` **Conexão:** Login social com Instagram Professional e TikTok.
* `RF-10` **Sincronização (CRON):** Atualização automática do catálogo de mídia em intervalos definidos.
* `RF-11` **Gestão:** Possibilidade de ocultar mídias específicas do mapa.
* `RF-12` **Insights:** Métricas de visualização e cliques em rotas.

### Segurança e Back-end
* `RF-13` **Moderação IA:** Análise automática de uploads via Visão Computacional.
* `RF-14` **Bloqueio:** Rejeição imediata de conteúdo impróprio (Nudez, Violência, Spam).
* `RF-15` **Injeção Web:** A extensão insere o conteúdo visual diretamente na interface do Google Maps no navegador.

---

## 🛡️ 4. Requisitos Não Funcionais (RNF)

* `RNF-01` **Performance:** Carregamento de vídeos em até 2 segundos (4G/5G).
* `RNF-02` **Lazy Loading:** Carregamento de dados sob demanda (paginação) para economizar memória.
* `RNF-03` **Otimização:** Compressão automática de imagens para WebP.
* `RNF-04` **Segurança:** Tráfego 100% criptografado (HTTPS/TLS).
* `RNF-05` **Credenciais:** Chaves de API protegidas em variáveis de ambiente (.env).
* `RNF-06` **OAuth 2.0:** Autenticação segura sem armazenamento de senhas de terceiros.
* `RNF-07` **Responsividade:** Interface Mobile-First adaptável.
* `RNF-09` **Fallback:** O sistema deve funcionar (exibindo dados básicos) mesmo se as APIs sociais caírem.

---

## 🏗️ 5. Observações Técnicas (Stack Planejada)

* **Back-end:** Node.js (TypeScript) + Express/NestJS.
* **Front-end:** React Native (Expo) ou Flutter (Mobile) + JS Manifest V3 (Extensão).
* **Banco de Dados:** PostgreSQL + PostGIS (Geolocalização).
* **Identificador Único:** Uso do `Place_ID` do Google como chave primária dos locais.
* **APIs:** Google Places, Maps JS, Instagram Graph, TikTok Dev, Cloud Vision/Rekognition.
* **Storage:** AWS S3 ou Cloudinary (priorizando URLs de embed para economia).

---

## 📚 Referências Bibliográficas e Tecnológicas

Este projeto foi idealizado e fundamentado com base nas seguintes diretrizes, documentações e padrões da indústria:

1. Normas, Leis e Diretrizes de Qualidade
ISO/IEC 25010: Padrão internacional para modelagem e avaliação de qualidade de software, base para a categorização dos Requisitos Não Funcionais.

WCAG (Web Content Accessibility Guidelines): Diretrizes de Acessibilidade para Conteúdo Web (W3C), aplicadas aos requisitos de interface e usabilidade.

LGPD (Lei Geral de Proteção de Dados - Lei nº 13.709/2018): Base legal para decisões de privacidade, proteção de dados e revogação de acesso (Soft Delete).

Scrum / Kanban: Metodologias Ágeis utilizadas para o planejamento e divisão de épicas.

2. Documentações de APIs Oficiais
Google Maps Platform: Documentação técnica da Google Places API e Maps JavaScript API (geolocalização e rotas).

Meta for Developers: Documentação da Instagram Graph API (autenticação e extração de mídia).

TikTok for Developers: Documentação oficial para integração e login social.

Google Business Profile API: Documentação para a validação de propriedade de estabelecimentos comerciais.

3. Serviços em Nuvem e Inteligência Artificial
AWS (Amazon Web Services): Manuais do AWS S3 (Object Storage) e AWS Rekognition (Visão computacional para moderação).

Google Cloud Platform (GCP): Documentação técnica da Cloud Vision API.

4. Arquitetura, Protocolos e Bancos de Dados
OAuth 2.0: Protocolo padrão para autorização segura em integrações de login social.

Manifest V3: Arquitetura oficial do Google Chrome para o desenvolvimento da extensão de navegador.

PostgreSQL & PostGIS: Documentação do banco de dados relacional e sua extensão espacial para cálculos de coordenadas.

REST (Representational State Transfer): Padrão de arquitetura utilizado para modelar as requisições do Back-end.

🛡️ Autoria e Propriedade Intelectual
Projeto Desenvolvido por: Emanuel Filipe da Silva
Instituição: SENAC Minas Gerais
Etapa Atual: 1 de 3 (Planejamento e Requisitos)

🔒 Aviso: Todo o conteúdo deste repositório, incluindo a arquitetura proposta e documentos anexos, é de autoria própria. A reprodução sem os devidos créditos é considerada plágio. Consulte o arquivo LICENSE para termos de uso.
