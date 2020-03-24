package com.example.systemeamdiltr;

import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.systemeamdiltr.MainActivity.tP;


public class tempDialog extends AppCompatDialogFragment {
    private EditText eT1,eT2,eT3;
    private ExampleDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view);
        if(tP==1){
            builder.setTitle("Entrer nouvelles températures ");
        }else builder.setTitle("Entrer nouvelles pressions ");

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String t1value = eT1.getText().toString();
                        String t2value = eT2.getText().toString();
                        String t3value = eT3.getText().toString();
                        if(isNumeric(t1value) && isNumeric(t2value) && isNumeric(t3value) ){
                            listener.applyTexts(t1value, t2value,t3value);
                        }else {
                            Toast.makeText(getContext(), "Veuillez remplir tous les champs avec des nombres", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        eT1 = view.findViewById(R.id.edit_T1);
        eT2 = view.findViewById(R.id.edit_T2);
        eT3 = view.findViewById(R.id.edit_T3);

        if(tP==1){
            eT1.setHint("T1");
            eT2.setHint("T2");
            eT3.setHint("T3");
        }else {
            eT1.setHint("P1");
            eT2.setHint("P2");
            eT3.setHint("P3");
        }

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (ExampleDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface ExampleDialogListener {
        void applyTexts(String t1, String t2, String t3);
    }
    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }
}