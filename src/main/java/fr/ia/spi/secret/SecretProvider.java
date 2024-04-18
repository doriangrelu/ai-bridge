package fr.ia.spi.secret;

import java.util.Optional;

public interface SecretProvider {

    Optional<String> revolve(String path, String name);
}
