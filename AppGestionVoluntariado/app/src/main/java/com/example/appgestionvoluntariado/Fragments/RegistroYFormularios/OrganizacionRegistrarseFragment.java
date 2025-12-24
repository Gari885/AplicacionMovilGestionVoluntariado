package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import static com.example.appgestionvoluntariado.SesionGlobal.invocarError;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appgestionvoluntariado.R;
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

import java.util.HashMap;
import java.util.Map;

public class OrganizacionRegistrarseFragment extends Fragment {

    private Button registrado;

    private Button volver;
    private Boolean registroValido;

    private FirebaseAuth mAuth;

    // Declaras los layouts (contenedores), no solo los editTexts
    TextInputLayout tilNombre, tilCorreo, tilSector, tilZona, tilDescripcion, tilContraseña;
    TextInputEditText nombre, email, sector, zona, descripcion, contraseña;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registrarse_organizacion, container, false);

        nombre = view.findViewById(R.id.etNombre);
        email = view.findViewById(R.id.etCorreo);
        sector = view.findViewById(R.id.etSector);
        zona = view.findViewById(R.id.etZona);
        descripcion = view.findViewById(R.id.etDescripcion);
        mAuth = FirebaseAuth.getInstance();
        contraseña = view.findViewById(R.id.etPassword);
        tilNombre = view.findViewById(R.id.tilNombre);
        tilCorreo = view.findViewById(R.id.tilCorreo);
        tilSector = view.findViewById(R.id.tilSector);
        tilZona = view.findViewById(R.id.tilZona);
        tilDescripcion = view.findViewById(R.id.tilDescripcion);
        tilContraseña = view.findViewById(R.id.tilPassword);
        registroValido = false;

        registrado = view.findViewById(R.id.btnRegistrar);
        registrado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (esFormularioValido()){
                    registrarOrganizacion(v,email.getText().toString(),contraseña.getText().toString(), "organizacion");
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

    private boolean esFormularioValido() {
        boolean esValido = true;

        // --- 1. VALIDAR NOMBRE ---
        String nombre = this.nombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            tilNombre.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!nombre.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            tilNombre.setError("El nombre solo puede contener letras");
            esValido = false;
        } else {
            tilNombre.setError(null); // Borra el error si ya lo arregló
        }

        // --- 2. VALIDAR EMAIL ---
        String email = this.email.getText().toString().trim();
        if (email.isEmpty()) {
            tilCorreo.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilCorreo.setError("Introduce un correo válido");
            esValido = false;
        } else {
            tilCorreo.setError(null);
        }

        // --- 3. VALIDAR CONTRASEÑA ---
        String contraseña = this.contraseña.getText().toString().trim();
        if (contraseña.isEmpty()) {
            tilContraseña.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!comprobarContra(contraseña)) {
            tilContraseña.setError("La contraseña debe tener minimo 10 caracteres, una mayuscula, una minuscula, un numero y un caracter especial");
            esValido = false;
        } else {
            tilCorreo.setError(null);
        }

        // --- 4. VALIDAR SECTOR ---
        String sector = this.sector.getText().toString().trim();
        if (sector.isEmpty()) {
            tilSector.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else if (!sector.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚ\\s]+$")) {
            tilSector.setError("Solo letras permitidas");
            esValido = false;
        } else {
            tilSector.setError(null);
        }

        // --- 5. VALIDAR ZONA ---
        if (this.zona.getText().toString().trim().isEmpty()) {
            tilZona.setError("No puedes dejar el campo vacío");
            esValido = false;
        } else {
            tilZona.setError(null);
        }

        // --- 6. VALIDAR DESCRIPCIÓN ---
        if (this.descripcion.getText().toString().trim().isEmpty()) {
            tilDescripcion.setError("Cuéntanos algo sobre la organización");
            esValido = false;
        } else {
            tilDescripcion.setError(null);
        }

        return esValido;
    }

    private void registrarOrganizacion(View v,String email, String password, String tipoUsuario) {
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
        datosUsuario.put("rol", tipoUsuario); // Aquí guardamos "admin" o "voluntario"

        // Guardar en la colección "usuarios", usando el UID como nombre del documento
        db.collection("usuarios").document(uid)
                .set(datosUsuario)
                .addOnFailureListener(e -> {
                    Toast.makeText(volver.getContext(), "Error al guardar datos", Toast.LENGTH_SHORT).show();
                });
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