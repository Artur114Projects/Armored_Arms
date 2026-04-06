package com.artur114.armoredarms.gradle

import com.artur114.armoredarms.gradle.projectbuild.BuildModuleDependencies
import com.artur114.armoredarms.gradle.projectbuild.BuildModuleJar
import com.artur114.armoredarms.gradle.projectbuild.BuildModuleProject
import com.artur114.armoredarms.gradle.projectbuild.BuildModuleResources
import com.artur114.armoredarms.gradle.conf.MainConfigureExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.java.archives.Manifest
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar

class CoreBuildPlugin implements Plugin<Project> {
    private Map<Class<? extends IProjectBuildModule>, IProjectBuildModule> modulesMap
    private List<IProjectBuildModule> modules

    public MainConfigureExtension pluginConfig
    public SourceSet mainSourceSet

    CoreBuildPlugin() {
        this.constructModules(BuildModuleProject, BuildModuleResources, BuildModuleDependencies, BuildModuleJar)
    }

    @Override
    void apply(Project target) {
        if (target.plugins.hasPlugin('core-build')) return

        this.prepare(target)

        this.configureModules(target)
    }

    def <T extends IProjectBuildModule> T module(Class<T> clazz) {
        return this.modulesMap.get(clazz) as T
    }

    private void constructModules(Class<? extends IProjectBuildModule>... classes) {
        this.modulesMap = new HashMap<>()
        for (Class<? extends IProjectBuildModule> clazz : classes) {
            try {
                IProjectBuildModule module = clazz.newInstance()
                this.modulesMap.put(clazz, module)
            } catch (Exception e) {
                println("Module could not be construct: " + clazz)
                println(e)
            }
        }

        this.modules = CorePluginUtils.sortPrioritisedList(this.modulesMap.values())
    }

    private void prepare(Project project) {
        if (!project.plugins.hasPlugin('java')) {
            project.apply plugin: 'java'
        }

        this.pluginConfig = project.extensions.create("coreBuildConf", MainConfigureExtension)
        this.mainSourceSet = project.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

        for (IProjectBuildModule module : this.modules) {
            if (module instanceof IConfiguredBuildModule) {
                module.applyConfig(this.pluginConfig.findConfig(module.configClass()))
            }
        }
    }

    private void configureModules(Project project) {
        for (IProjectBuildModule module : this.modules) {
            try {
                module.configure(this, project)
            } catch (Exception e) {
                println("Module could not be configure: " + module)
                println(e)
            }
        }
    }
}
