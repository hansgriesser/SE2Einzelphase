package com.example.SE2Einzelphase;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private EditText studentNumber;
    private TextView serverResponse;
    private TextView calculatedMatrNr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSend = findViewById(R.id.btnSend);
        Button btnCaclulate = findViewById(R.id.btnCalculate);

        studentNumber = findViewById(R.id.editStudentNumber);
        serverResponse = findViewById(R.id.responseText);
        calculatedMatrNr = findViewById(R.id.calculatedMatrNrPlaceHolder);

        btnSend.setOnClickListener(v -> btnSendToServer());

        btnCaclulate.setOnClickListener(v -> {
            String userInput = studentNumber.getText().toString();
            String res = manipulateMatrNrString(userInput);
            runOnUiThread(() -> calculatedMatrNr.setText(res));
        });
    }

    public void btnSendToServer() {
        String userInput = studentNumber.getText().toString();


        new Thread(() -> {
            try {
                Socket s = new Socket("se2-submission.aau.at", 20080);

                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                out.println(userInput);

                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String response = in.readLine();

                runOnUiThread(() -> serverResponse.setText(response));


                out.close();
                in.close();
                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    // Replace every second digit with corresponding character (0 = a, 1 = b....)
    // For example: 12209209 should return 1b2a9b0i
    public String manipulateMatrNrString(String matrnr) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matrnr.length(); i++) {
            if (i % 2 != 0) {
                // Latin Alphabet: Lowercase: a-h = 97-105 with UniCode representation
                char digit = matrnr.charAt(i);
                // If the digit is zero, we replace by a, otherwise we add the numeric value
                char correspondingChar = (digit == '0') ? 'a' : (char) ('a' + Character.getNumericValue(digit) - 1);
                sb.append(correspondingChar);
            } else {
                sb.append(matrnr.charAt(i));
            }
        }
        return sb.toString();
    }
}