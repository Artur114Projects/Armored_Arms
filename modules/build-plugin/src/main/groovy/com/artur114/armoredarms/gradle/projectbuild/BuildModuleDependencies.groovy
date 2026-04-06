package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.conf.DependenciesConfigureExtension
import com.artur114.armoredarms.gradle.util.IPriority
import com.artur114.armoredarms.gradle.util.Priority
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.FlatDirectoryArtifactRepository
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.file.FileTree
import org.gradle.api.plugins.JavaPlugin

class BuildModuleDependencies implements IProjectBuildModule, IConfiguredBuildModule<DependenciesConfigureExtension> {
    private DependenciesConfigureExtension config
    Configuration include
    
    @Override
    void configure(CoreBuildPlugin plugin, Project project) {
        this.include = project.configurations.create("include")
        project.configurations.getByName(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME).extendsFrom(this.include)

        this.manageGHRepositories(project)
        this.loadCoreDependence(project)
        this.manageCurseMaven(project)
        this.manageMassDependencies(project)
    }

    private void manageGHRepositories(Project project) {
        project.repositories { RepositoryHandler rep ->
            List<String> repositories = this.config.getGHPackagesConf().getRepositories()
            PasswordCredentials credentials = this.config.getGHPackagesConf().getCredentials(project)

            for (String repo : repositories) {
                rep.maven { MavenArtifactRepository repository ->
                    repository.name = "GHP: " + repo
                    repository.url = project.uri(repo)
                    repository.credentials {credentials}
                }
            }
        }
    }

    private void loadCoreDependence(Project project) {
        project.dependencies.add('include', this.config.coreDependenceConf.build(project))
    }

    private void manageCurseMaven(Project project) {
        if (this.config.doLoadCurseMaven) {
            project.repositories { RepositoryHandler rep ->
                rep.maven { MavenArtifactRepository repository ->
                    repository.url = project.uri("https://cursemaven.com")
                }
            }
        }
    }

    private void manageMassDependencies(Project project) {
        switch (this.config.massDependenceConf.dependenceLoadType) {
            case DependenciesConfigureExtension.MassDependenceConf.EnumDependenceLoadType.SEPARATED:
                this.manageMassDependenciesSeparated(project)
            break
            case DependenciesConfigureExtension.MassDependenceConf.EnumDependenceLoadType.ALL_FLAT_DIR:
                this.manageMassDependenciesAllFlatDir(project)
            break
            case DependenciesConfigureExtension.MassDependenceConf.EnumDependenceLoadType.ALL_FILE_TREE:
                this.manageMassDependenciesAllFileTree(project)
            break
        }
    }

    private void manageMassDependenciesSeparated(Project project) {
        for (String source : this.config.massDependenceConf.sources) {
            project.dependencies.add("compileOnly", project.fileTree(dir: source, includes: ["*-deobf.jar"]))
        }

        List<String> nonEmptySources = new ArrayList<>(this.config.massDependenceConf.sources.size())
        List<FileTree> nonEmptyThrees = new ArrayList<>(this.config.massDependenceConf.sources.size())

        for (String source : this.config.massDependenceConf.sources) {
            FileTree tree = project.fileTree(dir: source, includes: ["*.jar"], excludes: ["*-deobf.jar"])

            if (!tree.isEmpty()) {
                nonEmptyThrees.add(tree)
                nonEmptySources.add(source)
            }
        }

        if (!nonEmptySources.isEmpty()) {
            project.repositories { RepositoryHandler rep ->
                rep.flatDir { FlatDirectoryArtifactRepository flat ->
                    flat.dirs nonEmptySources
                }
            }

            for (FileTree tree : nonEmptyThrees) {
                tree.each {
                    String lib = ":" + it.name.replaceAll(".jar", "") + ":0"

                    project.dependencies.add("compileOnly", this.config.massDependenceConf.deObfHook(lib))
                }
            }
        }
    }

    private void manageMassDependenciesAllFlatDir(Project project) {
        project.repositories { RepositoryHandler rep ->
            rep.flatDir { FlatDirectoryArtifactRepository flat ->
                flat.dirs this.config.massDependenceConf.sources
            }
        }

        for (String source : this.config.massDependenceConf.sources) {
            FileTree tree = project.fileTree(dir: source, includes: ["*.jar"])

            tree.each {
                String lib = ":" + it.name.replaceAll(".jar", "") + ":0"

                if (!lib.contains("-deobf")) {
                    project.dependencies.add("compileOnly", this.config.massDependenceConf.deObfHook(lib))
                } else {
                    project.dependencies.add("compileOnly", lib)
                }
            }
        }
    }

    private void manageMassDependenciesAllFileTree(Project project) {
        for (String source : this.config.massDependenceConf.sources) {
            project.dependencies.add("compileOnly", project.fileTree(dir: source, includes: ["*.jar"]))
        }
    }

    @Override
    void applyConfig(DependenciesConfigureExtension config) {
        this.config = config;
    }

    @Override
    Class<DependenciesConfigureExtension> configClass() {
        return DependenciesConfigureExtension
    }

    @Override
    IPriority priority() {
        return Priority.HIGH
    }
}
