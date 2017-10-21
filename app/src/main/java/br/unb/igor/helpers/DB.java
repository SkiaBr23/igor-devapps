package br.unb.igor.helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Sessao;
import br.unb.igor.model.User;

public class DB {

    private DatabaseReference db;
    private FirebaseAuth auth;
    private static DB singleton;

    public DB(FirebaseAuth auth, DatabaseReference db) {
        this.auth = auth;
        this.db = db;
        DB.singleton = this;
    }

    public static DB getLastInstance() {
        return DB.singleton;
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

    public void upsertAdventure(final Aventura aventura) {
        if (aventura.getKey().isEmpty()) {
            return;
        }
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/adventures/" + aventura.getKey(), aventura);
        db.updateChildren(childUpdates);
    }

    public void removeAdventure(final Aventura aventura) {
        if (auth.getCurrentUser() != null) {
            final String userId = auth.getCurrentUser().getUid();
            db.child("adventures").child(aventura.getKey()).removeValue();
            db.child("users").child(userId).child("adventures").child(aventura.getKey()).removeValue();
        }
    }

    public void getUserInfoById(final String uid, final OnCompleteHandler callback) {
        db.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                callback.setExtra(user);
                callback.advance();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.cancel();
            }
        });
    }

    public void getUsersById(final List<String> uids, final OnCompleteHandler callback) {
        final List<User> users = new ArrayList<>();
        final OnCompleteHandler handler = new OnCompleteHandler(uids.size(), new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra) {
                callback.setExtra(users);
                callback.advance();
            }
        });
        for (String uid : uids) {
            db.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    synchronized (users) {
                        users.add(user);
                    }
                    handler.advance();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    handler.advance();
                }
            });
        }
    }

    public void getUserByUsername(final String username) {

    }

}
