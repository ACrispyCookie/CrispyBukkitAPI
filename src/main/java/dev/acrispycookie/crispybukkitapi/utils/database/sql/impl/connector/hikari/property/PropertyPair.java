package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.connector.hikari.property;

public class PropertyPair {
    private final HikariProperty property;
    private final String value;

    public PropertyPair(HikariProperty property, String value) {
        this.property = property;
        this.value = value;
    }

    public HikariProperty getProperty() {
        return property;
    }

    public String getValue() {
        return value;
    }
}
