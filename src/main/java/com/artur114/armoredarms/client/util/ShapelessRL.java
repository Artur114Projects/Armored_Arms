package com.artur114.armoredarms.client.util;



import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class ShapelessRL extends ResourceLocation {

    private ShapelessRL(String[] pDecomposedLocation) {
        this(pDecomposedLocation[0], pDecomposedLocation[1]);
    }

    public ShapelessRL(String namespace, String path) {
        super(namespace, path, null);
    }

    public ShapelessRL(String string) {
        this(decompose(string, ':'));
    }

    public ShapelessRL(ResourceLocation rl) {
        this(rl.getNamespace(), rl.getPath());
    }

    public boolean isShapeless() {
        return this.getNamespace().equals("*") || this.getPath().equals("*");
    }

    public boolean isAbsoluteShapeless() {
        return this.getNamespace().equals("*") && this.getPath().equals("*");
    }

    public boolean isEmpty() {
        return this.getNamespace().isEmpty() || this.getPath().isEmpty();
    }

    @Override
    public boolean equals(Object rl) {
        boolean flag = false;
        if (rl instanceof ShapelessRL) {
            if ((this.getPath().equals("*") && this.getNamespace().equals("*")) || (((ShapelessRL) rl).getPath().equals("*") && ((ShapelessRL) rl).getNamespace().equals("*"))) {
                flag = true;
            }
            if ((this.getNamespace().equals("*") || ((ShapelessRL) rl).getNamespace().equals("*"))) {
                flag |= this.getPath().equals(((ResourceLocation) rl).getPath());
            }
            if ((this.getPath().equals("*") || ((ShapelessRL) rl).getPath().equals("*"))) {
                flag |= this.getNamespace().equals(((ResourceLocation) rl).getNamespace());
            }
        }
        return flag || super.equals(rl);
    }

    public static ShapelessRL fromNamespaceAndPath(String namespace, String path) {
        return new ShapelessRL(namespace, path);
    }
}
