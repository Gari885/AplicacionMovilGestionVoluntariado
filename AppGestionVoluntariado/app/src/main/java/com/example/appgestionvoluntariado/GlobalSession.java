package com.example.appgestionvoluntariado;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.appgestionvoluntariado.Models.Organization;
import com.example.appgestionvoluntariado.Models.Volunteer;

public class GlobalSession {

    public enum UserType {
        VOLUNTEER,
        ORGANIZATION,
        ADMINISTRATOR
    }

    private static UserType currentType;
    private static Volunteer currentVolunteer;
    private static Organization currentOrganization;
    private static String password;

    public static void loginVolunteer(Volunteer volunteer){
        currentVolunteer = volunteer;
        currentType = UserType.VOLUNTEER;
    }

    public static void loginOrganization(Organization organization){
        currentOrganization = organization;
        currentType = UserType.ORGANIZATION;
    }

    public static void loginAdmin(){
        currentType = UserType.ADMINISTRATOR;
    }

    public static void showError(Context context, String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.dialog_error_message, null);

        TextView msnError = popupView.findViewById(R.id.tvErrorMessage);
        LinearLayout closeBtn = popupView.findViewById(R.id.btnClosePopup);

        msnError.setText(error);
        builder.setView(popupView);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        closeBtn.setOnClickListener(x -> dialog.dismiss());
        dialog.show();
    }

    public static boolean isOrganization(){
        return currentType == UserType.ORGANIZATION;
    }
    
    public static boolean isAdmin(){
        return currentType == UserType.ADMINISTRATOR;
    }

    public static void setPassword(String pass){
        password = pass;
    }

    public static void logout() {
        currentType = null;
        currentVolunteer = null;
        currentOrganization = null;
        password = null;
    }

    public static String getRole() {
        String type = "";
        if (currentType != null) {
             switch(currentType) {
                case VOLUNTEER:
                    type = "Voluntario";
                    break;
                case ORGANIZATION:
                    type = "Organizacion"; // Keeping Spanish Strings for compatibility if needed
                    break;
                case ADMINISTRATOR:
                    type = "Administrador"; // Keeping Spanish Strings for compatibility if needed
                    break;
            }
        }
        return type;
    }

    public static Volunteer getVolunteer() {
        return currentVolunteer;
    }

    public static Organization getOrganization(){
        return currentOrganization;
    }
}
