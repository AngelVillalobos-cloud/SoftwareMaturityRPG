@echo off
title Software Maturity RPG
chcp 65001 > nul
cd /d "%~dp0"
rem En un clon recien descargado no existe bin\ (esta en .gitignore): se compila primero.
if not exist bin\com\softwarerpg\Main.class (
    echo No se encontro el juego compilado. Compilando por primera vez...
    call build_and_run.bat
    exit /b
)
echo Iniciando Software Maturity RPG...
java -cp bin com.softwarerpg.Main
pause
