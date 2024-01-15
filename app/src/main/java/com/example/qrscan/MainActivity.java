package com.example.qrscan;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<student> studentArrayList;
    stdAdapter stdadapter;

    FirebaseFirestore db;
    Button BTScan,enter,input,cancel,confirm,mannual;
    EditText otherTXT;
    Spinner room,exam;
    Map<String, Object> data = new HashMap<>();
    ProgressDialog progressDialog;
    AlertDialog dialog;
    public String testName,type,sj,teacher,stdroom,adviser,id,ID;
    public Boolean select = false;
    public ArrayList<String> stdID;
    CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent receive = getIntent();
        String receivedValue = receive.getStringExtra("KEY_SENDER");
        String[]  data = receivedValue.split(" ");
        id = data[0];
        teacher = data[1];
        db = FirebaseFirestore.getInstance();
        otherTXT = findViewById(R.id.othertxt);
        enter = findViewById(R.id.manualID);
        room = findViewById(R.id.roomspin);
        exam = findViewById(R.id.examspin);
        BTScan = findViewById(R.id.btScan);
        input = findViewById(R.id.ManualBtn);
        cancel = findViewById(R.id.CancelBtn);
        confirm = findViewById(R.id.confirmbtn);
        //mannual = findViewById(R.id.ManualBtn);
        progressDialog = new ProgressDialog(this);
        String[] items = new String[]{"MidTerm", "Final", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        exam.setAdapter(adapter);
        ArrayList<String> strings = new ArrayList<>();
        stdID = new ArrayList<>();
        db.collection("student")
                .whereEqualTo(id, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(strings.contains(String.valueOf(document.getString("room")))){
                                    Log.d(TAG, document.getId() + " => " + document.getString("room"));
                                }
                                else{
                                    strings.add(String.valueOf(document.getString("room")));
                                    Log.d(TAG, "add to array");
                                }

                            }

                            Log.d(TAG, String.valueOf(strings.size()));
                            Log.d(TAG, String.valueOf(strings));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item, strings);
                            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            room.setAdapter(arrayAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        db.collection("subject").whereEqualTo("ID", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        sj = document.getString("name");
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        exam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    otherTXT.setVisibility(View.INVISIBLE);
                } else if (i==1) {
                    otherTXT.setVisibility(View.INVISIBLE);
                }
                if(i==2){
                    otherTXT.setVisibility(View.VISIBLE);
                    select = true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
room.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        db.collection("student").whereEqualTo("room", room.getSelectedItem().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        adviser = document.getString("adviser");

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
});

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Mannual Input");

                View getview = getLayoutInflater().inflate(R.layout.dialog_box,null);
                EditText inputID;
                Button submit;
                inputID = getview.findViewById(R.id.inputIDtxt);
                submit = getview.findViewById(R.id.submitbtn);
                builder.setView(getview);
                dialog = builder.create();
                dialog.show();
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String getid = String.valueOf(inputID.getText());
                        db.collection("student").whereEqualTo("room", room.getSelectedItem().toString()).whereEqualTo("ID", getid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        ID = document.getId();
                                        Map<String, Object> data = new HashMap<>();
                                        String getid = String.valueOf(inputID.getText());
                                        if(ID.equals(getid)) {
                                            data.put(getid, true);
                                            data.put(getid + "_time", FieldValue.serverTimestamp());
                                            db.collection("test").document(testName).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(MainActivity.this,"Add Data Success",Toast.LENGTH_SHORT).show();
                                                    EvenChangListrner();
                                                    dialog.dismiss();

                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this,"นักศึกษารหัส : "+getid+"ไม่ได้ลงทะเบียนในรายวิชานี้",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    //stdID.add(result.getContents());
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
                        //ID = result.getContents();
                    }
                });
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(MainActivity.this, MainActivity2.class);
                send.putExtra("KEY_SENDER",teacher);
                startActivity(send);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView = findViewById(R.id.stdrv);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                studentArrayList = new ArrayList<student>();
                stdadapter = new stdAdapter(MainActivity.this,studentArrayList);
                recyclerView.setAdapter(stdadapter);
                cancel.setEnabled(false);
                BTScan.setEnabled(false);
                input.setEnabled(false);
                enter.setEnabled(true);
                confirm.setEnabled(false);
                db.collection("test").document(testName)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(otherTXT.getText().toString()) && select == true ){
                    Toast.makeText(MainActivity.this,"Please input Test name",Toast.LENGTH_SHORT).show();
                }else{
                    confirm.setEnabled(true);
                    cancel.setEnabled(true);
                    BTScan.setEnabled(true);
                    input.setEnabled(true);
                    enter.setEnabled(false);
                    Map<String, Object> data = new HashMap<>();
                    // Get the current date and time
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    // Define a specific date and time format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
                    // Format the current date and time using the formatter
                    String formattedDateTime = currentDateTime.format(formatter);
                    testName = id + "_" + formattedDateTime;
                    recyclerView = findViewById(R.id.stdrv);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    studentArrayList = new ArrayList<student>();
                    stdadapter = new stdAdapter(MainActivity.this,studentArrayList);
                    recyclerView.setAdapter(stdadapter);
                    Toast.makeText(MainActivity.this,String.valueOf(room.getSelectedItem()),Toast.LENGTH_SHORT).show();

                    if(exam.getSelectedItemPosition() == 0){
                        type = "MidTerm";
                    } else if (exam.getSelectedItemPosition() == 1) {
                        type = "Final";
                    }
                    else{
                        type = otherTXT.getText().toString();
                    }
                    data.put("adviser", adviser);
                    data.put("subject", sj);
                    data.put("room", room.getSelectedItem().toString());
                    data.put("type", type);
                    data.put("Teacher", teacher);
                    db.collection("test").document(testName)
                            .update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error updating document", e);
                                    data.put("start_test", FieldValue.serverTimestamp());
                                    db.collection("test").document(testName).set(data);
                                }
                            });
                    //EvenChangListrner();
                }
            }
        });
        BTScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ScanOptions options = new ScanOptions();
                    options.setPrompt("Volume up to flash on");
                    options.setBeepEnabled(true);
                    options.setOrientationLocked(true);
                    options.setCaptureActivity(Capture.class);
                    barLaucher.launch(options);
            }
        });
    }


    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result ->
    {
        if(result.getContents() != null)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
            db.collection("student").whereEqualTo("room", room.getSelectedItem().toString()).whereEqualTo("ID", result.getContents()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ID = document.getId();
                            Map<String, Object> data = new HashMap<>();

                            if(ID.equals(result.getContents())) {
                                data.put(result.getContents(), true);
                                data.put(result.getContents() + "_time", FieldValue.serverTimestamp());
                                db.collection("test").document(testName).update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MainActivity.this,"Add Data Success",Toast.LENGTH_SHORT).show();
                                        EvenChangListrner();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(MainActivity.this,"นักศึกษารหัส : "+result.getContents()+"ไม่ได้ลงทะเบียนในรายวิชานี้",Toast.LENGTH_SHORT).show();
                            }
                        }
                        //stdID.add(result.getContents());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
            //ID = result.getContents();

        }
    });
    private void EvenChangListrner() {

        db.collection("student").whereEqualTo("ID",ID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null)
                        {
                            Log.e("Firestore Error",error.getMessage());
                            return;
                        }
                        for(DocumentChange dc : value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED)
                            {
                                studentArrayList.add(dc.getDocument().toObject(student.class));
                            }

                            stdadapter.notifyDataSetChanged();
                        }
                    }
                });
    }

}