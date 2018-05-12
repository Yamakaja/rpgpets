package me.yamakaja.rpgpets.api.classgen;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by Yamakaja on 5/12/18.
 */
public class PetAnnotationTemplateVisitor extends AnnotationVisitor {

    static String PET_FILTER_CLASS_DESC = "L" + PetFilter.class.getName().replace(".", "/") + ";";
    private PetMethodTemplateVisitor methodVisitor;

    public PetAnnotationTemplateVisitor(int api, PetMethodTemplateVisitor petMethodTemplateVisitor) {
        super(api);

        this.methodVisitor = petMethodTemplateVisitor;
    }

    @Override
    public void visit(String name, Object value) {
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return null;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return new PetAnnotationTemplateVisitor(Opcodes.ASM5, this.methodVisitor);
    }

    @Override
    public void visitEnd() {
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        if (this.methodVisitor.getClassVisitor().getPetType().name().equals(value))
            methodVisitor.setSave(true);
    }

}
