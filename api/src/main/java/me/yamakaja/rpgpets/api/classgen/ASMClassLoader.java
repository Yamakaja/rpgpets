package me.yamakaja.rpgpets.api.classgen;

/**
 * Created by Yamakaja on 5/12/18.
 */
public class ASMClassLoader extends ClassLoader {

    public ASMClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public Class<?> loadCustomClass(String name, byte[] data) {
        return this.defineClass(name, data, 0, data.length);
    }

}
