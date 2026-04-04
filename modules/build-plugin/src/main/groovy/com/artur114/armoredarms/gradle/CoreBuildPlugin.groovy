package com.artur114.armoredarms.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.file.CopySpec
import org.gradle.api.java.archives.Manifest
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.language.jvm.tasks.ProcessResources

class CoreBuildPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        if (!target.plugins.hasPlugin('java')) {
            target.apply plugin: 'java'
        }

        SourceSetContainer sourceSets = target.sourceSets
        SourceSet mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        Object modVersionProperty = findPropertyAndValidate(target, "modVersion")
        Object coreVersionProperty = findPropertyAndValidate(target, "coreVersion")

        def shade = target.configurations.create("shade")
        target.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME).extendsFrom(shade)

        BuildConfigureExtension config = target.extensions.create("coreBuildConf", BuildConfigureExtension)

        target.repositories.maven { MavenArtifactRepository repository ->
            repository.name = "GitHubPackages"
            repository.url = target.uri(config.repositoryUrl)
            repository.credentials {
                it.username = target.findProperty('gpr.user') ?: System.getenv('GITHUB_ACTOR')
                it.password = target.findProperty('gpr.key') ?: System.getenv('GITHUB_TOKEN')
            }
        }

        target.tasks.withType(ProcessResources).configureEach { ProcessResources task ->
            task.inputs.property("version", modVersionProperty)
            task.inputs.property("description", coreVersionProperty)

            task.from(mainSourceSet.resources.srcDirs) { CopySpec cp ->
                cp.include('mcmod.info')

                cp.filesMatching("mcmod.info") {
                    it.expand(version: modVersionProperty, description: coreVersionProperty)
                }
            }

            task.from(mainSourceSet.resources.srcDirs) { CopySpec cp ->
                cp.exclude('mcmod.info')
            }
        }

        target.tasks.withType(Jar).configureEach { Jar task ->
            task.manifest { Manifest manifest ->
                manifest.attributes "Implementation-Timestamp" : new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
                manifest.attributes "Implementation-Vendor"    : "${findPropertyAndValidate(target, "author")}"
                manifest.attributes "Implementation-Version"   : "${target.version}"
                manifest.attributes "Core-Version"             : "${coreVersionProperty}"
            }

            if (config.atFile != null) {
                task.manifest {Manifest manifest -> manifest.attributes "FMLAT": "${config.atFile}"}
            }

            task.from {
                shade.collect {it.isDirectory() ? it : target.zipTree(it)}
            }
        }

        target.dependencies.add('shade', "com.artur114.armoredarms:core:${coreVersionProperty}")

        if (config.doSetProjectData) {
            target.group = findPropertyAndValidate(target, "modGroup")
            target.version = findPropertyAndValidate(target, "modVersion")
            target.archivesBaseName = findPropertyAndValidate(target, "modFileName")
        }
    }

    static Object findPropertyAndValidate(Project target, String name) {
        Object property = target.findProperty(name)

        if (property == null) {
            throw new IllegalAccessException("Can't find a required property: " + name + ", please add this property!")
        }

        return property
    }
}
