package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.conf.ResourcesConfigureExtension
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.language.jvm.tasks.ProcessResources

class BuildModuleResources implements IProjectBuildModule, IConfiguredBuildModule<ResourcesConfigureExtension> {
    private ResourcesConfigureExtension config

    @Override
    void configure(CoreBuildPlugin plugin, Project project) {}

    @Override
    void configureAfter(CoreBuildPlugin plugin, Project project) {
        project.tasks.withType(ProcessResources).configureEach {ProcessResources task ->
            def replaceProperties = this.config.getReplaceProperties(project)
            def includeFiles = this.config.includeFiles

            task.inputs.properties replaceProperties

            task.filesMatching(includeFiles) {
                it.expand(replaceProperties)
            }
        }
    }

    @Override
    void applyConfig(ResourcesConfigureExtension config) {
        this.config = config
    }

    @Override
    Class<ResourcesConfigureExtension> configClass() {
        return ResourcesConfigureExtension
    }

    @Override
    IPriority priority() {
        return Priority.NORMAL
    }
}
