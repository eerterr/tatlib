# Readwise Reader — Web / iOS / Android / macOS / Windows — read-it-later / ридер длинного текста

Источники: [apps.apple.com — Readwise Reader](https://apps.apple.com/us/app/readwise-reader/id1567599761), [readwise.io/read](https://readwise.io/read), [docs.readwise.io/reader/docs](https://docs.readwise.io/reader/docs), [docs.readwise.io — Appearance](https://docs.readwise.io/reader/docs/faqs/appearance), [docs.readwise.io — Highlights, Tags, and Notes](https://docs.readwise.io/reader/docs/faqs/highlights-tags-notes), [docs.readwise.io — Adding New Content](https://docs.readwise.io/reader/docs/faqs/adding-new-content), [docs.readwise.io — Basics (FAQ)](https://docs.readwise.io/reader/docs/faqs), [docs.readwise.io — Ghostreader Overview](https://docs.readwise.io/reader/guides/ghostreader/overview), [play.google.com — Readwise Reader](https://play.google.com/store/apps/details?id=com.readermobile&hl=en_US) (дата просмотра 2026-09-20)

## 1. Что за продукт, для кого, где популярен
Readwise Reader — «the first read-it-later app built for power readers» (readwise.io/read): объединяет веб-статьи, email-рассылки, RSS, треды X, PDF, EPUB, YouTube-видео и подкасты в одном приложении с аннотированием. Заявленная аудитория на readwise.io/read: founders, учёные и автодидакты, инженеры/технологи, knowledge workers.

App Store (apps.apple.com/us/app/readwise-reader/id1567599761): категория Productivity, рейтинг 4.6 из 5 (582 оценки), цена Free с покупками внутри приложения, возрастной рейтинг 16+. Google Play-листинг существует (play.google.com/store/apps/details?id=com.readermobile), но конкретные цифры рейтинга/установок с этой страницы сегодня извлечь не удалось техническими средствами — не проверено. Популярные страны — не проверено, в открытых первоисточниках не указано.

## 2. Главный экран: структура, что первое видит пользователь
Продукт делится на два верхнеуровневых раздела — Library и Feed (docs.readwise.io/reader/docs/faqs/adding-new-content). Library — то, что пользователь вручную сохраняет себе насовсем; подразделяется на Inbox, Later, Archive, Shortlist и отдельную вкладку Podcasts. Feed — контент, который поступает автоматически (RSS и т.п.), делится на Unseen и Seen; пользователь вручную переносит нужное из Feed в Library.

Какой конкретно экран пользователь видит первым при открытии приложения — не проверено, в открытой документации явно не описано.

## 3. Экран чтения: типографика, слои/переводы, тап по слову, управление сложностью
Панель Appearance (иконка «Aa» на вебе, пункт меню «...» на мобильном, docs.readwise.io/reader/docs/faqs/appearance) даёт настроить: typeface — набор serif- и sans-serif-гарнитур, среди названных явно Atkinson Hyperlegible и OpenDyslexic (полный список гарнитур — не проверено); font size 14–80px, дефолт 20px; line spacing, дефолт 1.4; line width (только на вебе, дефолт medium); направление текста для RTL-языков (арабский, иврит); режим «paged scroll» и горизонтальная пагинация на планшетах в альбомной ориентации.

Выделение (docs.readwise.io/reader/docs/faqs/highlights-tags-notes): «Reader doesn't support multiple colors of highlights» — выделение одноцветное, для категоризации используются теги, а не цвета. На iPad Apple Pencil запускает выделение сразу при касании стилуса текста, «just like a highlighter on a real piece of paper»; также поддержаны S Pen и стилусы BOOX. Заметка к выделению добавляется через кнопку «i» → вкладку Notebook, либо через значок речевого пузыря на экране сохранения документа.

Слоёв «оригинал / упрощённый текст / перевод» и регулировки сложности текста в Reader не найдено — не проверено / не применимо, так как продукт не для изучения языка.

## 4. Прогресс и мотивация: как показаны уровень, серия, статистика
Reader отслеживает две отдельные метрики документа — reading progress (самая дальняя точка, которую пользователь реально прочитал) и last location (текущее место, включая быстрый скролл). Reading progress не двигается при быстром пролистывании и не уменьшается при возврате назад перечитать раздел; на мобильном тот же прогресс показан тонкой линией над нижним тулбаром во время чтения (docs.readwise.io/reader/docs/faqs). По прогрессу можно фильтровать библиотеку запросами вида `progress__gt:5` (подтверждено на [docs.readwise.io/reader/guides/filtering/query-examples](https://docs.readwise.io/reader/guides/filtering/query-examples), проверено 2026-09-20 — исходная ссылка «там же» на faqs-страницу этот пример не содержит).

Серии (streak), уровни, очки, геймификация — в открытых официальных источниках не описаны — не проверено.

## 5. Онбординг и тест уровня
На старте: бесплатный пробный период 30 дней без привязки карты; импорт из Pocket, Instapaper, Feedly (CSV/URL), импорт RSS-подписок через OPML, добавление контента через браузерное расширение, шеринг с телефона или загрузку файла, затем настройка интерфейса (размер шрифта, тёмная тема, режим скролла) и переход к чтению с выделением (docs.readwise.io/reader/docs).

Тест уровня владения языком — не применимо: Reader не приложение для изучения языка, теста уровня в продукте не найдено.

## 6. Визуальная система: палитра, шрифты, радиусы, иконки, иллюстрации, движение
Темы: light, dark и auto (подхватывает тему ОС), переключаются из меню Appearance или сочетанием клавиш Cmd/Ctrl+Option+T (docs.readwise.io/reader/docs/faqs/appearance); отдельного режима «sepia» в этом источнике не упомянуто — не проверено. Шрифты для чтения — variable typeface picker с serif/sans-serif гарнитурами, явно названы Atkinson Hyperlegible и OpenDyslexic (акцент на читаемость и accessibility).

Ghostreader (docs.readwise.io/reader/guides/ghostreader/overview) — «Reader's AI-powered assistant»: на вебе вызывается клавишей `G` для выделенного текста или `Shift+G` для всего документа, либо пунктом «Chat about this» в контекстном меню выделения; на мобильном — через поле «Ask anything...», меню действий или значок «призрака» в панели аннотаций после выделения текста. Пресеты: summarize, объяснение слова/термина/персонажа/локации, перевод на любой язык, черновик врезки для рассылки, плюс кастомные промпты; есть Global Ghostreader по всей библиотеке с цитированием источников (веб/десктоп), на мобильном — Quick Lookup для быстрых определений.

Hex-коды палитры бренда, радиусы компонентов, стиль иконок, иллюстрации и анимация/движение интерфейса — в открытых первоисточниках сегодня не нашлись — не проверено. Встреченные в поиске hex-коды «цветов выделения» взяты с неофициального дизайн-блога (не readwise.io / docs.readwise.io) и прямо противоречат официальной документации о том, что цветного выделения в Reader нет, — поэтому в файл не включены.

## 7. Что берём для TatLib (3 конкретных приёма)
- **Двойная метрика прогресса**: «reading progress» (докуда реально дочитал) отдельно от «last location» (где просто пролистал) — честнее одного процента и подходит для трёхслойного чтения TatLib, где переход к переводу не должен засчитываться как прочтение оригинала.
- **Тап по слову/фрагменту → всплывающая панель действий** (аналог Ghostreader: объяснить/перевести/определить, без ухода с экрана чтения) — прямое попадание в механику TatLib «тап по слову с переводом».
- **Панель настроек чтения с раздельными контролами** font size / line spacing / line width и явным выбором гарнитуры под читаемость (Atkinson Hyperlegible, OpenDyslexic) — для длинного текста на татарском и русском важна именно раздельная настройка интерлиньяжа и ширины строки, а не один пресет «крупно/мелко».

## 8. Что НЕ берём и почему
- **Одноцветное выделение без цветовой кодировки** — в Reader это сознательный выбор (теги вместо цветов), но три языковых слоя TatLib (оригинал/упрощённый/перевод) выигрывают от визуальной дифференциации, одного цвета выделения недостаточно.
- **Двухчастная структура Library/Feed с ручным триажем Unseen → Seen → Library** — модель для read-it-later потока из множества внешних источников (RSS, рассылки); у TatLib нет входящего потока контента, который нужно разбирать, сложность триажа не нужна.
- **Ставка на AI-чат по всей библиотеке (Global Ghostreader с цитированием источников)** — функция для исследовательского чтения большого разнородного корпуса, избыточна для приложения с одной книгой и тестом уровня A1–C1.

## 9. Скриншоты: ссылки на страницы сторов/пресс-китов
- [apps.apple.com — Readwise Reader](https://apps.apple.com/us/app/readwise-reader/id1567599761) — карточка в App Store
- [play.google.com — Readwise Reader](https://play.google.com/store/apps/details?id=com.readermobile&hl=en_US) — карточка в Google Play
- [readwise.io/read](https://readwise.io/read) — маркетинговая страница продукта
- Отдельного пресс-кита/страницы брендбука не найдено — не проверено.

Отчёт: файл записан по указанному пути. Открыто 8 источников: App Store, Google Play (частично — рейтинг/установки не считались), readwise.io/read, docs.readwise.io (docs, faqs/appearance, faqs/highlights-tags-notes, faqs/adding-new-content, faqs), docs.readwise.io/reader/guides/ghostreader/overview. Не проверено: установки и рейтинг Google Play, страны популярности, что именно видит пользователь первым при запуске, наличие sepia-темы, серии/геймификация, hex-палитра бренда, радиусы/иконки/иллюстрации/анимация UI, тест уровня (в продукте отсутствует по построению).
