#!/bin/bash

STRINGS_FILE="core3/src/commonMain/composeResources/values/strings.xml"
STRING_NAMES=$(grep -oP '(?<=<string name=")[^"]+' "$STRINGS_FILE")
EXCLUDED_KEYS=("app_name")

PROJECT_DIR="."

echo "🔍 Scanning .kt files for unused string resources..."

UNUSED_STRINGS=()

for STRING_NAME in $STRING_NAMES; do
    if [[ " ${EXCLUDED_KEYS[*]} " == *" $STRING_NAME "* ]]; then
        continue
    fi

    USAGE=$(find "$PROJECT_DIR" -type d -name build -prune -o -name "*.kt" -type f -exec grep -w "$STRING_NAME" {} +)

    if [ -z "$USAGE" ]; then
        UNUSED_STRINGS+=("$STRING_NAME")
    fi
done

echo ""
if [ ${#UNUSED_STRINGS[@]} -eq 0 ]; then
    echo "✅ All string resources are used in .kt files!"
else
    echo "🚨 Unused string resources found:"
    for STR in "${UNUSED_STRINGS[@]}"; do
        echo " - $STR"
    done
fi
