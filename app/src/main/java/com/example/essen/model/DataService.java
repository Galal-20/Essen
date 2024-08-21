package com.example.essen.model;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DataService {
    private FirebaseFirestore db;
    private String userId;


    public DataService(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    public void saveUserData(FirebaseUser user, Map<String, Object> data, final SaveCallback callback) {
        db.collection("users").document(user.getUid())
                .set(data)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }


    public interface SaveCallback {
        void onSuccess();

        void onFailure(String message);
    }

}

