package fi.vincit.multiusertest.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class UserIdentifiers {

    private final List<UserIdentifier> identifiers;

    public static UserIdentifierCollection users(String... usernames) {
        return Users.create(usernames);
    }

    public static UserIdentifierCollection roles(String... roles) {
        return Roles.create(roles);
    }

    public static UserIdentifierCollection producer() {
        return new GenericUserIdentifierCollection(UserIdentifier.getProducer());
    }

    public static UserIdentifierCollection anonymous() {
        return new GenericUserIdentifierCollection(UserIdentifier.getAnonymous());
    }

    public static UserIdentifierCollection withProducerRole() {
        return new GenericUserIdentifierCollection(UserIdentifier.getWithProducerRole());
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

    public UserIdentifiers(UserIdentifier... identifiers) {
        this.identifiers = Arrays.asList(identifiers);
    }

    public List<UserIdentifier> getIdentifiers() {
        return identifiers;
    }
}
