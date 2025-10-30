# Bus Time Tracker 🚌⏱️

Приложение для Android для удобного учёта и отслеживания времени прибытия автобусов (вахты).

## О проекте

Bus Time Tracker позволяет отслеживать время прибытия и отправления автобусов, управлять расписанием вахтового транспорта и просматривать актуальную информацию о рейсах.

## Технические характеристики

- **Язык:** Kotlin
- **Минимальная версия Android:** API 21 (Android 5.0)
- **Целевая версия Android:** API 36
- **Архитектура:** Jetpack Compose
- **Версия приложения:** 2.1 (versionCode: 3)

##  🚀 Установка и сборка

Клонируйте репозиторий:
git clone [URL репозитория]

cd bustimetracker

Откройте проект в Android Studio через "Open an existing project"

Соберите проект: Build → Make Project (Ctrl+F9)

Запустите на устройстве/эмуляторе через Run → Run 'app'

## 📦 Сборка APK

Отладочная сборка:
./gradlew assembleDebug


Релизная сборка:
./gradlew assembleRelease


APK файлы будут созданы в папке `app/build/outputs/apk/`

## 🧪Тестирование

Unit тесты:
./gradlew test


Инструментальные тесты:
./gradlew connectedAndroidTest



## ⚙️ Основные зависимости

- Jetpack Compose (UI, Material3, Activity)
- Android KTX (Core, Lifecycle)
- Gson для работы с JSON
- JUnit и Espresso для тестирования

---

**Версия:** 2.1  
