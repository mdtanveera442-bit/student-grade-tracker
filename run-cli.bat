@echo off
if exist "student-grade-tracker.jar" (
    java -jar student-grade-tracker.jar --cli
) else (
    java -cp out com.grade.tracker.Main --cli
)
pause
