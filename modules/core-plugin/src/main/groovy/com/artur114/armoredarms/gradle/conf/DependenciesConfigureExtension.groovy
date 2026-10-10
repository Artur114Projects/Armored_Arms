package com.artur114.armoredarms.gradle.conf

import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.ext.MassDependenceConf
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.util.ConfigureUtil
import org.gradle.util.GradleVersion

import javax.annotation.Nullable

class DependenciesConfigureExtension extends AbstractConfig {
    private CoreDependenceConf coreDependenceConf

    DependenciesConfigureExtension() {
        this.coreDependenceConf = new CoreDependenceConf()
    }

    CoreDependenceConf getCoreDependenceConf() {
        return this.coreDependenceConf
    }

    void coreConf(Action<? extends CoreDependenceConf> action) {
        action.execute(this.coreDependenceConf)
    }

    void coreConf(@DelegatesTo(value = CoreDependenceConf, strategy = Closure.DELEGATE_FIRST) Closure c) {
        c.delegate = this.coreDependenceConf
        c.resolveStrategy = Closure.DELEGATE_FIRST

        c.call()
    }

    class CoreDependenceConf {
        String artifactId
        String version
        String group

        CoreDependenceConf() {
            this.group = "com.github.Artur114Projects.Armored_Arms"
            this.version = "@core_version@"
            this.artifactId = "core"
        }

        String getArtifactId() {
            return this.artifactId
        }

        void setArtifactId(String artifactId) {
            this.artifactId = artifactId
        }

        String getVersion() {
            return this.version
        }

        void setVersion(String version) {
            this.version = version
        }

        String getGroup() {
            return this.group
        }

        void setGroup(String group) {
            this.group = group
        }

        String build(Project project) {
            def parsedGroup = CorePluginUtils.parseValue(project, this.group).toString()
            def parsedArtifactId = CorePluginUtils.parseValue(project, this.artifactId).toString()
            def parsedVersion = CorePluginUtils.parseValue(project, this.version).toString()
            if (parsedGroup.isEmpty() || parsedArtifactId.isEmpty() || parsedVersion.isEmpty()) {
                throw new IllegalStateException("Group, artifactId or version cannot be empty")
            }
            return "${parsedGroup}:${parsedArtifactId}:${parsedVersion}"
        }
    }
}
