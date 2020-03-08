package externalapi.appparam.models;

import java.util.Objects;

public class AppParam {
    private String appId;
    private String name;
    private ParamType type;
    private String label;
    private String description;

    public String getAppId() {
        return appId;
    }

    public AppParam setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getName() {
        return name;
    }

    public AppParam setName(String name) {
        this.name = name;
        return this;
    }

    public ParamType getType() {
        return type;
    }

    public AppParam setType(ParamType type) {
        this.type = type;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public AppParam setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AppParam setDescription(String description) {
        this.description = description;
        return this;
    }
    
    //////////////////////////////////////////

    @Override
    public String toString() {
        return "AppParam{" + "appId=" + appId + ", name=" + name + ", type=" + type + ", label=" + label + ", description=" + description + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppParam other = (AppParam) obj;
        if (!Objects.equals(this.appId, other.appId)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }
    
    
}
