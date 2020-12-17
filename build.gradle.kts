plugins {
	kotlin("jvm") version "1.4.10"
}

repositories {
	maven {
		name = "TomTheGeek-repo"
		url = uri("https://maven.tomthegeek.ml/")
	}
	maven {
		name = "Jitpack"
		url = uri("https://jitpack.io/")
	}
	maven {
		name = "Mojang"
		url = uri("https://libraries.minecraft.net/")
	}
	mavenCentral()
	jcenter()
}

dependencies {
	implementation("me.geek.tom:thread-api:1.0+build.1")
	implementation("me.geek.tom:thread-network-strip-api:1.0+build.1")
	implementation("com.uchuhimo:konf-toml:0.23.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.13.1")
	implementation("com.github.PolyhedralDev:parsii:2827a24047")
	implementation("com.soywiz.korlibs.korio:korio-jvm:1.10.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
	kotlinOptions.jvmTarget = "1.8"
}
