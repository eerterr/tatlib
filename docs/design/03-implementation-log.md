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

## Шаг 4.3 — экраны

### Группа A — Splash, Welcome, AuthChoice, Register, Login (коммит 4e24894)

- Позиция фото `center 30/40/35 %` → `BiasAlignment(0f, −0.4/−0.2/−0.3)` (у `Alignment` нет фабрики с числами).
- Welcome RU: отдельного экрана нет — тексты `WelcomeRu.dc.html` лежат в `values-ru` и берутся по локали системы; TAT/RU — локальное состояние, визуальный no-op (00-ux-map п. 21), то же на AuthChoice/Register/Login.
- «Трендлар» на Welcome — `onClick = {}`: в макете ссылка ведёт в никуда.
- `OnPhotoLink` имеет внутренний отступ 16 dp → на Welcome `offset(x = 16.dp)`, на Login `offset(x = −16.dp)`, чтобы текст стоял у края как в макете.
- AuthChoice «Теркәлү»: `ActionButton(Secondary)` даёт подпись ink → private `SecondaryOnPhotoAction` (круг sky2 + узор, подпись белая с тенью), без `pressScale` (он `internal` в components).
- AuthChoice градиент `rgba(255,255,255,.1)` → `onPhoto.copy(.1f)`; Register/Login без градиента — `SolidColor(scrim.copy(alpha = 0f))`.
- `ActionButton` без `enabled` → валидация старого кода сохранена как guard в `onClick` + `alpha(0.5f)` при невалидной форме.
- Register: убраны подпись «Сезнең яшь» и старая кнопка (их нет в макете); чипы возраста с en dash из макета, `translatable="false"`; выбор — локальное состояние.
- Login: плейсхолдер «Логин» → «Телефон яки электрон почта» из макета.
- Поля пароля: «глаз» рабочий (Visibility/VisibilityOff), в макете статичный.
- Ошибка «Серсүзләр туры килми» — `bodySmall` `error` под полем повтора (RU «Пароли не совпадают»).
- Register/Login: `imePadding()` + скролл, кнопка остаётся над клавиатурой.

### Группа B — LevelIntro, Quiz, LevelResult, Progress, Profile (коммит bd40d13 → cherry-pick)

- Фон экранов теста `#FBF9F4` → `colorScheme.background` (cream #F6F1E7): отдельного токена нет.
- Декор (дуги, звёзды) и арка вложены в `Box.statusBarsPadding()` вместе с контентом — геометрия макета (без строки статуса) сохраняется относительно шапки.
- Quiz: счётчик «Сорау n / N» наложен на `TopBar` как `labelSmall` uppercase; неактивная «Алга» без выбора — `alpha 0.45`, клик игнорируется (в макете состояние не нарисовано). `DecoA()` — `internal` helper в `LevelIntroScreen.kt`, переиспользован в `LevelResultScreen.kt`.
- Progress: заголовок `headlineMedium` 30/36 вместо 34/40; чипы «Соңгы тәрҗемәләр» — горизонтальный скролл (`FlowRow` экспериментальный) без «· {book}» (в `RecentWordDto` нет книги); «актив көн» — всегда «—» (в API нет); waffle подсвечивает дни текущей недели по `recent_words[].created_at` (ISO UTC, `SimpleDateFormat` + `Calendar`, понедельник = 0); при ошибке API — `EmptyState(«Әлегә мәгълүмат юк», текст исключения)`, карточка уровня и фраза остаются; «Урта» — в ресурсе `%1$s · Урта` под B1 из `UserMeta`; чипы периодов без действия.
- `data/UserMeta.kt`: уровень B1 и возраст 18 скопированы из `users`/`user_level_state` с `TODO backend`.
- Profile: нижняя навигация не рисуется (маршрут не в `bottomBarRoutes`), низ — `navigationBarsPadding()`; экран без прокрутки — на экранах ниже ~700 dp возможен клип; чипы темы/шрифта `FilterChip` 36 dp вместо 32; чип «Literata» не в шрифте Literata; «Укучы» `headlineMedium` 30/36 вместо 26/30; иконка Settings без действия; тень аватара `tatlibColors.shadow` (в тёмной прозрачная).
- RU-строки: «переводы / уникальные слова / активные дни» без склонения по числу; «%1$d лет»; «Алгарышың» → «Твой прогресс»; фраза дня `translatable="false"`.

### Группа C — Library, Search, BookDetail (коммит eb3bb6e → cherry-pick)

- Library: время суток — `HOUR_OF_DAY < 12` иртә, `12–17` көн, `≥ 18` кич (`java.util.Calendar`, без desugaring).
- Library: hero сдвигается вместе с прокруткой (`graphicsLayer { translationY = −scroll }`), иначе тёмные заголовки секций наезжали бы на фото.
- Library: «Трендлар» без действия; ряд чипов прокручивается горизонтально (в макете 4-й чип обрезан); ленты `LazyRow` с `contentPadding` 24 уходят под край экрана.
- Library: состояния загрузки/ошибки/пусто перенесены из старого экрана (`library_loading`, `library_load_failed` + `TextLink("Кабатлап карау")`, `library_empty`) — в макете их нет.
- Search: плитки «Тукай» и «Дәрәҗәм B1» — в теме добавлены токены `tileSteel #3F6F8A` и `tileHoney #8C6A1F` из `tile()` (агент временно брал `sky`/`sunset`, белый текст на `sky` ≈ 1.8:1 — исправлено главной сессией).
- Search: уровень пользователя — константа `TatarLevel.B1` с `TODO backend`; тап по плитке фильтрует список, повторный снимает; пустой результат — `EmptyState` без текста; старые чипы и `inspirationalQuotes` убраны; счётчики — `plurals` (`values/` one/other, `values-ru/` one/few/many/other).
- Search: спиннер только при `isLoading && books.isEmpty()` (флаг общий с `loadBook`).
- Жанры API (`сказка в стихах`, `рассказ`) → `шигъри әкият` / `хикәя` через `internal genreLabel()` в `SearchScreen.kt`, используется Library и BookDetail.
- BookDetail: фон — обложка `graphicsLayer(1.3f)` + `clipToBounds` без blur (API < 31); подпись без «· чыганак: …» (`source_note` в API нет); чип жанра — private `GenreChip` 28 dp; «…» без действия; книга из `viewModel.books` (`collectAsState`), текст из `selectedBook` при совпадении id.
- Строки `meta_pair`/`meta_triple` (`%1$s · %2$s[ · %3$s]`) `translatable="false"`, чтобы « · » не хардкодить.

### Группа D — BookReader, Recap, Scanner (коммит 60dbcca → cherry-pick)

- `MockData.kt`: удалены `suAnasy/shurale/najip/allBooks/bookById/ProgressStats/progressStats/inspirationalQuotes`; остались `QuizOption/QuizQuestion/levelQuiz/levelFromScore`.
- Reader: подпись слоя — арабская «{N} бүлек» вместо римской «I»; счётчик «{N} бүлек · {сүз} сүз» скрыт при `pageCount == 1` (00-ux-map п. 15) — у «Шүрәле» цифра слов внизу не видна.
- Reader: «← Артка / Алга →» — `TextLink` (labelLarge, подчёркивание sage), не bodySmall muted как в макете; на последней странице «Тәмамлау» → `Routes.recap(id)` — единственное изменение навигации (PROMPT § 3).
- Reader: подсветка выбранного слова — `SpanStyle(background)` без r4.
- Reader: подчёркивание слов выше уровня реализовано (`drawBehind` по `getBoundingBox`, 1.5 dp цветом `level().dot`), карта `hardWords` пуста — API токенов нет; `AppPreferences.underlineHardWords` учитывается.
- Reader: лист «Аа» — `ModalBottomSheet` + `BottomSheetCard`, чипы `FilterChip` 36 dp; `AnimatedContent` ключуется по тексту (слой + страница), scroll на страницу общий для слоёв; при смене слоя всплывашка исчезает, `selectedWord` в ViewModel не очищается.
- Recap: «×» на белом круге 60 % — своя `IconButton` 48 dp; «Укырга» ведёт в Reader (в коде было BookDetail, в макете Reader); `book == null` → обе кнопки в LIBRARY; статистика — три «—».
- Scanner Start: визир пустой (sand) с иконкой камеры — фото Казани из макета не ставится (фальшивое превью при системной камере).
- Scanner Busy: индикатор неопределённый `LinearProgressIndicator` sky/surfaceVariant вместо «55 %»; «Туктату» отменяет `Job` корутины (`CancellationException` пробрасывается, Retrofit-вызов отменяется).
- Scanner Text: `translateOcrText` вызывается при любом непустом тексте (и для найденной книги) — слой «Русча» работает всегда; карточка «Бу өзек кайсы китаптан?» не показывается (сравнение приходит вместе с распознаванием); в карточке найденной книги только «тулы версиясе базада бар» (номер блока API не отдаёт); добавлено состояние `bookAuthor` из `OcrResponse.book_author`.
- Scanner Match: `ModalBottomSheet` + `BottomSheetCard`; «{author} · {year}» без жанра; текст листа — «Тулы версиясен өч катламда укый аласыз.»; год/слова/блоки/уровень из `BookMetaTable`; «Аа» в шапке сканера без действия.
- Фолбэки `e.message` («Не удалось распознать изображение», «Не удалось открыть изображение») оставлены в коде по-русски — логика, не UI-строки.

## Шаги 4.4–4.5 и фаза 5

- 4.4: `./gradlew assembleDebug` не запускался — нет JDK и SDK; вместо сборки — статические проверки ресурсов и импортов скриптами (см. `04-verification.md`). Дубликат `cd_profile` между `strings_library.xml` и `strings_leveltest.xml` убран (aapt «duplicate resource»).
- 4.5: три полных прогона `feature-dev:code-reviewer` + `/code-review high`; починено 12 пунктов (список в `04-verification.md`). `TatlibApp`: `popUpTo(Routes.LIBRARY)` вместо стартового `SPLASH`, который уже вытолкнут из стека.
- `WhitePill` удалён: в v2.1 на фото используется `ActionButton.OnPhoto`, вызовов не было. `LevelDot` оставлен (MASTER § 2.3, лист компонентов).
- Сканер: `cameraUri` в `rememberSaveable` (пересоздание Activity во время системной камеры), `finally` сбрасывает `isLoading` только своему `Job`, запуск камеры в `runCatching` (эмулятор без камеры).
- Ридер: `getLineEnd(visibleEnd = true)` — иначе первая часть текста перед всплывашкой заканчивалась переводом строки.

## Фаза 5 — независимый агент (три расхождения, все починены)

- Плитки «Әкиятләр»/«Хикәяләр» на «Эзләү» брали `forest`/`terracotta`, которые в тёмной теме светлеют (`#A9C79A`, `#E08A66`) — белый текст 1.85:1 / 2.63:1. В `tile()` цвета — литералы, не зависящие от темы → токены `tileForest #4E6B48`, `tileTerracotta #B4522E` одинаковы в обеих схемах (как `tileSteel`/`tileHoney`).
- Мини-бар «Хәзер укыла» лежал в `bottomBar` Scaffold и входил в `innerPadding` — выглядел второй полкой навигации, а не плавающим плеером (`.mini` position:absolute, bottom 88). Вынесен в `Box` поверх `NavHost` (`align(BottomCenter)`, 12/12/8); Library и Search оставляют 72 dp снизу.
- У полей `GlassField`/`PlainField` не было состояния фокуса (`.field.focus` 2 dp forest, MASTER § 7.10) — добавлен `MutableInteractionSource` + `collectIsFocusedAsState`: рамка 2 dp `colorScheme.secondary` при фокусе.

## После сборки на эмуляторе (по команде владельца поставлены JDK 17 и Android SDK)

- `./gradlew clean assembleDebug` — BUILD SUCCESSFUL, 0 ошибок; `gradlew` получил бит исполнения (закоммичен).
- Ридер: слово для API и всплывашки — без кавычек/знаков препинания по краям (`trim { !it.isLetterOrDigit() }`), раньше уходило «Кырлай» с кавычками (как и в старом коде).
- Ридер: перенос строки после строки слова не переносится во вторую часть текста — иначе под карточкой была пустая строка.
- Скриншоты — `docs/design/screenshots/{light,dark}/*.jpg` (JPEG q85 вместо PNG: 35 МБ → 7 МБ).
