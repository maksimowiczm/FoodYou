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

# Mapping from original Excel column names to desired field names - ENGLISH
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

# Mapping from original Excel column names to desired field names - ITALIAN
COLUMN_MAPPING_IT = {
    "Nome": "name",
    "Proteine (g)": "proteins",
    "Glucidi, disponibili (g)": "carbohydrates",
    "Lipidi, totali (g)": "fats",
    "Energia, calorie (kcal)": "calories",
    "Acidi grassi, saturi (g)": "saturated_fats",
    "Acidi grassi, monoinsaturi (g)": "monounsaturated_fats",
    "Acidi grassi, polinsaturi (g)": "polyunsaturated_fats",
    "Zuccheri (g)": "sugars",
    "Sale (NaCl) (g)": "salt",
    "Fibra alimentare (g)": "fiber",
    "Colesterolo (mg)": "cholesterol_milli",
    "Attività di vitamina A, RE (µg-RE)": "vitamin_a_micro",
    "Vitamina B1 (tiamina) (mg)": "vitamin_b1_milli",
    "Vitamina B2 (riboflavina) (mg)": "vitamin_b2_milli",
    "Niacina (mg)": "vitamin_b3_milli",
    "Acido pantotenico (mg)": "vitamin_b5_milli",
    "Vitamina B6 (piridossina) (mg)": "vitamin_b6_milli",
    "Folati (µg)": "vitamin_b9_micro",
    "Vitamina B12 (cobalamina) (µg)": "vitamin_b12_micro",
    "Vitamina C (acido ascorbico) (mg)": "vitamin_c_milli",
    "Vitamina D (calciferolo) (µg)": "vitamin_d_micro",
    "Vitamina E (α-tocoferolo) (mg)": "vitamin_e_milli",
    "Magnesio (Mg) (mg)": "magnesium_milli",
    "Potassio (K) (mg)": "potassium_milli",
    "Calcio (Ca) (mg)": "calcium_milli",
    "Zinco (Zn)  (mg)": "zinc_milli",
    "Sodio (Na) (mg)": "sodium_milli",
    "Ferro (Fe) (mg)": "iron_milli",
    "Fosforo (P) (mg)": "phosphorus_milli",
    "Selenio (Se) (µg)": "selenium_micro",
    "Iodio (I) (µg)": "iodine_micro",
}

# Mapping from original Excel column names to desired field names - GERMAN
COLUMN_MAPPING_DE = {
    "Name": "name",
    "Protein (g)": "proteins",
    "Kohlenhydrate, verfügbar (g)": "carbohydrates",
    "Fett, total (g)": "fats",
    "Energie, Kalorien (kcal)": "calories",
    "Fettsäuren, gesättigt (g)": "saturated_fats",
    "Fettsäuren, einfach ungesättigt (g)": "monounsaturated_fats",
    "Fettsäuren, mehrfach ungesättigt (g)": "polyunsaturated_fats",
    "Zucker (g)": "sugars",
    "Salz (NaCl) (g)": "salt",
    "Nahrungsfasern (g)": "fiber",
    "Cholesterin (mg)": "cholesterol_milli",
    "Vitamin A-Aktivität, RE (µg-RE)": "vitamin_a_micro",
    "Vitamin B1 (Thiamin) (mg)": "vitamin_b1_milli",
    "Vitamin B2 (Riboflavin) (mg)": "vitamin_b2_milli",
    "Niacin (mg)": "vitamin_b3_milli",
    "Pantothensäure (mg)": "vitamin_b5_milli",
    "Vitamin B6 (Pyridoxin) (mg)": "vitamin_b6_milli",
    "Folat (µg)": "vitamin_b9_micro",
    "Vitamin B12 (Cobalamin) (µg)": "vitamin_b12_micro",
    "Vitamin C (Ascorbinsäure) (mg)": "vitamin_c_milli",
    "Vitamin D (Calciferol) (µg)": "vitamin_d_micro",
    "Vitamin E (α-Tocopherol) (mg)": "vitamin_e_milli",
    "Magnesium (Mg) (mg)": "magnesium_milli",
    "Kalium (K) (mg)": "potassium_milli",
    "Calcium (Ca) (mg)": "calcium_milli",
    "Zink (Zn)  (mg)": "zinc_milli",
    "Natrium (Na) (mg)": "sodium_milli",
    "Eisen (Fe) (mg)": "iron_milli",
    "Phosphor (P) (mg)": "phosphorus_milli",
    "Selen (Se) (µg)": "selenium_micro",
    "Jod (I) (µg)": "iodine_micro",
}

# Mapping from original Excel column names to desired field names - FRENCH
COLUMN_MAPPING_FR = {
    "Nom": "name",
    "Protéines (g)": "proteins",
    "Glucides, disponibles (g)": "carbohydrates",
    "Lipides, totaux (g)": "fats",
    "Énergie, calories (kcal)": "calories",
    "Acides gras, saturés (g)": "saturated_fats",
    "Acides gras, mono-insaturés (g)": "monounsaturated_fats",
    "Acides gras, poly-insaturés (g)": "polyunsaturated_fats",
    "Sucres (g)": "sugars",
    "Sel (NaCl) (g)": "salt",
    "Fibres alimentaires (g)": "fiber",
    "Cholestérol (mg)": "cholesterol_milli",
    "Activité de vitamine A, RE (µg-RE)": "vitamin_a_micro",
    "Vitamine B1 (thiamine) (mg)": "vitamin_b1_milli",
    "Vitamine B2 (riboflavine) (mg)": "vitamin_b2_milli",
    "Niacine (mg)": "vitamin_b3_milli",
    "Acide pantothénique (mg)": "vitamin_b5_milli",
    "Vitamine B6 (pyridoxine) (mg)": "vitamin_b6_milli",
    "Folate (µg)": "vitamin_b9_micro",
    "Vitamine B12 (cobalamine) (µg)": "vitamin_b12_micro",
    "Vitamine C (acide ascorbique) (mg)": "vitamin_c_milli",
    "Vitamine D (calciférol) (µg)": "vitamin_d_micro",
    "Vitamine E (α-tocophérol) (mg)": "vitamin_e_milli",
    "Magnésium (Mg) (mg)": "magnesium_milli",
    "Potassium (K) (mg)": "potassium_milli",
    "Calcium (Ca) (mg)": "calcium_milli",
    "Zinc (Zn)  (mg)": "zinc_milli",
    "Sodium (Na) (mg)": "sodium_milli",
    "Fer (Fe) (mg)": "iron_milli",
    "Phosphore (P) (mg)": "phosphorus_milli",
    "Sélénium (Se) (µg)": "selenium_micro",
    "Iode (I) (µg)": "iodine_micro",
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

        # Keep only the mapped columns
        df = df[[v for v in mapping.values() if v in df.columns]]

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
            "calories",
            "saturated_fats",
            "monounsaturated_fats",
            "polyunsaturated_fats",
            "omega3",
            "omega6",
            "sugars",
            "salt",
            "fiber",
            "cholesterol_milli",
            "caffeine_milli",
            "vitamin_a_micro",
            "vitamin_b1_milli",
            "vitamin_b2_milli",
            "vitamin_b3_milli",
            "vitamin_b5_milli",
            "vitamin_b6_milli",
            "vitamin_b7_micro",
            "vitamin_b9_micro",
            "vitamin_b12_micro",
            "vitamin_c_milli",
            "vitamin_d_micro",
            "vitamin_e_milli",
            "vitamin_k_micro",
            "manganese_milli",
            "magnesium_milli",
            "potassium_milli",
            "calcium_milli",
            "copper_milli",
            "zinc_milli",
            "sodium_milli",
            "iron_milli",
            "phosphorus_milli",
            "selenium_micro",
            "iodine_micro",
            "package_weight",
            "serving_weight",
        ]

        # Add any missing columns not previously handled
        for col in final_columns:
            if col not in df.columns:
                df[col] = ""

        df = df[final_columns]

        # Clean values like '<1.0' → '1.0', then convert to numeric if possible
        for col in df.columns:
            if df[col].dtype == object:
                df[col] = df[col].astype(str).str.replace(r"^<\s*", "", regex=True)
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
    parser = argparse.ArgumentParser(
        description=description, formatter_class=argparse.RawTextHelpFormatter
    )
    parser.add_argument("input_file", help="Path to the input Excel file")
    parser.add_argument("output_file", help="Path to the output CSV file")
    args = parser.parse_args()

    extract_and_map(args.input_file, args.output_file)


if __name__ == "__main__":
    main()
