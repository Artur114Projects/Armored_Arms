package com.artur114.armoredarms.core.util;

public interface IMultiTexture extends Iterable<ITexture> {
    ITextureIterator textureIterator();
    ITexture[] textures();
}
