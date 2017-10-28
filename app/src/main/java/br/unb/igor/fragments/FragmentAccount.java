package br.unb.igor.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.DB;
import br.unb.igor.model.User;

public class FragmentAccount extends Fragment {

    public static String TAG = FragmentAccount.class.getName();

    private TextView txtUserEmail;
    private TextView txtUserName;
    private EditText txtUserNameEdit;
    private LinearLayout buttonEdit;
    private ImageView usernameActionIcon;

    private boolean isEditingUsername = false;
    private User user;

    public FragmentAccount() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_conta, container, false);

        user = ((ActivityHome)getActivity()).getCurrentUser();

        txtUserEmail = root.findViewById(R.id.txtUserEmail);
        txtUserName = root.findViewById(R.id.txtUserName);
        txtUserNameEdit = root.findViewById(R.id.txtUserNameEdit);
        buttonEdit = root.findViewById(R.id.containerEditUsername);
        usernameActionIcon = root.findViewById(R.id.usernameActionIcon);

        txtUserEmail.setText(user.getEmail());
        txtUserName.setText(user.getFullName());

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleUsernameEdit(true);
            }
        });

        return root;
    }

    public boolean isEditingUsername() {
        return isEditingUsername;
    }

    public void toggleUsernameEdit(boolean save) {
        isEditingUsername = !isEditingUsername;
        if (isEditingUsername) {
            String username = txtUserName.getText().toString();
            usernameActionIcon.setImageResource(android.R.drawable.ic_menu_save);
            txtUserNameEdit.setText(username);
            txtUserNameEdit.setVisibility(View.VISIBLE);
            txtUserNameEdit.setSelection(username.length());
            txtUserNameEdit.requestFocus();
            txtUserName.setVisibility(View.INVISIBLE);
            ((ActivityHome)getActivity()).showKeyboard();
        } else {
            usernameActionIcon.setImageResource(android.R.drawable.ic_menu_edit);
            txtUserName.setVisibility(View.VISIBLE);
            txtUserNameEdit.setVisibility(View.INVISIBLE);
            if (save) {
                String newName = txtUserNameEdit.getText().toString();
                txtUserName.setText(newName);
                user.setFullName(newName);
                DB.upsertUser(user);
            }
            ((ActivityHome)getActivity()).hideKeyboard();
        }
    }
}
