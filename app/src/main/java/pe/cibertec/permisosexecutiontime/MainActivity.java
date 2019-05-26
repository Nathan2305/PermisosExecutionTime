package pe.cibertec.permisosexecutiontime;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button btnCamera;
    ImageView imvFoto;
    public final int REQUEST_CAMERA=1;
    public final int REQUEST_TAKE_PICTURE=2;
    String currentPathImage;  //ruta absoluta de la imagen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamera=findViewById(R.id.btnCamera);
        imvFoto=findViewById(R.id.imvFoto);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }
    private void takePicture() {
        Intent takeCameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //validar que camara este disponible
        if (takeCameraIntent.resolveActivity(getPackageManager())!=null){
            //Verificar que se disponga de permiso
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                requestCameraPermission();
            }else{
                //obtener una imagen que es el resultado de llamar esa activity
                File photoFile=null;
                try {
                    photoFile=createImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startActivityForResult(takeCameraIntent,REQUEST_TAKE_PICTURE);
            }
        }
    }

    private File createImage() throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imgFileName="JPG_"+timeStamp;
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(imgFileName,".jpg",storageDir);
        currentPathImage=image.getAbsolutePath();
        return image;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CAMERA){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                takePicture();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_TAKE_PICTURE && resultCode== RESULT_OK){
            //la foto viaja con atributo "data"
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            imvFoto.setImageBitmap(bitmap);
        }

    }
}
