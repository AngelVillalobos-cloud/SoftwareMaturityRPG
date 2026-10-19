@echo off
title Compilar y Ejecutar Software Maturity RPG
chcp 65001 > nul
cd /d "%~dp0"
echo Compilando codigo fuente Java...
if not exist bin mkdir bin
rem Se compila desde Main con -sourcepath: javac encuentra solo todas las clases que usa
rem (incluye paquetes nuevos). Rutas relativas, asi funciona aunque la carpeta tenga espacios.
javac -encoding UTF-8 -d bin -sourcepath src src\com\softwarerpg\Main.java
set COMPILE_RESULT=%ERRORLEVEL%
if %COMPILE_RESULT% EQU 0 (
    echo [OK] Compilacion exitosa.
    echo Iniciando el juego...
    java -cp bin com.softwarerpg.Main
) else (
    echo [ERROR] Ocurrio un error en la compilacion. Verifica que tengas instalado un JDK 17 o superior.
    pause
)
