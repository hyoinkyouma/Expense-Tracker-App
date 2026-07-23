#!/usr/bin/env bash
#Gradle wrapper startup script for Unix
#This is a simplified version to test compilation

GRADLE_HOME=$(dirname "$0")
java -jar "$GRADLE_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
