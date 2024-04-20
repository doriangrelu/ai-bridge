package fr.ia;

import fr.ia.services.ai.DefaultAIProvider;
import fr.ia.spi.ai.AIProvider;

public class Main {
    public static void main(String[] args) {
        final AIProvider<String> ai = DefaultAIProvider.createInstance("secret/gpt", "api-key");
        final String response = ai.executeWithModel(model -> model.generate("Coucou !!"));
        System.out.println(response);

        ai.withContext("J'aime les motos");
        ai.withContext("Je n'aime pas le foot");
        ai.withContext("J'adore le JAVA !");

        System.out.println(
                ai.find("Java ?")
                        .map(AIProvider::collect)
        );

        final String chainResponse = ai.executeWithRetriever(chain -> chain.execute("Donne moi la liste de mes activités préférées..."));
        System.out.println(
                chainResponse
        );

    }
}