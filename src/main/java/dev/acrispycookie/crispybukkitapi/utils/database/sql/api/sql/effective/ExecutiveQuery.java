package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.effective;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * represents an object that constructs and executes
 * operations only towards the database,
 * without expecting a return value
 *
 * @param <T> The type of QueryBuilder used
 */
public interface ExecutiveQuery<T> {

    /**
     * constructs the query to a String
     *
     * @return the QueryBuilder
     */
    T build();

    PreparedStatement asPrepared() throws SQLException;

    /**
     * executes the string query through a Connector
     *
     * @return the QueryBuilder
     */
    T exec();

    /**
     * Clone a QueryBuilder returning a new one
     */
    T patternClone();

    /**
     * executes the string query through a Connector in asynchronous
     *
     * @return the QueryBuilder
     */
    CompletableFuture<T> execAsync();

    String getSql();
}
