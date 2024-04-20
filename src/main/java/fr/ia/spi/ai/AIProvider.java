package fr.ia.spi.ai;

import dev.langchain4j.chain.ConversationalRetrievalChain;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import fr.ia.spi.ai.store.StoreProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface AIProvider<E> {

    <T> T executeWithModel(Function<ChatLanguageModel, T> executor);

    <T> T executeWithRetriever(Function<ConversationalRetrievalChain, T> executor);

     AIProvider<E> withStore(StoreProvider<E> storeProvider);

    AIProvider<E> withContext(E context);

    Optional<EmbeddingMatch<TextSegment>> find(String query);

    List<EmbeddingMatch<TextSegment>> find(String query, int maxResults);

    static String collect(EmbeddingMatch<TextSegment> embeddedResponse) {
        return embeddedResponse.embedded().text();
    }

    static Optional<String> collect(List<EmbeddingMatch<TextSegment>> embeddedResponses) {
        return embeddedResponses.stream().map(AIProvider::collect)
                .limit(1)
                .findAny();
    }

}
