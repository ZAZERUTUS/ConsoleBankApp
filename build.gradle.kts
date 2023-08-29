plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    jacoco
}

//tasks.withType<Jar> {
tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    testCompileOnly("org.projectlombok:lombok:1.18.28")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.28")

    implementation("org.postgresql:postgresql:42.6.0")
//    compileOnly("org.postgresql:postgresql:42.6.0")

    implementation("log4j:log4j:1.2.17")
    shadow("log4j:log4j:1.2.17")

    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.1")


    implementation("org.yaml:snakeyaml:1.8")
    implementation("com.google.guava:guava:r05")

//    implementation("com.itextpdf:itextpdf:5.0.6")
//    implementation("org.apache.pdfbox:pdfbox:3.0.0")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

jacoco {
    toolVersion = "0.8.9"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
