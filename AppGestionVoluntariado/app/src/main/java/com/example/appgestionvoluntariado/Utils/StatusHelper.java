package com.example.appgestionvoluntariado.Utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appgestionvoluntariado.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

// Logic in English as requested [cite: 2026-01-09]
public class StatusHelper {

    public static void showStatus(Context context, String title, String message, boolean isError) {
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_status_bottom_sheet, null);

        ImageView icon = view.findViewById(R.id.imgStatusIcon);
        TextView tvTitle = view.findViewById(R.id.tvStatusTitle);
        TextView tvMsg = view.findViewById(R.id.tvStatusMessage);
        Button btn = view.findViewById(R.id.btnStatusClose);

        tvTitle.setText(title);
        tvMsg.setText(message);

        if (isError) {
            icon.setImageResource(R.drawable.ic_error_outline); // Create this vector
            icon.setColorFilter(Color.parseColor("#D32F2F"));
        } else {
            icon.setImageResource(R.drawable.ic_check_circle); // Create this vector
            icon.setColorFilter(Color.parseColor("#2E7D32"));
        }

        btn.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }
    public static void showToast(Context context, String message, boolean isError) {
        if (context == null) return;
        
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);

        ImageView icon = layout.findViewById(R.id.imgToastIcon);
        TextView text = layout.findViewById(R.id.tvToastMessage);

        text.setText(message);

        if (isError) {
            icon.setImageResource(android.R.drawable.ic_delete); // Simple X icon or similar
            icon.setColorFilter(Color.parseColor("#D32F2F")); // Red
        } else {
            icon.setImageResource(R.drawable.ic_check_circle); // We used this in bottom sheet too
            icon.setColorFilter(Color.parseColor("#2E7D32")); // Green
        }

        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setDuration(android.widget.Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
