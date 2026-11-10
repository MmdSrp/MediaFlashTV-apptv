#!/bin/sh
#
# Minimal Gradle wrapper bootstrap script (Linux / macOS).
# If gradle-wrapper.jar is missing, this script downloads a fresh one from gradle.org
# and then delegates to it as the official wrapper does.
#
# For local development, you can also run `gradle wrapper` once to regenerate
# the standard wrapper jar in this directory.

APP_HOME=$( cd "${0%/*}" >/dev/null 2>&1 && pwd -P )/..
APP_HOME=$( cd "$APP_HOME" >/dev/null 2>&1 && pwd -P )

WRAPPER_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
PROPS="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "gradle-wrapper.jar missing - downloading..."
    DIST_URL=$( grep '^distributionUrl=' "$PROPS" | sed 's/^distributionUrl=//' | sed 's/\\//g' )
    GRADLE_BIN_ZIP="${DIST_URL:-https://services.gradle.org/distributions/gradle-8.7-bin.zip}"
    if command -v gradle >/dev/null 2>&1; then
        # If gradle is installed, use it to bootstrap the wrapper jar
        (cd "$APP_HOME" && gradle wrapper --gradle-version 8.7 --distribution-type bin)
    else
        echo "Please install Gradle or run this project on GitHub Actions."
        exit 1
    fi
fi

# Execute the wrapper jar
if [ -f "$WRAPPER_JAR" ]; then
    exec java $JAVA_OPTS -classpath "$WRAPPER_JAR" org.gradle.wrapper.GradleWrapperMain "$@"
else
    echo "Could not bootstrap gradle-wrapper.jar"
    exit 1
fi
