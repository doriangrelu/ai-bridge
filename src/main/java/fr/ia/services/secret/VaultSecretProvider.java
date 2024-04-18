package fr.ia.services.secret;

import fr.ia.spi.secret.SecretProvider;
import io.github.jopenlibs.vault.Vault;
import io.github.jopenlibs.vault.VaultConfig;
import io.github.jopenlibs.vault.VaultException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class VaultSecretProvider implements SecretProvider {

    private final Vault vault;

    private VaultSecretProvider(Vault vault) {
        this.vault = vault;
    }

    public static VaultSecretProvider createInstance() throws VaultException {
        log.warn("-- Use default security configuration for Vault provider");
        return createInstance(new VaultConfig()
                .address("http://127.0.0.1:8200")
                .token("testtoken")
                .build());
    }

    public static VaultSecretProvider createInstance(final VaultConfig configuration) throws VaultException {
        return new VaultSecretProvider(
                Vault.create(configuration)
        );
    }

    @Override
    public Optional<String> revolve(final String path, final String name) {
        try {
            log.info("-- Try to resolve VAULT Path {}::{}", path, name);
            return Optional.ofNullable(this.vault.logical()
                    .read(path)
                    .getData()
                    .get(name)
            );
        } catch (VaultException e) {
            throw new IllegalStateException(e);
        }
    }
}
