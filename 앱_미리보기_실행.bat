@echo off
@chcp 65001 >nul
title MaternityBag - Mobile Web Preview

echo ========================================================
echo  [MaternityBag] Launching mobile app preview in browser...
echo ========================================================
echo.

start "" "%~dp0index.html"

exit
