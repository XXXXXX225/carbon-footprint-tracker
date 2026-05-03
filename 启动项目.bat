@echo off
echo ========================================
echo Carbon Footprint Tracker - Quick Start
echo ========================================
echo.

cd /d "%~dp0"

echo [0/2] Releasing port 8888 if already in use...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$l = Get-NetTCPConnection -LocalPort 8888 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1; if ($l) { Stop-Process -Id $l.OwningProcess -Force }"

if "%AI_PROVIDER%"=="" set "AI_PROVIDER=zhipu"
if "%AI_API_KEY%"=="" set "AI_API_KEY=e228506735934d6f83299192705bd921.08VVDcY3ODpO3uwx"
if "%AI_MODEL%"=="" set "AI_MODEL=glm-4-flash"
if "%AI_VISION_API_KEY%"=="" set "AI_VISION_API_KEY=8622f339ebc54568a096820d43d12970.NIcVdGN7oCmnqfD2"
if "%AI_VISION_MODEL%"=="" set "AI_VISION_MODEL=glm-4v-flash"
if "%AI_BASE_URL%"=="" set "AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4"
if "%AI_TIMEOUT_SECONDS%"=="" set "AI_TIMEOUT_SECONDS=45"

echo.
echo AI Configuration:
echo Provider: %AI_PROVIDER%
echo Text Model: %AI_MODEL%
echo Vision Model: %AI_VISION_MODEL%
echo.

echo [1/2] Starting Backend...
start "Backend" /D "%~dp0backend" cmd /k mvn spring-boot:run

echo Waiting for backend to initialize...
timeout /t 10 /nobreak >nul

echo [2/2] Starting Frontend...
start "Frontend" /D "%~dp0frontend" cmd /k npm run dev

echo.
echo Started!
echo Backend: http://localhost:8888/api
echo Frontend: http://localhost:5173/
echo.
pause