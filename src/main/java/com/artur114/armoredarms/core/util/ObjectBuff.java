package com.artur114.armoredarms.core.util;

import java.util.Arrays;
import java.util.NoSuchElementException;

public class ObjectBuff {
    private String[] entryNames;
    private Object[] data;
    private int dataSize = 0;
    private int cursor = 0;

    public ObjectBuff() {
        this(0);
    }

    public ObjectBuff(int size) {
        this.entryNames = new String[size];
        this.data = new Object[size];
    }

    public boolean hasNext() {
        return this.cursor < this.dataSize;
    }

    public int readInt() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return (int) this.data[this.cursor++];
    }

    public boolean readBoolean() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return (boolean) this.data[this.cursor++];
    }

    public float readFloat() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return (float) this.data[this.cursor++];
    }

    public double readDouble() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return (double) this.data[this.cursor++];
    }

    public Object readObject() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return this.data[this.cursor++];
    }

    public <T> T readObject(Class<T> clazz) {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        return clazz.cast(this.data[this.cursor++]);
    }

    public String peekName() {
        if (this.cursor >= this.dataSize) {
            throw new NoSuchElementException();
        }

        if (this.cursor >= this.entryNames.length) {
            return "";
        }

        String name = this.entryNames[this.cursor];

        return name == null ? "" : name;
    }

    public void writeInt(int i) {
        this.write(i, null, this.cursor++);
    }

    public void writeBoolean(boolean b) {
        this.write(b, null, this.cursor++);
    }

    public void writeFloat(float f) {
        this.write(f, null, this.cursor++);
    }

    public void writeDouble(double d) {
        this.write(d, null, this.cursor++);
    }

    public void writeObject(Object obj) {
        this.write(obj, null, this.cursor++);
    }

    public void writeInt(String name, int i) {
        this.write(i, name, this.cursor++);
    }

    public void writeBoolean(String name, boolean b) {
        this.write(b, name, this.cursor++);
    }

    public void writeFloat(String name, float f) {
        this.write(f, name, this.cursor++);
    }

    public void writeDouble(String name, double d) {
        this.write(d, name, this.cursor++);
    }

    public void writeObject(String name, Object obj) {
        this.write(obj, name, this.cursor++);
    }

    public void jump(int n) {
        if (this.cursor + n > this.dataSize) {
            throw new NoSuchElementException();
        }

        this.cursor += n;
    }

    public ObjectBuff copyFrom(ObjectBuff buff) {
        this.entryNames = Arrays.copyOf(buff.entryNames, buff.entryNames.length);
        this.data = Arrays.copyOf(buff.data, buff.data.length);
        this.dataSize = buff.dataSize;
        this.cursor = buff.cursor;
        return this;
    }

    public void clear() {
        Arrays.fill(this.entryNames, null);
        Arrays.fill(this.data, null);
        this.dataSize = 0;
        this.cursor = 0;
    }

    public ObjectBuff reset() {
        this.cursor = 0; return this;
    }

    private void write(Object obj, String entryName, int index) {
        if (this.data.length <= index) {
            this.data = Arrays.copyOf(this.data, index + 1);
        }
        if (entryName != null && this.entryNames.length <= index) {
            this.entryNames = Arrays.copyOf(this.entryNames, index + 1);
        }

        this.data[index] = obj;
        this.dataSize = index + 1;

        if (index < this.entryNames.length) {
            this.entryNames[index] = entryName;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectBuff)) {
            return false;
        }
        return Arrays.equals(((ObjectBuff) obj).data, this.data);
    }
}
