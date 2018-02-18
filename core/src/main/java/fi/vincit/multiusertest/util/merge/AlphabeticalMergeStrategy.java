package fi.vincit.multiusertest.util.merge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AlphabeticalMergeStrategy implements MergeStrategy {

    @Override
    public String[] mergeDefinitions(String[] first, String[] second) {
        final Set<String> mergedDefinitions = new HashSet<>();

        addIfNotNull(first, mergedDefinitions);
        addIfNotNull(second, mergedDefinitions);

        final String[] finalDefinitions = mergedDefinitions.toArray(new String[0]);
        Arrays.sort(finalDefinitions);

        return finalDefinitions;
    }

    private void addIfNotNull(String[] first, Set<String> mergedDefinitions) {
        if (first != null) {
            mergedDefinitions.addAll(Arrays.asList(first));
        }
    }
}
