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
        APACHE COMMONS
         */

        // https://mvnrepository.com/artifact/org.apache.bcel/bcel
        group("org.apache.bcel")
                .name("bcel")
                .version("6.5.0")
                .add();
        // https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils
        group("commons-beanutils")
                .name("commons-beanutils")
                .version("1.9.4")
                .add();
        // https://mvnrepository.com/artifact/org.apache.bsf/bsf-api
        group("org.apache.bsf")
                .name("bsf-api")
                .version("3.1")
                .add();
        // https://mvnrepository.com/artifact/commons-chain/commons-chain
        group("commons-chain")
                .name("commons-chain")
                .version("1.2")
                .add();
        // https://mvnrepository.com/artifact/commons-cli/commons-cli
        group("commons-cli")
                .name("commons-cli")
                .version("1.4")
                .add();
        // https://mvnrepository.com/artifact/commons-codec/commons-codec
        group("commons-codec")
                .name("commons-codec")
                .version("1.15")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
        group("org.apache.commons")
                .name("commons-collections4")
                .version("4.4")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-compress
        group("org.apache.commons")
                .name("commons-compress")
                .version("1.20")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-configuration2
        group("org.apache.commons")
                .name("commons-configuration2")
                .version("2.7")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-crypto
        group("org.apache.commons")
                .name("commons-crypto")
                .version("1.1.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-csv
        group("org.apache.commons")
                .name("commons-csv")
                .version("1.8")
                .add();
        // https://mvnrepository.com/artifact/commons-daemon/commons-daemon
        group("commons-daemon")
                .name("commons-daemon")
                .version("1.2.4")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-dbcp2
        group("org.apache.commons")
                .name("commons-dbcp2")
                .version("2.8.0")
                .add();
        // https://mvnrepository.com/artifact/commons-dbutils/commons-dbutils
        group("commons-dbutils")
                .name("commons-dbutils")
                .version("1.7")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-digester3
        group("org.apache.commons")
                .name("commons-digester3")
                .version("3.2")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-email
        group("org.apache.commons")
                .name("commons-email")
                .version("1.5")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-exec
        group("org.apache.commons")
                .name("commons-exec")
                .version("1.3")
                .add();
        // https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload
        group("commons-fileupload")
                .name("commons-fileupload")
                .version("1.4")
                .add();
        // https://mvnrepository.com/artifact/commons-io/commons-io
        group("commons-io")
                .name("commons-io")
                .version("2.8.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-jci-core
        group("org.apache.commons")
                .name("commons-jci-core")
                .version("1.1")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-jcs-core
        group("org.apache.commons")
                .name("commons-jcs-core")
                .version("2.2.1")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-jexl
        group("org.apache.commons")
                .name("commons-jexl")
                .version("2.1.1")
                .add();
        // https://mvnrepository.com/artifact/commons-jxpath/commons-jxpath
        group("commons-jxpath")
                .name("commons-jxpath")
                .version("1.3")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
        group("org.apache.commons")
                .name("commons-lang3")
                .version("3.12.0")
                .add();
        // https://mvnrepository.com/artifact/commons-logging/commons-logging
        group("commons-logging")
                .name("commons-logging")
                .version("1.2")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-math3
        group("org.apache.commons")
                .name("commons-math3")
                .version("3.6.1")
                .add();
        // https://mvnrepository.com/artifact/commons-net/commons-net
        group("commons-net")
                .name("commons-net")
                .version("3.8.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-pool2
        group("org.apache.commons")
                .name("commons-pool2")
                .version("2.9.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-proxy
        group("org.apache.commons")
                .name("commons-proxy")
                .version("1.0")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-rng-simple
        group("org.apache.commons")
                .name("commons-rng-simple")
                .version("1.3")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-text
        group("org.apache.commons")
                .name("commons-text")
                .version("1.9")
                .add();
        // https://mvnrepository.com/artifact/commons-validator/commons-validator
        group("commons-validator")
                .name("commons-validator")
                .version("1.7")
                .add();
        // https://mvnrepository.com/artifact/org.apache.commons/commons-vfs2
        group("org.apache.commons")
                .name("commons-vfs2")
                .version("2.8.0")
                .add();


        /*
        GOOGLE GUAVA
         */

        // https://mvnrepository.com/artifact/com.google.guava/guava
        group("com.google.guava")
                .name("guava")
                .version("30.1.1-jre")
                .add();


        /*
        SSH TOOLS
         */

        // https://mvnrepository.com/artifact/com.jcraft/jsch
        group("com.jcraft")
                .name("jsch")
                .version("0.1.55")
                .add();


        /*
        DATABASE TOOLS
         */

        // https://mvnrepository.com/artifact/hikari-cp/hikari-cp
        group("hikari-cp")
                .name("hikari-cp")
                .version("2.13.0")
                .add();


        /*
        UTILITY LIBRARIES
         */

        // To be added


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
                .version("5.7.2")
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
                .version("3.10.0")
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
                .version("2.0-groovy-3.0")
                .add();
    }

    private DependencyBom() {
    }

    public static Stream<BomEntry> getDependencyBom() {
        return entries.stream();
    }
}
