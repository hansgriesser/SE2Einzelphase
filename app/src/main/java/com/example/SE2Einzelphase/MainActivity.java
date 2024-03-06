package com.example.SE2Einzelphase;

import android.os.Bundle;
import android.view.View;
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
        studentNumber = findViewById(R.id.editStudentNumber);
        serverResponse = findViewById(R.id.responseText);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSendToServer();
            }
        });
    }

    public void btnSendToServer() {
        String userInput = studentNumber.getText().toString();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket s = new Socket("se2-submission.aau.at",20080);

                    PrintWriter out = new PrintWriter(s.getOutputStream(),true);
                    out.println(userInput);

                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String response = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverResponse.setText(response);
                        }
                    });


                    out.close();
                    in.close();
                    s.close();

                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}