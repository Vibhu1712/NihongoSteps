package com.nihongosteps.app.data

enum class Script(val label: String, val native: String) {
    HIRAGANA("Hiragana", "ひらがな"),
    KATAKANA("Katakana", "カタカナ"),
}

data class Kana(
    val char: String,
    val romaji: String,
    val strokes: Int,
    val script: Script,
    val voiced: Boolean,
)

/** A kanji used in the writing practice. */
data class Kanji(
    val char: String,
    val meaning: String,
    val on: String,
    val kun: String,
    val strokes: Int,
)

object KanaData {

    private const val HIRA_BASIC =
        "あ:a:3 い:i:2 う:u:2 え:e:2 お:o:3 か:ka:3 き:ki:4 く:ku:1 け:ke:3 こ:ko:2 " +
        "さ:sa:3 し:shi:1 す:su:2 せ:se:3 そ:so:1 た:ta:4 ち:chi:2 つ:tsu:1 て:te:1 と:to:2 " +
        "な:na:4 に:ni:3 ぬ:nu:2 ね:ne:2 の:no:1 は:ha:3 ひ:hi:1 ふ:fu:4 へ:he:1 ほ:ho:4 " +
        "ま:ma:3 み:mi:2 む:mu:3 め:me:2 も:mo:3 や:ya:3 ゆ:yu:2 よ:yo:2 " +
        "ら:ra:2 り:ri:2 る:ru:1 れ:re:2 ろ:ro:1 わ:wa:2 を:wo:3 ん:n:1"

    private const val KATA_BASIC =
        "ア:a:2 イ:i:2 ウ:u:3 エ:e:3 オ:o:3 カ:ka:2 キ:ki:3 ク:ku:2 ケ:ke:3 コ:ko:2 " +
        "サ:sa:3 シ:shi:3 ス:su:2 セ:se:2 ソ:so:2 タ:ta:3 チ:chi:3 ツ:tsu:3 テ:te:3 ト:to:2 " +
        "ナ:na:2 ニ:ni:2 ヌ:nu:2 ネ:ne:4 ノ:no:1 ハ:ha:2 ヒ:hi:2 フ:fu:1 ヘ:he:1 ホ:ho:4 " +
        "マ:ma:2 ミ:mi:3 ム:mu:2 メ:me:2 モ:mo:3 ヤ:ya:2 ユ:yu:2 ヨ:yo:3 " +
        "ラ:ra:2 リ:ri:2 ル:ru:2 レ:re:1 ロ:ro:3 ワ:wa:2 ヲ:wo:3 ン:n:2"

    // voiced char : romaji : base char. Stroke count = base + 2 (dakuten) or + 1 (handakuten).
    private const val HIRA_VOICED =
        "が:ga:か ぎ:gi:き ぐ:gu:く げ:ge:け ご:go:こ ざ:za:さ じ:ji:し ず:zu:す ぜ:ze:せ ぞ:zo:そ " +
        "だ:da:た ぢ:ji:ち づ:zu:つ で:de:て ど:do:と ば:ba:は び:bi:ひ ぶ:bu:ふ べ:be:へ ぼ:bo:ほ " +
        "ぱ:pa:は ぴ:pi:ひ ぷ:pu:ふ ぺ:pe:へ ぽ:po:ほ"

    private const val KATA_VOICED =
        "ガ:ga:カ ギ:gi:キ グ:gu:ク ゲ:ge:ケ ゴ:go:コ ザ:za:サ ジ:ji:シ ズ:zu:ス ゼ:ze:セ ゾ:zo:ソ " +
        "ダ:da:タ ヂ:ji:チ ヅ:zu:ツ デ:de:テ ド:do:ト バ:ba:ハ ビ:bi:ヒ ブ:bu:フ ベ:be:ヘ ボ:bo:ホ " +
        "パ:pa:ハ ピ:pi:ヒ プ:pu:フ ペ:pe:ヘ ポ:po:ホ"

    private fun parseBasic(src: String, script: Script): List<Kana> =
        src.split(" ").map {
            val (c, r, s) = it.split(":")
            Kana(c, r, s.toInt(), script, voiced = false)
        }

    private fun parseVoiced(src: String, base: List<Kana>, script: Script): List<Kana> {
        val byChar = base.associateBy { it.char }
        return src.split(" ").map {
            val (c, r, b) = it.split(":")
            val extra = if (r.startsWith("p")) 1 else 2
            Kana(c, r, (byChar[b]?.strokes ?: 0) + extra, script, voiced = true)
        }
    }

    val hiraganaBasic = parseBasic(HIRA_BASIC, Script.HIRAGANA)
    val katakanaBasic = parseBasic(KATA_BASIC, Script.KATAKANA)
    val hiraganaVoiced = parseVoiced(HIRA_VOICED, hiraganaBasic, Script.HIRAGANA)
    val katakanaVoiced = parseVoiced(KATA_VOICED, katakanaBasic, Script.KATAKANA)

    fun basic(script: Script) = if (script == Script.HIRAGANA) hiraganaBasic else katakanaBasic
    fun voiced(script: Script) = if (script == Script.HIRAGANA) hiraganaVoiced else katakanaVoiced
    fun all(script: Script) = basic(script) + voiced(script)

    /**
     * The classic gojūon chart: 5 columns (a i u e o), null = empty cell.
     */
    fun chart(script: Script): List<Kana?> {
        val k = basic(script)
        val cells = mutableListOf<Kana?>()
        cells += k.subList(0, 35)                         // a .. ma rows
        cells += listOf(k[35], null, k[36], null, k[37])  // ya yu yo
        cells += k.subList(38, 43)                        // ra row
        cells += listOf(k[43], null, null, null, k[44])   // wa wo
        cells += listOf(k[45], null, null, null, null)    // n
        return cells
    }

    fun voicedChart(script: Script): List<Kana?> = voiced(script)

    val kanji: List<Kanji> = listOf(
        Kanji("一", "one", "イチ", "ひと(つ)", 1),
        Kanji("二", "two", "ニ", "ふた(つ)", 2),
        Kanji("三", "three", "サン", "みっ(つ)", 3),
        Kanji("四", "four", "シ", "よん・よっ(つ)", 5),
        Kanji("五", "five", "ゴ", "いつ(つ)", 4),
        Kanji("六", "six", "ロク", "むっ(つ)", 4),
        Kanji("七", "seven", "シチ", "なな", 2),
        Kanji("八", "eight", "ハチ", "やっ(つ)", 2),
        Kanji("九", "nine", "キュウ・ク", "ここの(つ)", 2),
        Kanji("十", "ten", "ジュウ", "とお", 2),
        Kanji("日", "sun, day", "ニチ", "ひ", 4),
        Kanji("月", "moon, month", "ゲツ・ガツ", "つき", 4),
        Kanji("火", "fire", "カ", "ひ", 4),
        Kanji("水", "water", "スイ", "みず", 4),
        Kanji("木", "tree", "モク", "き", 4),
        Kanji("人", "person", "ジン・ニン", "ひと", 2),
        Kanji("口", "mouth", "コウ", "くち", 3),
        Kanji("山", "mountain", "サン", "やま", 3),
        Kanji("川", "river", "セン", "かわ", 3),
        Kanji("田", "rice field", "デン", "た", 5),
        Kanji("目", "eye", "モク", "め", 5),
        Kanji("手", "hand", "シュ", "て", 4),
        Kanji("大", "big", "ダイ", "おお(きい)", 3),
        Kanji("小", "small", "ショウ", "ちい(さい)", 3),
        Kanji("中", "middle, inside", "チュウ", "なか", 4),
        Kanji("上", "up, above", "ジョウ", "うえ", 3),
        Kanji("下", "down, below", "カ", "した", 3),
        Kanji("本", "book, origin", "ホン", "もと", 5),
        Kanji("子", "child", "シ", "こ", 3),
        Kanji("女", "woman", "ジョ", "おんな", 3),
        Kanji("男", "man", "ダン", "おとこ", 7),
        Kanji("円", "yen, circle", "エン", "まる(い)", 4),
        Kanji("年", "year", "ネン", "とし", 6),
        Kanji("今", "now", "コン", "いま", 4),
        Kanji("何", "what", "カ", "なに・なん", 7),
        Kanji("学", "study", "ガク", "まな(ぶ)", 8),
        Kanji("生", "life, birth", "セイ", "い(きる)", 5),
        Kanji("先", "ahead, previous", "セン", "さき", 6),
        Kanji("名", "name", "メイ", "な", 6),
    )
}
