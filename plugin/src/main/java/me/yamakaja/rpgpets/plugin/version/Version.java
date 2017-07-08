package me.yamakaja.rpgpets.plugin.version;

/**
 * Created by Yamakaja on 08.07.17.
 */
public class Version {

    private String version;
    private long release;
    private String changes;
    private transient int encodedVersion;

    public String getVersion() {
        return version;
    }

    public long getRelease() {
        return release;
    }

    public String getChanges() {
        return changes;
    }

    public int getEncodedVersion() {
        return encodedVersion;
    }

    public void setEncodedVersion(int encodedVersion) {
        this.encodedVersion = encodedVersion;
    }

}
