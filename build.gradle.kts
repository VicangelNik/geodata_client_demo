plugins {
    id("java")
    id("groovy")
}

group = "gr.uoa.di"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.spockframework:spock-core:2.4-M1-groovy-4.0")
    testImplementation("com.github.tomakehurst:wiremock-jre8:2.25.1")
    testImplementation("org.apache.groovy:groovy:4.0.12")
}

tasks.test {
    useJUnitPlatform()
}