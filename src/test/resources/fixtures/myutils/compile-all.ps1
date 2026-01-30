$files = Get-ChildItem -Path "D:\code\ets-har-builder\ets2jsc\src\test\resources\fixtures\myutils\package\src\main\ets\components" -Filter *.ets | Select-Object Name
$compiler = "D:\code\ets-har-builder\ets2jsc\src\main\java\com\ets2jsc\EtsCompiler.class"

$compiledDir = "D:\code\ets-har-builder\ets2jsc\target\test-compiled-output\myutils"

if (-not (Test-Path $compiledDir)) {
    New-Item -ItemType Directory -Path $compiledDir | Out-Null
}

foreach ($file in $files) {
    $outputFile = Join-Path $compiledDir $file.Name
    Write-Host "Compiling: $file to $outputFile"
    & $compiler --batch "src/test/resources/fixtures/myutils/package/src/main/ets/components" $compiledDir
}
