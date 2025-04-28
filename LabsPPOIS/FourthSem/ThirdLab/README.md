# Документация проекта Arkanoid

## Содержание
- [Документация проекта Arkanoid](#документация-проекта-arkanoid)
  - [Содержание](#содержание)
  - [Введение](#введение)
  - [Архитектура проекта](#архитектура-проекта)
  - [Основные компоненты](#основные-компоненты)
    - [Arkanoid.java](#arkanoidjava)
    - [ArkanoidEntityFactory.java](#arkanoidentityfactoryjava)
    - [Компоненты (components)](#компоненты-components)
    - [Система данных](#система-данных)
  - [Игровая механика](#игровая-механика)
    - [Типы бонусов:](#типы-бонусов)
  - [Как запустить проект](#как-запустить-проект)
  - [Управление в игре](#управление-в-игре)

## Введение

Данный проект представляет собой реализацию классической игры Arkanoid на Java с использованием фреймворка FXGL. Игра предоставляет пользователю возможность управлять платформой для отбивания мяча, разрушая блоки на игровом поле.

## Архитектура проекта

Проект использует архитектуру MVC (Model-View-Controller), что обеспечивает хорошую модульность и расширяемость кода


## Основные компоненты

### Arkanoid.java

Главный класс приложения, который инициализирует игру, настраивает физику, обрабатывает ввод пользователя и управляет игровым циклом.

```java
public class Arkanoid extends GameApplication {
    // Инициализация приложения
    @Override
    protected void onPreInit() {
        FXGL.loopBGM("background_music.mp3");
        // ...
    }
    
    // Настройка физики и обработка коллизий
    @Override
    protected void initPhysics() {
        FXGL.onCollision(EntityType.PADDLE, EntityType.BUFF, (paddle, buff) -> {
            buff.getComponent(BuffComponent.class).voidSpecialEffect();
        });
        // ...
    }
    
    // Создание игровых объектов
    @Override
    protected void initGame() {
        paddle = arkanoidEntityFactory.createPaddle(new SpawnData(350, 500));
        // ...
    }
    
    // Обработка пользовательского ввода
    @Override
    protected void initInput() {
        FXGL.getInput().addAction(new UserAction("Move Left") {
            // ...
        }, KeyCode.LEFT);
        // ...
    }
}
```

### ArkanoidEntityFactory.java

Класс-фабрика, отвечающий за создание игровых сущностей (платформа, мяч, блоки, стены и т.д.).

```java
public class ArkanoidEntityFactory implements EntityFactory {
    @Spawns("Paddle")
    public Entity createPaddle(SpawnData data) {
        var physics = new PhysicsComponent();
        physics.setBodyType(BodyType.KINEMATIC);

        return FXGL.entityBuilder(data)
                .type(EntityType.PADDLE)
                .at(data.getX(), data.getY())
                .with(new CollidableComponent(true))
                .with(physics)
                .viewWithBBox(FXGL.texture("paddle.png"))
                .build();
    }
    
    @Spawns("Ball")
    public Entity createBall(SpawnData data, float speedPx) {
        // Создание мяча
    }
    
    @Spawns("Brick")
    public Entity createBrick(SpawnData data, int health) {
        // Создание блока
    }
    
    // Другие методы для создания сущностей
}
```

### Компоненты (components)

Игра использует компонентную архитектуру, где каждый компонент отвечает за определенное поведение игровой сущности:

- **BallMovementComponent** - управляет движением мяча
- **BrickHealthComponent** - отвечает за здоровье блоков и их разрушение
- **BuffComponent** - реализует бонусы и их эффекты
- **BuffMovementComponent** - управляет движением бонусов
- **OutOfBoundsComponent** - отслеживает выход объектов за границы экрана

### Система данных

- **ConfigManager** - управление конфигурацией приложения
- **LevelManager** - загрузка и инициализация уровней
- **ScoreTableManager** - управление таблицей результатов

## Игровая механика

Игровой процесс включает в себя:
1. Управление платформой для отбивания мяча
2. Разрушение блоков мячом
3. Сбор выпадающих бонусов для получения различных эффектов
4. Прохождение уровней и набор очков
5. Ведение таблицы рекордов

### Типы бонусов:
- Увеличение ширины платформы
- Увеличение силы мяча
- Способность мяча разбивать все блоки
- Создание дополнительных стен
- Негативные бонусы (смерть)

## Как запустить проект

1. Убедитесь, что у вас установлена Java 11 или выше
2. Соберите проект с помощью Maven:
   ```
   mvn clean package
   ```
3. Запустите полученный JAR-файл:
   ```
   java -jar target/arkanoid-1.0.jar
   ```

## Управление в игре

- **Стрелка влево** - движение платформы влево
- **Стрелка вправо** - движение платформы вправо
- **ESC** - выход в главное меню