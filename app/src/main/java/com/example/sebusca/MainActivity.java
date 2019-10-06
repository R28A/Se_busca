package com.example.sebusca;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import android.text.Editable;
import android.widget.EditText;
import java.lang.ref.Reference;


public class MainActivity extends AppCompatActivity {

    EditText Nombre;
    private Button Guardar;
    private Button Objetivo;
    private ImageView Captura;
    private static final int CHOOSER_IMAGES =1;
    private StorageReference Ref_storage;
    private static final String TAG = "MainActivity";
    EditText Jugador;
    //String Nombre_Jugador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Nombre = findViewById(R.id.Caja_nombre);
        Guardar = (Button) findViewById(R.id.B_guardar);
        Objetivo = (Button) findViewById(R.id.B_objetivo);
        Captura = (ImageView) findViewById(R.id.Foto);

        Ref_storage = FirebaseStorage.getInstance().getReference();
        Jugador = findViewById(R.id.Caja_nombre);

        //String Nombre_Jugador = Jugador.getText().toString().trim();


        Captura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Selecciona una imagen"), CHOOSER_IMAGES);
            }
        });

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //final StorageReference Referencia = Ref_storage.child("Nombre_del_archivo.jpg");
                String Nombre_Jugador=Jugador.getText().toString();
                final StorageReference Referencia = Ref_storage.child(Nombre_Jugador+".jpg");
                Captura.setDrawingCacheEnabled(true);
                Captura.buildDrawingCache();

                Bitmap bitmap = Captura.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);

                byte[] imagen_byte = baos.toByteArray();

                UploadTask uploadTask = Referencia.putBytes(imagen_byte);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Ocurrio un error al subir");
                        e.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(MainActivity.this, "Subida exitosa", Toast.LENGTH_SHORT).show();
                        String downUri = taskSnapshot.getUploadSessionUri().getPath();
                        Log.w(TAG, "Imagen URL:" + Jugador);

                    }
                });
            }
        });
                Objetivo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final File file;
                        try{
                            file = File.createTempFile("Buscado","jpg");
                            Ref_storage.child("Buscado.jpg").getFile(file)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                            Captura.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                             Log.e(TAG, "Tranquile, todavia no empieza");
                                             e.printStackTrace();
                                        }
                                    });

                        }catch (Exception e){
                            Log.e(TAG,"Aun no empieza la partida");
                            e.printStackTrace();
                        }
                    }
                });
            }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGES){
            Uri imageUri = data.getData();
            if (imageUri != null){
                Captura.setImageURI(imageUri);
            }
        }
    }
    /*
    public void save(View v){
        String Nombre_Jugador=Jugador.getText().toString();
    }*/

    //////////////////////////////////////////////////////////////////////////////////
    /*public TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String Nombre_Jugador = Jugador.getText().toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };*/
    //////////////////////////////////////////////////////////////////////////////////

    //public void Boton_guardar(View view){
        //Toast.makeText(this, "Datos guardaditos", Toast.LENGTH_SHORT).show();
    //}

}
