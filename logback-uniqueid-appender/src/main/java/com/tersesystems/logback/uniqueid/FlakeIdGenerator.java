package com.tersesystems.logback.uniqueid;

import net.mguenther.idem.flake.Flake128S;
import net.mguenther.idem.flake.Flake64L;
import net.mguenther.idem.provider.LinearTimeProvider;
import net.mguenther.idem.provider.StaticWorkerIdProvider;

public class FlakeIdGenerator implements IdGenerator {

    private static final Flake128S flake64 = new Flake128S(
            new LinearTimeProvider(),
            new StaticWorkerIdProvider("logback"));

    @Override
    public String generateId() {
        return flake64.nextId();
    }
}
