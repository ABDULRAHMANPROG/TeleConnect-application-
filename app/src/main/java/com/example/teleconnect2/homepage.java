package com.example.teleconnect2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class homepage extends AppCompatActivity {

    private EditText editTextSearch;
    private EditText editTextCalculatePlan;

    private Button btnSearch;
    private Button btnLogout;
    private TextView textViewShowPlan;

    private Button btnMakeOffer;


    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleconnect2);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Customers");

        editTextSearch = findViewById(R.id.editTextSearch);
        editTextCalculatePlan = findViewById(R.id.editTextCalculatePlan);
        textViewShowPlan = findViewById(R.id.textViewShowPlan);
        btnMakeOffer = findViewById(R.id.btnMakeOffer);

        // Initialize btnSearch by finding its ID in the layout
        btnSearch = findViewById(R.id.btnSearch);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchCustomer();


            }
        });

        btnMakeOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredPlan = editTextCalculatePlan.getText().toString().trim();
                getOffer(enteredPlan);
            }
        });

        // Initialize btn Navigate To TaskPage by finding its ID in the layout
        Button btnNavigateToTaskPage = findViewById(R.id.btnNavigateToTaskPage);

        btnNavigateToTaskPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the TaskPageActivity when the button is clicked
                startActivity(new Intent(homepage.this, TaskPage.class));
            }
        });


        // Initialize btnLogout by finding its ID in the layout
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action here
                FirebaseAuth.getInstance().signOut();
                // Redirect to login page
                startActivity(new Intent(homepage.this, LoginActivity.class));
                finish();// Close the current activity
                Toast.makeText(homepage.this, "Login Out successful", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void searchCustomer() {
        String searchQuery = editTextSearch.getText().toString().trim();

        // Assuming you're searching by the customer's number and retrieving all fields
        databaseReference.child(searchQuery).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String customerName = dataSnapshot.child("name").getValue(String.class);
                    String customerAddress = dataSnapshot.child("address").getValue(String.class);
                    String customerPlan = dataSnapshot.child("plan").getValue(String.class);
                    String customerBill = dataSnapshot.child("BILL").getValue(String.class);

                    // Display customer information in TextViews
                    TextView textViewCustomerName = findViewById(R.id.textViewCustomerName);
                    TextView textViewCustomerAddress = findViewById(R.id.textViewCustomerAddress);
                    TextView textViewCalculatePlan = findViewById(R.id.textViewCalculatePlan);
                    TextView textViewCustomerBill = findViewById(R.id.textViewCustomerBill);

                    textViewCustomerName.setText(customerName);
                    textViewCustomerAddress.setText(customerAddress);
                    textViewCalculatePlan.setText(customerPlan);
                    textViewCustomerBill.setText(customerBill);
                } else {
                    // Customer doesn't exist
                    Toast.makeText(homepage.this, "No record found for this number", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential error
                Toast.makeText(homepage.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });
    }
    private void getOffer(String enteredPlan) {
        // Assuming plans are stored in Firebase under a node named "plans"
        DatabaseReference plansRef = FirebaseDatabase.getInstance().getReference("Plans");

        plansRef.orderByValue().equalTo(enteredPlan).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String matchingPlan = snapshot.getKey();
                        // Display the matching plan in the TextView
                        textViewShowPlan.setText("The Offer is  "+ matchingPlan);
                        return;
                    }
                } else {
                    // If there are no matching plan found
                    textViewShowPlan.setText("No matching plan found");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                textViewShowPlan.setText("Error: " + databaseError.getMessage());
            }
        });
    }


}

