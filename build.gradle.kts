plugins {
	id("fabric-loom") version "1.6-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
	archivesName.set(project.property("archives_base_name") as String)
}

repositories {
	mavenCentral()
	maven {
		url = uri("https://maven.notenoughupdates.org/releases/")
	}
}

//TODO: MAKE SHADOW JAR WORK INSTEAD OF USING INCLUDE FOR THE LIBRARIES
val shadowModImpl by configurations.creating {
	configurations.modImplementation.get().extendsFrom(this)
}

dependencies {
	// To change the versions see the gradle.properties file
	"minecraft"("com.mojang:minecraft:${project.property("minecraft_version")}")
	"mappings"("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
	"modImplementation"("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	"modImplementation"("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
	"modImplementation"("org.notenoughupdates.moulconfig:modern:${project.property("moulconfig_version")}")

	implementation("org.reflections:reflections:0.9.12")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")

	include("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")
	include("org.notenoughupdates.moulconfig:modern:${project.property("moulconfig_version")}")

}

//TODO: MAKE SHADOW JAR WORK INSTEAD OF USING INCLUDE FOR THE LIBRARIES
tasks.shadowJar {
	// Make sure to relocate MoulConfig to avoid version clashes with other mods
	configurations = listOf(shadowModImpl)
	relocate("io.github.notenoughupdates.moulconfig", "${project.property("maven_group")}.dependencies.moulconfig")
	relocate("net.fabricmc.language.kotlin", "${project.property("maven_group")}.dependencies.fabric-language-kotlin")
}

tasks.processResources {
	inputs.property("version", project.version)
	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to project.version))
	}
}

tasks.withType<JavaCompile> {
	options.release.set(17)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.extensions.getByType<BasePluginExtension>().archivesName.get()}" }
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.extensions.getByType<BasePluginExtension>().archivesName.get()
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
