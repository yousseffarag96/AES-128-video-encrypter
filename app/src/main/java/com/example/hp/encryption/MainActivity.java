package com.example.hp.encryption;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private EditText inpath;
    private EditText outpath;
    private byte[] skey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Encrypter encrypter=new Encrypter();
        verifyStoragePermissions(this);
        inpath=(EditText)findViewById(R.id.inpath);
        outpath=(EditText)findViewById(R.id.outpath);
        Button button = (Button) findViewById(R.id.button);
        final String path_in=inpath.getText().toString();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File folder=new File(path_in);
                    File[] fields =folder.listFiles();
                    SecretKey key=encrypter.secretKey("encrypt");
                    String satate =Environment.getExternalStorageState();
                    for (int count = 0; count < fields.length; count++)
                    {

                        String name=fields[count].getName();
                        String inPath=folder.getPath()+"/"+name;
                    File file1=new File(inPath);
                        if(Environment.MEDIA_MOUNTED.equals(satate))
                        {
                            File root=Environment.getExternalStorageDirectory();
                            File dir=new File(root.getAbsolutePath()+"/"+outpath.getText().toString());
                            if(!dir.exists())
                            {
                                dir.mkdir();
                            }
                            File file=new File(dir,name);
                            String outPath=file.getPath();

//                            String code=key.toString();
//                            code = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
//                            skey=key.getEncoded();
                            byte[] encodedKey = Base64.decode("Mk9nYNWASQeEduqNsVUvwA==", Base64.DEFAULT);
                            SecretKey skey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
                            byte[] skey_bytes=skey.getEncoded();
                            byte[]fildata=   EncryptionModule.readFile(file1);
                            byte[] encry_file=   EncryptionModule.encodeFile(encodedKey,fildata);
                            FileOutputStream fos = new FileOutputStream(outPath);
                           // System.out.println(code+"  "+name);
                            //writeToFile("  "+skey[count]+"  "+name,outPath);
                          try {
                              fos.write(encry_file);
                          }finally {
                              fos.close();
                          }


                   }
                        Toast.makeText(getApplicationContext(),"Encryption is finished",Toast.LENGTH_LONG).show();
//                     }

                } }catch (Exception e) {
                    e.printStackTrace();
                }
                //System.out.println(skey.toString());
            }
        });

    }
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
