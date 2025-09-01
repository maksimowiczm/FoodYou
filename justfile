default:
    @just --list

# $KTFMT_JAR - path to the ktfmt jar file
format:
    @find . -type f \( -name "*.kt" -o -name "*.kts" \) -not -path "*/build/*" | xargs java -jar $KTFMT_JAR --kotlinlang-style

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

screenshots output="screenshots/":
    @adb shell rm -fr /sdcard/Pictures/com.maksimowiczm.foodyou
    @./gradlew -Pandroid.testInstrumentationRunnerArguments.class=com.maksimowiczm.foodyou.screenshot.GenerateAppMetadataScreenshots composeApp:connectedAndroidTest
    @mkdir -p {{ output }}
    @adb shell find /sdcard/Pictures/com.maksimowiczm.foodyou -iname "*.png" | while read line; do adb pull "$line" {{ output }}; done
