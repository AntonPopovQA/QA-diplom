# Инструкция по запуску проекта авто-тестов

### Перечень используемого ПО
* Операционная система Windows 10 Pro, версия 21H2, сборка ОС 19044.2006
* IntelliJ IDEA 2022.1.3 (Community Edition)
* Java:
  * OpenJDK build 221.5921.22 on June 21, 2022
  * OpenJDK Runtime version: 11.0.15+10-b2043.56 amd64
  * OpenJDK VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
* Docker Desktop 4.11.1 (84025)
* Google Chrome версия 105.0.5195.127
* Git version v2.37.1

### Порядок действий для запуска автотестов
1. Запустить Docker Desktop, дождаться пока приложение подключится к серверу
2. Запустить IntelliJ IDEA
3. Склонировать репозиторий c GitHub командой: git clone https://github.com/AspireVX15/QA-diplom.git
4. Открыть терминал IntelliJ IDEA и запустить контейнеры командой: docker-compose up
5. В терминале IntelliJ IDEA, отправить команду для запуска сервиса aqa-shop.jar с конфигурацией подключения 
к БД MySQL: java "-Dspring.datasource.url=jdbc:mysql://localhost:3306/app" -jar artifacts/aqa-shop.jar
6. В терминале IntelliJ IDEA запустить тесты с конфигурацией подключения к БД MySQL командой: 
./gradlew clean test --info "-Ddb.url=jdbc:mysql://localhost:3306/app"
7. Открыть терминал  IntelliJ IDEA, в котором запускался сервис aqa-shop.jar, нажать сочетание клавиш - "Ctrl+C" и дождаться пока приложение закроется
8. В файле build.gradle склонированного проекта в разделе “test” заменить строку “jdbc:mysql://localhost:3306/app” на “jdbc:postgresql://localhost:5432/app”, обновить build.gradle
9. В терминале IntelliJ IDEA, отправить команду для запуска сервиса aqa-shop.jar с конфигурацией подключения 
к БД PostgreSQL: java "-Dspring.datasource.url=jdbc:postgresql://localhost:5432/app" -jar artifacts/aqa-shop.jar
10. В терминале IntelliJ IDEA запустить тесты с конфигурацией подключения к БД PostgreSQL командой:
./gradlew clean test --info "-Ddb.url=jdbc:postgresql://localhost:5432/app"
11. Открыть терминал  IntelliJ IDEA, в котором запускался сервис aqa-shop.jar, нажать сочетание клавиш - "Ctrl+C" и дождаться пока приложение закроется
12. В терминале IntelliJ IDEA остановить контейнеры Docker командой: docker-compose down