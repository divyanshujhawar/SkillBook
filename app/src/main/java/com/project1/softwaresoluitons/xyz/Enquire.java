package com.project1.softwaresoluitons.xyz;

/**
 * Created by DELL on 10/31/2017.
 */

public class Enquire {
    String trainingProgramId, traineeId, trainerId, message, date, time, replyStatus, replyMessage, status;
    String trainingTitle;
    String id;

    public Enquire() {
    }

    public Enquire(String id,String trainingProgramId, String traineeId, String trainerId, String message, String date, String time, String status,
                   String replyMessage, String replyStatus, String trainingTitle) {
        this.id = id;
        this.trainingProgramId = trainingProgramId;
        this.trainerId = trainerId;
        this.traineeId = traineeId;
        this.message = message;
        this.date = date;
        this.time = time;
        this.replyStatus = replyStatus;
        this.replyMessage = replyMessage;
        this.status = status;
        this.trainingTitle = trainingTitle;
    }

    public String getTrainerId() {
        return trainerId;
    }

    public String getId() {
        return id;
    }

    public String getTrainingProgramId() {
        return trainingProgramId;
    }

    public String getTraineeId() {
        return traineeId;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public String getStatus() {
        return status;
    }

    public String getTrainingTitle() {
        return trainingTitle;
    }
}
