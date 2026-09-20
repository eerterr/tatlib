package com.tatlib.app.data

/** Уровень и возраст пользователя — единственный источник для экранов «Алгарышың» и «Профиль». */
// TODO backend: /api/user — скопировано из backend/tatar_adaptive.db users (age 18, profile_level B1) и user_level_state (B1) 20.09.2026
object UserMeta {
    val level = TatarLevel.B1
    val age = 18
}
