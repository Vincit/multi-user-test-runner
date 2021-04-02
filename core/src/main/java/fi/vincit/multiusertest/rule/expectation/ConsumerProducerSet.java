package fi.vincit.multiusertest.rule.expectation;

import fi.vincit.multiusertest.util.UserIdentifier;

import java.util.Objects;

public class ConsumerProducerSet {
    private final UserIdentifier producer;
    private final UserIdentifier consumer;

    public ConsumerProducerSet(UserIdentifier producer, UserIdentifier consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public ConsumerProducerSet(UserIdentifier consumer) {
        this.producer = null;
        this.consumer = consumer;
    }

    public UserIdentifier getProducer() {
        return producer;
    }

    public UserIdentifier getConsumer() {
        return consumer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConsumerProducerSet that = (ConsumerProducerSet) o;

        if (this.producer == null || that.producer == null) {
            return consumer.equals(that.consumer);
        } else {
            return Objects.equals(producer, that.producer) && consumer.equals(that.consumer);
        }
    }

    @Override
    public int hashCode() {
        // Don't compare producer since producer can in some cases be "any"
        return Objects.hashCode(consumer);
    }

    @Override
    public String toString() {
        final String producerString = producer != null ? producer.toString() : "<any>";
        return "producer=" + producerString + ", consumer=" + consumer;
    }
}
