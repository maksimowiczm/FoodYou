---
name: foodyou-add-language
description: Use this skill when asked to add a new language to the FoodYou app after the user has pasted translation strings with a locale directory name (e.g. values-pl-rPL). Covers all required changes across domain, infrastructure, database migration, Android config, and UI layers.
---

# FoodYou: Add a New Language

## Step 0 — Derive language metadata from the directory name

The user will provide a locale directory name in the format `values-{lang}-r{REGION}` (e.g.
`values-pl-rPL`). Extract:

| Variable       | Source                                                                                                                          | Example           |
|----------------|---------------------------------------------------------------------------------------------------------------------------------|-------------------|
| `ISO`          | `{lang}` part                                                                                                                   | `pl`              |
| `CC`           | `{REGION}` part                                                                                                                 | `PL`              |
| `DISPLAY_NAME` | Android system name for this locale — the name as it appears in Android per-app language settings, in the language's own script | `Polski (Polska)` |

Use your general knowledge to determine `DISPLAY_NAME`. It must match exactly what Android shows (
native name, not English).

---

## Step 1 — Read before writing

Before editing any file, **read it first**. Find the last existing language entry in each file and
use it as your insertion anchor. If the file structure differs from what this skill describes, stop
and report the discrepancy rather than guessing.

---

## Step 2 — Domain layer

### [`Language.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/common/domain/Language.kt)

Read the file. Add a new enum entry after the last existing one:

```kotlin
NewLanguage("DISPLAY_NAME", "ISO", "CC"),
```

### [`FoodName.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/common/domain/food/FoodName.kt)

Read the file. Make four changes:

1. **Property** — add `val ISO: String? = null` alongside existing language properties (e.g.
   `val polish: String? = null`)
2. **`list` property** — append `ISO` to the `listOf(...)`
3. **`get(language: Language)` function** — add a branch: `Language.NewLanguage -> ISO`
4. **`requireAll` companion function** — add the new parameter to the signature.

---

## Step 3 — Infrastructure layer

### [`FoodNameEntity.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/FoodNameEntity.kt)

Read the file. Make three changes:

1. **Column** — add `@ColumnInfo(name = "ISO") val ISO: String? = null`. Note: for region-specific
   codes like `pt-BR`, use exactly that in `name`.
2. **`fallback` getter** — append `ISO` to the list

### [`SearchResultMapper.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/SearchResultMapper.kt)

Read the file. Make two changes:

1. In `toDomain(entity: SearchEntity)` — add `ISO = entity.ISO` to the `FoodName.requireAll(...)`
   call
2. In `toFoodNameEntity(name: FoodName)` — map `ISO = name.ISO`

### [`SearchDao.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/SearchDao.kt)

Read the `SIMPLE_NAME_SELECT` constant carefully. Make two changes:

1. Add a `WHEN` branch in the `CASE :languageCode` block:
   ```sql
   WHEN 'ISO-CC' THEN s.name_ISO
   ```
2. Add `s.name_ISO` to the `COALESCE` fallback list. Note: For special codes like `pt-BR`, use
   backticks: `s.`name_pt-BR``.

### [`OpenFoodFactsProductMapper.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/openfoodfacts/infrastructure/OpenFoodFactsProductMapper.kt)

In `toModel()`, add the new language to the `FoodName.requireAll` call:

```kotlin
ISO = localizedNames["ISO"].sanitized() ?: localizedGenericNames["ISO"].sanitized(),
```

Use the short ISO code (e.g., `pl`) for the keys.

---

## Step 4 — Database migration

**This step is mandatory.** Adding a column to `FoodNameEntity` is a breaking schema change.

1. Open [`ReadModelDatabase.kt`](../../../app/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/infrastructure/room/ReadModelDatabase.kt)
2. Increment `VERSION` in the companion object: `const val VERSION = N+1`
3. Append to the `autoMigrations` list in the `@Database` annotation, using the fully qualified name
   to match existing style:
   ```kotlin
   androidx.room.AutoMigration(from = N, to = N+1)
   ```

Room handles the `ALTER TABLE` automatically for a nullable column addition.

> **FTS caveat:** If the existing migrations use manual `Migration` objects for `SearchFts` (FTS
> tables cannot be auto-migrated), check whether this change also requires FTS table recreation. If
> so, fall back to a manual `Migration` for this version increment instead and report this to the
> user.

If you cannot locate the database class or version, report this and ask the user where to find it.

---

## Step 5 — Android locale config

### [`locales_config.xml`](../../../androidApp/src/main/res/xml/locales_config.xml)

Add inside the root element:

```xml
<locale android:name="ISO-CC" />
```

---

## Step 6 — UI registration

### [`Translation.kt`](../../../app/src/commonMain/kotlin/com/maksimowiczm/foodyou/features/language/Translation.kt)

Add to the `languages` list:

```kotlin
Translation("DISPLAY_NAME", Language.NewLanguage),
```

### [`RecipeFormTransformer.kt`](../../../app/src/commonMain/kotlin/com/maksimowiczm/foodyou/features/food/userrecipe/RecipeFormTransformer.kt) and [`ProductFormTransformer.kt`](../../../app/src/commonMain/kotlin/com/maksimowiczm/foodyou/features/userproduct/ProductFormTransformer.kt)
In both files, update the `FoodName.requireAll` call inside the `transform` function:

```kotlin
ISO = if (language == Language.NewLanguage) nameStr else null,
```

---

## Step 7 — Default Meals

### [`MealTemplates.kt`](../../../core/src/commonMain/kotlin/com/maksimowiczm/foodyou/mealplan/domain/MealTemplates.kt)

Add a new branch to the `when (language)` block in the private `MealBuilder.forLanguage` extension.

#### Guidelines for selecting default meals:

* **Research Culture-Specific Defaults**: Do not just translate English meal names. Research common
  meal times and naming conventions for the target country/culture.
* **Include Traditional Intermediate Meals**: If the culture has traditional mid-morning or
  mid-afternoon meals (e.g., "Drugie śniadanie" in Poland), include them using strings in the DSL.
* **Use Standard Meal DSL**: Use the predefined DSL in `MealBuilder`:
  `"Meal Name" from "HH:mm" until "HH:mm"` or `"Meal Name".allDay()`.
* **Time Ranges**: Ensure time ranges are realistic and typically cover the entire day when
  combined.

```kotlin
Language.NewLanguage -> {
    "Breakfast" from "06:00" until "10:00"
    "Lunch" from "12:00" until "15:00"
    "Dinner" from "18:00" until "21:00"
    "Snacks".allDay()
}
```

---

## Step 8 — Validation checklist

After all edits, verify:

- [ ] `Language.kt` — new enum entry present with correct ISO, CC, and display name
- [ ] `FoodName.kt` — property, `list`, `get()`, and `requireAll` all updated
- [ ] `FoodNameEntity.kt` — column `@ColumnInfo` name matches expected mapping
- [ ] `SearchResultMapper.kt` — both `toDomain` and `toFoodNameEntity` updated
- [ ] `SearchDao.kt` — `CASE` tag is `'ISO-CC'` and `COALESCE` includes `name_ISO`; column name
  matches entity mapping
- [ ] Database version incremented and migration registered
- [ ] `locales_config.xml` — locale tag added
- [ ] `Translation.kt` — entry added
- [ ] `OpenFoodFactsProductMapper.kt` — branch added
- [ ] `RecipeFormTransformer.kt` — branch added
- [ ] `ProductFormTransformer.kt` — branch added
- [ ] `MealTemplates.kt` — new branch added matching the language defaults
