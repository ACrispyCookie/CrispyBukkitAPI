package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;
import dev.acrispycookie.crispybukkitapi.features.CrispyFeature;
import dev.acrispycookie.crispybukkitapi.features.options.PersistentOption;
import dev.acrispycookie.crispybukkitapi.utility.DataType;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

public class DataManager extends BaseManager {

    private SessionFactory factory;
    private String tablePrefix;

    public DataManager(CrispyBukkitAPI api) {
        super(api);
    }

    public void load() throws ManagerLoadException {
        if(!api.getManager(ConfigManager.class).hasDefault() || !api.getManager(ConfigManager.class).hasPath(
                api.getManager(ConfigManager.class).getDefault(), "database"))
            return;

        try {
            initHibernate();
        } catch (HibernateError e) {
            throw new ManagerLoadException(e);
        }
    }

    public void unload() {
        factory.close();
        tablePrefix = null;
    }

    @Override
    public void reload() throws ManagerReloadException {
        try {
            initHibernate();
        } catch (HibernateError e) {
            throw new ManagerReloadException(e, true, true);
        }
    }

    public Session newSession() {
        return factory.openSession();
    }

    private void initHibernate() throws HibernateError {
        Configuration configuration = new Configuration();
        configuration.setProperty(AvailableSettings.DIALECT, getHibernateDialect(getOptionValue(DatabaseOption.TYPE)));
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_URL, getOptionValue(DatabaseOption.HOST));
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_USER, getOptionValue(DatabaseOption.USERNAME));
        configuration.setProperty(AvailableSettings.JAKARTA_JDBC_PASSWORD, getOptionValue(DatabaseOption.PASSWORD));
        configuration.setProperty(AvailableSettings.JAKARTA_HBM2DDL_DB_NAME, getOptionValue(DatabaseOption.DATABASE));
        configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");

        FeatureManager featureManager = api.getManager(FeatureManager.class);
        for (CrispyFeature<?, ?, ?, ? extends PersistentOption> feature : featureManager.getEnabledFeatures()) {
            feature.getData().forEach((d) -> configuration.addAnnotatedClass(d.clazz()));
        }

        factory = configuration.buildSessionFactory();
        tablePrefix = getOptionValue(DatabaseOption.TABLE_PREFIX);
    }

    private String getOptionValue(DatabaseOption option) {
        return api.getManager(ConfigManager.class).getFromType(
                api.getManager(ConfigManager.class).getDefault(),
                "database." + option.getPath(), DataType.STRING,
                String.class
        );
    }

    private String getHibernateDialect(String option) {
        String packageName = "org.hibernate.dialect.";
        switch (option) {
            case "h2":
                packageName += "H2Dialect";
                break;
            case "mysql":
                packageName += "MySQL5Dialect";
                break;
            case "postgresql":
                packageName += "PostgreSQLDialect";
                break;
        }
        return packageName;
    }

    private enum DatabaseOption {
        TYPE("type"),
        HOST("host"),
        DATABASE("database"),
        USERNAME("username"),
        PASSWORD("password"),
        TABLE_PREFIX("table-prefix");

        private final String path;

        DatabaseOption(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
