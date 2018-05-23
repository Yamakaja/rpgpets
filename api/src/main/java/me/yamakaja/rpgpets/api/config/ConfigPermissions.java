package me.yamakaja.rpgpets.api.config;

/**
 * Created by Yamakaja on 10.06.17.
 */
public enum ConfigPermissions {

    COMMAND_HELP,
    COMMAND_GIVE,
    COMMAND_MINIFY,
    COMMAND_DEMINIFY;

    private String permission;

    public String get() {
        return permission;
    }

    public void set(String permission) {
        this.permission = permission;
    }
}
