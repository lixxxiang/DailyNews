package com.example.administrator.news;

import android.content.Intent;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class CallBack extends CordovaPlugin {


    public void speak(String content) {
        Log.e("url", content);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("speak".equals(action)) {
            String content = args.getString(0);
            speak(content);
            Intent intent = new Intent().setClass(cordova.getActivity(), FullscreenActivityNews.class);
            intent.putExtra("url", content);
            cordova.getActivity().startActivity(intent);
            callbackContext.success("finish");
            return true;
        }
        return false;
    }

}
