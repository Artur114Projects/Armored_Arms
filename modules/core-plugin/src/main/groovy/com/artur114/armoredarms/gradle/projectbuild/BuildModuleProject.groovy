package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class BuildModuleProject implements IProjectBuildModule {
    @Override
    void configure(CoreBuildPlugin plugin, Project project) {}

    @Override
    void configureAfter(CoreBuildPlugin plugin, Project project) {
        project.tasks.withType(JavaCompile).configureEach {
            it.options.encoding = 'UTF-8'
        }
    }

    @Override
    IPriority priority() {
        return Priority.HIGHEST
    }
}
