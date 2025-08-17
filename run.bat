@echo off
setlocal
set CP=build;lib/*
where java >nul 2>&1
if errorlevel 1 (
	echo java not found in PATH. Add JDK bin to PATH.
	exit /b 1
)
java -cp "%CP%" com.expense.Main
