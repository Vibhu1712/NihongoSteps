package com.nihongosteps.app.data

data class Example(val jp: String, val romaji: String, val en: String)

data class Check(
    val question: String,
    val options: List<String>,
    val answer: Int,
    val why: String,
)

data class GrammarLesson(
    val id: String,
    val title: String,
    val jpTitle: String,
    val unit: String,
    val pattern: String,
    val explanation: List<String>,
    val examples: List<Example>,
    val tip: String,
    val checks: List<Check>,
)

object GrammarData {

    val lessons: List<GrammarLesson> = listOf(
        GrammarLesson(
            id = "wa-desu", title = "X is Y", jpTitle = "〜は〜です", unit = "Basics",
            pattern = "[topic] は [noun] です",
            explanation = listOf(
                "は marks the topic of the sentence — what you are talking about. です at the end works like \"is / am / are\" and makes the sentence polite.",
                "When は is used as a particle it is pronounced \"wa\", not \"ha\".",
                "Japanese does not change です for I / you / he / she / it, and there are no articles like \"a\" or \"the\".",
            ),
            examples = listOf(
                Example("わたしは がくせいです。", "Watashi wa gakusei desu.", "I am a student."),
                Example("たなかさんは せんせいです。", "Tanaka-san wa sensei desu.", "Mr/Ms Tanaka is a teacher."),
                Example("これは ほんです。", "Kore wa hon desu.", "This is a book."),
            ),
            tip = "When the topic is obvious, drop it: がくせいです。 alone means \"(I) am a student.\" Never add さん to your own name.",
            checks = listOf(
                Check("How is the particle は read in わたしは?", listOf("ha", "wa", "ga"), 1,
                    "As a particle, は is always read \"wa\"."),
                Check("Choose: \"I am Indian.\"", listOf("わたしは インドじんです。", "わたしを インドじんです。", "わたしの インドじんです。"), 0,
                    "The topic (I) takes は. を marks objects of verbs, の shows possession."),
            ),
        ),
        GrammarLesson(
            id = "negative-desu", title = "X is not Y", jpTitle = "〜じゃありません", unit = "Basics",
            pattern = "[topic] は [noun] じゃありません",
            explanation = listOf(
                "To say \"is not\", replace です with じゃありません (everyday polite) or ではありません (a little more formal).",
                "Past tense: です → でした (was), じゃありません → じゃありませんでした (was not).",
            ),
            examples = listOf(
                Example("わたしは せんせいじゃありません。", "Watashi wa sensei ja arimasen.", "I am not a teacher."),
                Example("これは みずではありません。", "Kore wa mizu dewa arimasen.", "This is not water."),
                Example("きのうは にちようびでした。", "Kinou wa nichiyoubi deshita.", "Yesterday was Sunday."),
            ),
            tip = "In casual speech with friends you will also hear じゃない, but stick to じゃありません while learning polite Japanese.",
            checks = listOf(
                Check("Choose: \"That is not a cat.\"", listOf("それは ねこです。", "それは ねこじゃありません。", "それは ねこでした。"), 1,
                    "じゃありません is the negative of です."),
                Check("Past of です is…", listOf("でした", "ました", "です か"), 0,
                    "でした = was. ました is the past ending for verbs."),
            ),
        ),
        GrammarLesson(
            id = "ka", title = "Asking questions", jpTitle = "〜か", unit = "Basics",
            pattern = "[sentence] か。",
            explanation = listOf(
                "Add か to the end of any polite sentence to turn it into a question. The word order does not change.",
                "Answer with はい (yes) or いいえ (no). Question words: なん/なに (what), だれ (who), どこ (where), いつ (when).",
            ),
            examples = listOf(
                Example("がくせいですか。", "Gakusei desu ka.", "Are you a student?"),
                Example("はい、がくせいです。", "Hai, gakusei desu.", "Yes, I am a student."),
                Example("これは なんですか。", "Kore wa nan desu ka.", "What is this?"),
                Example("トイレは どこですか。", "Toire wa doko desu ka.", "Where is the toilet?"),
            ),
            tip = "In writing, Japanese normally ends a question with 。— the か already makes it a question.",
            checks = listOf(
                Check("Make it a question: あなたは せんせいです。", listOf("あなたは せんせいですか。", "あなたか せんせいです。", "かあなたは せんせいです。"), 0,
                    "か goes at the very end."),
                Check("Which word means \"where\"?", listOf("だれ", "どこ", "なん"), 1, "どこ = where, だれ = who, なん = what."),
            ),
        ),
        GrammarLesson(
            id = "no", title = "Possession: my, your, of", jpTitle = "AのB", unit = "Particles",
            pattern = "[A] の [B]  =  A's B",
            explanation = listOf(
                "の links two nouns. The first noun describes or owns the second: わたしの ほん = my book.",
                "It also works for \"of / about\": にほんごの ほん = a Japanese-language book.",
            ),
            examples = listOf(
                Example("わたしの かばんです。", "Watashi no kaban desu.", "It is my bag."),
                Example("せんせいの くるまは あかいです。", "Sensei no kuruma wa akai desu.", "The teacher's car is red."),
                Example("にほんの くるまです。", "Nihon no kuruma desu.", "It is a Japanese car."),
            ),
            tip = "Order matters: the owner comes first. ともだちの いぬ = my friend's dog; いぬの ともだち = the dog's friend!",
            checks = listOf(
                Check("Choose: \"my friend\"", listOf("ともだちの わたし", "わたしの ともだち", "わたしは ともだち"), 1,
                    "Owner (わたし) + の + thing (ともだち)."),
            ),
        ),
        GrammarLesson(
            id = "kore-sore", title = "This, that, that over there", jpTitle = "これ・それ・あれ", unit = "Basics",
            pattern = "これ / それ / あれ は 〜です  ·  この / その / あの + noun",
            explanation = listOf(
                "これ = this (near me), それ = that (near you), あれ = that (far from both of us), どれ = which one.",
                "これ/それ/あれ stand alone. When a noun follows, use この/その/あの instead: この ほん = this book.",
            ),
            examples = listOf(
                Example("それは わたしの かばんです。", "Sore wa watashi no kaban desu.", "That is my bag."),
                Example("この ほんは あたらしいです。", "Kono hon wa atarashii desu.", "This book is new."),
                Example("あの ひとは だれですか。", "Ano hito wa dare desu ka.", "Who is that person over there?"),
            ),
            tip = "× これ ほん  ✓ この ほん.  × この は ほんです  ✓ これは ほんです.",
            checks = listOf(
                Check("Choose: \"This book is new.\"", listOf("これ ほんは あたらしいです。", "この ほんは あたらしいです。", "この は ほん あたらしいです。"), 1,
                    "Before a noun use この."),
                Check("A thing far from both speaker and listener is…", listOf("それ", "これ", "あれ"), 2, "あれ = that over there."),
            ),
        ),
        GrammarLesson(
            id = "wo", title = "Objects of verbs", jpTitle = "〜を", unit = "Particles",
            pattern = "[object] を [verb]",
            explanation = listOf(
                "を marks the thing that receives the action: what you eat, drink, read, buy…",
                "を is pronounced \"o\". It is used almost only as this particle.",
                "Japanese verbs always come at the end of the sentence.",
            ),
            examples = listOf(
                Example("パンを たべます。", "Pan o tabemasu.", "I eat bread."),
                Example("コーヒーを のみます。", "Koohii o nomimasu.", "I drink coffee."),
                Example("にほんごを べんきょうします。", "Nihongo o benkyou shimasu.", "I study Japanese."),
            ),
            tip = "Word order: subject → object → verb. English \"I read a book\" becomes わたしは ほんを よみます (I book read).",
            checks = listOf(
                Check("Choose: \"I read a book.\"", listOf("ほんに よみます。", "ほんを よみます。", "よみます ほんを。"), 1,
                    "The book is the object → を, and the verb goes last."),
            ),
        ),
        GrammarLesson(
            id = "masu", title = "Verb tenses (polite)", jpTitle = "ます・ません・ました", unit = "Verbs",
            pattern = "〜ます / 〜ません / 〜ました / 〜ませんでした",
            explanation = listOf(
                "Polite verbs end in ます. Change the ending to change the meaning:",
                "たべます = eat / will eat\nたべません = don't eat / won't eat\nたべました = ate\nたべませんでした = didn't eat",
                "There is no separate future tense — ます covers habits and the future. Words like あした (tomorrow) make the time clear.",
            ),
            examples = listOf(
                Example("あした がっこうに いきます。", "Ashita gakkou ni ikimasu.", "I will go to school tomorrow."),
                Example("にくを たべません。", "Niku o tabemasen.", "I don't eat meat."),
                Example("きのう えいがを みました。", "Kinou eiga o mimashita.", "I watched a movie yesterday."),
            ),
            tip = "Invitations: 〜ませんか = won't you…? (いきませんか) and 〜ましょう = let's… (いきましょう).",
            checks = listOf(
                Check("\"I didn't drink.\"", listOf("のみません", "のみました", "のみませんでした"), 2,
                    "ませんでした = past negative."),
                Check("\"I will watch it tomorrow.\" — あした…", listOf("みます", "みました", "みましょう"), 0,
                    "ます form is also the future."),
            ),
        ),
        GrammarLesson(
            id = "ni-e", title = "Going somewhere, at a time", jpTitle = "〜に・〜へ", unit = "Particles",
            pattern = "[place] に/へ いきます  ·  [clock time] に",
            explanation = listOf(
                "With movement verbs (いきます, きます, かえります), に or へ marks the destination. へ is read \"e\".",
                "に also marks a specific time: しちじに (at 7 o'clock), げつようびに (on Monday).",
                "Relative time words — きょう, あした, きのう, まいにち, いま — take no particle.",
            ),
            examples = listOf(
                Example("にほんに いきます。", "Nihon ni ikimasu.", "I will go to Japan."),
                Example("うちへ かえります。", "Uchi e kaerimasu.", "I'm going home."),
                Example("ろくじに おきます。", "Rokuji ni okimasu.", "I wake up at 6."),
            ),
            tip = "× あしたに いきます  ✓ あした いきます.",
            checks = listOf(
                Check("\"I go to the station.\"", listOf("えきを いきます。", "えきに いきます。", "えきで いきます。"), 1,
                    "Destination → に (or へ)."),
                Check("Which needs に?", listOf("あした", "まいにち", "しちじ"), 2, "Clock times take に; relative times do not."),
            ),
        ),
        GrammarLesson(
            id = "de", title = "Where an action happens", jpTitle = "〜で", unit = "Particles",
            pattern = "[place] で [action verb]  ·  [tool/transport] で",
            explanation = listOf(
                "で marks the place where an action happens: I read AT the library, I buy AT the shop.",
                "で also marks the means: バスで (by bus), はしで (with chopsticks), にほんごで (in Japanese).",
            ),
            examples = listOf(
                Example("としょかんで ほんを よみます。", "Toshokan de hon o yomimasu.", "I read books at the library."),
                Example("みせで やさいを かいます。", "Mise de yasai o kaimasu.", "I buy vegetables at the shop."),
                Example("バスで がっこうに いきます。", "Basu de gakkou ni ikimasu.", "I go to school by bus."),
            ),
            tip = "に = where something is or goes. で = where something is done.",
            checks = listOf(
                Check("\"I eat at home.\" — うち___ たべます", listOf("に", "で", "を"), 1, "Eating is an action → で."),
            ),
        ),
        GrammarLesson(
            id = "i-adj", title = "い-adjectives", jpTitle = "い形容詞", unit = "Adjectives",
            pattern = "たかい ほん  ·  ほんは たかいです",
            explanation = listOf(
                "い-adjectives end in い and go straight before a noun: たかい ほん (an expensive book).",
                "Negative: drop い, add くない — たかい → たかくないです.\nPast: drop い, add かった — たかい → たかかったです.",
                "Exception: いい (good) → よくないです, よかったです.",
            ),
            examples = listOf(
                Example("この りんごは おいしいです。", "Kono ringo wa oishii desu.", "This apple is delicious."),
                Example("にほんごは むずかしくないです。", "Nihongo wa muzukashikunai desu.", "Japanese isn't difficult."),
                Example("きのうは さむかったです。", "Kinou wa samukatta desu.", "Yesterday was cold."),
            ),
            tip = "Don't use でした with い-adjectives: × さむいでした  ✓ さむかったです.",
            checks = listOf(
                Check("Negative of おおきい", listOf("おおきいじゃありません", "おおきくないです", "おおきいません"), 1,
                    "い → くない."),
                Check("\"It was fun.\"", listOf("たのしいでした", "たのしかったです", "たのしくないです"), 1, "い → かった + です."),
            ),
        ),
        GrammarLesson(
            id = "na-adj", title = "な-adjectives", jpTitle = "な形容詞", unit = "Adjectives",
            pattern = "しずかな へや  ·  へやは しずかです",
            explanation = listOf(
                "な-adjectives take な before a noun: しずかな へや (a quiet room).",
                "At the end of a sentence they behave like nouns: しずかです, しずかじゃありません, しずかでした.",
            ),
            examples = listOf(
                Example("しずかな へやです。", "Shizuka na heya desu.", "It's a quiet room."),
                Example("きょうとは ゆうめいです。", "Kyouto wa yuumei desu.", "Kyoto is famous."),
                Example("せんせいは げんきでした。", "Sensei wa genki deshita.", "The teacher was well."),
            ),
            tip = "きれい (pretty) and ゆうめい (famous) end in い but are な-adjectives: ✓ きれいじゃありません  × きれくない.",
            checks = listOf(
                Check("\"a pretty flower\" (はな = flower)", listOf("きれい はな", "きれいな はな", "きれいの はな"), 1,
                    "な-adjective + な + noun."),
            ),
        ),
        GrammarLesson(
            id = "ga-suki", title = "Likes and dislikes", jpTitle = "〜が好きです", unit = "Particles",
            pattern = "[person] は [thing] が すきです",
            explanation = listOf(
                "すき (liked) is a な-adjective, so the thing you like is marked with が, not を.",
                "Opposite: きらい (disliked). Ask: なにが すきですか (What do you like?).",
            ),
            examples = listOf(
                Example("わたしは ねこが すきです。", "Watashi wa neko ga suki desu.", "I like cats."),
                Example("にほんの ごはんが すきです。", "Nihon no gohan ga suki desu.", "I like Japanese food."),
                Example("なにが すきですか。", "Nani ga suki desu ka.", "What do you like?"),
            ),
            tip = "× ねこを すきです  ✓ ねこが すきです.",
            checks = listOf(
                Check("\"I like coffee.\"", listOf("コーヒーを すきです。", "コーヒーが すきです。", "コーヒーに すきです。"), 1,
                    "The liked thing takes が."),
            ),
        ),
        GrammarLesson(
            id = "aru-iru", title = "There is / there are", jpTitle = "あります・います", unit = "Verbs",
            pattern = "[place] に [thing] が あります / います",
            explanation = listOf(
                "あります is for things that don't move by themselves (objects, buildings, plants). います is for people and animals.",
                "The place takes に and the thing takes が.",
            ),
            examples = listOf(
                Example("つくえの うえに ほんが あります。", "Tsukue no ue ni hon ga arimasu.", "There is a book on the desk."),
                Example("こうえんに いぬが います。", "Kouen ni inu ga imasu.", "There is a dog in the park."),
                Example("えきは あそこに あります。", "Eki wa asoko ni arimasu.", "The station is over there."),
            ),
            tip = "あります also means \"to have\": じかんが ありません = I don't have time.",
            checks = listOf(
                Check("\"There is a cat.\"", listOf("ねこが あります。", "ねこが います。", "ねこを います。"), 1, "Animals use います."),
                Check("\"There is a bank.\" (ぎんこう)", listOf("ぎんこうが あります。", "ぎんこうが います。", "ぎんこうに います。"), 0, "Buildings use あります."),
            ),
        ),
        GrammarLesson(
            id = "mo", title = "Also, too", jpTitle = "〜も", unit = "Particles",
            pattern = "[noun] も …",
            explanation = listOf(
                "も means \"also / too\". It replaces は, が and を.",
                "With other particles it is added after them: にほんにも いきます (I'll go to Japan too).",
            ),
            examples = listOf(
                Example("わたしも がくせいです。", "Watashi mo gakusei desu.", "I'm a student too."),
                Example("ねこも いぬも すきです。", "Neko mo inu mo suki desu.", "I like both cats and dogs."),
                Example("コーヒーも のみます。", "Koohii mo nomimasu.", "I drink coffee too."),
            ),
            tip = "× わたしはも  ✓ わたしも.",
            checks = listOf(
                Check("\"The teacher is also Japanese.\"", listOf("せんせいはも にほんじんです。", "せんせいも にほんじんです。", "せんせいの にほんじんもです。"), 1,
                    "も replaces は."),
            ),
        ),
        GrammarLesson(
            id = "tai", title = "I want to…", jpTitle = "〜たいです", unit = "Verbs",
            pattern = "[verb stem] たいです",
            explanation = listOf(
                "Take the ます form, remove ます, add たいです: たべます → たべたいです (I want to eat).",
                "Negative: たくないです. Past: たかったです. The object can take が or を.",
            ),
            examples = listOf(
                Example("すしが たべたいです。", "Sushi ga tabetai desu.", "I want to eat sushi."),
                Example("にほんに いきたいです。", "Nihon ni ikitai desu.", "I want to go to Japan."),
                Example("なにが のみたいですか。", "Nani ga nomitai desu ka.", "What do you want to drink?"),
            ),
            tip = "Use たい for your own wishes or to ask \"you\". Saying what someone else wants uses a different form.",
            checks = listOf(
                Check("\"I want to sleep.\" (ねます)", listOf("ねたいです", "ねますたい", "ねるたいです"), 0, "Stem ね + たい."),
            ),
        ),
        GrammarLesson(
            id = "te-kudasai", title = "Please do…", jpTitle = "〜てください", unit = "Verbs",
            pattern = "[verb て-form] ください",
            explanation = listOf(
                "To ask someone to do something politely, use the て-form + ください.",
                "Making the て-form from the ます form:\n• たべます, みます, ねます → たべて, みて, ねて (just drop ます)\n• かきます → かいて · ききます → きいて\n• のみます, よみます → のんで, よんで\n• かいます, まちます, かえります → かって, まって, かえって\n• はなします → はなして\n• いきます → いって (exception)\n• します → して · きます → きて",
            ),
            examples = listOf(
                Example("ゆっくり はなしてください。", "Yukkuri hanashite kudasai.", "Please speak slowly."),
                Example("ここに なまえを かいてください。", "Koko ni namae o kaite kudasai.", "Please write your name here."),
                Example("ちょっと まってください。", "Chotto matte kudasai.", "Please wait a moment."),
            ),
            tip = "もう いちど いってください = Please say it once more. Very useful for learners!",
            checks = listOf(
                Check("て-form of のみます", listOf("のみて", "のんで", "のって"), 1, "み → んで."),
                Check("て-form of いきます", listOf("いいて", "いきて", "いって"), 2, "いきます is the exception: いって."),
            ),
        ),
    )

    fun byId(id: String) = lessons.firstOrNull { it.id == id }
}
