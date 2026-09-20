# -*- coding: utf-8 -*-
"""TatLib — артборды v2: фото Казани, стеклянные формы, узоры из Figma, подача как у музыкальных приложений."""
import json, os, datetime
ROOT = os.path.join(os.path.dirname(__file__), "project")
os.makedirs(ROOT, exist_ok=True)

IMG = {
 "cover1": "/_blob/a86ae6ac98f3c54733b1e3c1ed64d7dd", "cover2": "/_blob/50118ecb8fc3f640e5dba5ef4f27c7e9", "cover3": "/_blob/3f8b9977a3946a889a3d102184e8bfbd",
 "blossom": "/_blob/3c915c13b70cf82c38de75f63d0c5da8", "sunset": "/_blob/cf0a6beb7e5cc640bf14c4d1c0b70eb0",
 "morning": "/_blob/94d55e1c0f1f9bf7e01058a13c463c2a", "day": "/_blob/260f1c0cadb84b5a32c7b99cab409e85", "evening": "/_blob/3f1ab79bc7ba5cf13b4a41d2c1538b16",
 "arch": "/_blob/9e80bf9a6822aa619180c9c75d38f5fd", "kremlin": "/_blob/46e1cb74bef3c9db10591ffd009b41d5",
 "meadow": "/_blob/2af7724fe200746834e39f6ca966d17a", "book": "/_blob/1f5bf5039b48012769b96c0f820eb9e8", "cup": "/_blob/b6eb228ba5a599be9ad05a229aff8b79", "domes": "/_blob/9e66ad665fa54d91ac0216e3d0f76f89",
}

# ---------- иконки ----------
def ic(name, size=24, cls="ic"):
    p = {
        "back": '<path d="M15 5l-7 7 7 7"/>', "chev": '<path d="M9 5l7 7-7 7"/>', "close": '<path d="M6 6l12 12M18 6L6 18"/>',
        "book": '<path d="M4 5.5A2.5 2.5 0 016.5 3H20v15H6.5A2.5 2.5 0 004 20.5z"/><path d="M4 20.5A2.5 2.5 0 016.5 18H20"/>',
        "search": '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
        "scan": '<path d="M4 8V5.5A1.5 1.5 0 015.5 4H8M16 4h2.5A1.5 1.5 0 0120 5.5V8M20 16v2.5a1.5 1.5 0 01-1.5 1.5H16M8 20H5.5A1.5 1.5 0 014 18.5V16"/><path d="M7 12h10"/>',
        "chart": '<path d="M4 19h16"/><path d="M7 15v-4M12 15V7M17 15v-6"/>',
        "person": '<circle cx="12" cy="8" r="4"/><path d="M4 20c1.5-3.5 4.5-5 8-5s6.5 1.5 8 5"/>',
        "mail": '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/>',
        "lock": '<rect x="5" y="10" width="14" height="10" rx="2"/><path d="M8 10V7a4 4 0 018 0v3"/>',
        "eye-off": '<path d="M3 3l18 18M10.5 10.6A2 2 0 0013.4 13.5M9.9 5.1A9.5 9.5 0 0112 5c5 0 8.5 4 9.5 7-.4 1.1-1.1 2.3-2 3.3M6.6 6.6C4.6 8 3.2 10 2.5 12c1 3 4.5 7 9.5 7 1.7 0 3.2-.4 4.5-1.1"/>',
        "image": '<rect x="3" y="4" width="18" height="16" rx="2"/><circle cx="9" cy="10" r="1.6"/><path d="M21 16l-5-5-8 8"/>',
        "camera": '<path d="M4 8h3l2-3h6l2 3h3v11H4z"/><circle cx="12" cy="13" r="3.5"/>',
        "aa": '<path d="M3 17l4-10 4 10M4.6 13.5h4.8"/><path d="M14 17v-6.5M14 13.5c0-1.7 1.3-3 3-3s3 1.3 3 3v3.5M20 15.5c0 1-1.3 1.8-3 1.8s-3-.8-3-1.8 1.3-1.8 3-1.8 3 .8 3 1.8"/>',
        "check": '<path d="M5 12l5 5 9-10"/>', "logout": '<path d="M10 4H6a2 2 0 00-2 2v12a2 2 0 002 2h4M15 8l4 4-4 4M19 12H9"/>',
        "play": '<path d="M8 5l11 7-11 7z"/>', "more": '<circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/>',
        "bell": '<path d="M6 16V11a6 6 0 0112 0v5l1.5 2h-15z"/><path d="M10 20a2 2 0 004 0"/>',
        "settings": '<circle cx="12" cy="12" r="3"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3M4.9 4.9l2.1 2.1M17 17l2.1 2.1M4.9 19.1L7 17M17 7l2.1-2.1"/>',
    }[name]
    return f'<svg class="{cls}" viewBox="0 0 24 24" width="{size}" height="{size}" aria-hidden="true">{p}</svg>'

# ---------- узоры из Figma (перерисованы по макету, 24×24) ----------
ORN = {
 1: '<path d="M2.5 3.5c5.2 1.2 8.6 4.2 9.8 8.5-1.2 4.3-4.6 7.3-9.8 8.5 3.4-2.3 5.4-5.1 5.4-8.5S5.9 5.8 2.5 3.5z"/><path d="M11 2.5c5.8 1.4 9.6 4.8 10.8 9.5-1.2 4.7-5 8.1-10.8 9.5 3.8-2.6 6-5.7 6-9.5s-2.2-6.9-6-9.5z"/>',
 2: '<path d="M12 1.5C9.8 5 6.2 6.2 6.2 10.4c0 2.1 1 3.5 2.6 4.6L12 12.2l3.2 2.8c1.6-1.1 2.6-2.5 2.6-4.6 0-4.2-3.6-5.4-5.8-8.9z"/><path d="M12 8.6c-1.4-1.6-4-.7-4 1.3 0 1.6 2.3 3 4 4.6 1.7-1.6 4-3 4-4.6 0-2-2.6-2.9-4-1.3z" fill="var(--paper)"/><path d="M3.6 13.2c-2.3.3-3.9 2.6-2.8 4.9.6-1.6 2.2-2.7 3.9-2.3 2 .4 2.6 2.7 1 3.6 2.6-.4 3.9-2.9 3-5.1-.9-1.6-3.1-1.6-5.1-1.1z"/><path d="M20.4 13.2c2.3.3 3.9 2.6 2.8 4.9-.6-1.6-2.2-2.7-3.9-2.3-2 .4-2.6 2.7-1 3.6-2.6-.4-3.9-2.9-3-5.1.9-1.6 3.1-1.6 5.1-1.1z"/>',
 3: '<path d="M12 1l3.2 5.2L12 11.4 8.8 6.2z"/><path d="M11.3 12c-1.1 4.2-4.3 7.4-9.3 8.6.2-5.2 3.4-8.4 8.6-9.4z"/><path d="M12.7 12c1.1 4.2 4.3 7.4 9.3 8.6-.2-5.2-3.4-8.4-8.6-9.4z"/><path d="M11.3 11.5h1.4v11.5h-1.4z"/>',
 4: '<path d="M12 1c-2.6 3.2-3 7.4 0 11.6 3-4.2 2.6-8.4 0-11.6z"/><path d="M7.6 8.6c-2.2.3-3.8 2.2-3 4.3.6-1.3 2.1-1.9 3.3-1.3 1.1.5 1.3 2 .3 2.8 2-.2 3.3-2 2.8-3.7-.4-1.5-2-2.4-3.4-2.1z"/><path d="M16.4 8.6c2.2.3 3.8 2.2 3 4.3-.6-1.3-2.1-1.9-3.3-1.3-1.1.5-1.3 2-.3 2.8-2-.2-3.3-2-2.8-3.7.4-1.5 2-2.4 3.4-2.1z"/><path d="M12 12.4c-1.3 2.8-1.1 5.6 0 8.6 1.1-3 1.3-5.8 0-8.6z"/><path d="M10.8 16.2c-3.2-1.2-7 .1-9.2 2.9 3.3.6 7.1-.3 9.2-2.9z"/><path d="M13.2 16.2c3.2-1.2 7 .1 9.2 2.9-3.3.6-7.1-.3-9.2-2.9z"/>',
 "star": '<path d="M12 .5c.9 6.4 5.1 10.6 11.5 11.5C17.1 12.9 12.9 17.1 12 23.5 11.1 17.1 6.9 12.9.5 12 6.9 11.1 11.1 6.9 12 .5z"/>',
}
def orn(n, size=24, color="currentColor", extra=""):
    return f'<svg viewBox="0 0 24 24" width="{size}" height="{size}" fill="{color}" style="color:{color};flex:none;{extra}" aria-hidden="true">{ORN[n]}</svg>'
def star(size, color, x, y, extra=""):
    return f'<span style="position:absolute;left:{x}px;top:{y}px;line-height:0;{extra}">{orn("star", size, color)}</span>'
def arc(d, x, y, color, w=1.2):
    return f'<span style="position:absolute;left:{x}px;top:{y}px;width:{d}px;height:{d}px;border:{w}px solid {color};border-radius:999px;pointer-events:none"></span>'

# ---------- общие блоки ----------
def lang(on="TAT", light=False):
    a = "on" if on == "TAT" else "off"; b = "on" if on == "RU" else "off"
    return f'<div class="lang{" light" if light else ""}" aria-label="Тел / Язык"><span class="{a}">TAT</span><span class="off">/</span><span class="{b}">RU</span></div>'
def sprout(size=28, color="var(--ink)"):
    return f'<span style="color:{color};line-height:0">{orn(4, size, color)}</span>'
def topbar(left="back", right="", href="Library.dc.html", label="Артка"):
    if left == "back": l = f'<a class="iconbtn" href="{href}" aria-label="{label}">{ic("back")}</a>'
    elif left == "close": l = f'<a class="iconbtn" href="{href}" aria-label="Ябарга">{ic("close")}</a>'
    elif left == "sprout": l = f'<div class="iconbtn">{sprout()}</div>'
    else: l = '<div style="width:48px"></div>'
    return f'<div class="top">{l}{right or "<div></div>"}</div>'
def pill(text, href, kind="white", extra="", icon=None):
    i = f'<span class="orb">{icon}</span>' if icon else ""
    return f'<a class="btn btn-{kind} {extra}" href="{href}">{i}<span>{text}</span></a>'
def roundbtn(href, color="var(--peach2)", label="Алга", size=52, fg="var(--ink)"):
    return f'<a class="rb" href="{href}" aria-label="{label}" style="width:{size}px;height:{size}px;background:{color};color:{fg}">{orn(1, 22, fg)}</a>'
def action(text, href, color="var(--peach2)", align="left", light=False, fg="var(--ink)"):
    lbl = 'style="color:#fff;text-shadow:0 1px 10px rgba(0,0,0,.35)"' if light else ""
    return (f'<a class="act{" r" if align == "right" else ""}" href="{href}">'
            f'<span class="rb" style="width:56px;height:56px;background:{color};color:{fg}">{orn(1, 24, fg)}</span>'
            f'<span class="t2" {lbl}>{text}</span></a>')
def lv(l): return f'<span class="lv lv-{l}">{l}</span>'

BOOKS = [(1, "Су анасы", "Габдулла Тукай", "B1", "шигъри әкият", "1908", 413, 292, 28, IMG["cover1"]),
         (2, "Шүрәле", "Габдулла Тукай", "B1", "шигъри әкият", "1907", 929, 623, 4, IMG["cover2"]),
         (3, "Нәҗип", "Фатих Әмирхан", "B1", "хикәя", "—", 1560, 731, 32, IMG["cover3"]),
         (4, "Алтын әтәч", "Габдулла Тукай", "B2", "шигъри әкият", "1908", 1500, 845, 29, None)]
def cover(b, w=150, h=225, r=16):
    if b[9]:
        return f'<img src="{b[9]}" alt="{b[1]}" style="width:{w}px;height:{h}px;border-radius:{r}px;object-fit:cover;flex:none;display:block">'
    # 4-я книга: обложки в Figma нет — типографика в стиле триптиха (закат + звезда)
    return (f'<div style="width:{w}px;height:{h}px;border-radius:{r}px;flex:none;position:relative;overflow:hidden;background:url({IMG["sunset"]}) center/cover">'
            f'<div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(29,49,40,.05),rgba(29,49,40,.55))"></div>'
            f'<span style="position:absolute;left:50%;top:14%;transform:translateX(-50%);color:#fff;opacity:.9">{orn(3, max(14, w//8), "#fff")}</span>'
            f'<div style="position:absolute;inset:auto 8px 12px 8px;text-align:center;color:#fff"><div class="cov-t" style="font-size:{max(12, w//7)}px;line-height:1.1">{b[1]}</div><div class="cov-a" style="font-size:{max(6, w//17)}px">{b[2]}</div></div></div>')
def nav(on):
    items = [("Library.dc.html", "book", "Китапханә", "lib"), ("Search.dc.html", "search", "Эзләү", "search"),
             ("ScannerStart.dc.html", "scan", "Скан", "scan"), ("Progress.dc.html", "chart", "Алгарыш", "prog")]
    out = '<nav class="nav" aria-label="Төп навигация">'
    for href, i, t, k in items:
        out += f'<a href="{href}" class="{"on" if k == on else ""}"><span class="pill-i">{ic(i, 22)}</span><span>{t}</span></a>'
    return out + "</nav>"
def mini_reading(book=BOOKS[1]):
    return (f'<a class="mini" href="Reader.dc.html">{cover(book, 36, 48, 8)}<div style="flex:1;min-width:0"><div class="ov" style="opacity:.7">Хәзер укыла</div>'
            f'<div class="t2" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis">{book[1]} · {book[2]}</div></div>'
            f'<span class="rb" style="width:40px;height:40px;background:var(--sage2);color:var(--ink)">{ic("play", 20)}</span></a>')
def phrase_card():
    return (f'<div class="wave">{star(44, "rgba(255,255,255,.35)", 250, -8)}{star(18, "rgba(255,255,255,.6)", 300, 70)}{star(12, "rgba(255,255,255,.5)", 22, 88)}'
            f'<div style="display:flex;align-items:center;gap:10px;position:relative"><span style="color:#fff">{orn(2, 22, "#fff")}</span>'
            '<div style="flex:1;display:flex;flex-direction:column;gap:6px;text-align:center;color:#fff">'
            '<p class="display-i" style="font-size:20px;line-height:26px">Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр</p>'
            '<p class="ov" style="opacity:.85">Шүрәле · Габдулла Тукай, 1907</p></div>'
            f'<span style="color:#fff;transform:scaleX(-1)">{orn(2, 22, "#fff")}</span></div></div>')
def seg(on):
    parts = []
    for k, t in [("ru", "Русча"), ("ad", "Адаптация"), ("or", "Оригинал")]:
        parts.append(f'<span class="{"on" if k == on else ""}">{orn(1, 12, "var(--bg)") if k == on else ""}{t}</span>')
    return f'<div class="seg" role="radiogroup" aria-label="Текст катламы">{"".join(parts)}</div>'
def field(label, icon, value="", right="", glass=True):
    cls = "gfield" if glass else "field"
    inner = f'<span>{value}</span>' if value else f'<span class="muted">{label}</span>'
    r = f'<span style="margin-left:auto;color:var(--ink-muted)">{ic(right, 20)}</span>' if right else ""
    return f'<label class="{cls}" aria-label="{label}"><span style="color:var(--ink-soft)">{ic(icon, 20)}</span>{inner}{r}</label>'
def stat(n, l):
    return f'<div style="display:flex;flex-direction:column;gap:2px"><span class="h2" style="font-variant-numeric:tabular-nums">{n}</span><span class="ov muted">{l}</span></div>'
def photo_hero(img, h, pos="center", overlay="linear-gradient(180deg,rgba(255,255,255,0) 55%,var(--bg) 100%)"):
    return f'<div style="position:absolute;inset:0 0 auto 0;height:{h}px;background:url({img}) {pos}/cover"><div style="position:absolute;inset:0;background:{overlay}"></div></div>'

def wrap(title, body, w=390, h=844, dark=False, lang_code="tt", bg=None):
    cls = "tl dark" if dark else "tl"
    bgc = bg or ("#14201B" if dark else "#F6F1E7"); fg = "#F1EBDD" if dark else "#1D3128"
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
body{{margin:0;background:{bgc};font-family:"Golos Text","PT Sans",Arial,sans-serif;color:{fg}}}
a{{color:inherit}} a:hover{{color:inherit}}
</style>
</helmet>
<div class="{cls} screen" style="width: {w}px; height: {h}px; background: {bgc}; color: {fg};">
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
def add(name, title, body, w=390, h=844, dark=False, interactive=True, lang_code="tt", bg=None):
    boards[name] = dict(title=title, html=wrap(title, body, w, h, dark, lang_code, bg), w=w, h=h, interactive=interactive)

# ================= 1. Кереш =================
add("Main.dc.html", "Splash", f'''
<div style="position:absolute;inset:0;background:url({IMG["blossom"]}) center 30%/cover"></div>
<div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(29,49,40,.05),rgba(29,49,40,.55))"></div>
<div style="position:relative;flex:1;display:flex;flex-direction:column;align-items:center;justify-content:center;gap:14px;color:#fff">
  {sprout(56, "#fff")}<a class="h0" href="Welcome.dc.html" style="color:#fff">TatLib</a>
  <p class="display-i" style="font-size:22px;line-height:28px;opacity:.9">Татарча күбрәк</p>
</div>
<div style="position:relative;display:flex;justify-content:center;padding-bottom:56px"><span style="width:28px;height:28px;border-radius:999px;border:3px solid rgba(255,255,255,.35);border-top-color:#fff"></span></div>''')

def welcome(ru):
    h = "Изучай татарский легко" if ru else "Рәхим итегез!"
    s = "Язык объединяет людей и открывает новые горизонты" if ru else "Телне белү — дөньяны башкача күрү"
    b = "Начать" if ru else "Башлау"
    return f'''
<div style="position:absolute;inset:0;background:url({IMG["blossom"]}) center 40%/cover"></div>
<div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(255,255,255,0) 40%,rgba(29,49,40,.62) 100%)"></div>
<div class="top" style="position:relative">{'<div class="iconbtn">' + sprout(28, "#fff") + '</div>'}{lang("RU" if ru else "TAT", light=True)}</div>
<div class="body" style="position:relative;justify-content:flex-end;gap:12px;color:#fff;padding-bottom:36px">
  <h1 class="h0" style="text-wrap:balance">{h}</h1>
  <p class="b1" style="opacity:.92;margin-bottom:10px">{s}</p>
  <div style="display:flex;align-items:center;justify-content:space-between">{action(b, "AuthChoice.dc.html", "rgba(255,255,255,.92)", light=True)}<span class="b2" style="text-decoration:underline;text-underline-offset:4px">{"Тренды" if ru else "Трендлар"}</span></div>
</div>'''
add("Welcome.dc.html", "Рәхим итегез", welcome(False))
add("WelcomeRu.dc.html", "Добро пожаловать (RU)", welcome(True), lang_code="ru")

add("AuthChoice.dc.html", "Керү яки теркәлү", f'''
<div style="position:absolute;inset:0;background:url({IMG["sunset"]}) center/cover"></div>
<div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(255,255,255,.1),rgba(29,49,40,.7))"></div>
<div class="top" style="position:relative"><a class="iconbtn" href="Welcome.dc.html" aria-label="Артка" style="color:#fff">{ic("back")}</a>{lang("TAT", light=True)}</div>
<div class="body" style="position:relative;justify-content:flex-end;gap:14px;color:#fff;padding-bottom:40px">
  <h1 class="h0" style="text-wrap:balance">Сездә исәп язмасы бармы?</h1>
  <p class="b1" style="opacity:.9;margin-bottom:12px">Дәвам итү өчен керегез яки яңа хисап ачыгыз.</p>
  {action("Керү", "Login.dc.html", "rgba(255,255,255,.92)", light=True)}
  {action("Теркәлү", "Register.dc.html", "var(--sky2)", "right", light=True)}
</div>''')

# ================= 2. Регистрация / вход (стекло на фото, как в примере владельца) =================
add("Register.dc.html", "Теркәлү", f'''
<div style="position:absolute;inset:0;background:url({IMG["blossom"]}) center 35%/cover"></div>
<div class="top" style="position:relative"><div class="iconbtn">{sprout(28, "#fff")}</div>{lang("TAT", light=True)}</div>
<div class="body" style="position:relative;gap:18px;padding-top:60px">
  <h1 class="h0">Теркәлү</h1>
  <div class="glass">
    {field("Исем", "person")}
    {field("Телефон номеры / электрон почта", "mail")}
    {field("Серсүз", "lock", "", "eye-off")}
    {field("Серсүзне кабатлагыз", "lock", "", "eye-off")}
  </div>
  <div style="display:flex;gap:8px">{"".join(f'<span class="chip glassy{" on" if c == "18–25" else ""}">{c}</span>' for c in ["10–14", "14–18", "18–25", "25+"])}</div>
  <div style="flex:1"></div>
  {action("Дәвам итү", "LevelIntro.dc.html", "rgba(255,255,255,.92)", "right", light=True)}
</div>''')

add("Login.dc.html", "Керү", f'''
<div style="position:absolute;inset:0;background:url({IMG["blossom"]}) center 35%/cover"></div>
<div class="top" style="position:relative"><div class="iconbtn">{sprout(28, "#fff")}</div>{lang("TAT", light=True)}</div>
<div class="body" style="position:relative;gap:18px;padding-top:120px">
  <h1 class="h0">Керү</h1>
  <div class="glass">
    {field("Телефон яки электрон почта", "mail")}
    {field("Серсүз", "lock", "", "eye-off")}
  </div>
  <a class="b2" href="Register.dc.html" style="color:#fff;text-decoration:underline;text-underline-offset:4px;text-shadow:0 1px 8px rgba(0,0,0,.3)">Исәп язмасы юк — теркәлү</a>
  <div style="flex:1"></div>
  {action("Керү", "LevelIntro.dc.html", "rgba(255,255,255,.92)", "right", light=True)}
</div>''')

# ================= 3. Тест уровня (дуги, звёзды, арка с фото) =================
DECO_A = (arc(520, 250, -420, "var(--sky2)") + arc(700, -330, 470, "var(--peach2)") +
          star(40, "var(--sky2)", 318, 150) + star(16, "var(--peach2)", 262, 330) +
          star(60, "var(--sky2)", 20, 545) + star(20, "var(--peach2)", 80, 620) + star(22, "var(--peach2)", 250, 555))
def arch(img, w=210, h=330, right=-20, top=430):
    return (f'<div style="position:absolute;right:{right}px;top:{top}px;width:{w}px;height:{h}px;'
            f'clip-path:path(\'M{w/2} 0C{w/2} 0 {w*.78} {h*.12} {w*.95} {h*.27}C{w*1.03} {h*.34} {w*1.02} {h*.43} {w} {h*.46}L{w} {h*.9}Q{w} {h} {w*.85} {h}L{w*.15} {h}Q0 {h} 0 {h*.9}L0 {h*.46}C{-w*.02} {h*.43} {-w*.03} {h*.34} {w*.05} {h*.27}C{w*.22} {h*.12} {w/2} 0 {w/2} 0Z\');'
            f'background:url({img}) center/cover"></div>')
add("LevelIntro.dc.html", "Дәрәҗә тесты", f'''
{DECO_A}{arch(IMG["arch"], 210, 330, -20, 440)}
<div class="top" style="position:relative"><div class="iconbtn">{sprout()}</div>{lang("TAT")}</div>
<div class="body" style="position:relative;gap:12px;padding-top:150px">
  <h1 class="h0" style="text-wrap:balance;max-width:300px">Сезнең дәрәҗәгезне билгелибезме?</h1>
  <p class="b1 soft" style="max-width:250px">Татар телен ни дәрәҗәдә белүегезне ачыклагыз.</p>
  <div style="flex:1"></div>
  {action("Тест узарга", "Quiz.dc.html", "var(--peach2)")}
  {action("Үзем күрсәтермен", "LevelResult.dc.html", "var(--sky2)", "right")}
</div>''', bg="#FBF9F4")

def opt(text, on=False):
    st = "background:var(--ink);color:var(--bg)" if on else "background:var(--paper);border:1px solid var(--line)"
    return f'<button type="button" class="b1" style="{st};border-radius:18px;min-height:58px;padding:14px 18px;text-align:left;display:flex;align-items:center;justify-content:space-between">{text}{ic("check", 20) if on else ""}</button>'
add("Quiz.dc.html", "Тест 1/5", f'''
{arc(520, 250, -420, "var(--sky2)")}{star(28, "var(--sky2)", 330, 118)}{star(14, "var(--peach2)", 300, 160)}
<div class="top" style="position:relative"><a class="iconbtn" href="LevelIntro.dc.html" aria-label="Артка">{ic("back")}</a><span class="ov muted">Сорау 1 / 5</span><div style="width:48px"></div></div>
<div class="body" style="position:relative;gap:20px">
  <div class="bar"><i style="width:20%"></i></div>
  <h1 class="h0" style="font-size:30px;line-height:36px">Дәрәҗәгезне билгелик</h1>
  <h2 class="t1 soft" lang="ru" style="font-weight:500">Как правильно сказать «Я читаю книгу»?</h2>
  <div style="display:flex;flex-direction:column;gap:12px">{opt("Мин китап укыйм", True)}{opt("Мин китап укыдым")}{opt("Мин китап укыячакмын")}{opt("Мин китап укымыйм")}</div>
  <div style="flex:1"></div>
  {action("Алга", "LevelResult.dc.html", "var(--peach2)", "right")}
</div>''', bg="#FBF9F4")

def level_row(sel="B1"):
    out = '<div style="display:flex;justify-content:space-between;align-items:center;padding:0 4px">'
    for l in ["A1", "A2", "B1", "B2", "C1"]:
        if l == sel: out += f'<span style="width:60px;height:60px;border-radius:999px;background:var(--ink);color:var(--bg);display:flex;align-items:center;justify-content:center;font-weight:600;font-size:19px">{l}</span>'
        else: out += f'<span class="lv lv-{l}" style="height:42px;padding:0 14px;font-size:15px">{l}</span>'
    return out + "</div>"
add("LevelResult.dc.html", "Дәрәҗә нәтиҗәсе", f'''
{DECO_A}{arch(IMG["kremlin"], 200, 300, -30, 470)}
<div class="top" style="position:relative"><a class="iconbtn" href="Quiz.dc.html" aria-label="Артка">{ic("back")}</a>{lang("TAT")}</div>
<div class="body" style="position:relative;gap:22px;padding-top:110px">
  <h1 class="h0" style="text-wrap:balance">Сезнең татар теле дәрәҗәсе</h1>
  {level_row("B1")}
  <p class="b2 soft" style="max-width:250px">Дәрәҗәгезне төзәтергә телисез икән, теләгән хәрефкә басыгыз.</p>
  <div style="flex:1"></div>
  {action("Дәвам итү", "Library.dc.html", "var(--peach2)")}
  {action("Татар телен белмим", "Library.dc.html", "var(--sky2)", "right")}
</div>''', bg="#FBF9F4")

# ================= 4. Главная (как у музыкальных приложений) =================
def library(greet, img, name):
    return f'''
<div style="position:absolute;inset:0 0 auto 0;height:400px;background:url({img}) center bottom/cover"><div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(29,49,40,.05) 30%,rgba(29,49,40,.6) 75%,var(--bg) 100%)"></div></div>
<div class="top" style="position:relative;color:#fff"><div class="iconbtn">{sprout(28, "#fff")}</div><div style="display:flex;gap:4px;align-items:center">{lang("TAT", light=True)}<a class="iconbtn" href="Profile.dc.html" aria-label="Профиль">{ic("person", 22)}</a></div></div>
<div class="body" style="position:relative;gap:22px;padding-top:120px;overflow:hidden">
  <div style="color:#fff;display:flex;flex-direction:column;gap:6px">
    <h1 class="h0" style="font-size:40px;line-height:44px">{greet}</h1>
    <p class="b1" style="opacity:.92">Укуны дәвам итәбезме?</p>
    <div style="display:flex;align-items:center;justify-content:space-between;margin-top:8px">{action("Укуны дәвам итү", "Reader.dc.html", "rgba(255,255,255,.92)", light=True)}<span class="b2" style="text-decoration:underline;text-underline-offset:4px">Трендлар</span></div>
  </div>
  <div style="display:flex;gap:8px;margin-top:6px"><span class="chip on">Барысы</span><span class="chip">Әкиятләр</span><span class="chip">Хикәяләр</span><span class="chip">Тукай</span></div>
  <section style="display:flex;flex-direction:column;gap:12px">
    <div style="display:flex;justify-content:space-between;align-items:baseline"><h2 class="t1">Минем китапларым</h2><a class="b2 soft" href="Search.dc.html">Барысы</a></div>
    <div style="display:flex;gap:14px;width:342px">{"".join(f'<a href="BookDetail.dc.html" style="display:flex;flex-direction:column;gap:8px;width:150px;flex:none">{cover(b)}<span class="t2">{b[1]}</span><span class="b3 soft">{b[2]} · {b[3]}</span></a>' for b in BOOKS)}</div>
  </section>
  <section style="display:flex;flex-direction:column;gap:12px">
    <div style="display:flex;justify-content:space-between;align-items:baseline"><h2 class="t1">Тәкъдим итәбез</h2><a class="b2 soft" href="Search.dc.html">Барысы</a></div>
    <div style="display:flex;gap:14px;width:342px">{"".join(f'<a href="BookDetail.dc.html" style="display:flex;flex-direction:column;gap:8px;width:150px;flex:none">{cover(b)}<span class="t2">{b[1]}</span><span class="b3 soft">{b[2]}</span></a>' for b in reversed(BOOKS))}</div>
  </section>
</div>
{mini_reading()}
{nav("lib")}'''
add("Library.dc.html", "Китапханә · көндез", library("Хәерле көн!", IMG["day"], "day"))
add("LibraryMorning.dc.html", "Китапханә · иртә", library("Хәерле иртә!", IMG["morning"], "morning"))
add("LibraryEvening.dc.html", "Китапханә · кич", library("Хәерле кич!", IMG["evening"], "evening"))

# ================= 5. Поиск (обзор категорий плитками) =================
def tile(img, title, sub, color, pos="center"):
    return (f'<a href="Search.dc.html" style="position:relative;height:120px;border-radius:18px;overflow:hidden;background:{color}">'
            f'<img src="{img}" alt="" style="position:absolute;right:-26px;bottom:-26px;width:104px;height:112px;object-fit:cover;object-position:{pos};border-radius:12px;transform:rotate(18deg);box-shadow:0 8px 20px rgba(0,0,0,.28)">'
            f'<div style="position:absolute;left:14px;top:14px;color:#fff;max-width:96px"><div class="t2" style="line-height:20px">{title}</div><div class="b3" style="opacity:.85">{sub}</div></div></a>')
def row(b):
    return (f'<a href="BookDetail.dc.html" style="display:flex;gap:14px;align-items:center">{cover(b, 56, 76, 10)}'
            f'<div style="display:flex;flex-direction:column;gap:2px;flex:1"><span class="t2">{b[1]}</span><span class="b3 soft">{b[2]}</span>'
            f'<span class="b3 muted" style="margin-top:2px">{b[3]} · {b[4]} · {b[5]}</span></div><span class="muted">{ic("chev", 20)}</span></a>')
add("Search.dc.html", "Эзләү", f'''
<div class="body" style="padding-top:20px;gap:16px;overflow:hidden">
  <h1 class="h0" style="font-size:34px;line-height:40px">Эзләү</h1>
  <div class="field" style="height:52px;border-color:var(--line);background:var(--paper)">{ic("search", 22)}<span class="muted b1">Китап, автор яки тема…</span></div>
  <div style="display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:12px">
    {tile(IMG["cover2"], "Әкиятләр", "3 китап", "#4E6B48", "center 40%")}{tile(IMG["cover3"], "Хикәяләр", "1 китап", "#B4522E", "center 60%")}
    {tile(IMG["cover1"], "Тукай", "3 китап", "#3F6F8A", "center 70%")}{tile(IMG["kremlin"], "Дәрәҗәм B1", "3 китап", "#8C6A1F")}
  </div>
  <h2 class="t1">Барлык китаплар</h2>
  <div style="display:flex;flex-direction:column;gap:14px">{"".join(row(b) for b in BOOKS)}</div>
</div>
{mini_reading()}
{nav("search")}''')

# ================= 6. Детали книги (страница альбома) =================
shurale = BOOKS[1]
add("BookDetail.dc.html", "Шүрәле", f'''
<div style="position:absolute;inset:0 0 auto 0;height:470px;overflow:hidden"><div style="position:absolute;inset:-60px;background:url({IMG["cover2"]}) center/cover;filter:blur(30px) saturate(1.2)"></div><div style="position:absolute;inset:0;background:linear-gradient(180deg,rgba(29,49,40,.25) 0%,rgba(246,241,231,.2) 55%,var(--bg) 100%)"></div></div>
<div class="top" style="position:relative;color:#fff"><a class="iconbtn" href="Library.dc.html" aria-label="Артка">{ic("back")}</a><span class="iconbtn">{ic("more")}</span></div>
<div class="body" style="position:relative;gap:16px;padding-top:0;align-items:center;text-align:center">
  {cover(shurale, 190, 285, 18)}
  <div style="display:flex;flex-direction:column;gap:6px;align-items:center"><h1 class="h0" style="font-size:32px;line-height:36px">Шүрәле</h1><span class="b1 soft">Габдулла Тукай · 1907</span>
    <div style="display:flex;gap:6px;flex-wrap:wrap;justify-content:center;margin-top:4px">{lv("B1")}<span class="chip" style="height:28px;font-size:13px">шигъри әкият</span></div></div>
  {pill("Укырга", "Reader.dc.html", "sage", "btn-wide", ic("play", 18))}
  <div style="display:flex;justify-content:space-between;width:100%;text-align:left">{stat("929", "сүз")}{stat("623", "уникаль сүз")}{stat("4", "бүлек")}</div>
  <p class="b1" style="text-align:left">Шагыйрь Казан артындагы Кырлай авылы турында сөйли. Авыл кечкенә, ләкин урманы һәм болыннары бик матур.</p>
  <p class="b3 muted" style="text-align:left;width:100%">Беренче бүлекнең адаптацияләнгән тексты · чыганак: speak.tatar, gabdullatukay.ru</p>
</div>''')

# ================= 7. Ридер =================
ORIG = ["Нәкъ Казан артында бардыр бер авыл&nbsp;—", "«Кырлай» диләр;",
 '<span class="u-B2">Җырлаганда</span> көй өчен, «<span class="u-B2">тавыклары</span> җырлай», диләр.',
 'Гәрчә анда <span class="u-B2">тугъмасам</span> да, мин бераз торган идем;',
 "Җирне әз-мәз тырмалап, чәчкән идем, урган идем.", "Ул авылның, һич онытмыйм, {W} урман иде,",
 "Ул болын, яшел үләннәр хәтфәдән юрган иде.", "Зурмы, дисәң, зур түгелдер, бу авыл бик кечкенә;",
 "Халкының эчкән суы бик кечкенә&nbsp;— инеш кенә.", "Анда бик салкын вә бик эссе түгел, урта һава;",
 "Җил дә вактында исеп, яңгыр да вактында ява.", '<span class="u-B2">Урманында</span> кып-кызыл кура җиләк тә җир җиләк;',
 'Күз ачып йомганчы, <span class="u-B2">һичшиксез</span>, җыярсың бер чиләк.', "Бик хозур! Рәт-рәт тора, гаскәр кеби, чыршы, нарат;",
 '<span class="u-B2">Төпләрендә</span> ятканым бар, хәл җыеп, күккә карап.', 'Юкә, каеннар төбендә <span class="u-B2">кузгалаклар</span>, гөмбәләр',
 "Берлә бергә үсә аллы-гөлле гөлләр, гонҗәләр."]
def reader_body(layer="or", word=False, dark=False):
    if layer == "or":
        lines = [l.replace("{W}", ('<span class="hl">һәрьягы</span>' if word else "һәрьягы")) for l in ORIG]
        text = "\n".join(lines[:6] if word else lines); lbl = "Оригинал"
    elif layer == "ad":
        text = "Шагыйрь Казан артындагы Кырлай авылы турында сөйли. Авыл кечкенә, ләкин урманы һәм болыннары бик матур."; lbl = "Адаптация"
    else:
        text = "Поэт рассказывает о деревне Кырлай недалеко от Казани. Деревня небольшая, но её леса и луга очень красивы."; lbl = "Русча"
    popup = ""
    if word:
        rest = "\n".join(ORIG[6:])
        popup = f'''<div class="card" style="box-shadow:var(--shadow);border:1px solid var(--line);display:flex;flex-direction:column;gap:6px;margin-top:8px">
      <div style="display:flex;align-items:center;gap:10px"><span class="h2" style="font-size:24px">һәрьягы</span>{lv("B1")}</div>
      <p class="b1" lang="ru">со всех сторон, вокруг</p><p class="b3 muted">Ул авылның һәрьягы урман иде.</p>
      <div style="display:flex;justify-content:flex-end"><a class="btn btn-ghost" href="Reader.dc.html">Ябарга</a></div></div>
    <p class="rtext" style="margin-top:8px">{rest}</p>'''
    return f'''
<div class="top"><a class="iconbtn" href="BookDetail.dc.html" aria-label="Артка">{ic("back")}</a><span class="t2">Шүрәле</span><button type="button" class="iconbtn" aria-label="Шрифт көйләүләре">{ic("aa")}</button></div>
<div class="body" style="gap:14px;padding-top:0">
  {seg(layer)}
  <span class="ov muted">{lbl} · I бүлек · Шүрәле, 1907</span>
  <div style="flex:1;min-height:0;overflow:hidden;display:flex;flex-direction:column;gap:4px"><p class="rtext" style="{"font-size:19px" if layer != "or" else ""}">{text}</p>{popup}</div>
  <div style="display:flex;flex-direction:column;gap:8px"><div class="bar"><i style="width:100%"></i></div>
    <div style="display:flex;justify-content:space-between;align-items:center" class="b3 muted"><span>← Артка</span><span>1 бүлек · 929 сүз</span><span>Алга →</span></div></div>
</div>'''
add("Reader.dc.html", "Ридер · Оригинал", reader_body("or"))
add("ReaderWord.dc.html", "Ридер · Сүз", reader_body("or", word=True))
add("ReaderDark.dc.html", "Ридер · Караңгы", reader_body("ad"), dark=True)

# ================= 8. Recap =================
add("Recap.dc.html", "Сессия нәтиҗәсе", f'''
{photo_hero(IMG["kremlin"], 320, "center", "linear-gradient(180deg,rgba(246,241,231,.15) 0%,rgba(246,241,231,.75) 55%,var(--bg) 100%)")}
<div class="top" style="position:relative"><a class="iconbtn" href="Library.dc.html" aria-label="Ябарга" style="background:rgba(255,255,255,.6)">{ic("close")}</a><div></div></div>
<div class="body" style="position:relative;gap:22px;padding-top:150px">
  <h1 class="h0" style="text-wrap:balance">Һәр укылган бит сине оригиналга якынайта!</h1>
  <div style="display:flex;justify-content:space-between">{stat("—", "бит")}{stat("—", "яңа сүз")}{stat("—", "оригинал")}</div>
  <div class="card" style="border:1px solid var(--line);display:flex;gap:14px;align-items:center;padding:18px">
    <span style="color:var(--forest)">{orn(2, 40, "var(--forest)")}</span>
    <div style="display:flex;flex-direction:column;gap:4px"><span class="t2">Уку — үсеш</span><span class="b2 soft">Беренче сессия әле язылмаган: укуны тәмамлагач, битләр һәм яңа сүзләр монда күренер.</span></div></div>
  <div style="flex:1"></div>
  {pill("Укырга", "Reader.dc.html", "sage", "btn-wide", ic("play", 18))}
  <a class="btn btn-ghost btn-wide" href="Library.dc.html">Китапханәгә кайту</a>
</div>''')

# ================= 9. Прогресс =================
DAYS = ["Дш", "Сш", "Чш", "Пҗ", "Җм", "Шм", "Як"]
def ring():
    petals = "".join(f'<span style="position:absolute;left:50%;top:50%;transform:translate(-50%,-50%) rotate({i*30}deg) translateY(-62px)">{orn(4, 14, "var(--line)")}</span>' for i in range(12))
    return f'<div class="ring" style="background:conic-gradient(var(--forest) 0 50%, var(--sand) 50% 100%)">{petals}<div style="width:100px;height:100px;border-radius:999px;background:var(--bg);display:flex;align-items:center;justify-content:center"><span class="display-i">B1</span></div></div>'
add("Progress.dc.html", "Алгарышың", f'''
{arc(420, 230, -300, "var(--sky2)")}{star(26, "var(--sky2)", 336, 104)}{star(12, "var(--peach2)", 312, 150)}
<div class="top" style="position:relative;padding:12px 24px 0;height:auto;align-items:center"><h1 class="h0" style="font-size:34px;line-height:40px">Алгарышың</h1><a class="iconbtn" href="Profile.dc.html" aria-label="Профиль" style="background:var(--sage-c)">{ic("person", 22)}</a></div>
<div class="body" style="position:relative;gap:16px;overflow:hidden">
  <div style="display:flex;gap:8px"><span class="chip on">Атна</span><span class="chip">Ай</span><span class="chip">Ел</span></div>
  <div class="card" style="display:flex;gap:16px;align-items:center;border:1px solid var(--line)">{ring()}
    <div style="display:flex;flex-direction:column;gap:6px"><span class="ov muted">Хәзерге дәрәҗәң</span><span class="t1">B1 · Урта</span><span class="b2 soft">Тагын бераз — һәм B2 дәрәҗәсен сынап карарга була.</span></div></div>
  <div style="display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:10px">
    <div class="tile" style="background:var(--sage-c)">{stat("4", "тәрҗемә")}</div><div class="tile" style="background:var(--sky-c)">{stat("4", "уникаль сүз")}</div><div class="tile" style="background:var(--peach-c)">{stat("1", "актив көн")}</div></div>
  <div style="display:flex;flex-direction:column;gap:8px"><span class="t2">Бу атна</span>
    <div class="waffle">{"".join('<i class="on"></i>' if i == 6 else "<i></i>" for i in range(7))}</div>
    <div style="display:grid;grid-template-columns:repeat(7,24px);gap:4px" class="b3 muted">{"".join(f"<span style='text-align:center'>{d}</span>" for d in DAYS)}</div></div>
  {phrase_card()}
  <div style="display:flex;flex-direction:column;gap:8px"><span class="t2">Соңгы тәрҗемәләр</span>
    <div style="display:flex;gap:8px;flex-wrap:wrap"><span class="chip">көне</span><span class="chip">Эссе</span><span class="chip">һавада</span><span class="b3 muted" style="align-self:center">· Су анасы</span></div></div>
</div>
{nav("prog")}''')

# ================= 10. Сканер =================
def viewfinder(inner="", img=IMG["kremlin"]):
    c = 'position:absolute;width:28px;height:28px;border:3px solid #fff;'
    return f'''<div style="position:relative;height:400px;border-radius:24px;overflow:hidden;background:url({img}) center/cover;display:flex;align-items:center;justify-content:center">
      <div style="position:absolute;inset:0;background:rgba(29,49,40,.25)"></div>
      <span style="{c}top:20px;left:20px;border-right:0;border-bottom:0;border-radius:8px 0 0 0"></span><span style="{c}top:20px;right:20px;border-left:0;border-bottom:0;border-radius:0 8px 0 0"></span>
      <span style="{c}bottom:20px;left:20px;border-right:0;border-top:0;border-radius:0 0 0 8px"></span><span style="{c}bottom:20px;right:20px;border-left:0;border-top:0;border-radius:0 0 8px 0"></span>
      <div style="position:relative;color:#fff">{inner}</div></div>'''
scan_top = f'<div class="top"><a class="iconbtn" href="Library.dc.html" aria-label="Ябарга">{ic("close")}</a><button type="button" class="iconbtn" aria-label="Галерея">{ic("image")}</button></div>'
add("ScannerStart.dc.html", "Сканер", f'''
{scan_top}
<div class="body" style="gap:18px;padding-top:0">
  <h1 class="h0" style="font-size:26px;line-height:32px;text-align:center;text-wrap:balance">Камераны китапка яки биткә юнәлтегез</h1>
  {viewfinder()}
  <p class="b2 soft" style="text-align:center">Без текстны табып, аны уку өчен әзерләячәкбез.</p>
  <div style="flex:1"></div>
  <div style="display:flex;gap:16px;align-items:center;justify-content:center"><a class="btn btn-ghost" href="ScannerBusy.dc.html">Галерея</a><a class="rb" href="ScannerBusy.dc.html" aria-label="Камера" style="width:72px;height:72px;background:var(--ink);color:#fff;border:4px solid var(--sage2)">{ic("camera", 28)}</a><span style="width:76px"></span></div>
</div>
{nav("scan")}''')
add("ScannerBusy.dc.html", "Сканер · тану", f'''
{scan_top}
<div class="body" style="gap:18px;padding-top:0">
  <h1 class="h0" style="font-size:26px;line-height:32px;text-align:center">Текст таныла…</h1>
  {viewfinder('<div style="display:flex;flex-direction:column;align-items:center;gap:12px"><div style="width:40px;height:40px;border-radius:999px;border:3px solid rgba(255,255,255,.4);border-top-color:#fff"></div><span class="b3">tat_cyrl · Tesseract</span></div>')}
  <div class="bar"><i style="width:55%;background:var(--sky)"></i></div>
  <p class="b2 soft" style="text-align:center">Бит базадагы дүрт китап белән чагыштырыла.</p>
  <div style="flex:1"></div>
  <a class="btn btn-ghost btn-wide" href="ScannerText.dc.html">Туктату</a>
</div>
{nav("scan")}''')
sua = BOOKS[0]
SCAN_ORIG = ["Җәй көне. Эссе һавада мин суда койнам, йөзәм;", "Чәчрәтәм, уйныйм, чумам, башым белән суны сөзәм.",
 "Шул рәвешчә бер сәгать ярым кадәрле уйнагач,", 'Инде, шаять, бер <span class="u-B2">сәгатьсез</span> тирләмәм дип уйлагач,',
 "Йөгереп чыктым судан, тиз-тиз киендем өс-башым;", "Куркам үзем әллә нидән,&nbsp;— юк янымда юлдашым."]
SCAN_AD = "Җәй көне бала елгада коена һәм ялгыз уйный. Ул суда озак вакыт рәхәтләнеп йөзә."
SCAN_RU = "Летом мальчик купается и играет в реке. Он долго плавает и радуется жаркому дню."
def scan_text(layer, matched=False, sheet=False):
    text = {"or": "\n".join(SCAN_ORIG), "ad": SCAN_AD, "ru": SCAN_RU}[layer]
    lbl = {"or": "Оригинал", "ad": "Адаптация", "ru": "Русча"}[layer]
    if not matched:
        found = ('<div class="card" style="border:1px solid var(--line);display:flex;gap:12px;align-items:center;padding:12px 14px">'
                 '<span style="width:28px;height:28px;border-radius:999px;border:3px solid var(--sand);border-top-color:var(--forest);flex:none"></span>'
                 '<div style="display:flex;flex-direction:column;gap:2px"><span class="t2">Бу өзек кайсы китаптан?</span><span class="b3 muted">Базадагы дүрт китап белән чагыштырабыз…</span></div></div>')
    else:
        found = (f'<a class="card" href="ScannerMatch.dc.html" style="border:1px solid var(--line);display:flex;gap:12px;align-items:center;padding:12px 14px;background:var(--sage-c)">{cover(sua, 44, 62, 8)}'
                 '<div style="display:flex;flex-direction:column;gap:2px;flex:1"><span class="ov" style="color:var(--forest)">Китап табылды</span><span class="t2">Су анасы · Габдулла Тукай</span><span class="b3 muted">I бүлек · тулы версиясе базада бар</span></div>'
                 f'<span class="muted">{ic("chev", 20)}</span></a>')
    overlay = ""
    if sheet:
        overlay = f'''<div style="position:absolute;inset:0;background:rgba(29,49,40,.45)"></div>
<div class="sheet" style="position:absolute;left:0;right:0;bottom:0;display:flex;flex-direction:column;gap:14px;padding-bottom:28px">
  <div class="handle"></div>
  <div style="display:flex;align-items:center;gap:8px"><span style="color:var(--forest)">{orn(2, 22, "var(--forest)")}</span><span class="ov" style="color:var(--forest)">Китап табылды</span></div>
  <div style="display:flex;gap:16px;align-items:center">{cover(sua, 88, 124, 12)}
    <div style="display:flex;flex-direction:column;gap:6px"><span class="h0" style="font-size:26px;line-height:30px">Су анасы</span><span class="b2 soft">Габдулла Тукай · 1908 · шигъри әкият</span>
      <div style="display:flex;gap:8px;align-items:center">{lv("B1")}<span class="b3 muted">413 сүз · 28 бүлек</span></div></div></div>
  <p class="b2 soft">Сканланган өзек — бу китапның I бүлеге. Тулы версиясен өч катламда укый аласыз.</p>
  {pill("Тулы версиясен укырга", "BookDetail.dc.html", "sage", "btn-wide", ic("play", 18))}
  <a class="btn btn-ghost btn-wide" href="ScannerText.dc.html">Юк, өзекне генә укыйм</a>
</div>'''
    return f'''
<div class="top"><a class="iconbtn" href="ScannerStart.dc.html" aria-label="Артка">{ic("back")}</a><span class="t2">Танылган текст</span><button type="button" class="iconbtn" aria-label="Шрифт көйләүләре">{ic("aa")}</button></div>
<div class="body" style="gap:14px;padding-top:0">
  <div style="display:flex;flex-direction:column;gap:8px"><span class="ov muted">Катлаулылык</span>{seg(layer)}</div>
  <span class="ov muted">{lbl} · фотодан танылган өзек</span>
  <div style="flex:1;min-height:0;overflow:hidden"><p class="rtext" style="{"font-size:19px" if layer != "or" else ""}">{text}</p></div>
  {found}
</div>
{nav("scan")}{overlay}'''
add("ScannerText.dc.html", "Сканер · текст · оригинал", scan_text("or"))
add("ScannerTextAd.dc.html", "Сканер · текст · адаптация", scan_text("ad"))
add("ScannerTextRu.dc.html", "Сканер · текст · русча", scan_text("ru", matched=True))
add("ScannerMatch.dc.html", "Сканер · китап табылды", scan_text("or", matched=True, sheet=True))

# ================= 11. Профиль =================
def srow(label, control, sub=""):
    s = f'<span class="b3 muted">{sub}</span>' if sub else ""
    return f'<div style="display:flex;justify-content:space-between;align-items:center;gap:12px;min-height:52px"><div style="display:flex;flex-direction:column;gap:2px"><span class="b1">{label}</span>{s}</div>{control}</div>'
add("Profile.dc.html", "Профиль", f'''
{photo_hero(IMG["arch"], 220, "center 30%", "linear-gradient(180deg,rgba(29,49,40,.1),var(--bg) 100%)")}
<div class="top" style="position:relative;color:#fff"><a class="iconbtn" href="Progress.dc.html" aria-label="Артка">{ic("back")}</a><span class="iconbtn">{ic("settings")}</span></div>
<div class="body" style="position:relative;gap:18px;padding-top:60px">
  <div style="display:flex;gap:16px;align-items:center">
    <span style="width:72px;height:72px;border-radius:999px;background:var(--paper);border:3px solid #fff;display:flex;align-items:center;justify-content:center;color:var(--forest);box-shadow:var(--shadow)">{sprout(34, "var(--forest)")}</span>
    <div style="display:flex;flex-direction:column;gap:4px"><span class="h0" style="font-size:26px;line-height:30px">Укучы</span><div style="display:flex;gap:8px;align-items:center">{lv("B1")}<span class="b3 muted">18 яшь</span></div></div></div>
  <div class="card" style="border:1px solid var(--line);display:flex;flex-direction:column;gap:2px">
    {srow("Интерфейс теле", lang("TAT"), "Татарча төп, русча күчерү")}
    {srow("Тема", '<div style="display:flex;gap:6px"><span class="chip on" style="height:32px;font-size:13px">Система</span><span class="chip" style="height:32px;font-size:13px">Якты</span><span class="chip" style="height:32px;font-size:13px">Караңгы</span></div>')}
    {srow("Ридер шрифты", '<div style="display:flex;gap:6px"><span class="chip on" style="height:32px;font-size:13px;font-family:var(--reader)">Literata</span><span class="chip" style="height:32px;font-size:13px">Golos</span></div>')}
    <div style="display:flex;flex-direction:column;gap:8px;padding:8px 0"><span class="b1">Шрифт зурлыгы</span>
      <div style="display:flex;align-items:center;gap:12px"><span class="b3">Аа</span><div style="flex:1;height:4px;border-radius:2px;background:var(--sand);position:relative"><i style="position:absolute;left:0;top:0;height:100%;width:50%;background:var(--forest);border-radius:2px"></i><i style="position:absolute;left:50%;top:-8px;width:20px;height:20px;border-radius:999px;background:var(--forest);transform:translateX(-50%)"></i></div><span class="t1" style="font-size:20px">Аа</span></div><span class="b3 muted">18 sp · 16–20</span></div>
    {srow("Сүз асты сызыгы", '<span class="toggle on" role="switch" aria-checked="true"><i></i></span>', "Дәрәҗәдән югары сүзләрне билгеләү")}
  </div>
  <div style="flex:1"></div>
  <a class="btn btn-ghost btn-wide" href="Welcome.dc.html">{ic("logout", 20)}<span style="margin-left:8px">Чыгу</span></a>
</div>
{nav("prog")}''')

# ================= 12. Компоненты =================
def sw(name, var): return f'<div style="display:flex;flex-direction:column;gap:6px;width:96px"><div style="height:56px;border-radius:12px;background:{var};border:1px solid var(--line)"></div><span class="b3">{name}</span></div>'
def components(dark):
    mini_static = mini_reading().replace('class="mini"', 'class="mini" style="position:static"')
    swatches = "".join(sw(n, f"var(--{v})") for n, v in [("bg", "bg"), ("paper", "paper"), ("sand", "sand"), ("ink", "ink"), ("forest", "forest"), ("sage", "sage"), ("sage2", "sage2"), ("sky2", "sky2"), ("peach2", "peach2"), ("cta", "cta"), ("error", "error")])
    levels = "".join(f'<div style="display:flex;align-items:center;gap:8px">{lv(l)}<span class="dot dot-{l}"></span></div>' for l in ["A1", "A2", "B1", "B2", "C1"])
    ornaments = "".join(f'<div style="display:flex;flex-direction:column;align-items:center;gap:6px"><span style="color:var(--ink)">{orn(i, 56)}</span><span class="b3 muted">мотив {i}</span></div>' for i in (1, 2, 3, 4)) + f'<div style="display:flex;flex-direction:column;align-items:center;gap:6px"><span style="display:flex;gap:6px;align-items:center">{orn("star", 44, "var(--sky2)")}{orn("star", 22, "var(--peach2)")}</span><span class="b3 muted">йолдыз</span></div>'
    sect = lambda t, inner: f'<section style="display:flex;flex-direction:column;gap:12px"><h2 class="ov muted">{t}</h2>{inner}</section>'
    return f'''
<div style="padding:32px;display:flex;flex-direction:column;gap:28px;overflow:hidden;height:100%">
  <div><h1 class="h0">TatLib · компонентлар {"· караңгы тема" if dark else "· якты тема"}</h1><p class="b2 soft">MASTER.md § 7 · v2 · 20.09.2026</p></div>
  {sect("Шрифтлар һәм хәрефләр ә ө ү җ ң һ", '<div style="display:flex;flex-direction:column;gap:10px"><p class="h0">Golos Text 600 · Рәхим итегез! Әә Өө Үү Җҗ Ңң Һһ</p><p class="display-i">Playfair Italic · Татарча күбрәк — Әә Өө Үү Җҗ Ңң Һһ</p><p class="b1">Golos Text 400 · Телне белү — дөньяны башкача күрү · Әә Өө Үү Җҗ Ңң Һһ</p><p class="rtext">Literata 18 · Нәкъ Казан артында бардыр бер авыл — «Кырлай» диләр. Әә Өө Үү Җҗ Ңң Һһ</p></div>')}
  {sect("Палитра", f'<div style="display:flex;gap:10px;flex-wrap:wrap">{swatches}</div>')}
  {sect("Кнопкалар — один компонент перехода везде", f'<div style="display:flex;gap:18px;align-items:center;flex-wrap:wrap">{action("Тест узарга", "#", "var(--peach2)")}{action("Үзем күрсәтермен", "#", "var(--sky2)")}<div style="padding:12px 16px;border-radius:16px;background:url({IMG["kremlin"]}) center/cover">{action("Укуны дәвам итү", "#", "rgba(255,255,255,.92)", light=True)}</div>{pill("Укырга", "#", "sage", "", ic("play", 18))}<a class="btn btn-ghost" href="#">Китапханәгә кайту</a></div>')}
  {sect("Чиплар һәм дәрәҗәләр", f'<div style="display:flex;gap:16px;align-items:center;flex-wrap:wrap"><span class="chip on">Барысы</span><span class="chip">Китаплар</span>{levels}</div>')}
  {sect("Стеклянная форма на фото", f'<div style="width:390px;padding:20px;border-radius:24px;background:url({IMG["blossom"]}) center 40%/cover"><div class="glass">{field("Исем", "person")}{field("Серсүз", "lock", "", "eye-off")}</div></div>')}
  {sect("Катлам күчергече, сүз, мини-бар", f'<div style="display:flex;gap:24px;align-items:flex-start;flex-wrap:wrap"><div style="width:342px">{seg("or")}</div><div class="card" style="width:300px;box-shadow:var(--shadow);border:1px solid var(--line);display:flex;flex-direction:column;gap:6px"><div style="display:flex;align-items:center;gap:10px"><span class="h2" style="font-size:24px">һәрьягы</span>{lv("B1")}</div><p class="b1" lang="ru">со всех сторон, вокруг</p></div><div style="width:390px;position:relative;height:64px">{mini_static}</div></div>')}
  {sect("Прогресс, кольцо, атна, фраза", f'<div style="display:flex;gap:32px;align-items:center;flex-wrap:wrap"><div style="width:240px"><div class="bar"><i style="width:34%"></i></div></div>{ring()}<div class="waffle"><i class="on"></i><i class="on"></i><i></i><i class="on"></i><i></i><i></i><i class="on"></i></div><div style="width:342px">{phrase_card()}</div></div>')}
  {sect("Тышлыклар һәм узоры Figma", f'<div style="display:flex;gap:16px;align-items:flex-end;flex-wrap:wrap">{"".join(cover(b) for b in BOOKS)}<div style="display:flex;gap:20px;margin-left:24px">{ornaments}</div></div>')}
  {sect("Нижняя навигация", f'<div style="width:390px;border:1px solid var(--line);border-radius:12px;overflow:hidden">{nav("lib")}</div>')}
</div>'''
add("Components.dc.html", "Компонентлар · якты", components(False), w=1000, h=1900, interactive=False)
add("ComponentsDark.dc.html", "Компонентлар · караңгы", components(True), w=1000, h=1900, dark=True, interactive=False)

# ================= canvas.json =================
rows = [
    ("1 · Кереш: Splash, Welcome (TAT / RU), выбор входа", ["Main.dc.html", "Welcome.dc.html", "WelcomeRu.dc.html", "AuthChoice.dc.html"]),
    ("2 · Теркәлү һәм керү — стекло на фото", ["Register.dc.html", "Login.dc.html"]),
    ("3 · Дәрәҗә тесты — дуги, звёзды, арка", ["LevelIntro.dc.html", "Quiz.dc.html", "LevelResult.dc.html"]),
    ("4 · Китапханә: иртә / көндез / кич", ["LibraryMorning.dc.html", "Library.dc.html", "LibraryEvening.dc.html"]),
    ("5–6 · Эзләү · Китап", ["Search.dc.html", "BookDetail.dc.html"]),
    ("7–8 · Ридер (оригинал, сүз, караңгы) · Recap", ["Reader.dc.html", "ReaderWord.dc.html", "ReaderDark.dc.html", "Recap.dc.html"]),
    ("9 · Алгарышың · Профиль", ["Progress.dc.html", "Profile.dc.html"]),
    ("10 · Сканер: камера → тану → текст с ползунком сложности → китап табылды", ["ScannerStart.dc.html", "ScannerBusy.dc.html", "ScannerText.dc.html", "ScannerTextAd.dc.html", "ScannerTextRu.dc.html", "ScannerMatch.dc.html"]),
    ("12 · Компонентлар: якты һәм караңгы тема", ["Components.dc.html", "ComponentsDark.dc.html"]),
]
def build_canvas(existing=None):
    canvas = existing or {"v": 3, "createdOnFiles": {"v": 1, "at": datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")},
                          "title": "TatLib — экраны", "launch": {"view": "canvas"}, "pages": [], "boards": {}, "order": [], "notes": {}, "designSystems": []}
    canvas["boards"] = {}; canvas["order"] = []
    canvas["notes"] = {k: v for k, v in canvas.get("notes", {}).items() if not k.startswith("row")}
    y = 0
    for i, (title, names) in enumerate(rows):
        x = 0; rw = 0
        for n in names:
            b = boards[n]; e = {"x": x, "y": y, "w": b["w"], "h": b["h"], "title": b["title"]}
            if b["interactive"]: e["is_interactive"] = True
            canvas["boards"][n] = e; canvas["order"].append(n); x += b["w"] + 80; rw = x - 80
        canvas["notes"][f"row{i+1}"] = {"x": 0, "y": y - 260, "text": title, "kind": "title1", "maxW": max(rw, 1400)}
        y += max(boards[n]["h"] for n in names) + 420
    return canvas

if __name__ == "__main__":
    for n, b in boards.items():
        with open(os.path.join(ROOT, n), "w", encoding="utf-8") as f: f.write(b["html"])
    existing = None
    p = os.path.join(ROOT, "canvas.json")
    if os.path.exists(p):
        try: existing = json.load(open(p, encoding="utf-8"))
        except Exception: existing = None
    json.dump(build_canvas(existing), open(p, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print(len(boards), "artboards")
