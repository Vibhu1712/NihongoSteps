package com.nihongosteps.app.data

data class Word(
    val jp: String,       // how it is usually written (may contain kanji)
    val kana: String,     // reading in kana
    val romaji: String,
    val en: String,
    val category: String,
    val note: String = "",
) {
    val hasKanji: Boolean get() = jp != kana
}

object VocabData {

    private fun cat(name: String, vararg rows: String): List<Word> = rows.map { row ->
        val p = row.split("|")
        Word(jp = p[0], kana = p[1].ifEmpty { p[0] }, romaji = p[2], en = p[3], category = name, note = p.getOrElse(4) { "" })
    }

    val words: List<Word> = buildList {
        addAll(cat("Greetings",
            "おはようございます||ohayou gozaimasu|Good morning",
            "こんにちは||konnichiwa|Hello / Good afternoon",
            "こんばんは||konbanwa|Good evening",
            "さようなら||sayounara|Goodbye",
            "おやすみなさい||oyasuminasai|Good night",
            "ありがとうございます||arigatou gozaimasu|Thank you",
            "すみません||sumimasen|Excuse me / Sorry",
            "はじめまして||hajimemashite|Nice to meet you",
            "よろしくおねがいします||yoroshiku onegaishimasu|Pleased to meet you|Said after introducing yourself",
            "おねがいします||onegaishimasu|Please (when requesting)",
            "はい||hai|Yes",
            "いいえ||iie|No",
            "いただきます||itadakimasu|Let's eat|Said before a meal",
            "ごちそうさまでした||gochisousama deshita|Thanks for the meal|Said after a meal",
        ))
        addAll(cat("Numbers",
            "一|いち|ichi|one", "二|に|ni|two", "三|さん|san|three",
            "四|よん|yon|four|Also read し (shi)", "五|ご|go|five", "六|ろく|roku|six",
            "七|なな|nana|seven|Also read しち (shichi)", "八|はち|hachi|eight",
            "九|きゅう|kyuu|nine|Also read く (ku)", "十|じゅう|juu|ten",
            "百|ひゃく|hyaku|hundred", "千|せん|sen|thousand",
        ))
        addAll(cat("People",
            "私|わたし|watashi|I, me", "あなた||anata|you|Often avoided — use the person's name + さん",
            "人|ひと|hito|person", "友達|ともだち|tomodachi|friend",
            "先生|せんせい|sensei|teacher", "学生|がくせい|gakusei|student",
            "家族|かぞく|kazoku|family", "母|はは|haha|(my) mother",
            "父|ちち|chichi|(my) father", "子供|こども|kodomo|child",
            "名前|なまえ|namae|name", "日本人|にほんじん|nihonjin|Japanese person",
        ))
        addAll(cat("Food & drink",
            "水|みず|mizu|water", "お茶|おちゃ|ocha|green tea",
            "ご飯|ごはん|gohan|cooked rice / meal", "パン||pan|bread",
            "魚|さかな|sakana|fish", "肉|にく|niku|meat",
            "野菜|やさい|yasai|vegetables", "果物|くだもの|kudamono|fruit",
            "りんご||ringo|apple", "コーヒー||koohii|coffee",
            "牛乳|ぎゅうにゅう|gyuunyuu|milk", "卵|たまご|tamago|egg",
        ))
        addAll(cat("Places",
            "家|いえ|ie|house, home", "学校|がっこう|gakkou|school",
            "駅|えき|eki|station", "会社|かいしゃ|kaisha|company",
            "店|みせ|mise|shop", "病院|びょういん|byouin|hospital",
            "図書館|としょかん|toshokan|library", "部屋|へや|heya|room",
            "トイレ||toire|toilet", "日本|にほん|nihon|Japan", "インド||indo|India",
        ))
        addAll(cat("Time",
            "今日|きょう|kyou|today", "明日|あした|ashita|tomorrow",
            "昨日|きのう|kinou|yesterday", "今|いま|ima|now",
            "朝|あさ|asa|morning", "夜|よる|yoru|night",
            "毎日|まいにち|mainichi|every day", "週末|しゅうまつ|shuumatsu|weekend",
            "月曜日|げつようび|getsuyoubi|Monday", "〜時|〜じ|~ji|~ o'clock",
        ))
        addAll(cat("Verbs",
            "食べます|たべます|tabemasu|eat", "飲みます|のみます|nomimasu|drink",
            "行きます|いきます|ikimasu|go", "来ます|きます|kimasu|come",
            "帰ります|かえります|kaerimasu|go home, return", "見ます|みます|mimasu|see, watch",
            "読みます|よみます|yomimasu|read", "書きます|かきます|kakimasu|write",
            "聞きます|ききます|kikimasu|listen, ask", "話します|はなします|hanashimasu|speak",
            "買います|かいます|kaimasu|buy", "勉強します|べんきょうします|benkyou shimasu|study",
            "寝ます|ねます|nemasu|sleep", "起きます|おきます|okimasu|wake up",
            "分かります|わかります|wakarimasu|understand",
            "あります||arimasu|there is (things)", "います||imasu|there is (people, animals)",
        ))
        addAll(cat("Adjectives",
            "大きい|おおきい|ookii|big", "小さい|ちいさい|chiisai|small",
            "新しい|あたらしい|atarashii|new", "古い|ふるい|furui|old (things)",
            "高い|たかい|takai|expensive, tall", "安い|やすい|yasui|cheap",
            "暑い|あつい|atsui|hot (weather)", "寒い|さむい|samui|cold (weather)",
            "おいしい||oishii|delicious", "楽しい|たのしい|tanoshii|fun",
            "難しい|むずかしい|muzukashii|difficult", "易しい|やさしい|yasashii|easy",
            "元気|げんき|genki|well, energetic|な-adjective", "静か|しずか|shizuka|quiet|な-adjective",
            "好き|すき|suki|liked|な-adjective: X が好きです = I like X",
            "きれい||kirei|pretty, clean|な-adjective (ends in い but is not an い-adjective!)",
            "有名|ゆうめい|yuumei|famous|な-adjective",
        ))
        addAll(cat("Colours",
            "赤|あか|aka|red", "青|あお|ao|blue", "白|しろ|shiro|white",
            "黒|くろ|kuro|black", "黄色|きいろ|kiiro|yellow", "緑|みどり|midori|green",
        ))
        addAll(cat("Things",
            "本|ほん|hon|book", "車|くるま|kuruma|car", "電話|でんわ|denwa|telephone",
            "傘|かさ|kasa|umbrella", "時計|とけい|tokei|watch, clock", "鞄|かばん|kaban|bag",
            "猫|ねこ|neko|cat", "犬|いぬ|inu|dog", "日本語|にほんご|nihongo|Japanese language",
            "英語|えいご|eigo|English language", "映画|えいが|eiga|movie",
        ))
    }

    val categories: List<String> = words.map { it.category }.distinct()
}
