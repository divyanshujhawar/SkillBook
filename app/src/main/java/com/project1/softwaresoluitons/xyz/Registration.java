package com.project1.softwaresoluitons.xyz;

import android.util.Log;

/**
 * Created by DELL on 11/1/2017.
 */

public class Registration{
    String traineeId;
    String trainingId;

    public Registration() {
    }

    public Registration( String traineeId , String trainingId) {

        this.traineeId = traineeId;
        this.trainingId = trainingId;
        Log.i("traineeId", this.traineeId + " " + this.trainingId);
    }

    public String getTraineeId() {
        return traineeId;
    }

    public String getTrainingId() {
        return trainingId;
    }
}
