#!/usr/bin/env bash

# Get the absolute path of the project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

definition_input_dir="${PROJECT_ROOT}/src/functionalTest/resources"
definition_output_file="${PROJECT_ROOT}/src/functionalTest/resources/CCD_HRS_v1.7-AAT.xlsx"
params="$@"

echo "Definition input directory: ${definition_input_dir}"
echo "Definition output file: ${definition_output_file}"

# Execute the import script with the correct paths
"${PROJECT_ROOT}/bin/utils/import-ccd-definition.sh" "${definition_input_dir}" "${definition_output_file}" "${params}"
