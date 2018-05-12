package me.yamakaja.rpgpets.api.classgen;

import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by Yamakaja on 5/12/18.
 */
public class PetMethodTemplateVisitor extends MethodVisitor {

    private PetClassTemplateVisitor classVisitor;
    private MethodHeader header;
    private boolean save = true;

    public PetMethodTemplateVisitor(int api, PetClassTemplateVisitor classVisitor, MethodHeader methodHeader) {
        super(api);

        this.classVisitor = classVisitor;
        this.header = methodHeader;
    }

    public void setSave(boolean bool) {
        this.save = bool;
    }

    public void saveMethod() {
        if (this.mv == null)
            super.mv = classVisitor.getMethodWriter(header);
    }

    @Override
    public void visitLabel(Label label) {
        if (save && this.mv == null)
            this.saveMethod();

        super.visitLabel(label);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if ("Lme/yamakaja/rpgpets/api/classgen/PetFilter;".equals(desc)) {
            save = false;
            return new PetAnnotationTemplateVisitor(Opcodes.ASM5, this);
        }

        return super.visitAnnotation(desc, visible);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        super.visitMethodInsn(opcode, transformOwner(owner), name, desc, itf);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        super.visitFieldInsn(opcode, transformOwner(owner), name, desc);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, transformOwner(desc), signature, start, end, index);
    }

    @Override
    public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    private String transformOwner(String owner) {
        if (owner.contains(this.classVisitor.getTemplateClassName()))
            return owner.replace(this.classVisitor.getTemplateClassName(), this.classVisitor.getTargetClassName());

        if (owner.contains(this.classVisitor.getTemplateSuperClassName()))
            return owner.replace(this.classVisitor.getTemplateSuperClassName(), this.classVisitor.getTargetSuperClassName());

        return owner;
    }

    public PetClassTemplateVisitor getClassVisitor() {
        return classVisitor;
    }

}
