package com.artur114.armoredarms.gradle.projectbuild

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import com.artur114.armoredarms.gradle.IConfiguredBuildModule
import com.artur114.armoredarms.gradle.IProjectBuildModule
import com.artur114.armoredarms.gradle.conf.DependenciesConfigureExtension
import com.artur114.armoredarms.gradle.ext.MassDependenceConf
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

        plugin.coreMethods.registerListener(MassDependenceConf) {
            this.manageMassDependencies(it, project)
        }

        this.manageCurseMaven(project)
    }

    @Override
    void configureAfter(CoreBuildPlugin plugin, Project project) {
        this.manageGHRepositories(project)
        this.loadCoreDependence(project)

        this.manageDependencies(project)
    }

    private void manageGHRepositories(Project project) {
        project.repositories { RepositoryHandler rep ->
            List<String> repositories = this.config.ghPackagesConf.getRepositories()
            PasswordCredentials credentials = this.config.ghPackagesConf.getCredentials(project)
            println "GitHub credentials: [${credentials}]"

            for (String repo : repositories) {
                rep.maven { MavenArtifactRepository repository ->
                    repository.name = "GHP: " + repo
                    repository.url = project.uri(repo)
                    println "Added GitHub repo: [${repository.url}]"
                    repository.credentials {
                        it.username = credentials.username
                        it.password = credentials.password
                    }
                }
            }
        }
    }

    private void loadCoreDependence(Project project) {
        String dep = this.config.coreDependenceConf.build(project)
        project.dependencies.add('include', dep)

        println("Loaded core dependency: [${dep}]")
    }

    private void manageCurseMaven(Project project) {
        project.repositories { RepositoryHandler rep ->
            rep.maven { MavenArtifactRepository repository ->
                repository.url = project.uri("https://cursemaven.com")
            }
        }
    }

    private void manageDependencies(Project project) {
        for (Object dep : this.config.dependencies) {
            project.dependencies.add("implementation", dep)

            println("Loaded late dependency: [${dep}]")
        }
    }

    private void manageMassDependencies(MassDependenceConf config, Project project) {
        if (config == null) {
            return
        }
        if (config.sources.isEmpty()) {
            return
        }

        switch (config.dependenceLoadType) {
            case "SEPARATED":
                this.manageMassDependenciesSeparated(config, project)
            break
            case "ALL_FLAT_DIR":
                this.manageMassDependenciesAllFlatDir(config, project)
            break
            case "ALL_FILE_TREE":
                this.manageMassDependenciesAllFileTree(config, project)
            break
        }
    }

    private void manageMassDependenciesSeparated(MassDependenceConf config, Project project) {
        for (String source : config.sources) {
            FileTree tree = project.fileTree(dir: source, includes: ["*-deobf.jar"])

            project.dependencies.add("implementation", tree)

            tree.each {
                println("Loaded file dependency: [${it}]")
            }
        }

        List<String> nonEmptySources = new ArrayList<>(config.sources.size())
        List<FileTree> nonEmptyThrees = new ArrayList<>(config.sources.size())

        for (String source : config.sources) {
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
                    String lib = "blank:" + it.name.replaceAll(".jar", "") + ":0"

                    project.dependencies.add("implementation", config.deObfHook(lib))
                    println("Loaded flat dir dependency: [${lib}]")
                }
            }
        }
    }

    private void manageMassDependenciesAllFlatDir(MassDependenceConf config, Project project) {
        project.repositories { RepositoryHandler rep ->
            rep.flatDir { FlatDirectoryArtifactRepository flat ->
                flat.dirs config.sources
            }
        }

        for (String source : config.sources) {
            FileTree tree = project.fileTree(dir: source, includes: ["*.jar"])

            tree.each {
                String lib = "blank:" + it.name.replaceAll(".jar", "") + ":0"

                if (!lib.contains("-deobf")) {
                    project.dependencies.add("implementation", config.deObfHook(lib))
                    println("Loaded flat dir dependency: [${lib}]")
                } else {
                    project.dependencies.add("implementation", lib)
                    println("Loaded flat dir dependency: [${lib}]")
                }
            }
        }
    }

    private void manageMassDependenciesAllFileTree(MassDependenceConf config, Project project) {
        for (String source : config.sources) {
            FileTree tree = project.fileTree(dir: source, includes: ["*.jar"])

            project.dependencies.add("implementation", tree)

            tree.each {
                println("Loaded file dependency: [${it}]")
            }
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
