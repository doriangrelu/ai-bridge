package fr.ia.services.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.ia.services.secret.VaultSecretProvider;
import fr.ia.spi.ai.AIProvider;
import fr.ia.spi.secret.SecretProvider;
import io.github.jopenlibs.vault.VaultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class DefaultAIProvider implements AIProvider {

    private final ChatLanguageModel model;

    public static DefaultAIProvider createInstance(final SecretProvider provider, final String path, final String name) {
        return new DefaultAIProvider(
                OpenAiChatModel.withApiKey(provider.revolve(path, name).orElseThrow())
        );
    }

    public static DefaultAIProvider createInstance(final String path, final String name) {
        try {
            return createInstance(
                    VaultSecretProvider.createInstance(),
                    path,
                    name
            );
        } catch (VaultException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T execute(Function<ChatLanguageModel, T> executor) {
        return executor.apply(this.model);
    }
}
