@echo off
title Pruebas de Software Maturity RPG
chcp 65001 > nul
cd /d "%~dp0"
echo Ejecutando pruebas JUnit con Maven Wrapper (la primera vez descarga Maven)...
call mvnw.cmd -q test
if %ERRORLEVEL% EQU 0 (
    echo [OK] Todas las pruebas pasaron.
) else (
    echo [ERROR] Hay pruebas fallidas. Revisa target\surefire-reports
)
pause
