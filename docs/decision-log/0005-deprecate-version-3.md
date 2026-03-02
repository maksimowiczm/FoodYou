## 5. Deprecate version 3

Date: 2026-03-02

### Problem

Food You 3.x.x has been in active development for some time, but maintaining two major versions
simultaneously requires significant effort and resources. Version 3.x.x also contains several
fundamental design decisions that have proven to be suboptimal and difficult to maintain. Version 4
represents a significant improvement in architecture, features, and user experience, and continuing
to invest heavily in version 3 diverts attention from these advancements.

### Decision

I decided to deprecate Food You 3.x.x. From this point forward:

- Version 3.x.x will only receive **critical bug fixes** and **translation updates**
- No new features will be added to version 3
- All development effort will focus on Food You 4

### Rationale

- **Better User Experience**: Version 4 provides a better foundation for future development and
  offers users an improved experience.
- **Sustainable Maintenance**: Limiting version 3 support to critical fixes and translations makes
  the maintenance burden manageable while still supporting existing users.
- **Clear Migration Path**: Users have a clear signal that version 4 is the future of the app,
  encouraging migration when ready.

### Consequences

- **User Impact**: Users on version 3 will not receive new features, though their app will remain
  functional with critical fixes.
- **Reduced Maintenance Burden**: Less time spent on version 3 means more time for version 4
  development.
