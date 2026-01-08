package com.example.appgestionvoluntariado.Fragments.VistaVoluntario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appgestionvoluntariado.Adapters.AdaptadorVoluntariado;
import com.example.appgestionvoluntariado.R;

public class MisVoluntariadosVolFragment extends Fragment {

    private RecyclerView recyclerView;

    private AdaptadorVoluntariado adaptadorVoluntariado;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniciarVistas();
    }

    private void iniciarVistas() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mis_voluntariados_vol, container, false);

        return view;
    }
}