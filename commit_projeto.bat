@echo off
title Automação Git - Projeto Integrador (Etapa 1)
cls
echo ======================================================
echo    GIT AUTOMATIZADO - PROJETO INTEGRADOR (ETAPA 1)
echo ======================================================
echo.

echo [1/4] Adicionando arquivos...
git add .

echo.
set /p desc="Descreva o que foi feito nesta parte da Etapa 1: "
set msg=PI-Etapa1: %desc%

echo.
echo [2/4] Criando commit: "%msg%"
git commit -m "%msg%"

echo.
echo [3/4] Sincronizando com o GitHub...
git pull origin main

echo.
echo [4/4] Enviando para o repositório remoto...
git push origin main

echo.
echo ======================================================
echo    ETAPA 1 ATUALIZADA COM SUCESSO!
echo ======================================================
echo.
pause