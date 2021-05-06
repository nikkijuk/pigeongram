import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.spring") version "1.5.0"
    kotlin("plugin.jpa") version "1.5.0"

    // open api generator 5.1.0 doesn't work with gradle 7.X - 5.1.1 is coming very soon!
    id("org.openapi.generator") version "5.1.0"
}

group = "com.nikkijuk"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
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

extra["azureVersion"] = "3.3.0"
extra["springCloudVersion"] = "2020.0.2"
extra["testcontainersVersion"] = "1.15.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.springframework.boot:spring-boot-starter-validation") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }
    implementation("com.azure.spring:azure-spring-boot-starter") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
        exclude("com.sun.mail", "javax.mail")
    }

    // TODO: define azure.storage.accountName
    /*
    implementation("com.azure.spring:azure-spring-boot-starter-storage") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("com.sun.mail", "javax.mail")
    }
     */

    /*
    TODO: Correct the classpath of your application so that it contains a single,
     compatible version of org.springframework.vault.support.SslConfiguration
    implementation("org.springframework.cloud:spring-cloud-starter-vault-config") {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    exclude("com.sun.mail", "javax.mail")
    }

     */
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

    // version 3.5.X of cosmos db spring data didn't work with data classes as expected,
    // but 3.6.X seems to be ok with them
    implementation("com.azure:azure-spring-data-cosmos:3.6.0")

    implementation("org.flywaydb:flyway-core")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.microsoft.sqlserver:mssql-jdbc")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mssqlserver")

    // used to generate api model and controller interface
    implementation("io.swagger.core.v3:swagger-annotations:2.1.9")

    // should be updated to 5.1.1 as soon as it's available
    // https://github.com/OpenAPITools/openapi-generator
    implementation("org.openapitools:openapi-generator-gradle-plugin:5.1.0")

    implementation("org.slf4j:slf4j-simple:1.7.30")

    // activation and imap provider are needed for jakarta mail 2.X
    implementation("jakarta.mail:jakarta.mail-api:2.0.1")
    implementation("com.sun.activation:jakarta.activation:2.0.1")
    implementation("com.sun.mail:imap:2.0.1")

    // it might make sense to use jakarta mail 1.X instead of 2.X
    // Simple java mail requires jakarta mail 1.X or javax.mail
    implementation("org.simplejavamail:simple-java-mail:6.5.2")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
        mavenBom("com.azure.spring:azure-spring-boot-bom:${property("azureVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

// TODO: generation using version 5.1.0 of open api generator doesn't work with gradle 7
// NOTE: use gradle 6.8.x to get model generated
// Test with: gradle openApiGenerate --scan
// Error: Type 'GenerateTask' property 'input' annotated with @Internal should not be also annotated with @Input.
// https://docs.gradle.org/7.0/userguide/validation_problems.html#ignored_property_must_not_be_annotated
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

tasks.withType<Test> {
    useJUnitPlatform()
}


