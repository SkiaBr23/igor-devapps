package br.unb.igor.helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.unb.igor.model.Aventura;
import br.unb.igor.model.Convite;
import br.unb.igor.model.User;

public class DB {

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    public static StorageReference store = FirebaseStorage.getInstance().getReference();

    public static void deleteAllAdventures() {
        ref.child("adventures").addListenerForSingleValueEvent(new ValueEventListener() {
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
                ref.child("adventures").removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                HashMap<String, Object> users = dataSnapshot.getValue(genericTypeIndicator);
                if (users != null) {
                    for (String id : users.keySet()) {
                        String name = dataSnapshot.child(id).child("fullName").getValue(String.class);
                        String email = dataSnapshot.child(id).child("email").getValue(String.class);
                        System.out.println("Clearing adventures of user " + name + " (" + email + "/" + id + ").");
                        ref.child("users").child(id).child("aventuras").removeValue();
                        ref.child("users").child(id).child("convites").removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static void createAdventure(final Aventura aventura) {
        String key = ref.child("adventures").push().getKey();
        aventura.setKey(key);
        ref.child("adventures").child(key).setValue(aventura);
    }

    public static void upsertAdventure(final Aventura aventura) {
        if (aventura.getKey().isEmpty()) {
            return;
        }
        ref.child("adventures").child(aventura.getKey()).setValue(aventura);
    }

    public static void removeAdventure(final Aventura aventura) {
        ref.child("adventures").child(aventura.getKey()).removeValue();
    }

    public static void getAdventureById(String id, final OnCompleteHandler handler) {
        ref.child("adventures").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void upsertUser(final User user) {
        if (user.getUserId().isEmpty()) {
            return;
        }
        ref.child("users").child(user.getUserId()).setValue(user);
    }

    public static void setUserInvitation(final User invitedUser, User whoInvited, final Aventura aventura, boolean isInvited) {
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
        Convite convite = new Convite(whoInvited.getUserId(), aventura.getKey(),whoInvited.getFullName(),whoInvited.getProfilePictureUrl());
        invitedUser.setInvitation(convite, isInvited);
        if (isInvited) {
            aventura.addInvitedUser(uid);
        } else {
            aventura.removeInvitedUser(uid);
        }
        upsertAdventure(aventura);
        upsertUser(invitedUser);
    }

    public static void getUserInfoById(final String uid, final OnCompleteHandler callback) {
        ref.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void getUsersById(final List<String> uids, final OnCompleteHandler callback) {
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
            ref.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void updateUserPhoto(User user, Bitmap bm, final OnCompleteHandler handler) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        UploadTask task = store
            .child("profilePhotos")
            .child(user.getUserId())
            .putBytes(baos.toByteArray());
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handler.cancel();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                handler.setExtra(taskSnapshot.getDownloadUrl());
                handler.advance();
            }
        });


    }

}
