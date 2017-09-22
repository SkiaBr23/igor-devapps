package br.unb.igor.helpers;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;

public interface AdventureEditListener {
    void onAdicionarSessao(String keyAventura);
    void onSelectSessao (Sessao sessao, int index);
}
