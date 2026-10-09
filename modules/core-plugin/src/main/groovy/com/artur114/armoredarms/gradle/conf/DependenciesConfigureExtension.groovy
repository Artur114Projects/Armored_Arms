package com.artur114.armoredarms.gradle.conf

import com.artur114.armoredarms.gradle.CorePluginUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.util.ConfigureUtil
import org.gradle.util.GradleVersion

import javax.annotation.Nullable

class DependenciesConfigureExtension extends AbstractConfig {
    private CoreDependenceConf coreDependenceConf
    private GHPackagesConf ghPackagesConf
    private Set<Object> dependencies

    DependenciesConfigureExtension() {
        this.coreDependenceConf = new CoreDependenceConf()
        this.ghPackagesConf = new GHPackagesConf()
        this.dependencies = new HashSet<>()
    }

    Set<Object> getDependencies() {
        return this.dependencies
    }

    GHPackagesConf getGhPackagesConf() {
        return this.ghPackagesConf
    }

    CoreDependenceConf getCoreDependenceConf() {
        return this.coreDependenceConf
    }

    void ghPackages(Action<? extends GHPackagesConf> action) {
        action.execute(this.ghPackagesConf)
    }

    void ghPackages(Closure<? extends GHPackagesConf> c) {
        this.ghPackages(ConfigureUtil.configureUsing(c))
    }

    void coreConf(Action<? extends CoreDependenceConf> action) {
        action.execute(this.coreDependenceConf)
    }

    void coreConf(Closure<? extends CoreDependenceConf> c) {
        this.coreConf(ConfigureUtil.configureUsing(c))
    }

    void afterDependence(Object... dependence) {
        this.dependencies.addAll(Arrays.asList(dependence))
    }

    class CoreDependenceConf {
        String artifactId
        String version
        String group

        CoreDependenceConf() {
            this.group = "com.artur114.armoredarms"
            this.version = "@coreVersion@"
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

    class GHPackagesConf {
        private GenericPasswordCredentials credentials
        private Set<String> repositories

        GHPackagesConf() {
            this.repositories = new HashSet<>(Collections.singletonList("Artur114Projects:Armored_Arms"))
            this.credentials = new GenericPasswordCredentials()
        }

        PasswordCredentials getCredentials(Project project) {
            this.credentials.prepare(project)
            return this.credentials
        }

        List<String> getRepositories() {
            List<String> ret = new ArrayList<>(repositories.size())

            for (String repo : this.repositories) {
                try {
                    if (repo.contains(":")) {
                        String[] split = repo.split(":")

                        if (split.length == 2) {
                            ret.add("https://maven.pkg.github.com/" + split[0] + "/" + split[1])
                        }
                    } else {
                        ret.add(repo)
                    }
                } catch (Exception ignored) {}
            }

            return ret
        }

        void repositories(String... array) {
            this.repositories.addAll(Arrays.asList(array))
        }

        void credentials(Action<? super PasswordCredentials> action) {
            action.execute(this.credentials)
        }
    }

    private class GenericPasswordCredentials implements PasswordCredentials {
        private String username = null
        private String password = null
        private Project project

        private void prepare(Project project) {
            this.project = project
        }

        @Override
        String getUsername() {
            if (this.username != null && this.username != '@gpr.user@') {
                return this.username
            } else {
                if (GradleVersion.current() >= GradleVersion.version('7.0')) {
                    return this.project.providers.gradleProperty('gpr.user').getOrNull() ?: System.getenv('GITHUB_ACTOR')
                } else {
                    return this.project.findProperty('gpr.user') ?: System.getenv('GITHUB_ACTOR')
                }
            }
        }

        @Override
        void setUsername(@Nullable String userName) {
            this.username = userName
        }

        @Override
        String getPassword() {
            if (this.password != null && this.password != '@gpr.key@') {
                return this.password
            } else {
                if (GradleVersion.current() >= GradleVersion.version('7.0')) {
                    return this.project.providers.gradleProperty('gpr.key').getOrNull() ?: System.getenv('GITHUB_TOKEN')
                } else {
                    return this.project.findProperty('gpr.key') ?: System.getenv('GITHUB_TOKEN')
                }
            }
        }

        @Override
        void setPassword(@Nullable String password) {
            this.password = password
        }

        @Override
        String toString() {
            String password = this.getPassword()

            if (password != null) {
                password = "*" * password.length()
            }

            return "Credentials [username: ${this.getUsername()}, password: ${password}]"
        }
    }
}
