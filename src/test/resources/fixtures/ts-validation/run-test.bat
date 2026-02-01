@echo off
setlocal enabledelayedexpansion
set "TEST_FILE=%~1"
set "TS_FILE=%~2"
set "JS_FILE=%~3"

:: Execute TypeScript with ts-node
echo Executing TypeScript...
npx ts-node --esm --experimentalSpecifierResolution=node "%TS_FILE%" > "%TEST_FILE%-ts.log" 2>&1

:: Execute JavaScript
echo Executing JavaScript...
node "%JS_FILE%" > "%TEST_FILE%-js.log" 2>&1

echo Execution complete
