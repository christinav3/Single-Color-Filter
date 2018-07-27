package com.example.android.gallerypicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
    Bitmap mbitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_gallery_picker);


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, requestCode);
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode) {
            Uri selectedImage = data.getData();
            //turn into bitmap

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //creating a mutable bitmap
                mbitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            image = (ImageView) findViewById(R.id.image);
            image.setImageURI(selectedImage);


            image.setOnTouchListener(handleTouch);
            image.setVisibility(View.GONE);

            //display bitmap?
            image.setImageBitmap(mbitmap);
        }
    }


    //get x, y coordinates of touch event
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {



            Matrix m = new Matrix();
            image.getImageMatrix().invert(m);
            float[] pts; // = {event.getX(), event.getY()};
            //m.mapPoints(pts);
            //Log.d("TAG", "onTouch x: " + Math.floor(pts[0]) + ", y: " + Math.floor(pts[1]));


            int x = 0; //= (int) pts[0];
            int y = 0; //= (int) pts[1];

      /*      if (x < 0 || x >= mbitmap.getWidth() || y < 0 || y >= mbitmap.getHeight()) {
                return false;
            } */


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pts  = new float[]{event.getX(), event.getY()};
                    x = (int) pts[0];
                    y = (int) pts[1];
                    m.mapPoints(pts);
                    //filterColor = bitmap.getPixel(x,y);
                    /*Log.i("TAG", " get x and y: ( " + x + " , " + y + ") ");
                    Log.i("TAG", "red: " + red + " blue: " + blue + ", green: " + green + " ");*/
                    break;
            }

            if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
                return false;
            }

                int filterColor = mbitmap.getPixel(x, y);
                int red = Color.red(filterColor);
                int green = Color.green(filterColor);
                int blue = Color.blue(filterColor);

                //calculate tolerances
                int tolerance = 20;
                int minRed;
                int maxRed;
                int minGreen;
                int maxGreen;
                int minBlue;
                int maxBlue;

                //RED
                if (red == 0 || red >= 235) {
                    minRed = red;
                    maxRed = red;
                } else if (red >= 235) {
                    minRed = 235;
                    maxRed = 255;
                } else {
                    minRed = red - tolerance;
                    maxRed = red + tolerance;
                }
                //GREEN
                if (green == 0 || green >= 235) {
                    minGreen = green;
                    maxGreen = green;
                } else if (green >= 235) {
                    minGreen = 235;
                    maxGreen = 255;
                } else {
                    minGreen = green - tolerance;
                    maxGreen = green + tolerance;
                }
                //BLUE
                if (blue == 0) {
                    minBlue = blue;
                    maxBlue = blue + tolerance;
                } else if (blue >= 235) {
                    minBlue = 235;
                    maxBlue = 255;
                } else {
                    minBlue = blue - tolerance;
                    maxBlue = blue + tolerance;
                }


                //loops to run through bitmap
                for (int i = 0; i < mbitmap.getWidth(); i++) {
                    for (int j = 0; j < mbitmap.getHeight(); j++) {

                        int currColor = mbitmap.getPixel(i, j);
                        int currRed = Color.red(currColor);
                        int currGreen = Color.green(currColor);
                        int currBlue = Color.blue(currColor);
                        //int currAlpha = Color.alpha(currColor);
                        //float gray;
                        int intGray;

                        //if current pixel is within range of color to filter, leave as is
                        if ((currRed <= maxRed && currRed >= minRed) && (currGreen <= maxGreen && currGreen >= minGreen) && (currBlue <= maxBlue && currBlue >= minBlue)) {
                           // break;
                            mbitmap.setPixel(i, j, currColor);
                        }
                        //else apply grayscale
                        else {
                            intGray = (currRed + currGreen + currBlue) / 3;
                            //intGray = (int) gray;
                            intGray = Color.rgb(intGray, intGray, intGray);
                            //Log.i("TAG", " intGray is : ( " + intGray + "");
                            mbitmap.setPixel(i, j, intGray);
                        }
                    }
                }


            return true;
        }


    };

}

