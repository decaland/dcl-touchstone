package com.github.decaland.touchstone.configs;

public class BuildParametersManifest {
    public static final String VERSION_JAVA = "11";
    public static final String VERSION_KOTLIN = "1.4.10";
    public static final String VERSION_KOTLIN_API;
    public static final String MIN_VERSION_GRADLE = "6.7";
    public static final String REPO_MAVEN_RELEASES = "https://serpnet.jfrog.io/artifactory/decaland-maven-releases";
    public static final String REPO_MAVEN_SNAPSHOTS = "https://serpnet.jfrog.io/artifactory/decaland-maven-snapshots";
    public static final String SOURCE_ENCODING = "UTF-8";
    public static final String OUTPUT_ENCODING = "UTF-8";

    static {
        VERSION_KOTLIN_API = extractKotlinApiVersion();
    }

    private static String extractKotlinApiVersion() {
        try {
            return VERSION_KOTLIN.substring(0, VERSION_KOTLIN.indexOf('.', VERSION_KOTLIN.indexOf('.') + 1));
        } catch (IndexOutOfBoundsException e) {
            return VERSION_KOTLIN;
        }
    }
}
