@echo off
setlocal EnableDelayedExpansion
title HMIS Backend - Control Center

:: ── Cau hinh duong dan (auto-detected) ─────────────────────
set JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.18.8-hotspot
set MAVEN_HOME=C:\Tools\apache-maven-3.9.6
set PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%

cls
echo.
echo  ===========================================================
echo       HMIS ENTERPRISE - BACKEND CONTROL CENTER
echo  ===========================================================
echo   Java  : %JAVA_HOME%
echo   Maven : %MAVEN_HOME%
echo  ===========================================================
echo.
echo   Chon che do chay:
echo   ---------------------------------------------------------
echo   [1] Docker Mode  - PostgreSQL + Kafka + Redis
echo   [2] Local Mode   - H2 In-Memory, khong can Docker
echo   ---------------------------------------------------------
echo.

set /p choice=  Nhap 1 hoac 2, nhan Enter: 

echo.

if "%choice%"=="1" goto DOCKER_MODE
if "%choice%"=="2" goto LOCAL_MODE
echo   [ERROR] Nhap 1 hoac 2.
pause
exit /b 1

:: ── DOCKER MODE ─────────────────────────────────────────────
:DOCKER_MODE
echo   [1/3] Kiem tra Docker...
docker --version 2>nul
if %errorlevel% neq 0 (
    echo.
    echo   [ERROR] Docker chua duoc cai. Chon [2] de chay khong can Docker.
    echo.
    pause
    exit /b 1
)
echo   [OK] Docker san sang.
echo.
echo   [2/3] Khoi dong Postgres + Kafka + Redis...
cd /d "%~dp0backend"
docker-compose up -d
if %errorlevel% neq 0 (
    echo   [ERROR] docker-compose that bai.
    pause
    exit /b 1
)
echo   [OK] Infrastructure dang khoi dong (cho 8 giay)...
timeout /t 8 /nobreak >nul
set SPRING_PROFILES_ACTIVE=default
goto RUN_BOOT

:: ── LOCAL MODE ───────────────────────────────────────────────
:LOCAL_MODE
echo   [OK] Che do Local - H2 In-Memory Database.
echo   [!] Du lieu se mat khi dong ung dung.
echo.
set SPRING_PROFILES_ACTIVE=local
goto CHECK_ENV

:: ── KIEM TRA JAVA ────────────────────────────────────────────
:CHECK_ENV
echo   [*] Kiem tra Java...
"%JAVA_HOME%\bin\java.exe" -version 2>nul
if %errorlevel% neq 0 (
    echo.
    echo   [ERROR] Khong tim thay Java tai: %JAVA_HOME%
    echo   Vui long cai lai JDK 17 hoac cap nhat duong dan trong file bat.
    echo.
    pause
    exit /b 1
)
echo   [OK] Java san sang.

echo   [*] Kiem tra Maven...
call "%MAVEN_HOME%\bin\mvn.cmd" -version 2>nul
if %errorlevel% neq 0 (
    echo.
    echo   [ERROR] Khong tim thay Maven tai: %MAVEN_HOME%
    echo   Vui long kiem tra lai thu muc C:\Tools\apache-maven-3.9.6
    echo.
    pause
    exit /b 1
)
echo   [OK] Maven san sang.
echo.

:: ── KHOI DONG SPRING BOOT ────────────────────────────────────
:RUN_BOOT
echo  ===========================================================
echo   Khoi dong Spring Boot...
echo   Profile   : %SPRING_PROFILES_ACTIVE%
echo   URL        : http://localhost:8080
echo   Swagger UI : http://localhost:8080/swagger-ui
if "%SPRING_PROFILES_ACTIVE%"=="local" (
    echo   H2 Console : http://localhost:8080/h2-console
)
echo   Nhan Ctrl+C de dung.
echo  ===========================================================
echo.

cd /d "%~dp0backend\core"
call "%MAVEN_HOME%\bin\mvn.cmd" spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILES_ACTIVE%

echo.
echo   [!] Spring Boot da dung.
pause
exit /b 0
