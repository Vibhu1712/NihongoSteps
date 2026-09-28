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
