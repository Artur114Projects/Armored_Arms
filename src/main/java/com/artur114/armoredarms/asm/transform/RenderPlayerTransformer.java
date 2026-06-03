package com.artur114.armoredarms.asm.transform;

import com.artur114.armoredarms.asm.ASMHooksOut;
import com.artur114.armoredarms.asm.util.ASMUtils;
import com.artur114.armoredarms.asm.util.ITargetTransformer;
import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

public class RenderPlayerTransformer implements ITargetTransformer {
    @Override
    public byte[] transform(Logger logger, String transformedName, byte[] basicClass) {
        try {
            ClassNode clazz = ASMUtils.createClassNode(basicClass);

            for (MethodNode method : clazz.methods) {
                if (method.name.equals("func_82441_a")) { // Hard code! eeeeeeeeeeeeeeeeeeeeeeeeeeeee
                    logger.info("    transforming method [RenderPlayer::renderFirstPersonArm]");
                    InsnList insn = method.instructions;
                    InsnList insertList = new InsnList();
                    insertList.add(new MethodInsnNode(INVOKESTATIC, HOOKS_OUT_CLASS, "renderFirstPersonArmHook", "()Z", false));
                    LabelNode labelNode = new LabelNode();
                    insertList.add(new JumpInsnNode(IFNE, labelNode));
                    insertList.add(new InsnNode(RETURN));
                    insertList.add(labelNode);
                    insn.insert(insertList);
                }
            }

            return ASMUtils.toBytecode(clazz);
        } catch (Exception e) {
            FMLLog.bigWarning("[ARMOREDARMS-ASM] There was an error during transformation, render source [asm] will not work!");
        }

        return basicClass;
    }

    @Override
    public boolean isTarget(String transformedName) {
        return "net.minecraft.client.renderer.entity.RenderPlayer".equals(transformedName);
    }
}
