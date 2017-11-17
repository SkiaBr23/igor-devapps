package br.unb.igor.helpers;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
import br.unb.igor.model.Jogada;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;

public interface AdventureListener {
    void onRemoveAdventure(Aventura aventura, int index);
    void onSelectAdventure(Aventura aventura, int index);
    void onCreateAdventure(String title);
    void onClickAddSessionFAB();
    void onSelectSessao (Sessao sessao, int index);
    void onClickInviteUsersFAB();
    void onViewUserInfo(User user);
    void onConfirmarSessao(String tituloSessao, String dataSessao);
    void onUserInvitation(User user, boolean hasBeenInvited);
    void onUserKickedOut(User user);
    void onCreateRoll(Jogada jogada);
    void onClickConfirmarConvite(Convite convite, int index);
    void onClickCancelarConvite(Convite convite, int index);
    void rolagemDados();
    void onInvitesView();
}
