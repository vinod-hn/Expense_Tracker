@echo off
setlocal
where javac >nul 2>&1
if errorlevel 1 (
  echo javac not found in PATH. Ensure JDK bin is added.
  exit /b 1
)
if not exist build mkdir build
if exist sources.txt del sources.txt
echo Collecting sources...
for /R src %%f in (*.java) do (
  echo %%f>>sources.txt
)
if not exist sources.txt (
  echo No source files found under src
  exit /b 1
)
set CP=.;lib/*
echo Compiling...
javac -d build -cp %CP% @sources.txt 2>compile_errors.txt
if errorlevel 1 (
  echo ================= COMPILATION FAILED =================
  type compile_errors.txt
  exit /b 1
) else (
  echo Compilation succeeded.
  del compile_errors.txt 2>nul
)
