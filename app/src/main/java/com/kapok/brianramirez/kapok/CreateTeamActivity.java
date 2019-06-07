package com.kapok.brianramirez.kapok;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmList;
import io.realm.SyncUser;

public class CreateTeamActivity extends AppCompatActivity {

    Button confirmTeam;
    EditText team_name;
    private RealmAsyncTask asyncTransaction;
    EditText team_location;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        team_name = (EditText) findViewById(R.id.teamName);
        team_location = (EditText) findViewById(R.id.location);
        confirmTeam = (Button) findViewById(R.id.create_NewTeam);
//        Realm realm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        String currentUser = mAuth.getCurrentUser().getEmail();


        confirmTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RealmList<Person> b  = new RealmList<>();
//                Team newTeam = new Team(0, team_name.getText().toString(), team_location.getText().toString());
                //RealmManager.add(newTeam);
               // String currentUserAboutme = realm.where(Person.class).equalTo("id", SyncUser.current().getIdentity()).findFirstAsync().getAboutMe();
                //realm.commitTransaction();
              //  int twen=0;
//                String currentId = SyncUser.current().getIdentity();
//                final Person[] currentUser = null;

                FirebaseFirestore db = FirebaseFirestore.getInstance();

                //create list containing the user
                ArrayList<String> members = new ArrayList<String>(1);
                members.add(currentUser);
                String teamID = genTeamCode(db);

                // Create a new user with a first and last name
                Map<String, Object> team = new HashMap<>();
                team.put("name", team_name.getText().toString());
                team.put("location", team_location.getText().toString());
                team.put("members", members);
                team.put("id", teamID);


                db.collection("Teams").document(teamID)
                        .set(team)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                DocumentReference userProf = db.collection("Profiles").document(currentUser);

                                // Set the admin field of the current user to true
                                userProf
                                        .update("isAdmin", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                                userProf
                                        .update("team", FieldValue.arrayUnion(teamID))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
//

                                openCodeDisplay();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateTeamActivity.this, "Team Setup failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });


/*              Person currentUser = realm.where(Person.class).equalTo("id", currentId).findFirst();
                currentUser.setTeam(newTeam);
                currentUser.setAdmin(true);
                currentUser.setStatus("joined");
                realm.commitTransaction();
                realm.close();
                openCodeDisplay();*/
            }
        });
    }

    public void openCodeDisplay(){
        Intent i = new Intent(this, WaitingScreenActivity.class);
        startActivity(i);
    }

    private String genTeamCode(FirebaseFirestore db){
        do{
            Random rand = new Random();
            int number = rand.nextInt(1000000)+100000;
            final String[] code = {Integer.toString(number)};
            DocumentReference docRef = db.collection("Teams").document(code[0]);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            code[0] = null;
                        }
                    } else {
                    }
                }
            });
            if(code[0] != null) {
                return code[0];
            }
        }
        while(true);
    }

}
