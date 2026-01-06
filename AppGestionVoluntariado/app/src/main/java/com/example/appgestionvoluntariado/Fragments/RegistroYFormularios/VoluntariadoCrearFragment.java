package com.example.appgestionvoluntariado.Fragments.RegistroYFormularios;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.DatosGlobales;
import com.example.appgestionvoluntariado.Models.ActividadCreacionRequest;
import com.example.appgestionvoluntariado.Models.Organizacion;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Services.ActivitiesAPIService;
import com.example.appgestionvoluntariado.SesionGlobal;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;

public class VoluntariadoCrearFragment extends Fragment {

    // ==========================================
    // VARIABLES Y CONSTANTES
    // ==========================================

    // --- Elementos UI ---
    private ImageButton btnClose;
    private Button btnCrear, btnAnadirHabilidad, btnAnadirODS;

    // Inputs de Texto
    private TextInputEditText etNombre, etDescripcion, etFechaInicio, etFechaFin;
    private TextInputEditText etNuevaHabilidad, etNuevoODS, etMaxParticipantes;

    // Layouts (Para mostrar errores)
    private TextInputLayout tilNombre, tilOrganizacion, tilDescripcion, tilFechaInicio, tilFechaFin;
    private TextInputLayout tilSector, tilZona, tilNuevaHabilidad, tilNuevoODS, tilMaxParticipantes;

    // Desplegables
    private AutoCompleteTextView actvSector, actvZona, actvOrganizacion;

    // Chips (Etiquetas)
    private ChipGroup chipGroupDatosAnadidos;

    // --- Datos ---
    private String cifOrganizacionSeleccionada = "";
    private List<Organizacion> organizaciones;

    private List<String> ods = new ArrayList<String>();

    private List<String> habilidades = new ArrayList<String>();


    // ==========================================
    // CICLO DE VIDA
    // ==========================================

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crear_voluntariado, container, false);

        initViews(view);                // 1. Enlazar vistas
        initListeners();                // 2. Configurar botones
        cargarDatosDesplegables();      // 3. Rellenar Sectores y Zonas
        configurarLogicaSegunRol();     // 4. Configurar campo Organización según si es Admin u Org

        return view;
    }

    // ==========================================
    // INICIALIZACIÓN DE UI
    // ==========================================

    private void initViews(View view) {
        // Botones
        btnClose = view.findViewById(R.id.btnClose);
        btnCrear = view.findViewById(R.id.btnCrearVoluntariadoFinal);
        btnAnadirHabilidad = view.findViewById(R.id.btnAnadirHabilidad);
        btnAnadirODS = view.findViewById(R.id.btnAnadirODS);

        // Inputs Texto
        etNombre = view.findViewById(R.id.etNombre);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFechaInicio = view.findViewById(R.id.etFechaInicio);
        etFechaFin = view.findViewById(R.id.etFechaFin);
        etNuevaHabilidad = view.findViewById(R.id.etNuevaHabilidad);
        etNuevoODS = view.findViewById(R.id.etNuevoODS);
        etMaxParticipantes = view.findViewById(R.id.etMaxParticipantes);

        // Layouts
        tilNombre = view.findViewById(R.id.tilNombre);
        tilOrganizacion = view.findViewById(R.id.tilOrganizacion);
        tilDescripcion = view.findViewById(R.id.tilDescripcion);
        tilFechaInicio = view.findViewById(R.id.tilFechaInicio);
        tilFechaFin = view.findViewById(R.id.tilFechaFin);
        tilSector = view.findViewById(R.id.tilSector);
        tilZona = view.findViewById(R.id.tilZona);
        tilNuevaHabilidad = view.findViewById(R.id.tilNuevaHabilidad);
        tilNuevoODS = view.findViewById(R.id.tilNuevoODS);
        tilMaxParticipantes = view.findViewById(R.id.tilMaxParticipantes);

        // Desplegables
        actvSector = view.findViewById(R.id.actvSector);
        actvZona = view.findViewById(R.id.actvZona);
        actvOrganizacion = view.findViewById(R.id.actvOrganizacion);

        // Chips Container
        chipGroupDatosAnadidos = view.findViewById(R.id.chipGroupDatosAnadidos);
    }

    private void initListeners() {
        // Cerrar Fragmento
        btnClose.setOnClickListener(v -> requireActivity().onBackPressed());

        // Selectores de Fecha/Hora
        etFechaInicio.setOnClickListener(v -> mostrarSelectorFechaHora(etFechaInicio));
        etFechaFin.setOnClickListener(v -> mostrarSelectorFechaHora(etFechaFin));

        // Añadir Habilidad
        btnAnadirHabilidad.setOnClickListener(v -> agregarEtiqueta(etNuevaHabilidad, tilNuevaHabilidad, "Habilidad"));

        // Añadir ODS
        btnAnadirODS.setOnClickListener(v -> agregarEtiqueta(etNuevoODS, tilNuevoODS, "ODS"));

        // Botón Crear Final
        btnCrear.setOnClickListener(v -> {
            if (esFormularioValido()) {
                crearActividadAPI();
                Toast.makeText(getContext(), "¡Voluntariado Creado Correctamente!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    // ==========================================
    // LÓGICA DE NEGOCIO Y DATOS
    // ==========================================

    private void cargarDatosDesplegables() {
        // Sector
        ArrayAdapter<String> adapterSector = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, DatosGlobales.getInstance().LISTA_SECTORES);
        actvSector.setAdapter(adapterSector);

        // Zona
        ArrayAdapter<String> adapterZona = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, DatosGlobales.getInstance().LISTA_ZONAS);
        actvZona.setAdapter(adapterZona);
    }

    private void configurarLogicaSegunRol() {
        String rolUser = SesionGlobal.devolverRol();

        if (rolUser != null && rolUser.equalsIgnoreCase("Organizacion")) {
            // CASO 1: ES ORGANIZACIÓN
            if (SesionGlobal.getOrganizacion() != null) {
                String nombreOrg = SesionGlobal.getOrganizacion().getNombre();
                String cifOrg = SesionGlobal.getOrganizacion().getCif();

                actvOrganizacion.setText(nombreOrg);
                cifOrganizacionSeleccionada = cifOrg; // Guardamos el CIF directamente
            }

            actvOrganizacion.setEnabled(false);
            actvOrganizacion.setTextColor(getResources().getColor(R.color.black)); // Para que se lea bien aunque esté disabled

        } else if (rolUser != null && rolUser.equalsIgnoreCase("Administrador")) {
            // CASO 2: ES ADMINISTRADOR
            actvOrganizacion.setEnabled(true);
            cargarTodasLasOrganizacionesEnDropdown();
        }
    }

    private void cargarTodasLasOrganizacionesEnDropdown() {
        organizaciones = DatosGlobales.getInstance().organizaciones; // Asegúrate de que esto no sea null
        if (organizaciones == null) organizaciones = new ArrayList<>();

        List<String> nombreOrganizaciones = new ArrayList<>();
        for (Organizacion org : organizaciones){
            nombreOrganizaciones.add(org.getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nombreOrganizaciones);
        actvOrganizacion.setAdapter(adapter);

        actvOrganizacion.setOnItemClickListener((parent, view, position, id) -> {
            String nombreSeleccionado = (String) parent.getItemAtPosition(position);
            cifOrganizacionSeleccionada = buscarCifPorNombre(nombreSeleccionado);
            // Toast.makeText(getContext(), "CIF Seleccionado: " + cifOrganizacionSeleccionada, Toast.LENGTH_SHORT).show(); // Debug
        });
    }

    private String buscarCifPorNombre(String nombreSeleccionado) {
        if (organizaciones != null) {
            for (Organizacion org : organizaciones) {
                if (org.getNombre().equalsIgnoreCase(nombreSeleccionado)) {
                    return org.getCif();
                }
            }
        }
        return ""; // Retornar vacío si no se encuentra
    }

    private void crearActividadAPI() {

        String cif = cifOrganizacionSeleccionada;
        String nombreActividad = etNombre.getText().toString();
        String descripcion = etDescripcion.getText().toString();
        String fechaAppInicio = etFechaInicio.getText().toString();
        String fechaAppFin = etFechaFin.getText().toString();
        String fechaInicio = formatearFechaParaBackend(fechaAppInicio);
        String fechaFin = formatearFechaParaBackend(fechaAppFin);


        int maxParticipantes = 0;
        try {
            maxParticipantes = Integer.parseInt(etMaxParticipantes.getText().toString());
        } catch (NumberFormatException e) {
            maxParticipantes = 10; // Valor por defecto si falla algo raro
        }

        List<String> listaOds = this.ods;

        // 2. Creamos el objeto Request
        ActividadCreacionRequest request = new ActividadCreacionRequest(
                cif,
                nombreActividad,
                descripcion,
                fechaInicio,
                fechaFin,
                maxParticipantes,
                listaOds
        );

        // 3. Llamada a Retrofit
        ActivitiesAPIService service = APIClient.getActivitiesAPIService();
        service.crearActividad(request).enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {

                // --- PROTECCIÓN CONTRA CRASH ---
                if (getContext() == null || !isAdded()) {
                    return; // Si el fragmento ya no existe, paramos aquí y no hacemos nada.
                }
                // -------------------------------

                if (response.isSuccessful()) {
                    // EXITO 201: Ahora sí mostramos el mensaje y cerramos
                    Toast.makeText(getContext(), "¡Voluntariado Creado Correctamente!", Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                } else {
                    // ERROR (400, 404, 500...)
                    Toast.makeText(getContext(), "Error al crear: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // ERROR DE RED
                Toast.makeText(getContext(), "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // UTILIDADES UI (Chips, Fechas)
    // ==========================================

    private void agregarEtiqueta(TextInputEditText input, TextInputLayout layout, String tipo) {
        String texto = input.getText() != null ? input.getText().toString().trim() : "";


        if (!texto.isEmpty()) {
            anadirChipVisual(texto, tipo);
            input.setText("");
            layout.setError(null);
        } else {
            layout.setError("Escribe algo");
        }
    }

    private void anadirChipVisual(String texto, String tipo) {
        Chip chip = new Chip(requireContext());
        chip.setText(texto);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);

        // Estilos según tipo
        if (tipo.equals("ODS")) {
            ods.add(texto);
            chip.setChipBackgroundColorResource(android.R.color.holo_blue_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            habilidades.add(texto);
            chip.setChipBackgroundColorResource(android.R.color.holo_green_light);
            chip.setTextColor(getResources().getColor(android.R.color.white));
        }

        chip.setOnCloseIconClickListener(v -> {
            chipGroupDatosAnadidos.removeView(chip); // Borrar visualmente

            // 2. BORRAR DE LA LISTA DE DATOS TAMBIÉN
            if (tipo.equals("ODS")) {
                ods.remove(texto);
            }
        });
        chipGroupDatosAnadidos.addView(chip);
    }

    private void mostrarSelectorFechaHora(final TextInputEditText inputField) {
        final Calendar calendario = Calendar.getInstance();
        int anyo = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    mostrarSelectorHora(inputField, dayOfMonth, month, year);
                }, anyo, mes, dia);

        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void mostrarSelectorHora(final TextInputEditText inputField, int dia, int mes, int anio) {
        final Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePicker = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    String fechaFormateada = String.format(Locale.getDefault(),
                            "%02d/%02d/%d %02d:%02d",
                            dia, mes + 1, anio, hourOfDay, minute);
                    inputField.setText(fechaFormateada);
                }, hora, minuto, true);
        timePicker.show();
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================

    private boolean esFormularioValido() {
        boolean esValido = true;

        if (validarCampoVacio(etNombre, tilNombre)) esValido = false;
        if (validarCampoVacio(actvSector, tilSector)) esValido = false;
        if (validarCampoVacio(actvZona, tilZona)) esValido = false;
        if (validarCampoVacio(etFechaInicio, tilFechaInicio)) esValido = false;
        if (validarCampoVacio(etFechaFin, tilFechaFin)) esValido = false;
        if (validarCampoVacio(etDescripcion, tilDescripcion)) esValido = false;

        // Validación extra: CIF obligatorio
        if (cifOrganizacionSeleccionada.isEmpty()) {
            tilOrganizacion.setError("Organización no válida");
            esValido = false;
        } else {
            tilOrganizacion.setError(null);
        }
        //Validacion participantesMaximos
        String cupoStr = etMaxParticipantes.getText().toString().trim();
        if (TextUtils.isEmpty(cupoStr)) {
            tilMaxParticipantes.setError("Indica el cupo");
            esValido = false;
        } else {
            try {
                int cupo = Integer.parseInt(cupoStr);
                if (cupo <= 0) {
                    tilMaxParticipantes.setError("Debe ser mayor a 0");
                    esValido = false;
                } else {
                    tilMaxParticipantes.setError(null);
                }
            } catch (NumberFormatException e) {
                tilMaxParticipantes.setError("Número inválido");
                esValido = false;
            }
        }

        if (ods.isEmpty()) {
            tilNuevoODS.setError("Campo obligatorio");
        }

        if (habilidades.isEmpty()) {
            tilNuevaHabilidad.setError("Campo obligatorio");
        }

        return esValido;
    }

    // Método helper para no repetir código de validación
    private boolean validarCampoVacio(android.widget.TextView input, TextInputLayout layout) {
        if (TextUtils.isEmpty(input.getText())) {
            layout.setError("Campo obligatorio");
            return true; // Hay error
        } else {
            layout.setError(null);
            return false; // No hay error
        }
    }

    // ==========================================
    // METODOS AUXILIARES
    // ==========================================

    // Método auxiliar para convertir dd/MM/yyyy HH:mm -> yyyy-MM-dd'T'HH:mm:ss
    private String formatearFechaParaBackend(String fechaApp) {
        try {
            // Formato original (App)
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            // Formato destino (API ISO-8601)
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date date = inputFormat.parse(fechaApp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return fechaApp; // Si falla, mandamos la original por si acaso
        }
    }
}