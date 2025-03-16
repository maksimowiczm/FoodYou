{
  description = "Food you development environment";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs?ref=nixos-unstable";
    # Make sure that the version of ktlint is 1.5.0
    ktlint_1_5_0.url = "github:nixos/nixpkgs/d98abf5cf5914e5e4e9d57205e3af55ca90ffc1d";
  };

  outputs =
    { self, nixpkgs, ... }@inputs:
    let
      system = "x86_64-linux";
      pkgs = nixpkgs.legacyPackages.${system};
    in
    {
      devShells.x86_64-linux.default =
        let
          ktlintComposeJar = pkgs.fetchurl {
            url = "https://github.com/mrmans0n/compose-rules/releases/download/v0.4.22/ktlint-compose-0.4.22-all.jar";
            sha256 = "98118356601fa5817145aebf3887bedd311791a4599ae644c602c52453d9dda2";
          };
        in
        pkgs.mkShell {
          KTLINT_COMPOSE_JAR = "${ktlintComposeJar}";

          buildInputs = [
            pkgs.just
            pkgs.temurin-bin-17
          ];

          nativeBuildInputs = with pkgs; [
            inputs.ktlint_1_5_0.legacyPackages.${system}.ktlint
          ];

          shellHook = ''
            alias format="just format"
            echo "Welcome to Food You dev" | ${pkgs.lolcat}/bin/lolcat
          '';
        };
    };
}
