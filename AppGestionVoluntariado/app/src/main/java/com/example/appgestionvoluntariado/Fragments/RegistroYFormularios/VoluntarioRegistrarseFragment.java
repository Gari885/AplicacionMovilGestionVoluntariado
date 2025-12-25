package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import static com.example.appgestionvoluntariado.SesionGlobal.invocarError;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.appgestionvoluntariado.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class VoluntarioRegistrarseFragment extends Fragment {

    private Button registrado;

    private FirebaseAuth mAuth;

    private Button volver;

    private TextInputEditText nombreInput, emailInput, contraseñaInput, zonaInput;

    private TextInputLayout nombreLayout, emailLayout, contraseñaLayout, zonaLayout;

    private ArrayList<String> dias, horario;

    private Spinner dia, hora;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_voluntario, container, false);

        registrado = view.findViewById(R.id.btnRegistrar);
        nombreInput = view.findViewById(R.id.etNombre);
        zonaInput = view.findViewById(R.id.etZona);
        emailInput = view.findViewById(R.id.etCorreo);
        contraseñaInput = view.findViewById(R.id.etPassword);
        nombreLayout = view.findViewById(R.id.tilNombre);
        emailLayout = view.findViewById(R.id.tilCorreo);
        contraseñaLayout = view.findViewById(R.id.tilPassword);
        zonaLayout = view.findViewById(R.id.tilZona);
        mAuth = FirebaseAuth.getInstance();
        dia = view.findViewById(R.id.spinnerDia);
        hora = view.findViewById(R.id.spinnerHorario);


        // A. Crear la lista de datos
        String[] opcionesDias = {"Selecciona un día", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        
        ArrayAdapter<String> adapterDias = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opcionesDias);

        // C. Definir cómo se ve al abrirlo (Dropdown)
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // D. Asignar el adaptador al Spinner
        // Asegúrate de haber hecho el findViewById previamente o usar binding
        dia.setAdapter(adapterDias);


        // --- 2. CONFIGURAR SPINNER DE HORARIO ---
        String[] opcionesHoras = {"Selecciona horario", "Mañanas (8:00 - 14:00)", "Tardes (15:00 - 20:00)"};

        ArrayAdapter<String> adapterHoras = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, opcionesHoras);
        adapterHoras.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        hora.setAdapter(adapterHoras);

        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (esFormularioValido()){
                    registrarVoluntario(v,emailInput.getText().toString(),contraseñaInput.getText().toString(), "voluntario");

                }

            }
        });

        volver = view.findViewById(R.id.btnVolver);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.containerFragments, new MenuRegistrarseFragment())
                        .addToBackStack(null) // <--- IMPORTANTE: Para que el botón 'Atrás' del móvil te devuelva al menú
                        .commit();
            }
        });

        return view;
    }

    private void registrarVoluntario(View v,String email, String password, String tipoUsuario) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // El usuario se creó en Auth, ahora guardamos sus datos extra en Firestore
                    FirebaseUser user = mAuth.getCurrentUser();
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // El correo se envió correctamente
                                invocarError(getContext(), "Cuenta creada. Por favor, revisa tu correo para verificar tu identidad antes de entrar.");
                                guardarDatosUsuario(v,user.getUid(), email, tipoUsuario);
                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.containerFragments, new LogInFragment())
                                        .commit();
                            } else {
                                invocarError(getContext(), "No se pudo enviar el correo de verificación.");
                            }
                        }
                    });

                } else {
                    String mensajeError = "";

                    try {
                        // Lanzamos la excepción que ocurrió para atraparla abajo
                        throw task.getException();
                    }
                    // 1. ERROR: EL CORREO YA EXISTE
                    catch (FirebaseAuthUserCollisionException e) {
                        mensajeError = "Ese correo ya está registrado en otra cuenta.";
                    }
                    // 4. ERROR: SIN INTERNET
                    catch (FirebaseNetworkException e) {
                        mensajeError = "No tienes conexión a internet.";
                    }
                    // 5. CUALQUIER OTRO ERROR
                    catch (Exception e) {
                        mensajeError = "Error desconocido: " + e.getMessage();
                    }

                    // Finalmente mostramos tu popup con el mensaje personalizado
                    invocarError(getContext(), mensajeError);
                }
            }
        });
    }

    private void guardarDatosUsuario(View v,String uid, String email, String tipoUsuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear un objeto con los datos (usamos un Map simple)
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("email", email);
        datosUsuario.put("rol", "voluntario"); // Aquí guardamos "admin" o "voluntario"

        // Guardar en la colección "usuarios", usando el UID como nombre del documento
        db.collection("usuarios").document(uid)
                .set(datosUsuario)
                .addOnFailureListener(e -> {
                    Toast.makeText(volver.getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean esFormularioValido() {
        boolean esValido = true;

        // --- 1. VALIDAR NOMBRE ---
        String nombre = this.nombreInput.getText().toString().trim();
        if (nombre.isEmpty()) {
            nombreLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!nombre.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            nombreLayout.setError("El nombre solo puede contener letras");
            esValido = false;
        } else {
            nombreLayout.setError(null); // Borra el error si ya lo arregló
        }

        // --- 2. VALIDAR EMAIL ---
        String email = this.emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Introduce un correo válido");
            esValido = false;
        } else {
            emailLayout.setError(null);
        }

        // --- 3. VALIDAR CONTRASEÑA ---
        String contraseña = this.contraseñaInput.getText().toString().trim();
        if (contraseña.isEmpty()) {
            contraseñaLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!comprobarContra(contraseña)) {
            contraseñaLayout.setError("La contraseña debe tener minimo 10 caracteres, una mayuscula, una minuscula, un numero y un caracter especial");
            esValido = false;
        } else {
            contraseñaLayout.setError(null);
        }

        // --- 5. VALIDAR ZONA ---
        if (this.zonaInput.getText().toString().trim().isEmpty()) {
            zonaLayout.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else {
            zonaLayout.setError(null);
        }

        //TO DO:
        //AÑADIR VALIDACIONES QUE FALTAN, NECESITAMOS CONECTAR CON API PARA SACAR DATOS

        return esValido;
    }

    private boolean comprobarContra(String contra) {
        Boolean minuscula = false;
        Boolean mayuscula = false;
        Boolean caractererEspecial = false;
        Boolean numero = false;
        Boolean longMinima = false;
        int longitud = 0;

        for (char c : contra.toCharArray()) {
            if (Character.isLowerCase(c)) {
                minuscula = true;
            } else if (Character.isUpperCase(c)) {
                mayuscula = true;
            } else if (Character.isDigit(c)) {
                numero = true;
            } else {
                caractererEspecial = true;
            }
            longitud++;

        }
        if (longitud >= 10) longMinima = true;

        return minuscula && mayuscula && caractererEspecial && numero && longMinima;
    }

}