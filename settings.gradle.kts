pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

rootProject.name = "helloworld-commerce"

includeBuild("domain")
includeBuild("data")
includeBuild("application")
includeBuild("app")
