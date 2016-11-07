package com.rawalinfocom.rcontact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MCrypt mcrypt = new MCrypt();
        /* Encrypt */
        try {
            String encrypted = mcrypt.bytesToHex(mcrypt.encrypt("Hello World!"));
            Log.e("encrypted string: ", encrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class MCrypt {

        private String iv = "fedcba9876543210";//Dummy iv
        private IvParameterSpec ivspec;
        private SecretKeySpec keyspec;
        private Cipher cipher;

        private String SecretKey = "0123456789abcdef";//Dummy secretKey

        MCrypt() {
            ivspec = new IvParameterSpec(iv.getBytes());

            keyspec = new SecretKeySpec(SecretKey.getBytes(), "AES");

            try {
                cipher = Cipher.getInstance("AES/CBC/NoPadding");
            } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }

        byte[] encrypt(String text) throws Exception {
            if (text == null || text.length() == 0)
                throw new Exception("Empty string");

            byte[] encrypted = null;

            try {
                cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

                encrypted = cipher.doFinal(padString(text).getBytes());
            } catch (Exception e) {
                throw new Exception("[encrypt] " + e.getMessage());
            }

            return encrypted;
        }

        public byte[] decrypt(String code) throws Exception {
            if (code == null || code.length() == 0)
                throw new Exception("Empty string");

            byte[] decrypted = null;

            try {
                cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

                decrypted = cipher.doFinal(hexToBytes(code));
            } catch (Exception e) {
                throw new Exception("[decrypt] " + e.getMessage());
            }
            return decrypted;
        }


        String bytesToHex(byte[] data) {
            if (data == null) {
                return null;
            }

            int len = data.length;
            String str = "";
            for (byte aData : data) {
                if ((aData & 0xFF) < 16)
                    str = str + "0" + Integer.toHexString(aData & 0xFF);
                else
                    str = str + Integer.toHexString(aData & 0xFF);
            }
            return str;
        }


        byte[] hexToBytes(String str) {
            if (str == null) {
                return null;
            } else if (str.length() < 2) {
                return null;
            } else {
                int len = str.length() / 2;
                byte[] buffer = new byte[len];
                for (int i = 0; i < len; i++) {
                    buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
                }
                return buffer;
            }
        }


        private String padString(String source) {
            char paddingChar = ' ';
            int size = 16;
            int x = source.length() % size;
            int padLength = size - x;

            for (int i = 0; i < padLength; i++) {
                source += paddingChar;
            }

            return source;
        }
    }


}
