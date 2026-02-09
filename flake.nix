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
        url = "https://github.com/facebook/ktfmt/releases/download/v0.61/ktfmt-0.61-with-dependencies.jar";
        sha256 = "b2a6ef02352a4c4ada96610196038129a877d7cddd34fe5290c764bce98cd5f9";
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
