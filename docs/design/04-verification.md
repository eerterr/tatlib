# TatLib — проверка фаз 4–5 (перенос дизайна в Compose)

Дата: 20.09.2026 · ветка `silvers-rayleigh7` · модель Opus 5.
Правило: «готово» — это осмотренный глазами результат, не лог. Ниже — что проверено чем, и явный список непроверенного.

## Окружение

| Что | Результат |
|---|---|
| `java -version` | «Unable to locate a Java Runtime» |
| `ls ~/Library/Android/sdk`, `ANDROID_HOME`, `local.properties` | SDK нет, переменная пуста, файла нет |
| `./gradlew assembleDebug` | **не запускался**: нет JDK и SDK (`gradlew` к тому же без бита исполнения в репозитории) |

## Что проверено чем

| Фаза / шаг | Артефакт | Проверено чем | Результат |
|---|---|---|---|
| 4.1 тема | `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`, `Shape.kt`, `Ornaments.kt`, `Preferences.kt` | значения сверены с MASTER § 2–3 и `tatlib.css` построчно; API (`lightColorScheme(surfaceContainer*)`, `addPathNodes`, `ImageVector.Builder.addPath`, `PathParser`) — по sources-jar точных версий из POM BOM 2024.06.00 (material3 1.2.1, ui 1.6.8, dl.google.com) | совпадает; API существуют |
| 4.1 шрифты | `res/font/*.ttf` (9), `assets/licenses/OFL-*.txt` (3) | fontTools `getBestCmap()` на ә ө ү җ ң һ Ә Ө Ү Җ Ң Һ после копирования | 9/9 файлов содержат все 12 букв |
| 4.1 фото | `res/drawable-nodpi/*.jpg` (10) | `ls`, размеры 17–169 КБ, имена lowercase | 10/10 |
| 4.1 данные | `data/BookMeta.kt` | `sqlite3 backend/tatar_adaptive.db`: `books`, `book_stats`, `text_complexity` | 413/292/28 · 929/623/4 · 1560/731/32 · 1500/845/29; уровни B1 B1 B1 B2; годы 1908/1907/—/1908 — совпадают с кодом и макетами |
| 4.2 компоненты | `ui/components/*.kt` (10 файлов) | иконки — по sources-jar `material-icons-core/extended 1.6.8` (`AutoMirrored.Rounded.MenuBook`, `CenterFocusWeak`, `BarChart`, `ChevronRight`, `CameraAlt`, `VisibilityOff`, `Search`, `PlayArrow`, `Close`, `ArrowBack`, `Check`, `Person`, `Lock`); `SwitchDefaults.colors(checkedBorderColor)`, `SliderDefaults.colors(activeTickColor)`, `ModalBottomSheet(dragHandle)`, `CircularProgressIndicator(progress: () -> Float)`, `pluralStringResource` (без Experimental), `AnimatedContent`/`togetherWith` (без Experimental), `TextLayoutResult.getLineEnd(lineIndex, visibleEnd)` — по sources-jar 1.6.8 / 1.2.1 | все существуют в этих версиях |
| 4.3 экраны | 16 экранов, 5 файлов строк tt + 5 ru | скрипт: 0 ошибок XML; 167 строк tt / 150 ru, все непереведённые помечены `translatable="false"`; дубликатов имён нет (один найден и убран: `cd_profile`); все `R.string/drawable/font/plurals/array` существуют; плейсхолдеры `%1$d/%1$s` совпадают tt↔ru; тексты макетов сверены агентами групп посимвольно с `.dc.html` (A: 23/23, B: 49/49, C: 24/24, D: все) | чисто |
| 4.3 правила | весь `app/src/main/java` | `grep "Color(0x"` вне `ui/theme/` — только градиент `.wave` (`components/Progress.kt:141–143`, разрешён); `grep "\.sp\b"` вне `ui/theme/` — 0; кириллица в коде — только `e.message`-фолбэки сканера, ключи данных API, `MockData.levelQuiz`, `ComponentsPreview.kt` | чисто |
| 4.3 «кто вызывает» | `TatlibApp.kt` | все 16 экранов вызываются ровно по разу; `HeroBackground.kt`/`KazanSunsetHero` — 0 упоминаний; старые компоненты (`PrimaryButton`, `ScreenTopBar`, `BookCoverArt`, `LevelBadge`, `StatTile`, `SimpleLineChart`…) — 0 упоминаний; `WhitePill` без вызовов — удалён; `MockData.suAnasy/shurale/najip/progressStats/inspirationalQuotes` — удалены, ссылок 0 | чисто |
| 4.3 импорты | все `.kt` | скрипт «использование ↔ import» для 45 модификаторов/функций Compose (`height`, `clip`, `graphicsLayer`, `stringResource`, `rememberSaveable`, иконки…) — чувствительность проверена удалением импорта | 0 пропусков (один найден и починен: `Modifier.height` в `ProfileScreen`) |
| 4.5 ревью | diff ветки | `feature-dev:code-reviewer` (Opus) — три полных прогона + перепроверка правок; `/code-review high` — один прогон | найдено и починено 12 пунктов: непрокручиваемые колонки сканера/профиля/recap, `selectedBook` без проверки id и без ошибки в ридере, `popUpTo(SPLASH)` в `TatlibApp`, накопление reader→recap, «Аа» литерал, отступ навигационной панели на странице книги, пустой OCR-текст без сообщения, импорт `height`, `cameraUri` в `rememberSaveable`, гонка `finally` при «Туктату», `ActivityNotFoundException` камеры, тип лямбды `openCamera` (`Result<Unit>` вместо `Unit`) |
| 5 независимый агент | реализация против макета/MASTER | `general-purpose` (Opus): три расхождения, которых нет в журнале | найдено 3, починено 3: плитки поиска в тёмной теме (контраст 1.85:1 → токены `tileForest`/`tileTerracotta`), мини-бар как полка вместо плавающего (`.mini` absolute → `Box` поверх `NavHost`), нет фокуса у полей (§ 7.10 → рамка 2 dp `secondary`); агент подтвердил без расхождений: цифры книг и пользователя против базы, все hex цветов ролей и шкалы уровней, тексты tt/ru против `.dc.html`, размеры 20 компонентов против `tatlib.css` |
| 5 гейт | ветка | `git status` чистый; 30 коммитов над `main`; журнал `03-implementation-log.md` — все отступления четырёх групп и мои | чисто |

## Не проверено

1. **Компиляция и сборка APK** — нет JDK и Android SDK. Все утверждения о компилируемости — по чтению и sources-jar, не по компилятору. Первые кандидаты на ошибку при сборке: `@Composable`-лямбды в слотах (`subtitle: @Composable (Book) -> String` в `LibraryScreen`), `BiasAlignment` в группе A, общий `ScrollState` у двух детей `AnimatedContent` в ридере (не падает по чтению исходников, на устройстве не проверено), `Modifier.shadow(ambientColor/spotColor)` (цвет тени только с API 28+).
2. **Скриншоты экранов в светлой и тёмной теме** (`docs/design/screenshots/`) — нет эмулятора; визуальная сверка с `docs/design/mockups/shots/*.png` не выполнена. Не осмотрены глазами: положение всплывашки под строкой слова, сдвиг hero при прокрутке главной, декор (дуги/звёзды/арка) по координатам макета, стекло без blur, обложка «Алтын әтәч» (кегль от ширины), контраст в тёмной теме.
3. **Поведение на устройстве**: отмена OCR по «Туктату», поворот экрана во время OCR (состояние сканера в `remember`, не в ViewModel — теряется), клавиатура на Register/Login (`imePadding`), тач-цели ≥ 48 dp у чипов.
4. **Бэкенд-контракты**: `/api/ocr` может вернуть `text: null` (Gson обойдёт non-null Kotlin) — не проверял `ocr.py`; локально API не поднимал (Windows-пути в `ocr.py`).
5. **Тексты**: `Skill(text-quality-guard)` агентами не вызывался — русские и татарские строки проверены вручную (дважды каждым агентом, посимвольная сверка с `.dc.html` скриптом). Возможны ошибки в русских переводах, которых нет в макетах (состояния загрузки/ошибок).
6. **Тег 1.6.8 на googlesource** не открылся; сигнатуры взяты из sources-jar тех же версий с dl.google.com — это первоисточник, но не git-тег.
7. `ComponentsPreview.kt` лежит в `main` (компилируется в APK) — не перенесён в `src/debug`.

## Открытые вопросы владельцу (до трёх)

1. `AppViewModel.kt:294` кладёт «Тәрҗемә табылмады» в состояние перевода — из-за этого ресурс `reader_translation_missing` (tt/ru) не срабатывает; починка — одна строка в ViewModel (`= null`), но файл вне scope фазы. Разрешить?
2. `viewModel.updateProgress` никто не вызывает (и до редизайна тоже) — прогресс книг всегда 0, мини-бар «Хәзер укыла» и «Укуны дәвам итү» всегда показывают первую книгу. Добавить вызов из ридера при смене страницы (одна строка в `BookReaderScreen`)?
3. Результат теста (`correctAnswers`) не передаётся в `LevelResult` (как и раньше) — уровень всегда B1. Оставить как есть или передать через аргумент маршрута (изменение навигации)?
