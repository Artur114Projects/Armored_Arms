package com.artur114.armoredarms.gradle.conf

import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class MainConfigureExtension extends AbstractConfig {
    private DependenciesConfigureExtension dependenciesConf
    private ResourcesConfigureExtension resourcesConf
    private JarBuildConfigureExtension jarBuildConf

    MainConfigureExtension() {
        this.dependenciesConf = this.createNewConfig(DependenciesConfigureExtension)
        this.resourcesConf = this.createNewConfig(ResourcesConfigureExtension)
        this.jarBuildConf = this.createNewConfig(JarBuildConfigureExtension)
    }

    ResourcesConfigureExtension getResourcesConf() {
        return this.resourcesConf
    }

    void resourcesConf(Action<? extends ResourcesConfigureExtension> action) {
        action.execute(this.resourcesConf)
    }

    void resourcesConf(@DelegatesTo(value = ResourcesConfigureExtension, strategy = Closure.DELEGATE_FIRST) Closure c) {
        c.delegate = this.resourcesConf
        c.resolveStrategy = Closure.DELEGATE_FIRST

        c.call()
    }

    DependenciesConfigureExtension getDependenciesConf() {
        return this.dependenciesConf
    }

    void dependenciesConf(Action<? extends DependenciesConfigureExtension> action) {
        action.execute(this.dependenciesConf)
    }

    void dependenciesConf(@DelegatesTo(value = DependenciesConfigureExtension, strategy = Closure.DELEGATE_FIRST) Closure c) {
        c.delegate = this.dependenciesConf
        c.resolveStrategy = Closure.DELEGATE_FIRST

        c.call()
    }

    JarBuildConfigureExtension getJarBuildConf() {
        return this.jarBuildConf
    }

    void jarConf(Action<? extends JarBuildConfigureExtension> action) {
        action.execute(this.jarBuildConf)
    }

    void jarConf(@DelegatesTo(value = JarBuildConfigureExtension, strategy = Closure.DELEGATE_FIRST) Closure c) {
        c.delegate = this.jarBuildConf
        c.resolveStrategy = Closure.DELEGATE_FIRST

        c.call()
    }
}
