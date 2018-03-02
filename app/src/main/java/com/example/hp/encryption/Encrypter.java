package com.example.hp.encryption;

import android.net.Uri;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Encrypter {

    private final static int IV_LENGTH = 16; // Default length with Default 128
    // key AES encryption
    private final static int DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE = 1024;

    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    private final static String ALGO_VIDEO_ENCRYPTOR = "AES";

    //private final static String ALGO_VIDEO_ENCRYPTOR = "AES";

    @SuppressWarnings("resource")
    public  SecretKey secretKey(String type) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        SecretKey key = KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey();
        byte[] keyData = key.getEncoded();
        SecretKey key2 = new SecretKeySpec(keyData, 0, keyData.length, ALGO_SECRET_KEY_GENERATOR);
        if(type=="decrypt")
        {
            return key2;
        }
        return key;
    }
    public void encrypt(SecretKey key, InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            Cipher c = Cipher.getInstance(ALGO_VIDEO_ENCRYPTOR);
            c.init(Cipher.ENCRYPT_MODE, key);
            out = new CipherOutputStream(out, c);
            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } finally {
            out.close();
            in.close();
        }
    }

    @SuppressWarnings("resource")
    public void decrypt(SecretKey key,  InputStream in, OutputStream out)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IOException {
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            CipherOutputStream cos = new CipherOutputStream(out, c);
            int count = 0;
            byte[] buffer = new byte[DEFAULT_READ_WRITE_BLOCK_BUFFER_SIZE];
            while ((count = in.read(buffer)) >= 0) {
                cos.write(buffer, 0, count);
                cos.flush();
            }
        } finally {
            out.close();
            in.close();
        }
    }


    public String encryptFile(Uri uri, Uri encrypted,SecretKey key) {

        File inFile = new File(uri.getPath());
        File outFile = new File(encrypted.getPath());

        try {

            encrypt(key, new FileInputStream(inFile), new FileOutputStream(outFile));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }


    public void decryptFile(Uri uri, Uri uriOut, SecretKey key) {

        File inFile = new File(uri.getPath());
        File outFile = new File(uriOut.getPath());



        try {

            byte[] iv = new byte[IV_LENGTH];
            SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR).nextBytes(iv);
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            decrypt(key,  new FileInputStream(inFile), new FileOutputStream(outFile));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}