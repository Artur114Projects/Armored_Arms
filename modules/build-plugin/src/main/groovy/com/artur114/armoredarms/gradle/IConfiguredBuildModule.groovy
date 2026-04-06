package com.artur114.armoredarms.gradle

import com.artur114.armoredarms.gradle.conf.AbstractConfig

interface IConfiguredBuildModule<C extends AbstractConfig> {
    void applyConfig(C config)
    Class<C> configClass()
}