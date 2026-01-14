package com.example.appgestionvoluntariado.Fragments.Volunteer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Volunteer;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.VolunteerService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolunteerProfileEditDataFragment extends Fragment {

    // Vistas del formulario
    private TextInputEditText etName, etDni, etZone, etBirthday;
    private AutoCompleteTextView spinnerCycle, spinnerExp, spinnerLanguages, spinnerCar;
    private MaterialButton btnSave, btnBack;

    // Lógica y datos
    private VolunteerService volunteerService;
    private Volunteer currentVolunteer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflamos el diseño que no tiene foto de perfil
        View view = inflater.inflate(R.layout.fragment_volunteer_edit_data, container, false);

        initViews(view);
        setupSpinners();
        setupDatePicker();

        // Cargamos los datos directamente desde la API
        fetchUserData();

        // Configuración de botones
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnSave.setOnClickListener(v -> saveData());

        return view;
    }

    private void initViews(View v) {
        etName = v.findViewById(R.id.etEditFullName);
        etDni = v.findViewById(R.id.etEditDni);
        etZone = v.findViewById(R.id.etEditZone);
        etBirthday = v.findViewById(R.id.etEditBirthday);

        spinnerCycle = v.findViewById(R.id.etEditCycle);
        spinnerExp = v.findViewById(R.id.etEditExperience);
        spinnerLanguages = v.findViewById(R.id.etEditLanguages);
        spinnerCar = v.findViewById(R.id.etEditCar);

        btnSave = v.findViewById(R.id.btnSaveFullData);
        btnBack = v.findViewById(R.id.btnBack);

        volunteerService = APIClient.getVolunteerService();
    }

    private void setupSpinners() {
        // Opciones basadas en el formulario de registro
        String[] cycles = {"ASIR", "DAM", "DAW", "Marketing", "Administración", "Finanzas"};
        String[] expOptions = {"Sin experiencia", "Menos de 1 año", "1-3 años", "Más de 3 años"};
        String[] langOptions = {"Castellano", "Euskera", "Inglés", "Francés", "Alemán"};
        String[] carOptions = {"Sí", "No"};

        spinnerCycle.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, cycles));
        spinnerExp.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, expOptions));
        spinnerLanguages.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, langOptions));
        spinnerCar.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, carOptions));
    }

    private void setupDatePicker() {
        etBirthday.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (monthOfYear + 1), year1);
                        etBirthday.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void fetchUserData() {
        // La API identifica al usuario mediante el Token de Firebase
        volunteerService.getProfile().enqueue(new Callback<Volunteer>() {
            @Override
            public void onResponse(Call<Volunteer> call, Response<Volunteer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentVolunteer = response.body();
                    fillFields(currentVolunteer);
                } else {
                    Toast.makeText(getContext(), "No se pudieron cargar tus datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Volunteer> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillFields(Volunteer v) {
        etName.setText(v.getName());
        etDni.setText(v.getDni());
        etZone.setText(v.getAddress());
        etBirthday.setText(v.getBirthday());

        // El parámetro 'false' evita que el filtro del AutoCompleteTextView se active al cargar
        spinnerCycle.setText(v.getCycle(), false);
        spinnerExp.setText(v.getExperience(), false);
        spinnerLanguages.setText(v.getLanguages(), false);
        spinnerCar.setText(v.getHasCar() ? "Sí" : "No", false);
    }

    private void saveData() {
        if (currentVolunteer == null) return;

        // Recogemos la información de los inputs
        currentVolunteer.setName(etName.getText().toString());
        currentVolunteer.setAddress(etZone.getText().toString());
        currentVolunteer.setBirthday(etBirthday.getText().toString());
        currentVolunteer.setCycle(spinnerCycle.getText().toString());
        currentVolunteer.setExperience(spinnerExp.getText().toString());
        currentVolunteer.setLanguages(spinnerLanguages.getText().toString());
        currentVolunteer.setHasCar(spinnerCar.getText().toString().equals("Sí"));

        volunteerService.updateProfile(currentVolunteer).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Información actualizada correctamente", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Fallo en la comunicación con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}