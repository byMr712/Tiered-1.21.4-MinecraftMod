> **Language:** Русский · [English](README.en.md)

# Tiered (Minecraft 1.21.4 Fabric Port)

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue.svg)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue.svg)
![ModMenu](https://img.shields.io/badge/ModMenu-Supported-blue.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Порт, оптимизация и адаптация мода **Tiered** для **Minecraft 1.21.4 (Fabric)** от **byMr712**.

Оригинальный разработчик: [Draylar/tiered](https://github.com/Draylar/tiered).

---

## О моде

**Tiered** добавляет RPG-механику рангов (префиксов и модификаторов качества) для оружия, инструментов и брони в Minecraft. Создаваемая или выпадающая экипировка наделяется уникальными свойствами и атрибутами.

---

## Возможности

- **Система рангов экипировки**: броня, инструменты и оружие получают случайные префиксы (от обычных до легендарных), влияющие на характеристики (урон, скорость атаки, защиту, скорость добычи, здоровье, дальность удара).
- **Перековка предметов**: возможность перековывать экипировку на наковальне или рефордж-станции для получения лучших модификаторов.
- **Поддержка конвенций и тегов**: совместимость с `c:*` и ванильными тегами `minecraft:*`.

---

## Что изменено в порте для 1.21.4 (byMr712)

- **Портирование на Minecraft 1.21.4 & Data Components**:
  - Полная миграция с устаревшего NBT на систему Data Components 1.21.4 (`NbtComponent`, `AttributeModifiersComponent`, `EQUIPPABLE`).
  - Обновление сетевого взаимодействия через `PayloadTypeRegistry` и `CustomPayload`.
  - Заменена внешняя библиотека `reach-entity-attributes` на нативные ванильные атрибуты 1.21.4 (`player.block_interaction_range`, `player.entity_interaction_range`).
- **Глубокая оптимизация производительности (Performance Engine)**:
  - **Кэширование атрибутов**: все допустимые модификаторы и ранги кэшируются в `ConcurrentHashMap`. Полностью устранены повторные сканирования реестров и аллокации памяти при каждом запросе.
  - **Оптимизация `ItemEntity`**: устранены непрерывные проверки в каждом тике (`tick()`) для лежащих в мире предметов. Ранг генерируется один раз при создании стака, а не-экипируемые предметы отсекаются мгновенно.
  - **Быстрая проверка компонентов**: устранено паразитное копирование NBT-структур (`copyNbt()`).
  - **Zero-Allocation итерация слотов**: заменены вызовы `Arrays.asList` на прямые итерации по массивам слотов экипировки.
- **Полная локализация**:
  - Добавлена полная русская (`ru_ru.json`) и английская (`en_us.json`) локализация.
- Настроена быстрая сборка и автокопирование в лаунчер.

---

## Установка

1. Скачайте последнюю версию со страницы [GitHub Releases](https://github.com/byMr712/Tiered-1.21.4-MinecraftMod/releases).
2. Требуются:
   - [Fabric Loader](https://fabricmc.net/) (Minecraft 1.21.4)
   - [Fabric API](https://modrinth.com/mod/fabric-api)
3. Поместите `.jar` файл в папку `mods`.
4. Запустите игру.

---

## Сборка

1. Требуется Java 21 и Fabric Loader для Minecraft 1.21.4.
2. Для сборки выполните:
   ```bash
   ./gradlew build
   ```
3. Собранный файл находится в `build/libs/Tiered-1.21.4-byMr712.jar`.

---

## Авторы и лицензия

- Оригинальный автор: [Draylar](https://github.com/Draylar).
- Порт и оптимизация для 1.21.4: [Mr712](https://github.com/byMr712).
- Распространяется под лицензией [MIT License](LICENSE).
