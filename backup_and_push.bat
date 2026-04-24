@echo off
echo ==========================================
echo    HMIS BACKUP AND CHECKPOINT SCRIPT
echo ==========================================
echo.

echo [1/3] Adding files to Git...
git add .

echo [2/3] Committing changes (Checkpoint)...
git commit -m "feat(ehr): finalize premium UI, align with HMIS standard, fix e2e bugs"

echo [3/3] Pushing to GitHub...
git push

echo.
echo ==========================================
echo SUCCESS! Backup and Checkpoint complete.
echo ==========================================
pause
