package dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.data;


import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.effective.ExecutiveQuery;

public interface QueryBuilderReplace extends ExecutiveQuery<QueryBuilderReplace> {

    QueryBuilderReplace into(String tableName);

    QueryBuilderReplace columnSchema(String... columns);

    QueryBuilderReplace values(Object... values);

    QueryBuilderReplace valuesNQ(Object... values);

    QueryBuilderReplace clearValues();

}
