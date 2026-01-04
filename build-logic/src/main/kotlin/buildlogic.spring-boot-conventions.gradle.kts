plugins {
    kotlin("jvm")
//    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.allopen")
    id("buildlogic.kotlin-conventions")
    id("buildlogic.kotlin-logging-conventions")
    id("buildlogic.test-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

//kapt {
//    keepJavacAnnotationProcessors = true
//}
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")


    testImplementation(libs.findBundle("kotest").get())
    testImplementation(libs.findLibrary("kotest.extensions.spring").get())

    implementation(platform(libs.findLibrary("jmolecules.bom").get()))
    implementation(libs.findBundle("jmolecules").get())
    implementation(libs.findLibrary("jmolecules.hexagonal.architecture").get())
}


allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}
