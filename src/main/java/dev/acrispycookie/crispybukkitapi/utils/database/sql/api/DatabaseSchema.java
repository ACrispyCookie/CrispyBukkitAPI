package dev.acrispycookie.crispybukkitapi.utils.database.sql.api;

import com.google.gson.JsonObject;
import dev.acrispycookie.crispybukkitapi.utils.database.sql.api.sql.queries.builders.table.QueryBuilderCreateTable;

import java.util.ArrayList;

public class DatabaseSchema {

    private final ArrayList<QueryBuilderCreateTable> tables;
    private final JsonObject emptyJson;
    private final SchemaType type;


    public DatabaseSchema(ArrayList<QueryBuilderCreateTable> tables) {
        this.tables = tables;
        this.emptyJson = null;
        this.type = SchemaType.TABLE;
    }

    public DatabaseSchema(JsonObject emptyJson) {
        this.emptyJson = emptyJson;
        this.tables = null;
        this.type = SchemaType.JSON;
    }

    public ArrayList<QueryBuilderCreateTable> getTables() {
        return tables;
    }

    public JsonObject getEmptyJson() {
        return emptyJson;
    }

    public SchemaType getType() {
        return type;
    }

    public enum SchemaType {
        TABLE,
        JSON
    }
}
