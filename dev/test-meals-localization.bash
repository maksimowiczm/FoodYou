#!/bin/bash

# Directory to scan for meal files (hardcoded)
MEALS_DIR="app/src/commonMain/composeResources/files/meals"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Arrays to store results
valid_files=()
invalid_files=()
error_messages=()

# Function to validate time format (HH:MM)
validate_time_format() {
    local time="$1"
    # Check if time matches HH:MM format (00:00 to 23:59)
    if [[ $time =~ ^([0-1][0-9]|2[0-3]):[0-5][0-9]$ ]]; then
        return 0
    fi
    return 1
}

# Function to validate individual meal object
validate_meal() {
    local meal="$1"
    local file="$2"
    local index="$3"

    # Check if required fields exist
    local name=$(echo "$meal" | jq -r '.name // empty' 2>/dev/null)
    local from=$(echo "$meal" | jq -r '.from // empty' 2>/dev/null)
    local to=$(echo "$meal" | jq -r '.to // empty' 2>/dev/null)

    if [[ -z "$name" ]]; then
        error_messages+=("$file: Meal at index $index missing 'name' field")
        return 1
    fi

    if [[ -z "$from" ]]; then
        error_messages+=("$file: Meal at index $index missing 'from' field")
        return 1
    fi

    if [[ -z "$to" ]]; then
        error_messages+=("$file: Meal at index $index missing 'to' field")
        return 1
    fi

    # Validate time formats
    if ! validate_time_format "$from"; then
        error_messages+=("$file: Meal '$name' has invalid 'from' time format: '$from' (expected HH:MM)")
        return 1
    fi

    if ! validate_time_format "$to"; then
        error_messages+=("$file: Meal '$name' has invalid 'to' time format: '$to' (expected HH:MM)")
        return 1
    fi

    return 0
}

# Function to validate JSON file
validate_json_file() {
    local file="$1"
    local is_valid=true

    echo "Validating: $file"

    # Check if file exists and is readable
    if [[ ! -r "$file" ]]; then
        error_messages+=("$file: File not readable or does not exist")
        return 1
    fi

    # Check if it's valid JSON
    if ! jq empty "$file" 2>/dev/null; then
        error_messages+=("$file: Invalid JSON format")
        return 1
    fi

    # Check if it's an array
    if [[ $(jq -r 'type' "$file" 2>/dev/null) != "array" ]]; then
        error_messages+=("$file: Root element must be an array")
        return 1
    fi

    # Check if array is not empty
    local array_length=$(jq length "$file" 2>/dev/null)
    if [[ $array_length -eq 0 ]]; then
        error_messages+=("$file: Array cannot be empty")
        return 1
    fi

    # Validate each meal object
    for ((i=0; i<$array_length; i++)); do
        local meal=$(jq ".[$i]" "$file" 2>/dev/null)
        if ! validate_meal "$meal" "$file" "$i"; then
            is_valid=false
        fi
    done

    if $is_valid; then
        return 0
    else
        return 1
    fi
}

# Main execution
echo -e "${YELLOW}JSON Meal Files Validator${NC}"
echo "=================================="
echo "Scanning directory: $MEALS_DIR"
echo ""

# Check if directory exists
if [[ ! -d "$MEALS_DIR" ]]; then
    echo -e "${RED}Error: Directory '$MEALS_DIR' does not exist${NC}"
    exit 1
fi

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo -e "${RED}Error: 'jq' command is required but not installed${NC}"
    echo "Please install jq: sudo apt-get install jq (Ubuntu/Debian) or brew install jq (macOS)"
    exit 1
fi

# Find all meals-*.json files
meal_files=($(find "$MEALS_DIR" -name "meals-*.json" -type f 2>/dev/null))

if [[ ${#meal_files[@]} -eq 0 ]]; then
    echo -e "${YELLOW}No meals-*.json files found in $MEALS_DIR${NC}"
    exit 0
fi

echo "Found ${#meal_files[@]} meal file(s) to validate:"
printf '%s\n' "${meal_files[@]}"
echo ""

# Validate each file
for file in "${meal_files[@]}"; do
    if validate_json_file "$file"; then
        valid_files+=("$file")
        echo -e "${GREEN}✓ Valid: $(basename "$file")${NC}"
    else
        invalid_files+=("$file")
        echo -e "${RED}✗ Invalid: $(basename "$file")${NC}"
    fi
    echo ""
done

# Print summary
echo "=================================="
echo -e "${YELLOW}VALIDATION SUMMARY${NC}"
echo "=================================="

if [[ ${#valid_files[@]} -gt 0 ]]; then
    echo -e "${GREEN}Valid files (${#valid_files[@]}):${NC}"
    for file in "${valid_files[@]}"; do
        echo "  ✓ $(basename "$file")"
    done
    echo ""
fi

if [[ ${#invalid_files[@]} -gt 0 ]]; then
    echo -e "${RED}Invalid files (${#invalid_files[@]}):${NC}"
    for file in "${invalid_files[@]}"; do
        echo "  ✗ $(basename "$file")"
    done
    echo ""

    echo -e "${RED}Error details:${NC}"
    for error in "${error_messages[@]}"; do
        echo "  • $error"
    done
    echo ""
fi

# Final result
if [[ ${#invalid_files[@]} -eq 0 ]]; then
    echo -e "${GREEN}All files are valid!${NC}"
    exit 0
else
    echo -e "${RED}Validation failed: ${#invalid_files[@]} invalid file(s) found${NC}"
    exit 1
fi