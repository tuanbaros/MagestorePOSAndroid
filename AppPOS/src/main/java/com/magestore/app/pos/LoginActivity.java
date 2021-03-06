package com.magestore.app.pos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;

import android.support.v7.app.AlertDialog;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.magestore.app.lib.controller.Controller;
import com.magestore.app.lib.controller.ControllerListener;
import com.magestore.app.lib.usecase.UseCaseFactory;
import com.magestore.app.lib.usecase.UseCaseProgress;
import com.magestore.app.lib.usecase.pos.POSUserUseCase;
import com.magestore.app.pos.controller.LoginController;
import com.magestore.app.pos.ui.AbstractActivity;
import com.magestore.app.pos.ui.LoginUI;
import com.magestore.app.util.AndroidNetworkUtil;

/**
 * Màn hình login
 */
public class LoginActivity extends AbstractActivity implements LoginUI {
    /**
     * Tác vụ để login
     */
    private LoginController mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mDomainView;
    private AutoCompleteTextView mUserNameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Tải view từ template xml
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // cấu hình lại các layout control
        initControlLayout();

        // thiết lập các giá trị ban đầu
        initControlValue();
    }

    /**
     * Cấu hình lại các control layout
     */
    protected void initControlLayout() {
        // Tham chiếu đến các trường và điền các giá trị vào
        mDomainView = (AutoCompleteTextView) findViewById(R.id.domain);
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.user_name);
        mUserNameView.requestFocus();

        // Sự kiện nhấn enter ở ô password, thực hiện login luôn
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // TODO: remove while release
        mUserNameView.setText("demo");
        mPasswordView.setText("demo123");

        // Sự kiện nút login được nhấn
        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        // Tham chiếu đến form và progress
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Tự động điền giá trị cho các trường trong form
     */
    protected void initControlValue() {
        // Lấy domain là domain của lần đăng nhập cuối mà thành công
        mDomainView.setText(getSharedValue("login_activity_domain", "demo-magento2.magestore.com").trim());
    }

    /**
     * Kiểm tra giá trịc ác control trước khi login
     *
     * @return
     */
    protected boolean validControlValue() {
        // Kiểm tra kết nối mạng trước, nếu không có thì báo lỗi
        if (!AndroidNetworkUtil.isNetworkAvaiable(this)) {
            showErrorMsg(getApplicationContext().getString(R.string.err_no_network_connection));
        }

        // lấy giá trị khi nhấn login
        String domain = mDomainView.getText().toString().trim();
        String username = mUserNameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Kiểm tra đã nhập domain chưa
        if (TextUtils.isEmpty(domain)) {
            mDomainView.setError(getString(R.string.err_field_required));
            mDomainView.requestFocus();
            return false;
        }

        // Kiểm tra đã nhập user name chưa
        else if (TextUtils.isEmpty(username)) {
            mUserNameView.setError(getString(R.string.err_field_required));
            mUserNameView.requestFocus();
            ;
            return false;
        }

        // Kiểm tra đã nhập password chưa
        else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.login_error_invalid_password));
            mPasswordView.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Được gọi khi nút login được nhấn hoặc người dùng ấn enter lúc password
     */
    private void attemptLogin() {
        // Nếu đang có task thực thi thì bỏ qua việc login
        if (mAuthTask != null) {
            return;
        }

        // Khởi tạo lại các thông báo lỗi
        mDomainView.setError(null);
        mUserNameView.setError(null);
        mPasswordView.setError(null);

        // Kiểm tra giá trị các control
        boolean valid = validControlValue();

        if (valid) {
            // Hiện progress bar
            showProgress(true);

            // lấy giá trị khi nhấn login
            String domain = mDomainView.getText().toString().trim();
            String username = mUserNameView.getText().toString().trim();
            String password = mPasswordView.getText().toString().trim();

            // Bắt đầu login task
            mAuthTask = new LoginController(new LoginListener(), domain, username, password);
//            mAuthTask.execute();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) // Above Api Level 13
            {
                mAuthTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else // Below Api Level 13
            {
                mAuthTask.execute();
            }
        }
    }

    /**
     * Hiển thị progress bar lúc login
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public class LoginListener implements ControllerListener<Void, Void, Boolean> {

        @Override
        public void onPreController(Controller controller) {
            showProgress(true);
        }

        @Override
        public void onPostController(Controller controller, Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                // Đăng nhập thành công, lưu domain lại để lần sau không phải nhập
                saveSharedValue("login_activity_domain", mDomainView.getText().toString().trim());

                // Đăng nhập thành công, mở sẵn form sales
                Intent intent = new Intent(getContext(), SalesListActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Đăng nhập không thành công, báo lỗi và yêu cầu nhập lại
                mPasswordView.setError(getString(R.string.login_error_incorrect_password));
                mPasswordView.requestFocus();

            }
        }

        @Override
        public void onCancelController(Controller controller, Exception exp) {
            mAuthTask = null;
            showProgress(false);
            if (exp != null) showErrorMsg(exp);
        }

        @Override
        public void onProgressController(Controller controller, Void... progress) {

        }
    }
}

