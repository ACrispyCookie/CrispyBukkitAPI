package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.database;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.effective.ReturningQuery;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.structure.AbstractSqlDatabase;

/**
 * Builder representing a 'CREATE DATABASE' Query
 */
public interface QueryBuilderCreateDatabase extends ReturningQuery<QueryBuilderCreateDatabase, AbstractSqlDatabase> {

    /**
     * if true it includes
     * OR REPLACE
     * block in the final query
     *
     * @param add if the block must be present
     * @return self Query Builder
     */
    QueryBuilderCreateDatabase orReplace(boolean add);

    /**
     * if true it includes
     * IF NOT EXISTS
     * block in the final query
     *
     * @param add if the block must be present
     * @return selfQuery Builder
     */
    QueryBuilderCreateDatabase ifNotExists(boolean add);

}
