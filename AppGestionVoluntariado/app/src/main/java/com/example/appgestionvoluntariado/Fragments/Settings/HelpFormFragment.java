package com.example.appgestionvoluntariado.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.GlobalSession;
import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;

public class HelpFormFragment extends Fragment {

    private Button btnBack, btnSend;
    private TextView userEmail;
    private EditText etMessage;
    private Volunteer volunteer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_form, container, false);

        btnBack = view.findViewById(R.id.btnVolver);
        btnSend = view.findViewById(R.id.btnEnviar);
        userEmail = view.findViewById(R.id.tvDeUsuario);
        etMessage = view.findViewById(R.id.etMensaje);
        
        volunteer = GlobalSession.getVolunteer();
        if (volunteer != null) {
            userEmail.setText(volunteer.getEmail());
        }

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileHelpFragment())
                    .commit();
        });

        btnSend.setOnClickListener(v -> validateForm());

        return view;
    }

    private void validateForm() {
        String message = etMessage.getText().toString();
        String error = "";
        
        if (message.trim().isEmpty()) {
           error = "No puedes dejar el mensaje vacio";
        }

        if (error.isEmpty()){
            sendMessage();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileHelpFragment())
                    .commit();
        }
    }

    private void sendMessage() {
        // Implement logic
    }
}
