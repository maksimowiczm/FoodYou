default:
    @just --list

# KTLINT_COMPOSE_JAR - path to the ktlint-compose jar file
format:
    @ktlint -R $KTLINT_COMPOSE_JAR --editorconfig="./.editorconfig" --format
