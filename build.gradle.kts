import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    id("org.springframework.boot") version "2.5.4"
    // camunda 7.16.0

    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"

    id("org.openapi.generator") version "5.2.1"
}

group = "com.nikkijuk"
version = "0.0.2-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()

    maven(url = "https://app.camunda.com/nexus/content/groups/public")
}

// Get a SourceSet collection and add generated artifacts to it
sourceSets {
    main {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            // Gradle Kotlin for JVM plugin configures "src/main/kotlin" on its own
            kotlin.srcDirs("$buildDir/generated/src/main/kotlin".toString())
        }
    }
    test {
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            // Gradle Kotlin for JVM plugin configures "src/test/kotlin" on its own
            kotlin.srcDirs("$buildDir/generated/src/test/kotlin".toString())
        }
    }
}

extra["springCloudVersion"] = "2020.0.4"
extra["testcontainersVersion"] = "1.16.1" // 1.16.2 is ready, but not distibuted
extra["camundaVersion"] = "7.16.0" // compatible with spring boot 2.5.4

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.springframework.boot:spring-boot-starter-validation") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-rest") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.camunda.bpm.springboot:camunda-bpm-spring-boot-starter-webapp") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.camunda.bpm:camunda-engine-plugin-spin")

    implementation("org.camunda.spin:camunda-spin-dataformat-all")

    developmentOnly("org.springframework.boot:spring-boot-devtools") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.springframework.boot:spring-boot-starter-data-mongodb") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }

    implementation("org.springframework.boot:spring-boot-starter-jdbc") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }

    implementation("com.h2database:h2")

    implementation("org.flywaydb:flyway-core")

    implementation("io.github.microutils:kotlin-logging:2.0.11")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mssqlserver")

    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    // assert j comes with spring boot - no need to add it
    //testImplementation("org.assertj:assertj-core:3.21.0")

    // https://github.com/camunda/camunda-bpm-assert
    testImplementation("org.camunda.bpm.assert:camunda-bpm-assert:13.0.0")

    // https://github.com/camunda-community-hub/camunda-bpm-process-test-coverage
    testImplementation("org.camunda.bpm.extension:camunda-bpm-process-test-coverage-junit5:1.0.0")

    // https://mvnrepository.com/artifact/org.camunda.bpm.extension/camunda-bpm-junit5
    testImplementation("org.camunda.bpm.extension:camunda-bpm-junit5:1.0.2")

    // https://mvnrepository.com/artifact/org.camunda.bpm.extension.mockito/camunda-bpm-mockito
    testImplementation("org.camunda.bpm.extension.mockito:camunda-bpm-mockito:5.15.0")

    // used to generate api model and controller interface
    implementation("io.swagger.core.v3:swagger-annotations:2.1.9")

    // https://github.com/OpenAPITools/openapi-generator
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.2.1")

    implementation("org.slf4j:slf4j-simple:1.7.32")

    // activation and imap provider are needed for jakarta mail 2.X
    implementation("jakarta.mail:jakarta.mail-api:2.0.1")
    implementation("com.sun.activation:jakarta.activation:2.0.1")
    implementation("com.sun.mail:imap:2.0.1")

    // it might make sense to use jakarta mail 1.X instead of 2.X
    // Simple java mail requires jakarta mail 1.X or javax.mail
    implementation("org.simplejavamail:simple-java-mail:6.6.1")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.camunda.bpm:camunda-bom:${property("camundaVersion")}")
    }
}

// see supported options for kotlin-spring here
// https://openapi-generator.tech/docs/generators/kotlin-spring/
openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/specs/api.yaml".toString())
    outputDir.set("$buildDir/generated".toString())
    modelPackage.set("com.nikkijuk.pigeongram.generated.model")
    apiPackage.set("com.nikkijuk.pigeongram.generated.api")
    //invokerPackage.set("com.nikkijuk.pigeongram.generated.invoker")
    configOptions.set(
        mapOf(
            // it's important to generate only interfaces
            // otherwise one needs to start from service interface
            "interfaceOnly" to "true",

            // one can generate service api also
            // this is especially useful if one generated complete controller implementation
            //"serviceInterface" to "true",

            // there's quite some generation options - just play with them to see what happens
            //"delegatePattern" to "true",

            "useBeanValidation" to "false",
            "enumPropertyNaming" to "UPPERCASE",
            "dateLibrary" to "java8"
        )
    )
}

tasks.withType<KotlinCompile> {
    dependsOn ("openApiGenerate") // compile only after generation
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    // Use the built-in JUnit support of Gradle.
    useJUnitPlatform()
}
