package fi.vincit.multiusertest.util;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class UserIdentifiers {

    private static final UserIdentifiers EMPTY = new UserIdentifiers();

    private final List<UserIdentifier> identifiers;

    public static UserIdentifiers empty() {
        return EMPTY;
    }

    public static UserIdentifiers ifAnyOf(String... identifiers) {
        return new UserIdentifiers(identifiers);
    }

    public static UserIdentifiers ifAnyOf(UserIdentifierCollection... identifiers) {
        List<String> allIdentifiers = Stream.of(identifiers)
                .map(UserIdentifierCollection::getUserIdentifiers)
                .flatMap(Collection::stream)
                .collect(toList());

        return new UserIdentifiers(allIdentifiers);
    }

    public static UserIdentifiers anyOf(String... identifiers) {
        return ifAnyOf(identifiers);
    }

    public static UserIdentifiers anyOf(UserIdentifierCollection... identifiers) {
        return ifAnyOf(identifiers);
    }

    public static UserIdentifierCollection users(String... usernames) {
        return new Users(usernames);
    }

    public static UserIdentifierCollection roles(String... roles) {
        return new Roles(roles);
    }

    public UserIdentifiers(String... identifiers) {
        this.identifiers = Stream.of(identifiers)
                .map(UserIdentifier::parse)
                .collect(toList());
    }

    public UserIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers.stream()
                .map(UserIdentifier::parse)
                .collect(toList());
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }

}
