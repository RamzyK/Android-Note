package fr.freekit.androidmvvm.ui.authorization;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.freekit.androidmvvm.R;
import fr.freekit.androidmvvm.rest.ApiWebService;
import fr.freekit.androidmvvm.rest.dto.AuthentificationResponseDto;
import fr.freekit.androidmvvm.rest.dto.UserDto;
import fr.freekit.androidmvvm.ui.NoteListActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AccessActivity extends AppCompatActivity {

    private CompositeDisposable disposable = new CompositeDisposable();
    private String token;

    @OnClick(R.id.log)
    public void sign(View v) {
        if ((!email.getText().toString().equals("")) && (!password.getText().toString().equals(""))) {
            String mail = email.getText().toString();
            String pass = password.getText().toString();
            checkUser(mail, pass);
        } else {
            Toast.makeText(this, "Invalid email & password", Toast.LENGTH_SHORT).show();
        }
    }

    @BindView(R.id.mail)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_activity);

        ButterKnife.bind(this);
    }

    private void checkUser(String mail, String pwd) {

        UserDto user = new UserDto();
        user.setEmail(mail);
        user.setPwd(pwd);

        disposable.add(new ApiWebService(this).getService().getAccess(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response != null) {
                        if (response.body().getCode() == 202 && response.body() != null) {
                            SharedPreferences sharedPrefs = this.getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPrefs.edit();
                            token = response.body().getAccessToken();
                            editor.putString("token", token);
                            editor.commit();
                            //TODO: Save token to SharedPrefs
                            userChecked();
                        }else{
                            Toast.makeText(this, "Invalid user email or adress", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Invalid user, try again.", Toast.LENGTH_SHORT).show();
                    }
                }, Timber::e));
    }

    private void userChecked() {
        Intent sign_in = new Intent(this, NoteListActivity.class);
        startActivity(sign_in);
    }

}
