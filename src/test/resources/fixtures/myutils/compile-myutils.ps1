$ErrorActionPreference = 'Stop'
$files = Get-ChildItem -Path "D:\code\ets-har-builder\ets2jsc\src\test\resources\fixtures\myutils\package\src\main\ets\components\*.ets" -File -Filter *.ets
$count = 0
$compiler = "D:\code\ets-har-builder\ets2jsc\src\main\java\com\ets2jsc\EtsCompiler.class"
$compiledDir = "D:\code\ets-har-builder\ets2jsc\target\test-compiled-output\myutils"

if (-not (Test-Path $compiledDir)) {
    New-Item -ItemType Directory -Force | Out-Null
}

foreach ($file in $files) {
    $outputFile = Join-Path $compiledDir $file.Name.Replace(".ets", ".js")
    Write-Host "Compiling $file"
    & $compiler -Dexec.args "src/test/resources/fixtures/myutils/package/src/main/ets/components/$file $outputFile"
    if ($LASTEXITCODE -eq 0) {
        $count++
    }
}

Write-Host "Compiled $count myutils files."
