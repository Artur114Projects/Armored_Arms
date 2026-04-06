package com.artur114.armoredarms.gradle

import com.artur114.armoredarms.gradle.conf.AbstractConfig
import com.artur114.armoredarms.gradle.util.IPrioritised
import org.gradle.api.Project

interface IProjectBuildModule extends IPrioritised {
    void configure(CoreBuildPlugin plugin, Project project)
}