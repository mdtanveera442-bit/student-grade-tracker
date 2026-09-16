@echo off
echo ===================================================
echo   Building Student Grade Tracker
echo ===================================================

if not exist "out" mkdir "out"

echo Compiling Java source files...
javac -d out src\com\grade\tracker\*.java src\com\grade\tracker\model\*.java src\com\grade\tracker\service\*.java src\com\grade\tracker\util\*.java src\com\grade\tracker\ui\cli\*.java src\com\grade\tracker\ui\gui\*.java test\com\grade\tracker\*.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo Packaging student-grade-tracker.jar...
"C:\Program Files\Java\latest\jdk-26\bin\jar.exe" cfe student-grade-tracker.jar com.grade.tracker.Main -C out com

if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] Build completed! JAR file generated at student-grade-tracker.jar
) else (
    echo [WARNING] Could not package JAR using explicit JDK path, running javac output directly via out/
)

pause
