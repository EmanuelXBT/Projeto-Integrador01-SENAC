# 🤝 Contribuindo com o QAwler

Obrigado pelo interesse em contribuir com o QAwler! Este documento estabelece as diretrizes para colaboração.

---

## 📋 Código de Conduta

- Respeite o tempo e esforço dos outros contribuidores
- Críticas são bem-vindas, mas devem ser construtivas e focadas no código
- Assédio, discriminação ou qualquer forma de desrespeito não serão tolerados

---

## 🚀 Como Contribuir

### 1. Escolha uma Issue

- Verifique as [issues abertas](https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC/issues)
- Comente na issue que deseja trabalhar para evitar duplicação
- Se for uma funcionalidade nova, abra uma issue antes de codificar

### 2. Fork e Branch

```bash
# Fork o repositório pelo GitHub
git clone https://github.com/SEU_USER/Projeto-Integrador01-SENAC.git
cd Projeto-Integrador01-SENAC

# Crie uma branch descritiva
git checkout -b feat/nome-da-feature
# ou
git checkout -b fix/descricao-do-bug
```

### 3. Desenvolva

- Siga o [Clean Code](https://github.com/ryanmcdermott/clean-code-javascript) e princípios SOLID
- Mantenha coesão alta e acoplamento baixo entre módulos
- Documente classes e métodos públicos com Javadoc
- Testes unitários são obrigatórios para novas funcionalidades

### 4. Commits Padronizados

Use [Conventional Commits](https://www.conventionalcommits.org/):

```
feat: adiciona scanner de acessibilidade
fix: corrige NullPointerException no crawler de imagens
docs: atualiza DER com nova tabela de logs
refactor: extrai lógica de autenticação para módulo separado
test: adiciona testes para o agendamento de varreduras
chore: atualiza dependências do Maven
```

### 5. Pull Request

- Descreva **o quê** e **por quê** (não apenas o como)
- Referencie a issue relacionada (`Closes #42`)
- Inclua screenshots se a mudança afetar o frontend
- Marque revisores se aplicável

---

## 🧱 Stack e Ambiente

| Ferramenta | Versão |
|---|---|
| Java | 17+ |
| Spring Boot | 3.x |
| MySQL | 8 |
| Maven | 3.9+ |
| Docker | 24+ |

### Setup Local

```bash
# 1. Clone e configure
cp .env.example .env
# Preencha as variáveis no .env

# 2. Build
mvn clean compile

# 3. Testes
mvn test

# 4. Execução
docker-compose up -d
```

---

## 📁 Estrutura de Pastas

```
src/main/java/br/com/qawler/
├── controller/     # REST endpoints
├── service/        # Lógica de negócio
├── repository/     # Spring Data JPA
├── model/          # Entidades JPA
├── scanner/        # Módulos de varredura
├── report/         # Geração de relatórios
└── config/         # Security, RabbitMQ, Selenium
```

---

## ❓ Dúvidas

Abra uma [issue](https://github.com/EmanuelXBT/Projeto-Integrador01-SENAC/issues) ou entre em contato: **contato.emanuel2002@gmail.com**
