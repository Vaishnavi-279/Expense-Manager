package com.example.expensemanager;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class AddTransactionActivity extends AppCompatActivity {

    EditText edtAmount, edtDescription;
    Button btnSave;
    SessionManager session;
    TransactionDb transactionDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        edtAmount = findViewById(R.id.edtAmount);
        edtDescription = findViewById(R.id.edtDescription);
        RadioGroup radioGroupType = findViewById(R.id.radioGroupType);
        btnSave = findViewById(R.id.btnSave);
        session = new SessionManager(this);
        transactionDb = new TransactionDb(this);

        btnSave.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString();
            String description = edtDescription.getText().toString();
            int selectedId = radioGroupType.getCheckedRadioButtonId();

            if (amountStr.isEmpty() || description.isEmpty() || selectedId == -1) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            float amount;
            try {
                amount = Float.parseFloat(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            String type = ((RadioButton) findViewById(selectedId)).getText().toString().toLowerCase();
            int userId = session.getUserId();
            long timestamp = System.currentTimeMillis();

            Transaction transaction = new Transaction(0, userId, amount, description, timestamp, type);
            transactionDb.insertTransaction(transaction);

            Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
