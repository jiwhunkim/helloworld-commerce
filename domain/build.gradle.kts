plugins {
    id("buildlogic.kotlin-conventions")
    id("buildlogic.kotlin-logging-conventions")
    id("buildlogic.test-conventions")
}

dependencies {
    // jMolecules for DDD patterns
    implementation(libs.bundles.jmolecules)

    // Testing
    testImplementation(libs.bundles.kotest)
}
