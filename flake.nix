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
      supportedSystems = [
        "x86_64-linux"
        "aarch64-darwin"
      ];

      forAllSystems = nixpkgs.lib.genAttrs supportedSystems;

      pkgsFor =
        system:
        import nixpkgs {
          inherit system;
          config.android_sdk.accept_license = true;
          config.allowUnfree = true;
        };

      buildToolsVersion = "36.0.0";

      androidCompositionFor =
        system:
        let
          pkgs = pkgsFor system;
        in
        pkgs.androidenv.composeAndroidPackages {
          buildToolsVersions = [ buildToolsVersion ];
          systemImageTypes = [ "google_apis_playstore" ];
          abiVersions = [ "arm64-v8a" ];
          includeNDK = false;
          includeEmulator = false;
          includeExtras = [ ];
        };

      ktfmtJar =
        pkgs:
        pkgs.fetchurl {
          url = "https://github.com/facebook/ktfmt/releases/download/v0.62/ktfmt-0.62-with-dependencies.jar";
          sha256 = "f39bf9a1f520d27f86f2bdf4d6dbb2574c05e84f656171ed65c4e534b86b9965";
        };
    in
    {
      devShells = forAllSystems (
        system:
        let
          pkgs = pkgsFor system;
          androidComposition = androidCompositionFor system;
        in
        {
          default = pkgs.mkShell {
            buildInputs = [
              pkgs.nixfmt
              pkgs.just
              pkgs.temurin-bin-21
              pkgs.zensical
              androidComposition.androidsdk
            ];

            KTFMT_JAR = "${ktfmtJar pkgs}";
            ANDROID_HOME = "${androidComposition.androidsdk}/libexec/android-sdk";
            ANDROID_SDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk";
            ANDROID_NDK_ROOT = "${androidComposition.androidsdk}/libexec/android-sdk/ndk-bundle";

            shellHook = ''
              export PATH="$ANDROID_HOME/build-tools/${buildToolsVersion}:$PATH"
              export PATH="$ANDROID_HOME/platform-tools:$PATH"
              just | ${pkgs.lolcat}/bin/lolcat
            '';
          };
        }
      );
    };
}
