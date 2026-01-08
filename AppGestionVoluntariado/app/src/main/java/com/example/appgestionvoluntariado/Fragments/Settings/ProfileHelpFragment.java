package com.example.appgestionvoluntariado.Fragments.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.appgestionvoluntariado.R;

public class ProfileHelpFragment extends Fragment {

    private Button btnBack;
    private Button btnContact;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_help, container, false);

        btnBack = view.findViewById(R.id.btnVolver);
        btnContact = view.findViewById(R.id.btnContactarEmail);

        btnBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileUserFragment())
                    .commit();
        });

        btnContact.setOnClickListener(v -> {
             getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HelpFormFragment())
                    .commit();
        });

        return view;
    }
}
