package me.yamakaja.rpgpets.api.classgen;

/**
 * Created by Yamakaja on 5/12/18.
 *
 * super.visitMethod(access, name, desc, signature, exceptions)
 */
public class MethodHeader {

    private int access;
    private String name;
    private String desc;
    private String signature;
    private String exceptions[];

    public MethodHeader(int access, String name, String desc, String signature, String[] exceptions) {
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }

    public int getAccess() {
        return access;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public String getSignature() {
        return signature;
    }

    public String[] getExceptions() {
        return exceptions;
    }

}
