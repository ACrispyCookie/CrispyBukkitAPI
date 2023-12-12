package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.column;

public enum SqlType {

    VARCHAR("VARCHAR", "VARCHAR"), // size
    TEXT("TEXT", null),
    TINYINT("TINYINT ", "TINYINT"),
    BOOL("BOOL", "BOOLEAN"),
    SMALLINT("SMALLINT", "SMALLINT"),
    MEDIUMINT("MEDIUMINT", null),
    INT("INT", "INTEGER"),
    BIGINT("BIGINT", "BIGINT"),
    FLOAT("FLOAT", "REAL"), // size (n, m)
    DOUBLE("DOUBLE", "DOUBLE PRECISION"), // size (n, m)
    DECIMAL("DECIMAL", "DECFLOAT"), // size (n, m)
    DATE("DATE", "DATE"),
    DATETIME("DATETIME", null),
    TIMESTAMP("TIMESTAMP", "TIMESTAMP"),
    TIME("TIME", "TIME"),
    YEAR("YEAR", null);

    private final String maria;
    private final String h2;

    SqlType(String maria, String h2) {
        this.maria = maria;
        this.h2 = h2;
    }

    public String maria() {
        return maria;
    }

    public String h2() {
        return h2;
    }
}
