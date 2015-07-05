package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.TestUsers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AnnotationUtil {

    public static Set<UserIdentifier> getUsers(TestUsers testUsers) {
        if (testUsers != null) {
            Set<UserIdentifier> userIdentifiers = new HashSet<>();
            for (String user : testUsers.users()) {
                userIdentifiers.add(UserIdentifier.parse(user));
            }
            return userIdentifiers;
        } else {
            return Collections.emptySet();
        }
    }

    public static Set<UserIdentifier> getCreators(TestUsers testUsers) {
        if (testUsers != null) {
            Set<UserIdentifier> userIdentifiers = new HashSet<>();
            for (String user : testUsers.creators()) {
                userIdentifiers.add(UserIdentifier.parse(user));
            }
            return userIdentifiers;
        } else {
            return Collections.emptySet();
        }
    }

}
