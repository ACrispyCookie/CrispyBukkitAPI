package dev.acrispycookie.crispybukkitapi.managers;

import dev.acrispycookie.crispybukkitapi.CrispyBukkitAPI;

public abstract class BaseManager {

    protected final CrispyBukkitAPI api;

    public BaseManager(CrispyBukkitAPI api) {
        this.api = api;
    }

    public abstract void load() throws ManagerLoadException;

    public abstract void reload() throws ManagerReloadException;

    public static abstract class ManagerException extends Exception {
        private ManagerException(Exception e) {
            super(e);
        }

        private ManagerException(String s) {
            super(s);
        }
    }

    public static class ManagerLoadException extends ManagerException {
        public ManagerLoadException(Exception e) {
            super(e);
        }

        public ManagerLoadException(String s) {
            super(s);
        }
    }

    public static class ManagerReloadException extends ManagerException {
        private final boolean requiresRestart;
        private final boolean stopLoading;

        public ManagerReloadException(Exception e, boolean requiresRestart, boolean stopLoading) {
            super(e);
            this.requiresRestart = requiresRestart;
            this.stopLoading = stopLoading;
        }

        public ManagerReloadException(String s, boolean requiresRestart, boolean stopLoading) {
            super(s);
            this.requiresRestart = requiresRestart;
            this.stopLoading = stopLoading;
        }

        public boolean requiresRestart() {
            return requiresRestart;
        }

        public boolean stopLoading() {
            return stopLoading;
        }
    }
}
