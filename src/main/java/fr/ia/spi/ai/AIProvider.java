package fr.ia.spi.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;

import java.util.function.Function;

public interface AIProvider {

    <T> T execute(Function<ChatLanguageModel, T> executor);

}
