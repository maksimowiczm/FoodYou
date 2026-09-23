---
name: foodyou-release-process
description: Use this skill whenever asked to start, continue, or check on a release of the FoodYou app — including creating a release branch, bumping versions, drafting a changelog, checking metadata, or asking "what's left before we ship". Trigger on phrases like "cut a release", "start release X.Y.Z", "prepare the release", "release checklist", or "what's next for the release". This skill enforces the correct order of automated steps, mandatory manual checks, and developer confirmation gates, and hands off cleanly once the work becomes developer-only — do not skip the gates or attempt developer-only steps yourself even if asked to "just do the whole release."
---

# FoodYou: Release Process

Work through the release in this exact order. Never skip ahead or attempt a step out of turn,
and never do a step marked **[Manual]** yourself, even if asked.

Each step is tagged:

- **[Agent]** - do this yourself.
- **[Agent, dev confirms]** - draft/attempt it, but you need explicit developer sign-off before
  moving to the next step. If you can't produce something meaningful, say so plainly and hand it
  to the developer instead of forcing a weak result.
- **[Manual]** - entirely the developer's job. Don't run the commands or do the work — just tell
  the developer clearly what needs to happen, and wait for confirmation before moving on.

## Versioning Scheme

FoodYou uses semantic versioning: `MAJOR.MINOR.PATCH`, with an optional pre-release stage
progressing `alpha` → `beta` → `rc` → stable. Pre-release format is `<version>-<stage>.<n>`,
dot-separated, e.g. `4.1.0-alpha.1`, `4.1.0-beta.1`, `4.1.0-rc.1`, then `4.1.0` for the stable
release.

`android-versionCode` must strictly increase across every single one of these, including
between pre-release stages of the *same* version — `4.1.0-alpha.1`, `4.1.0-beta.1`,
`4.1.0-rc.1`, and `4.1.0` each get their own distinct, higher versionCode. Never reuse or
decrement it.

## Step 0 — Determine Target Version [Agent]

Always ask the developer directly what the exact target version and stage is for this release
(e.g. `4.1.0`, `4.1.0-beta.1`, `4.1.0-rc.2`). Never infer or guess it yourself from the current
branch, the last tag, or the size/content of the changelog — this is a call the developer makes
every time, not something to derive automatically. Use their answer verbatim for the branch
name and `version-name` in the steps below, and to work out the correct `android-versionCode`.

## Step 1 — Create Release Branch [Agent]

Branch off `main` using the version confirmed in Step 0: `chore/release-<version>` (e.g.,
`chore/release-4.1.0-beta.1`).

## Step 2 — Update Versioning [Agent]

Open [`libs.versions.toml`](../../../gradle/libs.versions.toml) and update the `[versions]`
block:

- `version-name` → the version confirmed in Step 0, e.g. `"4.1.0-beta.1"`
- `android-versionCode` → increment to a new value higher than any previous release *or*
  pre-release, e.g. `"1002"`

## Step 3 — Verify Reproducibility [Manual]

Do not compile, build, or hash anything yourself here — this must be done by hand.

Tell the developer:
> "Please build the release twice from a clean checkout and confirm the output hashes match
> before we continue."

Wait for explicit confirmation that the hashes matched before moving on.

## Step 4 — Create the Changelog [Agent, dev confirms]

1. Look at what changed since the last release, e.g. `git log <last-tag>..HEAD` or
   `git diff <last-tag>..HEAD`.
2. Try to draft `metadata/en-US/changelog/<versionCode>.txt` using these categories:
   **New features**, **Changes**, **Bug fixes**, **Translations**.

   Example:
   ```text
   New features
   - Added a new meal planning feature.

   Bug fixes
   - Fixed a crash on the settings screen.
   ```
3. **If the diff is too noisy, too technical, or otherwise doesn't translate into a meaningful
   user-facing changelog, say so directly** — don't force a low-quality summary. Ask the
   developer to write this one themselves instead.
4. Either way, present whatever you have (or the fact that you couldn't produce anything useful)
   to the developer and get explicit confirmation on the final changelog text before moving on.

## Step 5 — Ensure Metadata Is Up to Date [Agent, dev confirms]

1. Review the `metadata/` directory (descriptions, titles, screenshots, etc.) for anything that
   looks stale relative to the new version's changes.
2. Flag anything questionable — e.g. screenshots that may no longer reflect the current UI. If
   you can't tell whether something needs updating, say so rather than guessing.
3. Ask the developer to confirm the metadata is accurate and ready.

> [!IMPORTANT]
> Do not proceed past this point until the developer has explicitly confirmed the changelog
> (Step 4) and metadata (Step 5) are both ready.

---

## Everything Below Is Developer-Only [Manual]

From here on, your job is only to tell the developer what needs to happen next, in order. Do
not open PRs, merge branches, tag commits, run builds, draft GitHub releases, or upload files
yourself — even if it seems faster to just do it.

### Step 6 — Open a PR to `develop`

Open a pull request from the release branch into `develop` for review.

### Step 7 — Merge and Tag

1. Merge the PR.
2. Tag the resulting merge commit with the version name from Step 0: `git tag <version>` (e.g.,
   `git tag 4.1.0-beta.1`).
3. Push the tag: `git push origin <version>`.

### Step 8 — Build and Sign

Build, align, and sign the release artifact(s). For Android today, that's:

```sh
just release
```

### Step 9 — Create the GitHub Release

1. Go to the repo's releases page and draft a new release using the tag from Step 7.
2. Use the confirmed changelog from Step 4 as the release notes — the developer can lightly
   supplement with extra notes if it adds value, but the changelog is the source of truth.
3. If the target version has a pre-release stage (`alpha`, `beta`, `rc`), mark the GitHub
   release as a pre-release rather than the latest release.

### Step 10 — Upload App Installers

Attach the signed installer(s) from Step 8 to the GitHub release. Today that's the signed
`.apk`; treat this as a generic "upload installers for each supported platform" step so it
still applies cleanly once iOS (or others) are added.

### Step 11 — Merge into `main`, Publish Release

For a **stable** release: merge the release branch (or `develop`, per your usual flow) into
`main`, then publish the drafted GitHub release so it goes live.

For an **alpha/beta/rc** pre-release: `develop` is enough and `main` stays on the last stable
release — don't merge into `main`. Publish the pre-release on GitHub