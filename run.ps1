param(
    [ValidateSet('start', 'test', 'package')]
    [string]$Task = 'start',
    [switch]$Browser
)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

# Honor JAVA_HOME; use the verified portable JDK only when it is not set.
$portableJava = Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot '.tools') -Directory -Filter 'jdk-17*' -ErrorAction SilentlyContinue |
    Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName 'bin/java.exe') } |
    Select-Object -First 1
if (-not $env:JAVA_HOME -and $portableJava) {
    $env:JAVA_HOME = $portableJava.FullName
}
if (-not $env:JAVA_HOME -or -not (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/java.exe'))) {
    throw 'Set JAVA_HOME to your JDK 17 or JDK 21 directory (without /bin).'
}
$javaRelease = Join-Path $env:JAVA_HOME 'release'
if (-not (Test-Path -LiteralPath $javaRelease) -or
    -not (Select-String -LiteralPath $javaRelease -Pattern '^JAVA_VERSION="(17|21)([."+-])' -Quiet)) {
    throw 'Set JAVA_HOME to JDK 17 or JDK 21. JDK 27 is not supported by this project.'
}
$env:PATH = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:PATH
$portableMaven = Join-Path $PSScriptRoot '.tools/apache-maven-3.9.9/bin/mvn.cmd'
if (Test-Path -LiteralPath $portableMaven) {
    $mavenCommand = $portableMaven
    $mavenOptions = @('-Dmaven.repo.local=.tools/repository', '-B', '-ntp')
} else {
    $mavenCommand = (Get-Command mvn.cmd -ErrorAction Stop).Source
    $mavenOptions = @('-B', '-ntp')
}
$goal = switch ($Task) {
    'start' { 'spring-boot:run' }
    'test' { 'test' }
    'package' { 'package' }
}
if ($Browser) { $mavenOptions += '-Dcatalog.browser-test=true' }
& $mavenCommand @mavenOptions $goal
exit $LASTEXITCODE
