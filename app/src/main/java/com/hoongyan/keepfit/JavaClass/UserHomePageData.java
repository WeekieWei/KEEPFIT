package com.hoongyan.keepfit.JavaClass;

import android.widget.ImageView;

import com.hoongyan.keepfit.R;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UserHomePageData {

    private String card1Title, card1TimeText, card2_1Text, card2_2Text, card2_3Text,
            card2_4Text, card2_5Text;
    private int card1ImageID;


    public UserHomePageData(String card2_1Text, String card2_2Text, String card2_3Text,
                            String card2_4Text, String card2_5Text) {
        this.card2_1Text = card2_1Text;
        this.card2_2Text = card2_2Text;
        this.card2_3Text = card2_3Text;
        this.card2_4Text = card2_4Text;
        this.card2_5Text = card2_5Text;

        int time = LocalTime.now().getHour();

        if(time < 12) {
            //Morning
            card1Title = "GOOD MORNING!";
            card1ImageID = R.drawable.ic_morning;
        }else if(time < 18){
            //Afternoon
            card1Title = "GOOD AFTERNOON!";
            card1ImageID = R.drawable.ic_afternoon;
        }else{
            card1Title = "GOOD EVENING!";
            card1ImageID = R.drawable.ic_evening;
        }

        card1TimeText = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public String getCard1Title() {
        return card1Title;
    }

    public String getCard1TimeText() {
        return card1TimeText;
    }

    public String getCard2_1Text() {
        return card2_1Text;
    }

    public String getCard2_2Text() {
        return card2_2Text;
    }

    public String getCard2_3Text() {
        return card2_3Text;
    }

    public String getCard2_4Text() {
        return card2_4Text;
    }

    public String getCard2_5Text() {
        return card2_5Text;
    }

    public int getCard1ImageID() {
        return card1ImageID;
    }
}
