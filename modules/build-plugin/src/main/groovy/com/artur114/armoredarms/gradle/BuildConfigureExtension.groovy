package com.artur114.armoredarms.gradle

class BuildConfigureExtension {
    boolean doSetProjectData
    String repositoryUrl
    String atFile

    BuildConfigureExtension() {
        repositoryUrl = "https://maven.pkg.github.com/Artur114Projects/Armored_Arms"
        doSetProjectData = true
        atFile = null
    }
}
