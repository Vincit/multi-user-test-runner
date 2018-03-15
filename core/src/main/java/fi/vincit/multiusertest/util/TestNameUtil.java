package fi.vincit.multiusertest.util;

public class TestNameUtil {

    public static String resolveTestName(UserIdentifier producerIdentifier, UserIdentifier consumerIdentifier) {
        return String.format("producer={%s}, consumer={%s}", producerIdentifier, consumerIdentifier);
    }

}
