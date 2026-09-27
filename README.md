# Nihongo Steps — learn elementary Japanese (Android)

A fully offline Android app for absolute beginners, built with **Kotlin + Jetpack Compose
(Material 3)**. No internet permission, no ads, no analytics.

| Tab | What's inside |
|-----|---------------|
| **Learn** | Hiragana & katakana charts (basic + voiced, audio, stroke counts) · 16 grammar lessons with patterns, English explanations, romaji examples, "watch out" tips and quick checks · 122 words in 10 topics · Sentence Builder (32 English → Japanese tile translations, alternate word orders accepted) |
| **Write** | Handwriting practice for all kana + 39 kanji on a genkōyōshi square, guide toggle, undo/clear, on-device similarity score (+XP when ≥ 55 %) |
| **Play** | Kana Sprint · Word Quiz (EN→JP, JP→EN, listening; by topic) · Memory Match (hiragana / katakana / words) · Sentence round |
| **Me** | XP, level, streak, daily goal (20/50/100 XP), romaji on/off, speech speed, install Japanese voice, **Help & FAQ**, privacy policy, licences, reset |

Light and dark themes, edge-to-edge, splash screen, adaptive + themed (monochrome) icon. A short,
beginner-friendly **FAQ** (what hiragana/katakana/kanji are, how the app works) is reachable from
Me → Help & FAQ, and a "New to Japanese?" card points brand-new users to it from the Learn tab.

---

## 1. Open and run

1. Install the latest **Android Studio** (Narwhal or newer) — it bundles JDK 21.
2. *File → Open…* and select this folder. Let Gradle sync (first sync downloads
   ~1 GB of dependencies). If Studio offers dependency/AGP upgrades, accepting them
   is fine.
3. Pick a device (API 26+ / Android 8.0+) and press **Run ▶**.
4. For pronunciation, the device needs a Japanese TTS voice:
   *Settings → System → Languages → Text-to-speech → Google → Install voice data →
   Japanese* (the app's **Me → Install Japanese voice** button opens this).

> The project was written without access to the Android SDK, so it has not been
> compiled yet. If the first build reports anything, it will be a small fix — paste
> the error back to Claude.

## 1b. Or build the APK on GitHub (no Android Studio needed)

The workflow in `.github/workflows/build.yml` builds the app on GitHub's servers.

1. Create a new repository on github.com (private is fine).
2. On the empty repo page click **uploading an existing file**, then drag in
   **everything inside** the `NihongoSteps` folder (including `.github`), so that
   `gradlew` sits at the top level of the repo. Commit.
   *If `.github` didn't upload, use Add file → Create new file, type the name
   `.github/workflows/build.yml` and paste the file's contents.*
3. Open the **Actions** tab. The *Build APK* run starts by itself (first run ≈ 8–10 min;
   use **Run workflow** to start it manually any time).
4. When it's green, open the run → **Artifacts** → `nihongo-steps-debug-apk`.
   Unzip it, copy `app-debug.apk` to your phone and install (allow
   "Install unknown apps" when asked).
5. If a step fails, open it, copy the red error lines and send them to whoever is
   helping you (e.g. Claude).

**Signed release APK + AAB for Play** — create the upload key (section 3), then add
two repository secrets under *Settings → Secrets and variables → Actions*:

| Secret | Value |
|---|---|
| `KEYSTORE_BASE64` | the keystore as base64 — Windows PowerShell: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload-keystore.jks")) \| Set-Clipboard` · macOS/Linux: `base64 -i upload-keystore.jks \| pbcopy` or `base64 -w0 upload-keystore.jks` |
| `KEYSTORE_PASSWORD` | the keystore password |

(Optional: `KEY_ALIAS` if it isn't `upload`, `KEY_PASSWORD` if it differs.) The next run
also produces `nihongo-steps-release` containing `app-release.apk` and
`app-release.aab` — the `.aab` is what you upload to Play Console.

**Direct download link:** *Releases → Draft a new release*, create a tag such as
`v1.0.0` and publish. The workflow attaches the APK to that release page, which is
the easiest way to install it on phones.

> Debug builds made on GitHub are signed with a throw-away key that changes every
> run, so to install a newer debug APK you must uninstall the old one first. Signed
> release APKs update in place.

## 2. Before publishing — make it yours

1. **Change the applicationId** in `app/build.gradle.kts`
   (`com.nihongosteps.app` → e.g. `com.yourname.nihongosteps`). It must be unique on
   Play and can never change after the first upload. You don't need to rename the
   Kotlin package.
2. Replace the placeholders (`<your name>`, `<your support email>`, `<date>`) in
   `store/privacy_policy.md`. The in-app summary (`PRIVACY` in
   `ui/screens/MeScreen.kt`) already matches; add your email there if you like.
3. Bump `versionCode` (+1) and `versionName` for every upload.

## 3. Create an upload key (once — back it up!)

```bash
keytool -genkeypair -v -keystore upload-keystore.jks -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000
```

Copy `keystore.properties.example` → `keystore.properties` and fill in the passwords.
Both files are git-ignored. Losing this key means contacting Play support to reset it,
so store it somewhere safe. Use **Play App Signing** (default) — Google holds the
actual app-signing key.

## 4. Build the release bundle

```bash
./gradlew bundleRelease          # Windows: gradlew.bat bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`.
Release builds are minified with R8 and resource-shrunk. Test the release build on a
real device before uploading (*Build Variants → release*).

## 5. Publish on Google Play

1. Create a developer account at <https://play.google.com/console> (one-time US$25).
2. **Create app** → name, default language English, *App*, *Free*.
3. **Store listing**: copy text from `store/listing.md`, upload
   `store/icon-512.png`, `store/feature-graphic-1024x500.png` and at least 2 phone
   screenshots (list of suggested shots is in `listing.md`).
4. **App content**: privacy-policy URL, ads = No, content rating, target audience,
   Data safety — the answers are in `store/data_safety.md`.
   Host the privacy policy anywhere public (GitHub Pages, Google Sites, etc.).
5. **Testing → Closed testing**: upload the `.aab`. *Personal developer accounts
   created after Nov 2023 must run a closed test with at least 12 testers opted in
   for 14 consecutive days* before Production access can be requested.
6. **Production → Create release** → upload the `.aab` → roll out. First reviews
   usually take a few days.

**Target API level:** the app targets **API 36** (Android 16), which Google Play
requires for new apps and updates from 31 Aug 2026 (extension possible to 1 Nov 2026).
Each year Google raises this — bump `targetSdk`/`compileSdk` when it does.

## Project structure

```
.github/workflows/build.yml   GitHub Actions: builds APK/AAB in the cloud
app/src/main/java/com/nihongosteps/app/
├── MainActivity.kt, NihongoApp.kt
├── data/     Kana.kt, Vocab.kt, Grammar.kt, Sentences.kt, ProgressRepository.kt (DataStore)
├── util/     Speaker.kt (TextToSpeech), HandwritingGrader.kt
└── ui/       AppRoot.kt (navigation), Components.kt, theme/Theme.kt,
              screens/ Learn, KanaChart, Vocab, Grammar, Write, QuizRunner,
                       Games (Play hub, Kana Sprint, Word Quiz, Memory Match),
                       SentenceBuilder, Me
store/        icon-512.png, feature-graphic-1024x500.png, listing.md,
              privacy_policy.md, data_safety.md
```

**Adding content** is just editing data files: a new word is one line
(`"日本|にほん|nihon|Japan"`) in `data/Vocab.kt`; lessons, sentence tasks and kanji
follow the same pattern. If you add kanji that aren't already in the app, regenerate
the subset font (below) or they'll fall back to the system font.

### Fonts
Japanese text uses **Klee One** (Fontworks, SIL Open Font License 1.1), subset to the
characters the app uses (~170 KB each). To re-subset after adding new kanji:

```bash
pip install fonttools
pyftsubset KleeOne-Regular.ttf --text-file=chars.txt --unicodes="U+3000-30FF,U+FF01-FF5E,U+0020-007E" \
  --layout-features=kern,palt,liga --no-hinting --output-file=app/src/main/res/font/klee_one.ttf
```
(repeat for SemiBold → `klee_one_semibold.ttf`). The licence ships in
`app/src/main/assets/licenses/` and is shown in **Me → Open-source licences**.

## Notes & limitations
- The handwriting score compares the shape of your drawing with the reference glyph;
  it does not check stroke order or direction.
- Audio relies on the device's Japanese TTS voice; quality varies by manufacturer.
- Progress is stored locally (DataStore) and included in Android's device backup.

## Licence
App code: yours to license as you like. Klee One font: SIL OFL 1.1.
