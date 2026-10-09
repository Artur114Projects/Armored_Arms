package com.artur114.armoredarms.gradle.ext

import org.gradle.api.Action
import org.gradle.util.ConfigureUtil

class CoreMethodsExtension {
    Map<Class<?>, List<Action<?>>> listeners = new HashMap<>()

    def <T> void registerListener(Class<T> clazz, Action<T> action) {
        this.listeners.computeIfAbsent(clazz, {new ArrayList<>()}).add(action)
    }

    void massDependencies(Action<? extends MassDependenceConf> action) {
        def massDependenceConf = new MassDependenceConf()
        action.execute(massDependenceConf)

        this.onMethodInvoked(MassDependenceConf.class, massDependenceConf)
    }

    void massDependencies(Closure<? extends MassDependenceConf> c) {
        this.massDependencies(ConfigureUtil.configureUsing(c))
    }

    private <T> void onMethodInvoked(Class<T> clazz, T obj) {
        List<Action<?>> actions = this.listeners.get(clazz)

        if (actions instanceof List<Action<T>>) {
            for (Action<T> action : (actions as List<Action<T>>)) {
                action.execute(obj as T)
            }
        }
    }
}
