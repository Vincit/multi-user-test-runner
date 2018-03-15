package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.test.MultiUserConfig;

import java.lang.reflect.Field;
import java.util.Optional;

public class ConfigurationUtil {

    public static MultiUserConfig getConfigComponent(Object testInstance) {
        Optional<MultiUserConfig> config = Optional.empty();
        try {
            Optional<Field> field = findFieldWithConfig(testInstance);

            if (field.isPresent()) {
                Field fieldInstance = field.get();
                fieldInstance.setAccessible(true);
                config = Optional.ofNullable((MultiUserConfig) fieldInstance.get(testInstance));
                fieldInstance.setAccessible(false);
            }

            if (config.isPresent()) {
                return config.get();
            } else {
                throw new IllegalStateException("MultiUserConfigClass not found on " + testInstance.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Optional<Field> findFieldWithConfig(Object testInstance) {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(MultiUserConfigClass.class)) {
                return Optional.of(field);
            }
        }

        for (Field field : testInstance.getClass().getFields()) {
            if (field.isAnnotationPresent(MultiUserConfigClass.class)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

}
