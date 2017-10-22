package br.unb.igor.helpers;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class ChildEventListenerAdapter implements ChildEventListener {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
    }
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
    }
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }
    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
    }
    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
