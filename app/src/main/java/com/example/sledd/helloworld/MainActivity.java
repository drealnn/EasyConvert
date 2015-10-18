package com.example.sledd.helloworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.sledd.helloworld";
    private static final String TAG = "MainActivity";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;

    private static LinkedList<Uri> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null )
        {
            Uri uri = data.getData();

            if (imageList == null)
                imageList = new LinkedList<>();

            imageList.add(uri);
            Log.i(TAG, "This is the Image name: " + uri.getLastPathSegment());
            Log.i(TAG, "This the length of the list: " + imageList.size());




            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //Log.i(TAG, "The image name is: " + bitmap.toString() );
                ImageView imageview = (ImageView) findViewById(R.id.imageView);
                imageview.setImageBitmap(bitmap);




            }catch(IOException e)
            {
                e.printStackTrace();
            }


        }
    }


    public void sendMessage(View view)
    {

        /*
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
        */

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select a Picture!"), PICK_IMAGE_REQUEST_CODE);

    }

    public void createPdf(View view) throws  DocumentException, java.io.IOException
    {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "EasyConvert"); // check this warning, may be important for diff API levels

        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
            Log.i(TAG, "Folder successfully created");
        }

        if (imageList != null)
        {
            Date date = new Date();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

            File myPDF = new File(pdfFolder + "/" + timeStamp + ".pdf");

            OutputStream output = new FileOutputStream(myPDF);

            Document document = new Document();
            PdfWriter.getInstance(document, output);

            document.open();
            //document.add(new Paragraph("~~~~Hello World!!~~~~"));
            for (int i = 0; i < imageList.size(); i++)
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageList.get(i));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(byteArray);
                Log.i(TAG, "We got to here!");
                document.add(img);



            }
            imageList = null;
            document.close();


        }
    }
}
