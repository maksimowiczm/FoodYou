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

### [`Language.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/common/domain/Language.kt)

Read the file. Add a new enum entry after the last existing one:

```kotlin
NewLanguage("DISPLAY_NAME", "ISO", "CC"),
```

### [`FoodName.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/common/domain/food/FoodName.kt)

Read the file. Make four changes:

1. **Property** — add `val ISO: String? = null` alongside existing language properties
2. **`list` property** — append `ISO` to the `listOf(...)`
3. **`get(language: Language)` function** — add a branch: `Language.NewLanguage -> ISO`
4. **`requireAll` companion function** — add the new parameter

---

## Step 3 — Infrastructure layer

### [`FoodNameEntity.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/FoodNameEntity.kt)

Read the file. Make three changes:

1. **Column** — add `@ColumnInfo(name = "name_ISO") val ISO: String? = null`
2. **`anyProvided` check** in the `init` block — include `ISO != null`
3. **`fallback` getter** — append `ISO` to the list

> The column name in the database must be `name_ISO` (e.g. `name_pl`). Keep this consistent with
> existing columns.

### [`SearchResultMapper.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/SearchResultMapper.kt)

Read the file. Make two changes:

1. In `toDomain(entity: SearchEntity)` — add `ISO = entity.ISO` to the `FoodName.requireAll(...)`
   call
2. In `toFoodNameEntity(name: FoodName)` — map `ISO = name.ISO`

### [`SearchDao.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/search/infrastructure/SearchDao.kt)

Read the `SIMPLE_NAME_SELECT` constant carefully. Make two changes:

1. Add a `WHEN` branch in the `CASE :languageCode` block:
   ```sql
   WHEN 'ISO-CC' THEN s.name_ISO
   ```
2. Add `s.name_ISO` to the `COALESCE` fallback list

The column name here (`name_ISO`) must match exactly what you used in `FoodNameEntity`.

### [
`OpenFoodFactsProductMapper.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/openfoodfacts/infrastructure/OpenFoodFactsProductMapper.kt)

In `toModel()`, add the new language to the `FoodName.requireAll` call:

```kotlin
ISO =
    localizedNames["ISO"].takeIfNotBlank()
        ?: localizedGenericNames["ISO"].takeIfNotBlank(),
```

---

## Step 4 — Database migration

**This step is mandatory.** Adding a column to `FoodNameEntity` is a breaking schema change.

1. Open [
   `ReadModelDatabase.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/infrastructure/room/ReadModelDatabase.kt)
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

### [`Translation.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/ui/language/Translation.kt)

Add to the `languages` list:

```kotlin
Translation("DISPLAY_NAME", Language.NewLanguage),
```

### [`RecipeFormTransformer.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/ui/food/recipe/RecipeFormTransformer.kt) and [`ProductFormTransformer.kt`](../../../composeApp/src/commonMain/kotlin/com/maksimowiczm/foodyou/app/ui/userproduct/ProductFormTransformer.kt)
In both files, update the `FoodName.requireAll` call inside the `transform` function:

```kotlin
ISO = if (language == Language.NewLanguage) nameStr else null,
```

---

## Step 7 — Validation checklist

After all edits, verify:

- [ ] `Language.kt` — new enum entry present with correct ISO, CC, and display name
- [ ] `FoodName.kt` — property, `list`, `get()`, and `requireAll` all updated
- [ ] `FoodNameEntity.kt` — column name is `name_ISO`, `anyProvided` and `fallback` updated
- [ ] `SearchResultMapper.kt` — both `toDomain` and `toFoodNameEntity` updated
- [ ] `SearchDao.kt` — `CASE` tag is `'ISO-CC'` and `COALESCE` includes `name_ISO`; column name
  matches entity
- [ ] Database version incremented and migration registered
- [ ] `locales_config.xml` — locale tag added
- [ ] `Translation.kt` — entry added
- [ ] `OpenFoodFactsProductMapper.kt` — branch added
- [ ] `RecipeFormTransformer.kt` — branch added
- [ ] `ProductFormTransformer.kt` — branch added

The most common mistake is a mismatch between the column name in `FoodNameEntity` and the column
referenced in `SearchDao`. Double-check these match before finishing.