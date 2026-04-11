package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.conf.JarBuildConfigureExtension
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.tasks.bundling.Jar

class BuildModuleJar implements IProjectBuildModule, IConfiguredBuildModule<JarBuildConfigureExtension> {
    private JarBuildConfigureExtension config

    @Override
    void configure(CoreBuildPlugin plugin, Project project) {}

    @Override
    void configureAfter(CoreBuildPlugin plugin, Project project) {
        project.tasks.withType(Jar).configureEach { Jar task ->

            if (this.config.manifestAttributes != null) {
                task.manifest { Manifest manifest ->
                    manifest.attributes(this.config.manifestAttributes)
                }

            } else {
                task.manifest { Manifest manifest ->
                    manifest.attributes "Implementation-Timestamp": new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
                    manifest.attributes "Implementation-Vendor": "${CorePluginUtils.parseValue(project, this.config.author)}"
                    manifest.attributes "Implementation-Version": "${project.version}"
                    manifest.attributes "Core-Version": "${CorePluginUtils.parseValue(project, this.config.coreVersion)}"
                }
            }

            if (this.config.atFile != null) {
                task.manifest {Manifest manifest -> manifest.attributes "FMLAT": "${this.config.atFile}"}
            }

            task.from {
                plugin.module(BuildModuleDependencies).include.collect {it.isDirectory() ? it : project.zipTree(it)}
            }
        }
    }

    @Override
    IPriority priority() {
        return Priority.NORMAL
    }

    @Override
    void applyConfig(JarBuildConfigureExtension config) {
        this.config = config
    }

    @Override
    Class<JarBuildConfigureExtension> configClass() {
        return JarBuildConfigureExtension
    }
}
