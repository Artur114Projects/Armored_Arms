package com.artur114.armoredarms.gradle.conf

class JarBuildConfigureExtension extends AbstractConfig {
    private Map<String, ?> manifestAttributes
    private String coreVersion
    private String author
    private String atFile

    JarBuildConfigureExtension() {
        this.coreVersion = "@coreVersion@"
        this.author = "@author@"
        this.manifestAttributes = null
        this.atFile = null
    }

    String getAtFile() {
        return this.atFile
    }

    void setAtFile(String atFile) {
        this.atFile = atFile
    }

    String getAuthor() {
        return this.author
    }

    void setAuthor(String author) {
        this.author = author
    }

    String getCoreVersion() {
        return this.coreVersion
    }

    void setCoreVersion(String coreVersion) {
        this.coreVersion = coreVersion
    }

    Map<String, ?> getManifestAttributes() {
        return this.manifestAttributes
    }

    void overrideManifest(Map<String, ?> map) {
        this.manifestAttributes = map
    }
}
