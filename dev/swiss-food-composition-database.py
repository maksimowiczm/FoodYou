import pandas as pd
import argparse
import sys

description = """
This script extracts the "Generic Foods" sheet from an Excel file containing the Swiss Food Composition Database and saves it as a CSV file. It maps the original column names to a FoodYou compatible format and applies necessary data cleaning rules.

Download the Swiss Food Composition Database from:
https://naehrwertdaten.ch/en/downloads

Data cleaning rules applied:
- Values with '<' (e.g., '<1.0') are interpreted as the number without '<' (e.g., '1.0')
- Values marked as 'tr.' (trace) are interpreted as '0'

Usage:
    python extract-swiss-database.py <input_file> <output_file>
"""

# Mapping from original Excel column names to desired field names
COLUMN_MAPPING_EN = {
    "Name": "name",
    "Protein (g)": "proteins",
    "Carbohydrates, available (g)": "carbohydrates",
    "Fat, total (g)": "fats",
    "Energy, kilocalories (kcal)": "calories",
    "Fatty acids, saturated (g)": "saturated_fats",
    "Fatty acids, monounsaturated (g)": "monounsaturated_fats",
    "Fatty acids, polyunsaturated (g)": "polyunsaturated_fats",
    "Sugars (g)": "sugars",
    "Salt (NaCl) (g)": "salt",
    "Dietary fibres (g)": "fiber",
    "Cholesterol (mg)": "cholesterol_milli",
    "Vitamin A activity, RE (µg-RE)": "vitamin_a_micro",
    "Vitamin B1 (thiamine) (mg)": "vitamin_b1_milli",
    "Vitamin B2 (riboflavin) (mg)": "vitamin_b2_milli",
    "Niacin (mg)": "vitamin_b3_milli",
    "Panthotenic acid (mg)": "vitamin_b5_milli",
    "Vitamin B6 (pyridoxine) (mg)": "vitamin_b6_milli",
    "Folate (µg)": "vitamin_b9_micro",
    "Vitamin B12 (cobalamin) (µg)": "vitamin_b12_micro",
    "Vitamin C (ascorbic acid) (mg)": "vitamin_c_milli",
    "Vitamin D (calciferol) (µg)": "vitamin_d_micro",
    "Vitamin E (α-tocopherol) (mg)": "vitamin_e_milli",
    "Magnesium (Mg) (mg)": "magnesium_milli",
    "Potassium (K) (mg)": "potassium_milli",
    "Calcium (Ca) (mg)": "calcium_milli",
    "Zinc (Zn) (mg)": "zinc_milli",
    "Sodium (Na) (mg)": "sodium_milli",
    "Iron (Fe) (mg)": "iron_milli",
    "Phosphorus (P) (mg)": "phosphorus_milli",
    "Selenium (Se) (µg)": "selenium_micro",
    "Iodide (I) (µg)": "iodine_micro",
}

# Columns required but not in source → fill as empty
EXTRA_COLUMNS_EN = {
    "brand": "",
    "barcode": "",
    "omega3": "",
    "omega6": "",
    "vitamin_b7_micro": "",
    "vitamin_k_micro": "",
    "manganese_milli": "",
    "copper_milli": "",
    "package_weight": "",
    "serving_weight": ""
}

SHEET_NAME = "Generic Foods"

def extract_and_map(input_file, output_file):
    try:
        # Load sheet, skipping first two rows
        df = pd.read_excel(input_file, sheet_name=SHEET_NAME, skiprows=2)

        # Rename columns based on mapping
        df = df.rename(columns=COLUMN_MAPPING_EN)

        # Keep only the mapped columns
        df = df[[v for v in COLUMN_MAPPING_EN.values() if v in df.columns]]

        # Add missing columns with default values
        for col, default in EXTRA_COLUMNS_EN.items():
            df[col] = default

        # Reorder columns
        final_columns = [
            "name", "brand", "barcode", "proteins", "carbohydrates", "fats", "calories",
            "saturated_fats", "monounsaturated_fats", "polyunsaturated_fats",
            "omega3", "omega6", "sugars", "salt", "fiber", "cholesterol_milli",
            "caffeine_milli",  # Not mapped → will raise KeyError unless we add a default
            "vitamin_a_micro", "vitamin_b1_milli", "vitamin_b2_milli", "vitamin_b3_milli",
            "vitamin_b5_milli", "vitamin_b6_milli", "vitamin_b7_micro", "vitamin_b9_micro",
            "vitamin_b12_micro", "vitamin_c_milli", "vitamin_d_micro", "vitamin_e_milli",
            "vitamin_k_micro", "manganese_milli", "magnesium_milli", "potassium_milli",
            "calcium_milli", "copper_milli", "zinc_milli", "sodium_milli", "iron_milli",
            "phosphorus_milli", "selenium_micro", "iodine_micro", "package_weight",
            "serving_weight"
        ]

        # Add any missing columns not previously handled
        for col in final_columns:
            if col not in df.columns:
                df[col] = ""

        df = df[final_columns]

        # Clean values like '<1.0' → '1.0', then convert to numeric if possible
        for col in df.columns:
            if df[col].dtype == object:
                df[col] = df[col].astype(str).str.replace(r'^<\s*', '', regex=True)
                df.replace("tr.", "0", inplace=True)

                # Try converting to numeric, skip if it fails
                try:
                    df[col] = pd.to_numeric(df[col])
                except ValueError:
                    pass


        # Export to CSV
        df.to_csv(output_file, index=False)
        print(f"Successfully exported to {output_file}")

    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)

def main():
    parser = argparse.ArgumentParser(description=description, formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument("input_file", help="Path to the input Excel file")
    parser.add_argument("output_file", help="Path to the output CSV file")
    args = parser.parse_args()

    extract_and_map(args.input_file, args.output_file)

if __name__ == "__main__":
    main()
