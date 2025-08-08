{
  description = "Food you development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
  };

  outputs =
    {
      self,
      nixpkgs,
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

        KTFMT_JAR = "${ktfmtJar}";
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
