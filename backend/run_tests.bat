@echo off
chcp 65001 >nul
echo ═══════════════════════════════════════════════════════
echo    HMIS Backend - Running End-to-End Tests
echo    Profile: test (H2 in-memory, no Docker needed)
echo ═══════════════════════════════════════════════════════
echo.

cd /d "%~dp0core"

echo [1/3] Cleaning project...
call mvn clean -q

echo [2/3] Compiling project...
call mvn compile test-compile -q
if %ERRORLEVEL% neq 0 (
    echo.
    echo ╔═══════════════════════════════════════╗
    echo ║  ❌  COMPILE FAILED                    ║
    echo ╚═══════════════════════════════════════╝
    pause
    exit /b 1
)

echo [3/3] Running all tests...
echo.
call mvn test -Dspring.profiles.active=test -Dsurefire.useFile=false
set TEST_RESULT=%ERRORLEVEL%

echo.
echo ═══════════════════════════════════════════════════════
if %TEST_RESULT% equ 0 (
    echo   ✅  ALL TESTS PASSED
) else (
    echo   ❌  SOME TESTS FAILED - Check output above
)
echo ═══════════════════════════════════════════════════════
echo.

pause
