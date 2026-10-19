@echo off
title Compilar y Ejecutar Software Maturity RPG
chcp 65001 > nul
cd /d "%~dp0"
echo Compilando codigo fuente Java...
if not exist bin mkdir bin
rem Lista todos los .java (incluye paquetes nuevos sin tener que editar este script)
dir /s /b src\*.java > "%TEMP%\softwarerpg_sources.txt"
javac -encoding UTF-8 -d bin @"%TEMP%\softwarerpg_sources.txt"
set COMPILE_RESULT=%ERRORLEVEL%
del "%TEMP%\softwarerpg_sources.txt" > nul 2>&1
if %COMPILE_RESULT% EQU 0 (
    echo [OK] Compilacion exitosa.
    echo Iniciando el juego...
    java -cp bin com.softwarerpg.Main
) else (
    echo [ERROR] Ocurrio un error en la compilacion. Verifica que tengas instalado un JDK 17 o superior.
    pause
)
