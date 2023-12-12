package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table;

import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.effective.ExecutiveQuery;

/**
 * Builder representing a 'DROP TABLE' Query
 */
public interface QueryBuilderDropTable extends ExecutiveQuery<QueryBuilderDropTable> {

    /**
     * if true it includes
     * IF EXISTS
     * block in the final query
     *
     * @param add if the block must be present
     * @return self Query Builder
     */
    QueryBuilderDropTable ifExists(boolean add);

}
