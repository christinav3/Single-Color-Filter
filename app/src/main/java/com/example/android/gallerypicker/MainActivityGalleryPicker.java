package com.example.android.gallerypicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

public class MainActivityGalleryPicker extends AppCompatActivity {

    private int requestCode = 1;
    private ImageView image;
    //private TextView url;
    //String imgDecodableString;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gallery_picker);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == this.requestCode) {
            Uri selectedImage = data.getData();
            //turn into bitmap

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            image = (ImageView) findViewById(R.id.image);
            image.setImageURI(selectedImage);

            //applying grayscale
           /* ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            image.setColorFilter(filter);*/

            image.setOnTouchListener(handleTouch);
        }
    }


    //get x, y coordinates of touch event
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            Matrix m = new Matrix();
            image.getImageMatrix().invert(m);
            float[] pts = { event.getX(), event.getY()};
            m.mapPoints(pts);
            //Log.d("TAG", "onTouch x: " + Math.floor(pts[0]) + ", y: " + Math.floor(pts[1]));


            int x = (int) pts[0];
            int y = (int) pts[1];

            if(x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
                return false;
            }

            int color = bitmap.getPixel(x,y);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("TAG", " get x and y: ( " + x + " , " + y + ") ");
                    Log.i("TAG", "red: "+ red +" blue: "+blue +", green: "+green +" " );
                    break;
            }

            return true;
        }

    };


}

