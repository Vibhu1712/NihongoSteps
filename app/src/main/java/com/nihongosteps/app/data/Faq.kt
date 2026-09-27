package com.nihongosteps.app.data

data class FaqItem(val question: String, val answer: String)
data class FaqCategory(val title: String, val items: List<FaqItem>)

/**
 * Beginner-facing help content, written for someone who has never seen Japanese
 * before. Shown in the in-app FAQ (Me → Help & FAQ, and a "New here?" card on Learn).
 */
object FaqData {

    val categories: List<FaqCategory> = listOf(
        FaqCategory(
            "Japanese, in brief",
            listOf(
                FaqItem(
                    "What is \"Nihongo\"?",
                    "Nihongo (日本語) is simply the Japanese word for \"the Japanese language\" — " +
                        "everything in this app is one step towards being able to read, write and use it.",
                ),
                FaqItem(
                    "What writing systems does Japanese use?",
                    "Three, used together in the same sentence: hiragana and katakana (two phonetic " +
                        "alphabets, each with 46 basic characters) and kanji (characters borrowed from " +
                        "Chinese that carry meaning). A real sentence mixes all three, e.g. 私は日本語を勉強します " +
                        "uses kanji (私, 日本語, 勉強) and hiragana (は, を, します) side by side.",
                ),
                FaqItem(
                    "What is hiragana?",
                    "The everyday phonetic alphabet, used for grammar endings and native Japanese words. " +
                        "Every character stands for one sound, like あ (a), か (ka) or ん (n). It's the first " +
                        "thing Japanese children learn to read, and the first thing this app teaches too.",
                ),
                FaqItem(
                    "What is katakana?",
                    "A second phonetic alphabet with the same 46 sounds as hiragana, but angular instead of " +
                        "curved (compare あ with ア). It's mainly used for foreign words and names — コーヒー " +
                        "(coffee), アメリカ (America) — and for emphasis, a bit like italics in English.",
                ),
                FaqItem(
                    "What is kanji, and do I need it as a beginner?",
                    "Kanji are characters that carry a meaning rather than just a sound — 山 means \"mountain\" " +
                        "and is read differently depending on the word it's in. There are thousands in total, " +
                        "but you don't need them to get started: master hiragana and katakana first, then this " +
                        "app introduces a gentle first 39 kanji (numbers, days, family, simple nouns) once " +
                        "you're ready, in Write → Kanji.",
                ),
                FaqItem(
                    "What is \"romaji\"?",
                    "Japanese written with the English alphabet, e.g. \"konnichiwa\" for こんにちは. It's a " +
                        "helpful crutch for absolute beginners, but it isn't real Japanese writing — no one in " +
                        "Japan reads or writes it day to day, so treat it as training wheels.",
                ),
                FaqItem(
                    "Should I learn romaji or kana first?",
                    "Kana. Romaji feels faster at the very start, but it quietly stops you from reading real " +
                        "Japanese. This app shows romaji under most Japanese text so you're never stuck, but " +
                        "there's a \"Show romaji\" switch in Me — turn it off as soon as you can read hiragana, " +
                        "usually within a week or two of practice.",
                ),
                FaqItem(
                    "Is Japanese hard to learn?",
                    "It has an easier side and a harder side. Pronunciation and grammar rules are unusually " +
                        "regular — far fewer exceptions than English. What takes time is the writing system " +
                        "and having to build sentences in a different word order. Going one small step at a " +
                        "time, the way this app is structured, makes it manageable.",
                ),
                FaqItem(
                    "Why do Japanese sentences feel backwards?",
                    "English usually goes subject–verb–object (\"I eat sushi\"). Japanese goes subject–object–" +
                        "verb (\"I sushi eat\" — 私は寿司を食べます), and the verb almost always comes last. " +
                        "Small words called particles (は, を, に, で…) mark each part's role instead of word " +
                        "order doing it — the Grammar lessons walk through the most important ones.",
                ),
                FaqItem(
                    "What do です and ます mean? Why does Japanese sound so \"polite\"?",
                    "です and ます are polite sentence endings — roughly \"is/am/are\" and the polite verb " +
                        "ending. Japanese has different levels of politeness built into the grammar itself, " +
                        "not just word choice. This app teaches the polite ます/です forms first, since they're " +
                        "safe to use with anyone, in any situation.",
                ),
                FaqItem(
                    "What does \"JLPT N5\" mean?",
                    "The Japanese Language Proficiency Test (JLPT) has five levels, N5 being the easiest and " +
                        "N1 the hardest. N5 covers basic grammar, around 800 words and 100 kanji — roughly " +
                        "the level this app's Grammar and Vocabulary sections are aimed at.",
                ),
            ),
        ),
        FaqCategory(
            "Using Nihongo Steps",
            listOf(
                FaqItem(
                    "I've never studied Japanese — where do I start?",
                    "Learn → Hiragana first. Read through the chart, then switch to the Write tab and trace " +
                        "each character a few times. Once hiragana feels familiar, do the same for Katakana, " +
                        "then move on to Vocabulary and the first Grammar lesson.",
                ),
                FaqItem(
                    "What are the four tabs for?",
                    "Learn is where you study — kana charts, vocabulary, grammar and sentence translation. " +
                        "Write is handwriting practice for kana and kanji. Play has short quiz games to " +
                        "revise what you've already studied. Me holds your stats and settings.",
                ),
                FaqItem(
                    "What are XP, levels and the daily goal ring?",
                    "XP (experience points) are earned by finishing a lesson, writing a character well, or " +
                        "playing a game — they're just a way to see your effort add up. Levelling up needs " +
                        "more XP each time. The ring on the Learn tab fills toward your daily goal, which you " +
                        "can set to Casual, Regular or Serious in Me.",
                ),
                FaqItem(
                    "How does the handwriting check work?",
                    "It compares the shape of what you drew with the reference character and gives a rough " +
                        "similarity score — a passing score earns XP. It does not check stroke order or " +
                        "direction, so use the on-screen guide and the stroke count shown above the writing " +
                        "square to learn the correct order yourself.",
                ),
                FaqItem(
                    "Why is there no sound / the speaker icon is missing?",
                    "Audio uses your phone's own Japanese text-to-speech voice, which many phones don't have " +
                        "installed by default. Go to Me and tap \"Install Japanese voice\", or manually via " +
                        "Settings → System → Languages → Text-to-speech, then restart the app.",
                ),
                FaqItem(
                    "Do I need an internet connection?",
                    "No. Every lesson, word, character and game is built into the app, so it works fully " +
                        "offline — handy for studying on a commute or a flight.",
                ),
                FaqItem(
                    "Is my progress saved? What happens if I uninstall?",
                    "Progress is saved automatically on your device as you go. Uninstalling the app removes " +
                        "it, the same as any other app, unless your phone's Android backup happens to include " +
                        "it. See Me → Privacy policy for the full details.",
                ),
                FaqItem(
                    "Is the app free?",
                    "Yes — learning, writing practice and the quiz games are free to use.",
                ),
            ),
        ),
    )
}
