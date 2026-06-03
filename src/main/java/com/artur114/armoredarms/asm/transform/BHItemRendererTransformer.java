package com.artur114.armoredarms.asm.transform;

import com.artur114.armoredarms.asm.util.ASMUtils;
import com.artur114.armoredarms.asm.util.ITargetTransformer;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class BHItemRendererTransformer implements ITargetTransformer {
    @Override
    public byte[] transform(Logger logger, String transformedName, byte[] basicClass) {
        ClassNode clazz = ASMUtils.createClassNode(basicClass);
        MethodNode method = ASMUtils.findMethod(clazz, "lambda$renderOffhandReturn$1");

        if (method != null) {
            logger.info("    transforming method [ItemRendererHooks::lambda$renderOffhandReturn$1]");
            method.instructions.insert(ASMUtils.invokeOutMethod("backHandPreLeft"));
            ASMUtils.findPattern(method, RETURN).forEach(v -> {
                method.instructions.insertBefore(v, ASMUtils.invokeOutMethod("backHandPostLeft"));
            });
        } else {
            logger.warn("Cant find, method [lambda$renderOffhandReturn$1]");
        }

        return ASMUtils.toBytecode(clazz);
    }

    @Override
    public boolean isTarget(String transformedName) {
        return "xonin.backhand.client.hooks.ItemRendererHooks".equals(transformedName);
    }
}
