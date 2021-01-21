package com.github.decaland.touchstone.configs.dependencies;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.decaland.touchstone.configs.dependencies.UniBomEntry.group;

public class DependencyBom {

    static final List<BomEntry> entries = new LinkedList<>();

    static {
        // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
        group("org.apache.commons")
                .name("commons-lang3")
                .version("3.11")
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

        // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
        group("com.github.javafaker")
                .name("javafaker")
                .version("1.0.2")
                .add();

        // https://junit.org/junit5/docs/snapshot/release-notes
        group("org.junit.jupiter")
                .name("junit-jupiter")
                .version("5.7.0")
                .add();
        // https://github.com/junit-team/junit4/releases
        group("junit")
                .name("junit")
                .version("4.13.1")
                .add();
    }

    private DependencyBom() {
    }

    public static Stream<BomEntry> getDependencyBom() {
        return entries.stream();
    }
}
