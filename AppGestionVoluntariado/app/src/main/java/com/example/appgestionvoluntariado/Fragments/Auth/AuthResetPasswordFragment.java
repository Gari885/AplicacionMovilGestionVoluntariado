package com.example.appgestionvoluntariado.Fragments.Auth;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;

public class AuthResetPasswordFragment extends Fragment {

    private EditText etEmail;
    private Button btnReset;
    private ProgressBar pbLoading;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupListeners();
    }

    private void initViews(View v) {
        etEmail = v.findViewById(R.id.etEmail);
        btnReset = v.findViewById(R.id.btnReset);
        pbLoading = v.findViewById(R.id.pbLoading);
        
        MaterialToolbar toolbar = v.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void setupListeners() {
        btnReset.setOnClickListener(v -> handleReset());
    }

    private void handleReset() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            StatusHelper.showStatus(getContext(), "Email requerido", "Por favor, introduce tu email.", true);
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StatusHelper.showStatus(getContext(), "Email inválido", "El formato del correo no es correcto.", true);
            return;
        }

        toggleLoading(true);

        // Always show success message for security reasons, or handle specific errors if preferred by user [cite: 2026-01-19]
        // User asked to check if email exists. Firebase creates an error if user not found, 
        // but for security it's often better to say "If account exists, email sent".
        // However, user specifically asked: "comprobamos si ese emxail existe, si existe mandamos un email"
        // Firebase sendPasswordResetEmail fails if user doesn't exist.
        
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    toggleLoading(false);
                    if (task.isSuccessful()) {
                        StatusHelper.showStatus(getContext(), "Correo enviado", 
                                "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.", false);
                        getParentFragmentManager().popBackStack(); // Go back to login
                    } else {
                         // To avoid enumeration attacks, stick to generic message or only show error for network issues
                         // But for this project, let's show success message even if it failed due to user not found?
                         // User asked "si existe mandamos un email", implying if NOT exists, maybe tell them?
                         // "asi damos menos pistas por si alguien quiere intentar entrar" -> This means GENERIC MESSAGE.
                         
                         // So regardless of success/failure (except network error), we should probably show success.
                         // But sendPasswordResetEmail returns failure if email not found. 
                         // To be secure: "If email exists, sent."
                         
                        StatusHelper.showStatus(getContext(), "Proceso finalizado", 
                                "Si el correo es correcto y está registrado, se han enviado las instrucciones.", false);
                        getParentFragmentManager().popBackStack();
                    }
                });
    }

    private void toggleLoading(boolean isLoading) {
        if (pbLoading == null) return;
        pbLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        btnReset.setEnabled(!isLoading);
        btnReset.setText(isLoading ? "" : "Enviar Correo");
    }
}