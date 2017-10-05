package br.unb.igor.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;

public class DB {

    private DatabaseReference db;
    private FirebaseAuth auth;

    public DB(FirebaseAuth auth, DatabaseReference db) {
        this.auth = auth;
        this.db = db;
    }

    public String createAdventure(final Aventura aventura) {
        final String userId = auth.getCurrentUser().getUid();
        String key = db.child("adventures").push().getKey();
        aventura.setKey(key);
        aventura.setMestreUserId(userId);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/adventures/" + key, aventura);
        childUpdates.put("/users/" + userId + "/adventures/" + key, true);
        db.updateChildren(childUpdates);
        return key;
    }

    public void removeAdventure(final Aventura aventura) {
        if(auth.getCurrentUser() != null) {
            final String userId = auth.getCurrentUser().getUid();
            db.child("adventures").child(aventura.getKey()).removeValue();
            db.child("users").child(userId).child("adventures").child(aventura.getKey()).removeValue();
        }
    }

    public String createSession(final String keyAventura, final Sessao sessao){
        String key = db.child("adventures").child(keyAventura).child("sessions").push().getKey();
        sessao.setKey(key);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/adventures/" + keyAventura + "/sessoes/" + key, sessao);
        db.updateChildren(childUpdates);
        return key;
    }

    public void getUserByUsername(final String username) {

    }

}
