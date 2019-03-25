package com.github.daggerok

import com.codeborne.selenide.CollectionCondition.sizeGreaterThan
import com.codeborne.selenide.Condition.*
import com.codeborne.selenide.Selectors.withText
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import org.openqa.selenium.By

fun find(query: String) = Selenide.`$`(query)
fun findAll(by: By) = Selenide.`$$`(by)

@Tags(
    Tag("E2E")
)
internal class MyRemoteE2eTest {

  internal val host = System.getProperty("host", "127.0.0.1") // Selenium Hub: "app.net"
  internal val baseUrl = "http://$host:8080"

  @Test
  internal fun testIndexPage() {
    open(baseUrl)

    find("#message")
        .shouldBe(exist)
        .shouldBe(visible)
        .shouldHave(exactTextCaseSensitive("Immediately"))

    find("#message-with-delay")
        .shouldBe(exist)
        .shouldBe(visible)
        .shouldHave(exactTextCaseSensitive("on DOMContentLoaded event..."))

    sleep(3500)

    find("#message-with-delay")
        .shouldBe(exist)
        .shouldBe(visible)
        .shouldHave(exactTextCaseSensitive("on setTimeout!"))
  }

  @Test
  internal fun testActuatorPage() {
    open("$baseUrl/actuator")

    find("body")
        .shouldBe(exist)
        .shouldBe(visible)

    findAll(withText(baseUrl))
        .filterBy(exist)
        .filterBy(visible)
        .shouldHave(sizeGreaterThan(0))
  }

  @AfterEach
  internal fun after() = close()
}
