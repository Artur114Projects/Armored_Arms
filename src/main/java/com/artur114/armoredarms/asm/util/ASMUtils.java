package com.artur114.armoredarms.asm.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ASMUtils {
    public static ClassNode createClassNode(byte[] basicClass) {
        ClassReader classReader = new ClassReader(basicClass);
        ClassNode clazz = new ClassNode();
        classReader.accept(clazz, 0);
        return clazz;
    }

    public static byte[] toBytecode(ClassNode cn) {
        return toBytecode(cn, ClassWriter.COMPUTE_MAXS);
    }

    public static byte[] toBytecode(ClassNode cn, int flags) {
        ClassWriter writer = new ClassWriter(flags);
        cn.accept(writer);
        return writer.toByteArray();
    }

    public static MethodInsnNode invokeOutMethod(String name, String desc) {
        return new MethodInsnNode(Opcodes.INVOKESTATIC, ITypes.HOOKS_OUT_CLASS, name, desc, false);
    }

    public static MethodInsnNode invokeOutMethod(String name) {
        return invokeOutMethod(name, "()V");
    }

    public static MethodNode findMethod(ClassNode classNode, String name) {
        return findMethod(classNode.methods, name, null);
    }

    public static MethodNode findMethod(ClassNode classNode, String name, String desc) {
        return findMethod(classNode.methods, name, desc);
    }

    public static MethodNode findMethod(List<MethodNode> methods, String name) {
        return findMethod(methods, name, null);
    }

    public static MethodNode findMethod(List<MethodNode> methods, String name, String desc) {
        if (methods == null) return null;
        for (MethodNode node : methods) {
            if (node.name.equals(name) && (desc == null || node.desc.equals(desc)) && node.instructions != null) {
                return node;
            }
        }
        return null;
    }

    public static List<AbstractInsnNode> findPattern(MethodNode method, int... pattern) {
        return findPattern(method.instructions, pattern);
    }

    public static List<AbstractInsnNode> findPattern(InsnList list, int... pattern) {
        Iterator<AbstractInsnNode> iterator = list.iterator();
        List<AbstractInsnNode> ret = new ArrayList<>();

        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == -1) {
                continue;
            }
            boolean flag = true;
            AbstractInsnNode next = node;
            for (int opcode : pattern) {
                if (next != null && next.getOpcode() == -1) {
                    while (next != null && next.getOpcode() == -1) {
                        next = next.getNext();
                    }
                    if (next == null) {
                        flag = false;
                        break;
                    }
                }
                if (next == null || next.getOpcode() != opcode) {
                    flag = false;
                    break;
                }
                next = next.getNext();
            }

            if (flag) {
                ret.add(node);
            }
        }

        return ret;
    }
}
