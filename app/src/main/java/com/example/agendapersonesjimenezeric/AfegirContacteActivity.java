package com.example.agendapersonesjimenezeric;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class AfegirContacteActivity extends AppCompatActivity {

    private String rutaFoto;
    private ImageView imageViewFoto;

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir_contacte);

        final EditText editNom = findViewById(R.id.edit_nom);
        final EditText editTelefon = findViewById(R.id.edit_telefon);
        final EditText editEmail = findViewById(R.id.edit_email);
        imageViewFoto = findViewById(R.id.imageViewFoto);

        Button btnGuardar = findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = editNom.getText().toString().trim();
                String telefon = editTelefon.getText().toString().trim();
                String email = editEmail.getText().toString().trim();

                if (nom.isEmpty() || telefon.isEmpty() || email.isEmpty()) {
                    Toast.makeText(AfegirContacteActivity.this, "Completa tots els camps", Toast.LENGTH_SHORT).show();
                } else if (!validarTelefono(telefon)) {
                    Toast.makeText(AfegirContacteActivity.this, "El camp de telèfon no és correcte", Toast.LENGTH_SHORT).show();
                } else {
                    Contacte contacte = new Contacte(nom, telefon, email);
                    contacte.setRutaFoto(rutaFoto);
                    Intent intent = new Intent();
                    intent.putExtra("contacte", contacte);
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

    private boolean validarTelefono(String telefono) {
        try {
            int numero = Integer.parseInt(telefono);
            return numero > 0 && String.valueOf(numero).length() == 9;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    private void capturarFoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File archivoFoto = crearArchivoFoto();
                if (archivoFoto != null) {
                    rutaFoto = archivoFoto.getAbsolutePath();
                    Uri uriFoto = FileProvider.getUriForFile(this, "com.example.agendapersonesjimenezeric.fileprovider", archivoFoto);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }

    private File crearArchivoFoto() {
        String nombreArchivo = "foto_" + System.currentTimeMillis();
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoFoto = null;
        try {
            archivoFoto = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return archivoFoto;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (rutaFoto != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(rutaFoto);

                // Rotar la imagen si es necesario
                int orientation = getOrientation(rutaFoto);
                if (orientation != 0) {
                    bitmap = rotateImage(bitmap, orientation);
                }

                // Redimensionar la imagen para ajustarla al ImageView
                bitmap = resizeImage(bitmap, imageViewFoto.getWidth(), imageViewFoto.getHeight());

                imageViewFoto.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap resizeImage(Bitmap bitmap, int targetWidth, int targetHeight) {
        // Verificar si la imagen ya tiene un tamaño menor o igual al objetivo
        if (bitmap.getWidth() <= targetWidth && bitmap.getHeight() <= targetHeight) {
            return bitmap;
        }

        float scaleFactor = Math.min((float) targetWidth / bitmap.getWidth(), (float) targetHeight / bitmap.getHeight());
        int resizedWidth = Math.round(bitmap.getWidth() * scaleFactor);
        int resizedHeight = Math.round(bitmap.getHeight() * scaleFactor);

        // Verificar si el tamaño redimensionado es válido
        if (resizedWidth <= 0 || resizedHeight <= 0) {
            return bitmap;
        }

        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, true);
    }

    private int getOrientation(String imagePath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturarFoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
