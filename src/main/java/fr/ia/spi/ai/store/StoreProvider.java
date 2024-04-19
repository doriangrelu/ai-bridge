package fr.ia.spi.ai.store;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

public interface StoreProvider<T> {

    EmbeddingStore<TextSegment> provideStore();

    EmbeddingModel provideModel();

    StoreProvider<T> store(T data);

    Optional<String> exportStore();

    boolean exportStore(Path storePath);


    static Function<String, TextSegment> textTransformer() {
        return TextSegment::from;
    }

}
