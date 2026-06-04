@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM

@echo off

setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
setlocal enabledelayedexpansion

set WDIR=%cd%
cd /d "%DIRNAME%"

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
if not "%JAVA_HOME%" == "" goto OkJavaHome

for /f "tokens=*" %%a in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment" /v CurrentVersion') do (
    for /f "tokens=3" %%b in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment\%%b" /v JavaHome') do (
        set JAVA_HOME=%%b
    )
)

if not "%JAVA_HOME%" == "" goto OkJavaHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation. >&2
echo.
goto error

:OkJavaHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation. >&2
echo.
goto error

:init
setlocal

@setlocal

set CLASSPATH=%DIRNAME%.mvn\wrapper\maven-wrapper.jar

set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

"%JAVA_HOME%\bin\java.exe" ^
  "-Dclassworlds.conf=%DIRNAME%.mvn\wrapper\m2.conf" ^
  "-Dmaven.home=%DIRNAME%.mvn" ^
  "-Dmaven.multiModuleProjectDirectory=%DIRNAME%.." ^
  "-Dorg.slf4j.simpleLogger.defaultLogLevel=info" ^
  "-classpath" "%CLASSPATH%" ^
  "%WRAPPER_LAUNCHER%" %*

if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%FORK_MODE%"=="true" (
    exit /b %ERROR_CODE%
)

exit /b %ERROR_CODE%
