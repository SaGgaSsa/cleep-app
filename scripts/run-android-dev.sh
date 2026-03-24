#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
ANDROID_DIR="$ROOT_DIR/android-native"
ENV_FILE="$ANDROID_DIR/.env.dev"
GRADLEW="$ANDROID_DIR/gradlew"
LOCAL_PROPERTIES_FILE="$ANDROID_DIR/local.properties"

if [[ ! -f "$ENV_FILE" ]]; then
  cat <<EOF
Missing $ENV_FILE

Create it from:
  $ANDROID_DIR/.env.example
EOF
  exit 1
fi

# shellcheck disable=SC1090
source "$ENV_FILE"

if [[ -z "${BASE_URL:-}" ]]; then
  echo "Missing BASE_URL in $ENV_FILE" >&2
  exit 1
fi

if [[ -z "${GOOGLE_SERVER_CLIENT_ID:-}" ]]; then
  echo "Missing GOOGLE_SERVER_CLIENT_ID in $ENV_FILE" >&2
  exit 1
fi

if ! command -v adb >/dev/null 2>&1; then
  cat <<EOF
adb is not available in PATH.

Install Android SDK platform-tools and ensure adb is available before running this script.
EOF
  exit 1
fi

mapfile -t DEVICES < <(adb devices | awk 'NR>1 && $2=="device" {print $1}')

if [[ ${#DEVICES[@]} -eq 0 ]]; then
  cat <<EOF
No adb devices detected.

Connect a phone by USB or wireless debugging and verify it appears in:
  adb devices
EOF
  exit 1
fi

if [[ -n "${ANDROID_SERIAL:-}" ]]; then
  DEVICE="$ANDROID_SERIAL"
elif [[ ${#DEVICES[@]} -eq 1 ]]; then
  DEVICE="${DEVICES[0]}"
else
  cat <<EOF
Multiple adb devices detected.

Set ANDROID_SERIAL to one of:
$(printf '  %s\n' "${DEVICES[@]}")
EOF
  exit 1
fi

if [[ ! -x "$GRADLEW" ]]; then
  cat <<EOF
Android native Gradle wrapper not found at:
  $GRADLEW

This repo already defines the CLI workflow, but the Compose project has not been created yet.
Next step: bootstrap the Android native project inside android-native/ and add gradlew there.
EOF
  exit 1
fi

SDK_DIR="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"

if [[ -z "$SDK_DIR" ]]; then
  for candidate in "$HOME/Android/Sdk" /usr/lib/android-sdk /opt/android-sdk; do
    if [[ -d "$candidate" ]]; then
      SDK_DIR="$candidate"
      break
    fi
  done
fi

if [[ -z "$SDK_DIR" ]]; then
  cat <<EOF
Android SDK directory could not be detected.

Set ANDROID_SDK_ROOT or ANDROID_HOME before running this script.
EOF
  exit 1
fi

cat >"$LOCAL_PROPERTIES_FILE" <<EOF
sdk.dir=$SDK_DIR
EOF

cd "$ANDROID_DIR"

"$GRADLEW" \
  -Pcleep.baseUrl="$BASE_URL" \
  -Pcleep.googleServerClientId="$GOOGLE_SERVER_CLIENT_ID" \
  installDebug

APP_ID="${APPLICATION_ID:-dev.cleep.app.debug}"

adb -s "$DEVICE" shell monkey -p "$APP_ID" -c android.intent.category.LAUNCHER 1 >/dev/null
echo "App installed and launch command sent to $DEVICE"
