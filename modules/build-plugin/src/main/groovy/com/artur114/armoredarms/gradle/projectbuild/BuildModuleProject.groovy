package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project

class BuildModuleProject implements IProjectBuildModule {
    @Override
    void configure(CoreBuildPlugin plugin, Project project) {
        if (plugin.pluginConfig.doLoadProjectData) {
            project.group = CorePluginUtils.findPropertyAndValidate(project, "modGroup")
            project.version = CorePluginUtils.findPropertyAndValidate(project, "modVersion")
            project.archivesBaseName = CorePluginUtils.findPropertyAndValidate(project, "modFileName")
        }
    }

    @Override
    IPriority priority() {
        return Priority.HIGHEST
    }
}
