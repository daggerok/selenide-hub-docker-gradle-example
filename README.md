# selenide | selenium-hub | docker | gradle [![Build Status](https://travis-ci.org/daggerok/selenide-hub-docker-gradle-example.svg?branch=master)](https://travis-ci.org/daggerok/selenide-hub-docker-gradle-example)
E2E testing using Selenide, Selenium Hub in Docker and Gradle build based on kotlin DSL

## build and run

```batch
gradlew assemble
gradlew composeUp
```

## test locally

```batch
gradlew :test
gradlew :test -P chrome
gradlew :test -P firefox
```

## test using selenium hub

```batch
gradlew :test -P remote
gradlew :test -P remote -P chrome
gradlew :test -P remote -P firefox
```
