package me.yamakaja.rpgpets.api.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/**
 * Created by Yamakaja on 14.06.17.
 */
public enum ConfigGeneral {

    HATCH_DISTANCE("distance_to_hatch"),
    GROWUP_START("growup_start"),
    GROWUP_END("growup_end"),
    FEED_COOLDOWN("feed_cooldown"),
    ENABLE_SENTRY("sentry"),
    ENABLE_METRICS("metrics"),
    ENABLE_UPDATE_CHECKER("update"),
    ENABLE_CRAFTING_HACK("enable_crafting_hack");

    private static YamlConfiguration configuration;
    private String path;

    ConfigGeneral(String path) {
        this.path = path;
    }

    public static void initialize(YamlConfiguration configuration) {
        ConfigGeneral.configuration = configuration;
    }

    public List<Byte> getByteList() {
        return configuration.getByteList(this.path);
    }

    public int getAsInt() {
        return configuration.getInt(this.path);
    }

    public List<Integer> getAsIntList() {
        return configuration.getIntegerList(this.path);
    }

    public long getAsLong() {
        return configuration.getLong(this.path);
    }

    public List<Long> getAsLongList() {
        return configuration.getLongList(this.path);
    }

    public List<Float> getAsFloatList() {
        return configuration.getFloatList(this.path);
    }

    public double getAsDouble() {
        return configuration.getDouble(this.path);
    }

    public List<Double> getAsDoubleList() {
        return configuration.getDoubleList(this.path);
    }

    public String getAsString() {
        return configuration.getString(this.path);
    }

    public List<String> getAsStringList() {
        return configuration.getStringList(this.path);
    }

    public Object getAsObject() {
        return configuration.get(this.path);
    }

    public List<?> getAsList() {
        return configuration.getList(this.path);
    }

    public boolean getAsBoolean() {
        return configuration.getBoolean(this.path);
    }

    public List<Boolean> getAsBooleanList() {
        return configuration.getBooleanList(this.path);
    }

    public boolean isPresent() {
        return configuration.contains(this.path);
    }

}
