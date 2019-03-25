import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  war
  id("org.jetbrains.kotlin.jvm")
  id("org.springframework.boot")
  id("io.spring.dependency-management")
  id("org.jetbrains.kotlin.plugin.spring")
}

val javaVersion = JavaVersion.VERSION_1_8
val junitJupiterVersion = "5.4.0"
val kotlinVersion = "1.3.21"

repositories {
  mavenCentral()
  maven(url = "https://repo.spring.io/snapshot")
  maven(url = "https://repo.spring.io/milestone")
}

java {
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

tasks.withType<Test> {
  useJUnitPlatform() // useJUnit()
  testLogging {
    showExceptions = true
    showStandardStreams = true
    events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
  }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-hateoas")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  annotationProcessor("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  runtimeOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.projectreactor:reactor-test")

  implementation(kotlin("stdlib-jdk8"))
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation(platform("org.junit:junit-bom:$junitJupiterVersion"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
  testRuntime("org.junit.platform:junit-platform-launcher")
}

tasks {
  getByName("clean") {
    doLast {
      delete(project.buildDir)
    }
  }
}

the<SourceSetContainer>()["main"].java.srcDir("src/main/kotlin")
the<SourceSetContainer>()["test"].java.srcDir("src/test/kotlin")

//springBoot {
//  mainClassName = "com.github.daggerok.SpringBootGradleKotlinDslExampleApplication"
//}
