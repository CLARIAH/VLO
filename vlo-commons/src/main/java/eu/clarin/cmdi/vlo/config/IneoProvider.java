package eu.clarin.cmdi.vlo.config;

public class IneoProvider {
    public String name;
    public String profile;
    public String level;
    public String defaultVal;

    public IneoProvider(String name, String profile, String level, String defaultVal) {
        if (profile == null && level == null && defaultVal == null) {
            throw new IllegalArgumentException("At least one of profile, level, or defaultVal must be not null");
        }
        this.name = name;
        this.profile = profile;
        this.level = level;
        this.defaultVal = defaultVal;
    }

    @Override
    public String toString() {
        return "Provider{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", level='" + level + '\'' +
                ", defaultVal='" + defaultVal + '\'' +
                '}';
    }
}
