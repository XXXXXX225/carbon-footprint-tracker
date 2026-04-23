@echo off
echo ========================================
echo Carbon Footprint Tracker - Stop Services
echo ========================================
echo.

echo Stopping Java processes...
taskkill /F /FI "WINDOWTITLE eq Backend Service*" >nul 2>&1

echo Stopping Node.js processes...
taskkill /F /FI "WINDOWTITLE eq Frontend Service*" >nul 2>&1

echo.
echo ========================================
echo Services Stopped!
echo ========================================
echo.
pause