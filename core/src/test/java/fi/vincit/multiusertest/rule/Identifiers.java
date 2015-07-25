package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.annotation.TestUsers;

/**
 * Util class to make parameter name easy to read
 */
class Identifiers {

    private String[] identifiers;

    public static Identifiers of(String... identifiers) {
        Identifiers self = new Identifiers();
        self.identifiers = identifiers;
        return self;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String identifier : this.identifiers) {
            sb.append(identifierToString(identifier)).append(", ");
        }
        final String s = sb.toString();
        return s.substring(0, s.length() - 2);
    }

    private String identifierToString(String identifier) {
        switch (identifier) {
            case TestUsers.CREATOR: return "creator";
            case TestUsers.NEW_USER: return "new user";
            default: return identifier;
        }
    }
}
