package com.example.mohammad.tennisclub;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mohammad.tennisclub.model.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewChatFragment extends Fragment {


    public ViewChatFragment() {
    }

    DocumentReference userRef;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_view_chat, container, false);

        String fromId = "tw9dXHrBf4fVgjkW8FPu4PURkPh2";

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore fsdb = FirebaseFirestore.getInstance();
        final ArrayList<Message> messages = new ArrayList<>();
        final MessageListViewAdapter messagesAdapter = new MessageListViewAdapter(messages);
        ListView chatsListView = (ListView) rootView.findViewById(R.id.lv_messages);
        chatsListView.setAdapter(messagesAdapter);
        userRef = fsdb.document("users/" + user.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                userId = snapshot.getId();
            }
        });
        final DocumentReference fromRef = fsdb.document("coach/" + fromId);
        fromRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot snapshot) {
                ((TextView) rootView.findViewById(R.id.tv_name)).setText((String) snapshot.get("name"));
                ((Button) rootView.findViewById(R.id.btn_call)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + snapshot.get("phone"))));
                    }
                });
                ((Button) rootView.findViewById(R.id.btn_email)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_SENDTO).setData(Uri.parse("mailto:" + (String) snapshot.get("email") + "?subject=Message for Energy Tennis Club&body=Hi " + (String) snapshot.get("name") + ",")));
                    }
                });
            }
        });

        fsdb.collection("messages")
                .whereEqualTo("to", userRef)
                .whereEqualTo("from", fromRef)
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentReference> users = new ArrayList<>();
                        if (snapshots != null) {
                            for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                                Message message = documentChange.getDocument().toObject(Message.class);
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    messages.add(message);
                                }
                            }
                        }
                        updateMessages(messages, messagesAdapter);
                    }
                });
        fsdb.collection("messages")
                .whereEqualTo("from", userRef)
                .whereEqualTo("to", fromRef)
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        List<DocumentReference> users = new ArrayList<>();
                        if (snapshots != null) {
                            for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                                Message message = documentChange.getDocument().toObject(Message.class);
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    messages.add(message);
                                }
                            }
                        }
                        updateMessages(messages, messagesAdapter);
                    }
                });

        rootView.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etMessage = ((EditText) rootView.findViewById(R.id.et_message));
                final String message = etMessage.getText().toString().trim();
                if (!message.equals("")) {
                    CollectionReference messagesRef = fsdb.collection("messages/");
                    Map<String, Object> messageMap = new HashMap<>();
                    messageMap.put("from", userRef);
                    messageMap.put("to", fromRef);
                    messageMap.put("message", message);
                    messageMap.put("date", new Date());
                    messagesRef.add(messageMap);
                }
                etMessage.setText("");
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                fromRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(final DocumentSnapshot snapshot) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendMessage((String) snapshot.get("token"), message, (String) snapshot.get("name"), userId);
                            }
                        }).start();
                    }
                });
            }
        });

        return rootView;
    }

    private void sendMessage(String token, String message, String from, String fromUserId) {
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send"); //in the real code, there is an ip and a port
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=AIzaSyCiVN0XUC4U61fh8mRAsXYOEDvMsfQQcOY");
            conn.connect();

            JSONObject dataJSON = new JSONObject();
            dataJSON.put("title", "Message from " + from);
            dataJSON.put("message", message);
            dataJSON.put("fromId", fromUserId);

            JSONObject messageJSON = new JSONObject();
            messageJSON.put("to", token);
            messageJSON.put("data", dataJSON);
            messageJSON.put("priority", "high");

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(messageJSON.toString());

            os.flush();
            os.close();

            Log.d("Message:", conn.getResponseMessage());
            Log.d("Message:", messageJSON.toString());

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateMessages(ArrayList<Message> messages, MessageListViewAdapter messagesAdapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            messages.sort(new Comparator<Message>() {
                @Override
                public int compare(Message o1, Message o2) {
                    long o1time = o1.getDate().getTime();
                    long o2time = o2.getDate().getTime();
                    return Long.compare(o1time, o2time);
                }
            });
        }
        messagesAdapter.notifyDataSetChanged();
    }

    private class MessageListViewAdapter extends BaseAdapter {

        List<Message> chat;

        MessageListViewAdapter(List<Message> chat) {
            this.chat = chat;
        }

        @Override
        public int getCount() {
            return chat.size();
        }

        @Override
        public Message getItem(int position) {
            return chat.get(position);
        }

        @Override
        public long getItemId(int position) {
            return chat.get(position).hashCode();
        }

        @Override
        public View getView(int position, View listItemView, ViewGroup parent) {
            Message message = chat.get(position);
            if (listItemView == null) {
                if (message.getFrom().equals(userRef)) {
                    listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        listItemView.setBackgroundColor(getResources().getColor(R.color.primaryDark, null));
                    }
                } else {
                    listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat, parent, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        listItemView.setBackgroundColor(getResources().getColor(R.color.secondaryDark, null));
                    }
                }
            }
            ((TextView) listItemView.findViewById(R.id.tv_item_message)).setText(message.getMessage());
            ((TextView) listItemView.findViewById(R.id.tv_item_timestamp)).setText(message.getDateString());

            return listItemView;
        }

    }

}
