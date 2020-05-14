package io.lbry.browser.tasks.claim;

import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.lbry.browser.exceptions.ApiCallException;
import io.lbry.browser.model.Claim;
import io.lbry.browser.utils.Helper;
import io.lbry.browser.utils.Lbry;

public class ClaimListTask extends AsyncTask<Void, Void, List<Claim>> {
    private String type;
    private View progressView;
    private ClaimListResultHandler handler;
    private Exception error;

    public ClaimListTask(String type, View progressView, ClaimListResultHandler handler) {
        this.type = type;
        this.progressView = progressView;
        this.handler = handler;
    }
    protected void onPreExecute() {
        Helper.setViewVisibility(progressView, View.VISIBLE);
    }
    protected List<Claim> doInBackground(Void... params) {
        List<Claim> claims = null;

        try {
            Map<String, Object> options = new HashMap<>();
            if (!Helper.isNullOrEmpty(type)) {
                options.put("claim_type", type);
            }
            options.put("page", 1);
            options.put("page_size", 999);
            options.put("resolve", true);

            JSONObject result = (JSONObject) Lbry.genericApiCall(Lbry.METHOD_CLAIM_LIST, options);
            JSONArray items = result.getJSONArray("items");
            claims = new ArrayList<>();
            for (int i = 0; i < items.length(); i++) {
                claims.add(Claim.fromJSONObject(items.getJSONObject(i)));
            }
        } catch (ApiCallException | JSONException ex) {
            error = ex;
        }
        return claims;
    }
    protected void onPostExecute(List<Claim> claims) {
        Helper.setViewVisibility(progressView, View.GONE);
        if (handler != null) {
            if (claims != null) {
                handler.onSuccess(claims);
            } else {
                handler.onError(error);
            }
        }
    }
}