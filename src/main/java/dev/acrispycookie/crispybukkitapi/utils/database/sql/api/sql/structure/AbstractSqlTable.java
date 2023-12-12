package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure;

/**
 * represents the abstract definition of a table
 */
public interface AbstractSqlTable {

    /**
     * Name of the database
     *
     * @return name
     */
    AbstractSqlDatabase getDatabase();

    /**
     * Name of the table
     *
     * @return name
     */
    String getName();
}
