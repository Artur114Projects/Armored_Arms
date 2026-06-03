package com.artur114.armoredarms.asm.transform;

import com.artur114.armoredarms.asm.util.ASMUtils;
import com.artur114.armoredarms.asm.util.ITargetTransformer;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class RenderOffhandPlayerTransformer implements ITargetTransformer {
    @Override
    public byte[] transform(Logger logger, String transformedName, byte[] basicClass) {
        ClassNode clazz = ASMUtils.createClassNode(basicClass);
        MethodNode method = ASMUtils.findMethod(clazz, "renderOffhandItem");

        if (method != null) {
            logger.info("    transforming method [RenderOffhandPlayer::renderOffhandItem]");
            method.instructions.insert(ASMUtils.invokeOutMethod("backHandPreLeft"));
            ASMUtils.findPattern(method, RETURN).forEach(v -> {
                method.instructions.insertBefore(v, ASMUtils.invokeOutMethod("backHandPostLeft"));
            });
        } else {
            logger.warn("Cant find, method [renderOffhandItem]");
        }

        return ASMUtils.toBytecode(clazz);
    }

    @Override
    public boolean isTarget(String transformedName) {
        return "xonin.backhand.client.renderer.RenderOffhandPlayer".equals(transformedName);
    }
}
