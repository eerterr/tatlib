package com.tatlib.app.data

/**
 * Everything below is static mock data for the clickable prototype.
 * No network, no database — this is exactly what a future BookRepository /
 * UserRepository implementation should replace.
 */
object MockData {

    // -------------------------------------------------------------------
    // Books. Titles, authors and the "Шүрәле" excerpt are real (the poem is
    // public domain classic Tatar literature, taken from the product brief).
    // The other two books' page bodies are explicitly-labelled placeholder
    // text — swap them for the real digitised text when it's ready.
    // -------------------------------------------------------------------

    val suAnasy = Book(
        id = "su-anasy",
        title = "Су анасы",
        author = "Габдулла Тукай",
        genre = "Әкият",
        year = "1908",
        level = TatarLevel.B1,
        tags = listOf("Фольклор", "Әкият"),
        description = "Габдулла Тукайның халык авыз иҗатына нигезләнгән билгеле балладасы. " +
            "Су буенда очраган серле Су анасы турында хикәя — татар халкының табигать " +
            "белән бәйле ышануларын чагылдыра.",
        readingTimeLabel = "~3 сәг.",
        progress = 0f,
        pages = listOf(
            BookPage(
                bodyText = "[Прототип өчен урын тоткыч текст] Бу — «Су анасы» китабының беренче бите. " +
                    "Чын әсәр тексты монда девелопер тарафыннан кертеләчәк.",
                glossary = listOf(GlossWord("мирас", "наследие")),
                highlightPhrase = "Табигать белән бәйле бер серле хикәя"
            ),
            BookPage(
                bodyText = "[Прототип өчен урын тоткыч текст] Икенче бит. Су буенда — ай яктысында " +
                    "берәү йөзә, диләр. Ул — Су анасы.",
                glossary = listOf(GlossWord("гыйбрәт", "поучительный вывод"))
            )
        )
    )

    val shurale = Book(
        id = "shurale",
        title = "Шүрәле",
        author = "Габдулла Тукай",
        genre = "Поэма-әкият",
        year = "1907",
        level = TatarLevel.A2,
        tags = listOf("Фольклор", "Поэма"),
        description = "Кырлай авылы янәшәсендәге урманда яшәүче Шүрәле белән йөзе беркадәр " +
            "батыр йортка бармак кыстыргычлары аша бәйле сюжеты — татар балалар әдәбиятының " +
            "иң билгеле әсәрләреннән берсе.",
        readingTimeLabel = "~2 сәг.",
        progress = 0.22f,
        pages = listOf(
            BookPage(
                bodyText = "Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр. Җырлаганда көй " +
                    "өчен, «тавыклары җырлай», диләр. Гәрчә анда тумаган булсам да, мин бераз " +
                    "торган идем.",
                glossary = listOf(GlossWord("нәкъ", "точно, как раз"))
            ),
            BookPage(
                bodyText = "Җирне әз-мәз тырмалап, чәчкән идем, урган идем. Ул авылның, һич " +
                    "онытмыйм, һәрьягы урман иде.",
                glossary = listOf(GlossWord("һәрьягы", "со всех сторон, вокруг")),
                highlightPhrase = "Ул авылның һәрьягы урман иде."
            ),
            BookPage(
                bodyText = "Ул болын, яшел үләннәр хәтфәдән юрган иде. Зурмы, дисәң, зур түгелдер, " +
                    "бу авыл бик кечкенә; халкының эчкән суы бик кечкенә — инеш кенә.",
                glossary = listOf(GlossWord("хәтфә", "бархат"))
            ),
            BookPage(
                bodyText = "Анда бик салкын вә бик эссе түгел, урта һава; җил дә вакытында исеп, " +
                    "һавасын алыштырган.",
                glossary = listOf(GlossWord("алыштыру", "менять, обновлять"))
            )
        )
    )

    val najip = Book(
        id = "najip",
        title = "Нәҗип",
        author = "Фатих Әмирхан",
        genre = "Повесть",
        year = "1913",
        level = TatarLevel.B2,
        tags = listOf("Проза", "Мәдәният"),
        description = "Фатих Әмирханның XX йөз башы татар җәмгыятен, яшьләрнең уй-хисләрен һәм " +
            "рухи эзләнүләрен сурәтләгән повесте.",
        readingTimeLabel = "~4 сәг.",
        progress = 0f,
        pages = listOf(
            BookPage(
                bodyText = "[Прототип өчен урын тоткыч текст] Бу — «Нәҗип» повестенең беренче бите. " +
                    "Чын әсәр тексты монда девелопер тарафыннан кертеләчәк.",
                glossary = listOf(GlossWord("фикер", "мысль")),
                highlightPhrase = "XX йөз башы татар җәмгыяте"
            ),
            BookPage(
                bodyText = "[Прототип өчен урын тоткыч текст] Икенче бит — каһарманның эчке уйлары " +
                    "белән дәвам итә.",
                glossary = listOf(GlossWord("рухи", "духовный"))
            )
        )
    )

    val allBooks = listOf(suAnasy, shurale, najip)

    fun bookById(id: String): Book? = allBooks.firstOrNull { it.id == id }

    // -------------------------------------------------------------------
    // Level-assessment quiz
    // -------------------------------------------------------------------

    data class QuizOption(val text: String, val isCorrect: Boolean)
    data class QuizQuestion(val prompt: String, val options: List<QuizOption>)

    val levelQuiz = listOf(
        QuizQuestion(
            prompt = "Как правильно сказать «Я читаю книгу»?",
            options = listOf(
                QuizOption("Мин китап укыйм", true),
                QuizOption("Мин китап укыдым", false),
                QuizOption("Мин китап укыячакмын", false),
                QuizOption("Мин китап укымыйм", false)
            )
        ),
        QuizQuestion(
            prompt = "Как будет «Я прочитал книгу» (прошедшее время)?",
            options = listOf(
                QuizOption("Мин китап укыйм", false),
                QuizOption("Мин китап укыдым", true),
                QuizOption("Мин китап укыячакмын", false),
                QuizOption("Мин китап укымыйм", false)
            )
        ),
        QuizQuestion(
            prompt = "Что значит «Рәхим итегез!»?",
            options = listOf(
                QuizOption("Добро пожаловать!", true),
                QuizOption("До свидания", false),
                QuizOption("Спасибо", false),
                QuizOption("Извините", false)
            )
        ),
        QuizQuestion(
            prompt = "Что означает слово «дәрәҗә»?",
            options = listOf(
                QuizOption("уровень", true),
                QuizOption("слово", false),
                QuizOption("книга", false),
                QuizOption("время", false)
            )
        ),
        QuizQuestion(
            prompt = "Выберите синоним к слову «алга»",
            options = listOf(
                QuizOption("вперёд", true),
                QuizOption("назад", false),
                QuizOption("быстро", false),
                QuizOption("медленно", false)
            )
        )
    )

    /** Very small mock scoring rule — good enough for a clickable prototype. */
    fun levelFromScore(correctAnswers: Int): TatarLevel = when (correctAnswers) {
        0, 1 -> TatarLevel.A1
        2 -> TatarLevel.A2
        3 -> TatarLevel.B1
        4 -> TatarLevel.B2
        else -> TatarLevel.C1
    }

    // -------------------------------------------------------------------
    // Progress / stats screen mock numbers
    // -------------------------------------------------------------------

    data class ProgressStats(
        val newWords: Int,
        val textsRead: Int,
        val originalTextPercent: Int,
        val currentLevel: TatarLevel,
        val monthLabels: List<String>
    )

    val progressStats = ProgressStats(
        newWords = 184,
        textsRead = 12,
        originalTextPercent = 68,
        currentLevel = TatarLevel.B1,
        monthLabels = listOf("Июль", "Авг", "Сен")
    )

    val inspirationalQuotes = listOf(
        "Һәр укылган бит сине оригиналга якынайта!",
        "Телне белү - дөньяны башкача күрү.",
        "Уку — үсеш юлы."
    )
}
