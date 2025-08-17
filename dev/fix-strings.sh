#!/bin/bash

TARGET_DIR="shared/ui/src/commonMain/composeResources"

if [ ! -d "$TARGET_DIR" ]; then
    echo "Directory $TARGET_DIR does not exist."
    exit 1
fi

# Replace \' with ' in all strings.xml files under the directory
find "$TARGET_DIR" -type f -name "strings.xml" | while read -r file; do
    sed -i "s/\\\'/'/g" "$file"
    echo "Processed: $file"
done
