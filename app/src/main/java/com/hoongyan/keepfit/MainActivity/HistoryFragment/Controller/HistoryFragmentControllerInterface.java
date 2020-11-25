package com.hoongyan.keepfit.MainActivity.HistoryFragment.Controller;

import java.util.Date;

public interface HistoryFragmentControllerInterface {

    public void removeItemAtPosition(int position, String documentID, Date itemDate, int foodSlotID, double totalCal, int type);

    public void editItemAtPosition(int position, String documentID);
}
