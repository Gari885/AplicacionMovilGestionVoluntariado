package com.example.appgestionvoluntariado.Fragments.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Activities.AdminActivity;
import com.example.appgestionvoluntariado.Activities.OrganizationActivity;
import com.example.appgestionvoluntariado.Activities.VolunteerActivity;
import com.example.appgestionvoluntariado.Models.Response.ProfileResponse;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.example.appgestionvoluntariado.Models.Request.GoogleLoginRequest;
import org.json.JSONObject;

/**
 * Login logic in English, UI feedback in Spanish [cite: 2026-01-16].
 */
public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterPrompt, tvForgotPassword;
    private android.widget.ProgressBar pbLoading;
    private SignInButton btnGoogleLogin;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    private int contadorFallosLogin;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_login, container, false);

        initViews(view);
        setupListeners();

        checkAutoLogin();

        return view;
    }

    private void initViews(View view) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        btnGoogleLogin = view.findViewById(R.id.btnGoogleLogin);
        tvRegisterPrompt = view.findViewById(R.id.tvSignupPrompt);
        pbLoading = view.findViewById(R.id.pbLoginLoading);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        contadorFallosLogin = 0;
    }

    private void setupListeners() {
        tvRegisterPrompt.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new RegisterMenuFragment())
                .addToBackStack(null)
                .commit();
        });

        tvForgotPassword.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AuthResetPasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(email, password)) {
                performLogin(email, password);
            }
        });

        btnGoogleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            StatusHelper.showStatus(getContext(), "Campos vacíos", "Por favor, rellena todos los datos", true);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            StatusHelper.showStatus(getContext(), "Correo inválido", "Introduce un email correcto", true);
            return false;
        }
        return true;
    }

    private void performLogin(String email, String password) {
        toggleLoading(true);
        
        com.example.appgestionvoluntariado.Models.Request.LoginRequest request = 
            new com.example.appgestionvoluntariado.Models.Request.LoginRequest(email, password);

        APIClient.getAuthAPIService().login(request).enqueue(new Callback<com.example.appgestionvoluntariado.Models.Response.LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.example.appgestionvoluntariado.Models.Response.LoginResponse> call, 
                                   @NonNull Response<com.example.appgestionvoluntariado.Models.Response.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.appgestionvoluntariado.Models.Response.LoginResponse loginResponse = response.body();
                    
                    // Validate Status via Profile fetch
                    verifyUserStatusAndProceed(loginResponse.getToken(), loginResponse.getRol());
                } else {
                     contadorFallosLogin++;
                    if (contadorFallosLogin >= 3) {
                        tvForgotPassword.setVisibility(View.VISIBLE);
                        StatusHelper.showStatus(getContext(), "Intentos fallidos", 
                            "Parece que tienes problemas. Puedes restablecer tu contraseña si lo necesitas.", true);
                    } else {
                        // Try to parse error message
                        String errorMsg = "Credenciales incorrectas";
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                try {
                                    JSONObject jsonObject = new JSONObject(errorBody);
                                    if (jsonObject.has("error")) {
                                        errorMsg = jsonObject.getString("error");
                                    } else if (jsonObject.has("message")) {
                                        errorMsg = jsonObject.getString("message");
                                    } else {
                                        errorMsg = errorBody; // Fallback if not standard JSON
                                    }
                                } catch (Exception e) {
                                    // Not a JSON object, maybe plain text
                                    if (!errorBody.isEmpty()) errorMsg = errorBody;
                                }
                            }
                        } catch (Exception e) {}
                        StatusHelper.showStatus(getContext(), "Error de acceso", errorMsg, true);
                    }
                    toggleLoading(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.appgestionvoluntariado.Models.Response.LoginResponse> call, @NonNull Throwable t) {
                StatusHelper.showStatus(getContext(), "Error de conexión", "No se pudo conectar con el servidor.", true);
                toggleLoading(false);
            }
        });
    }

    // fetchUserProfile removed as we get role in login response

    private void processLogin(String role) {
        if (role == null) role = "";
        
        android.util.Log.d("LOGIN_DEBUG", "Rol recibido del backend: " + role);

        String normalizedRole = role.toUpperCase().trim();
        if (normalizedRole.startsWith("ROLE_")) {
            normalizedRole = normalizedRole.replace("ROLE_", "");
        }

        switch (normalizedRole) {
            case "VOLUNTARIO":
                startActivityAndFinish(VolunteerActivity.class);
                break;
            case "ORGANIZACION":
                startActivityAndFinish(OrganizationActivity.class);
                break;
            case "ADMIN":
                startActivityAndFinish(AdminActivity.class);
                break;
            default:
                StatusHelper.showStatus(getContext(), "Error", "Tipo de usuario desconocido: " + role, true);
                toggleLoading(false); 
        }
    }

    private void startActivityAndFinish(Class<?> activityClass) {
        // No need to untoggle loading here as activity finishes
        Intent intent = new Intent(getContext(), activityClass);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void toggleLoading(boolean isLoading) {
        if (isLoading) {
            pbLoading.setVisibility(View.VISIBLE);
            btnLogin.setText("");
            btnLogin.setEnabled(false);
        } else {
            pbLoading.setVisibility(View.INVISIBLE);
            btnLogin.setText(getString(R.string.logIn));
            btnLogin.setEnabled(true);
        }
    }

    private void checkAutoLogin() {
        com.example.appgestionvoluntariado.Utils.TokenManager tokenManager = com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext());
        String token = tokenManager.getToken();
        String role = tokenManager.getRole();

        if (token != null && role != null && !role.isEmpty()) {
            // Optimistic Login: Navigate immediately based on stored role
            processLogin(role);
        } else if (token != null) {
            // Fallback: Token exists but no role (legacy/error state), verify with API
            toggleLoading(true);
            
            APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ProfileResponse profile = response.body();
                        String fetchedRole = profile.getType();
                        
                        // Check status
                        boolean isBlocked = false;
                        if (profile.getData() != null && profile.getData().has("estado")) {
                            String status = profile.getData().get("estado").getAsString();
                            if (status != null && status.equalsIgnoreCase("RECHAZADO")) {
                                isBlocked = true;
                            }
                        }

                        if (isBlocked) {
                            tokenManager.clearToken();
                            toggleLoading(false);
                            StatusHelper.showStatus(getContext(), "Acceso Denegado", "Tu cuenta ha sido bloqueada o rechazada.", true);
                        } else {
                            // Save role for next time
                            tokenManager.saveRole(fetchedRole);
                            processLogin(fetchedRole);
                        } 
                    } else {
                        tokenManager.clearToken();
                        toggleLoading(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                    toggleLoading(false);
                }
            });
        }
    }

    private void signInWithGoogle() {
        // Sign out first to ensure account picker appears (but don't revoke access)
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                if (e.getStatusCode() == 12501) {
                    // User cancelled the login dialog, do nothing
                } else {
                    StatusHelper.showStatus(getContext(), "Error Google", "Fallo inicio de sesión: " + e.getStatusCode(), true);
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        toggleLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    sendTokenToBackend(tokenTask.getResult().getToken());
                                } else {
                                    StatusHelper.showStatus(getContext(), "Error", "No se pudo obtener el token de usuario.", true);
                                    toggleLoading(false);
                                }
                            });
                        }
                    } else {
                        StatusHelper.showStatus(getContext(), "Error", "Fallo autenticación con Firebase.", true);
                        toggleLoading(false);
                    }
                });
    }

    private void sendTokenToBackend(String token) {
        GoogleLoginRequest request = new GoogleLoginRequest(token);
        APIClient.getAuthAPIService().googleLogin(request).enqueue(new Callback<com.example.appgestionvoluntariado.Models.Response.LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<com.example.appgestionvoluntariado.Models.Response.LoginResponse> call,
                                   @NonNull Response<com.example.appgestionvoluntariado.Models.Response.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    com.example.appgestionvoluntariado.Models.Response.LoginResponse loginResponse = response.body();
                    com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveToken(loginResponse.getToken());
                    com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveRole(loginResponse.getRol());
                    processLogin(loginResponse.getRol());
                } else {
                    if (response.code() == 404) {
                        try {
                            String errorBody = response.errorBody().string();
                            JSONObject jsonObject = new JSONObject(errorBody);
                            String email = jsonObject.optString("email");
                            
                            // Redirect to Register
                            RegisterMenuFragment registerFragment = new RegisterMenuFragment();
                            Bundle args = new Bundle();
                            args.putString("email", email);
                            registerFragment.setArguments(args);
                            
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, registerFragment)
                                    .addToBackStack(null)
                                    .commit();
                            
                            StatusHelper.showStatus(getContext(), "No registrado", "Por favor completa tu registro.", true);
                        } catch (Exception e) {
                            StatusHelper.showStatus(getContext(), "Error", "Error al procesar respuesta.", true);
                        }
                    } else {
                        StatusHelper.showStatus(getContext(), "Error de acceso", "Error al iniciar con Google.", true);
                    }
                    toggleLoading(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.appgestionvoluntariado.Models.Response.LoginResponse> call, @NonNull Throwable t) {
                StatusHelper.showStatus(getContext(), "Error de conexión", "No se pudo conectar con el servidor.", true);
                toggleLoading(false);
            }
        });
    }


    private void verifyUserStatusAndProceed(String token, String role) {
        // Temporary save to make the call
        com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveToken(token);

        APIClient.getAuthAPIService().getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse profile = response.body();
                    boolean isBlocked = false;
                    
                    if (profile.getData() != null && profile.getData().has("estado")) {
                        String status = profile.getData().get("estado").getAsString();
                        if (status != null && status.equalsIgnoreCase("RECHAZADO")) {
                            isBlocked = true;
                        }
                    }

                    if (isBlocked) {
                        com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).clearToken();
                        StatusHelper.showStatus(getContext(), "Acceso Denegado", "Tu cuenta ha sido bloqueada o rechazada.", true);
                        toggleLoading(false);
                    } else {
                        com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveRole(role); // Final save
                        processLogin(role);
                    }
                } else {
                    // Fallback: If profile fetch fails but login worked, maybe let them in or show error?
                    // Safe approach: Let them in, backend will block specific actions if needed.
                    // But to be strict:
                     com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveRole(role); 
                     processLogin(role);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                // Network error on profile fetch -> Let them in, risk of banned user entering but they can't do much without connection
                 com.example.appgestionvoluntariado.Utils.TokenManager.getInstance(getContext()).saveRole(role); 
                 processLogin(role);
            }
        });
    }
}