package fi.vincit.multiusertest.util;

public class TestNameUtil {

    public static String getIdentifiers(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        return String.format("producer={%s}, consumer={%s}", producerIdentifier, consumerIdentifier);
    }

}
