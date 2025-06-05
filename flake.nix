{
  description = "Food you development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    # Make sure that the version of ktlint is 1.5.0
    ktlint_1_5_0.url = "github:nixos/nixpkgs/d98abf5cf5914e5e4e9d57205e3af55ca90ffc1d";
  };

  outputs =
    {
      self,
      nixpkgs,
      ktlint_1_5_0,
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
        url = "https://github.com/mrmans0n/compose-rules/releases/download/v0.4.22/ktlint-compose-0.4.22-all.jar";
        sha256 = "98118356601fa5817145aebf3887bedd311791a4599ae644c602c52453d9dda2";
      };

      pythonEnv = pkgs.python3.withPackages (ps: with ps; [ pandas openpyxl ]);
    in
    {
      devShells.${system}.default = pkgs.mkShell {
        buildInputs = [
          pkgs.just
          pkgs.temurin-bin-17
          androidComposition.androidsdk
          pythonEnv
        ];

        nativeBuildInputs = [
          ktlint_1_5_0.legacyPackages.${system}.ktlint
        ];

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
