@echo off
if exist "student-grade-tracker.jar" (
    start javaw -jar student-grade-tracker.jar
) else (
    start javaw -cp out com.grade.tracker.Main
)
