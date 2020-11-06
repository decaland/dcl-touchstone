package com.github.decaland.touchstone.configs;

import io.spring.gradle.dependencymanagement.dsl.DependenciesHandler;

public class DependencyVersionBom {

    public static void applyDependencyVersionConstraints(DependenciesHandler dependenciesHandler) {
        dependenciesHandler.dependency("org.apache.commons:commons-lang3:3.11");
        dependenciesHandler.dependency("org.apache.commons:commons-collections4:4.4");
        dependenciesHandler.dependency("commons-beanutils:commons-beanutils:1.9.4");

        dependenciesHandler.dependency("com.github.javafaker:javafaker:1.0.2");
    }
}
