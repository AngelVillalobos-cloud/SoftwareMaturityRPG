@echo off
title Compilar y Ejecutar Software Maturity RPG
chcp 65001 > nul
echo Compilando codigo fuente Java...
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\com\softwarerpg\model\*.java src\com\softwarerpg\data\*.java src\com\softwarerpg\engine\*.java src\com\softwarerpg\ui\*.java src\com\softwarerpg\Main.java
if %ERRORLEVEL% EQU 0 (
    echo [OK] Compilacion exitosa.
    echo Iniciando el juego...
    java -cp bin com.softwarerpg.Main
) else (
    echo [ERROR] Ocurrio un error en la compilacion.
    pause
)

