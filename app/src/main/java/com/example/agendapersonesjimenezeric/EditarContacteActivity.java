package com.example.agendapersonesjimenezeric;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditarContacteActivity extends AppCompatActivity {

    private int position;
    private Contacte contacte;
    private String rutaFotoActual;
    private ImageView imageViewFoto;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacte);

        position = getIntent().getIntExtra("position", -1);
        contacte = (Contacte) getIntent().getSerializableExtra("contacte");

        final EditText editNom = findViewById(R.id.edit_nom);
        final EditText editTelefon = findViewById(R.id.edit_telefon);
        final EditText editEmail = findViewById(R.id.edit_email);
        imageViewFoto = findViewById(R.id.imageViewFoto);

        editNom.setText(contacte.getNom());
        editTelefon.setText(contacte.getTelefon());
        editEmail.setText(contacte.getEmail());

        rutaFotoActual = contacte.getRutaFoto();
        if (rutaFotoActual != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(rutaFotoActual);
            imageViewFoto.setImageBitmap(bitmap);
        } else {
            imageViewFoto.setImageResource(R.drawable.ic_foto_default);
        }

        Button btnGuardar = findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = editNom.getText().toString().trim();
                String telefon = editTelefon.getText().toString().trim();
                String email = editEmail.getText().toString().trim();

                if (nom.isEmpty() || telefon.isEmpty() || email.isEmpty()) {
                    Toast.makeText(EditarContacteActivity.this, "Completa tots els camps", Toast.LENGTH_SHORT).show();
                } else {
                    Contacte contacteActualizado = new Contacte(nom, telefon, email);
                    contacteActualizado.setRutaFoto(rutaFotoActual);
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    intent.putExtra("contacte", contacteActualizado);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        ImageButton btnCapturarFoto = findViewById(R.id.btnCapturarFoto);
        btnCapturarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturarFoto();
            }
        });
    }

    private void capturarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File archivoFoto = crearArchivoFoto();
            if (archivoFoto != null) {
                rutaFotoActual = archivoFoto.getAbsolutePath();
                Uri uriFoto = FileProvider.getUriForFile(this, "com.example.agendapersonesjimenezeric.fileprovider", archivoFoto);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                startActivityForResult(intent, 1);
            }
        }
    }

    private File crearArchivoFoto() {
        String nombreArchivo = "foto_" + System.currentTimeMillis();
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoFoto = null;
        try {
            archivoFoto = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento);
            ExifInterface exifInterface = new ExifInterface(archivoFoto.getAbsolutePath());
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return archivoFoto;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (rutaFotoActual != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(rutaFotoActual);
                try {
                    ExifInterface exifInterface = new ExifInterface(rutaFotoActual);
                    int rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    bitmap = rotarBitmap(bitmap, rotation);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageViewFoto.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap rotarBitmap(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        switch (rotation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotatedBitmap;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        guardarRutaFotoEnArchivo();
    }

    private void guardarRutaFotoEnArchivo() {
        try {
            FileOutputStream fileOutputStream = openFileOutput("ruta_foto.txt", MODE_PRIVATE);
            fileOutputStream.write(rutaFotoActual.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        rutaFotoActual = leerRutaFotoDesdeArchivo();
        if (rutaFotoActual != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(rutaFotoActual);
            imageViewFoto.setImageBitmap(bitmap);
        }
    }

    private String leerRutaFotoDesdeArchivo() {
        try {
            FileInputStream fileInputStream = openFileInput("ruta_foto.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String rutaFoto = bufferedReader.readLine();

            bufferedReader.close();
            return rutaFoto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
