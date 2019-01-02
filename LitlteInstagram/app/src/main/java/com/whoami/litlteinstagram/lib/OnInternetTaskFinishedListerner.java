package com.whoami.litlteinstagram.lib;

import org.json.JSONException;

public interface OnInternetTaskFinishedListerner
{
    void OnInternetTaskFinished(InternetTask internetTask) throws JSONException;
    void OnInternetTaskFailed(InternetTask internetTask);
}
