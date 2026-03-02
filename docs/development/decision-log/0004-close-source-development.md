# Development Branch Access Restriction

Date: 2026-03-01

## Problem

Food You source code appears to be heavily scraped by bots/LLMs. Keeping the full development
history publicly visible makes it easy to continuously ingest unreleased work, and I do not want to
provide free training data at the expense of the project.

## Decision

Going forward, development will no longer occur in public.

App releases will still be published, and source code will still be available, but only for released
versions. The source code for the development version will not be publicly available until it is
released.

## Rationale

- **Reduce scraping**: Publishing only released snapshots reduces the value of continuous scraping
  of
  in-progress work.
- **Protect work-in-progress**: Keeps unreleased features, refactors, and experiments private until
  they are ready.

## Consequences

- **Less transparency**: External users will not see day-to-day development activity.
- **Reduced external contributions**: Drive-by fixes and community collaboration will be harder.
  Developers still can fork the repository, but it might be hard to keep it up to date with the main
  development branch without access to the latest code.
- **Release process changes**: Releasing should include publishing the corresponding source
  snapshot.