package externalapi.appinfo.models;

public enum InputFieldType {
    TEXT("ckan.input.text"),
    TEXT_LIST("ckan.input.testlist"),
    
    NUMBER("ckan.input.number"),
    NUMBER_LIST("ckan.input.numberlist"),
    
    BOOLEAN("ckan.input.boolean"),
    
    FILE("ckan.input.file");
    
    
    public final String prefix;

    private InputFieldType(String prefix) {
        this.prefix = prefix;
    }
    
    public String toEnvKey(String paramName) {
        return prefix + "." + paramName;
    }
}