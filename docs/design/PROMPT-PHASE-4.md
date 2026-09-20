# TatLib — промт для нового терминала: фазы 4–5 (перенос дизайна в Jetpack Compose)

> Запускать в Claude Code (desktop, Code tab) в репозитории `~/Projects/tatlib`, ветка **`silvers-rayleigh7`** (уже создана, всё закоммичено, `git status` чистый).
> Модель сессии: **Opus 5**, effort **high**. Смена модели по ходу не нужна.
> Дата составления: 20.09.2026. Дизайн принят владельцем 20.09.2026 (v2.1).

---

## 0. Роль и цель

Ты продолжаешь редизайн Android-приложения TatLib (адаптивное чтение татарской литературы). Фазы 0–3 закрыты: ресерч, дизайн-система, макеты 28 экранов, дизайн принят. Твоя задача — фаза 4 (перенос макетов в Jetpack Compose) и фаза 5 (закрытие). Логика, навигация, `AppViewModel.kt`, бэкенд не переписываются. Меняется всё, что видит пользователь.

Режимы, действующие всю сессию и вызываемые в точке применимости:
`/deep`, `Skill(critical-analysis-first)`, `Skill(no-stale-claims)`, `Skill(triple-verification)`, `Skill(task-completion-gate)`, `Skill(superpowers:verification-before-completion)`, `Skill(text-quality-guard)` для любых строк интерфейса, `Skill(stop-slop)` для любого текста владельцу.

## 1. Что уже есть в репозитории (читать первым, в этом порядке)

| Файл | Что там |
|---|---|
| `docs/design/PROMPT.md` | исходный бриф всего проекта, § 6–7 — фазы 4–5, § 9 — запреты |
| `docs/design/00-ux-map.md` | карта экранов, таблица 28 расхождений Figma / код / решение, аудит базы (уровни, страницы ридера, мёртвый маршрут Recap) |
| `docs/design/01-research-synthesis.md` | паттерны 10 бенчмарков, якорь |
| `design-system/tatlib/MASTER.md` + `pages/reader.md`, `pages/onboarding.md` | дизайн-система: цвета с посчитанным контрастом, шрифты, шкала уровней, компоненты. **§ 0 «Правки владельца после v1» и «v2.1» перекрывают остальные разделы там, где расходятся** |
| `docs/design/02-screens.md` | список 28 артбордов, решения по данным и текстам, что проверено |
| `docs/design/mockups/*.dc.html`, `tatlib.css`, `gen2.py` | **исходники макетов** — точная вёрстка каждого экрана, все тексты интерфейса, SVG-узоры (`ORN` в `gen2.py`), токены в `tatlib.css` |
| `docs/design/mockups/shots/*.png` | скриншоты всех 28 экранов — эталон для сверки |
| `docs/design/mockups/tatlib-screens.pdf` | то же одним файлом |
| `docs/design/mockups/assets/*.jpg` | обложки трёх книг, фото Казани (hero-blossom, hero-morning/day/evening, hero-sunset, arch-mosque, kremlin-bottom) |
| `docs/design/fonts/*.ttf` + `OFL-*.txt` | Playfair Display 400/400i/600, Golos Text 400/500/600, Literata 400/400i/500 — все проверены fontTools на ә ө ү җ ң һ, лицензии рядом |
| `docs/design/reference/figma/*.jpg` | исходники из Figma: триптих обложек 1536×1024, портрет Казани 940×1672, мудборд 1536×1024, два макета владельца |
| `backend/tatar_adaptive.db` | данные: 4 книги, `text_blocks` с тремя слоями, `tokens.estimated_level`, `book_stats`, `text_complexity`, `users`, `user_level_state` |

Канвас с макетами (Design-артефакт владельца): https://claude.ai/artifact/5AmucvovT4pXyUNev41YhM — открывать через `Artifact read` с `path: "project/<имя>.dc.html"`; те же файлы лежат в `docs/design/mockups/`, поэтому канвас нужен только для проверки.

## 2. Решения, принятые на гейте (не пересматривать)

1. **Стиль v2.1**: фото Казани на весь верх онбординга и главной, стеклянные формы (`backdrop`-эффект в Compose — `Modifier.blur` по подложке или полупрозрачный `paper` 34 % с рамкой 55 %), тонкие дуги и звёзды-ромбы на светлых экранах теста, фото в арке-куполе (`clip` по `Path`), ленты обложек 150×225 r16, мини-бар «Хәзер укыла» 64 dp над нижней навигацией, страница книги как страница альбома.
2. **Единый компонент перехода** на всех экранах: круг 56 dp с узором 1 + подпись рядом. Персиковый `#F3C7A1` — основное действие, голубой `#BFD7EA` — второстепенное, белый 92 % — на фото. «Читать» везде — пилюля `sage2 #C9DDB1` с ▶. Текстовые ссылки — ink с подчёркиванием `sage` 1.5 dp.
3. **Шрифты**: заголовки экранов Golos Text 600 (`h0` 36/40), интерфейс Golos Text 400/500, татарские фразы и обложка 4-й книги Playfair Italic, ридер Literata 18 sp (16–20, ×1.55).
4. **Сканер** (уточнено владельцем): камера → распознавание → текст с тем же переключателем слоёв, что в ридере (Русча · Адаптация · Оригинал) → если фрагмент найден в базе, лист «Китап табылды» с обложкой и «Тулы версиясен укырга». Бэкенд для OCR-текста даёт только русский (`/api/ocr/translate`); слой «Адаптация» для сканера показывать как недоступный (disabled) до появления API, не выдумывать текст.
5. **Вход и регистрация** — без доработок сверх макета, владелец просил не тратить на них время.
6. **Только 4 реальные книги**, цифры только из базы, пустые значения — «—» или пустое состояние. Обложка «Алтын әтәч» — типографическая на закатном фото (в Figma её нет).
7. **Нижняя навигация**: Китапханә · Эзләү · Скан · Алгарыш (как в коде). Профиль — экран, вход через аватар на «Алгарышың»; новый маршрут `PROFILE` добавить в `Routes.kt` (это единственное разрешённое изменение навигации, кроме п. 8).
8. **`SCANNER` добавить в `Routes.bottomBarRoutes`** (сейчас бар пропадает на сканере).

## 3. Открытые вопросы — решения по умолчанию, если владелец не ответит иначе

| Вопрос | Решение по умолчанию |
|---|---|
| Мок книг: `MockData.kt` знает 3 книги со slug-id, а API отдаёт int-id | Убрать книги из мока, читать `/api/books`; `MockData.levelQuiz` и `levelFromScore` оставить. `RecapScreen` перевести на `viewModel.getBook(id)`. Записать в `03-implementation-log.md` |
| Маршрут `RECAP` мёртвый | Добавить переход из ридера на последней странице кнопкой «Тәмамлау» (одна строка навигации) |
| `/api/books` не отдаёт уровень книги, токены не отдаёт | Бэкенд не трогать. Уровень книги временно из константной карты `bookId → level` по `text_complexity` (1 B1, 2 B1, 3 B1, 4 B2) с комментарием `// TODO backend: estimated_level`. Подчёркивание слов выше уровня — только там, где есть данные (в макете это блок I «Шүрәле»); без API токенов не реализовывать, оставить хук в компоненте `ReaderText` |
| Login → куда | Как в коде (LevelIntro) |
| Приветствие по времени суток | «Хәерле иртә / Хәерле көн / Хәерле кич» по `LocalTime`; фон hero — `hero_morning` / `hero_day` / `hero_evening` |

## 4. Фаза 4 — перенос в Compose

Стек по репо: Kotlin, Compose BOM 2024.06.00, Material3, minSdk 24, targetSdk 34, Retrofit. Перед кодом — `python3 ~/.claude/skills/ui-ux-pro-max/scripts/search.py "material3 theming typography" --stack jetpack-compose` (правила уже в MASTER.md § 8, перечитать).

**Шаг 4.1 — тема (главная сессия).**
- `ui/theme/Color.kt`, `Type.kt`, `Theme.kt`, `Shape.kt` заменить целиком по MASTER.md § 2–3 и § 0 (v2/v2.1): светлая и тёмная `ColorScheme` + `LocalTatlibColors` (sky2, peach2, sage2, шкала уровней A1–C1 как пары контейнер/текст и точки), `LocalReaderTypography`.
- Шрифты: скопировать `docs/design/fonts/*.ttf` в `app/src/main/res/font/` с именами `playfair_display_regular.ttf` и т. д. (Android требует lowercase + underscore), лицензии `OFL-*.txt` положить рядом в `app/src/main/res/font/` или `app/src/main/assets/licenses/`.
- Изображения: `docs/design/mockups/assets/*.jpg` → `app/src/main/res/drawable-nodpi/` (имена `cover_su_anasy.jpg`, `hero_blossom.jpg` …).
- Узоры: 4 мотива + звезда из `gen2.py` (`ORN`) → `ui/theme/Ornaments.kt` как `ImageVector` (path data переносится 1:1, viewport 24×24).
- Завести `docs/design/03-implementation-log.md`: каждое решение по коду одной строкой.
- Коммит `feat(theme): …`, сборка проверяется (см. 4.4).

**Шаг 4.2 — компоненты (один субагент Opus 5, `Agent(subagent_type="general-purpose", model="opus")`).**
`ui/components/`: `ActionButton` (круг + узор + подпись, 3 цвета), `ReadPill` (sage + ▶), `TextLink`, `LevelChip` + `LevelDot`, `BookCover` (Image для 3 книг, типографическая для 4-й), `BookCard`, `BookRow`, `CategoryTile`, `TopBar` с TAT/RU (`LanguageToggle` оставить), `BottomNav` (4 пункта, активный в pill-контейнере), `MiniReadingBar`, `LayerSwitch` (3 сегмента, ползунок с узором 1; значения 0 / 0.5 / 1 как в `AppViewModel.selectVersion`), `WordPopup`, `ProgressLine`, `LevelRing`, `WeekWaffle`, `PhraseCard` (градиент + звёзды), `GlassCard` + `GlassField`, `PhotoHero`, `ArchPhoto` (clip по Path из `gen2.py` `arch()`), `Stars`/`Arcs` (декор), `EmptyState`. Удалить `HeroBackground.kt` (не используется в v2). Спека размеров — MASTER.md § 7 и `tatlib.css`.

**Шаг 4.3 — экраны (4 параллельных субагента Opus 5, `isolation: "worktree"`).**
Группа A: onboarding + auth (Splash, Welcome, AuthChoice, Register, Login) — по макетам `Main`, `Welcome`, `AuthChoice`, `Register`, `Login`.
Группа B: leveltest (Intro, Quiz, Result) + Progress + Profile (новый экран, маршрут `PROFILE`).
Группа C: Library (hero по времени суток, ленты, мини-бар) + Search (плитки категорий) + BookDetail (страница альбома).
Группа D: BookReader (слои, всплывашка слова, «Тәмамлау» → Recap) + Recap (пустое состояние) + Scanner (камера → текст с переключателем → лист «Китап табылды», логика из существующего `ScannerScreen.kt`: `recognizeImage`, `book_found`, `translateOcrText`).
Каждый агент получает: MASTER.md, `02-screens.md`, свои `.dc.html` из `docs/design/mockups/`, свои скриншоты из `shots/`, файлы своей группы; запрет трогать чужие файлы, `AppViewModel.kt`, бэкенд. Строки интерфейса — в `res/values/strings.xml` (татарский) и `values-ru/strings.xml` (русский), тексты брать из `.dc.html` без изменений, каждый ярлык через `Skill(text-quality-guard)`.

**Шаг 4.4 — сборка.**
```bash
./gradlew assembleDebug
```
На машине владельца Android SDK не найден (`ANDROID_HOME` пуст, `~/Library/Android/sdk` нет) — сначала проверить `ls ~/Library/Android/sdk`, `cat local.properties`. Если SDK нет — написать честно «сборку не проверил, SDK отсутствует», приложить вывод, предложить владельцу поставить Android Studio или `sdkmanager`. Если SDK есть — собрать, поставить на эмулятор (`mcp__Claude_Code_iOS_Simulator` тут не подходит — это iOS), пройти все экраны, скриншоты в `docs/design/screenshots/` в светлой и тёмной теме.

**Шаг 4.5 — ревью.** `Agent(subagent_type="feature-dev:code-reviewer", model="opus")` по diff ветки: утечки состояний, hardcoded строки вместо ресурсов, цвета мимо темы, шрифты мимо `Type.kt`, `Color(0xFF…)` в экранах. Затем `/code-review high`. Замечания чинить, ревью повторять полностью.

Коммиты по шагам: `feat(theme): …`, `feat(components): …`, `feat(screens): группа A …` и т. д. Атрибуция: `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>`.

## 5. Фаза 5 — закрытие (`Skill(task-completion-gate)`)

1. `git status` чистый, `./gradlew assembleDebug` зелёный или честное «не проверено» с причиной.
2. Проверка по результату: скриншоты каждого экрана в обеих темах осмотрены глазами и сверены с `docs/design/mockups/shots/`; буквы ә ө ү җ ң һ на месте во всех трёх шрифтах; книги только 4; ни одной выдуманной цифры.
3. «Кто это вызывает»: каждая новая композиция подключена в `TatlibApp.kt` / навигации, `HeroBackground.kt` удалён, старые компоненты без вызовов удалены.
4. Независимый агент `Agent(subagent_type="general-purpose", model="opus")`: найти три места, где реализация расходится с макетом или MASTER.md.
5. `docs/design/04-verification.md`: что проверено чем, явный список «что НЕ проверил».
6. `git push -u origin silvers-rayleigh7`. **PR в `main` не создавать** без отдельной команды владельца.

Финальное сообщение владельцу: ссылка на канвас, ссылка на ветку, таблица «фаза → артефакт → проверено чем», список непроверенного, до трёх открытых вопросов.

## 6. Запреты (нарушение = откат шага)

- Выдумывать книги, авторов, цифры, имена, тексты книг. Только `backend/tatar_adaptive.db` и строки из `.dc.html`.
- Трогать бэкенд, `AppViewModel.kt`, `main`. Создавать PR.
- Менять принятый дизайн «по вкусу»: расхождение с макетом — только с записью причины в `03-implementation-log.md`.
- Говорить «собирается» / «работает» по логам. Готово — это скриншот экрана, осмотренный глазами.
- Утверждать факты о версиях библиотек, API Compose, поведении Android без проверки документации в день утверждения (`Skill(no-stale-claims)`; для документации библиотек — `mcp__plugin_context7_context7__query-docs`).
