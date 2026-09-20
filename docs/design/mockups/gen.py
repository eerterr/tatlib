# -*- coding: utf-8 -*-
"""Генератор артбордов TatLib для Design-канваса. Все тексты — татарский первичный, данные — из tatar_adaptive.db."""
import json, os, datetime
ROOT = os.path.join(os.path.dirname(__file__), "project")
os.makedirs(ROOT, exist_ok=True)

# ---------- иконки (stroke) и орнаменты (fill) ----------
def ic(name, size=24, cls="ic"):
    p = {
        "back": '<path d="M15 5l-7 7 7 7"/>',
        "chev": '<path d="M9 5l7 7-7 7"/>',
        "close": '<path d="M6 6l12 12M18 6L6 18"/>',
        "book": '<path d="M4 5.5A2.5 2.5 0 016.5 3H20v15H6.5A2.5 2.5 0 004 20.5z"/><path d="M4 20.5A2.5 2.5 0 016.5 18H20"/>',
        "search": '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
        "scan": '<path d="M4 8V5.5A1.5 1.5 0 015.5 4H8M16 4h2.5A1.5 1.5 0 0120 5.5V8M20 16v2.5a1.5 1.5 0 01-1.5 1.5H16M8 20H5.5A1.5 1.5 0 014 18.5V16"/><path d="M7 12h10"/>',
        "chart": '<path d="M4 19h16"/><path d="M7 15v-4M12 15V7M17 15v-6"/>',
        "person": '<circle cx="12" cy="8" r="4"/><path d="M4 20c1.5-3.5 4.5-5 8-5s6.5 1.5 8 5"/>',
        "image": '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="1.6"/><path d="M21 16l-5-5-8 8"/>',
        "camera": '<path d="M4 8h3l2-3h6l2 3h3v11H4z"/><circle cx="12" cy="13" r="3.5"/>',
        "aa": '<path d="M3 17l4-10 4 10M4.6 13.5h4.8"/><path d="M14 17v-6.5M14 13.5c0-1.7 1.3-3 3-3s3 1.3 3 3v3.5M20 15.5c0 1-1.3 1.8-3 1.8s-3-.8-3-1.8 1.3-1.8 3-1.8 3 .8 3 1.8"/>',
        "sprout": '<path d="M12 21v-8"/><path d="M12 13c0-4 3-7 8-7 0 4-3 7-8 7z"/><path d="M12 13c0-3-2.5-5.5-6-5.5 0 3 2.5 5.5 6 5.5z"/>',
        "check": '<path d="M5 12l5 5 9-10"/>',
        "moon": '<path d="M20 14.5A8 8 0 019.5 4a8 8 0 1010.5 10.5z"/>',
        "sun": '<circle cx="12" cy="12" r="4"/><path d="M12 3v2M12 19v2M3 12h2M19 12h2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/>',
        "globe": '<circle cx="12" cy="12" r="8.5"/><path d="M3.5 12h17M12 3.5c3 3 3 14 0 17M12 3.5c-3 3-3 14 0 17"/>',
        "logout": '<path d="M10 4H6a2 2 0 00-2 2v12a2 2 0 002 2h4M15 8l4 4-4 4M19 12H9"/>',
    }[name]
    return f'<svg class="{cls}" viewBox="0 0 24 24" width="{size}" height="{size}" aria-hidden="true">{p}</svg>'

def orn(n, size=24, color="currentColor", extra=""):
    paths = {
        1: '<path d="M5 4c4 2 7 5 8 8-1 3-4 6-8 8" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/><path d="M11 4c4 2 7 5 8 8-1 3-4 6-8 8" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>',
        2: '<path d="M12 2.5c-1.6 3.8-5.5 5.6-5.5 9.5 0 3 2.8 5 5.5 8.5 2.7-3.5 5.5-5.5 5.5-8.5 0-3.9-3.9-5.7-5.5-9.5z"/><path d="M12 9.5c-1 1.6-2.6 2.6-2.6 4.2 0 1.4 1.2 2.4 2.6 3.9 1.4-1.5 2.6-2.5 2.6-3.9 0-1.6-1.6-2.6-2.6-4.2z" fill="var(--paper)"/><path d="M4.5 13.5c-2 1.2-3 3.6-1.8 6.3 1.3-1.9 3.2-3.2 5.2-3.5-1.4-.8-2.6-1.7-3.4-2.8z"/><path d="M19.5 13.5c2 1.2 3 3.6 1.8 6.3-1.3-1.9-3.2-3.2-5.2-3.5 1.4-.8 2.6-1.7 3.4-2.8z"/>',
        3: '<path d="M12 1.5l3 4.2-3 4.3-3-4.3z"/><path d="M12 10.5c-2.4 3.4-6.6 3.4-8 8.5 4.2-.3 7-2.2 8-5.2 1 3 3.8 4.9 8 5.2-1.4-5.1-5.6-5.1-8-8.5z"/><path d="M12 17.5v5" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>',
        4: '<path d="M12 1.5c-2.2 3.2-2.2 7.8 0 11 2.2-3.2 2.2-7.8 0-11z"/><path d="M5 8.5c.8 4.4 3.2 7.8 7.2 9.8-4.2.2-8.3-2-9.6-6.2.2-1.4 1.2-2.7 2.4-3.6z"/><path d="M19 8.5c-.8 4.4-3.2 7.8-7.2 9.8 4.2.2 8.3-2 9.6-6.2-.2-1.4-1.2-2.7-2.4-3.6z"/><path d="M12 14.5v8" stroke="currentColor" stroke-width="1.6" stroke-linecap="round"/>',
    }[n]
    return f'<svg viewBox="0 0 24 24" width="{size}" height="{size}" fill="{color}" style="color:{color};flex:none;{extra}" aria-hidden="true">{paths}</svg>'

# ---------- общие блоки ----------
def lang(on="TAT"):
    a = "on" if on == "TAT" else "off"; b = "on" if on == "RU" else "off"
    return f'<div class="lang" aria-label="Тел / Язык"><span class="{a}">TAT</span><span class="off">/</span><span class="{b}">RU</span></div>'

def topbar(left="back", right="", href="Library.dc.html", label="Артка"):
    if left == "back":
        l = f'<a class="iconbtn" href="{href}" aria-label="{label}">{ic("back")}</a>'
    elif left == "close":
        l = f'<a class="iconbtn" href="{href}" aria-label="Ябарга">{ic("close")}</a>'
    elif left == "sprout":
        l = f'<div class="iconbtn" style="color:var(--ink)">{ic("sprout", 28)}</div>'
    else:
        l = '<div style="width:48px"></div>'
    return f'<div class="top">{l}{right or "<div></div>"}</div>'

def cta(text, href, icon=None, extra=""):
    inner = icon if icon else orn(1, 16, "var(--on-cta)")
    return f'<a class="btn btn-cta {extra}" href="{href}"><span class="orb">{inner}</span><span>{text}</span></a>'

def sec(text, href, extra="btn-wide"):
    return f'<a class="btn btn-sec {extra}" href="{href}">{text}</a>'

def ghost(text, href, extra="btn-wide"):
    return f'<a class="btn btn-ghost {extra}" href="{href}">{text}</a>'

def lv(l): return f'<span class="lv lv-{l}">{l}</span>'

BOOKS = [  # id, title, author, level(text_complexity), genre(DB, ru), genre(tat ui), year, words, uniq, blocks
    (1, "Су анасы", "Габдулла Тукай", "B1", "сказка в стихах", "шигъри әкият", "1908", 413, 292, 28),
    (2, "Шүрәле", "Габдулла Тукай", "B1", "сказка в стихах", "шигъри әкият", "1907", 929, 623, 4),
    (3, "Нәҗип", "Фатих Әмирхан", "B1", "рассказ", "хикәя", "—", 1560, 731, 32),
    (4, "Алтын әтәч", "Габдулла Тукай", "B2", "сказка в стихах", "шигъри әкият", "1908", 1500, 845, 29),
]
WM_POS = {1: "bottom:-10px;left:-14px", 2: "top:50%;left:50%;transform:translate(-50%,-50%)", 3: "top:-8px;right:-12px", 4: "bottom:-6px;right:-10px"}
def cover(b, w=132, h=176, wm=96):
    bid, title, author, level = b[0], b[1], b[2], b[3]
    c = f"var(--{level.lower()}c)"
    size = "sm" if w < 80 else ("md" if w < 110 else "")
    return (f'<div class="cover {size}" style="width:{w}px;height:{h}px;background:{c};flex:none">'
            f'<span class="wm" style="{WM_POS[bid]}">{orn(4, wm)}</span>'
            f'<span class="ct">{title}</span><span class="ca">{author}</span></div>')

def book_card(b, href="BookDetail.dc.html"):
    return (f'<a href="{href}" style="display:flex;flex-direction:column;gap:8px;width:132px;flex:none">{cover(b)}'
            f'<span class="h2" style="font-size:18px;line-height:22px">{b[1]}</span>'
            f'<span class="b3 soft">{b[2]}</span>{lv(b[3])}</a>')

def nav(on):
    items = [("Library.dc.html", "book", "Китапханә", "lib"), ("Search.dc.html", "search", "Эзләү", "search"),
             ("ScannerStart.dc.html", "scan", "Скан", "scan"), ("Progress.dc.html", "chart", "Алгарыш", "prog")]
    out = '<nav class="nav" aria-label="Төп навигация">'
    for href, i, t, k in items:
        out += f'<a href="{href}" class="{"on" if k == on else ""}"><span class="pill">{ic(i, 22)}</span><span>{t}</span></a>'
    return out + "</nav>"

def photo_ph(h=380, note="фото: предоставить"):
    return (f'<div class="photo-ph" style="height:{h}px;width:390px;flex:none">{orn(3, 120, "var(--line)")}'
            f'<span class="ov">{note}</span></div>')

def phrase_card():
    return ('<div style="background:var(--peach);border-radius:24px;padding:20px 16px;display:flex;gap:10px;align-items:center;color:var(--ink)">'
            f'<span style="color:var(--sunset)">{orn(2, 22)}</span>'
            '<div style="flex:1;display:flex;flex-direction:column;gap:6px;text-align:center">'
            '<p class="display-i" style="font-size:20px;line-height:26px">Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр</p>'
            '<p class="ov" style="opacity:.8">Шүрәле · Габдулла Тукай, 1907</p></div>'
            f'<span style="color:var(--sunset);transform:scaleX(-1)">{orn(2, 22)}</span></div>')

def seg(on):
    parts = []
    for k, t in [("ru", "Русча"), ("ad", "Адаптация"), ("or", "Оригинал")]:
        knob = orn(1, 12, "var(--bg)") if k == on else ""
        parts.append(f'<span class="{"on" if k == on else ""}">{knob}{t}</span>')
    return f'<div class="seg" role="radiogroup" aria-label="Текст катламы">{"".join(parts)}</div>'

def wrap(title, body, w=390, h=844, dark=False, lang_code="tt"):
    cls = "tl dark" if dark else "tl"
    return f'''<!doctype html>
<html lang="{lang_code}">
<head>
<meta charset="utf-8">
<title>{title}</title>
<script src="./support.js"></script>
<link rel="stylesheet" href="tatlib.css">
</head>
<body>
<x-dc>
<helmet>
<style>
body{{margin:0;background:{"#14201B" if dark else "#F6F1E7"};font-family:"Golos Text","PT Sans",Arial,sans-serif;color:{"#F1EBDD" if dark else "#1D3128"}}}
a{{color:inherit}} a:hover{{color:inherit}}
</style>
</helmet>
<div class="{cls} screen" style="width: {w}px; height: {h}px; background: {"#14201B" if dark else "#F6F1E7"}; color: {"#F1EBDD" if dark else "#1D3128"};">
{body}
</div>
</x-dc>
<script type="text/x-dc" data-dc-script data-props='{{"$preview":{{"width":{w},"height":{h}}}}}'>
class Component extends DCLogic {{
  renderVals() {{ return {{}}; }}
}}
</script>
</body>
</html>
'''

boards = {}
def add(name, title, body, w=390, h=844, dark=False, interactive=True, lang_code="tt"):
    boards[name] = dict(title=title, html=wrap(title, body, w, h, dark, lang_code), w=w, h=h, interactive=interactive)

# ================= 1. Кереш =================
add("Main.dc.html", "Splash", f'''
<div style="flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:16px">
  <span style="color:var(--forest)">{ic("sprout", 56)}</span>
  <a class="h1" href="Welcome.dc.html" style="font-size:34px;line-height:40px">TatLib</a>
  <div style="width:28px;height:28px;border-radius:999px;border:3px solid var(--sand);border-top-color:var(--forest)" aria-label="Йөкләнә"></div>
</div>
<p class="display-i soft" style="font-size:20px;line-height:26px;text-align:center;padding:0 24px 48px">Татарча күбрәк</p>''')

def welcome(ru):
    h = "Изучай татарский легко" if ru else "Рәхим итегез!"
    s = "Язык объединяет людей и открывает новые горизонты" if ru else "Телне белү — дөньяны башкача күрү"
    b = "Начать" if ru else "Башлау"
    return f'''
{topbar("sprout", lang("RU" if ru else "TAT"))}
{photo_ph(470)}
<div class="body" style="justify-content:flex-end;gap:12px">
  <h1 class="display" style="text-wrap:balance">{h}</h1>
  <p class="b1 soft" style="margin-bottom:12px">{s}</p>
  <div>{cta(b, "AuthChoice.dc.html")}</div>
</div>'''
add("Welcome.dc.html", "Рәхим итегез", welcome(False))
add("WelcomeRu.dc.html", "Добро пожаловать (RU)", welcome(True), lang_code="ru")

add("AuthChoice.dc.html", "Керү яки теркәлү", f'''
{topbar("back", "", "Welcome.dc.html")}
<div class="body" style="justify-content:center;gap:12px">
  <h1 class="h1" style="text-wrap:balance">Сездә исәп язмасы бармы?</h1>
  <p class="b1 soft" style="margin-bottom:28px">Дәвам итү өчен керегез яки яңа хисап ачыгыз.</p>
  {sec("Керү", "Login.dc.html")}
  {ghost("Теркәлү", "Register.dc.html")}
</div>''')

# ================= 2. Регистрация / вход =================
def field(label, value="", placeholder="", ftype="text", focus=False, err=""):
    inner = f'<span>{value}</span>' if value else f'<span class="muted">{placeholder}</span>'
    cls = "field focus" if focus else "field"
    e = f'<p class="b3" style="color:var(--error);margin-top:6px">{err}</p>' if err else ""
    return f'<label style="display:block"><span class="field-lbl">{label}</span><div class="{cls}">{inner}</div>{e}</label>'

add("Register.dc.html", "Теркәлү", f'''
{topbar("back", "", "AuthChoice.dc.html")}
<div class="body" style="gap:14px">
  <h1 class="h1" style="margin-bottom:6px">Теркәлү</h1>
  {field("Исем", placeholder="Исемегез")}
  {field("Телефон номеры / электрон почта", placeholder="+7 ··· ··· ·· ··")}
  {field("Серсүз", "••••••••")}
  {field("Серсүзне кабатлагыз", "•••••••", err="Серсүзләр туры килми")}
  <p class="t2" style="margin-top:6px">Сезнең яшь</p>
  <div style="display:flex;gap:8px"><span class="chip">10–14</span><span class="chip">14–18</span><span class="chip on">18–25</span><span class="chip">25+</span></div>
  <div style="flex:1"></div>
  {sec("Теркәлү", "LevelIntro.dc.html")}
</div>''')

add("Login.dc.html", "Керү", f'''
{topbar("sprout", lang("TAT"))}
<div class="body" style="gap:14px;justify-content:center">
  <h1 class="h1" style="margin-bottom:6px">Керү</h1>
  {field("Логин", placeholder="Телефон яки электрон почта", focus=True)}
  {field("Серсүз", "••••••••")}
  <div style="height:8px"></div>
  {sec("Керү", "LevelIntro.dc.html")}
  {ghost("Исәп язмасы юк — теркәлү", "Register.dc.html")}
</div>''')

# ================= 3. Тест уровня =================
add("LevelIntro.dc.html", "Дәрәҗә тесты", f'''
{topbar("back", "", "AuthChoice.dc.html")}
{photo_ph(430)}
<div class="body" style="justify-content:flex-end;gap:12px">
  <h1 class="h1" style="text-wrap:balance">Сезнең дәрәҗәгезне билгелибезме?</h1>
  <p class="b1 soft" style="margin-bottom:12px">Татар телен ни дәрәҗәдә белүегезне ачыклагыз.</p>
  <div style="display:flex;flex-direction:column;gap:4px;align-items:flex-start">{cta("Тест узарга", "Quiz.dc.html")}{ghost("Үзем күрсәтермен", "LevelResult.dc.html", "")}</div>
</div>''')

def opt(text, on=False):
    st = "background:var(--ink);color:var(--bg)" if on else "background:var(--sand)"
    return f'<button type="button" class="b1" style="{st};border-radius:16px;min-height:56px;padding:14px 18px;text-align:left;display:flex;align-items:center;justify-content:space-between">{text}{ic("check", 20) if on else ""}</button>'
add("Quiz.dc.html", "Тест 1/5", f'''
{topbar("back", "", "LevelIntro.dc.html")}
<div class="body" style="gap:20px">
  <h1 class="h1">Дәрәҗәгезне билгелик</h1>
  <div style="display:flex;flex-direction:column;gap:8px"><div style="display:flex;justify-content:space-between" class="b3 muted"><span>Сорау 1 / 5</span><span>20 %</span></div><div class="bar"><i style="width:20%"></i></div></div>
  <h2 class="h2" lang="ru">Как правильно сказать «Я читаю книгу»?</h2>
  <div style="display:flex;flex-direction:column;gap:12px">{opt("Мин китап укыйм", True)}{opt("Мин китап укыдым")}{opt("Мин китап укыячакмын")}{opt("Мин китап укымыйм")}</div>
  <div style="flex:1"></div>
  {sec("Алга", "LevelResult.dc.html")}
</div>''')

def level_row(sel="B1"):
    out = '<div style="display:flex;justify-content:space-between;align-items:center;padding:0 4px">'
    for l in ["A1", "A2", "B1", "B2", "C1"]:
        if l == sel:
            out += f'<span style="width:56px;height:56px;border-radius:999px;background:var(--ink);color:var(--bg);display:flex;align-items:center;justify-content:center;font-weight:600;font-size:18px">{l}</span>'
        else:
            out += f'<span class="lv lv-{l}" style="height:40px;padding:0 14px;font-size:15px">{l}</span>'
    return out + "</div>"
add("LevelResult.dc.html", "Дәрәҗә нәтиҗәсе", f'''
{topbar("back", "", "Quiz.dc.html")}
<div class="body" style="justify-content:center;gap:24px">
  <h1 class="h1" style="text-wrap:balance">Сезнең татар теле дәрәҗәсе</h1>
  {level_row("B1")}
  <p class="b2 soft">Дәрәҗәгезне төзәтергә телисез икән, теләгән хәрефкә басыгыз.</p>
  <div style="flex:0 0 60px"></div>
  <div style="display:flex;flex-direction:column;gap:4px;align-items:flex-start">{cta("Дәвам итү", "Library.dc.html")}{ghost("Татар телен белмим", "Library.dc.html", "")}</div>
</div>''')

# ================= 4. Главная =================
shurale = BOOKS[1]
add("Library.dc.html", "Китапханә", f'''
<div class="top" style="height:auto;padding:20px 24px 0;align-items:flex-start">
  <div><h1 class="display">Хәерле көн!</h1><p class="b1 soft">Укуны дәвам итәбезме?</p></div>{lang("TAT")}
</div>
<div class="body" style="padding-top:36px;gap:28px;overflow:hidden">
  <div class="card-sand" style="position:relative;display:flex;flex-direction:column;gap:6px;padding-right:130px">
    <div style="position:absolute;top:-24px;right:-12px">{cover(shurale, 96, 128, 72)}</div>
    <span class="ov soft">Киләсе адым</span>
    <span class="h2">Шүрәле</span>
    <span class="b2 soft">Габдулла Тукай · 1907</span>
    <div style="display:flex;gap:8px;align-items:center;margin-top:4px">{lv("B1")}<span class="b3 muted">Катлам: Оригинал</span></div>
    <div style="margin-top:12px">{cta("Укырга", "Reader.dc.html")}</div>
  </div>
  <section style="display:flex;flex-direction:column;gap:12px">
    <div style="display:flex;justify-content:space-between;align-items:baseline"><h2 class="t1">Минем китапларым</h2><a class="b2" href="Search.dc.html" style="color:var(--cta-deep)">Барысын карау</a></div>
    <div style="display:flex;gap:14px;width:342px;overflow:hidden">{"".join(book_card(b) for b in BOOKS)}</div>
  </section>
  <section style="display:flex;flex-direction:column;gap:12px">
    <div style="display:flex;justify-content:space-between;align-items:baseline"><h2 class="t1">Тәкъдим итәбез</h2><a class="b2" href="Search.dc.html" style="color:var(--cta-deep)">Барысын карау</a></div>
    <div style="display:flex;gap:14px;width:342px;overflow:hidden">{"".join(book_card(b) for b in reversed(BOOKS))}</div>
  </section>
</div>
{nav("lib")}''')

# ================= 5. Поиск =================
def row(b):
    return (f'<a href="BookDetail.dc.html" style="display:flex;gap:14px;align-items:center">{cover(b, 56, 76, 40)}'
            f'<div style="display:flex;flex-direction:column;gap:2px"><span class="t2">{b[1]}</span><span class="b3 soft">{b[2]}</span>'
            f'<span class="b3 muted" style="margin-top:4px">{b[3]} · {b[5]} · {b[6]}</span></div></a>')
add("Search.dc.html", "Эзләү", f'''
<div class="body" style="padding-top:24px;gap:16px">
  <h1 class="h1">Эзләү</h1>
  <div class="field" style="height:52px;border-color:var(--line)">{ic("search", 22)}<span class="muted b1">Китап, автор яки тема буенча эзләгез…</span></div>
  <div style="display:flex;gap:8px"><span class="chip on">Барысы</span><span class="chip">Китаплар</span><span class="chip">Авторлар</span><span class="chip">Темалар</span></div>
  <div style="display:flex;flex-direction:column;gap:14px;margin-top:4px">{"".join(row(b) for b in BOOKS)}</div>
  <div style="flex:1"></div>
  {phrase_card()}
</div>
{nav("search")}''')

# ================= 6. Детали книги =================
def stat(n, l):
    return f'<div style="display:flex;flex-direction:column;gap:2px"><span class="h2" style="font-variant-numeric:tabular-nums">{n}</span><span class="ov muted">{l}</span></div>'
add("BookDetail.dc.html", "Шүрәле", f'''
{topbar("back", "", "Library.dc.html")}
<div class="body" style="gap:20px">
  <div style="display:flex;gap:16px;align-items:flex-end">{cover(shurale, 118, 158, 88)}
    <div style="display:flex;flex-direction:column;gap:6px"><h1 class="h1">Шүрәле</h1><span class="b1 soft">Габдулла Тукай</span>
      <div style="display:flex;gap:6px;flex-wrap:wrap;margin-top:4px">{lv("B1")}<span class="chip" style="height:28px;font-size:13px">шигъри әкият</span><span class="chip" style="height:28px;font-size:13px">1907</span></div></div>
  </div>
  <div>{cta("Укырга", "Reader.dc.html")}</div>
  <div style="display:flex;justify-content:space-between;padding:4px 0">{stat("929", "сүз")}{stat("623", "уникаль сүз")}{stat("4", "бүлек")}</div>
  <div class="divider">{orn(3, 16)}</div>
  <section style="display:flex;flex-direction:column;gap:8px">
    <h2 class="t1">Китап турында</h2>
    <p class="b1">Шагыйрь Казан артындагы Кырлай авылы турында сөйли. Авыл кечкенә, ләкин урманы һәм болыннары бик матур.</p>
    <p class="b3 muted">Беренче бүлекнең адаптацияләнгән тексты · Чыганак: speak.tatar, gabdullatukay.ru</p>
  </section>
</div>''')

# ================= 7. Ридер =================
ORIG = [
 "Нәкъ Казан артында бардыр бер авыл —", "«Кырлай» диләр;",
 "<span class=\"u-B2\">Җырлаганда</span> көй өчен, «<span class=\"u-B2\">тавыклары</span> җырлай», диләр.",
 "Гәрчә анда <span class=\"u-B2\">тугъмасам</span> да, мин бераз торган идем;",
 "Җирне әз-мәз тырмалап, чәчкән идем, урган идем.",
 "Ул авылның, һич онытмыйм, {W} урман иде,",
 "Ул болын, яшел үләннәр хәтфәдән юрган иде.",
 "Зурмы, дисәң, зур түгелдер, бу авыл бик кечкенә;",
 "Халкының эчкән суы бик кечкенә — инеш кенә.",
 "Анда бик салкын вә бик эссе түгел, урта һава;",
 "Җил дә вактында исеп, яңгыр да вактында ява.",
 "<span class=\"u-B2\">Урманында</span> кып-кызыл кура җиләк тә җир җиләк;",
 "Күз ачып йомганчы, <span class=\"u-B2\">һичшиксез</span>, җыярсың бер чиләк.",
 "Бик хозур! Рәт-рәт тора, гаскәр кеби, чыршы, нарат;",
 "<span class=\"u-B2\">Төпләрендә</span> ятканым бар, хәл җыеп, күккә карап.",
 "Юкә, каеннар төбендә <span class=\"u-B2\">кузгалаклар</span>, гөмбәләр",
 "Берлә бергә үсә аллы-гөлле гөлләр, гонҗәләр.",
]
def reader_body(layer="or", word=None, dark=False):
    if layer == "or":
        lines = [l.replace("{W}", ('<span class="hl">һәрьягы</span>' if word else "һәрьягы")).replace(" —", "&nbsp;—") for l in ORIG]
        text = "\n".join(lines[:6] if word else lines)
        lbl = "Оригинал"
    elif layer == "ad":
        text = "Шагыйрь Казан артындагы Кырлай авылы турында сөйли. Авыл кечкенә, ләкин урманы һәм болыннары бик матур."; lbl = "Адаптация"
    else:
        text = "Поэт рассказывает о деревне Кырлай недалеко от Казани. Деревня небольшая, но её леса и луга очень красивы."; lbl = "Русча"
    popup = ""
    if word:
        rest = "\n".join(l.replace(" —", "&nbsp;—") for l in ORIG[6:])
        popup = f'''<div class="card" style="box-shadow:var(--shadow);border:1px solid var(--line);display:flex;flex-direction:column;gap:6px;margin-top:8px">
      <div style="display:flex;align-items:center;gap:10px"><span class="h2" style="font-size:24px">һәрьягы</span>{lv("B1")}</div>
      <p class="b1" lang="ru">со всех сторон, вокруг</p>
      <p class="b3 muted">Ул авылның һәрьягы урман иде.</p>
      <div style="display:flex;justify-content:flex-end">{ghost("Ябарга", "Reader.dc.html", "")}</div></div>
    <p class="rtext" style="margin-top:8px">{rest}</p>'''
    return f'''
<div class="top">
  <a class="iconbtn" href="BookDetail.dc.html" aria-label="Артка">{ic("back")}</a>
  <span class="t2">Шүрәле</span>
  <button type="button" class="iconbtn" aria-label="Шрифт көйләүләре">{ic("aa")}</button>
</div>
<div class="body" style="gap:14px;padding-top:0">
  {seg(layer)}
  <span class="ov muted">{lbl} · I бүлек · Шүрәле, 1907</span>
  <div style="flex:1;min-height:0;overflow:hidden;display:flex;flex-direction:column;gap:4px">
    <p class="rtext" style="{"font-size:19px" if layer != "or" else ""}">{text}</p>{popup}
  </div>
  <div style="display:flex;flex-direction:column;gap:8px">
    <div class="bar"><i style="width:100%"></i></div>
    <div style="display:flex;justify-content:space-between;align-items:center" class="b3 muted"><span>← Артка</span><span>1 бүлек · 929 сүз</span><span>Алга →</span></div>
  </div>
</div>'''
add("Reader.dc.html", "Ридер · Оригинал", reader_body("or"))
add("ReaderWord.dc.html", "Ридер · Сүз", reader_body("or", word=True))
add("ReaderDark.dc.html", "Ридер · Караңгы", reader_body("ad"), dark=True)

# ================= 8. Recap =================
add("Recap.dc.html", "Сессия нәтиҗәсе", f'''
<div class="top"><a class="iconbtn" href="Library.dc.html" aria-label="Ябарга">{ic("close")}</a><a class="btn btn-sec" href="Reader.dc.html" style="height:40px;padding:0 18px">{orn(1, 14, "var(--bg)")}<span style="margin-left:8px">Укырга</span></a></div>
<div class="body" style="justify-content:center;gap:28px">
  <h1 class="h1" style="text-wrap:balance">Һәр укылган бит сине оригиналга якынайта!</h1>
  <div style="display:flex;justify-content:space-between">{stat("—", "бит")}{stat("—", "яңа сүз")}{stat("—", "оригинал")}</div>
  <div class="card-sand" style="display:flex;flex-direction:column;align-items:center;gap:10px;text-align:center;padding:28px 20px">
    {orn(2, 72, "var(--line)")}
    <span class="t1">Беренче сессия әле язылмаган</span>
    <span class="b2 soft">Укуны тәмамлагач, битләр һәм яңа сүзләр монда күренер.</span>
  </div>
  <div style="flex:0 0 40px"></div>
  {ghost("Китапханәгә кайту", "Library.dc.html")}
</div>''')

# ================= 9. Прогресс =================
DAYS = ["Дш", "Сш", "Чш", "Пҗ", "Җм", "Шм", "Як"]
def ring():
    petals = "".join(f'<span style="position:absolute;left:50%;top:50%;transform:translate(-50%,-50%) rotate({i*30}deg) translateY(-62px)">{orn(4, 14, "var(--line)")}</span>' for i in range(12))
    return f'''<div class="ring" style="background:conic-gradient(var(--forest) 0 50%, var(--sand) 50% 100%)">{petals}
      <div style="width:100px;height:100px;border-radius:999px;background:var(--bg);display:flex;align-items:center;justify-content:center"><span class="display-i">B1</span></div></div>'''
add("Progress.dc.html", "Алгарышың", f'''
<div class="top" style="padding:12px 24px 0;height:auto;align-items:center"><h1 class="h1">Алгарышың</h1><a class="iconbtn" href="Profile.dc.html" aria-label="Профиль" style="background:var(--sage-c);color:var(--ink)">{ic("person", 22)}</a></div>
<div class="body" style="gap:16px;overflow:hidden">
  <div style="display:flex;gap:8px"><span class="chip on">Атна</span><span class="chip">Ай</span><span class="chip">Ел</span></div>
  <div class="card" style="display:flex;gap:16px;align-items:center;border:1px solid var(--line)">{ring()}
    <div style="display:flex;flex-direction:column;gap:6px"><span class="ov muted">Хәзерге дәрәҗәң</span><span class="t1">B1 · Урта</span><span class="b2 soft">Тагын бераз — һәм B2 дәрәҗәсен сынап карарга була.</span></div></div>
  <div style="display:flex;justify-content:space-between;padding:0 4px">{stat("4", "тәрҗемә")}{stat("4", "уникаль сүз")}{stat("1", "актив көн")}</div>
  <div style="display:flex;flex-direction:column;gap:8px"><span class="t2">Бу атна</span>
    <div class="waffle">{"".join('<i class="on"></i>' if i == 6 else "<i></i>" for i in range(7))}</div>
    <div style="display:grid;grid-template-columns:repeat(7,24px);gap:4px" class="b3 muted">{"".join(f"<span style='text-align:center'>{d}</span>" for d in DAYS)}</div></div>
  <div style="border:1.5px dashed var(--line);border-radius:20px;padding:20px;display:flex;flex-direction:column;align-items:center;gap:6px;text-align:center">
    <span class="t2 soft">Әлегә мәгълүмат юк</span><span class="b3 muted">Дәрәҗә тарихы икенче тесттан соң языла.</span></div>
  <div style="display:flex;flex-direction:column;gap:8px"><span class="t2">Соңгы тәрҗемәләр</span>
    <div style="display:flex;gap:8px;flex-wrap:wrap"><span class="chip">көне</span><span class="chip">Эссе</span><span class="chip">һавада</span><span class="b3 muted" style="align-self:center">· Су анасы</span></div></div>
</div>
{nav("prog")}''')

# ================= 10. Сканер =================
def viewfinder(inner=""):
    c = 'position:absolute;width:28px;height:28px;border:3px solid var(--ink);'
    return f'''<div style="position:relative;height:380px;border-radius:24px;background:var(--sand);display:flex;align-items:center;justify-content:center">
      <span style="{c}top:20px;left:20px;border-right:0;border-bottom:0;border-radius:8px 0 0 0"></span>
      <span style="{c}top:20px;right:20px;border-left:0;border-bottom:0;border-radius:0 8px 0 0"></span>
      <span style="{c}bottom:20px;left:20px;border-right:0;border-top:0;border-radius:0 0 0 8px"></span>
      <span style="{c}bottom:20px;right:20px;border-left:0;border-top:0;border-radius:0 0 8px 0"></span>
      {inner or f'<span class="ov muted">камера</span>'}</div>'''
scan_top = f'<div class="top"><a class="iconbtn" href="Library.dc.html" aria-label="Ябарга">{ic("close")}</a><button type="button" class="iconbtn" aria-label="Галерея">{ic("image")}</button></div>'
add("ScannerStart.dc.html", "Сканер", f'''
{scan_top}
<div class="body" style="gap:20px;padding-top:0">
  <h1 class="t1" style="text-align:center;text-wrap:balance">Камераны китапка яки биткә юнәлтегез</h1>
  {viewfinder()}
  <p class="b2 soft" style="text-align:center">Без текстны табып, аны уку өчен әзерләячәкбез.</p>
  <div style="flex:1"></div>
  <div style="display:flex;gap:12px;align-items:center;justify-content:center">{cta("Камера", "ScannerBusy.dc.html", ic("camera", 18, "ic"))}{ghost("Галерея", "ScannerBusy.dc.html", "")}</div>
</div>
{nav("scan")}''')
add("ScannerBusy.dc.html", "Сканер · тану", f'''
{scan_top}
<div class="body" style="gap:20px;padding-top:0">
  <h1 class="t1" style="text-align:center">Текст таныла…</h1>
  {viewfinder('<div style="display:flex;flex-direction:column;align-items:center;gap:12px"><div style="width:36px;height:36px;border-radius:999px;border:3px solid var(--paper);border-top-color:var(--forest)"></div><span class="b3 muted">tat_cyrl · Tesseract</span></div>')}
  <div style="height:4px;border-radius:2px;background:var(--sand);overflow:hidden"><i style="display:block;width:55%;height:100%;background:var(--sky)"></i></div>
  <p class="b2 soft" style="text-align:center">Бит базадагы дүрт китап белән чагыштырыла.</p>
  <div style="flex:1"></div>
  <a class="btn btn-ghost btn-wide" href="ScannerFound.dc.html">Туктату</a>
</div>
{nav("scan")}''')
sua = BOOKS[0]
add("ScannerFound.dc.html", "Сканер · табылды", f'''
{scan_top}
<div class="body" style="gap:16px;padding-top:0">
  <div class="card" style="border:1px solid var(--line);display:flex;gap:14px;align-items:center">{cover(sua, 72, 96, 56)}
    <div style="display:flex;flex-direction:column;gap:4px"><span class="ov" style="color:var(--forest)">Китап табылды</span><span class="h2">Су анасы</span><span class="b2 soft">Габдулла Тукай · 1908</span><div>{lv("B1")}</div></div></div>
  <div>{cta("Укырга", "Reader.dc.html")}</div>
  <div class="divider">{orn(3, 16)}</div>
  <span class="t2">Танылган текст</span>
  <div class="seg" role="radiogroup" aria-label="Тел"><span class="on">{orn(1, 12, "var(--bg)")}Татарча</span><span>Русча</span></div>
  <p class="rtext" style="font-size:17px">Җәй көне. Эссе һавада мин суда койнам, йөзәм;
Чәчрәтәм, уйныйм, чумам, башым белән суны сөзәм.</p>
  <p class="b3 muted">Ышанычлылык: OCR нәтиҗәсе · чагыштыру ≥ 30 % сүз туры килү</p>
</div>
{nav("scan")}''')

# ================= 11. Профиль и настройки =================
def srow(label, control, sub=""):
    s = f'<span class="b3 muted">{sub}</span>' if sub else ""
    return f'<div style="display:flex;justify-content:space-between;align-items:center;gap:12px;min-height:52px"><div style="display:flex;flex-direction:column;gap:2px"><span class="b1">{label}</span>{s}</div>{control}</div>'
add("Profile.dc.html", "Профиль", f'''
<div class="top"><a class="iconbtn" href="Progress.dc.html" aria-label="Артка">{ic("back")}</a><h1 class="t1">Профиль</h1><div style="width:48px"></div></div>
<div class="body" style="gap:20px">
  <div style="display:flex;gap:16px;align-items:center">
    <span style="width:64px;height:64px;border-radius:999px;background:var(--sage-c);display:flex;align-items:center;justify-content:center;color:var(--forest)">{ic("sprout", 32)}</span>
    <div style="display:flex;flex-direction:column;gap:4px"><span class="h2">Укучы</span><div style="display:flex;gap:8px;align-items:center">{lv("B1")}<span class="b3 muted">18 яшь</span></div></div>
  </div>
  <div class="divider">{orn(3, 16)}</div>
  <div class="card" style="border:1px solid var(--line);display:flex;flex-direction:column;gap:4px">
    {srow("Интерфейс теле", lang("TAT"), "Татарча төп, русча күчерү")}
    {srow("Тема", '<div style="display:flex;gap:6px"><span class="chip on" style="height:32px;font-size:13px">Система</span><span class="chip" style="height:32px;font-size:13px">Якты</span><span class="chip" style="height:32px;font-size:13px">Караңгы</span></div>')}
    {srow("Ридер шрифты", '<div style="display:flex;gap:6px"><span class="chip on" style="height:32px;font-size:13px;font-family:var(--reader)">Literata</span><span class="chip" style="height:32px;font-size:13px">Golos</span></div>')}
    <div style="display:flex;flex-direction:column;gap:8px;padding:8px 0"><span class="b1">Шрифт зурлыгы</span>
      <div style="display:flex;align-items:center;gap:12px"><span class="b3">Аа</span><div style="flex:1;height:4px;border-radius:2px;background:var(--sand);position:relative"><i style="position:absolute;left:0;top:0;height:100%;width:50%;background:var(--forest);border-radius:2px"></i><i style="position:absolute;left:50%;top:-8px;width:20px;height:20px;border-radius:999px;background:var(--forest);transform:translateX(-50%)"></i></div><span class="t1" style="font-size:20px">Аа</span></div>
      <span class="b3 muted">18 sp · 16–20</span></div>
    {srow("Сүз асты сызыгы", '<span class="toggle on" role="switch" aria-checked="true"><i></i></span>', "Дәрәҗәдән югары сүзләрне билгеләү")}
  </div>
  <div style="flex:1"></div>
  <a class="btn btn-ghost btn-wide" href="Welcome.dc.html">{ic("logout", 20)}<span style="margin-left:8px">Чыгу</span></a>
</div>
{nav("prog")}''')

# ================= 12. Компоненты =================
def sw(name, var, txt="var(--ink)"):
    return f'<div style="display:flex;flex-direction:column;gap:6px;width:96px"><div style="height:56px;border-radius:12px;background:{var};border:1px solid var(--line)"></div><span class="b3" style="color:{txt}">{name}</span></div>'
def components(dark):
    swatches = "".join(sw(n, f"var(--{v})") for n, v in [("bg", "bg"), ("paper", "paper"), ("sand", "sand"), ("ink", "ink"), ("forest", "forest"), ("sage", "sage"), ("cta", "cta"), ("sunset", "sunset"), ("peach", "peach"), ("sky", "sky"), ("error", "error")])
    levels = "".join(f'<div style="display:flex;align-items:center;gap:8px">{lv(l)}<span class="dot dot-{l}"></span></div>' for l in ["A1", "A2", "B1", "B2", "C1"])
    covers = "".join(cover(b) for b in BOOKS)
    ornaments = "".join(f'<div style="display:flex;flex-direction:column;align-items:center;gap:6px"><span style="color:var(--ink)">{orn(i, 48)}</span><span class="b3 muted">мотив {i}</span></div>' for i in (1, 2, 3, 4))
    sect = lambda t, inner: f'<section style="display:flex;flex-direction:column;gap:12px"><h2 class="ov muted">{t}</h2>{inner}</section>'
    return f'''
<div style="padding:32px;display:flex;flex-direction:column;gap:28px;overflow:hidden;height:100%">
  <div><h1 class="h1">TatLib · компонентлар {"· караңгы тема" if dark else "· якты тема"}</h1><p class="b2 soft">MASTER.md § 7 · 20.09.2026</p></div>
  {sect("Шрифтлар һәм хәрефләр ә ө ү җ ң һ", '<div style="display:flex;flex-direction:column;gap:10px"><p class="display">Рәхим итегез! Әә Өө Үү Җҗ Ңң Һһ</p><p class="display-i">Татарча күбрәк — Әә Өө Үү Җҗ Ңң Һһ</p><p class="t1">Golos Text 600 · Китапханә · Эзләү · Әә Өө Үү Җҗ Ңң Һһ</p><p class="b1">Golos Text 400 · Телне белү — дөньяны башкача күрү · Әә Өө Үү Җҗ Ңң Һһ</p><p class="rtext">Literata 18 · Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр. Әә Өө Үү Җҗ Ңң Һһ</p><p class="rtext" style="font-style:italic">Literata Italic · Җырлаганда көй өчен, «тавыклары җырлай», диләр.</p></div>')}
  {sect("Палитра", f'<div style="display:flex;gap:10px;flex-wrap:wrap">{swatches}</div>')}
  {sect("Кнопкалар", f'<div style="display:flex;gap:12px;align-items:center;flex-wrap:wrap">{cta("Башлау", "#")}{sec("Керү", "#", "")}{ghost("Теркәлү", "#", "")}<a class="btn btn-sec" href="#" style="opacity:.38">Disabled</a></div>')}
  {sect("Чиплар һәм дәрәҗәләр", f'<div style="display:flex;gap:16px;align-items:center;flex-wrap:wrap"><span class="chip on">Барысы</span><span class="chip">Китаплар</span>{levels}</div>')}
  {sect("Кертү кыры", f'<div style="display:flex;gap:16px;flex-wrap:wrap;width:600px">{field("Исем", placeholder="Исемегез")}{field("Логин", placeholder="Телефон яки электрон почта", focus=True)}{field("Серсүз", "•••", err="Серсүзләр туры килми")}</div>')}
  {sect("Катлам күчергече һәм сүз", f'<div style="display:flex;gap:24px;align-items:flex-start"><div style="width:342px">{seg("or")}</div><div class="card" style="width:300px;box-shadow:var(--shadow);border:1px solid var(--line);display:flex;flex-direction:column;gap:6px"><div style="display:flex;align-items:center;gap:10px"><span class="h2" style="font-size:24px">һәрьягы</span>{lv("B1")}</div><p class="b1" lang="ru">со всех сторон, вокруг</p></div></div>')}
  {sect("Прогресс, кольцо, атна", f'<div style="display:flex;gap:32px;align-items:center"><div style="width:240px"><div class="bar"><i style="width:34%"></i></div></div>{ring()}<div class="waffle"><i class="on"></i><i class="on"></i><i></i><i class="on"></i><i></i><i></i><i class="on"></i></div><span class="toggle on"><i></i></span></div>')}
  {sect("Тышлыклар (фотосыз) һәм орнаментлар", f'<div style="display:flex;gap:16px;align-items:flex-end">{covers}<div style="display:flex;gap:20px;margin-left:24px">{ornaments}</div></div>')}
  {sect("Нижняя навигация", f'<div style="width:390px;border:1px solid var(--line);border-radius:12px;overflow:hidden">{nav("lib")}</div>')}
</div>'''
add("Components.dc.html", "Компонентлар · якты", components(False), w=1000, h=1560, interactive=False)
add("ComponentsDark.dc.html", "Компонентлар · караңгы", components(True), w=1000, h=1560, dark=True, interactive=False)

# ================= canvas.json =================
rows = [
    ("1 · Кереш: Splash, Welcome (TAT / RU), выбор входа", ["Main.dc.html", "Welcome.dc.html", "WelcomeRu.dc.html", "AuthChoice.dc.html"]),
    ("2 · Теркәлү һәм керү", ["Register.dc.html", "Login.dc.html"]),
    ("3 · Дәрәҗә тесты: интро, сорау 1/5, нәтиҗә", ["LevelIntro.dc.html", "Quiz.dc.html", "LevelResult.dc.html"]),
    ("4–6 · Китапханә · Эзләү · Китап", ["Library.dc.html", "Search.dc.html", "BookDetail.dc.html"]),
    ("7–8 · Ридер (оригинал, сүз, караңгы) · Recap", ["Reader.dc.html", "ReaderWord.dc.html", "ReaderDark.dc.html", "Recap.dc.html"]),
    ("9–11 · Алгарышың · Сканер ×3 · Профиль", ["Progress.dc.html", "ScannerStart.dc.html", "ScannerBusy.dc.html", "ScannerFound.dc.html", "Profile.dc.html"]),
    ("12 · Компонентлар: якты һәм караңгы тема", ["Components.dc.html", "ComponentsDark.dc.html"]),
]
canvas = {"v": 3, "createdOnFiles": {"v": 1, "at": datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")},
          "title": "TatLib — экраны", "launch": {"view": "canvas"}, "pages": [], "boards": {}, "order": [], "notes": {}, "designSystems": []}
y = 0
for i, (title, names) in enumerate(rows):
    x = 0; rw = 0
    for n in names:
        b = boards[n]
        e = {"x": x, "y": y, "w": b["w"], "h": b["h"], "title": b["title"]}
        if b["interactive"]: e["is_interactive"] = True
        canvas["boards"][n] = e; canvas["order"].append(n)
        x += b["w"] + 80; rw = x - 80
    canvas["notes"][f"row{i+1}"] = {"x": 0, "y": y - 260, "text": title, "kind": "title1", "maxW": max(rw, 1400)}
    y += max(b["h"] for b in (boards[n] for n in names)) + 420

for n, b in boards.items():
    with open(os.path.join(ROOT, n), "w", encoding="utf-8") as f: f.write(b["html"])
with open(os.path.join(ROOT, "canvas.json"), "w", encoding="utf-8") as f: json.dump(canvas, f, ensure_ascii=False, indent=1)
print(len(boards), "artboards;", "files:", sorted(os.listdir(ROOT)))
