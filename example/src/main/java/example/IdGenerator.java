package example;

import net.mguenther.idem.flake.Flake128S;
import net.mguenther.idem.provider.LinearTimeProvider;
import net.mguenther.idem.provider.StaticWorkerIdProvider;

import java.util.UUID;

class IdGenerator {
    private final Flake128S idgen;

    private IdGenerator() {
        idgen = new Flake128S(
                new LinearTimeProvider(),
                new StaticWorkerIdProvider(UUID.randomUUID().toString()));
    }

    private static class SingletonHolder {
        static final IdGenerator instance = new IdGenerator();
    }

    public static IdGenerator getInstance() {
        return SingletonHolder.instance;
    }

    public String generateCorrelationId() {
        return idgen.nextId();
    }
}
