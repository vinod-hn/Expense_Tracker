@echo off
REM Package compiled classes into runnable JAR.
if not exist build (echo Build folder missing. Run compile.bat first.& exit /b 1)
if exist ExpenseTracker.jar del ExpenseTracker.jar
jar cfe ExpenseTracker.jar com.expense.Main -C build .
if errorlevel 1 (echo JAR creation failed.& exit /b 1)
echo Created ExpenseTracker.jar
