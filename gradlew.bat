@echo off
::
:: Minimal Gradle wrapper bootstrap (Windows).
:: Downloads gradle-wrapper.jar if missing by invoking a system-installed Gradle.
::

setlocal
set DIR=%~dp0
set APP_HOME=%DIR%..

set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
set PROPS=%APP_HOME%\gradle\wrapper\gradle-wrapper.properties

if not exist "%WRAPPER_JAR%" (
    echo gradle-wrapper.jar missing - invoking system gradle to bootstrap...
    where gradle >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        cd /d "%APP_HOME%"
        call gradle wrapper --gradle-version 8.7 --distribution-type bin
    ) else (
        echo Please install Gradle or build via GitHub Actions.
        exit /b 1
    )
)

if exist "%WRAPPER_JAR%" (
    java %JAVA_OPTS% -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
) else (
    echo Could not bootstrap gradle-wrapper.jar
    exit /b 1
)
