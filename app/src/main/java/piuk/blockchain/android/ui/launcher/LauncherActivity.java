package piuk.blockchain.android.ui.launcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import piuk.blockchain.android.R;
import piuk.blockchain.android.ui.auth.LandingActivity;
import piuk.blockchain.android.ui.auth.PasswordRequiredActivity;
import piuk.blockchain.android.ui.auth.PinEntryActivity;
import piuk.blockchain.android.ui.home.MainActivity;
import piuk.blockchain.android.ui.onboarding.OnboardingActivity;
import piuk.blockchain.android.ui.upgrade.UpgradeWalletActivity;
import piuk.blockchain.android.util.annotations.Thunk;

import static piuk.blockchain.android.ui.onboarding.OnboardingActivity.EXTRAS_EMAIL_ONLY;

/**
 * Created by adambennett on 09/08/2016.
 */

public class LauncherActivity extends AppCompatActivity implements LauncherViewModel.DataListener {

    private LauncherViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        viewModel = new LauncherViewModel(this);

        Handler handler = new Handler();
        handler.postDelayed(new DelayStartRunnable(this), 500);
    }

    @Thunk
    void onViewReady() {
        viewModel.onViewReady();
    }

    @Override
    public Intent getPageIntent() {
        return getIntent();
    }

    @Override
    public void onNoGuid() {
        startSingleActivity(LandingActivity.class, null);
    }

    @Override
    public void onRequestPin() {
        startSingleActivity(PinEntryActivity.class, null);
    }

    @Override
    public void onCorruptPayload() {
        new AlertDialog.Builder(this, R.style.AlertDialogStyle)
                .setTitle(R.string.app_name)
                .setMessage(getString(R.string.not_sane_error))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                    viewModel.getAppUtil().clearCredentialsAndRestart();
                    viewModel.getAppUtil().restartApp();
                })
                .show();
    }

    @Override
    public void onRequestUpgrade() {
        startActivity(new Intent(this, UpgradeWalletActivity.class));
        finish();
    }

    @Override
    public void onStartMainActivity() {
        startSingleActivity(MainActivity.class, null);
    }

    @Override
    public void onStartOnboarding(boolean emailOnly) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXTRAS_EMAIL_ONLY, emailOnly);
        startSingleActivity(OnboardingActivity.class, bundle);
    }

    @Override
    public void onReEnterPassword() {
        startSingleActivity(PasswordRequiredActivity.class, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.destroy();
    }

    private void startSingleActivity(Class clazz, @Nullable Bundle extras) {
        Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (extras != null) intent.putExtras(extras);
        startActivity(intent);
    }

    private static class DelayStartRunnable implements Runnable {

        private LauncherActivity activity;

        DelayStartRunnable(LauncherActivity activity) {
            this.activity = activity;
        }

        @Override
        public void run() {
            activity.onViewReady();
        }
    }

}
