@echo off
@chcp 65001 >nul
title MaternityBag - Sync Sheet Data

echo.
echo ========================================================
echo  [MaternityBag] Syncing Excel/Sheet Data to Project...
echo ========================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0sync_sheet_data.ps1"

echo.
pause
