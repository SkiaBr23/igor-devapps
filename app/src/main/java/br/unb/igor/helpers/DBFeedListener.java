package br.unb.igor.helpers;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Jogada;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;

public class DBFeedListener {
    public void onUserChanged(User newUser, User oldUser) {}
    public void onSelectedAdventureChange(Aventura newAdventure, Aventura oldAdventure) {}
    public void onInvitationAdded(Convite invitation, int index) {}
    public void onInvitationRemoved(Convite invitation, int index) {}
}
