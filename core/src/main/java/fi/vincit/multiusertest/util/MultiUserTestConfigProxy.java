package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.test.MultiUserConfig;

import java.lang.reflect.Field;

public class MultiUserTestConfigProxy implements MultiUserTestConfigProvider {

    private Field field;
    private Object instance;

    public MultiUserTestConfigProxy(Field field, Object instance) {
        this.field = field;
        this.instance = instance;
    }

    @Override
    public MultiUserConfig getConfig() {
        try {
            field.setAccessible(true);
            MultiUserConfig config = (MultiUserConfig) field.get(instance);
            field.setAccessible(false);
            return config;
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
