package com.example.mohammad.tennisclub.fcm;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseAppInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String newToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
        fsdb.document("users/" + auth.getUid())
                .update("token", newToken);
    }

}
