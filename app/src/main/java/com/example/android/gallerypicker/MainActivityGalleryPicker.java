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
            //image.setImageURI(selectedImage);


            image.setOnTouchListener(handleTouch);

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
                    m.mapPoints(pts);
                    x = (int) pts[0];
                    y = (int) pts[1];
                    //filterColor = bitmap.getPixel(x,y);
                    /*Log.i("TAG", " get x and y: ( " + x + " , " + y + ") ");
                    Log.i("TAG", "red: " + red + " blue: " + blue + ", green: " + green + " ");*/
                    break;

                default:
                    return false;

            }

            /*Log.i("TAG", " get x and y: ( " + x + " , " + y + ") " + bitmap.getWidth() + "x" + bitmap.getHeight());
            if (x != 983455)
                return false;*/

            if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
                Log.i("TAG", " in if ");
                return false;
            }


                int filterColor = bitmap.getPixel(x, y);
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
                if (red < tolerance) {
                    minRed = 0;
                }
                else {
                    minRed = red - tolerance;
                }

                if(red >= (255 - tolerance)){
                    maxRed = 255;
                }
                else {
                    maxRed = red + tolerance;
                }
                //GREEN
                if (green < tolerance) {
                    minGreen = 0;
                }
                else {
                    minGreen = green - tolerance;
                }

                if(green >= 255 - tolerance) {
                    maxGreen = 255;
                }
                else {
                    maxGreen = green + tolerance;
                }
                //BLUE
                if (blue < tolerance) {
                    minBlue = 0;
                }
                else {
                    minBlue = blue - tolerance;
                }

                if(blue >= 255 - tolerance) {
                    maxBlue = 255;
                }
                else {
                    maxBlue = blue + tolerance;
                }


                //loops to run through bitmap
                for (int i = 0; i < bitmap.getWidth(); i++) {
                    for (int j = 0; j < bitmap.getHeight(); j++) {

                        int currColor = bitmap.getPixel(i, j);
                        int currRed = Color.red(currColor);
                        int currGreen = Color.green(currColor);
                        int currBlue = Color.blue(currColor);
                        //int currAlpha = Color.alpha(currColor);
                        //float gray;
                        int intGray;

                        //if current pixel is within range of color to filter, leave as is
                        if ((currRed <= maxRed && currRed >= minRed) && (currGreen <= maxGreen && currGreen >= minGreen) && (currBlue <= maxBlue && currBlue >= minBlue)) {
                            //continue;
                            mbitmap.setPixel(i, j, currColor);
                        }
                        //else apply grayscale
                        else {
                            intGray = (currRed + currGreen + currBlue) / 3;
                            intGray = Color.rgb(intGray, intGray, intGray);
                            //Log.i("TAG", " intGray is : ( " + intGray + "");
                            mbitmap.setPixel(i, j, intGray);
                        }
                    }
                }

            image.invalidate();
            return true;
        }


    };

}

