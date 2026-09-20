# TatLib — журнал решений фазы 4 (перенос в Compose)

Дата старта: 20.09.2026 · ветка `silvers-rayleigh7` · модель Opus 5. Одна строка — одно решение по коду.
Источники: `PROMPT-PHASE-4.md`, `MASTER.md` (§ 0 v2/v2.1 перекрывает остальное), `mockups/*.dc.html`, `tatlib.css`, `gen2.py`.

## Окружение

- 20.09 · На машине нет JDK (`java -version` → «Unable to locate a Java Runtime») и Android SDK (`~/Library/Android/sdk` нет, `ANDROID_HOME` пуст) → сборка в фазе 4 не проверялась, см. `04-verification.md`.
- 20.09 · Сигнатуры API проверены по исходникам точных версий из BOM 2024.06.00 (POM с dl.google.com: material3 1.2.1, ui 1.6.8): `lightColorScheme(surfaceContainer*)`, `addPathNodes(String?)`, `ImageVector.Builder.addPath(...)`, `PathParser.parsePathString/toNodes`.

## Шаг 4.1 — тема

- `Color.kt`: токены светлой/тёмной темы из MASTER § 2 + `tatlib.css` (`.tl` / `.tl.dark`); `TatlibColors` + `LocalTatlibColors` для ролей вне M3 (terracotta, sky2, peach2, sage2, шкала уровней контейнер/текст/точка, стекло, тень).
- `Theme.kt`: две `ColorScheme` (тёмная — отдельная схема по § 2.2, не сдвиг светлой); `MaterialTheme.tatlibColors` и `MaterialTheme.readerTypography` как точки доступа; `surfaceContainer*` заданы явно, чтобы M3-компоненты не тянули дефолтный лиловый tonal-оттенок.
- `Type.kt`: заголовки экранов — Golos Text 600 (`h0` из § 0 v2), не Playfair из § 3.1; Playfair только в `displayMedium`/`displaySmall` (курсив: «B1» в кольце, «Татарча күбрәк», фраза дня) и `headlineSmall` (цифры статистики, слово во всплывашке).
- `Type.kt`: у макета шесть размеров `h0` (26–40 sp); в Compose три слота — `displayLarge` 40/44 (приветствие), `headlineLarge` 36/40, `headlineMedium` 30/36 (Quiz, BookDetail, Search, Progress, Scanner, Profile). Отступление от макета ради правила «без sp в экранах».
- `Type.kt`: у Playfair нет начертания 500 (MASTER § 3.1) — в наличии 400/400i/600; взято 600.
- `Type.kt`: `ReaderTypography` — Literata (или Golos по выбору) 16–20 sp, интерлиньяж `1.55.em`, чтобы пропорция сохранялась при смене кегля; `LocalReaderTypography` пересобирается в `TatlibTheme` из `AppPreferences`.
- `Preferences.kt`: `AppPreferences` (тема Система/Якты/Караңгы, шрифт и кегль ридера, подчёркивание слов) — `mutableStateOf` в памяти без persistence, потому что `AppViewModel.kt` не трогаем и слоя настроек в проекте нет.
- `Shape.kt`: радиусы по MASTER § 8 (12/14/20/24/28), `CoverShape` r16 для лент v2, `ArchShape` — путь `arch()` из `gen2.py` 1:1 как `GenericShape`.
- `Ornaments.kt`: пять `ImageVector` из `ORN` в `gen2.py` через `addPathNodes` (path data без изменений, viewport 24). Мотив 2: внутреннее сердце в SVG залито цветом бумаги — заменено на вырез `PathFillType.EvenOdd`, чтобы узор оставался одноцветным под `tint`.
- Шрифты: `docs/design/fonts/*.ttf` → `res/font/` с именами lowercase+underscore; fontTools подтвердил ә ө ү җ ң һ во всех 9 файлах после копирования. Лицензии OFL — `assets/licenses/` (в `res/font/` .txt не допускается).
- Фото: `mockups/assets/*.jpg` → `res/drawable-nodpi/` (дефисы → подчёркивания: `cover_su_anasy`, `hero_blossom` …), без масштабирования по плотности.
- `themes.xml` + `values-night/`: фон окна до первого кадра Compose — cream `#F6F1E7` / night `#14201B` вместо белого; статус- и навигационная панели прозрачные.
- `strings.xml`: `values/` — татарский по умолчанию, `values-ru/` — русский; строки экранов — в `strings_<группа>.xml`, чтобы четыре параллельных агента не правили один файл.
- `data/BookMeta.kt`: константная таблица `bookId → level / year / word_count / unique_words / blocks`, скопированная из базы (`text_complexity`, `books`, `book_stats`) с `TODO backend`; клиентский подсчёт слов даёт 930 и 1501 вместо 929 и 1500 у книг 2 и 4, поэтому только копия. `Book.displayLevel`, `Book.displayYear`.
- `Misc.kt`: `colorForLevel` временно переведён на `LightTatlibColors.level(..).dot`, чтобы старые экраны компилировались до шага 4.2.
- Мок книг (`MockData.suAnasy/shurale/najip`, `allBooks`, `bookById`) убирается в 4.3 группой D вместе с `RecapScreen` (→ `viewModel.getBook(id)`); `levelQuiz` и `levelFromScore` остаются.
