package com.example.cu1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Camara extends AppCompatActivity implements LocationListener, SensorEventListener {


    //Srings de respuestas
    public static final String RESPUESTA_FOTO = "FOTO";
    public static final String RESPUESTA_BRUJULA = "BRUJULA";
    public static final String RESPUESTA_COORDENADAS = "GPS";


    //XML
    private ImageButton Guardar;
    private TextView BX, BY, BZ, LatV, LongV;
    private TextureView textureView;

    //LOG
    private static final String TAG = "MyCameraApi";

    //Texture
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            //Abrimos la camara
            abrirCamara();
        }

        @Override
        public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

        }
    };

    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };


    //CAMARA
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    //GPS
    LocationManager locationManager;
    private double Latitud = 0;
    private double Longitud = 0;

    //Sensor
    private SensorManager sensorManager;
    private float Xvalue = 0;
    private float YValue = 0;
    private float Zvalue = 0;
    //Handlers
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    //_____________________________________COMIENZO_________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        Guardar = findViewById(R.id.Guardarboton);

        BX = findViewById(R.id.BX);
        BY = findViewById(R.id.BY);
        BZ = findViewById(R.id.BZ);

        LatV = findViewById(R.id.LatV);
        LongV = findViewById(R.id.LongV);

        textureView = findViewById(R.id.textureView);

        Configurar();
    }

    private void Configurar() {
        //SENSOR
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //GPS
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Tenemos los permisos
            UpdateGPS();
        }

        //Con esto tan solo unimos el listener a la textura
        textureView.setSurfaceTextureListener(textureListener);

        //Y aqui establecemos el uso del boton
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Debe de capturar la imagen, sacarla como bitmap y ponerla en el imageView
                ///corresponde a la funcion takepicture
                if (null == cameraDevice) {
                    Log.e(TAG, "cameraDevice is null");
                    return;
                }
                CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

                try {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
                    Size[] jpegSizes = null;
                    if (characteristics != null) {
                        jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
                    }
                    int width = 858;
                    int height = 480;
                    /*if (jpegSizes != null && 0 < jpegSizes.length) {
                        width = jpegSizes[0].getWidth();
                        height = jpegSizes[0].getHeight();
                    }*/
                    ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
                    List<Surface> outputSurfaces = new ArrayList<Surface>(2);
                    outputSurfaces.add(reader.getSurface());
                    outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
                    final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                    captureBuilder.addTarget(reader.getSurface());
                    captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                    // Orientation
                    int rotation = getWindowManager().getDefaultDisplay().getRotation();
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

                    ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onImageAvailable(ImageReader reader) {                                                          //Aqui esta lo bueno del asunto
                            Image image = null;
                            try {
                                image = reader.acquireLatestImage();                    //Sacamos la imagen de la cola

                                ByteBuffer buffer = image.getPlanes()[0].getBuffer();   //Obtenemos los bytes de la imagen

                                byte[] bytes = new byte[buffer.capacity()];

                                buffer.get(bytes);

                                String imagen;
                                // textView.setText(imagen);

                                byte[] bytesencode = Base64.getEncoder().encode(bytes);             //Codificamos los datos
                                imagen = new String(bytesencode);                                    //Y lo convertimos a un string que pueda enviar por json (por ejemplo)                           //Hasta unos 60 mil caracteres ahora sacar de aqui la imagen, hasta 6 millones en mi movil

                                Log.e(TAG, String.valueOf(imagen.length()));
                                Log.e(TAG, imagen);
                                //textView2.setText(imagen); Esto peta por que son demasiados cracteres
                                //textView2.setText(String.valueOf(imagen.length()));

                                Intent respuestaintent = new Intent();                                  //NO FUNCIONA EN MI TELEFONO, HAY QUE BAJARLE LA CALIDAD A LA IMAGEN
                                respuestaintent.putExtra(RESPUESTA_FOTO, imagen);

                                float [] brujula = {Xvalue, YValue, Zvalue};
                                respuestaintent.putExtra(RESPUESTA_BRUJULA, brujula);

                                double [] GPS = {Latitud, Longitud};
                                respuestaintent.putExtra(RESPUESTA_COORDENADAS, GPS);

                                setResult(RESULT_OK, respuestaintent);
                                //Toast.makeText(getBaseContext(),"LLego hasta aqui", Toast.LENGTH_LONG).show();

                                finish();                                      //MATA LA ACTIVITY...
                                //Toast.makeText(getBaseContext(),"Tambien aqui", Toast.LENGTH_LONG).show();

                                //Esta parte es como devolver el string a un bitmap
                                byte[] respuesta = Base64.getDecoder().decode(imagen);              //Un ejemplo de recibir un string de json y convertirlo de nuevo a bytes


                                Bitmap bitmapImage = BitmapFactory.decodeByteArray(respuesta, 0, bytes.length, null);    //Instrucion clave, convierte los bytes a una imagen que se puede mostrar

                                //Poner el bitmap en el imageview
                                //imageView.setImageBitmap(bitmapImage);                                                                  //EL RESULTADO HERMOSO, mostramos la imagen

                            } catch (Exception e) {
                                e.printStackTrace();
                                finish();
                            } finally {
                                if (image != null) {
                                    image.close();
                                    finish();
                                }
                            }
                        }

                    };
                    reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
                    final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                        @Override
                        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                            super.onCaptureCompleted(session, request, result);
                            createCameraPreview();
                        }
                    };
                    cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                        @Override
                        public void onConfigured(CameraCaptureSession session) {
                            try {
                                session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session) {
                        }
                    }, mBackgroundHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    //--------------------------------------------CAMARA------------------------------------
    private void abrirCamara() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                return;

            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "open Camera");
    }

    private void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(Camara.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------GPS------------------------------------------

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Latitud = location.getLatitude();
        Longitud = location.getLongitude();

        LatV.setText("" + Latitud);
        LongV.setText("" + Longitud);
    }

    @SuppressLint("MissingPermission")
    private void UpdateGPS() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, Camara.this);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Un poema con el gps", Toast.LENGTH_LONG);
        }
    }

    //------------------------------------SENSOR-----------------------------------------------

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            Xvalue = sensorEvent.values[0];
            YValue = sensorEvent.values[0];
            Xvalue = sensorEvent.values[0];


            BX.setText("" + Xvalue);
            BY.setText("" + YValue);
            BZ.setText("" + Zvalue);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            abrirCamara();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
}


