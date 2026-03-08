default:
    @just --list

# $KTFMT_JAR - path to the ktfmt jar file
format:
    @find . -type f \( -name "*.kt" -o -name "*.kts" \) -not -path "*/build/*" | xargs java -jar $KTFMT_JAR --kotlinlang-style

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

preview:
    @./gradlew --no-daemon --no-build-cache clean
    @./gradlew --no-daemon --no-build-cache androidApp:assemblePreview
    @zipalign -f -p -v 4 \
      androidApp/build/outputs/apk/preview/androidApp-preview-unsigned.apk \
      androidApp/build/outputs/apk/preview/aligned.apk
    @apksigner sign \
      --alignment-preserved \
      --ks foodyou.keystore \
      --ks-key-alias foodyou \
      --out ./preview-signed.apk \
      androidApp/build/outputs/apk/preview/aligned.apk

[working-directory: 'docs']
serve:
    zensical serve
