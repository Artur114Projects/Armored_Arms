package com.artur114.armoredarms.core.util;

import com.artur114.armoredarms.core.api.IPrioritised;
import com.artur114.armoredarms.core.api.IPriority;
import com.artur114.armoredarms.core.api.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CoreUtilsTest {
    public static void main(String[] args) {
        List<IPrioritised> priorities = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i != 20; i++) {
            priorities.add(new IPrioritised() {
                Priority priority = Priority.values()[rand.nextInt(Priority.values().length)];
                @Override
                public IPriority priority() {
                    return priority;
                }

                @Override
                public String toString() {
                    return priority.name();
                }
            });
        }


        System.out.println(CoreUtils.sortPrioritisedList(priorities));
    }

}