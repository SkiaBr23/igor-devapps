package br.unb.igor.helpers;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
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

    public void deleteAllAdventures() {
        db.child("adventures").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> adventures = dataSnapshot.getValue(genericTypeIndicator);
                if (adventures != null) {
                    for (String id : adventures.keySet()) {
                        String name = dataSnapshot.child(id).child("titulo").getValue(String.class);
                        System.out.println("Deleting adventure " + name + " (" + id + ").");
                    }
                }
                db.child("adventures").removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        db.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> users = dataSnapshot.getValue(genericTypeIndicator);
                if (users != null) {
                    for (String id : users.keySet()) {
                        String name = dataSnapshot.child(id).child("fullName").getValue(String.class);
                        String email = dataSnapshot.child(id).child("email").getValue(String.class);
                        System.out.println("Clearing adventures of user " + name + " (" + email + "/" + id + ").");
                        db.child("users").child(id).child("aventuras").removeValue();
                        db.child("users").child(id).child("convites").removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static DB getLastInstance() {
        return DB.singleton;
    }

    public void createAdventure(final Aventura aventura) {
        String key = db.child("adventures").push().getKey();
        aventura.setKey(key);
        db.child("adventures").child(key).setValue(aventura);
    }

    public void upsertAdventure(final Aventura aventura) {
        if (aventura.getKey().isEmpty()) {
            return;
        }
        db.child("adventures").child(aventura.getKey()).setValue(aventura);
    }

    public void removeAdventure(final Aventura aventura) {
        db.child("adventures").child(aventura.getKey()).removeValue();
    }

    public void getAdventureById(String id, final OnCompleteHandler handler) {
        db.child("adventures").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Aventura adventure = dataSnapshot.getValue(Aventura.class);
                handler.setExtra(adventure);
                handler.advance();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.cancel();
            }
        });
    }

    public void upsertUser(final User user) {
        if (user.getUserId().isEmpty()) {
            return;
        }
        db.child("users").child(user.getUserId()).setValue(user);
    }

    public void setUserInvitation(final User invitedUser, String whoInvitedId, final Aventura aventura, boolean isInvited) {
        String uid = invitedUser.getUserId();
        if (aventura.getKey().isEmpty() || uid.isEmpty()) {
            // Invalid keys
            return;
        }
        if (aventura.getJogadoresUserIdsSet().contains(uid)) {
            // User has already joined the adventure
            return;
        }
        boolean isAlreadyInvited = aventura.getJogadoresConvidadosIdsSet().contains(uid);
        if (isAlreadyInvited == isInvited) {
            // Invitation has already been sent or revoked
            return;
        }
        Convite convite = new Convite(whoInvitedId, aventura.getKey());
        invitedUser.setInvitation(convite, isInvited);
        if (isInvited) {
            aventura.addInvitedUser(uid);
        } else {
            aventura.removeInvitedUser(uid);
        }
        upsertAdventure(aventura);
        upsertUser(invitedUser);
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
        if (uids.isEmpty()) {
            callback.setExtra(users);
            callback.advance();
            return;
        }
        final OnCompleteHandler handler = new OnCompleteHandler(uids.size(), new OnCompleteHandler.OnCompleteCallback() {
            @Override
            public void onComplete(boolean cancelled, Object extra, int step) {
                callback.setExtra(users);
                callback.advance();
            }
        });
        int index = 0;
        for (String uid : uids) {
            final int userIndex = index++;
            db.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final User user = dataSnapshot.getValue(User.class);
                    synchronized (users) {
                        if (userIndex >= users.size()) {
                            users.add(user);
                        } else {
                            users.add(userIndex, user);
                        }
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

}
