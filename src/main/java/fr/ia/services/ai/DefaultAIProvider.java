package fr.ia.services.ai;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import fr.ia.services.ai.store.InMemoryStore;
import fr.ia.services.secret.VaultSecretProvider;
import fr.ia.spi.ai.AIProvider;
import fr.ia.spi.ai.store.StoreProvider;
import fr.ia.spi.secret.SecretProvider;
import io.github.jopenlibs.vault.VaultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class DefaultAIProvider<N> implements AIProvider<N> {

    private final ChatLanguageModel model;
    private final AtomicReference<StoreProvider<N>> provider;
    private final AtomicReference<ConversationalRetrievalChain> chain;

    public static DefaultAIProvider<String> createInstance(final SecretProvider provider, final String path, final String name) {
        final String apiKey = provider.revolve(path, name).orElseThrow();
        return new DefaultAIProvider<>(
                OpenAiChatModel.withApiKey(apiKey),
                new AtomicReference<>(InMemoryStore.createInstance()),
                new AtomicReference<>()
        );
    }

    public static DefaultAIProvider<String> createInstance(final String path, final String name) {
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
    public <T> T executeWithModel(Function<ChatLanguageModel, T> executor) {
        return executor.apply(this.model);
    }

    @Override
    public <T> T executeWithRetriever(Function<ConversationalRetrievalChain, T> executor) {
        final ConversationalRetrievalChain chain = ConversationalRetrievalChain.builder()
                .chatLanguageModel(this.model)
                .retriever(EmbeddingStoreRetriever.from(this.provider.get().provideStore(), this.provider.get().provideModel()))
                // .chatMemory() // you can override default chat memory
                // .promptTemplate() // you can override default prompt template
                .build();
        return executor.apply(chain);
    }

    @Override
    public AIProvider<N> withStore(StoreProvider<N> storeProvider) {
        this.provider.set(storeProvider);
        return this;
    }

    @Override
    public AIProvider<N> withContext(N context) {
        this.provider.get().store(context);
        return this;
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> find(String query, int maxResults) {
        final Embedding queryEmbedding = this.provider.get().provideModel().embed(query).content();
        return this.provider.get().provideStore().findRelevant(queryEmbedding, maxResults);
    }

    @Override
    public Optional<EmbeddingMatch<TextSegment>> find(String query) {
        final Embedding queryEmbedding = this.provider.get().provideModel().embed(query).content();
        return this.provider.get().provideStore().findRelevant(queryEmbedding, 1)
                .stream()
                .findFirst();
    }


}
