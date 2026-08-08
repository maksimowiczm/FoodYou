default:
    @just --list

# $KTFMT_JAR - path to the ktfmt jar file

# Format only changed files (git diff)
format:
    @{ git diff --name-only --diff-filter=ACMR HEAD; \
       git diff --name-only --diff-filter=ACMR --cached; } | \
        sort -u | \
        grep -E '\.kts?$' | \
        xargs --no-run-if-empty java -jar $KTFMT_JAR --kotlinlang-style

# Format every Kotlin file in the project
format-all:
    @find . -type f \( -name "*.kt" -o -name "*.kts" \) -not -path "*/build/*" | \
        xargs java -jar $KTFMT_JAR --kotlinlang-style

release:
    @./gradlew --no-daemon --no-build-cache clean
    @./gradlew --no-daemon --no-build-cache androidApp:assembleRelease
    @zipalign -f -p -v 4 \
      androidApp/build/outputs/apk/release/androidApp-release-unsigned.apk \
      androidApp/build/outputs/apk/release/aligned.apk
    @apksigner sign \
      --alignment-preserved \
      --ks foodyou.keystore \
      --ks-key-alias foodyou \
      --out ./release-signed.apk \
      androidApp/build/outputs/apk/release/aligned.apk

[working-directory('docs')]
serve:
    zensical serve

# Fix apostrophes in strings.xml files
fix-strings:
    @find app/src/commonMain/composeResources -type f -name "strings.xml" | while read -r file; do \
        sed -i "s/\\\'/'/g" "$file"; \
        echo "Processed: $file"; \
    done

# Find unused string resources in .kt files
find-unused-strings:
    #!/usr/bin/env bash
    STRINGS_FILE="app/src/commonMain/composeResources/values/strings.xml"

    ALL_NAMES=$(grep -oP '(?<=<string name=")[^"]+' "$STRINGS_FILE" | sort -u)
    USED_NAMES=$(grep -rohw --include='*.kt' --exclude-dir=build -f <(echo "$ALL_NAMES") . | sort -u)
    UNUSED=$(comm -23 <(echo "$ALL_NAMES") <(echo "$USED_NAMES"))

    if [ -z "$UNUSED" ]; then
        echo -e "\e[32mAll string resources are used!\e[0m"
    else
        echo -e "\e[31mUnused string resources found:\e[0m"
        echo "$UNUSED" | sed 's/^/ - /'
    fi
