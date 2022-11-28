#!/usr/bin/env bash

TEMPLATE_NAME='tabula'
SERVICE_NAME_PREFIX='dcl'
PRETTY_NAME_PREFIX='Decaland'
ROOT_PACKAGE='com.github.decaland'
ROOT_PACKAGE_DIRS='com/github/decaland'

main() {
  define_console_colors
  check_bash_version
  where_am_i
  parse_options "$@"
  ensure_service_can_be_created

  announce_intentions
  prompt_user

  create_service
}

define_console_colors() {
  RED="$( printf '\033[31m' )"
  GREEN="$( printf '\033[32m' )"
  YELLOW="$( printf '\033[33m' )"
  BOLD="$( printf '\033[1m' )"
  NORMAL="$( printf '\033[0m' )"
  SUCCESS="${BOLD}[${GREEN}SUCCESS${NORMAL}${BOLD}]${NORMAL}"
  WARNING="${BOLD}[${YELLOW}WARNING${NORMAL}${BOLD}]${NORMAL}"
  ERROR="${BOLD}[${RED}ERROR  ${NORMAL}${BOLD}]${NORMAL}"
}

check_bash_version() {
  # Retrieve and inspect major Bash version
  case ${BASH_VERSION:0:1} in
    3|4|5|6)  :;;
    *)        printf >&2 "%s Unsupported/unrecognized version of Bash: '%s' (3+ required)\n" "$ERROR" "${BASH_VERSION}"
              exit 1;;
  esac
}

where_am_i() {
  # Declare holder variables
  SCRIPT_DIRPATH=
  TEMPLATE_DIRPATH=
  PARENT_DIRPATH=
  SCRIPT_FILENAME=

  # Locate this script
  local filepath="${BASH_SOURCE[0]}" dirpath
  SCRIPT_FILENAME="$( basename -- "$filepath" )"
  while [ -L "$filepath" ]; do
    dirpath="$( cd -P "$( dirname -- "$filepath" )" &>/dev/null && pwd )"
    filepath="$( readlink -- "$filepath" )"
    [[ $filepath != /* ]] && filepath="$dirpath/$filepath"
  done
  filepath="$( cd -P "$( dirname -- "$filepath" )" &>/dev/null && pwd )/$( basename -- "$filepath" )"
  SCRIPT_DIRPATH="$( dirname -- "$filepath" )"
  TEMPLATE_DIRPATH="$SCRIPT_DIRPATH/$SERVICE_NAME_PREFIX-$TEMPLATE_NAME"
  PARENT_DIRPATH="$( dirname -- "$SCRIPT_DIRPATH" )"
}

parse_options() {
  # Declare holder variables
  SERVICE_NAME=
  PRETTY_NAME=
  PACKAGE_NAME=

  service_name_candidates=()
  pretty_name_candidates=()
  package_name_candidates=()

  # Parse arguments
  local arg opt i erra=()
  while (($#)); do
    arg="$1"; shift
    case $arg in
      -*) case ${arg:1} in
        -)                service_name_candidates+=("$@"); break;;
        h|-pretty-name)   if (($#)); then
                            pretty_name_candidates+=("$1"); shift
                          else
                            erra+=( "- option '$arg' requires argument" )
                          fi;;
        p|-package-name)  if (($#)); then
                            package_name_candidates+=("$1"); shift
                          else
                            erra+=( "- option '$arg' requires argument" )
                          fi;;
        '')               erra+=( -i- "- unrecognized option '-'" );;
        -*)               erra+=( -i- "- unrecognized option '$arg'" );;
        *)                for ((i=1;i<${#arg};++i)); do
                            opt="${arg:i:1}"
                            case $opt in
                              h)  if (($#)); then
                                    pretty_name_candidates+=("$1"); shift
                                  else
                                    erra+=( "- option '$opt' requires argument" )
                                  fi;;
                              p)  if (($#)); then
                                    package_name_candidates+=("$1"); shift
                                  else
                                    erra+=( "- option '$opt' requires argument" )
                                  fi;;
                              *)  erra+=( "- unrecognized option '$opt'" );;
                            esac
                          done
          esac;;
      *)  [ -n "$arg" ] && service_name_candidates+=("$arg");;
    esac
  done

  # Check service name
  if [ ${#service_name_candidates[@]} -eq 0 ]; then
    erra+=( "- service name is not provided" )
  elif [ ${#service_name_candidates[@]} -gt 1 ]; then
    set -- "${service_name_candidates[@]}"; shift
    while (($#)); do
      erra+=( "- extra argument provided: '$1'" )
    done
  else
    local service_name="${service_name_candidates[0]}"
    if service_name_is_valid "$service_name"; then
      SERVICE_NAME="$( normalize_service_name "$service_name" )"
    else
      erra+=( "- invalid service name: '$service_name'" )
    fi
  fi

  # Check pretty name
  if [ ${#pretty_name_candidates[@]} -eq 0 ]; then
    if [ -n "$SERVICE_NAME" ]; then
      PRETTY_NAME="$( service_name_to_pretty_name "$SERVICE_NAME" )"
    fi
  elif [ ${#pretty_name_candidates[@]} -gt 1 ]; then
    set -- "${pretty_name_candidates[@]}"; shift
    while (($#)); do
      erra+=( "- extra pretty name provided: '$1'" )
    done
  else
    local pretty_name="${pretty_name_candidates[0]}"
    if pretty_name_is_valid "$pretty_name"; then
      PRETTY_NAME="$pretty_name"
    else
      erra+=( "- invalid pretty name: '$pretty_name'" )
    fi
  fi

  # Check package name
  if [ ${#package_name_candidates[@]} -eq 0 ]; then
    if [ -n "$SERVICE_NAME" ]; then
      PACKAGE_NAME="$( service_name_to_package_name "$SERVICE_NAME" )"
    fi
  elif [ ${#package_name_candidates[@]} -gt 1 ]; then
    set -- "${package_name_candidates[@]}"; shift
    while (($#)); do
      erra+=( "- extra package name provided: '$1'" )
    done
  else
    local package_name="${package_name_candidates[0]}"
    if package_name_is_valid "$package_name"; then
      PACKAGE_NAME="$package_name"
    else
      erra+=( "- invalid package name: '$package_name'" )
    fi
  fi

  # Check if any errors were encountered
  if [ ${#erra[@]} -gt 0 ]; then
    printf >&2 '%s Invalid request:\n' "$ERROR"
    printf >&2 '  %s\n' "${erra[@]}"
    printf >&2 '\nUsage:\n%s <SERVICE_NAME> [-h|--pretty-name <PRETTY_NAME>] [-p|--package-name <PACKAGE_NAME>]\n' "$SCRIPT_FILENAME"
    exit 1
  fi
}

ensure_service_can_be_created() {
  FULL_SERVICE_NAME="$SERVICE_NAME_PREFIX-$SERVICE_NAME"
  FULL_PRETTY_NAME="$PRETTY_NAME_PREFIX $PRETTY_NAME"
  SERVICE_DIRPATH="$PARENT_DIRPATH/$FULL_SERVICE_NAME"
  if ! [ -d "$TEMPLATE_DIRPATH" ]; then
    printf >&2 "%s Template project path is not a directory: %s\n" \
      "$ERROR" "$TEMPLATE_DIRPATH"
    exit 1
  fi
  if [ "$SERVICE_NAME" = "$TEMPLATE_NAME" ]; then
    printf >&2 "%s Cannot name new service the same as the template: '%s'\n" \
      "$ERROR" "$TEMPLATE_NAME"
    exit 1
  fi
  if [ -e "$SERVICE_DIRPATH" ]; then
    printf >&2 '%s Path already exists: %s\n' "$ERROR" "$SERVICE_DIRPATH"
    exit 1
  fi
}

announce_intentions() {
  printf >&2 'About to create a new Decaland service:\n'
  printf >&2 'Service path      : %s\n' "${GREEN}${BOLD}${SERVICE_DIRPATH}${NORMAL}"
  printf >&2 'Service name      : %s\n' "${GREEN}${BOLD}${FULL_SERVICE_NAME}${NORMAL}"
  printf >&2 'Pretty name       : %s\n' "${GREEN}${BOLD}${FULL_PRETTY_NAME}${NORMAL}"
  printf >&2 'Root Java package : %s\n' "${GREEN}${BOLD}${ROOT_PACKAGE}.${PACKAGE_NAME}${NORMAL}"
}

prompt_user() {
  local input
  printf >&2 'Proceed? (y/n) '
  while true; do
    read -rsn1 input
    case $input in
      y|Y)  printf >&2 '%s\n' 'y'; return 0;;
      n|N)  printf >&2 '%s\n' 'n'; exit 1;;
    esac
  done
}

create_service() {
  printf >&2 "\nCreating '%s'...\n" "${GREEN}${BOLD}${FULL_SERVICE_NAME}${NORMAL}"

  if mkdir "$SERVICE_DIRPATH"; then
    printf >&2 'Created directory : %s\n' "$SERVICE_DIRPATH"
  else
    printf >&2 '%s Failed while creating service directory: %s\n' "$ERROR" "$SERVICE_DIRPATH"
    exit 1
  fi

  local src_path dst_path
  local package_path resources_path

  local module_name src_type dir_type
  for module_name in 'app' 'client' 'model'; do
    src_path="$TEMPLATE_DIRPATH/$SERVICE_NAME_PREFIX-$TEMPLATE_NAME-$module_name"
    dst_path="$SERVICE_DIRPATH/$FULL_SERVICE_NAME-$module_name"
    if cp -r -- "$src_path" "$dst_path"; then
      printf >&2 'Copied directory  : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while copying directory: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
    for src_type in 'main' 'test'; do
      for dir_type in 'java' 'kotlin'; do
        package_path="$dst_path/src/$src_type/$dir_type/$ROOT_PACKAGE_DIRS/$PACKAGE_NAME"
        if mkdir -p "$package_path"; then
          printf >&2 'Created directory : %s\n' "$package_path"
        else
          printf >&2 '%s Failed while creating directory: %s\n' "$ERROR" "$package_path"
          exit 1
        fi
      done
      resources_path="$dst_path/src/$src_type/resources"
      if mkdir -p "$resources_path"; then
        printf >&2 'Created directory : %s\n' "$resources_path"
      else
        printf >&2 '%s Failed while creating directory: %s\n' "$ERROR" "$resources_path"
        exit 1
      fi
    done

    # Special treatment for build.gradle
    dst_path="$SERVICE_DIRPATH/$FULL_SERVICE_NAME-$module_name/build.gradle"
    if sed --version &>/dev/null; then
      sed -i "s/$ROOT_PACKAGE\.$TEMPLATE_NAME/$ROOT_PACKAGE.$PACKAGE_NAME/g" "$dst_path" \
        && sed -i "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
    else
      sed -i '' "s/$ROOT_PACKAGE\.$TEMPLATE_NAME/$ROOT_PACKAGE.$PACKAGE_NAME/g" "$dst_path" \
        && sed -i '' "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
    fi
    if [ $? -eq 0 ]; then
      printf >&2 'Modified file     : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while modifying file: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
  done

  local dir_names=(
    'gradle'
  )
  local file_names=(
    '.gitattributes'
    '.gitignore'
    'Dockerfile'
    'gradle.properties'
    'gradlew'
    'gradlew.bat'
    'gradlew.bat'
    'LICENSE'
    'README.template'
    'settings.gradle'
  )
  local dir_name file_name

  for dir_name in "${dir_names[@]}"; do
    src_path="$TEMPLATE_DIRPATH/$dir_name"
    dst_path="$SERVICE_DIRPATH/$dir_name"
    if cp -r -- "$src_path" "$dst_path"; then
      printf >&2 'Copied directory  : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while copying directory: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
  done

  for file_name in "${file_names[@]}"; do
    src_path="$TEMPLATE_DIRPATH/$file_name"
    dst_path="$SERVICE_DIRPATH/$file_name"
    if cp -- "$src_path" "$dst_path"; then
      printf >&2 'Copied file       : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while copying file: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
  done

  # Special treatment for Dockerfile
  dst_path="$SERVICE_DIRPATH/Dockerfile"
  if sed --version &>/dev/null; then
    sed -i "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
  else
    sed -i '' "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
  fi
  if [ $? -eq 0 ]; then
    printf >&2 'Modified file     : %s\n' "$dst_path"
  else
    printf >&2 '%s Failed while modifying file: %s\n' "$ERROR" "$dst_path"
    exit 1
  fi

  # Special treatment for README
  src_path="$SERVICE_DIRPATH/README.template"
  dst_path="$SERVICE_DIRPATH/README.adoc"
  if mv "$src_path" "$dst_path"; then
    printf >&2 'Renamed file      : %s\n' "$dst_path"
  else
    printf >&2 '%s Failed while renaming file: %s\n' "$ERROR" "$dst_path"
    exit 1
  fi
  if sed --version &>/dev/null; then
    sed -i "s/%%FULL_PRETTY_NAME%%/$FULL_PRETTY_NAME/" "$dst_path"
  else
    sed -i '' "s/%%FULL_PRETTY_NAME%%/$FULL_PRETTY_NAME/" "$dst_path"
  fi
  if [ $? -eq 0 ]; then
    printf >&2 'Modified file     : %s\n' "$dst_path"
  else
    printf >&2 '%s Failed while modifying file: %s\n' "$ERROR" "$dst_path"
    exit 1
  fi

  # Special treatment for settings.gradle
  dst_path="$SERVICE_DIRPATH/settings.gradle"
  if sed --version &>/dev/null; then
    sed -i "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
  else
    sed -i '' "s/$TEMPLATE_NAME/$SERVICE_NAME/g" "$dst_path"
  fi
  if [ $? -eq 0 ]; then
    printf >&2 'Modified file     : %s\n' "$dst_path"
  else
    printf >&2 '%s Failed while modifying file: %s\n' "$ERROR" "$dst_path"
    exit 1
  fi

  # Take service under version control
  if ! cd -- "$SERVICE_DIRPATH"; then
    printf >&2 '%s Failed to change into service directory: %s\n' "$ERROR" "$SERVICE_DIRPATH"
    exit 1
  fi
  if ! git init .; then
    printf >&2 '%s Failed to initialize Git repository at: %s\n' "$ERROR" "$SERVICE_DIRPATH"
    exit 1
  fi

  # Copy Git hooks
  file_names=(
    'pre-commit'
  )
  for file_name in "${file_names[@]}"; do
    src_path="$TEMPLATE_DIRPATH/.git-hooks/$file_name"
    dst_path="$SERVICE_DIRPATH/$( git rev-parse --git-dir )/hooks/$file_name"
    if cp -- "$src_path" "$dst_path"; then
      printf >&2 'Copied file       : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while copying file: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
    if chmod +x "$dst_path"; then
      printf >&2 'Made executable   : %s\n' "$dst_path"
    else
      printf >&2 '%s Failed while making file executable: %s\n' "$ERROR" "$dst_path"
      exit 1
    fi
  done

  # All done!
  printf >&2 "%s Created '%s' at: %s\n" \
    "$SUCCESS" "${GREEN}${BOLD}${FULL_SERVICE_NAME}${NORMAL}" "$SERVICE_DIRPATH"
}

service_name_is_valid() {
  local service_name="$1"
  [[ $service_name =~ ^[A-Za-z]+(-[A-Za-z]+(-[A-Za-z]+)?)?$ ]]
  return $?
}

normalize_service_name() {
  local service_name="$1"
  service_name="$( tr '[:upper:]' '[:lower:]' <<<"$service_name" )"
  printf '%s' "$service_name"
}

service_name_to_pretty_name() {
  local service_name="$1" chunks=() chunk pretty_name i
  IFS='-' read -ra chunks <<< "$service_name"
  for (( i=0; i<${#chunks[@]}; ++i )); do
    if ((i)); then
      pretty_name+=' '
    fi
    chunk="${chunks[$i]}"
    pretty_name+="$( tr '[:lower:]' '[:upper:]' <<<"${chunk:0:1}" )${chunk:1}"
  done
  printf '%s' "$pretty_name"
}

service_name_to_package_name() {
  local service_name="$1" chunks=() chunk package_name i
  IFS='-' read -ra chunks <<< "$service_name"
  for (( i=0; i<${#chunks[@]}; ++i )); do
    chunk="${chunks[$i]}"
    package_name+="$chunk"
  done
  printf '%s' "$package_name"
}

pretty_name_is_valid() {
  local pretty_name="$1"
  [[ $pretty_name =~ ^[A-Z][a-z]*( [A-Z][a-z]*( [A-Z][a-z]*)?)?$ ]]
  return $?
}

package_name_is_valid() {
  local package_name="$1"
  [[ $package_name =~ ^[a-z]+$ ]]
  return $?
}

main "$@"
