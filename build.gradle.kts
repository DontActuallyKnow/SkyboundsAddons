plugins {
	id("fabric-loom") version "1.10-SNAPSHOT"
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm") version "2.1.20"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
	archivesName.set(project.property("archives_base_name") as String)
}

repositories {
	maven("https://oss.sonatype.org/content/repositories/snapshots/")
	maven("https://jitpack.io")
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")

	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

	// Fabric API. This is technically optional, but you probably want it anyway.
	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")

	implementation("org.reflections:reflections:0.10.2")

	implementation("org.lwjgl:lwjgl-nanovg:3.3.3")
	runtimeOnly("org.lwjgl:lwjgl-nanovg:3.3.3:natives-windows")
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand(mapOf("version" to inputs.properties["version"]))
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
	kotlinOptions {
		jvmTarget = "21"
	}
}

java {
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()

	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
	inputs.property("archivesName", project.base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}" }
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.property("archives_base_name") as String
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