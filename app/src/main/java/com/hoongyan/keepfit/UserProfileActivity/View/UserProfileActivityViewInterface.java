package com.hoongyan.keepfit.UserProfileActivity.View;

import com.hoongyan.keepfit.MVCView;

public interface UserProfileActivityViewInterface extends MVCView {

    public void attachListenersToButtonGroups();
    public void adaptViewsVisibility(int type);
    public void navigateToNewActivity(boolean isUserProfileCreated);

}
