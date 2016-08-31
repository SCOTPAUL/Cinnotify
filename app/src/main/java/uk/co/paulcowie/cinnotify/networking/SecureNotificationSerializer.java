package uk.co.paulcowie.cinnotify.networking;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * Same as {@link NotificationSerializer}, but encrypts the JSON data.
 * This causes some other minor format changes detailed below.
 *
 * The transmission will have 4 bytes used to store the length of the rest
 * of the transmission, followed by a 16 byte AES IV, followed by the encrypted JSON.
 * @see NotificationSerializer
 */
public class SecureNotificationSerializer extends NotificationSerializer {
    private static final String TAG = SecureNotificationSerializer.class.getName();

    /**
     * Caches the result of {@link #encryptionEnabled()} to make future lookups inexpensive
     */
    private Boolean cryptoEnabledCache;


    private static byte[] salt = "l1pWK8SAkDR4UtE1uk9pPIc1qEMlxq7pwIJaoV1W4dspCYlnbs\0".getBytes(StandardCharsets.UTF_8);

    public SecureNotificationSerializer(String senderPackage, Context context, Notification notification) {
        super(senderPackage, context, notification);
    }

    /**
     * @return true if encryption is enabled and the provided password is not empty or null
     */
    public boolean encryptionEnabled(){
        if(cryptoEnabledCache != null){
            return cryptoEnabledCache;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = preferences.getBoolean("pref_encryption_enabled", false);
        String password = preferences.getString("pref_encryption_password", "");

        cryptoEnabledCache = enabled && !TextUtils.isEmpty(password);
        return cryptoEnabledCache;
    }

    /**
     * Generates a secret key from the password specified in the default {@link SharedPreferences}
     * @return the generated key
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private Key getSecretKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        char[] password = preferences.getString("pref_encryption_password", "").toCharArray();

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec ks = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey s = secretKeyFactory.generateSecret(ks);
        return new SecretKeySpec(s.getEncoded(),"AES");
    }

    /**
     * Creates a serialized transmission in the required form.
     *
     * @return encrypted byte array in the format shown in {@link NotificationSerializer}, or null if the encryption failed.
     * @see NotificationSerializer
     */
    public byte[] getSerializedTransmission(){
        byte[] transmissionBody;
        transmissionBody = getTransmissionFromNotification().getBytes(StandardCharsets.UTF_8);

        try {
            transmissionBody = encrypt(getSecretKey(), transmissionBody);
        } catch (GeneralSecurityException | IOException e) {
            return null;
        }

        return getSerializedTransmission(transmissionBody);
    }

    private static byte[] encrypt(Key key, byte[] unencrypted) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        os.write(cipher.getIV());
        os.write(cipher.doFinal(unencrypted));

        return os.toByteArray();
    }
}
