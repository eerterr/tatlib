# Readlang — веб (+ PWA, без нативных iOS/Android) — языковой ридер с inline-переводом

Источники: [readlang.com](https://readlang.com/), [readlang.com/about](https://readlang.com/about), [readlang.com/features](https://readlang.com/features), [readlang.com/pricing](https://readlang.com/pricing), [readlang.com/start](https://readlang.com/start), [forum.readlang.com — есть ли нативное приложение](https://forum.readlang.com/t/is-there-a-readlang-iphone-ipad-android-app/46), [forum.readlang.com — AA tab / настройки чтения](https://forum.readlang.com/t/guide-to-the-options-in-the-reader-page-aa-tab/1294), [forum.readlang.com — цветовая шкала знания слов](https://forum.readlang.com/t/a-color-scale-feature-for-tracking-word-familiarity-progress/1634), [blog.readlang.com](https://blog.readlang.com/), [blog.readlang.com — streak improvements](https://blog.readlang.com/2023/06/27/streak-improvements.html), [apps.apple.com — ReadLang WatchLang (сторонний клон)](https://apps.apple.com/us/app/readlang-watchlang/id1602114377) (дата просмотра 2026-09-20)

## 1. Что за продукт, для кого, где популярен
Readlang — веб-сервис для изучения языка через чтение: клик по слову/фразе даёт перевод, переведённые слова сохраняются во флэшкарты со spaced repetition (readlang.com). Разработан Стивом Ридаутом, ранее инженером Duolingo (делал Duolingo Stories); мотив создания — не нашёл простого инструмента для изучения испанского (readlang.com/about). Заявленная миссия — «лучший инструмент для чтения в мире для изучающих языки» (readlang.com/about). Счётчик на главной: 550 086 пользователей перевели 53 598 232 слова из 521 750 текстов (readlang.com).

**Официального нативного приложения для iOS/Android нет.** Ссылка «Readlang for iOS and Android» в футере сайта ведёт не в App Store/Google Play, а на форумный гайд по установке PWA на домашний экран (readlang.com; forum.readlang.com). Прямая цитата основателя Steve: «There aren't native Readlang mobile apps, but the Readlang web-app has been designed with phones and tablets in mind» (forum.readlang.com). В сторах есть приложение «ReadLang WatchLang» — НЕ продукт Readlang: разработчик Nguyen Hong Phuc, рейтинг 4.4 (62 оценки) в App Store, в собственном описании упоминает ReadLang и Learning with Texts как референс-инструменты: «Do you know Learning with Texts (LWT), ReadLang, Lingq, LingoTube, Flowlingo, Redwiki, Tedict, Idiom ... LangBrowser works the same way» (apps.apple.com, проверено WebFetch 2026-09-20; ранее в файле цитата была дана неточно, дословной фразы «established tools like...» на странице нет). Оценки и установки собственно readlang.com в сторах — не проверено (площадок нет).

Страны/география популярности — не проверено (на официальных страницах не указано).

## 2. Главный экран: структура, что первое видит пользователь
Hero-секция с призывом «Click to translate», галерея поддерживаемых языков (испанский, немецкий, французский, английский, итальянский, португальский, русский, голландский, шведский и ещё, более 100 всего), кнопка старта обучения (readlang.com). Внутренний экран `/start` — SPA-загрузчик («Loading Readlang…» со спиннером), контент рендерится через JS и не считывается инструментом без исполнения скриптов — структура онбординга/теста уровня после загрузки не проверено.

## 3. Экран чтения: типографика, слои/переводы, тап по слову, управление сложностью
Клик по слову/фразе на любом тексте или сайте даёт мгновенный перевод («fast inline translations», «super fast click-to-translate»); попап-словарь в Web Reader, боковой словарь в онлайн-ридере (readlang.com/features). Контекстные AI-объяснения слов и фраз (readlang.com/features, readlang.com). Настройки чтения (вкладка Settings/«AA») в readlang.com/library — по официальному гайду форума:
- шрифты: `Helvetica`, `Open Sans`, `Times New Roman`, `Jost`;
- +/- Font (размер шрифта), +/- Line Height (межстрочный интервал);
- Column-контролы для ширины колонки;
- темы `Light` / `Dark`;
- выравнивание `Align Left` / `Justify`;
- форматирование `No Sentence Breaks` / `Sentence Breaks` (разбивка на предложения по строкам)
(forum.readlang.com/guide-to-the-options-in-the-reader-page-aa-tab).

Трёх-слойного текста (оригинал/упрощённый/перевод одновременно, как в TatLib) на официальных страницах не описано — не проверено. Материалы делятся по CEFR-уровню сложности — не проверено (встречалось только в сторонних сниппетах поиска, не на первоисточнике).

Дополнительно на readlang.com/features заявлены: словарь с управлением («Collect your words and phrases», редактирование, экспорт в Anki), видеоплеер с полными транскрипциями («Practice listening with full transcriptions»), разговорная практика с AI-партнёром «Ami» (readlang.com/features).

## 4. Прогресс и мотивация: как показаны уровень, серия, статистика
Streak (серия дней) — иконка пламени со счётчиком в углу страницы, круг вокруг заполняется по прогрессу; бледное пламя — цель дня не достигнута, яркое — достигнута и счётчик +1. Показывается и на главной, и прямо в режиме чтения. Дневная цель: «read 500 words or to practice 10 words, or some combination of the two, every day». Философия явно названа Seinfeld Strategy: «Your only job is to not break the chain» (blog.readlang.com/streak-improvements).

Отдельно зафиксирован сознательный отказ от подробного трекинга «знания» каждого слова: на прямой вопрос пользователя о цветовой шкале знакомости слов Steve не предложил такую фичу и пояснил нежелание «have the overhead of having to store your progress in all words» — по образцу LingQ — ради простоты интерфейса (forum.readlang.com/color-scale-feature). Единственная упомянутая связанная функция — auto-highlight (автоподсветка слов при чтении), без детализации визуального кодирования — не проверено, как именно выглядит подсветка.

## 5. Онбординг и тест уровня
Не проверено. Страница `/start` — JS-приложение, содержимое после загрузки инструментом не считано; упоминаний placement/level-теста на readlang.com, readlang.com/about, readlang.com/features официально не найдено.

Тарифная механика для справки (readlang.com/pricing): Free — 10 переводов фраз в день (макс. 6 слов) и 10 контекстных объяснений в день, флэшкарты недоступны; Premium ($6/мес или $48/год) — безлимитные переводы (до 12 слов во фразе), флэшкарты и экспорт, голос для слов/фраз; Premium Plus ($15/мес или $120/год) — GPT-4o вместо GPT-4o mini, голосовая озвучка целых текстов, до 200 часов аудиохранилища. Одна подписка работает сразу для нескольких изучаемых языков одновременно.

## 6. Визуальная система: палитра, шрифты, радиусы, иконки, иллюстрации, движение
Шрифты в самом ридере подтверждены (раздел 3): Helvetica, Open Sans, Times New Roman, Jost. Hex-коды палитры сайта/интерфейса, радиусы компонентов, стиль иконок и иллюстраций, наличие анимаций/движения — не проверено (официальные страницы текстовые, без спецификации токенов дизайн-системы; инструмент не даёт снять CSS-значения с уверенностью в точности).

## 7. Что берём для TatLib (3 конкретных приёма)
- **Инлайн-перевод по тапу без модалки** — перевод слова/фразы появляется прямо в потоке чтения (попап/сайдбар), не разрывая контекст — прямое попадание в задачу TatLib с тремя слоями текста.
- **Настройки чтения как отдельная лёгкая панель (AA-вкладка)**: шрифт, размер, интервал, ширина колонки, тема, выравнивание — компактный, но полный набор именно того, что нужно длинному тексту книги, без лишних опций.
- **Минимальный, но заметный сигнал мотивации** — один индикатор (пламя + счётчик), цель уровня «дёшево достижима» (500 слов или 10 повторений в день) — вместо перегруженной статистики. Для TatLib это модель для показа серии/прогресса теста A1–C1 без превращения экрана чтения в дашборд.

## 8. Что НЕ берём и почему
- **Отсутствие нативного мобильного приложения** — TatLib изначально Android-приложение, PWA-подход Readlang («сохрани на экран») не подходит для наших целей офлайн-чтения и сканера бумажной книги.
- **Сознательный отказ от детального трекинга знания каждого слова** — у TatLib цель другая: явный многоуровневый прогресс по трём слоям и CEFR-тесту, поэтому минимализм Readlang здесь неприменим один в один, нам нужна более богатая модель прогресса (это ближе к LingQ, который Readlang сознательно не повторяет).
- **Разрозненные цены Premium/Premium Plus по фичам** ($6 и $15 в месяц, см. readlang.com/pricing) — не образец для TatLib, у нас другая модель монетизации/задача бенчмарка не про pricing.

## 9. Скриншоты: ссылки на страницы сторов/пресс-китов
- [readlang.com](https://readlang.com/) — главная страница
- [readlang.com/features](https://readlang.com/features) — страница функций
- [readlang.com/pricing](https://readlang.com/pricing) — тарифы
- [apps.apple.com — ReadLang WatchLang](https://apps.apple.com/us/app/readlang-watchlang/id1602114377) — сторонний клон в App Store (НЕ официальный продукт, справочно)
- [readlang.com/about](https://readlang.com/about) — страница о команде и истории продукта
- Официального листинга Readlang в Google Play / App Store — не проверено (не существует по данным форума)

Отчёт: файл записан по указанному пути. Открыто 9 источников: readlang.com (главная, /about, /features, /pricing, /start), forum.readlang.com (2 темы: наличие приложения, настройки AA-вкладки, цветовая шкала — 3 темы), blog.readlang.com (главная + пост про streak), apps.apple.com (страница ReadLang WatchLang). Не проверено: онбординг и тест уровня (SPA не рендерится), CEFR-деление материалов, трёхслойный текст, hex-палитра и радиусы UI, иконки/иллюстрации/анимация, страны популярности, рейтинги/установки собственно Readlang в сторах (официального листинга нет), точное визуальное кодирование auto-highlight.
