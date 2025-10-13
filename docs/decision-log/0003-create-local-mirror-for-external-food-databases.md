## 3. Create local mirror for external food databases

Date: 2025-10-13

### Problem

Food You relies on external food databases like Open Food Facts and USDA FoodData Central to provide
comprehensive food information. Currently, app stores all fetched food items in single table in
local database and uses it's own search mechanism. However, external databases use their own search
mechanisms, which can lead to inconsistent search results and a suboptimal user experience, like:

- filtering out items that remote database provides
- food items being outdated

### Decision

To address these issues, I decided to create a local mirror of the external food databases. This
mirror would behave same as the remote databases. This means that the app would fetch food items
from the remote databases and store them in a local database, but it would also replicate the search
and filtering mechanisms of the remote databases. This way, users would get consistent search
results whether they are searching in the app or in browser on the remote database's website.

### Rationale

- **Consistency**: By replicating the search and filtering mechanisms of the remote databases, users
  will have a consistent experience.

### Consequences

- **Refactoring Effort**: Implementing this change will require significant refactoring of the
  existing codebase, particularly in the areas related to data fetching, storage, and search
  functionality.
- **Offline Access**: This would limit the app's ability to provide offline access to external food
  databases, as the local mirror would need an internet to search for food items. Maybe we could
  fallback to local search mechanism when offline.