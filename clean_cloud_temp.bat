@echo off
@chcp 65001 >nul
title MaternityBag - Cloud Temp Cache Cleaner

echo.
echo ========================================================
echo  [MaternityBag] Cleaning cloud temp and build caches...
echo ========================================================
echo.

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0clean_cloud_temp.ps1"

echo.
pause
