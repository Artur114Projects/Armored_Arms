package com.artur114.armoredarms.gradle.conf

import com.artur114.armoredarms.gradle.CorePluginUtils
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.internal.artifacts.repositories.DefaultPasswordCredentials
import org.gradle.util.ConfigureUtil

import javax.annotation.Nullable
import java.util.function.Function

class DependenciesConfigureExtension extends AbstractConfig {
    private MassDependenceConf massDependenceConf
    private CoreDependenceConf coreDependenceConf
    private GHPackagesConf ghPackagesConf
    boolean doLoadCurseMaven

    DependenciesConfigureExtension() {
        this.coreDependenceConf = new CoreDependenceConf()
        this.ghPackagesConf = new GHPackagesConf()
        this.massDependenceConf = null
        this.doLoadCurseMaven = false;
    }

    GHPackagesConf getGHPackagesConf() {
        return this.ghPackagesConf
    }

    MassDependenceConf getMassDependenceConf() {
        return this.massDependenceConf
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

    void massDependencies(Action<? extends MassDependenceConf> action) {
        if (this.massDependenceConf == null) {
            this.massDependenceConf = new MassDependenceConf()
        }
        action.execute(this.massDependenceConf)
    }

    void massDependencies(Closure<? extends MassDependenceConf> c) {
        this.massDependencies(ConfigureUtil.configureUsing(c))
    }

    void core(Action<? extends CoreDependenceConf> action) {
        action.execute(this.coreDependenceConf)
    }

    void core(Closure<? extends CoreDependenceConf> c) {
        this.core(ConfigureUtil.configureUsing(c))
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

        String build(Project project) {
            def parsedGroup = CorePluginUtils.parseValue(project, this.group)?.toString()
            def parsedArtifactId = CorePluginUtils.parseValue(project, this.artifactId)?.toString()
            def parsedVersion = CorePluginUtils.parseValue(project, this.version)?.toString()
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

    class MassDependenceConf {
//        public static final List<String> DEPENDENCE_LOAD_TYPES  = ["SEPARATED", "ALL_FLAT_DIR", "ALL_FILE_TREE"]
        EnumDependenceLoadType dependenceLoadType
        private Closure<Object> deObfHook
        private Set<String> sources

        MassDependenceConf() {
            this.dependenceLoadType = EnumDependenceLoadType.SEPARATED
            this.sources = new HashSet<>()
            this.deObfHook = {it}
        }

        Closure<Object> getDeObfHook() { this.deObfHook }

        EnumDependenceLoadType getDependenceLoadType() { this.dependenceLoadType }

        Set<String> getSources() { Collections.unmodifiableSet(this.sources) }

        void setDependenceLoadType(EnumDependenceLoadType type) {
            this.dependenceLoadType = type
        }

        void deObfHook(Closure<?> closure) {
            this.deObfHook = closure as Closure<Object>
        }

        void source(String... source) {
            this.sources.addAll(Arrays.asList(source))
        }

        enum EnumDependenceLoadType {
            SEPARATED, ALL_FLAT_DIR, ALL_FILE_TREE
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
            if (this.username != null) {
                return this.username
            } else {
                return this.project.findProperty('gpr.user') ?: System.getenv('GITHUB_ACTOR')
            }
        }

        @Override
        void setUsername(@Nullable String userName) {
            this.username = userName
        }

        @Override
        String getPassword() {
            if (this.password != null) {
                return this.password
            } else {
                return this.project.findProperty('gpr.key') ?: System.getenv('GITHUB_TOKEN')
            }
        }

        @Override
        void setPassword(@Nullable String password) {
            this.password = password
        }
    }
}
