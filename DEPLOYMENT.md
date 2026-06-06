# Deployment

Releases are automated with two GitHub Actions workflows and
[Gradle Play Publisher](https://github.com/Triple-T/gradle-play-publisher). A release builds a
signed Android App Bundle and uploads it to the Play **internal** track; promotion to production is
a manual click in the Play Console.

## Steady-state flow

1. Run the **Prepare Release** workflow (Actions tab → *Prepare Release* → Run) and pick `patch`,
   `minor`, or `major`. It bumps `Configuration.kt` on `develop` and opens a `develop → main` PR
   titled `Release vX.Y.Z`.
2. Review and **merge that PR with a merge commit** (never squash — see CLAUDE.md).
3. The **Release** workflow runs on the push to `main`: it runs Spotless + unit tests, builds the
   signed AAB, uploads it to the Play internal track, tags `vX.Y.Z`, and cuts a GitHub Release.
4. In the Play Console, promote the internal release to production when ready.

That's it — the only manual acts are clicking *Prepare Release* and merging the PR.

## One-time setup (do these once before the first automated release)

The Play Publisher API cannot bootstrap a brand-new app, so the first release is manual.

1. **Create the app** in the Play Console and complete the store listing, content rating, and
   data-safety form.
2. **Enable Play App Signing.** Generate an *upload* keystore locally — Google holds the real
   app-signing key:
   ```bash
   keytool -genkey -v -keystore upload.keystore -alias upload \
     -keyalg RSA -keysize 2048 -validity 10000
   ```
3. **Upload the first AAB manually.** Build it locally with the keystore env vars set (see below)
   and `./gradlew :app:bundleRelease`, then upload `app/build/outputs/bundle/release/app-release.aab`
   to your target track in the Console. Every upload after this can be automated.
4. **Create a service account** in Google Cloud Console, then in the Play Console under
   *Users & permissions* invite the service-account email and grant it release permissions.
   Download the JSON key.

## Required GitHub secrets

Add these under *Settings → Secrets and variables → Actions*:

| Secret | What it is |
| --- | --- |
| `KEYSTORE_BASE64` | `base64 -i upload.keystore` — the upload keystore, base64-encoded |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (e.g. `upload`) |
| `KEY_PASSWORD` | Key password |
| `PLAY_SERVICE_ACCOUNT_JSON` | Full contents of the service-account JSON key |

## Building a signed release locally (optional)

```bash
export KEYSTORE_PATH=/abs/path/to/upload.keystore
export KEYSTORE_PASSWORD=...
export KEY_ALIAS=upload
export KEY_PASSWORD=...
./gradlew :app:bundleRelease
```

Without these env vars the release build falls back to debug signing, so plain local builds keep
working.
