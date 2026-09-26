package com.nihongosteps.app.data

data class Tile(val jp: String, val romaji: String)

data class SentenceTask(
    val en: String,
    val tiles: List<Tile>,               // correct tiles + distractors (unshuffled)
    val answers: List<List<String>>,     // every accepted order, as jp strings
    val note: String,
) {
    val mainAnswer: List<String> get() = answers.first()
    fun isCorrect(chosen: List<String>) = answers.any { it == chosen }
}

object SentenceData {

    /**
     * main    = "jp:romaji jp:romaji …" in the preferred order
     * extra   = distractor tiles in the same format
     * alts    = other accepted orders, jp only, separated by spaces
     */
    private fun s(en: String, main: String, extra: String, note: String, vararg alts: String): SentenceTask {
        fun parse(src: String) = src.trim().split(" ").filter { it.isNotBlank() }.map {
            val i = it.indexOf(':')
            Tile(it.substring(0, i), it.substring(i + 1).replace('_', ' '))
        }
        val correct = parse(main)
        val distract = parse(extra)
        val answers = listOf(correct.map { it.jp }) + alts.map { it.split(" ") }
        return SentenceTask(en, correct + distract, answers, note)
    }

    val tasks: List<SentenceTask> = listOf(
        s("I am a student.", "わたし:watashi は:wa がくせい:gakusei です:desu", "を:o せんせい:sensei",
            "Topic は + noun + です."),
        s("This is a book.", "これ:kore は:wa ほん:hon です:desu", "この:kono を:o",
            "これ stands alone; この needs a noun after it."),
        s("That is my bag.", "それ:sore は:wa わたし:watashi の:no かばん:kaban です:desu", "を:o ねこ:neko",
            "わたしの かばん = my bag."),
        s("Is this water?", "これ:kore は:wa みず:mizu です:desu か:ka", "の:no おちゃ:ocha",
            "か at the end makes a question."),
        s("I am not a teacher.", "わたし:watashi は:wa せんせい:sensei じゃありません:ja_arimasen", "です:desu が:ga",
            "じゃありません = is not."),
        s("I eat bread.", "わたし:watashi は:wa パン:pan を:o たべます:tabemasu", "に:ni のみます:nomimasu",
            "The object takes を and the verb comes last."),
        s("I drink coffee every day.", "まいにち:mainichi コーヒー:koohii を:o のみます:nomimasu", "に:ni たべます:tabemasu",
            "まいにち needs no particle.", "コーヒー を まいにち のみます"),
        s("I go to school.", "がっこう:gakkou に:ni いきます:ikimasu", "へ:e を:o",
            "に or へ both mark the destination.", "がっこう へ いきます"),
        s("I will go to Japan tomorrow.", "あした:ashita にほん:nihon に:ni いきます:ikimasu", "で:de いきました:ikimashita",
            "ます also expresses the future.", "にほん に あした いきます"),
        s("I read a book at the library.", "としょかん:toshokan で:de ほん:hon を:o よみます:yomimasu", "に:ni が:ga",
            "で marks where an action happens.", "ほん を としょかん で よみます"),
        s("There is a cat.", "ねこ:neko が:ga います:imasu", "あります:arimasu を:o",
            "Animals use います."),
        s("There is a station.", "えき:eki が:ga あります:arimasu", "います:imasu を:o",
            "Things and buildings use あります."),
        s("This apple is delicious.", "この:kono りんご:ringo は:wa おいしい:oishii です:desu", "これ:kore な:na",
            "この + noun."),
        s("The room is quiet.", "へや:heya は:wa しずか:shizuka です:desu", "を:o の:no",
            "な-adjectives drop な at the end of a sentence."),
        s("I like cats.", "わたし:watashi は:wa ねこ:neko が:ga すき:suki です:desu", "を:o い:i",
            "The liked thing takes が, not を."),
        s("What is this?", "これ:kore は:wa なん:nan です:desu か:ka", "が:ga の:no",
            "なん = what."),
        s("Where is the toilet?", "トイレ:toire は:wa どこ:doko です:desu か:ka", "なに:nani を:o",
            "どこ = where."),
        s("I don't eat meat.", "わたし:watashi は:wa にく:niku を:o たべません:tabemasen", "たべます:tabemasu が:ga",
            "ません = negative."),
        s("I watched a movie yesterday.", "きのう:kinou えいが:eiga を:o みました:mimashita", "みます:mimasu に:ni",
            "ました = past.", "えいが を きのう みました"),
        s("I want to drink water.", "みず:mizu が:ga のみたい:nomitai です:desu", "のみます:nomimasu に:ni",
            "stem + たいです = want to. を is also fine.", "みず を のみたい です"),
        s("Please speak slowly.", "ゆっくり:yukkuri はなして:hanashite ください:kudasai", "はなします:hanashimasu を:o",
            "て-form + ください = please do."),
        s("My name is Tanaka.", "わたし:watashi の:no なまえ:namae は:wa たなか:tanaka です:desu", "が:ga を:o",
            "わたしの なまえ = my name."),
        s("Japanese is difficult.", "にほんご:nihongo は:wa むずかしい:muzukashii です:desu", "な:na を:o",
            "い-adjective + です."),
        s("That is not expensive.", "それ:sore は:wa たかくない:takakunai です:desu", "たかい:takai な:na",
            "い → くない for the negative."),
        s("I study Japanese every day.", "まいにち:mainichi にほんご:nihongo を:o べんきょうします:benkyou_shimasu", "が:ga に:ni",
            "The subject you study takes を.", "にほんご を まいにち べんきょうします"),
        s("The teacher is also Japanese.", "せんせい:sensei も:mo にほんじん:nihonjin です:desu", "は:wa の:no",
            "も replaces は."),
        s("I wake up at 7 o'clock.", "しちじ:shichiji に:ni おきます:okimasu", "で:de を:o",
            "Clock times take に."),
        s("I buy vegetables at the shop.", "みせ:mise で:de やさい:yasai を:o かいます:kaimasu", "に:ni が:ga",
            "Shop = place of action → で.", "やさい を みせ で かいます"),
        s("Do you understand?", "わかります:wakarimasu か:ka", "わかりません:wakarimasen を:o",
            "Add か to ask."),
        s("Let's go together.", "いっしょ:issho に:ni いきましょう:ikimashou", "いきます:ikimasu を:o",
            "ましょう = let's."),
        s("I came from India.", "わたし:watashi は:wa インド:indo から:kara きました:kimashita", "に:ni で:de",
            "から = from."),
        s("The dog is big.", "いぬ:inu は:wa おおきい:ookii です:desu", "な:na を:o",
            "い-adjectives need no な."),
    )
}
