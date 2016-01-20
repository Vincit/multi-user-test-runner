package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.test.MultiUserConfig;

public class MultiUserTestConfigContainer implements MultiUserTestConfigProvider {

    private MultiUserConfig config;

    public MultiUserTestConfigContainer(MultiUserConfig config) {
        this.config = config;
    }

    @Override
    public MultiUserConfig getConfig() {
        return config;
    }
}
