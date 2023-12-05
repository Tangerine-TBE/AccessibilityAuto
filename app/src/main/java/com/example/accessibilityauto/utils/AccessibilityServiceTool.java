package com.example.accessibilityauto.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.text.TextUtils;

import com.example.accessibilityauto.MyService;
import com.example.accessibilityauto.R;
import com.example.accessibilityauto.custom.App;
import com.example.accessibilityauto.service.AccessibilityService;

import java.util.Locale;


public class AccessibilityServiceTool {

    private static final Class<MyService> sAccessibilityServiceClass = MyService.class;

    public static void enableAccessibilityService() {
        goToAccessibilitySetting();
    }

    public static void goToAccessibilitySetting() {
        Context context = App.Companion.getApp();
        if (Pref.isFirstGoToAccessibilitySetting()) {
//            GlobalAppContext.toast(context.getString(R.string.text_please_choose) + context.getString(R.string.app_name));
        }
        try {
            AccessibilityServiceUtils.INSTANCE.goToAccessibilitySetting(context);
        } catch (ActivityNotFoundException e) {
//            GlobalAppContext.toast(context.getString(R.string.go_to_accessibility_settings) + context.getString(R.string.app_name));
        }
    }

    private static final String cmd = "enabled=$(settings get secure enabled_accessibility_services)\n" +
            "pkg=%s\n" +
            "if [[ $enabled == *$pkg* ]]\n" +
            "then\n" +
            "echo already_enabled\n" +
            "else\n" +
            "enabled=$pkg:$enabled\n" +
            "settings put secure enabled_accessibility_services $enabled\n" +
            "fi\n" +
            "settings put secure accessibility_enabled 1";





    public static boolean isAccessibilityServiceEnabled(Context context) {
        return AccessibilityServiceUtils.INSTANCE.isAccessibilityServiceEnabled(context, sAccessibilityServiceClass);
    }
}
