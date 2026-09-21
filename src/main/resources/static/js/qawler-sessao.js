/* ─────────────────────────────────────────────────────────────────────────────
   QAwler — painel da sessão assistida (login assistido)
   Ver docs/LOGIN-ASSISTIDO.md.

   Como usar numa tela Thymeleaf:

     <div class="sessao-assistida" data-sistema-id="12" data-teste-id="34"></div>
     <script src="/js/qawler-sessao.js"></script>

   O painel conversa com /api/sistemas/{id}/sessao/* usando o cookie de login
   (JwtAuthFilter) ou, se existir, o token guardado em sessionStorage/localStorage
   na chave `qawler.token` (clientes que usam a API com Bearer).
   ───────────────────────────────────────────────────────────────────────────── */
(function () {
  'use strict';

  var INTERVALO_MS = 3000;
  var CHAVE_TOKEN = 'qawler.token';

  function token() {
    try {
      return sessionStorage.getItem(CHAVE_TOKEN) || localStorage.getItem(CHAVE_TOKEN) || '';
    } catch (e) {
      return '';
    }
  }

  function cabecalhos() {
    var h = { Accept: 'application/json' };
    var t = token();
    if (t) { h.Authorization = 'Bearer ' + t; }
    return h;
  }

  function api(caminho, metodo) {
    return fetch(caminho, {
      method: metodo || 'GET',
      headers: cabecalhos(),
      credentials: 'same-origin'
    }).then(function (resp) {
      if (resp.status === 401 || resp.status === 403) {
        throw new Error('sem-autorizacao');
      }
      if (resp.status === 404) {
        return null; // sem sessão registrada
      }
      return resp.text().then(function (texto) {
        var corpo = null;
        if (texto) { try { corpo = JSON.parse(texto); } catch (e) { corpo = null; } }
        if (!resp.ok) {
          throw new Error((corpo && (corpo.erro || corpo.error)) || ('HTTP ' + resp.status));
        }
        return corpo;
      });
    });
  }

  function html(raiz) {
    raiz.innerHTML =
      '<div class="d-flex flex-wrap gap-2 align-items-center">' +
      '  <span class="text-muted small"><i class="bi bi-shield-lock me-1"></i>Sessão de login</span>' +
      '  <input type="text" class="form-control form-control-sm sessao-estacao" ' +
      '         placeholder="estação (vazio = esta máquina)" style="max-width:16rem;background:#0f172a;border-color:#334155;color:#e2e8f0;">' +
      '  <button type="button" class="btn btn-sm btn-outline-sky sessao-btn-abrir">Abrir janela de login</button>' +
      '  <button type="button" class="btn btn-sm btn-outline-info sessao-btn-testar">Testar conexão</button>' +
      '  <button type="button" class="btn btn-sm btn-outline-success sessao-btn-confirmar">Concluí o login</button>' +
      '  <button type="button" class="btn btn-sm btn-outline-danger sessao-btn-encerrar">Encerrar</button>' +
      '</div>' +
      '<div class="small mt-2 sessao-status text-muted">Verificando sessão…</div>' +
      '<div class="small text-muted mt-1">A janela abre na máquina da estação; o login é manual ' +
      '(captcha/2FA ficam com você) e o QAwler <strong>não guarda credenciais</strong>.</div>';
  }

  function elementos(raiz) {
    return {
      estacao: raiz.querySelector('.sessao-estacao'),
      abrir: raiz.querySelector('.sessao-btn-abrir'),
      testar: raiz.querySelector('.sessao-btn-testar'),
      confirmar: raiz.querySelector('.sessao-btn-confirmar'),
      encerrar: raiz.querySelector('.sessao-btn-encerrar'),
      status: raiz.querySelector('.sessao-status')
    };
  }

  function mensagem(ui, texto, cor) {
    ui.status.className = 'small mt-2 sessao-status ' + (cor || 'text-muted');
    ui.status.innerHTML = texto;
  }

  function aplicar(ui, sessao) {
    if (!sessao) {
      mensagem(ui, 'Nenhuma sessão aberta para este sistema.', 'text-muted');
      ui.confirmar.disabled = true;
      ui.encerrar.disabled = true;
      return;
    }
    var conectada = sessao.conectada;
    var estado = sessao.aguardandoLogin
      ? '<span class="text-warning">aguardando você concluir o login</span>'
      : '<span class="text-success">login confirmado — varredura liberada</span>';
    var linha = [];
    linha.push('<span class="badge bg-secondary">' + (sessao.modo || '-') + '</span>');
    linha.push(conectada
      ? '<span class="text-success">● conectada</span>'
      : '<span class="text-danger">● sem resposta</span>');
    linha.push(estado);
    if (sessao.estacao) { linha.push('<span class="text-muted">estação ' + sessao.estacao + '</span>'); }
    if (sessao.navegador) { linha.push('<span class="text-muted">' + sessao.navegador + '</span>'); }
    var detalhe = '';
    if (sessao.urlAtual) { detalhe = '<div class="text-muted">aba atual: ' + sessao.urlAtual + '</div>'; }
    if (sessao.observacao) { detalhe += '<div class="text-muted">' + sessao.observacao + '</div>'; }
    mensagem(ui, linha.join(' · ') + detalhe, '');
    ui.confirmar.disabled = !sessao.aguardandoLogin;
    ui.encerrar.disabled = false;
  }

  function erro(ui, e) {
    if (e.message === 'sem-autorizacao') {
      mensagem(ui, 'Sem autorização — faça login no QAwler para operar a sessão.', 'text-warning');
    } else {
      mensagem(ui, 'Falha: ' + e.message, 'text-danger');
    }
  }

  function montar(raiz) {
    var sistemaId = raiz.getAttribute('data-sistema-id');
    if (!sistemaId) { return; }
    var base = '/api/sistemas/' + sistemaId + '/sessao';
    html(raiz);
    var ui = elementos(raiz);

    function atualizar() {
      api(base).then(function (s) { aplicar(ui, s); }).catch(function (e) { erro(ui, e); });
    }

    function acao(caminho, mostrarResultado) {
      var estacao = ui.estacao.value.trim();
      var url = caminho + (estacao ? '?estacao=' + encodeURIComponent(estacao) : '');
      ui.status.className = 'small mt-2 sessao-status text-muted';
      ui.status.textContent = 'Executando…';
      api(url, 'POST').then(function (r) {
        if (mostrarResultado && r) {
          if (r.ok) {
            mensagem(ui, 'Conexão OK com <code>' + r.endereco + '</code> — ' + (r.navegador || 'navegador sem versão'), 'text-success');
          } else {
            mensagem(ui, 'Sem conexão: ' + (r.erro || 'sem resposta'), 'text-danger');
          }
        } else {
          atualizar();
        }
      }).catch(function (e) { erro(ui, e); });
    }

    ui.abrir.addEventListener('click', function () { acao(base + '/abrir', false); });
    ui.testar.addEventListener('click', function () { acao(base + '/testar', true); });
    ui.confirmar.addEventListener('click', function () { acao(base + '/confirmar', false); });
    ui.encerrar.addEventListener('click', function () { acao(base + '/encerrar', false); });

    atualizar();
    var timer = setInterval(function () {
      if (document.visibilityState === 'visible') { atualizar(); }
    }, INTERVALO_MS);
    window.addEventListener('pagehide', function () { clearInterval(timer); });
  }

  function iniciar() {
    var paineis = document.querySelectorAll('.sessao-assistida');
    for (var i = 0; i < paineis.length; i++) { montar(paineis[i]); }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', iniciar);
  } else {
    iniciar();
  }
})();
