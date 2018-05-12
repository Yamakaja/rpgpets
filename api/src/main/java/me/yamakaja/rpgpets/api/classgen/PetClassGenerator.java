package me.yamakaja.rpgpets.api.classgen;

import me.yamakaja.rpgpets.api.entity.PetType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.*;

/**
 * Created by Yamakaja on 5/12/18.
 */
public class PetClassGenerator {

    private byte[] templateClassBytes;
    private Class<?> templateClass;
    private ASMClassLoader classLoader = new ASMClassLoader(this.getClass().getClassLoader());

    public PetClassGenerator(Class<?> templateClass) {
        try {
            InputStream inputStream = templateClass.getResourceAsStream(templateClass.getSimpleName() + ".class");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            for (int read; (read = inputStream.read(buffer)) > 0; )
                byteArrayOutputStream.write(buffer, 0, read);

            this.templateClassBytes = byteArrayOutputStream.toByteArray();
            this.templateClass = templateClass;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read class file of " + templateClass.getSimpleName(), e);
        }
    }

    /**
     * Generate a class for the specified pet type
     *
     * @param type The pet type
     * @return The generated class
     */
    public Class<?> generatePetClass(PetType type) {
        ClassReader reader = new ClassReader(this.templateClassBytes);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        String name = templateClass.getName();
        int lastDot = name.lastIndexOf('.');
        name = name.substring(0, lastDot + 1) + "entity." + type.getEntityName();
        String className = name.replace('.', '/');
        ClassVisitor visitor = new PetClassTemplateVisitor(Opcodes.ASM5, writer, className, type);
        reader.accept(visitor, 0);

        return classLoader.loadCustomClass(name, writer.toByteArray());
    }

}
