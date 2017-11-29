package br.unb.igor.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.unb.igor.R;
import br.unb.igor.activities.ActivityHome;
import br.unb.igor.helpers.CircleTransform;
import br.unb.igor.helpers.DB;
import br.unb.igor.helpers.ImageAssets;
import br.unb.igor.helpers.OnCompleteHandler;
import br.unb.igor.model.User;

public class FragmentAccount extends Fragment {

    public static String TAG = FragmentAccount.class.getName();

    private static final int PICK_IMAGE = 1;

    private TextView txtUserEmail;
    private TextView txtUserName;
    private EditText txtUserNameEdit;
    private LinearLayout buttonEdit;
    private ImageView usernameActionIcon;
    private ImageView profileImage;
    private ImageView profileImgChanger;

    private TextView txtLoading;
    private ProgressBar progressBar;
    private ConstraintLayout userInfoContainer;

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
        profileImage = root.findViewById(R.id.profileImageJogador);
        profileImgChanger = root.findViewById(R.id.profileCamera);
        userInfoContainer = root.findViewById(R.id.container);
        txtLoading = root.findViewById(R.id.labelLoading);
        progressBar = root.findViewById(R.id.progressBar);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user.hasBeenFetchedFromDB) {
                    toggleUsernameEdit(true);
                }
            }
        });

        if (user.isCurrentUserAndLoggedInWithFacebookOrGoogle()) {
            profileImgChanger.setVisibility(View.GONE);
        } else {
            profileImgChanger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.hasBeenFetchedFromDB) {
                        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        getIntent.setType("image/*");

                        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickIntent.setType("image/*");

                        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                        startActivityForResult(chooserIntent, PICK_IMAGE);
                    }
                }
            });
        }

        onUserChanged();

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            final Bitmap image = ImageAssets.getBitmapFromUri(getActivity(), data.getData(), 512);
            DB.updateUserPhoto(user, image, new OnCompleteHandler(new OnCompleteHandler.OnCompleteCallback() {
                @Override
                public void onComplete(boolean cancelled, Object extra, int step) {
                    image.recycle();
                    if (cancelled || extra == null || !(extra instanceof Uri)) {
                        Toast toast = Toast.makeText(getActivity(),
                                R.string.msg_failed_to_upload_picture, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 0, 0);
                        toast.show();
                    } else {
                        Uri photoUrl = (Uri) extra;
                        user.setProfilePictureUrl(photoUrl.toString());
                        DB.upsertUser(user);
                        Picasso
                            .with(profileImage.getContext())
                            .load(photoUrl)
                            .transform(new CircleTransform())
                            .into(profileImage);
                    }
                }
            }));
        }
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

    public void onUserChanged() {

        if (user.hasBeenFetchedFromDB) {
            progressBar.setVisibility(View.GONE);
            txtLoading.setVisibility(View.GONE);
            userInfoContainer.setVisibility(View.VISIBLE);

            Picasso
                    .with(profileImage.getContext())
                    .load(user.getProfilePictureUrl())
                    .transform(new CircleTransform())
                    .into(profileImage);

            txtUserEmail.setText(user.getEmail());
            txtUserName.setText(user.getFullName());
        } else {
            progressBar.setVisibility(View.VISIBLE);
            txtLoading.setVisibility(View.VISIBLE);
            userInfoContainer.setVisibility(View.GONE);
        }
    }
}
