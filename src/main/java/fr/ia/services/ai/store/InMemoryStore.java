package fr.ia.services.ai.store;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import fr.ia.spi.ai.store.StoreProvider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryStore<T> implements StoreProvider<T> {

    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel model;
    private final Function<T, TextSegment> transformer;

    public static <E> InMemoryStore<E> createInstance(final Function<E, TextSegment> transformer) {
        return new InMemoryStore<>(
                new InMemoryEmbeddingStore<>(),
                new AllMiniLmL6V2EmbeddingModel(),
                transformer
        );
    }

    public static InMemoryStore<String> createInstance() {
        return new InMemoryStore<>(
                new InMemoryEmbeddingStore<>(),
                new AllMiniLmL6V2EmbeddingModel(),
                StoreProvider.textTransformer()
        );
    }

    public static InMemoryStore<String> createInstance(final String serdeStore) {
        final InMemoryEmbeddingStore<TextSegment> importedStore = InMemoryEmbeddingStore.fromJson(serdeStore);
        return new InMemoryStore<>(
                importedStore,
                new AllMiniLmL6V2EmbeddingModel(),
                StoreProvider.textTransformer()
        );
    }

    public static InMemoryStore<String> createInstance(final Path storePath) {
        final InMemoryEmbeddingStore<TextSegment> importedStore = InMemoryEmbeddingStore.fromFile(storePath);
        return new InMemoryStore<>(
                importedStore,
                new AllMiniLmL6V2EmbeddingModel(),
                StoreProvider.textTransformer()
        );
    }

    @Override
    public EmbeddingStore<TextSegment> provideStore() {
        return this.store;
    }

    @Override
    public EmbeddingModel provideModel() {
        return this.model;
    }

    @Override
    public StoreProvider<T> store(T data) {
        final TextSegment segment = this.transformer.apply(data);
        final Embedding embedding = this.provideModel().embed(segment).content();
        this.provideStore().add(embedding, segment);
        return this;
    }

    @Override
    public Optional<String> exportStore() {
        if (this.provideStore() instanceof InMemoryEmbeddingStore<?> memoryEmbeddingStore) {
            return Optional.of(memoryEmbeddingStore.serializeToJson());
        }
        return Optional.empty();
    }

    @Override
    public boolean exportStore(Path storePath) {
        if (this.provideStore() instanceof InMemoryEmbeddingStore<?> memoryEmbeddingStore) {
            memoryEmbeddingStore.serializeToFile(storePath);
            return true;
        }
        return false;
    }

}
