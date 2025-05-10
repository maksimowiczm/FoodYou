default:
    @just --list

# KTLINT_COMPOSE_JAR - path to the ktlint-compose jar file
format:
    @ktlint -R $KTLINT_COMPOSE_JAR --editorconfig="./.editorconfig" --format

release:
    @./gradlew clean
    @./gradlew --no-daemon assembleRelease
    @zipalign -f -p -v 4 \
      app/build/outputs/apk/release/app-release-unsigned.apk \
      app/build/outputs/apk/release/aligned.apk
    @apksigner sign \
      --alignment-preserved \
      --ks foodyou.keystore \
      --ks-key-alias foodyou \
      --out ./release-signed.apk \
      app/build/outputs/apk/release/aligned.apk
