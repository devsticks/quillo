package io.quillo.quillo.utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.result.BitmapPhoto;
import io.fotoapparat.view.CameraView;
import io.quillo.quillo.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import static io.fotoapparat.selector.LensPositionSelectorsKt.back;
import static io.fotoapparat.selector.ResolutionSelectorsKt.highestResolution;

/**
 * Created by shkla on 2018/02/03.
 */

public class FotoAppartCamera extends Fragment {

    @BindView(R.id.camera_view)
    CameraView cameraView;

    private Fotoapparat fotoapparat;
    private CameraListner cameraListner;

    public interface CameraListner{
        public void onPhotoTaken(BitmapPhoto bitmap);
    }

    public void setCameraListner(CameraListner cameraListner){
        this.cameraListner = cameraListner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fotoapart_camera, container, false);
        ButterKnife.bind(this, view);

        fotoapparat = Fotoapparat.with(getContext())
                .into(cameraView)
                .previewScaleType(ScaleType.CenterCrop)
                .photoResolution(highestResolution())
                .lensPosition(back())
                .build();

        fotoapparat.start();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        fotoapparat.stop();
    }

    @OnClick(R.id.take_photo_button)
    public void takePhoto(){
        fotoapparat.takePicture().toBitmap().whenAvailable(new Function1<BitmapPhoto, Unit>() {
            @Override
            public Unit invoke(BitmapPhoto bitmapPhoto) {
                cameraListner.onPhotoTaken(bitmapPhoto);
                getActivity().getSupportFragmentManager().popBackStack();
                return null;
            }
        });
    }
}




