# Builds the project and starts all three services in separate windows,
# ready for running the Postman collection.
#
# Run from anywhere in PowerShell:
#   & ".\scripts\start-services.ps1"
#
# Needs Java 17+ and Maven. If "mvn" isn't on your PATH, set $mvn below
# to the full path of mvn.cmd.

$repo = Split-Path $PSScriptRoot -Parent
$mvn  = "mvn"
if (-not (Get-Command $mvn -ErrorAction SilentlyContinue)) {
    $local = "$HOME\tools\apache-maven-3.9.9\bin\mvn.cmd"
    if (Test-Path $local) { $mvn = $local } else { Write-Host "Maven not found. Install it or edit `$mvn in this script." -ForegroundColor Red; return }
}

# Use a Java 17 install if one is there (Spring Boot 3 doesn't run on Java 8)
$jdk = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -Directory -ErrorAction SilentlyContinue |
       Where-Object Name -like "jdk-17*" | Select-Object -First 1
if ($jdk) { $env:JAVA_HOME = $jdk.FullName; $env:Path = "$($jdk.FullName)\bin;$env:Path" }
$java = if ($env:JAVA_HOME) { "$env:JAVA_HOME\bin\java.exe" } else { "java" }

Set-Location $repo
Write-Host "Building all services (tests skipped)..." -ForegroundColor Cyan
& $mvn -q -DskipTests package
if ($LASTEXITCODE -ne 0) { Write-Host "Build failed, see errors above." -ForegroundColor Red; return }

$services = @(
    @{ Name = "Farm & Device (8081)";      Dir = "farm-device-service" },
    @{ Name = "Monitoring & Alert (8082)"; Dir = "monitoring-alert-service" },
    @{ Name = "AI & Irrigation (8083)";    Dir = "ai-irrigation-service" }
)
foreach ($s in $services) {
    $jar = Get-ChildItem "$repo\$($s.Dir)\target\*.jar" -Exclude "*.original" | Select-Object -First 1
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "`$host.UI.RawUI.WindowTitle='$($s.Name)'; & '$java' -jar '$($jar.FullName)'"
    Write-Host "Started $($s.Name)" -ForegroundColor Green
}
Write-Host "`nGive them ~10 seconds, then run the Postman collection. Close the windows to stop the services." -ForegroundColor Yellow
