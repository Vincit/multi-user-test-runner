package fi.vincit.multiusertest.rule;

import fi.vincit.multiusertest.annotation.RunWithUsers;

/**
 * Util class to make parameter name easy to read
 */
class Identifiers {

    private final String[] identifiers;

    public static Identifiers of(String... identifiers) {
        return new Identifiers(identifiers);
    }

    private Identifiers(String[] identifiers) {
        this.identifiers = identifiers;
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
            case RunWithUsers.PRODUCER: return "producer";
            case RunWithUsers.WITH_PRODUCER_ROLE: return "user with producer role";
            case RunWithUsers.ANONYMOUS: return "anonymous";
            default: return identifier;
        }
    }
}
