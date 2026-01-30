@echo off
cd /d "D:\code\ets-har-builder\ets2jsc"
cd src\test\resources\fixtures\myutils\package\src\main\ets\components
for %%f in (*.ets) do (
    echo Compiling: %%f
    call mvn compile -q -f "%%f"
)
echo.
echo Done compiling myutils components.
cd /d "D:\code\ets-har-builder\ets2jsc"
