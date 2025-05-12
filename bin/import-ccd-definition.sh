#!/usr/bin/env bash

definition_input_dir=$(realpath '/src/functionalTest/resources')
definition_output_file="$(realpath ".")/src/functionalTest/resources/CCD_HRS_v1.7-AAT.xlsx"
params="$@"

echo definition_input_dir
echo definition_output_file

./bin/import-ccd-definition-validator.sh "${definition_input_dir}" "${definition_output_file}" "${params}"
