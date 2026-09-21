<#
    QAwler — helper da ESTAÇÃO de autenticação (login assistido) — Windows
    Abre o Chrome com perfil dedicado + porta de depuração (CDP) para a
    aplicação anexar e varrer a sessão autenticada. Nenhuma credencial passa
    pelo QAwler. Documentação: docs/LOGIN-ASSISTIDO.md

    Uso:
      powershell -ExecutionPolicy Bypass -File tools\qawler-login.ps1 -Url https://alvo/login
      powershell -ExecutionPolicy Bypass -File tools\qawler-login.ps1 -Status
      powershell -ExecutionPolicy Bypass -File tools\qawler-login.ps1 -Stop
#>
param(
    [string]$Url,
    [int]$Porta = 9222,
    [string]$Perfil = "$env:USERPROFILE\.qawler\chrome-profile",
    [string]$Bind = "127.0.0.1",
    [string]$Navegador,
    [switch]$Status,
    [switch]$Stop
)

$ErrorActionPreference = "Stop"

function Get-Navegador {
    if ($Navegador) { return $Navegador }
    $candidatos = @(
        "$env:PROGRAMFILES\Google\Chrome\Application\chrome.exe",
        "${env:PROGRAMFILES(X86)}\Google\Chrome\Application\chrome.exe",
        "$env:LOCALAPPDATA\Google\Chrome\Application\chrome.exe"
    )
    foreach ($c in $candidatos) { if (Test-Path $c) { return $c } }
    throw "Navegador não encontrado. Instale o Google Chrome ou use -Navegador <caminho>."
}

function Get-CdpJson([string]$Caminho) {
    try { return Invoke-RestMethod -Uri "http://${Bind}:${Porta}${Caminho}" -TimeoutSec 3 } catch { return $null }
}

function Show-Status {
    $versao = Get-CdpJson "/json/version"
    if (-not $versao) {
        Write-Host "✗ Sem sessão em ${Bind}:${Porta} — nenhuma janela aberta (ou porta inacessível)."
        return
    }
    $lista = Get-CdpJson "/json/list"
    $aba = ($lista | Where-Object { $_.type -eq "page" } | Select-Object -First 1).url
    Write-Host "✓ Sessão ativa em ${Bind}:${Porta}"
    Write-Host "  navegador : $($versao.Browser)"
    Write-Host "  perfil    : $Perfil"
    Write-Host "  aba atual : $aba"
}

function Stop-Sessao {
    $procs = Get-CimInstance Win32_Process -Filter "Name = 'chrome.exe'" |
        Where-Object { $_.CommandLine -like "*user-data-dir=$Perfil*" }
    if (-not $procs) { Write-Host "Nenhum navegador do perfil dedicado em execução ($Perfil)."; return }
    Write-Host "Encerrando o navegador do perfil dedicado (pids: $($procs.ProcessId -join ', '))"
    $procs | ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }
    Write-Host "Sessão encerrada."
}

function Start-Sessao {
    if (-not $Url) { throw "Informe -Url para abrir a janela (ou use -Status / -Stop)." }
    $bin = Get-Navegador
    New-Item -ItemType Directory -Force -Path $Perfil | Out-Null
    $args = @(
        "--remote-debugging-port=$Porta",
        "--remote-debugging-address=$Bind",
        "--user-data-dir=$Perfil",
        "--no-first-run",
        "--no-default-browser-check",
        $Url
    )
    Start-Process -FilePath $bin -ArgumentList $args | Out-Null

    for ($i = 0; $i -lt 40; $i++) {
        if (Get-CdpJson "/json/version") { break }
        Start-Sleep -Milliseconds 250
    }

    Write-Host "─────────────────────────────────────────────────────────────"
    Write-Host "✓ Janela de login aberta (perfil dedicado do QAwler)"
    Write-Host "  alvo     : $Url"
    Write-Host "  perfil   : $Perfil"
    Write-Host "  navegador: $bin"
    if (Get-CdpJson "/json/version") { Write-Host "  CDP      : ativo em ${Bind}:${Porta}" }
    else { Write-Host "  CDP      : ainda não respondeu em ${Bind}:${Porta} (aguarde alguns segundos)" }
    Write-Host ""
    Write-Host "  1. Faça o login na janela (captcha/2FA ficam com você)."
    Write-Host "  2. No QAwler, informe a estação: ${Bind}:${Porta}"
    Write-Host "  3. Clique em “Concluí o login” para o crawler varrer a sessão."
    Write-Host "─────────────────────────────────────────────────────────────"
}

if ($Stop) { Stop-Sessao }
elseif ($Status) { Show-Status }
else { Start-Sessao }
