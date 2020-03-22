package org.asamk.signal.util;

import org.whispersystems.signalservice.api.KeyBackupService;
import org.whispersystems.signalservice.api.kbs.HashedPin;
import org.whispersystems.signalservice.internal.registrationpin.PinHasher;

import at.gadermaier.argon2.Argon2;
import at.gadermaier.argon2.Argon2Factory;
import at.gadermaier.argon2.model.Argon2Type;

public final class PinHashing {

    private PinHashing() {
    }

    public static HashedPin hashPin(String pin, KeyBackupService.HashSession hashSession) {
        return PinHasher.hashPin(PinHasher.normalize(pin), password -> {
            Argon2 argon2 = Argon2Factory.create()
                    .setVersion(13)
                    .setType(Argon2Type.Argon2id)
                    .setMemoryInKiB(16 * 1024)
                    .setParallelism(1)
                    .setIterations(32)
                    .setOutputLength(64);

            argon2.hash(password, hashSession.hashSalt());

            return argon2.getOutput();
        });
    }
}
