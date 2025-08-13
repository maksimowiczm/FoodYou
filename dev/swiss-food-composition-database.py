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
- All nutritional values are converted to grams (mg → g, µg → g)

Usage:
    python extract-swiss-database.py <input_file> <output_file>
"""

# Mapping from original Excel column names to desired field names - ENGLISH
COLUMN_MAPPING_EN = {
    "Name": "name",
    "Protein (g)": "proteins",
    "Carbohydrates, available (g)": "carbohydrates",
    "Fat, total (g)": "fats",
    "Energy, kilocalories (kcal)": "energy",
    "Fatty acids, saturated (g)": "saturated_fats",
    "Fatty acids, monounsaturated (g)": "monounsaturated_fats",
    "Fatty acids, polyunsaturated (g)": "polyunsaturated_fats",
    "Sugars (g)": "sugars",
    "Salt (NaCl) (g)": "salt",
    "Dietary fibres (g)": "dietary_fiber",
    "Cholesterol (mg)": "cholesterol",
    "Vitamin A activity, RE (µg-RE)": "vitamin_a",
    "Vitamin B1 (thiamine) (mg)": "vitamin_b1",
    "Vitamin B2 (riboflavin) (mg)": "vitamin_b2",
    "Niacin (mg)": "vitamin_b3",
    "Panthotenic acid (mg)": "vitamin_b5",
    "Vitamin B6 (pyridoxine) (mg)": "vitamin_b6",
    "Folate (µg)": "vitamin_b9",
    "Vitamin B12 (cobalamin) (µg)": "vitamin_b12",
    "Vitamin C (ascorbic acid) (mg)": "vitamin_c",
    "Vitamin D (calciferol) (µg)": "vitamin_d",
    "Vitamin E (α-tocopherol) (mg)": "vitamin_e",
    "Magnesium (Mg) (mg)": "magnesium",
    "Potassium (K) (mg)": "potassium",
    "Calcium (Ca) (mg)": "calcium",
    "Zinc (Zn) (mg)": "zinc",
    "Sodium (Na) (mg)": "sodium",
    "Iron (Fe) (mg)": "iron",
    "Phosphorus (P) (mg)": "phosphorus",
    "Selenium (Se) (µg)": "selenium",
    "Iodide (I) (µg)": "iodine",
}

# Mapping from original Excel column names to desired field names - ITALIAN
COLUMN_MAPPING_IT = {
    "Nome": "name",
    "Proteine (g)": "proteins",
    "Glucidi, disponibili (g)": "carbohydrates",
    "Lipidi, totali (g)": "fats",
    "Energia, calorie (kcal)": "energy",
    "Acidi grassi, saturi (g)": "saturated_fats",
    "Acidi grassi, monoinsaturi (g)": "monounsaturated_fats",
    "Acidi grassi, polinsaturi (g)": "polyunsaturated_fats",
    "Zuccheri (g)": "sugars",
    "Sale (NaCl) (g)": "salt",
    "Fibra alimentare (g)": "dietary_fiber",
    "Colesterolo (mg)": "cholesterol",
    "Attività di vitamina A, RE (µg-RE)": "vitamin_a",
    "Vitamina B1 (tiamina) (mg)": "vitamin_b1",
    "Vitamina B2 (riboflavina) (mg)": "vitamin_b2",
    "Niacina (mg)": "vitamin_b3",
    "Acido pantotenico (mg)": "vitamin_b5",
    "Vitamina B6 (piridossina) (mg)": "vitamin_b6",
    "Folati (µg)": "vitamin_b9",
    "Vitamina B12 (cobalamina) (µg)": "vitamin_b12",
    "Vitamina C (acido ascorbico) (mg)": "vitamin_c",
    "Vitamina D (calciferolo) (µg)": "vitamin_d",
    "Vitamina E (α-tocoferolo) (mg)": "vitamin_e",
    "Magnesio (Mg) (mg)": "magnesium",
    "Potassio (K) (mg)": "potassium",
    "Calcio (Ca) (mg)": "calcium",
    "Zinco (Zn)  (mg)": "zinc",
    "Sodio (Na) (mg)": "sodium",
    "Ferro (Fe) (mg)": "iron",
    "Fosforo (P) (mg)": "phosphorus",
    "Selenio (Se) (µg)": "selenium",
    "Iodio (I) (µg)": "iodine",
}

# Mapping from original Excel column names to desired field names - GERMAN
COLUMN_MAPPING_DE = {
    "Name": "name",
    "Protein (g)": "proteins",
    "Kohlenhydrate, verfügbar (g)": "carbohydrates",
    "Fett, total (g)": "fats",
    "Energie, Kalorien (kcal)": "energy",
    "Fettsäuren, gesättigt (g)": "saturated_fats",
    "Fettsäuren, einfach ungesättigt (g)": "monounsaturated_fats",
    "Fettsäuren, mehrfach ungesättigt (g)": "polyunsaturated_fats",
    "Zucker (g)": "sugars",
    "Salz (NaCl) (g)": "salt",
    "Nahrungsfasern (g)": "dietary_fiber",
    "Cholesterin (mg)": "cholesterol",
    "Vitamin A-Aktivität, RE (µg-RE)": "vitamin_a",
    "Vitamin B1 (Thiamin) (mg)": "vitamin_b1",
    "Vitamin B2 (Riboflavin) (mg)": "vitamin_b2",
    "Niacin (mg)": "vitamin_b3",
    "Pantothensäure (mg)": "vitamin_b5",
    "Vitamin B6 (Pyridoxin) (mg)": "vitamin_b6",
    "Folat (µg)": "vitamin_b9",
    "Vitamin B12 (Cobalamin) (µg)": "vitamin_b12",
    "Vitamin C (Ascorbinsäure) (mg)": "vitamin_c",
    "Vitamin D (Calciferol) (µg)": "vitamin_d",
    "Vitamin E (α-Tocopherol) (mg)": "vitamin_e",
    "Magnesium (Mg) (mg)": "magnesium",
    "Kalium (K) (mg)": "potassium",
    "Calcium (Ca) (mg)": "calcium",
    "Zink (Zn)  (mg)": "zinc",
    "Natrium (Na) (mg)": "sodium",
    "Eisen (Fe) (mg)": "iron",
    "Phosphor (P) (mg)": "phosphorus",
    "Selen (Se) (µg)": "selenium",
    "Jod (I) (µg)": "iodine",
}

# Mapping from original Excel column names to desired field names - FRENCH
COLUMN_MAPPING_FR = {
    "Nom": "name",
    "Protéines (g)": "proteins",
    "Glucides, disponibles (g)": "carbohydrates",
    "Lipides, totaux (g)": "fats",
    "Énergie, calories (kcal)": "energy",
    "Acides gras, saturés (g)": "saturated_fats",
    "Acides gras, mono-insaturés (g)": "monounsaturated_fats",
    "Acides gras, poly-insaturés (g)": "polyunsaturated_fats",
    "Sucres (g)": "sugars",
    "Sel (NaCl) (g)": "salt",
    "Fibres alimentaires (g)": "dietary_fiber",
    "Cholestérol (mg)": "cholesterol",
    "Activité de vitamine A, RE (µg-RE)": "vitamin_a",
    "Vitamine B1 (thiamine) (mg)": "vitamin_b1",
    "Vitamine B2 (riboflavine) (mg)": "vitamin_b2",
    "Niacine (mg)": "vitamin_b3",
    "Acide pantothénique (mg)": "vitamin_b5",
    "Vitamine B6 (pyridoxine) (mg)": "vitamin_b6",
    "Folate (µg)": "vitamin_b9",
    "Vitamine B12 (cobalamine) (µg)": "vitamin_b12",
    "Vitamine C (acide ascorbique) (mg)": "vitamin_c",
    "Vitamine D (calciférol) (µg)": "vitamin_d",
    "Vitamine E (α-tocophérol) (mg)": "vitamin_e",
    "Magnésium (Mg) (mg)": "magnesium",
    "Potassium (K) (mg)": "potassium",
    "Calcium (Ca) (mg)": "calcium",
    "Zinc (Zn)  (mg)": "zinc",
    "Sodium (Na) (mg)": "sodium",
    "Fer (Fe) (mg)": "iron",
    "Phosphore (P) (mg)": "phosphorus",
    "Sélénium (Se) (µg)": "selenium",
    "Iode (I) (µg)": "iodine",
}

# Dictionary of all mappings
LANGUAGE_MAPPINGS = {
    "en": COLUMN_MAPPING_EN,
    "it": COLUMN_MAPPING_IT,
    "de": COLUMN_MAPPING_DE,
    "fr": COLUMN_MAPPING_FR,
}

# Columns required but not in source → fill as empty
EXTRA_COLUMNS = {
    "brand": "",
    "barcode": "",
    "omega3": "",
    "omega6": "",
    "caffeine_milli": "",
    "vitamin_b7_micro": "",
    "vitamin_k_micro": "",
    "manganese_milli": "",
    "copper_milli": "",
    "package_weight": "",
    "serving_weight": "",
}


def detect_language(df_columns):
    """
    Auto-detect the language based on column names
    """
    # Check for key columns that are unique to each language
    key_columns = {
        "en": ["Name", "Protein (g)", "Energy, kilocalories (kcal)"],
        "it": ["Nome", "Proteine (g)", "Energia, calorie (kcal)"],
        "de": ["Name", "Protein (g)", "Energie, Kalorien (kcal)"],
        "fr": ["Nom", "Protéines (g)", "Énergie, calories (kcal)"],
    }

    for lang, cols in key_columns.items():
        if all(col in df_columns for col in cols):
            return lang

    return None


def clean_and_convert_values(df):
    """
    Clean values and convert units to grams
    """
    # First, clean string values (remove '<', replace 'tr.' with '0')
    for col in df.columns:
        if col == "name":  # Skip name column
            continue

        if df[col].dtype == object:
            df[col] = df[col].astype(str).str.replace(r"^<\s*", "", regex=True)
            df[col] = df[col].str.replace("tr.", "0", regex=False)
            df[col] = df[col].str.replace("nan", "0", regex=False)

            # Convert to numeric, coerce errors to NaN
            df[col] = pd.to_numeric(df[col], errors='coerce')

    # Fill any remaining NaN values with 0
    df = df.fillna(0)

    # Convert mg to g (divide by 1000)
    mg_columns = [
        "cholesterol", "vitamin_b1", "vitamin_b2", "vitamin_b3", "vitamin_b5",
        "vitamin_b6", "vitamin_c", "vitamin_e", "magnesium", "potassium",
        "calcium", "zinc", "sodium", "iron", "phosphorus"
    ]

    for col in mg_columns:
        if col in df.columns:
            df[col] = df[col] / 1000

    # Convert µg to g (divide by 1,000,000)
    microg_columns = [
        "vitamin_a", "vitamin_b9", "vitamin_b12", "vitamin_d", "selenium", "iodine"
    ]

    for col in microg_columns:
        if col in df.columns:
            df[col] = df[col] / 1000000

    # Note: Energy stays in kcal as it's typically expected in that unit
    # If you want energy in grams equivalent, you'd need to specify the conversion

    return df


def extract_and_map(input_file, output_file, language=None):
    try:
        # Load sheet, skipping first two rows
        df = pd.read_excel(input_file, sheet_name=0, skiprows=2)

        if language is None:
            language = detect_language(df.columns.tolist())

        mapping = LANGUAGE_MAPPINGS.get(language)

        if mapping is None:
            print(
                f"Error: Unsupported language or no matching columns found in {input_file}."
            )
            sys.exit(1)

        print(f"Detected language: {language}")

        # Rename columns based on mapping
        df = df.rename(columns=mapping)

        # Keep only the mapped columns that exist
        available_cols = [v for v in mapping.values() if v in df.columns]
        df = df[available_cols]

        # Clean values and convert units
        df = clean_and_convert_values(df)

        # Add missing columns with default values
        for col, default in EXTRA_COLUMNS.items():
            df[col] = default

        # Reorder columns
        final_columns = [
            "name",
            "brand",
            "barcode",
            "proteins",
            "carbohydrates",
            "fats",
            "energy",
            "saturated_fats",
            "monounsaturated_fats",
            "polyunsaturated_fats",
            "omega3",
            "omega6",
            "sugars",
            "salt",
            "dietary_fiber",
            "cholesterol",
            "caffeine_milli",
            "vitamin_a",
            "vitamin_b1",
            "vitamin_b2",
            "vitamin_b3",
            "vitamin_b5",
            "vitamin_b6",
            "vitamin_b7_micro",
            "vitamin_b9",
            "vitamin_b12",
            "vitamin_c",
            "vitamin_d",
            "vitamin_e",
            "vitamin_k_micro",
            "manganese_milli",
            "magnesium",
            "potassium",
            "calcium",
            "copper_milli",
            "zinc",
            "sodium",
            "iron",
            "phosphorus",
            "selenium",
            "iodine",
            "package_weight",
            "serving_weight",
        ]

        # Add any missing columns not previously handled
        for col in final_columns:
            if col not in df.columns:
                df[col] = ""

        df = df[final_columns]

        # Export to CSV
        df.to_csv(output_file, index=False)
        print(f"Successfully exported to {output_file}")
        print("All nutritional values are now in grams (mg and µg converted to g)")

    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(
        description=description, formatter_class=argparse.RawTextHelpFormatter
    )
    parser.add_argument("input_file", help="Path to the input Excel file")
    parser.add_argument("output_file", help="Path to the output CSV file")
    args = parser.parse_args()

    extract_and_map(args.input_file, args.output_file)


if __name__ == "__main__":
    main()