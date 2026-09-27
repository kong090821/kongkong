@echo off
@chcp 65001 >nul
title MaternityBag - Environment Setup

echo.
echo ========================================================
echo  [MaternityBag] Setting up environment for current PC...
echo ========================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup_environment.ps1"

echo.
pause
