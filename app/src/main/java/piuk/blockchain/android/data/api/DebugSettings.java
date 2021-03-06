package piuk.blockchain.android.data.api;

import android.support.annotation.NonNull;
import android.util.Log;

import info.blockchain.wallet.api.PersistentUrls;

import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

import piuk.blockchain.android.BuildConfig;
import piuk.blockchain.android.util.PrefsUtil;

import static info.blockchain.wallet.api.PersistentUrls.Environment.DEV;
import static info.blockchain.wallet.api.PersistentUrls.Environment.STAGING;
import static info.blockchain.wallet.api.PersistentUrls.Environment.TESTNET;
import static info.blockchain.wallet.api.PersistentUrls.KEY_ENV_PROD;

@SuppressWarnings("WeakerAccess")
public class DebugSettings {

    public static final String KEY_CURRENT_ENVIRONMENT = "current_environment";

    private static final String TAG = DebugSettings.class.getSimpleName();

    private PrefsUtil prefsUtil;
    private PersistentUrls persistentUrls;

    public DebugSettings(PrefsUtil prefsUtil, PersistentUrls persistentUrls) {
        this.prefsUtil = prefsUtil;
        this.persistentUrls = persistentUrls;

        // Restore saved environment
        String storedEnv = this.prefsUtil.getValue(KEY_CURRENT_ENVIRONMENT, KEY_ENV_PROD);
        if (PersistentUrls.Environment.fromString(storedEnv) != null) {
            setEnvironment(PersistentUrls.Environment.fromString(storedEnv));
        } else {
            // Set default if empty or malformed for some reason
            setEnvironment(PersistentUrls.Environment.PRODUCTION);
        }
    }

    public boolean shouldShowDebugMenu() {
        return BuildConfig.DEBUG || BuildConfig.DOGFOOD;
    }

    @NonNull
    public PersistentUrls.Environment getCurrentEnvironment() {
        return persistentUrls.getCurrentEnvironment();
    }

    @NonNull
    public String getCurrentServerUrl() {
        return persistentUrls.getCurrentBaseServerUrl();
    }

    @NonNull
    public String getCurrentApiUrl() {
        return persistentUrls.getCurrentBaseApiUrl();
    }

    @NonNull
    public String getCurrentSFOXUrl() {
        return persistentUrls.getCurrentSFOXUrl();
    }

    @NonNull
    public String getCurrentCoinifyUrl() {
        return persistentUrls.getCurrentCoinifyUrl();
    }

    /**
     * Sets the current environment to whatever is passed to it. Clears all user data other than the
     * selected env and restarts the app
     *
     * @param environment The new {@link PersistentUrls.Environment} to switch to
     */
    public void changeEnvironment(@NonNull PersistentUrls.Environment environment) {
        setEnvironment(environment);
    }

    private void setEnvironment(PersistentUrls.Environment environment) {
        switch (environment) {
            case STAGING:
                persistentUrls.setCurrentNetworkParams(MainNetParams.get());
                persistentUrls.setCurrentApiUrl(BuildConfig.STAGING_API_SERVER);
                persistentUrls.setCurrentServerUrl(BuildConfig.STAGING_BASE_SERVER);
                persistentUrls.setCurrentWebsocketUrl(BuildConfig.STAGING_WEBSOCKET);
                persistentUrls.setCurrentEnvironment(STAGING);
                break;
            case DEV:
                persistentUrls.setCurrentNetworkParams(MainNetParams.get());
                persistentUrls.setCurrentApiUrl(BuildConfig.DEV_API_SERVER);
                persistentUrls.setCurrentServerUrl(BuildConfig.DEV_BASE_SERVER);
                persistentUrls.setCurrentWebsocketUrl(BuildConfig.DEV_WEBSOCKET);
                persistentUrls.setCurrentEnvironment(DEV);
                break;
            case TESTNET:
                persistentUrls.setCurrentNetworkParams(TestNet3Params.get());
                persistentUrls.setCurrentApiUrl(BuildConfig.TESTNET_API_SERVER);
                persistentUrls.setCurrentServerUrl(BuildConfig.TESTNET_BASE_SERVER);
                persistentUrls.setCurrentWebsocketUrl(BuildConfig.TESTNET_WEBSOCKET);
                persistentUrls.setCurrentEnvironment(TESTNET);
                break;
            default:
                persistentUrls.setProductionEnvironment();
                break;
        }

        storeEnvironment(environment);
        Log.d(TAG, "setEnvironment: " + environment.getName());
    }

    private void storeEnvironment(PersistentUrls.Environment environment) {
        prefsUtil.setValue(KEY_CURRENT_ENVIRONMENT, environment.getName());
    }

}
