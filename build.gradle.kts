import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.0.11.RELEASE"

    id("org.springframework.boot") version "2.6.2"
    // camunda 7.16.0

    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"

    // https://kotlinlang.org/docs/serialization.html#example-json-serialization
    kotlin("plugin.serialization") version "1.6.10"

    // https://kotlinlang.org/docs/no-arg-plugin.html
    //kotlin("plugin.noarg") version "1.6.10"

    // https://mvnrepository.com/artifact/org.openapi.generator/org.openapi.generator.gradle.plugin
    id("org.openapi.generator") version "5.3.1"

    // these are both needed for spingdoc
    // https://plugins.gradle.org/plugin/org.springdoc.openapi-gradle-plugin
    id ("org.springdoc.openapi-gradle-plugin") version "1.3.3"
    // https://plugins.gradle.org/plugin/com.github.johnrengelman.processes
    id ("com.github.johnrengelman.processes") version "0.5.0"

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
    gradlePluginPortal()

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

// https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-dependencies
extra["springCloudVersion"] = "2021.0.0"

// https://mvnrepository.com/artifact/org.testcontainers/testcontainers
extra["testcontainersVersion"] = "1.16.2" // 1.16.2 is ready, but not distibuted

// https://mvnrepository.com/artifact/org.camunda.bpm/camunda-engine
extra["camundaVersion"] = "7.17.0-alpha2" // "7.16.0" is compatible with spring boot 2.5.4

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

    implementation("org.springframework.boot:spring-boot-starter-hateoas") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }

    implementation("org.springframework.boot:spring-boot-starter-data-rest") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
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

    // https://mvnrepository.com/artifact/io.github.microutils/kotlin-logging
    implementation("io.github.microutils:kotlin-logging:2.1.21")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mssqlserver")

    // https://mvnrepository.com/artifact/io.mockk/mockk
    testImplementation ("io.mockk:mockk:1.12.2")

    // https://mvnrepository.com/artifact/com.ninja-squad/springmockk
    testImplementation("com.ninja-squad:springmockk:3.1.0")

    /***** test dependencies ****/
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
    testImplementation ("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation ("org.hamcrest:hamcrest-all:1.3")

    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    // assertj comes with spring boot - no need to add it
    //testImplementation("org.assertj:assertj-core:3.21.0")

    // https://mvnrepository.com/artifact/com.jayway.jsonpath/json-path
    testImplementation("com.jayway.jsonpath:json-path:2.6.0")

    // https://mvnrepository.com/artifact/io.rest-assured/spring-mock-mvc
    testImplementation("io.rest-assured:spring-mock-mvc:4.4.0")

    // https://mvnrepository.com/artifact/io.rest-assured/kotlin-extensions
    implementation("io.rest-assured:kotlin-extensions:4.4.0")

    // https://github.com/camunda/camunda-bpm-assert
    testImplementation("org.camunda.bpm.assert:camunda-bpm-assert:13.0.0")

    // https://github.com/camunda-community-hub/camunda-bpm-process-test-coverage
    testImplementation("org.camunda.bpm.extension:camunda-bpm-process-test-coverage-junit5:1.0.0")

    // https://mvnrepository.com/artifact/org.camunda.bpm.extension/camunda-bpm-junit5
    testImplementation("org.camunda.bpm.extension:camunda-bpm-junit5:1.0.2")

    // https://mvnrepository.com/artifact/org.camunda.bpm.extension.mockito/camunda-bpm-mockito
    testImplementation("org.camunda.bpm.extension.mockito:camunda-bpm-mockito:5.15.0")

    // https://mvnrepository.com/artifact/org.camunda.bpm.extension/camunda-bpm-assert-scenario
    testImplementation("org.camunda.bpm.extension:camunda-bpm-assert-scenario:1.1.1")

    // https://mvnrepository.com/artifact/io.toolisticon.addons.jgiven/jgiven-kotlin
    implementation("io.toolisticon.addons.jgiven:jgiven-kotlin:0.6.0")

    // https://mvnrepository.com/artifact/io.holunda.testing/camunda-bpm-jgiven
    implementation("io.holunda.testing:camunda-bpm-jgiven:0.0.8")

    // used to generate api model and controller interface
    // https://mvnrepository.com/artifact/io.swagger.core.v3/swagger-annotations
    implementation("io.swagger.core.v3:swagger-annotations:2.1.12")

    // https://github.com/OpenAPITools/openapi-generator
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.3.1")

    implementation("org.slf4j:slf4j-simple:1.7.32")

    // activation and imap provider are needed for jakarta mail 2.X
    // due to simple java mail dependency downgraded to jakarta mail 1.x
    implementation("jakarta.mail:jakarta.mail-api:2.0.1")
    implementation("com.sun.activation:jakarta.activation:2.0.1")
    implementation("com.sun.mail:imap:2.0.1")

    // removed 1.X line from use as simple java mail has upgraded to 2.x
    //implementation("jakarta.mail:jakarta.mail-api:1.6.7")
    //implementation("com.sun.mail:imap:1.6.7")
    //implementation("com.sun.activation:jakarta.activation:1.2.2")

    // it might make sense to use jakarta mail 1.X instead of 2.X
    // Simple java mail < 7.0.0 requires jakarta mail 1.X or javax.mail
    // https://mvnrepository.com/artifact/org.simplejavamail/simple-java-mail
    implementation("org.simplejavamail:simple-java-mail:7.0.0")

    // openapi in spring-boot-admin
    // https://mvnrepository.com/artifact/org.springdoc
    implementation("org.springdoc:springdoc-openapi-ui:1.6.3")
    implementation("org.springdoc:springdoc-openapi-webmvc-core:1.6.3")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.3")
    implementation("org.springdoc:springdoc-openapi-javadoc:1.6.3")
    implementation("org.springdoc:springdoc-openapi-hateoas:1.6.3")
    implementation("org.springdoc:springdoc-openapi-data-rest:1.6.3")
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
    inputSpec.set("$rootDir/specs/api.yaml")
    outputDir.set("$buildDir/generated")
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

// open api document generation
// https://github.com/springdoc/springdoc-openapi-gradle-plugin
//  ./gradlew or gradle clean generateOpenApiDocs
openApi {
    outputDir.set(file("$buildDir/docs"))
    outputFileName.set("api.json")
}

tasks.withType<KotlinCompile> {
    dependsOn ("openApiGenerate") // compile only after generation
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.test {
    // Use the built-in JUnit support of Gradle.
    useJUnitPlatform()
}
