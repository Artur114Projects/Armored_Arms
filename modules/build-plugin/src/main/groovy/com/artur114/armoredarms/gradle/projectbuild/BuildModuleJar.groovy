package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project
import org.gradle.api.java.archives.Manifest
import org.gradle.api.tasks.bundling.Jar

class BuildModuleJar implements IProjectBuildModule {
    @Override
    void configure(CoreBuildPlugin plugin, Project project) {
        project.tasks.withType(Jar).configureEach { Jar task ->
            task.manifest { Manifest manifest ->
                manifest.attributes "Implementation-Timestamp" : new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
                manifest.attributes "Implementation-Vendor"    : "${CorePluginUtils.findPropertyAndValidate(project, "author")}"
                manifest.attributes "Implementation-Version"   : "${project.version}"
                manifest.attributes "Core-Version"             : "${CorePluginUtils.findPropertyAndValidate(project, "coreVersion")}"
            }

            if (plugin.pluginConfig.atFile != null) {
                task.manifest {Manifest manifest -> manifest.attributes "FMLAT": "${plugin.pluginConfig.atFile}"}
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
}
