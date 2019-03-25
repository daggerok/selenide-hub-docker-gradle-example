import com.avast.gradle.dockercompose.RemoveImages
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    idea
    base
    id("org.jetbrains.kotlin.jvm") version "1.3.21"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.21"
    id("com.avast.gradle.docker-compose").version("0.9.1")
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    id("org.springframework.boot") version "2.1.3.RELEASE" apply false
}

val javaVersion = JavaVersion.VERSION_1_8
val junitJupiterVersion = "5.4.0"
val selenideVersion = "5.2.2"
val kotlinVersion = "1.3.21"
val gradleVersion = "5.3"

allprojects {
    group = "com.github.daggerok"
    version = "1.0-SNAPSHOT"

    extra["kotlin.version"] = kotlinVersion
    extra["junit-jupiter.version"] = junitJupiterVersion
}

repositories {
    mavenCentral()
}

this.apply(plugin = "java")
this.java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

this.the<SourceSetContainer>()["main"].java.srcDir("src/main/kotlin")
this.the<SourceSetContainer>()["test"].java.srcDir("src/test/kotlin")

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "$javaVersion"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation(platform("com.codeborne:selenide:$selenideVersion"))
    testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))

    testImplementation("org.slf4j:jul-to-slf4j:1.7.25")
    testImplementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("com.codeborne:selenide")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntime("org.junit.platform:junit-platform-launcher")
}

defaultTasks("assemble")

// wrapper:
tasks.withType<Wrapper>().configureEach {
    gradleVersion = gradleVersion
    distributionType = Wrapper.DistributionType.BIN
}

// docker:
val appAssemble = tasks.getByPath(":app:assemble")
val composeUp = tasks.getByPath("composeUp")
val tests = tasks.getByPath("test")

tests.shouldRunAfter(appAssemble, composeUp)
composeUp.dependsOn(appAssemble)
composeUp.shouldRunAfter(appAssemble)

dockerCompose {
    projectName = "e2e"
    removeImages = RemoveImages.Local
    isRemoveContainers = true
    isRemoveOrphans = true
    isRemoveVolumes = true
    isForceRecreate = true
    isBuildBeforeUp = true
    isIgnorePushFailure = true
}

// testing:
val e2eProps: MutableMap<String, Any> = System.getProperties().mapKeys { it.key.toString() }.toMutableMap()
if (null != project.findProperty("firefox")) e2eProps["selenide.browser"] = "firefox"
if (null != project.findProperty("chrome")) e2eProps["selenide.browser"] = "chrome"
if (null != project.findProperty("remote")) {
    e2eProps["selenide.remote"] = "http://127.0.0.1:4444/wd/hub"
    e2eProps["host"] = "app.net" // important: "app.net", not just "app"
}

tasks.withType<Test> {
    doFirst {
        systemProperties = e2eProps.toMap()
    }
    doLast {
        println(
            systemProperties
                .filter {
                    it.toString().toLowerCase().contains("selenide")
                        || it.toString().toLowerCase().contains("host") }
        )
    }
    /*
    useJUnitPlatform {
        val groups = (project.findProperty("groups") ?: "LOCAL") as String
        includeTags(groups)
    }
    */
    useJUnitPlatform() // useJUnit()
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events(PASSED, SKIPPED, FAILED)
    }
    // start tests every time, even when code not changed
    outputs.upToDateWhen { false }
}
