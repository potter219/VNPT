package com.example.vnpt;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.vnpt.model.LoaiDichVu;
import com.vnpt.model.NghiepVu;
import com.vnpt.model.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ActivityTraCuu extends Activity {
	ProgressDialog progressDialog;
	public static final String NAMESPACE = "http://tempuri.org/";
	public static String WSDL = "http://123.16.191.37/thinp/Webservice1.asmx?WSDL";
	String METHOD_NAME;
	String SOAP_ACTION;
	Spinner mSpinnerDichVu;
	EditText mInputData;
	Button mButtonOK;
	TextView mKetQua;
	Spinner mSpinnerNghiepVu;
	TextView lblNhap;
	TextView ldlKetQua;
	String maThongBao;
	ArrayList<NghiepVu> lstNghiepVuDuocChon = new ArrayList<NghiepVu>();
	List<NghiepVu> lst;
	
	public void inputDataToSpinnerNghiepVu(ArrayList<NghiepVu> lNghiepVus) {
		try {
			ArrayList<String> temp = new ArrayList<String>();
			for (NghiepVu nghiepVu : lNghiepVus) {
				temp.add(nghiepVu.getmTenDichVu());
			}
			ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityTraCuu.this,
					android.R.layout.simple_spinner_item, temp.toArray());
			arrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinnerNghiepVu.setAdapter(arrayAdapter);

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void getDichVu() {
		mSpinnerDichVu.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				mInputData.setHint("Nhập thông tin");
				lblNhap.setText("Nhập");
				ldlKetQua.setText("Kết quả");
				mInputData.setText("");
				mKetQua.setText("");
				mButtonOK.setEnabled(false);

				while (lstNghiepVuDuocChon.size() > 0) {
					lstNghiepVuDuocChon.remove(0);

				}
				String temp = Util.listLoaiDichVu.get(arg2).getMaDichVu();
				for (NghiepVu nghiepVu : lst) {
					if (nghiepVu.getmMaDichVu().equals(temp)) {
						lstNghiepVuDuocChon.add(nghiepVu);
					}
				}
				inputDataToSpinnerNghiepVu(lstNghiepVuDuocChon);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void getAllData() {

		mSpinnerNghiepVu
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						String temp = (lstNghiepVuDuocChon.get(arg2)
								.getmWebservice().split("/"))[1];
						METHOD_NAME = temp;
						NghiepVu nghiepVuTemp = lstNghiepVuDuocChon.get(arg2);
						SOAP_ACTION = "http://tempuri.org/" + temp;
						mInputData.setHint(nghiepVuTemp.getmViDu());
						lblNhap.setText(nghiepVuTemp.getmInput());
						ldlKetQua.setText(nghiepVuTemp.getmOutput());
						mInputData.setText("");
						mKetQua.setText("");
						mButtonOK.setEnabled(true);
						maThongBao = nghiepVuTemp.getmInput();
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

	}

	class WebSevice extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("data", mInputData.getText().toString());
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			envelope.dotNet = true;

			try {
				HttpTransportSE ht = new HttpTransportSE(WSDL, 20000);
				envelope.bodyOut = request;
				ht.debug = true;
				ht.call(SOAP_ACTION, envelope);
				SoapPrimitive primitive = (SoapPrimitive) envelope
						.getResponse();
				return primitive.toString();
			} catch (Exception e) {
				progressDialog.dismiss();
				return "Không kết nối được đến hệ thống";

			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			if (result.contains("<br />")) {
				String[] ketqua = result.split("<br />");
				result = ketqua[0] + "\n" + ketqua[1];
			}
			mKetQua.setText(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(ActivityTraCuu.this);
			progressDialog.setMessage("Đang xử lý");
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(ActivityTraCuu.this);
		alert.setTitle("Thông báo!");
		alert.setMessage("Bạn có muốn thoát khỏi tài khoản này không?");
		alert.setNegativeButton("Quay lại",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				});
		alert.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				back();
			}
		});
		alert.show();
	}

	public void back() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		
		lst = new ArrayList<NghiepVu>(); 
		for (int i = 0; i < Util.listQuyenUser.size(); i++) {
			int idNghiepVu = Util.listQuyenUser.get(i);
			for (int j = 0; j< Util.listNghiepVu.size(); j++) {
				if (Util.listNghiepVu.get(j).getID()==idNghiepVu){
					lst.add(Util.listNghiepVu.get(j));
				}
				
			}
			
		}
		//Util.listNghiepVu= lst;
		mSpinnerDichVu = (Spinner) findViewById(R.id.loaiDichVu);
		mSpinnerNghiepVu = (Spinner) findViewById(R.id.nghiepVu);
		mButtonOK = (Button) findViewById(R.id.bttOK);
		mInputData = (EditText) findViewById(R.id.inputData);
		mKetQua = (TextView) findViewById(R.id.ketqua);
		lblNhap = (TextView) findViewById(R.id.lblNhap);
		ldlKetQua = (TextView) findViewById(R.id.lblKetQua);

		ArrayList<String> listTenLoaiDichVu = new ArrayList<String>();
		for (LoaiDichVu loaiDichVu : Util.listLoaiDichVu) {
			listTenLoaiDichVu.add(loaiDichVu.getTenDichVu());

		}
		ArrayAdapter arrayAdapter = new ArrayAdapter(ActivityTraCuu.this,
				android.R.layout.simple_spinner_item,
				listTenLoaiDichVu.toArray());
		arrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerDichVu.setAdapter(arrayAdapter);
		getDichVu();
		getAllData();
		mButtonOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mInputData.getText().toString().length() == 0) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							ActivityTraCuu.this);
					alert.setTitle("Thông báo!");
					alert.setMessage("Chưa nhập " + maThongBao);
					alert.setPositiveButton("OK", null);
					alert.show();

				} else {
					WebSevice temp = new WebSevice();
					temp.execute();
				}
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}
}
