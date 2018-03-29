package me.yamakaja.rpgpets.api.hook;

/**
 * Created by Yamakaja on 28.06.17.
 */
public enum Hooks {

    WORLDGUARD,
    FEUDAL,
    TOWNY;

    private boolean enabled = false;

    public void enable() {
        this.enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
