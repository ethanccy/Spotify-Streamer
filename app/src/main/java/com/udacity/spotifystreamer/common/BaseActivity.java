package com.udacity.spotifystreamer.common;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

public abstract class BaseActivity extends ActionBarActivity {

	public ProgressDialog progressDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 *
	 *
	 * @param message
	 *
	 */
	public void showProgress(String message) {
		if(progressDialog == null) {
			progressDialog = new ProgressDialog(BaseActivity.this);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setMessage(message);
			progressDialog.show();
		} else if(!progressDialog.isShowing()) {
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
			progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			progressDialog.setMessage(message);
			progressDialog.show();
		}
	}

	public void finishProgress() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
			progressDialog = null;
		}
	}

    @Override
    protected void onDestroy() {
        finishProgress();
        super.onDestroy();
    }
}
