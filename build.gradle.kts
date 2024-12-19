plugins {
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.2"
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://mirai.mamoe.net/mirai-console-repository")
    maven("https://maven.aliyun.com/repository/public")  // 阿里云仓库
}

dependencies {
    implementation("net.mamoe:mirai-console:2.8.0")  // Mirai Console 依赖
    implementation(kotlin("stdlib"))
    implementation ("com.alibaba:dashscope-sdk-java:2.6.10")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0" ) // 添加 Jackson Kotlin 模块
}