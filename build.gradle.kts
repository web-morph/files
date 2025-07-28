var javaVersion = 17;
group = "com.github.webmorph"
version = "1.2.0"

plugins {
    id("java-library")
    id("maven-publish")
    id("io.spring.dependency-management").version("1.1.7")
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

repositories {
    mavenCentral()
    maven("https://repo.billmarssoft.com/public/")
}

dependencies {
    // Spring
    api("org.springframework.boot:spring-boot-starter-rsocket:3.5.0")
    api("org.springframework.boot:spring-boot-starter-webflux:3.5.0")
    api("org.springframework.boot:spring-boot-configuration-processor:3.5.0")

    // EventBus
    api("com.github.webmorph:eventbus:1.0.3")

    // MimeType
    api("org.apache.tika:tika-core:3.2.0")

    // OpenCV
    api("org.bytedeco:javacv:1.5.12")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks {
    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        from(javadoc)
    }
    javadoc {
        options.encoding = "UTF-8"
        options.memberLevel = JavadocMemberLevel.PUBLIC
        isFailOnError = false
    }
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(javaVersion)
    }
    build {
        dependsOn("sourcesJar", "javadocJar")
    }
    jar {
        enabled = true
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
    repositories {
        maven {
            name = "BillmarsSoft"
            url = uri("https://repo.billmarssoft.com/releases/")
            credentials {
                username = System.getenv("REPOSITORY_USERNAME")
                password = System.getenv("REPOSITORY_PASSWORD")
            }
        }
    }
}