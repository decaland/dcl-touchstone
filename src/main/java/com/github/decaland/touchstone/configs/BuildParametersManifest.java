package com.github.decaland.touchstone.configs;

public class BuildParametersManifest {
    public static final String VERSION_JAVA = "11";
    public static final String VERSION_KOTLIN = "1.4.10";
    public static final String VERSION_KOTLIN_API;
    public static final String MIN_VERSION_GRADLE = "6.7.1";
    public static final String SOURCE_ENCODING = "UTF-8";
    public static final String OUTPUT_ENCODING = "UTF-8";

    public static final String REPO_MAVEN_RELEASES_NAME = "serpnetReleases";
    public static final String REPO_MAVEN_RELEASES_URL = "https://serpnet.jfrog.io/artifactory/decaland-maven-releases";

    public static final String REPO_MAVEN_SNAPSHOTS_NAME = "serpnetSnapshots";
    public static final String REPO_MAVEN_SNAPSHOTS_URL = "https://serpnet.jfrog.io/artifactory/decaland-maven-snapshots";

    public static final String PROP_KEY_SERPNET = "serpnet";
    public static final String PROP_KEY_SERPNET_USERNAME = "dcl.repository.maven.username";
    public static final String PROP_KEY_SERPNET_PASSWORD = "dcl.repository.maven.password";

    static {
        VERSION_KOTLIN_API = extractKotlinApiVersion();
    }

    private BuildParametersManifest() {
    }

    private static String extractKotlinApiVersion() {
        try {
            return VERSION_KOTLIN.substring(0, VERSION_KOTLIN.indexOf('.', VERSION_KOTLIN.indexOf('.') + 1));
        } catch (IndexOutOfBoundsException e) {
            return VERSION_KOTLIN;
        }
    }
}
