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
    public static final String VERSION_KOTLIN = "1.4.30";
    public static final String VERSION_KOTLIN_API;
    static {
        VERSION_KOTLIN_API = extractKotlinApiVersion(VERSION_KOTLIN);
    }
    public static final String VERSION_KOTLINX_SERIALIZATION = "1.1.0-RC";
    public static final String VERSION_KOTLINX_COROUTINES = "1.4.2";

    // https://gradle.org/releases
    public static final String MIN_VERSION_GRADLE = "6.0";


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
