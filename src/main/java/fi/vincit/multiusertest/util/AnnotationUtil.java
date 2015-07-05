package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.TestUsers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtil {

    public static Set<UserIdentifier> getUsers(TestUsers testUsers) {
        if (testUsers != null) {
            return getDefinitions(testUsers.users());
        } else {
            return Collections.emptySet();
        }
    }

    public static Set<UserIdentifier> getCreators(TestUsers testUsers) {
        if (testUsers != null) {
            return getDefinitions(testUsers.creators());
        } else {
            return Collections.emptySet();
        }
    }

    private static Set<UserIdentifier> getDefinitions(String[] definitions) {
        Set<UserIdentifier> userIdentifiers = new HashSet<>();
        for (String user : definitions) {
            userIdentifiers.add(UserIdentifier.parse(user));
        }
        return userIdentifiers;
    }

}
