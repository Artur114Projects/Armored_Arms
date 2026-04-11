package com.artur114.armoredarms.gradle

import com.artur114.armoredarms.gradle.util.IPrioritised
import com.artur114.armoredarms.gradle.util.IPriority
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.util.function.Function
import java.util.stream.Collectors

class CorePluginUtils {
    static Object findPropertyAndValidate(Project target, String name) {
        Object property = target.findProperty(name)

        if (property == null) {
            throw new GradleException("Can't find a required property: '" + name + "', please add this property!")
        }

        return property
    }

    static Object parseValue(Project project, String value) {
        String parsed = value.replaceAll(" ", "")
        if (parsed.startsWith("@") && parsed.endsWith("@")) {
            if (parsed.length() <= 2) {
                throw new GradleException("Invalid parsing string: " + value)
            }
            return findPropertyAndValidate(project, parsed.substring(1, parsed.length() - 1))
        } else {
            return value
        }
    }

    static <T extends IPrioritised> List<T> sortPrioritisedList(Collection<T> list) {
        list.findAll{ it.priority() != null }.sort { -it.priority().toInt() }
    }
}
