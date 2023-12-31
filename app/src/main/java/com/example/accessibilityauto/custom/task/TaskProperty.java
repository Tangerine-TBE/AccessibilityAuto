package com.example.accessibilityauto.custom.task;

import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;

public class TaskProperty {
    private TaskType taskType;
    private String packName;
    private String pathName;
    private String type;
    private boolean keep;
    private Job job;
    private String appName;

    private long defaultTimeOut;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public boolean isKeep() {
        return keep;
    }


    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public TaskProperty(TaskType taskType, String packName, String pathName, String type, boolean keep, Job job,String appName) {
        this.taskType = taskType;
        this.packName = packName;
        this.pathName = pathName;
        this.type = type;
        this.keep = keep;
        this.job = job;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getDefaultTimeOut() {
        return defaultTimeOut;
    }

    public void setDefaultTimeOut(long defaultTimeOut) {
        this.defaultTimeOut = defaultTimeOut;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
