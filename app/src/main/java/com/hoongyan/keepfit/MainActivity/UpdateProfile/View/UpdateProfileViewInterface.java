package com.hoongyan.keepfit.MainActivity.UpdateProfile.View;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hoongyan.keepfit.MVCView;
import com.hoongyan.keepfit.MainActivity.UpdateProfile.Controller.UpdateProfileController;

public interface UpdateProfileViewInterface extends MVCView {
    public View inflateFragmentLayout(LayoutInflater inflater, ViewGroup container);

    public void setMVCController(UpdateProfileController controller);

    public void generateAlertDialog(double bmi, int weightAdjust);
}
