package com.artur114.armoredarms.gradle.conf

class AbstractConfig {
    private Map<Class<? extends AbstractConfig>, AbstractConfig> configsMap = new HashMap<>()

    protected  <T extends AbstractConfig> T createNewConfig(Class<T> clazz) {
        T config = clazz.newInstance()
        this.configsMap.put(clazz, config)
        return config
    }

    def <T extends AbstractConfig> T findConfig(Class<T> clazz) {
        if (this.getClass() == clazz) {
            return this as T
        }

        T config = this.configsMap.get(clazz) as T

        if (config == null) {
            for (AbstractConfig child : this.configsMap.values()) {
                config = child.findConfig(clazz) as T

                if (config != null) {
                    return config
                }
            }
        }

        return config
    }
}
