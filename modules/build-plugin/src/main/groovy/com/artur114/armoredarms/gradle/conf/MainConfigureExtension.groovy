package com.artur114.armoredarms.gradle.conf

import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class MainConfigureExtension extends AbstractConfig {
    private DependenciesConfigureExtension dependenciesConf
    private ResourcesConfigureExtension resourcesConf
    boolean doLoadProjectData
    String repositoryUrl
    String atFile

    MainConfigureExtension() {
        this.dependenciesConf = this.createNewConfig(DependenciesConfigureExtension)
        this.resourcesConf = this.createNewConfig(ResourcesConfigureExtension)
        repositoryUrl = "https://maven.pkg.github.com/Artur114Projects/Armored_Arms"
        doLoadProjectData = true
        atFile = null
    }

    ResourcesConfigureExtension getResourcesConf() {
        return this.resourcesConf
    }

    void resourcesConf(Action<? extends ResourcesConfigureExtension> action) {
        action.execute(this.resourcesConf)
    }

    void resourcesConf(Closure<? extends ResourcesConfigureExtension> c) {
        this.resourcesConf(ConfigureUtil.configureUsing(c))
    }

    DependenciesConfigureExtension getDependenciesConf() {
        return this.dependenciesConf
    }

    void dependenciesConf(Action<? extends DependenciesConfigureExtension> action) {
        action.execute(this.dependenciesConf)
    }

    void dependenciesConf(Closure<? extends DependenciesConfigureExtension> c) {
        this.dependenciesConf(ConfigureUtil.configureUsing(c))
    }
}
