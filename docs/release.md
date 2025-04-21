### Release Process

1. Create a new branch: `chore/release-<version>`
2. Update the `versionCode` and `versionName` in `build.gradle` to the new version.
3. Verify that the build is reproducible.
4. Create a changelog for the new version in `metadata/en-US/changelog/<versionCode>.txt` and
   `app/src/commonMain/kotlin/com/maksimowiczm/foodyou/feature/changelog/Changelog.kt`, patch
   notes can be generated using the Github `Draft a new release` feature.
5. Ensure that all metadata in the `metadata` folder is up to date.
6. Merge the branch into `main`.
7. Tag the merge commit with the new version and push the tag. Tag format: `<version>`.
8. Build the app and sign the APK with the release key.
9. Create a new release on GitHub with the release notes and upload the signed APK.
10. Celebrate the new release! 🎉

https://github.com/obfusk/apksigcopier#what-about-signatures-made-by-apksigner-from-build-tools--3500-rc1
