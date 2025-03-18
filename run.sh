#!/usr/bin/env bash

# Global variables
CURRENT_PATH="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"
DC_FILE="$CURRENT_PATH/docker-compose.yml"
DC_PROD_FILE="$CURRENT_PATH/docker-compose-prod.yml"
IMAGE_NAME="breizhcamp/kalon:latest"

ARGS=()
HELP=0 VERBOSE=0
PROD=0 UP=0 DOWN=0 BUILD=0 LINT=0

source "$CURRENT_PATH/libs/utils.sh"

function display_help() {
    local output=""
    output="
$(colors 'Y')Usage$(colors 'W') $(basename "$0") [OPTIONS] COMMAND
$(colors 'Y')Commands:$(colors 'N')
$(colors 'G')up$(colors 'W')                  Initialize and creating containers
$(colors 'G')down$(colors 'W')                Stopping/removing containers, networks and volumes
$(colors 'G')build$(colors 'W')               Building docker image
$(colors 'G')lint$(colors 'W')                Use hadolint to lint the Dockerfile
$(colors 'Y')Options:$(colors 'N')
$(colors 'G')-p, --prod$(colors 'W')          Starting kalon container
$(colors 'G')-v, --verbose$(colors 'W')       Make the command more talkative
$(colors 'G')-h, --help$(colors 'W')          Display help
    "
    echo -e "$output\n"|sed '1d; $d'
    return 0
}

function up() {
    if ! [[ -f .env  ]]; then
      info "Creating an .env file"
      cp .env-sample .env
    fi
    info "Creating and starting Docker containers"
    # postgresql
    debug "docker compose -f $DC_FILE up --build -d"
    ! docker compose -f "$DC_FILE" up --build -d && return 1
    # kalon
    if [[ $PROD -gt 0 ]]; then
        debug "docker compose -f $DC_PROD_FILE up --build -d"
        ! docker compose -f "$DC_PROD_FILE" up --build -d && return 2
    fi
    return 0
}

function down() {
    info "Remove Docker containers, networks and volumes"
    # postgresql
    debug "docker compose -f $DC_FILE down --volumes"
    ! docker compose -f "$DC_FILE" down --volumes && return 1
    # kalon
    debug "docker compose -f $DC_PROD_FILE down --volumes"
    ! docker compose -f "$DC_PROD_FILE" down --volumes && return 2
    return 0
}

function build() {
    info "Build Docker image"
    debug "docker compose -f $DC_PROD_FILE build"
    ! docker compose -f "$DC_PROD_FILE" build && return 1
    if [ -z "$(docker images -q $IMAGE_NAME 2> /dev/null)" ]; then
      error "Docker image failed to be created"
      return 2
    fi
    ! scan_trivy && return 1
    return 0
}

function scan_trivy() {
    info "Execute a scan to find vulnerabilities"
    local cmd_trivy="docker run --rm -v /var/run/docker.sock:/var/run/docker.sock aquasec/trivy:0.18.3 --severity HIGH,CRITICAL $IMAGE_NAME"
    debug "$cmd_trivy"
    exec $cmd_trivy
    return 0
}

function lint_dockerfile() {
    info "Run hadolint"
    debug "docker run --rm -i hadolint/hadolint < Dockerfile"
    ! docker run --rm -i hadolint/hadolint < Dockerfile && return 1
    return 0
}

# Check options passed as script parameters.
# shellcheck disable=SC2034
function check_opts() {
    read -ra opts <<< "$@"
    for opt in "${opts[@]}"; do
        case "$opt" in
            up) UP=1 ; ARGS+=("$opt") ;;
            down) DOWN=1 ; ARGS+=("$opt") ;;
            build) BUILD=1 ; ARGS+=("$opt") ;;
            lint) LINT=1 ;;
            --prod|-p) PROD=1 ;;
            --verbose|-v) VERBOSE=1 ;;
            --help) HELP=1 ;;
            *) ARGS+=("$opt") ;;
        esac
    done
    # Help is displayed if no option is passed as script parameter
    if [[ $((HELP+UP+DOWN+BUILD+LINT)) -eq 0 ]]; then
        HELP=1
    fi
    return 0
}

function main() {
    # Check options passed as script parameters and execute tasks
    ! check_opts "$@" && return 1
    # Display help
    if [[ $HELP -gt 0 ]]; then
        ! display_help && return 2
        return 0
    fi
    # Starting Docker containers
    if [[ $UP -gt 0 ]]; then
        ! up && return 3
    fi
    # Stopping and remove containers, networks and volumes
    if [[ $DOWN -gt 0 ]]; then
        ! down && return 4
    fi
    # Building docker image
    if [[ $BUILD -gt 0 ]]; then
        ! build && return 5
    fi
    # Lint Dockerfile
    if [[ $LINT -gt 0 ]]; then
        ! lint_dockerfile && return 6
    fi
    return 0
}

main "$@"
