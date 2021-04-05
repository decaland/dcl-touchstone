package com.github.decaland.touchstone.configs.dependencies;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.decaland.touchstone.configs.BuildParametersManifest.*;
import static com.github.decaland.touchstone.configs.dependencies.MultiBomEntry.library;
import static com.github.decaland.touchstone.configs.dependencies.UniBomEntry.group;

public class DependencyBom {

    static final List<BomEntry> entries = new LinkedList<>();

    static {

        /*
        Kotlin libraries
         */
        group("org.jetbrains.kotlinx")
                .name("kotlinx-serialization-json")
                .version(VERSION_KOTLINX_SERIALIZATION)
                .add();
        group("org.jetbrains.kotlinx")
                .name("kotlinx-coroutines-core")
                .version(VERSION_KOTLINX_COROUTINES)
                .add();
        group("com.fasterxml.jackson.module")
                .name("jackson-module-kotlin")
                .version(VERSION_KOTLIN_JACKSON)
                .add();

        /*
        SPRING FRAMEWORK
         */

        // Versions of Spring components are included with the Spring plugin


        /*
        UTILITY LIBRARIES
         */

        // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
        group("org.apache.commons")
                .name("commons-lang3")
                .version("3.12.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
        group("org.apache.commons")
                .name("commons-collections4")
                .version("4.4")
                .add();
        // https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils
        group("commons-beanutils")
                .name("commons-beanutils")
                .version("1.9.4")
                .add();


        /*
        SPECIALIZED TOOLS
         */

        // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
        group("com.github.javafaker")
                .name("javafaker")
                .version("1.0.2")
                .add();


        /*
        TESTING LIBRARIES
         */

        // https://junit.org/junit5/docs/snapshot/release-notes
        group("org.junit.jupiter")
                .name("junit-jupiter")
                .version("5.7.1")
                .add();
        // https://github.com/junit-team/junit4/releases
        group("junit")
                .name("junit")
                .version("4.13.2")
                .add();
        // https://mvnrepository.com/artifact/org.mockito/mockito-core
        library("org.mockito")
                .name("mockito-core")
                .name("mockito-junit-jupiter")
                .version("3.8.0")
                .add();
        // https://mvnrepository.com/artifact/org.assertj/assertj-core
        group("org.assertj")
                .name("assertj-core")
                .version("3.19.0")
                .add();
        // https://mvnrepository.com/artifact/org.hamcrest/hamcrest
        group("org.hamcrest")
                .name("hamcrest")
                .version("2.2")
                .add();
        // https://mvnrepository.com/artifact/org.spockframework/spock-core
        group("org.spockframework")
                .name("spock-core")
                .version("1.3-groovy-2.5")
                .add();
    }

    private DependencyBom() {
    }

    public static Stream<BomEntry> getDependencyBom() {
        return entries.stream();
    }
}
