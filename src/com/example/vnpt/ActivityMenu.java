package com.example.vnpt;

import com.vnpt.model.Util;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ActivityMenu extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		Toast.makeText(ActivityMenu.this, Util.listHeThong.toString(),Toast.LENGTH_LONG).show();
	}

}
