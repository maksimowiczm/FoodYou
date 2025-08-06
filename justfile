default:
    @just --list

# KTLINT_COMPOSE_JAR - path to the ktlint-compose jar file
format:
    @ktlint -R $KTLINT_COMPOSE_JAR --editorconfig="./.editorconfig" --format

# $KTFMT_JAR - path to the ktfmt jar file
ktfmt:
    @find ./business ./shared -type f \( -name "*.kt" -o -name "*.kts" \) -not -path "*/build/*" | xargs java -jar $KTFMT_JAR --kotlinlang-style

release:
    @./gradlew --no-daemon --no-build-cache clean
    @./gradlew --no-daemon --no-build-cache assembleRelease
    @zipalign -f -p -v 4 \
      composeApp/build/outputs/apk/release/composeApp-release-unsigned.apk \
      composeApp/build/outputs/apk/release/aligned.apk
    @apksigner sign \
      --alignment-preserved \
      --ks foodyou.keystore \
      --ks-key-alias foodyou \
      --out ./release-signed.apk \
      composeApp/build/outputs/apk/release/aligned.apk

preview:
    @./gradlew --no-daemon --no-build-cache clean
    @./gradlew --no-daemon --no-build-cache assemblePreview
    @zipalign -f -p -v 4 \
      composeApp/build/outputs/apk/preview/composeApp-preview-unsigned.apk \
      composeApp/build/outputs/apk/preview/aligned.apk
    @apksigner sign \
      --alignment-preserved \
      --ks foodyou.keystore \
      --ks-key-alias foodyou \
      --out ./preview-signed.apk \
      composeApp/build/outputs/apk/preview/aligned.apk
