package com.example.camera;


import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDirectory();
        storage= FirebaseStorage.getInstance();
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
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
                        Object obj = intent.getExtras().get("data");
                        if (obj instanceof Bitmap) {
                            Bitmap bitmap = (Bitmap) obj;
                            Log.d(TAG, "bitmap " + bitmap.getWidth() + " x "
                                    + bitmap.getHeight());
                            // ivPhoto.setImageBitmap(bitmap);
                            StorageReference storageRef = storage.getReference();
                            final StorageReference spaceRef = storageRef.child("images/space.jpg");
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = spaceRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                    //Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                    Log.d(TAG, "onSuccess:  11111111111111");
                                    spaceRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            // Got the download URL for 'users/me/profile.png'
                                            // Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                            //generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                                            Log.d(TAG, "onSuccess: "+uri.toString());
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });
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
                    Log.d(TAG, "Video uri: " + intent.getData());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Canceled");
            }
        }
    }

    private Uri generateFileUri(int type) {
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