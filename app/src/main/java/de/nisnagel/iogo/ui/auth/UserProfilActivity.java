/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.ui.base.BaseActivity;

public class UserProfilActivity extends BaseActivity {

    @BindView(R.id.old_email)
    EditText oldEmail;
    @BindView(R.id.new_email)
    EditText newEmail;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.newPassword)
    EditText newPassword;
    @BindView(R.id.changeEmail)
    Button changeEmail;
    @BindView(R.id.changePass)
    Button changePassword;
    @BindView(R.id.send)
    Button sendEmail;
    @BindView(R.id.remove)
    Button remove;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofil);
        ButterKnife.bind(this);

        toolbar.setTitle(R.string.settings_title_activity_settings_account);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        authListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(UserProfilActivity.this, LoginActivity.class));
                mAuth.removeAuthStateListener(authListener);

                finish();
            }
        };

        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.change_email_button)
    public void onClickChangeEmailButton() {
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.VISIBLE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.VISIBLE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.changeEmail)
    public void onClickChangeEmail() {
        progressBar.setVisibility(View.VISIBLE);
        if (mUser != null && !newEmail.getText().toString().trim().equals("")) {
            mUser.updateEmail(newEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfilActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                signOut();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfilActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else if (newEmail.getText().toString().trim().equals("")) {
            newEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.change_password_button)
    public void onClickChangePasswordButton() {
        oldEmail.setVisibility(View.GONE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.VISIBLE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.VISIBLE);
        sendEmail.setVisibility(View.GONE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.changePass)
    public void onClickChangePassword() {
        progressBar.setVisibility(View.VISIBLE);
        if (mUser != null && !newPassword.getText().toString().trim().equals("")) {
            if (newPassword.getText().toString().trim().length() < 6) {
                newPassword.setError("Password too short, enter minimum 6 characters");
                progressBar.setVisibility(View.GONE);
            } else {
                mUser.updatePassword(newPassword.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(UserProfilActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                    signOut();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(UserProfilActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else if (newPassword.getText().toString().trim().equals("")) {
            newPassword.setError("Enter password");
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.sending_pass_reset_button)
    public void onClickSendResetEmail() {
        oldEmail.setVisibility(View.VISIBLE);
        newEmail.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        changeEmail.setVisibility(View.GONE);
        changePassword.setVisibility(View.GONE);
        sendEmail.setVisibility(View.VISIBLE);
        remove.setVisibility(View.GONE);
    }

    @OnClick(R.id.send)
    public void onClickSendEmail() {
        progressBar.setVisibility(View.VISIBLE);
        if (!oldEmail.getText().toString().trim().equals("")) {
            mAuth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfilActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfilActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            oldEmail.setError("Enter email");
            progressBar.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.remove_user_button)
    public void onClickRemoveUser() {
        progressBar.setVisibility(View.VISIBLE);
        if (mUser != null) {
            mUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfilActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserProfilActivity.this, SignupActivity.class));
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfilActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.sign_out)
    public void onClickSignOut() {
        signOut();
        finish();
    }

    public void signOut() {
        mAuth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            mAuth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}