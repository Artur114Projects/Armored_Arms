package com.artur114.armoredarms.core.util;

import java.util.*;

public class MultiTexture implements IMultiTexture {
    private final List<ITexture> textures;
    private TextureIterator iterator;

    public MultiTexture(Collection<ITexture> textures) {
        this.textures = CoreUtils.sortPrioritisedList(textures);
    }

    public MultiTexture(ITexture... textures) {
        this(Arrays.asList(textures));
    }

    @Override
    public ITextureIterator textureIterator() {
        if (this.iterator == null) {
            this.iterator = new TextureIterator(this.textures);
        }
        return this.iterator.reload();
    }

    @Override
    public ITexture[] textures() {
        return this.textures.toArray(new ITexture[0]);
    }

    @Override
    public Iterator<ITexture> iterator() {
        return this.textures.iterator();
    }

    private static class TextureIterator implements ITextureIterator {
        private final ITexture[] textures;
        private int cursor = 0;

        private TextureIterator(List<ITexture> textures) {
            this.textures = textures.toArray(new ITexture[0]);
        }

        @Override
        public void bindNext() {
            ITexture texture = this.next();

            if (texture != null) {
                texture.bind();
            }
        }

        @Override
        public void postBind() {
            ITexture texture = this.prev();

            if (texture != null) {
                texture.postBind();
            }
        }

        @Override
        public boolean hasNext() {
            return this.cursor < this.textures.length;
        }

        private ITexture prev() {
            if (this.cursor == 0) {
                return null;
            }
            return this.textures[this.cursor - 1];
        }

        private ITexture next() {
            if (this.cursor >= this.textures.length) {
                return null;
            }
            return this.textures[this.cursor++];
        }

        private TextureIterator reload() {
            this.cursor = 0;
            return this;
        }
    }
}
