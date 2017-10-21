package br.unb.igor.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.unb.igor.R;
import br.unb.igor.helpers.AdventureListener;

public class FragmentCriarSessao extends Fragment {

    public static String TAG = FragmentCriarSessao.class.getName();

    private ImageView btnCloseSessao;
    private FloatingActionButton btnCloseCriarSessao;
    private Button btnConfirmarSessao;
    private EditText editTituloSessao;
    private Calendar myCalendar;
    private Button btnDataSessao;
    private DatePickerDialog.OnDateSetListener date;

    private AdventureListener mListener;

    public FragmentCriarSessao() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdventureListener) {
            mListener = (AdventureListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View root = inflater.inflate(R.layout.criar_sessao, container, false);

        btnCloseCriarSessao = root.findViewById(R.id.btnCloseCriarSessao);
        btnCloseSessao = root.findViewById(R.id.btnCloseSessao);
        btnConfirmarSessao = root.findViewById(R.id.btnConfirmarSessao);
        editTituloSessao = root.findViewById(R.id.editTituloSessao);
        btnDataSessao = root.findViewById(R.id.btnDataSessao);

        btnCloseSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });

        btnCloseCriarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnConfirmarSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sessionTitle = editTituloSessao.getText().toString();
                String sessionDate = btnDataSessao.getText().toString();
                if (sessionTitle.isEmpty()) {
                    editTituloSessao.requestFocus();
                    editTituloSessao.setError("Preencha com o título!");
                    return;
                } else {
                    editTituloSessao.setError(null);
                }
                if (sessionDate.equals("00/00/00") || sessionDate.isEmpty()) {
                    editTituloSessao.requestFocus();
                    btnDataSessao.setError("Selecione uma data!");
                    return;
                } else {
                    btnDataSessao.setError(null);
                }
                //Fecha o keyboard, ao final da criação da aventura
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                editTituloSessao.setText("");
                btnDataSessao.setText("00/00/00");
                mListener.onConfirmarSessao(sessionTitle, sessionDate);
            }
        });


        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                btnDataSessao.setError(null);
                updateLabel();
            }
        };

        btnDataSessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return root;
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        btnDataSessao.setText(sdf.format(myCalendar.getTime()));
    }

}
