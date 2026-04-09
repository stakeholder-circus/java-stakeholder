{
  description = "stakeholder-circus java-stakeholder";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { self, nixpkgs }:
    let
      systems = [ "x86_64-linux" "aarch64-darwin" "x86_64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs systems;
    in {
      devShells = forAllSystems (system:
        let pkgs = import nixpkgs { inherit system; };
        in {
          default = pkgs.mkShell {
            packages = with pkgs; [ jdk25 maven docker ];
          };
        });
      apps = forAllSystems (system:
        let pkgs = import nixpkgs { inherit system; };
            mk = name: text: {
              type = "app";
              program = "${pkgs.writeShellScript name text}";
            };
        in {
          build = mk "build" ''./mvnw -q test package'';
          test = mk "test" ''./mvnw -q test'';
          check = mk "check" ''./mvnw -q spotless:check checkstyle:check spotbugs:check test package'';
          format = mk "format" ''./mvnw -q spotless:check'';
        });
    };
}
