package com.ocaml.sdk.utils;

public class SdkInfo {

    public final String path;
    public final String version;
    public final String toplevel;
    public final String sources;
    public final String comp;

    public SdkInfo(String path, String toplevel, String comp, String version) {
        this(path, toplevel, comp, version, null);
    }

    public SdkInfo(String path, String toplevel, String comp, String version, String sources) {
        this.path = path;
        this.version = version;
        this.toplevel = toplevel;
        this.comp = comp;
        this.sources = sources;
    }

    @Override public String toString() {
        return "SdkInfo{" +
                "path='" + path + '\'' +
                ", version='" + version + '\'' +
                ", toplevel='" + toplevel + '\'' +
                ", sources='" + sources + '\'' +
                ", comp='" + comp + '\'' +
                '}';
    }
}
