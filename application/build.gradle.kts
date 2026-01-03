plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("com.helloworld.commerce:domain")
    implementation("org.springframework:spring-tx")


    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotest.extensions.spring)
}
