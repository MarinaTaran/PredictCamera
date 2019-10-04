package com.example.camera;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

import clarifai2.api.ClarifaiBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {
    final String TAG="MainActivity";
    File directory;
    final int TYPE_PHOTO = 1;
    final int TYPE_VIDEO = 2;

    final int REQUEST_CODE_PHOTO = 1;
    final int REQUEST_CODE_VIDEO = 2;
    FirebaseStorage storage;
    // final String TAG = "myLogs";

    ImageView ivPhoto;

    EditText editText;

    String urlFoto;

    String result ;
    Float veroat;

    private  GestureDetectorCompat detectorCompat = null;




    private final OkHttpClient mOkHttpClient = new OkHttpClient(); //po http obrashaemsya k api
//    private String mAccessToken ="3f46aa60f8d44778bbf7195cead28379";
private String mAccessToken="d25b3db73e7d4bccac37cc971c5a355c" ; //na klarify
    private Call mCall; //klass realiz interfeys call s bibliotekoy http
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");//svoystva body ,kakoy tip bodi,sozdaem konstanta


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        storage= FirebaseStorage.getInstance(); //ssilka na storage
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        editText = (EditText)findViewById(R.id.editText);

//        new ClarifaiBuilder(mAccessToken)
//                .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
//                .buildSync();

        DetectSwipeListener detectSwipeListener = new DetectSwipeListener();//obrabotka svaypa
        detectSwipeListener.setActivity(this);
       detectorCompat= new GestureDetectorCompat(this,detectSwipeListener);//obrabotchik gestov
        Intent intent=new Intent(MainActivity.this,GetAgeGender.class);
        intent.putExtra("qwer",result);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//obrabotchik kasaniya
        detectorCompat.onTouchEvent(event);
        return true;
    }

    public void displayMessage(){
if(editText.getText().equals("LET'S GUESS WHAT'S ON YOUR PHOTO")){
    editText.setText("You can know your age");
    Log.d(TAG, "displayMessage: "+ editText.getText() );
}else{
    editText.setText("LET'S GUESS WHAT'S ON YOUR PHOTO");
}

    }

    public void onClickPhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    public void onClickVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_VIDEO));
        startActivityForResult(intent, REQUEST_CODE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
                    Log.d(TAG, "Photo uri: " + intent.getData());
                    Bundle bndl = intent.getExtras();
                    if (bndl != null) {
                        Object obj = intent.getExtras().get("data");//posilka chtob peredat foto
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            // ivPhoto.setImageBitmap(bitmap);
                            StorageReference storageRef = storage.getReference();
                            final StorageReference spaceRef = storageRef.child("images/space.jpg");//poluchaem potok byt
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = spaceRef.putBytes(data);//zadacha po vikladivaniu foto v storage

                            spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) { //standartno chtob slushat chto proisxodit v moment vikladivaniya pishem listener
                                    // Got the download URL for 'users/me/profile.png'
                                    // Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                    //generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                                    Log.d(TAG, "onSuccess: "+uri.toString());
                                    urlFoto=uri.toString();
                                    onProf();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    }
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (resultCode == RESULT_OK) {
                if (intent == null) {
                    Log.d(TAG, "Intent is null");
                } else {
//                    Log.d(TAG, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    private Uri generateFileUri(int type) {
        System.out.println("hgsdfjgsdhjadd");
        File file = null;
        switch (type) {
            case TYPE_PHOTO:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                break;
            case TYPE_VIDEO:
                file = new File(directory.getPath() + "/" + "video_"
                        + System.currentTimeMillis() + ".mp4");
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private void createDirectory() {
        directory = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyFolder");
        if (!directory.exists())
            directory.mkdirs();
        Log.d(TAG, "createDirectory: "+directory.toString());
    }

    public void onProf() {
//        String test ="{\n" +
//                "      \"inputs\": [\n" +
//                "        {\n" +
//                "          \"data\": {\n" +
//                "            \"image\": {\n" +
//                "              \"url\": \"urlFoto\n" +
//                "            }\n" +
//                "          }\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }'";
        String test=" {\n" +
                "      \"inputs\": [\n" +
                "        {\n" +
                "          \"data\": {\n" +
                "            \"image\": {\n" +
                "              \"url\": \""+urlFoto+"\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }'";
//        String json = "{ \"name\": \"Baeldung\", \"java\": true }";
        Log.d(TAG, "onProf: " + test);
        //JsonObject jsonObject = new JsonParser().parse(test).getAsJsonObject();

//        Assert.assertTrue(jsonObject.isJsonObject());
//        Assert.assertTrue(jsonObject.get("url").getAsString().equals(urlFoto));

        RequestBody formBody = RequestBody.create(JSON, test);
        final Request request = new Request.Builder()
                .url("https://api.clarifai.com/v2/models/c0c0ac362b03416da06ab3fa36fb58e3/outputs")
                .post(formBody)
                .addHeader("authorization", "Key " + mAccessToken)
                .addHeader("content-type", "application/json")
                .build();
        Log.d(TAG, "onProf: !!!!!!!!!!!!!"+ request);

        cancelCall();
        mCall = mOkHttpClient.newCall(request);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setResponse("Failed to fetch data: " + e);
            }



            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String qwe = response.body().string();//otvet na zapros request
                Log.d(TAG, "onResponse: ****" + qwe);
                Log.d(TAG, "onResponse: FOTO " + urlFoto);

                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(qwe);
                JsonObject object = element.getAsJsonObject();

                JsonArray output = (JsonArray) object.getAsJsonArray("outputs");
                Iterator it1 = output.iterator();
                JsonObject nol = (JsonObject) it1.next();

                JsonElement id = (JsonElement) nol.get("id");
                MyFoto myFoto = new MyFoto(id.getAsString());
                JsonObject data = (JsonObject) nol.get("data");
                JsonArray regions = (JsonArray) data.getAsJsonArray("regions");
                Iterator it2 = regions.iterator();
                int i =0;
                while (it2.hasNext()) {
                    JsonObject regionObg = (JsonObject) it2.next();
                    JsonObject data2 = (JsonObject) regionObg.get("data");
                    JsonObject fase = (JsonObject) data2.get("face");
                    Face tempFace=new Face();
                          tempFace.setGender(getCharacteristic("gender_appearance",fase));
                    tempFace.setAge(getCharacteristic("age_appearance",fase));
                    tempFace.setId(String.valueOf(i));
                    i++;
                    myFoto.getFacescollection().add(tempFace);
                }

            }
             String getCharacteristic(String characteristic, JsonObject node) {
                 String result = "default";
                 Float veroat = 0.0f;
                 JsonObject temp = (JsonObject) node.get(characteristic);
                 JsonArray concepts = (JsonArray) temp.getAsJsonArray("concepts");
                 Iterator it3 = concepts.iterator();

                 while (it3.hasNext()) {
                     JsonObject arrayValue = (JsonObject) it3.next();
                     JsonElement name = (JsonElement) arrayValue.get("name");
                     JsonElement value = (JsonElement) arrayValue.get("value");
                     if (Float.valueOf(value.getAsFloat()).compareTo(veroat) > 0) {
                         result = name.getAsString();
                         veroat = Float.valueOf(value.getAsFloat());
                         Log.d(TAG, "getCharacteristic: !!!!!!!!!!!!!!!!!!!!" + result + veroat + name + value);
                     }
                 }
                 return result;
            }
        });
        //  cancelCall();
//        addTarck();

    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    private void setResponse(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }





}

//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//import com.google.firebase.storage.UploadTask;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class MainActivity extends Activity {
//    Bitmap bitmap;
//    File directory;
//    final int TYPE_PHOTO = 1;
//    private StorageReference mStorageRef;
//
//    final int REQUEST_CODE_PHOTO = 1;
//
//    final String TAG = "MainActivity";
//
//    ImageView ivPhoto;
//    Button upload;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        createDirectory();
//        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
//        //FirebaseApp.initializeApp(this);
//        mStorageRef = FirebaseStorage.getInstance().getReference();
//        upload=findViewById(R.id.upload);
//        upload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,mStorageRef.getName(), Toast.LENGTH_LONG).show();
//            }
//        });
////        FirebaseStorage storage;
////        StorageReference storageReference;
////
////        storage = FirebaseStorage.getInstance();
////        storageReference = storage.getReference();
////
////
////        if(filePath != null)
////        {
////            final ProgressDialog progressDialog = new ProgressDialog(this);
////            progressDialog.setTitle("Uploading...");
////            progressDialog.show();
////
////            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
////            ref.putFile(filePath)
////                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                        @Override
////                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////                            progressDialog.dismiss();
////                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
////                        }
////                    })
////                    .addOnFailureListener(new OnFailureListener() {
////                        @Override
////                        public void onFailure(@NonNull Exception e) {
////                            progressDialog.dismiss();
////                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
////                        }
////                    })
////                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
////                        @Override
////                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
////                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
////                                    .getTotalByteCount());
////                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
////                        }
////                    });
////        }
//
//    }
//
//    public void onClickPhoto(View view) {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, generateFileUri(TYPE_PHOTO));
////        startActivityForResult(intent, REQUEST_CODE_PHOTO);
//onActivityResult(REQUEST_CODE_PHOTO, RESULT_OK,intent);
//
//    }
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent intent) {
//        if (requestCode == REQUEST_CODE_PHOTO) {
//            if (resultCode == RESULT_OK) {
//                if (intent == null) {
//                    Log.d(TAG, "Intent is null");
//                } else {
//                    Log.d(TAG, "Photo uri: " + intent.getData());
//                    Bundle bndl = intent.getExtras();
//                    if (bndl != null) {
//                        Object obj = intent.getExtras().get("data");
//                        if (obj instanceof Bitmap) {
//                            bitmap = (Bitmap) obj;
//                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
//                                    + bitmap.getHeight());
//
//                        }
//                    }
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Log.d(TAG, "Canceled");
//            }
//            submit();
//
////            final Request request = new Request.Builder()
////                    .url("https://api.spotify.com/v1/me")
////                    .addHeader("Authorization", "Bearer " + mAccessToken)
////                    .build();
////            //cancelCall();
////            mCall2 = mOkHttpClient.newCall(request);
////            mCall2.enqueue(new Callback() {
////
////                @Override
////                public void onFailure(Call call, IOException e) {
////                    setResponse1("Failed to fetch data: " + e);
////                }
////
////                @Override
////                public void onResponse(Call call, Response response) throws IOException {
////                    String qwe = response.body().string();
////                    JsonParser parser = new JsonParser();
////                    JsonObject root = (JsonObject) parser.parse(qwe);
////
//////                userId=result.append(root.get("id").toString());
////                    userId = root.get("id").toString().replace("\"", "");
////
//////                Log.d(TAG, "onResponse: USERID " + result.toString());
////                }
////            });
//
//        }
//    }
//
//    public void submit() {hild("images/mou
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference storageRef = storage.getReference();
//
//        final StorageReference mountainsRef = storageRef.child("mountains.jpg");
//        StorageReference mountainImagesRef = storageRef.cntains.jpg");//kogda vot eto dobavila ,poyavilas kamere
//        mountainsRef.getName().equals(mountainImagesRef.getName());
//        mountainsRef.getPath().equals(mountainImagesRef.getPath());
//        ivPhoto.setImageBitmap(bitmap);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        UploadTask uploadTask = storageRef.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle unsuccessful uploads
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
//                // ...
//            }
//        });
//    }
//
//        private Uri generateFileUri(int type) {
//        File file = null;
////        switch (type) {
////            case TYPE_PHOTO:
//                file = new File(directory.getPath() + "/" + "photo_"
//                        + System.currentTimeMillis() + ".jpg");
////                break;
////            case TYPE_VIDEO:
////                file = new File(directory.getPath() + "/" + "video_"
////                        + System.currentTimeMillis() + ".mp4");
////                break;
////        }
//        Log.d(TAG, "fileName = " + file);
//        return Uri.fromFile(file);
//    }
//
//    private void createDirectory() {
//        directory = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "MyFolder");
//        if (!directory.exists())
//            directory.mkdirs();
//    }
//
//
//
//
//}