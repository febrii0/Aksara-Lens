package com.example.aksaralens;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aksaralens.ml.Model;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Menghubungkan variabel dengan elemen UI
        camera = findViewById(R.id.btn_take_picture);
        gallery = findViewById(R.id.btn_launch_galery);
        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);

        // Menetapkan fungsi untuk tombol kamera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Memeriksa izin kamera
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // Jika izin diberikan, membuka kamera
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    // Jika izin belum diberikan, meminta izin
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        // Menetapkan fungsi untuk tombol galeri
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Membuka galeri gambar
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent, 1);
            }
        });
    }

    public void classifyImage(Bitmap image){
        try {
            // Membuat instance dari model
            Model model = Model.newInstance(getApplicationContext());

            // Mengonversi gambar (Bitmap) menjadi TensorImage
            TensorImage tensorImage = TensorImage.fromBitmap(image);

            // Memproses gambar menggunakan model
            Model.Outputs outputs = model.process(tensorImage);
            // Mendapatkan hasil klasifikasi dalam bentuk daftar kategori
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            // Jika ada setidaknya satu kategori yang terdeteksi
            if (probability.size() > 0) {
                // Memilih kategori dengan probabilitas tertinggi
                Category category = probability.stream().max(Comparator.comparing(Category::getScore)).get();
                // Mencetak informasi probabilitas untuk setiap kategori
                for (int i = 0; i < probability.size(); i++) {
                    Log.i("Probability", probability.get(i).getLabel() + " : " + probability.get(i).getScore());
                }
                // Menetapkan hasil klasifikasi ke elemen UI
                result.setText(category.getLabel());
            }

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // Menangani eksepsi IOException jika terjadi kesalahan saat membuat atau memproses model
            // TODO Handle the exception
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                // Mendapatkan gambar dari hasil kamera
                Bitmap image = (Bitmap) data.getExtras().get("data");
                // Mendapatkan dimensi terkecil dari lebar dan tinggi gambar
                int dimension = Math.min(image.getWidth(), image.getHeight());
                // Membuat thumbnail dari gambar dengan dimensi yang sama
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                // Menetapkan thumbnail ke ImageView
                imageView.setImageBitmap(image);

                // Meresize gambar ke ukuran yang diinginkan dan melakukan klasifikasi
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }else{
                // Mendapatkan URI gambar dari hasil pemilihan galeri
                Uri dat = data.getData();
                Bitmap image = null;
                try {
                    // Mendapatkan gambar dari URI menggunakan resolver
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Menetapkan gambar ke ImageView
                imageView.setImageBitmap(image);

                // Meresize gambar ke ukuran yang diinginkan dan melakukan klasifikasi
                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
            }
        }
        /*Memanggil metode onActivityResult pada superclass untuk menangani
        proses onActivityResult pada kelas induk (superclass) jika diperlukan*/
        super.onActivityResult(requestCode, resultCode, data);
    }
}