## 2. Minimize Gradle modules

Date: 2025-09-25

### Problem

I started Food You as a random, experimental project with a multiple modules structure in mind. As
the project grew, I realized that the multiple modules structure added unnecessary complexity and
overhead to the development process. The modules were not providing significant benefits in terms of
code organization or reusability, and they were making it harder to manage dependencies and build
configurations. This complexity was slowing down development.

### Decision

I decided to minimize the number of Gradle modules in the Food You project. I decided to restructure
the project into the following modules:

- `app`: The main application module that contains the core functionality of the app.
- `resources`: A module for static resources such as images, fonts, strings.
- `common`: A module for utilities that are not specific to any feature but can be used across the
  app.
- `barcodescanner`: A module for barcode scanning functionality. It was separated due to it using a
  older Android XML layout system.

Obviously, the `app` module will be a large module, but if one of its parts grows too big, we can
always consider extracting it into a separate module.

### Rationale

- **Simplicity**: Reducing the number of modules simplifies the project structure, making it easier
  to navigate and understand.
- **Reduced Overhead**: Fewer modules mean less configuration and management overhead, leading to
  faster development.

### Consequences

- **Easier Maintenance**: With fewer modules, maintaining the project becomes easier, as there are
  fewer moving parts to manage.
- **Loss of Modularity**: While the consolidation improves simplicity, it may reduce the modularity
  of the codebase, making it harder to isolate and test specific components independently.
- **Refactoring Effort**: The process of consolidating modules requires refactoring of existing
  code, which could introduce bugs.
