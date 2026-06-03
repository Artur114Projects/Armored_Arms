package com.artur114.armoredarms.asm.util;

import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public interface ITargetTransformer extends ITypes {
    byte[] transform(Logger logger, String transformedName, byte[] basicClass);
    boolean isTarget(String transformedName);
}
