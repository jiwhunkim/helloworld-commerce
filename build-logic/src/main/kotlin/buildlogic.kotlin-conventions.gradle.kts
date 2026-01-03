import org.gradle.jvm.toolchain.JavaLanguageVersion

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/snapshot") }
}

plugins {
    kotlin("jvm")
}

group = "com.helloworld.commerce"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}
