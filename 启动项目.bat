@echo off
echo ========================================
echo Carbon Footprint Tracker - Quick Start
echo ========================================
echo.

cd /d "%~dp0"

echo [0/2] Releasing port 8888 if already in use...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$listener = Get-NetTCPConnection -LocalPort 8888 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1; if ($listener) { Stop-Process -Id $listener.OwningProcess -Force; Write-Host ('Stopped process using port 8888: ' + $listener.OwningProcess) }"

if "%AI_PROVIDER%"=="" (
        set "AI_PROVIDER=zhipu"
)
if "%AI_API_KEY%"=="" (
        set "AI_API_KEY=e228506735934d6f83299192705bd921.08VVDcY3ODpO3uwx"
)
if "%AI_BASE_URL%"=="" set "AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4"
if "%AI_MODEL%"=="" set "AI_MODEL=glm-4-flash"
if "%AI_TIMEOUT_SECONDS%"=="" set "AI_TIMEOUT_SECONDS=45"

echo.
echo AI analysis provider: %AI_PROVIDER%
echo AI model: %AI_MODEL%
echo AI base URL: %AI_BASE_URL%
echo AI timeout seconds: %AI_TIMEOUT_SECONDS%
echo AI API key: configured

echo [1/2] Starting Backend...
start "Backend" /D "%~dp0backend" cmd /k mvn spring-boot:run

echo Waiting...
timeout /t 10 /nobreak >nul

echo [2/2] Starting Frontend...
start "Frontend" /D "%~dp0frontend" cmd /k npm run dev

echo.
echo Started!
echo Backend: http://localhost:8888/api
echo Frontend: http://localhost:5173/
echo.
pause
