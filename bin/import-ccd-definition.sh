#!/usr/bin/env bash

# Exit on error
set -e

# Get the absolute path of the project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Default values
definition_input_dir="${PROJECT_ROOT}/src/functionalTest/resources"
definition_output_file="${PROJECT_ROOT}/src/functionalTest/resources/CCD_HRS_v1.7-AAT.xlsx"

# If no parameters are provided, use defaults
if [ $# -eq 0 ]; then
    echo "No parameters provided, using default values"
    params=""
else
    params="$@"
fi

echo "Definition input directory: ${definition_input_dir}"
echo "Definition output file: ${definition_output_file}"

# Create output directory if it doesn't exist
mkdir -p "$(dirname "${definition_output_file}")"

# Execute the import script with the correct paths
echo "Running import with parameters: ${params}"
"${PROJECT_ROOT}/bin/utils/import-ccd-definition.sh" "${definition_input_dir}" "${definition_output_file}" ${params}
