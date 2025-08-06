{
  description = "Food you development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    # Make sure that the version of ktlint is 1.6.0
    ktlint_1_6_0.url = "github:nixos/nixpkgs/e6f23dc08d3624daab7094b701aa3954923c6bbb";
  };

  outputs =
    {
      self,
      nixpkgs,
      ktlint_1_6_0,
      ...
    }@inputs:
    let
      system = "x86_64-linux";

      pkgs = import nixpkgs {
        system = system;
        config.android_sdk.accept_license = true;
        config.allowUnfree = true;
      };

      buildToolsVersion = "36.0.0";

      androidComposition = pkgs.androidenv.composeAndroidPackages {
        buildToolsVersions = [ buildToolsVersion ];
        systemImageTypes = [ "google_apis_playstore" ];
        abiVersions = [ "arm64-v8a" ];
        includeNDK = false;
        includeEmulator = false;
        includeExtras = [ ];
      };

      ktlintComposeJar = pkgs.fetchurl {
        url = "https://github.com/mrmans0n/compose-rules/releases/download/v0.4.24/ktlint-compose-0.4.24-all.jar";
        sha256 = "196a8aed6ca2bde9c02efeb13672881d99c733b3606fe65fe87a241655bb5d31";
      };

      ktfmtJar = pkgs.fetchurl {
        url = "https://github.com/facebook/ktfmt/releases/download/v0.56/ktfmt-0.56-with-dependencies.jar";
        sha256 = "49b6b92baf2fc22562a96ba9522bd9eddc0f79706af830fbea0b2a159d57900c";
      };

      pythonEnv = pkgs.python3.withPackages (ps: with ps; [ pandas openpyxl ]);
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        buildInputs = [
          pkgs.just
          pkgs.temurin-bin-21
          androidComposition.androidsdk
          pythonEnv
        ];

        nativeBuildInputs = [
          ktlint_1_6_0.legacyPackages.${system}.ktlint
        ];

        KTFMT_JAR = "${ktfmtJar}";
        KTLINT_COMPOSE_JAR = "${ktlintComposeJar}";
        ANDROID_HOME = "${androidComposition.androidsdk}/libexec/android-sdk";
        ANDROID_SDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk";
        ANDROID_NDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk/ndk-bundle";

        shellHook = ''
          export PATH="$ANDROID_HOME/build-tools/${buildToolsVersion}:$PATH"
          export PATH="$ANDROID_HOME/platform-tools:$PATH"
          just | ${pkgs.lolcat}/bin/lolcat
        '';
      };
    };
}
