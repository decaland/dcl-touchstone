package com.github.decaland.touchstone.configs;

/**
 * Static centralized storage for constants used by Touchstone plugins:
 * versions of fundamental tools and languages, along with some other global
 * configuration values.
 */
public class BuildParametersManifest {

    /*
    Versions of fundamental tools and languages
     */

    // https://en.wikipedia.org/wiki/Java_version_history
    public static final String VERSION_JAVA = "11";

    // https://kotlinlang.org/releases.html
    public static final String VERSION_KOTLIN = "1.5.10";
    public static final String VERSION_KOTLIN_API;
    static {
        VERSION_KOTLIN_API = extractKotlinApiVersion(VERSION_KOTLIN);
    }
    public static final String VERSION_KOTLINX_SERIALIZATION = "1.2.1";
    public static final String VERSION_KOTLINX_COROUTINES = "1.5.0";

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    public static final String VERSION_KOTLIN_JACKSON = "2.12.3";

    // https://spring.io/projects/spring-cloud#learn
    // Go to https://start.spring.io, choose Spring Boot version, add any Spring Cloud dependency, then Explore
    public static final String VERSION_SPRING_CLOUD = "2020.0.3";

    // https://gradle.org/releases
    // https://docs.gradle.org/current/userguide/dependency_resolution.html#sub:cache_copy
    // Gradle 7.0 switched to Groovy 3, a major change
    // Gradle 7.1 changed signature of JavaPluginExtension.getDocsDir(), which we use
    public static final String MIN_VERSION_GRADLE = "7.1";


    /*
    Global configuration values
     */

    public static final String SOURCE_ENCODING = "UTF-8";
    public static final String REPO_MAVEN_RELEASES_NAME = "serpnetReleases";

    public static final String REPO_MAVEN_RELEASES_URL
            = "https://serpnet.jfrog.io/artifactory/decaland-maven-releases";
    public static final String REPO_MAVEN_SNAPSHOTS_NAME = "serpnetSnapshots";

    public static final String REPO_MAVEN_SNAPSHOTS_URL
            = "https://serpnet.jfrog.io/artifactory/decaland-maven-snapshots";
    public static final String PROP_KEY_SERPNET = "serpnet";
    public static final String PROP_KEY_SERPNET_USERNAME = "dcl.repository.maven.username";

    public static final String PROP_KEY_SERPNET_PASSWORD = "dcl.repository.maven.password";

    public static final String TASK_DOWNLOAD_DEPENDENCIES = "downloadDependencies";

    private BuildParametersManifest() {
    }

    private static String extractKotlinApiVersion(String kotlinVersion) {
        try {
            return kotlinVersion.substring(
                    0, kotlinVersion.indexOf('.', kotlinVersion.indexOf('.') + 1)
            );
        } catch (IndexOutOfBoundsException e) {
            return kotlinVersion;
        }
    }
}
