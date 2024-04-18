package fr.ia;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import fr.ia.services.ai.DefaultAIProvider;
import fr.ia.services.secret.VaultSecretProvider;
import fr.ia.spi.ai.AIProvider;
import fr.ia.spi.secret.SecretProvider;
import io.github.jopenlibs.vault.VaultException;

public class Main {
    public static void main(String[] args) {
        final AIProvider ai = DefaultAIProvider.createInstance("secret/gpt", "api-key");
        final String response = ai.execute(model -> model.generate("Coucou !!"));
        System.out.println(response);
    }
}