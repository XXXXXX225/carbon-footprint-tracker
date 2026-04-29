@echo off
echo ========================================
echo Carbon Footprint Tracker - Quick Start
echo ========================================
echo.

cd /d "%~dp0"

echo [0/2] Releasing port 8888 if already in use...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$listener = Get-NetTCPConnection -LocalPort 8888 -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1; if ($listener) { Stop-Process -Id $listener.OwningProcess -Force; Write-Host ('Stopped process using port 8888: ' + $listener.OwningProcess) }"

if "%AI_PROVIDER%"=="" set "AI_PROVIDER=zhipu"
if "%AI_BASE_URL%"=="" set "AI_BASE_URL=https://open.bigmodel.cn/api/paas/v4"
if "%AI_MODEL%"=="" set "AI_MODEL=glm-4-flash"

echo.
echo AI analysis provider: %AI_PROVIDER%
echo AI model: %AI_MODEL%
echo AI base URL: %AI_BASE_URL%
if "%AI_API_KEY%"=="" (
        echo WARNING: AI_API_KEY is not set. The GLM model will fall back to local analysis until a valid key is provided.
)

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
