package com.artur114.armoredarms.asm;

import com.artur114.armoredarms.asm.transform.*;
import com.artur114.armoredarms.asm.util.ITargetTransformer;
import com.artur114.armoredarms.asm.util.ITypes;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ASMTransformerAA implements IClassTransformer, ITypes {
    public static final Logger LOGGER = LogManager.getLogger("ARMOREDARMS-ASM");
    private final List<ITargetTransformer> transformers = new ArrayList<>(Arrays.asList(
            new RenderPlayerTransformer(),
            new BHItemRendererTransformer(),
            new RenderOffhandPlayerTransformer()
    ));

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return basicClass;
        }
        for (ITargetTransformer transformer : this.transformers) {
            if (transformer.isTarget(transformedName)) {
                try {
                    LOGGER.info("Transforming class: {}...", transformedName);
                    basicClass = transformer.transform(LOGGER, transformedName, basicClass);
                } catch (Exception e) {
                    LOGGER.warn("Transformer {} is down!", transformer.getClass());
                    LOGGER.warn("Trace:", e);
                    ASMHooksOut.onTransformerDown(transformer);
                }
            }
        }

        return basicClass;
    }
}
