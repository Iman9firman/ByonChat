package zharfan.com.cameralibrary;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import androidx.annotation.RequiresPermission;

public class Camera {
    private CameraActivity cameraActivity;

    public Camera (Activity activity, int requestCode){
        CameraActivity.Builder builder = new CameraActivity.Builder(activity,requestCode);
        cameraActivity = builder.build();
    }
    public Camera (CameraActivity cameraActivity){
        this.cameraActivity = cameraActivity;
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    public void lauchCamera(){
        if (cameraActivity == null || (cameraActivity.getActivity() == null))
            return;
        Intent openCamera;

        openCamera = new Intent(cameraActivity.getActivity(),CameraActivity.class);
        openCamera.putExtra("LOCK_FACE_CAMERA",cameraActivity.getLockFaceCamera());
        openCamera.putExtra("CAMERA_FACE",cameraActivity.getCameraFace());
        openCamera.putExtra("FLASH",cameraActivity.getFlashDefault());
        openCamera.putExtra("QUALITY",cameraActivity.getQuality());
        openCamera.putExtra("RATIO",cameraActivity.getAspectRatio());
        openCamera.putExtra("FILE_NAME",cameraActivity.getFileName());
        openCamera.putExtra("REQUEST_CODE",cameraActivity.getRequestCode());
        ShrdPref.setFlag(cameraActivity.getActivity(),ShrdPref.START_CAMERA_LIB);
        cameraActivity.getActivity().startActivityForResult(openCamera,cameraActivity.getRequestCode());
    }
}
