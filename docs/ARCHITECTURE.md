## Food You – Architecture and Code Structure

**Food You** follows clean architecture principles, organizing the codebase into distinct layers:

- **`business`** – contains business logic, domain models, and infrastructure implementations organized by feature
- **`ComposeApp`** – the entry point of the application
- **`navigation`** – manages navigation between composables
- **`feature`** – the presentation layer, organized by UI features
- **`shared`** – shared utilities and components

---

### Business Layer

The **`business`** modules are further divided into shared and feature-specific code:

- **`business/shared`**

  - Shared domain models, services, and infrastructure implementations
  - Room database implementation lives here

- **`business/food`**

  - Food-related domain models and use cases
  - Implementations of remote food APIs (e.g., **OpenFoodFacts**, **FoodData Central (USDA)**)
  - Paging3 integration with remote mediators
  - This is the place where most of the complex logic resides, and I believe it can be improved over time. At the moment, I feel that I lack experience in designing clean architecture applications and sometimes tend to overengineer things.

- **`business/fooddiary`**

  - Meals, food diary entries, goals

- **`business/settings`**
- **`business/sponsorship`**

Repositories are typically implemented as simple wrappers around Room DAOs or DataStore. However, repositories can also combine multiple data sources when needed (e.g., merging local and remote data).

---

### Feature Layer

The **`feature`** modules are organized by UI screens or flows. Each feature module contains presentation logic, view models, and compose UI components.

This is the place where most degraded code can be found... I'm trying to improve it over time.

---

### Dependency Injection

Food You uses Koin for dependency injection. Modules which require DI define their own Koin modules in `com.maksimowiczm.foodyou.infrastructure.di` package and are loaded in the `ComposeApp` project with `initKoin` function.

Some Koin modules are complex with multiplatform deifinitions and generic types. Having this complexity isolated here makes it easier to manage.
