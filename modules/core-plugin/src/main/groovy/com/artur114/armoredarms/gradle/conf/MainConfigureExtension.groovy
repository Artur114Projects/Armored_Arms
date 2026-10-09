package com.artur114.armoredarms.gradle.conf

import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class MainConfigureExtension extends AbstractConfig {
    private DependenciesConfigureExtension dependenciesConf
    private ResourcesConfigureExtension resourcesConf
    private JarBuildConfigureExtension jarBuildConf
    private boolean doLoadProjectData

    MainConfigureExtension() {
        this.dependenciesConf = this.createNewConfig(DependenciesConfigureExtension)
        this.resourcesConf = this.createNewConfig(ResourcesConfigureExtension)
        this.jarBuildConf = this.createNewConfig(JarBuildConfigureExtension)
        doLoadProjectData = true
    }

    boolean getDoLoadProjectData() {
        return doLoadProjectData
    }

    void setDoLoadProjectData(boolean doLoadProjectData) {
        this.doLoadProjectData = doLoadProjectData
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

    JarBuildConfigureExtension getJarBuildConf() {
        return this.jarBuildConf
    }

    void jarConf(Action<? extends JarBuildConfigureExtension> action) {
        action.execute(this.jarBuildConf)
    }

    void jarConf(Closure<? extends JarBuildConfigureExtension> c) {
        this.jarConf(ConfigureUtil.configureUsing(c))
    }
}
