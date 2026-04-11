package com.artur114.armoredarms.gradle.conf

import com.artur114.armoredarms.gradle.CoreBuildPlugin
import com.artur114.armoredarms.gradle.CorePluginUtils
import org.gradle.api.Project

class ResourcesConfigureExtension extends AbstractConfig {
    private Map<String, String> replaceProperties
    private Set<String> includeFiles

    ResourcesConfigureExtension() {
        this.includeFiles = new HashSet<>(Arrays.asList("mcmod.info"))
        this.replaceProperties = [
                "version": "@modVersion@",
                "description": "@coreVersion@"
        ]
    }

    Set<String> getIncludeFiles() {
        return this.includeFiles
    }

    Map<String, Object> getReplaceProperties(Project project) {
        Map<String, Object> ret = new HashMap<>()

        this.replaceProperties.forEach {String key, String value ->
            ret.put(key, CorePluginUtils.parseValue(project, value))
        }

        return ret
    }

    void includeFiles(String... files) {
        this.includeFiles = new HashSet<>(Arrays.asList(files))
    }

    void replaceProperties(Map<String, String> properties) {
        this.replaceProperties = new HashMap<>(properties)
    }

    void addIncludeFiles(String... files) {
        this.includeFiles.addAll(Arrays.asList(files))
    }

    void addReplaceProperties(Map<String, String> properties) {
        this.replaceProperties.putAll(properties)
    }
}
