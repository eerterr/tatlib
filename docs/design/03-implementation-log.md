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

## Шаг 4.2 — компоненты (субагент Opus 5, отчёт перенесён)

- `ui/components/`: `Buttons.kt`, `Chips.kt`, `Books.kt`, `TopBar.kt`, `Navigation.kt`, `Reader.kt`, `Progress.kt`, `Surfaces.kt`, `Internal.kt`, `ComponentsPreview.kt`; старые `Buttons/BookCard/BookCover/Misc/SimpleLineChart/TopBar/HeroBackground.kt` удалены. Старые экраны до шага 4.3 не собираются — переписываются целиком.
- Иконки проверены по sources-jar `material-icons-core/extended 1.6.8` (dl.google.com): `AutoMirrored.Rounded.MenuBook`, `Rounded.CenterFocusWeak`, `BarChart`, `ChevronRight`, `CameraAlt`, `VisibilityOff` — есть. `SwitchDefaults.colors(checkedBorderColor)`, `SliderDefaults.colors(activeTickColor)` — есть в material3 1.2.1.
- Оверлайны (`.ov`): прописные делают компоненты через `.uppercase()`, в ресурсах текст как в `gen2.py` (канон принят на 4.2).
- `--ink`/`--bg` из CSS → `onBackground`/`background`; `.seg.on` в тёмной → `primary`/`onPrimary`.
- Мини-бар в тёмной теме: CSS даёт `color: var(--bg)` на `sand-deep` (нечитаемо) → текст `onSurface`.
- `ReadPill`, `ActionButton.OnPhoto`, `WhitePill`, `GlassChip.selected`: текст/узор на белом и sage2 — `colorScheme.scrim` (ink светлой / night тёмной), потому что `--ink` в тёмной = крем и пропал бы на белом.
- `LevelRow`: выбранный круг 600/19 из макета → `titleLarge` (20), отдельного слота нет.
- Тени CSS `0 X Y rgba(...)` → `Modifier.shadow(elevation ≈ blur/3, ambientColor/spotColor)`; цвет тени работает с API 28+, ниже — чёрная системная.
- `CategoryTile`: тень картинки `Color.Black.copy(.28f)`, тень подписи на фото `Color.Black.copy(.35f)` — как в CSS (`rgba(0,0,0,…)`).
- `FontSizeSlider`: «Аа» — литерал в коде (образец шрифта, одинаков в обоих языках).
- `FilterChip`: визуал 36 dp внутри тач-цели 48 dp, pressed-состояние не реализовано.
- `LayerSwitch`: анимируется только ползунок (220 мс, FastOutSlowIn), цвет подписи меняется мгновенно; `adaptedEnabled=false` для сканера (38 % альфа, без клика).
- `GlassCard`/`GlassField`: без `Modifier.blur` (API 31+), заливка 34 % + рамка 55 % — по решению § 2.1 промта.
- `BookCover`: id «1/2/3» → фото из drawable, любой другой id → типографическая обложка на `hero_sunset` (масштаб кегля от ширины через `LocalDensity`, не sp).
- `TextLink`/`LanguageToggle`: подчёркивание рисуется `drawBehind` по нижней кромке текстового бокса (цвет `sage`/terracotta отличается от текста, `TextDecoration.Underline` не подходит).
