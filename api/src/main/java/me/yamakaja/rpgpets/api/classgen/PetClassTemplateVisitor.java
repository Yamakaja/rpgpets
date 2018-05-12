package me.yamakaja.rpgpets.api.classgen;

import me.yamakaja.rpgpets.api.entity.PetType;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;

/**
 * Created by Yamakaja on 5/12/18.
 */
public class PetClassTemplateVisitor extends ClassVisitor {

    private String templateClassName;
    private String targetSuperClassName;
    private String targetClassName;
    private String templateSuperClassName;
    private PetType petType;

    public PetClassTemplateVisitor(int api, ClassVisitor cv, String targetClassName, PetType petType) {
        super(api, cv);

        this.targetSuperClassName = petType.getEntitySuperClass().getName().replace('.', '/');
        this.targetClassName = targetClassName;
        this.petType = petType;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.templateClassName = name;
        this.templateSuperClassName = superName;

        super.visit(version, access & ~Modifier.ABSTRACT, targetClassName, signature, targetSuperClassName, interfaces);
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        super.visitOuterClass(owner, name, desc);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new PetMethodTemplateVisitor(Opcodes.ASM5, this, new MethodHeader(access, name, desc, signature, exceptions));
    }

    public MethodVisitor getMethodWriter(MethodHeader header) {
        return super.visitMethod(header.getAccess(), header.getName(), header.getDesc(), header.getSignature(), header.getExceptions());
    }

    public String getTemplateClassName() {
        return this.templateClassName;
    }

    public String getTargetSuperClassName() {
        return this.targetSuperClassName;
    }

    public String getTargetClassName() {
        return this.targetClassName;
    }

    public String getTemplateSuperClassName() {
        return this.templateSuperClassName;
    }

    public PetType getPetType() {
        return petType;
    }

}
