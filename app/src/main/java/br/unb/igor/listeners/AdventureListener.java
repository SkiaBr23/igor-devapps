package br.unb.igor.listeners;

import br.unb.igor.model.Aventura;

public interface AdventureListener {
    void onRemoveAdventure(Aventura aventura, int index);
    void onSelectAdventure(Aventura aventura, int index);
    void onCreateAdventure(String title);
}
