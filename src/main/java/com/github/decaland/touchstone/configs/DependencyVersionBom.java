package com.github.decaland.touchstone.configs;

import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler;
import org.jetbrains.annotations.NotNull;

public class DependencyVersionBom {

    public static void applyDependencyVersionConstraints(@NotNull DependenciesHandler dependenciesHandler) {
        // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
        dependenciesHandler.dependency("org.apache.commons:commons-lang3:3.11");
        // https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
        dependenciesHandler.dependency("org.apache.commons:commons-collections4:4.4");
        // https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils
        dependenciesHandler.dependency("commons-beanutils:commons-beanutils:1.9.4");

        // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
        dependenciesHandler.dependency("com.github.javafaker:javafaker:1.0.2");

        // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
        dependenciesHandler.dependency("org.junit.jupiter:junit-jupiter:5.7.0");
        // https://mvnrepository.com/artifact/junit/junit
        dependenciesHandler.dependency("junit:junit:4.13.1");
    }
}
