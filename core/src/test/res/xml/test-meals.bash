#!/usr/bin/env bash

# This script validates meals XML files for both structure and time values

# Exit on any error
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

validate_time() {
    local time=$1
    local file=$2
    local meal_name=$3
    local field_name=$4

    # Check if time matches HH:MM format
    if ! [[ $time =~ ^([0-1][0-9]|2[0-3]):[0-5][0-9]$ ]]; then
        echo -e "${RED}Error in $file: Invalid $field_name time format '$time' for meal '$meal_name'${NC}"
        return 1
    fi
}

validate_xml_times() {
    local file=$1
    local result=0

    while IFS= read -r line; do
        if [[ $line =~ \<name\>([^<]+)\</name\> ]]; then
            meal_name="${BASH_REMATCH[1]}"
        elif [[ $line =~ \<from\>([^<]+)\</from\> ]]; then
            from_time="${BASH_REMATCH[1]}"
            validate_time "$from_time" "$file" "$meal_name" "from" || result=1
        elif [[ $line =~ \<to\>([^<]+)\</to\> ]]; then
            to_time="${BASH_REMATCH[1]}"
            validate_time "$to_time" "$file" "$meal_name" "to" || result=1
        fi
    done < "$file"

    return $result
}

# Main validation loop
for file in core/src/main/res/xml*/meals.xml; do
    if [ -f "$file" ]; then
        echo "Validating $file"

        # Validate XML structure
        if xmllint --schema core/src/test/res/xml/meals-schema.xsd --noout "$file"; then
            echo -e "${GREEN}✓ XML structure valid${NC}"
        else
            echo -e "${RED}✗ XML structure invalid${NC}"
            exit 1
        fi

        # Validate time values
        if validate_xml_times "$file"; then
            echo -e "${GREEN}✓ Time values valid${NC}"
        else
            echo -e "${RED}✗ Time values invalid${NC}"
            exit 1
        fi

        echo "----------------------------------------"
    fi
done

echo -e "${GREEN}All validations passed successfully!${NC}"