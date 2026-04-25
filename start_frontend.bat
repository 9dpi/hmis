@echo off
title HMIS Frontend - Local Server
echo.
echo  ===========================================================
echo       HMIS FRONTEND - Local Web Server
echo  ===========================================================
echo.

:: Kiem tra xem co Python khong
where python >nul 2>nul
if %errorlevel% equ 0 (
    echo   [OK] Su dung Python HTTP Server
    echo   Frontend: http://localhost:3000/ehr-test.html
    echo   Dashboard: http://localhost:3000/index.html
    echo.
    echo   Nhan Ctrl+C de dung.
    echo  ===========================================================
    echo.
    start "" "http://localhost:3000/ehr-test.html"
    cd /d "%~dp0"
    python -m http.server 3000
    goto END
)

:: Kiem tra xem co npx khong (Node.js)
where npx >nul 2>nul
if %errorlevel% equ 0 (
    echo   [OK] Su dung npx serve
    echo   Frontend se tu mo trong trinh duyet...
    echo.
    echo   Nhan Ctrl+C de dung.
    echo  ===========================================================
    echo.
    cd /d "%~dp0"
    npx -y serve -l 3000 -s .
    goto END
)

:: Khong co Python va Node.js - dung PowerShell
echo   [INFO] Khong tim thay Python/Node.js
echo   [OK] Su dung PowerShell HTTP Server
echo   Frontend: http://localhost:3000/ehr-test.html
echo   Dashboard: http://localhost:3000/index.html
echo.
echo   Nhan Ctrl+C de dung.
echo  ===========================================================
echo.
start "" "http://localhost:3000/ehr-test.html"
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$listener = New-Object System.Net.HttpListener; $listener.Prefixes.Add('http://localhost:3000/'); $listener.Start(); Write-Host '  [OK] Server dang chay tai http://localhost:3000'; while ($listener.IsListening) { $ctx = $listener.GetContext(); $req = $ctx.Request; $resp = $ctx.Response; $path = $req.Url.LocalPath; if ($path -eq '/') { $path = '/index.html' }; $file = Join-Path '%~dp0' ($path -replace '/','\'); if (Test-Path $file -PathType Leaf) { $bytes = [IO.File]::ReadAllBytes($file); $ext = [IO.Path]::GetExtension($file).ToLower(); $ct = switch($ext) { '.html' {'text/html;charset=utf-8'} '.css' {'text/css'} '.js' {'application/javascript'} '.json' {'application/json'} '.png' {'image/png'} '.jpg' {'image/jpeg'} '.svg' {'image/svg+xml'} '.ico' {'image/x-icon'} '.woff2' {'font/woff2'} default {'application/octet-stream'} }; $resp.ContentType = $ct; $resp.ContentLength64 = $bytes.Length; $resp.OutputStream.Write($bytes, 0, $bytes.Length) } else { $resp.StatusCode = 404; $msg = [Text.Encoding]::UTF8.GetBytes('Not Found'); $resp.OutputStream.Write($msg, 0, $msg.Length) }; $resp.Close(); Write-Host \"  $($req.HttpMethod) $($req.Url.LocalPath) -> $($resp.StatusCode)\" }"

:END
echo.
echo   [!] Server da dung.
pause
