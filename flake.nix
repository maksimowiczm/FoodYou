{
  description = "Food you development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      flake-utils,
    }:
    flake-utils.lib.eachSystem [ "x86_64-linux" "aarch64-darwin" ] (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          config.android_sdk.accept_license = true;
          config.allowUnfree = true;
        };

        buildToolsVersion = "36.0.0";
        platformVersion = "36";

        androidComposition = pkgs.androidenv.composeAndroidPackages {
          platformVersions = [ platformVersion ];
          buildToolsVersions = [ buildToolsVersion ];
          includeEmulator = true;
          includeNDK = false;
          abiVersions = [
            (if system == "aarch64-darwin" then "arm64-v8a" else "x86_64")
          ];
          includeSystemImages = true;
          systemImageTypes = [ "google_apis" ];
        };

        ktfmtJar = pkgs.fetchurl {
          url = "https://github.com/facebook/ktfmt/releases/download/v0.64/ktfmt-0.64-with-dependencies.jar";
          sha256 = "b8fbb814808d8da33f74a7bbacb6d1748cef81c0202a7f829b87139520b51273";
        };
      in
      {
        devShells.default = pkgs.mkShell {
          buildInputs = with pkgs; [
            just
            temurin-bin-21
            zensical
            androidComposition.androidsdk
          ];

          KTFMT_JAR = "${ktfmtJar}";

          ANDROID_HOME = "${androidComposition.androidsdk}/libexec/android-sdk";
          ANDROID_SDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk";
          ANDROID_NDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk/ndk-bundle";
          GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidComposition.androidsdk}/libexec/android-sdk/build-tools/${buildToolsVersion}/aapt2";

          shellHook = ''
            export PATH="$ANDROID_HOME/build-tools/${buildToolsVersion}:$PATH"
            export PATH="$ANDROID_HOME/platform-tools:$PATH"
            export PATH="$ANDROID_HOME/emulator:$PATH"
            export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

            just | ${pkgs.lolcat}/bin/lolcat
          '';
        };
      }
    );
}
