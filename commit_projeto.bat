@echo off
title Sincronização - Projeto Integrador (Etapa 1)
cls
echo ======================================================
echo    ATUALIZANDO PROJETO INTEGRADOR - ETAPA 1
echo ======================================================
echo.

echo [1/3] Identificando novas alterações...
git add .

echo.
set /p desc="O que foi alterado nesta etapa? "
set msg=PI-Etapa1: %desc%

echo.
echo [2/3] Registrando mudanças: "%msg%"
git commit -m "%msg%"

echo.
echo [3/3] Enviando para o GitHub...
:: O comando abaixo evita o erro de historias irrelevantes que voce teve
git pull origin main --rebase
git push origin main

echo.
echo ======================================================
echo    ARQUIVOS SINCRONIZADOS COM SUCESSO!
echo ======================================================
echo.
pause