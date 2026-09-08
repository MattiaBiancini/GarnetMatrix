plugins {
    id("java")
}

group = "me.mbiancini.garnetmatrix"
version = "1.0.0"

repositories {
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}