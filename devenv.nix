{
  pkgs,
  ...
}:
{
  languages.java.enable = true;

  packages = with pkgs; [
    just
    lolcat
    nixfmt
    zensical
  ];

  env.KTFMT_JAR = "${pkgs.fetchurl {
    url = "https://github.com/facebook/ktfmt/releases/download/v0.62/ktfmt-0.62-with-dependencies.jar";
    sha256 = "f39bf9a1f520d27f86f2bdf4d6dbb2574c05e84f656171ed65c4e534b86b9965";
  }}";

  enterShell = ''
    just | lolcat
  '';
}
