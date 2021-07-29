package com.example.pose_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.mlkit.vision.common.InputImage;

import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Uri uriImage;
    ImageView img;
    TextView txt;
    String globalMsg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.iv_foto);
        txt = (TextView) findViewById(R.id.ml_respuesta);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //verifica si se ha seleccionado una imagen
            if (requestCode == 200) {
                //obtener imageUri
                uriImage = data.getData();
                if (uriImage != null) {
                    try {
                        //ubicar imagen en contenedor ImageView
                        img.setImageURI(uriImage);
                        searchFace(uriImage);
                    } catch (Exception ex) {
                        Log.e("", ex.toString());
                    }
                }
            } else if (resultCode == 300) {

            }
        }
    }

    public void selectImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("Image/*");
        startActivityForResult(gallery, 200);
    }

    public void searchFace(Uri uri) throws IOException {
        InputImage image;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);
            detect(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detect (InputImage image) {
        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        globalMsg = "";
                        for (ImageLabel label : labels) {
                            int index = label.getIndex();
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            Log.e("rico",index + " - Objeto:" + text + " -> Confianza:" + confidence);
                            globalMsg += index + " - Objeto:" + text + " -> Confianza:" + confidence +"\n";
                        }
                        txt.setText(globalMsg);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //Alerts.MessageToast(MainActivity.this, "MLError: " + e.getMessage());
                    }
                });
    }



}