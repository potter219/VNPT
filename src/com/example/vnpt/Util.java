package com.example.vnpt;

import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Util {
	public static List<NghiepVu> listNghiepVu;
	public static List<LoaiDichVu> listLoaiDichVu;
	public static List<Integer> listQuyenUser;
	ProgressDialog progressDialog;
	public static boolean flag= false;
	public static void exit(Context context) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Thông báo!");
		alert.setMessage("Bạn có muốn thoát không ");
		alert.setNegativeButton("Quay lại", null);
		alert.setPositiveButton("Thoát", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		alert.show();
	}



	public static Object CallWebservice(String WSDL, final String METHOD_NAME,
			String SOAP_ACTION, String NAMESPACE, String[] input, String[] para)
			throws Exception {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		for (int i = 0; i < para.length; i++) {
			request.addProperty(input[i], para[i]);

		}
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		HttpTransportSE ht = new HttpTransportSE(WSDL, 20000);
		envelope.bodyOut = request;
		ht.debug = true;
		ht.call(SOAP_ACTION, envelope);
		Object object = envelope.getResponse();
		return object;
	}

	public static void hideKeyBoard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	public static void enterEdittext(final EditText edit, final View second){
		edit.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode== KeyEvent.KEYCODE_ENTER ) {
					second.isFocusable();
					edit.requestFocus();
				}
				return false;
			}
		});
		
	}
	public static List<NghiepVu> phanQuyen(List<NghiepVu> lstNghiepVu, List<Integer> listInt){
		
		for (NghiepVu item : lstNghiepVu) {
			if (!listInt.contains(item.getID())){
				lstNghiepVu.remove(item);
				
			}
		}
		return lstNghiepVu;
	}
}
