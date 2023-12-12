package dev.acrispycookie.crispybukkitapi.utils.database.sql.impl.structure;

public class ForeignKeyDefinition {
    private final String name;
    private final String outerDb;
    private final String outerTable;
    private final String outerColumn;
    private final String constraint;

    public ForeignKeyDefinition(String name, String outerDb, String outerTable, String outerColumn, String constraint) {
        this.name = name;
        this.outerDb = outerDb;
        this.outerTable = outerTable;
        this.outerColumn = outerColumn;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public String getOuterDb() {
        return outerDb;
    }

    public String getOuterTable() {
        return outerTable;
    }

    public String getOuterColumn() {
        return outerColumn;
    }

    public String getConstraint() {
        return constraint;
    }
}
