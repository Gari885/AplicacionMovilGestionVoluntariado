package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import static com.example.appgestionvoluntariado.SesionGlobal.invocarError;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.VoluntarioRegistroRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.AuthentificationAPIService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoluntarioRegistrarseFragment extends Fragment {

    // --- 1. UI ELEMENTS ---
    private Button btnRegistrar, btnVolver;

    // Campos de Texto
    private TextInputEditText etNombre, etCorreo, etDni, etPassword, etFechaNac, etZona;
    private TextInputLayout tilNombre, tilCorreo, tilDni, tilPassword, tilFechaNac, tilZona;

    // Dropdowns (Opcionales según tu criterio, pero necesarios para la UI)
    private AutoCompleteTextView actvIdiomas, actvExperiencia, actvCoche, actvCiclo;

    // Chips
    private ChipGroup chipGroupHabilidades, chipGroupIntereses, chipGroupDisponibilidad, chipGroupResumen;

    // --- 2. LOGICA Y LISTAS ---
    private FirebaseAuth mAuth;

    // Listas para guardar lo que seleccione el usuario (si selecciona algo)
    private final List<String> seleccionHabilidades = new ArrayList<>();
    private final List<String> seleccionIntereses = new ArrayList<>();
    private final List<String> seleccionDisponibilidad = new ArrayList<>();
    private final List<String> seleccionIdiomas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registrarse_voluntario, container, false);

        mAuth = FirebaseAuth.getInstance();

        initViews(view);
        cargarDatosListas(); // Carga los desplegables y chips visualmente
        initListeners();

        return view;
    }

    // --- INICIALIZACIÓN DE VISTAS ---
    private void initViews(View view) {
        btnRegistrar = view.findViewById(R.id.btnRegistrar);
        btnVolver = view.findViewById(R.id.btnVolver);

        etNombre = view.findViewById(R.id.etNombre);
        etCorreo = view.findViewById(R.id.etCorreo);
        etDni = view.findViewById(R.id.etDni);
        etPassword = view.findViewById(R.id.etPassword);
        etFechaNac = view.findViewById(R.id.etFechaNac); // Input normal
        etZona = view.findViewById(R.id.etZona);

        tilNombre = view.findViewById(R.id.tilNombre);
        tilCorreo = view.findViewById(R.id.tilCorreo);
        tilDni = view.findViewById(R.id.tilDni);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilFechaNac = view.findViewById(R.id.tilFechaNac);
        tilZona = view.findViewById(R.id.tilZona);

        actvIdiomas = view.findViewById(R.id.actvIdiomas);
        actvExperiencia = view.findViewById(R.id.actvExperiencia);
        actvCoche = view.findViewById(R.id.actvCoche);
        actvCiclo = view.findViewById(R.id.actvCiclo);

        chipGroupHabilidades = view.findViewById(R.id.chipGroupHabilidades);
        chipGroupIntereses = view.findViewById(R.id.chipGroupIntereses);
        chipGroupDisponibilidad = view.findViewById(R.id.chipGroupDisponibilidad);
        chipGroupResumen = view.findViewById(R.id.chipGroupResumen);
    }

    // --- LISTENERS (BOTONES) ---
    private void initListeners() {
        btnVolver.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnRegistrar.setOnClickListener(v -> {
            // 1. Validar SOLO los 4 obligatorios y el formato de fecha (si existe)
            if (validarCamposObligatorios()) {
                toggleLoading(true);

                String email = Objects.requireNonNull(etCorreo.getText()).toString().trim();
                String pass = Objects.requireNonNull(etPassword.getText()).toString().trim();

                registrarEnFirebase(email, pass);
            }
        });
    }

    // --- VALIDACIONES SIMPLIFICADAS ---
    private boolean validarCamposObligatorios() {
        boolean esValido = true;

        // 1. NOMBRE (OBLIGATORIO)
        if (TextUtils.isEmpty(etNombre.getText())) {
            tilNombre.setError("Obligatorio");
            esValido = false;
        } else {
            tilNombre.setError(null);
        }

        // 2. DNI (OBLIGATORIO)
        if (TextUtils.isEmpty(etDni.getText())) {
            tilDni.setError("Obligatorio");
            esValido = false;
        } else {
            tilDni.setError(null);
        }

        // 3. CORREO (OBLIGATORIO)
        String email = Objects.requireNonNull(etCorreo.getText()).toString().trim();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilCorreo.setError("Correo inválido");
            esValido = false;
        } else {
            tilCorreo.setError(null);
        }

        // 4. CONTRASEÑA (OBLIGATORIO)
        if (TextUtils.isEmpty(etPassword.getText()) || Objects.requireNonNull(etPassword.getText()).length() < 6) {
            tilPassword.setError("Mínimo 6 caracteres");
            esValido = false;
        } else {
            tilPassword.setError(null);
        }

        // 5. FECHA (OPCIONAL PERO SI SE ESCRIBE, QUE TENGA FORMATO)
        // No es obligatorio, pero si el usuario escribe algo tipo "hola", el backend puede explotar.
        // Verificamos formato DD/MM/YYYY simple con Regex.
        String fecha = Objects.requireNonNull(etFechaNac.getText()).toString().trim();
        if (!fecha.isEmpty()) {
            // Regex simple para dd/mm/yyyy
            if (!fecha.matches("^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[012])/\\d{4}$")) {
                tilFechaNac.setError("Formato incorrecto (DD/MM/YYYY)");
                esValido = false;
            } else {
                tilFechaNac.setError(null);
            }
        }

        return esValido;
    }

    // --- PROCESO DE REGISTRO ---

    // PASO 1: Auth Firebase
    private void registrarEnFirebase(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Éxito en Auth -> Vamos al Backend
                            guardarEnBackendSQL(user.getUid(), email, user);
                        }
                    } else {
                        toggleLoading(false);
                        String error = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        invocarError(getContext(), "Error Auth: " + error);
                    }
                });
    }

    // PASO 2: Backend API (Retrofit)
    private void guardarEnBackendSQL(String uid, String email, FirebaseUser firebaseUser) {
        // Recogemos datos obligatorios
        String nombre = getTextSafe(etNombre);
        String dni = getTextSafe(etDni);
        String pass = getTextSafe(etPassword);

        // --- LÓGICA DE FECHA (SOLUCIÓN AL ERROR 500) ---
        String fechaInput = getTextSafe(etFechaNac); // Viene como DD/MM/YYYY o vacío
        String fechaParaEnviar;

        if (fechaInput.isEmpty()) {
            // Si está vacío, enviamos una fecha "dummy" porque la BD la obliga
            fechaParaEnviar = "1900-01-01";
        } else {
            // Si escribió algo (ej: 25/12/2000), lo convertimos a SQL (2000-12-25)
            // porque SQL Server suele fallar con las barras /
            fechaParaEnviar = convertirFechaASQL(fechaInput);
        }

        // Datos opcionales
        String zona = getTextSafe(etZona);
        String experiencia = actvExperiencia.getText().toString();
        String coche = actvCoche.getText().toString();
        String ciclo = actvCiclo.getText().toString();

        VoluntarioRegistroRequest request = new VoluntarioRegistroRequest(
                nombre, dni, email, pass, zona, ciclo,
                fechaParaEnviar, // <--- Enviamos la fecha procesada
                experiencia, coche, seleccionIdiomas,
                seleccionHabilidades, seleccionIntereses, seleccionDisponibilidad,"Pendiente"
        );

        AuthentificationAPIService apiService = APIClient.getAuthenthificationAPIService();

        // ... El resto del código sigue igual (enqueue, onResponse...) ...
        apiService.registrarVoluntario(request).enqueue(new Callback<Void>() {
            // ... Copia tu onResponse y onFailure de antes ...
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful()) {
                    guardarRolEnFirestore(uid, email);
                    Toast.makeText(getContext(), "¡Registro completado!", Toast.LENGTH_LONG).show();
                    if (firebaseUser != null) firebaseUser.sendEmailVerification();
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.containerFragments, new LogInFragment())
                            .commit();
                } else {
                    firebaseUser.delete();
                    toggleLoading(false);
                    // Esto mostrará el error en pantalla si vuelve a fallar
                    invocarError(getContext(), "Error Servidor (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded() || getContext() == null) return;
                firebaseUser.delete();
                toggleLoading(false);
                invocarError(getContext(), "Fallo de conexión.");
            }
        });
    }

    // --- MÉTODO HELPER NUEVO ---
    private String convertirFechaASQL(String fechaDDMMYYYY) {
        // Entrada: 25/12/2000 -> Salida: 2000-12-25
        try {
            String[] partes = fechaDDMMYYYY.split("/");
            if (partes.length == 3) {
                return partes[2] + "-" + partes[1] + "-" + partes[0];
            }
        } catch (Exception e) {
            return "1900-01-01"; // Si falla el formato, enviamos default
        }
        return "1900-01-01";
    }

    // PASO 3: Firestore (Rol)
    private void guardarRolEnFirestore(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("email", email);
        datosUsuario.put("rol", "voluntario");

        db.collection("usuarios").document(uid).set(datosUsuario);
        // No ponemos listener de fallo porque no queremos bloquear el registro si esto falla
        // ya que la parte crítica (Auth y SQL) ya está hecha.
    }

    // --- CARGA DE DATOS UI (Desplegables y Chips) ---
    private void cargarDatosListas() {
        // Idiomas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, DatosGlobales.getInstance().LISTA_IDIOMAS);
        actvIdiomas.setAdapter(adapter);
        actvIdiomas.setOnItemClickListener((parent, view, position, id) -> {
            String idioma = parent.getItemAtPosition(position).toString();
            // actvIdiomas.setText(""); // Descomentar si quieres limpiar texto
            if (!seleccionIdiomas.contains(idioma)) {
                seleccionIdiomas.add(idioma);
                anadirChipResumen(idioma, "IDIOMA");
            }
        });

        // Otros Dropdowns
        fillDropdown(actvExperiencia, DatosGlobales.getInstance().LISTA_EXPERIENCIA);
        fillDropdown(actvCoche, DatosGlobales.getInstance().LISTA_COCHE);
        fillDropdown(actvCiclo, DatosGlobales.getInstance().LISTA_CICLOS);

        // Chips
        fillChips(chipGroupHabilidades, DatosGlobales.getInstance().CHIPS_HABILIDADES, seleccionHabilidades, "HABILIDADES");
        fillChips(chipGroupIntereses, DatosGlobales.getInstance().CHIPS_INTERESES, seleccionIntereses, "INTERESES");
        fillChips(chipGroupDisponibilidad, DatosGlobales.getInstance().DISPONIBILIDAD, seleccionDisponibilidad, "DISPONIBILIDAD");
    }

    // --- HELPERS UI ---
    private void fillDropdown(AutoCompleteTextView actv, String[] data) {
        actv.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, data));
    }

    private void fillChips(ChipGroup group, String[] tags, List<String> list, String type) {
        for (String tag : tags) {
            Chip chip = new Chip(requireContext());
            chip.setText(tag);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(android.R.color.white);
            chip.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    list.add(tag);
                    group.removeView(chip);
                    anadirChipResumen(tag, type);
                }
            });
            group.addView(chip);
        }
    }

    private void anadirChipResumen(String text, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        int color = android.R.color.darker_gray;
        if (type.equals("IDIOMA")) color = android.R.color.holo_purple;
        else if (type.equals("HABILIDADES")) color = android.R.color.holo_green_dark;
        else if (type.equals("INTERESES")) color = android.R.color.holo_orange_light;
        else if (type.equals("DISPONIBILIDAD")) color = android.R.color.holo_blue_dark;

        chip.setChipBackgroundColorResource(color);
        chip.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white));

        chip.setOnCloseIconClickListener(v -> {
            chipGroupResumen.removeView(chip);
            // Devolver a la lista original
            if (type.equals("IDIOMA")) seleccionIdiomas.remove(text);
            else if (type.equals("HABILIDADES")) { seleccionHabilidades.remove(text); restoreChip(chipGroupHabilidades, text, seleccionHabilidades, type); }
            else if (type.equals("INTERESES")) { seleccionIntereses.remove(text); restoreChip(chipGroupIntereses, text, seleccionIntereses, type); }
            else if (type.equals("DISPONIBILIDAD")) { seleccionDisponibilidad.remove(text); restoreChip(chipGroupDisponibilidad, text, seleccionDisponibilidad, type); }
        });
        chipGroupResumen.addView(chip);
    }

    private void restoreChip(ChipGroup group, String text, List<String> list, String type) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(android.R.color.white);
        chip.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                list.add(text);
                group.removeView(chip);
                anadirChipResumen(text, type);
            }
        });
        group.addView(chip);
    }

    private String getTextSafe(TextInputEditText et) {
        return Objects.requireNonNull(et.getText()).toString().trim();
    }

    private void toggleLoading(boolean isLoading) {
        btnRegistrar.setEnabled(!isLoading);
        btnRegistrar.setText(isLoading ? "Procesando..." : "Registrarme");
        btnVolver.setEnabled(!isLoading);
    }
}