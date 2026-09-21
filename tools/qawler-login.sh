#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# QAwler — helper da ESTAÇÃO de autenticação (login assistido)
#
# Abre o navegador do usuário com um perfil DEDICADO do QAwler e a porta de
# depuração (CDP) ligada. A aplicação então anexa a essa janela e faz a
# varredura já autenticada. Nenhuma credencial passa pelo QAwler.
#
# Documentação: docs/LOGIN-ASSISTIDO.md  ·  Testado em macOS e Linux.
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

PORTA="${QAWLER_PORTA_CDP:-9222}"
PERFIL="${QAWLER_PERFIL:-$HOME/.qawler/chrome-profile}"
BIND="127.0.0.1"
URL=""
NAVEGADOR="${QAWLER_CHROME_BIN:-}"
ACAO="abrir"

uso() {
  cat <<'TXT'
Uso: qawler-login.sh --url <URL do alvo> [opções]

  --url <url>        URL do sistema a autenticar (obrigatório ao abrir)
  --porta <n>        porta de depuração (CDP)            [padrão: 9222]
  --perfil <dir>     perfil dedicado do QAwler           [padrão: ~/.qawler/chrome-profile]
  --bind <endereço>  interface onde a porta CDP escuta   [padrão: 127.0.0.1]
                     para conectar de outra máquina, use o IP da tailnet (ex.: 100.x.y.z)
  --navegador <bin>  caminho do navegador (padrão: detectado automaticamente)
  --status           mostra o estado da sessão (navegador + aba atual)
  --stop             encerra o navegador do perfil dedicado
  --help             esta ajuda

Exemplos:
  ./qawler-login.sh --url https://alvo.dev.local/login
  ./qawler-login.sh --bind 100.81.89.63 --url https://alvo.dev.local
  ./qawler-login.sh --status
TXT
}

enquanto_parseia() {
  while [ $# -gt 0 ]; do
    case "$1" in
      --url)       URL="${2:-}"; shift 2 ;;
      --porta)     PORTA="${2:-}"; shift 2 ;;
      --perfil)    PERFIL="${2:-}"; shift 2 ;;
      --bind)      BIND="${2:-}"; shift 2 ;;
      --navegador) NAVEGADOR="${2:-}"; shift 2 ;;
      --status)    ACAO="status"; shift ;;
      --stop)      ACAO="stop"; shift ;;
      --help|-h)   uso; exit 0 ;;
      *) echo "opção desconhecida: $1" >&2; uso; exit 2 ;;
    esac
  done
}
enquanto_parseia "$@"

detectar_navegador() {
  [ -n "$NAVEGADOR" ] && { echo "$NAVEGADOR"; return 0; }
  local candidatos=()
  case "$(uname -s)" in
    Darwin)
      candidatos+=("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome")
      candidatos+=("$HOME/Applications/Google Chrome.app/Contents/MacOS/Google Chrome")
      candidatos+=("/Applications/Chromium.app/Contents/MacOS/Chromium")
      ;;
    *)
      candidatos+=("/usr/bin/google-chrome" "/usr/bin/google-chrome-stable" \
                   "/usr/bin/chromium" "/usr/bin/chromium-browser" "/snap/bin/chromium")
      ;;
  esac
  local c
  for c in "${candidatos[@]}"; do
    if [ -x "$c" ]; then echo "$c"; return 0; fi
  done
  echo "erro: navegador não encontrado. Instale o Google Chrome ou use --navegador <caminho>." >&2
  return 1
}

cdp_json() { curl -sf --max-time 3 "http://$BIND:$PORTA$1" 2>/dev/null || true; }

json_campo() { # json_campo <campo> [tipo: version|lista] — lê o JSON do stdin
  if command -v python3 >/dev/null 2>&1; then
    # -c (e não heredoc): o heredoc tomaria o stdin que traz o JSON do CDP.
    python3 -c '
import json, sys
campo, tipo = sys.argv[1], sys.argv[2]
try:
    dados = json.load(sys.stdin)
except Exception:
    sys.exit(0)
if tipo == "version":
    print(dados.get(campo, ""))
else:
    for alvo in dados:
        u = alvo.get("url", "")
        if alvo.get("type") == "page" and not u.startswith(("chrome://", "devtools://", "about:")):
            print(u)
            break
' "$1" "$2"
    return 0
  fi
  # Fallback sem python3: extração simples de campo string.
  if [ "$2" = "version" ]; then
    sed -n 's/.*"'"$1"'"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p' | head -n1
  else
    tr ',' '\n' | grep '"url"' | head -n1 | sed 's/.*"url"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/'
  fi
}

mostrar_status() {
  local versao aba
  versao=$(cdp_json "/json/version" | json_campo "Browser" "version")
  if [ -z "$versao" ]; then
    echo "✗ Sem sessão em $BIND:$PORTA — nenhuma janela aberta (ou a porta não está acessível)."
    return 1
  fi
  aba=$(cdp_json "/json/list" | json_campo "url" "lista")
  echo "✓ Sessão ativa em $BIND:$PORTA"
  echo "  navegador : $versao"
  echo "  perfil    : $PERFIL"
  echo "  aba atual : ${aba:-<nenhuma>}"
  return 0
}

parar() {
  local pids restantes
  pids=$(pgrep -f -- "user-data-dir=$PERFIL" 2>/dev/null || true)
  if [ -z "$pids" ]; then
    echo "Nenhum navegador do perfil dedicado em execução ($PERFIL)."
    return 0
  fi
  echo "Encerrando o navegador do perfil dedicado (pids: $pids)"
  # shellcheck disable=SC2086
  kill $pids 2>/dev/null || true
  sleep 1
  restantes=$(pgrep -f -- "user-data-dir=$PERFIL" 2>/dev/null || true)
  if [ -n "$restantes" ]; then
    echo "Ainda vivos: $restantes — encerrando à força."
    # shellcheck disable=SC2086
    kill -9 $restantes 2>/dev/null || true
  fi
  echo "Sessão encerrada."
}

abrir() {
  if [ -z "$URL" ]; then
    echo "erro: --url é obrigatório para abrir a janela." >&2; uso; exit 2
  fi
  local bin; bin=$(detectar_navegador)
  mkdir -p "$PERFIL"

  local args=(--remote-debugging-port="$PORTA" --remote-debugging-address="$BIND"
              --user-data-dir="$PERFIL" --no-first-run --no-default-browser-check "$URL")
  if [ "$(uname -s)" = "Darwin" ] && [ -z "${QAWLER_CHROME_BIN:-}" ] && [[ "$bin" == *".app/Contents/MacOS/"* ]]; then
    # macOS: abre pelo app (a janela pertence ao usuário e sobrevive ao script)
    open -na "${bin%%.app/*}.app" --args "${args[@]}"
  else
    "$bin" "${args[@]}" >/dev/null 2>&1 &
    disown 2>/dev/null || true
  fi

  local i
  for i in $(seq 1 40); do
    [ -n "$(cdp_json "/json/version" | json_campo "Browser" "version")" ] && break
    sleep 0.25
  done

  echo "─────────────────────────────────────────────────────────────"
  echo "✓ Janela de login aberta (perfil dedicado do QAwler)"
  echo "  alvo     : $URL"
  echo "  perfil   : $PERFIL"
  echo "  navegador: $bin"
  if [ -n "$(cdp_json "/json/version" | json_campo "Browser" "version")" ]; then
    echo "  CDP      : ativo em $BIND:$PORTA"
  else
    echo "  CDP      : ainda não respondeu em $BIND:$PORTA (aguarde alguns segundos)"
  fi
  echo
  echo "  1. Faça o login na janela (captcha/2FA ficam com você)."
  echo "  2. No QAwler, informe a estação: ${BIND}:${PORTA}"
  echo "  3. Clique em “Concluí o login” para o crawler varrer a sessão."
  echo
  echo "  Status: $0 --status    Encerrar: $0 --stop"
  echo "─────────────────────────────────────────────────────────────"
  if [ "$BIND" = "127.0.0.1" ]; then
    echo "  Dica (acesso de outra máquina): rode com --bind <IP-da-tailnet>."
  fi
}

case "$ACAO" in
  status) mostrar_status ;;
  stop)   parar ;;
  abrir)  abrir ;;
esac
