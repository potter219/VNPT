package com.example.vnpt;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.vnpt.model.LoaiDichVu;
import com.vnpt.model.NghiepVu;
import com.vnpt.model.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;

public class ActivityMain extends Activity {
	String eros;
	public static String SOAP_ACTION = "http://tempuri.org/TenLoaiDichVu";
	public static String METHOD_NAME = "TenLoaiDichVu";
	public static String SOAP_ACTION2 = "http://tempuri.org/ForMobile";
	public static String METHOD_NAME2 = "ForMobile";

	ProgressDialog progressDialog;
	public static final String NAMESPACE = "http://tempuri.org/";
	public static String WSDL = "http://123.16.191.37/thinp/returndata.asmx?WSDL";
	ArrayList<LoaiDichVu> lst = new ArrayList<LoaiDichVu>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		WebSevice web = new WebSevice();
		web.execute();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	class WebSevice extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			envelope.dotNet = true;

			try {
				HttpTransportSE ht = new HttpTransportSE(WSDL, 20000);
				envelope.bodyOut = request;
				ht.debug = true;
				ht.call(SOAP_ACTION, envelope);
				SoapObject result = (SoapObject) envelope.getResponse(); // get
				int length = result.getPropertyCount();
				for (int i = 0; i < length; i++) {
					SoapObject temp = (SoapObject) result.getProperty(i);
					LoaiDichVu loaiDichVu = new LoaiDichVu();
					loaiDichVu.setTenDichVu(temp.getProperty("tenLoaiDichVu")
							.toString());
					loaiDichVu.setMaDichVu(temp.getProperty("maLoaiDichVu")
							.toString());
					int coNghiepVu = Integer.valueOf(temp.getProperty(
							"coDichVu").toString());
					loaiDichVu.setCoNghiepVu(coNghiepVu);
					lst.add(loaiDichVu);
					Util.listLoaiDichVu = lst;
				}

				List<NghiepVu> listNghiepVus = new ArrayList<NghiepVu>();
				request = new SoapObject(NAMESPACE, METHOD_NAME2);
				envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);
				envelope.dotNet = true;

				ht = new HttpTransportSE(WSDL, 20000);
				envelope.bodyOut = request;
				ht.debug = true;
				ht.call(SOAP_ACTION2, envelope);
				result = (SoapObject) envelope.getResponse(); // get
				length = result.getPropertyCount();
				for (int i = 0; i < length; i++) {
					SoapObject temp = (SoapObject) result.getProperty(i);
					NghiepVu nghiepVu = new NghiepVu();
					nghiepVu.setmMaDichVu(temp.getProperty("MaLoaiDV")
							.toString());
					nghiepVu.setmAction(temp.getProperty("Action").toString());
					nghiepVu.setmBien(temp.getProperty("Bien").toString());
					nghiepVu.setmInput(temp.getProperty("INPut").toString());
					nghiepVu.setmOutput(temp.getProperty("OutPut").toString());
					nghiepVu.setmTenDichVu(temp.getProperty("TenDichVu")
							.toString());
					nghiepVu.setmViDu(temp.getProperty("ViDu").toString());
					nghiepVu.setmWebservice(temp.getProperty("WS").toString());
					nghiepVu.setID(Integer.parseInt(temp.getProperty("IDDichVu").toString()));

					listNghiepVus.add(nghiepVu);
				}
				Util.listNghiepVu = listNghiepVus;

			}

			catch (Exception e) {
				e.printStackTrace();
				progressDialog.dismiss();

				// System.exit(0);
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			if (Util.listLoaiDichVu == null) {
				AlertDialog.Builder alert = new AlertDialog.Builder(
						ActivityMain.this);
				alert.setTitle("Thông báo!");
				alert.setMessage("Kiểm tra lại đường truyền mạng ");
				alert.setPositiveButton("OK", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						System.exit(0);
					}
				});
				
//				alert.setOnCancelListener(new OnCancelListener() {
//					
//					@Override
//					public void onCancel(DialogInterface dialog) {
//						// TODO Auto-generated method stub
//						System.exit(0);
//						
//					}
//				});
//				
				
				alert.show();

			}

			else {
				Intent i = new Intent(ActivityMain.this, ActivityLogin.class);
				progressDialog.dismiss();
				startActivity(i);
				finish();

			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(ActivityMain.this);
			progressDialog.setMessage("Đang xử lý");
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

	}

}
