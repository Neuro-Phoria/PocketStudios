#!/bin/sh
# Gradle wrapper script — this is a stub
# The real gradlew binary is downloaded by Android Studio automatically
# Or run: gradle wrapper --gradle-version 8.9
exec "$JAVA_HOME/bin/java" \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$@"
