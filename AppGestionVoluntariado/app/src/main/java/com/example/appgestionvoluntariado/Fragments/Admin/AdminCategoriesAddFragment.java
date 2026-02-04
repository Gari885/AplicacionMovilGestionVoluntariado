package com.example.appgestionvoluntariado.Fragments.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.Models.Cycle;
import com.example.appgestionvoluntariado.Models.Interest;
import com.example.appgestionvoluntariado.Models.Request.InterestRequest;
import com.example.appgestionvoluntariado.Models.Ods;
import com.example.appgestionvoluntariado.Models.Skill;
import com.example.appgestionvoluntariado.Models.Need;
import com.example.appgestionvoluntariado.Models.Request.SkillRequest;
import com.example.appgestionvoluntariado.R;
import com.example.appgestionvoluntariado.Services.APIClient;
import com.example.appgestionvoluntariado.Utils.CategoryManager;
import com.example.appgestionvoluntariado.Utils.StatusHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoriesAddFragment extends Fragment {

    // UI Components from XML
    private ChipGroup cgSkills, cgInterests;
    private AutoCompleteTextView actvType;
    private TextInputEditText etName;
    private TextInputLayout tilName;
    private MaterialButton btnAdd;
    private android.widget.TextView loadingText;
    private android.widget.ImageView logoSpinner;
    private android.view.View loadingLayout;
    private android.view.animation.Animation rotateAnimation;
    private final String[] CATEGORY_TYPES = {"Habilidad", "Interés"};
    private int pendingLoads = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_categories_add, container, false);

        initViews(view);
        setupToolbar(view);
        setupDropdown();
        loadData(); // Carga inicial de elementos existentes

        btnAdd.setOnClickListener(v -> handleAddCategory());

        return view;
    }

    private void initViews(View v) {
        cgSkills = v.findViewById(R.id.chipGroupManageSkills);
        cgInterests = v.findViewById(R.id.chipGroupManageInterests);
        actvType = v.findViewById(R.id.actvCategoryType);
        etName = v.findViewById(R.id.etCategoryName);
        tilName = v.findViewById(R.id.tilCategoryName);
        btnAdd = v.findViewById(R.id.btnAddCategory);
        btnAdd = v.findViewById(R.id.btnAddCategory);
        loadingLayout = v.findViewById(R.id.layoutLoading);
        logoSpinner = v.findViewById(R.id.ivLogoSpinner);
        loadingText = v.findViewById(R.id.tvLoadingText);
        rotateAnimation = android.view.animation.AnimationUtils.loadAnimation(getContext(), R.anim.rotate_infinite);
    }

    private void setupToolbar(View v) {
        MaterialToolbar toolbar = v.findViewById(R.id.topAppBarOrg);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(view -> getParentFragmentManager().popBackStack());
        }
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, CATEGORY_TYPES);
        actvType.setAdapter(adapter);
    }

    /**
     * Carga las categorías actuales usando la lógica segura de CategoryManager [cite: 2026-01-17].
     */
    private void loadData() {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
            if(logoSpinner != null) logoSpinner.startAnimation(rotateAnimation);
        }
        pendingLoads = 2; // Skills + Interests

        new CategoryManager().fetchAllCategories(
                new CategoryManager.CategoryCallback<Ods>() { @Override public void onSuccess(List<Ods> data) {} @Override public void onError(String e) {} },
                new CategoryManager.CategoryCallback<Skill>() {
                    @Override public void onSuccess(List<Skill> data) { populateChips(cgSkills, data, "skill"); checkLoadComplete(); }
                    @Override public void onError(String e) { StatusHelper.showStatus(getContext(), "Error", "Fallo al cargar habilidades", true); checkLoadComplete(); }
                },
                new CategoryManager.CategoryCallback<Interest>() {
                    @Override public void onSuccess(List<Interest> data) { populateChips(cgInterests, data, "interest"); checkLoadComplete(); }
                    @Override public void onError(String e) { checkLoadComplete(); }
                },
                new CategoryManager.CategoryCallback<Need>() { @Override public void onSuccess(List<Need> d) {} @Override public void onError(String e) {}
                },
                new CategoryManager.CategoryCallback<Cycle>() { @Override public void onSuccess(List<Cycle> data) {} @Override public void onError(String error) {}
                }
        );
    }

    private void checkLoadComplete() {
        pendingLoads--;
        if (pendingLoads <= 0 && loadingLayout != null) {
            loadingLayout.setVisibility(View.GONE);
            if(logoSpinner != null) logoSpinner.clearAnimation();
        }
    }

    private <T> void populateChips(ChipGroup group, List<T> items, String type) {
        group.removeAllViews();
        for (T item : items) {
            String name = (item instanceof Skill) ? ((Skill) item).getName() : ((Interest) item).getName();
            int id = (item instanceof Skill) ? ((Skill) item).getId() : ((Interest) item).getId();

            Chip chip = new Chip(requireContext());
            chip.setText(name);
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> deleteCategory(id, type, chip, group));
            group.addView(chip);
        }
    }

    private void handleAddCategory() {
        String type = actvType.getText().toString();
        String name = etName.getText().toString().trim();

        if (type.isEmpty() || name.isEmpty()) {
            tilName.setError("Campo obligatorio");
            return;
        }
        tilName.setError(null);

        if (type.equals("Habilidad")) {
            saveSkill(new SkillRequest(name));
        } else {
            saveInterest(new InterestRequest(name));
        }
    }

    private void saveSkill(SkillRequest skill) {
        APIClient.getAdminService().addSkill(skill).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    etName.setText("");
                    loadData(); // Refrescar lista
                    Toast.makeText(getContext(), "Habilidad añadida", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    private void saveInterest(InterestRequest interest) {
        APIClient.getAdminService().addInterest(interest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    etName.setText("");
                    loadData();
                    Toast.makeText(getContext(), "Interés añadido", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) { }
        });
    }

    private void deleteCategory(int id, String type, Chip chip, ChipGroup group) {
        if (loadingLayout != null) {
            loadingLayout.setVisibility(View.VISIBLE);
            if(logoSpinner != null) logoSpinner.startAnimation(rotateAnimation);
        }
        Call<Void> call = type.equals("skill") ?
                APIClient.getAdminService().deleteSkill(id) :
                APIClient.getAdminService().deleteInterest(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    group.removeView(chip);
                    if (loadingLayout != null) loadingLayout.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), type.equals("skill") ? "Habilidad eliminada" : "Interés eliminado", Toast.LENGTH_SHORT).show();
                }else{
                    StatusHelper.showStatus(getContext(),"Error","No se ha podido borrar la " + type + " porque actualmente se esta usando", true);
                    if (loadingLayout != null) loadingLayout.setVisibility(View.INVISIBLE);
                }
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {
                if (loadingLayout != null) loadingLayout.setVisibility(View.INVISIBLE);

            }
        });
    }




}