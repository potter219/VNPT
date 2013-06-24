package com.example.vnpt;

import java.util.ArrayList;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import com.vnpt.model.PreferenceConnector;
import com.vnpt.model.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class Login extends Activity {
	Button bttLogin;
	CheckBox checkPass;
	ProgressDialog progressDialog;
	public static final String NAMESPACE = "http://tempuri.org/";
	public static String WSDL = "http://123.16.191.37/WSLink/XacThuc.asmx?WSDL";
	String METHOD_NAME = "NhanMatKhauOTPQuaSMS";
	String SOAP_ACTION = "http://tempuri.org/NhanMatKhauOTPQuaSMS";
	String USER;
	String PASS;
	EditText userEditText;
	EditText passEditText;
	boolean flag= true;
	

	public static String WSDLGetQuyen = "http://123.16.191.37/thinp/returndata.asmx?WSDL";
	String METHOD_NAMEGetQuyen = "PhanQuyen";
	String SOAP_ACTIONGetQuyen = "http://tempuri.org/PhanQuyen";

	private Element buildAuthHeader() {
		Element h = new Element().createElement(NAMESPACE, "AuthHeader");
		Element username = new Element().createElement(NAMESPACE, "Username");
		username.addChild(Node.TEXT, "WSGWDVKH");
		h.addChild(Node.ELEMENT, username);
		Element pass = new Element().createElement(NAMESPACE, "Password");
		pass.addChild(Node.TEXT, "Authentication");
		h.addChild(Node.ELEMENT, pass);

		return h;
	}

	class WebSevice extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("prUserName", USER);
			request.addProperty("prPassword", PASS);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			envelope.headerOut = new Element[1];
			envelope.headerOut[0] = buildAuthHeader();

			envelope.dotNet = true;

			SoapObject requestGetQuyen = new SoapObject(NAMESPACE,
					METHOD_NAMEGetQuyen);
			requestGetQuyen.addProperty("name", USER.toLowerCase());
			SoapSerializationEnvelope envelopeGetQuyen = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelopeGetQuyen.setOutputSoapObject(requestGetQuyen);
			envelopeGetQuyen.dotNet = true;

			try {
				
				// ------------getquyen-----------//
				HttpTransportSE ht = new HttpTransportSE(WSDLGetQuyen, 20000);
				envelopeGetQuyen.bodyOut = requestGetQuyen;
				ht.debug = true;
				ht.call(SOAP_ACTIONGetQuyen, envelopeGetQuyen);
				SoapObject obj = (SoapObject) envelopeGetQuyen.getResponse();
				while (Util.listQuyenUser.size()>0) {
					Util.listQuyenUser.remove(0);
					
				}
				int length = obj.getPropertyCount();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						SoapPrimitive temp = (SoapPrimitive) obj.getProperty(i);
						int item = Integer.parseInt(temp.toString());
						Util.listQuyenUser.add(item);

					}
				}
				
				
				// ---Dang nhap----//
				ht = new HttpTransportSE(WSDL, 20000);
				envelope.bodyOut = request;
				ht.debug = true;
				ht.call(SOAP_ACTION, envelope);
				SoapPrimitive primitive = (SoapPrimitive) envelope
						.getResponse();
				// ----------------xong------------------//

				

				return primitive.toString();
			} catch (Exception e) {
				e.printStackTrace();
				progressDialog.dismiss();
				return "Loi";

			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			System.out.println(result);
			String [] temp = result.split("\\|");
			String maKetQua= temp[0];
			final String maOPT = temp[1];
			if (result.equals("Loi")) {
				AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
				alert.setTitle("Thông báo!");
				alert.setMessage("Không kết nối được đến hệ thống ");
				alert.setPositiveButton("OK", null);
				alert.show();

			}
			if (maKetQua.equals("0")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
				builder.setTitle("Nhập mã OTP");

				// Set up the input
				final EditText input = new EditText(Login.this);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
				builder.setView(input);

				// Set up the buttons
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        String temp   = input.getText().toString();
				        if (temp.equals(maOPT)){
				        	Intent i = new Intent(Login.this, MainAction.class);
							startActivity(i);
				        	
				        }
				        else {
				        	AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
							alert.setTitle("Thông báo!");
							alert.setMessage("Kiểm tra lại mã OPT!");
							alert.setPositiveButton("OK", null);
							alert.show();
						}
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});

				builder.show();
			
				
				
			}
			if (maKetQua.equals("2")) {
				AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
				alert.setTitle("Thông báo!");
				alert.setMessage("Kiểm tra lại thông tin đăng nhập ");
				alert.setPositiveButton("OK", null);
				alert.show();

			}
			if (maKetQua.equals("1")) {
				AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
				alert.setTitle("Thông báo!");
				alert.setMessage("Tài khoản của bạn chưa đăng ký số điện thoại xác thực. Liên hệ quản lý hệ thống để đăng ký!");
				alert.setPositiveButton("OK", null);
				alert.show();

				
				
//				AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
//				builder.setTitle("Nhập số điện thoại của bạn để nhận mã OPT!");
//
//				// Set up the input
//				final EditText input = new EditText(Login.this);
//				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//				input.setInputType(InputType.TYPE_CLASS_NUMBER);
//				builder.setView(input);
//
//				// Set up the buttons
//				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
//				    @Override
//				    public void onClick(DialogInterface dialog, int which) {
//				       final String  sdt= input.getText().toString().trim();
//				       ProgressDialog pr = new ProgressDialog(Login.this);
//						pr.setMessage("Đang xử lý");
//						pr.setIndeterminate(false);
//						pr.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//						pr.setCancelable(true);
//						pr.show();
//				       Thread thread = new Thread(){
//							@Override
//							public void run() {
//								
//								try {
//										
//										SoapObject request = new SoapObject(NAMESPACE,
//												"CapNhatSoDiDong");
//										SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//												SoapEnvelope.VER11);
//										request.addProperty("username", USER);
//										request.addProperty("encryptpassword", PASS);
//										request.addProperty("sodidong",sdt);
//										envelope.setOutputSoapObject(request);
//										envelope.headerOut = new Element[1];
//										envelope.headerOut[0] = buildAuthHeader();
//										envelope.dotNet = true;
//										envelope.setOutputSoapObject(request);
//										HttpTransportSE ht = new HttpTransportSE(WSDL, 20000);
//										ht.debug = true;
//										ht.call("http://tempuri.org/CapNhatSoDiDong", envelope);
//										envelope.bodyOut = request;
//										System.out.println(request.toString());
//										SoapPrimitive primitive = (SoapPrimitive) envelope
//												.getResponse();
//
//
//									
//									
//								} catch (Exception e) {
//									// TODO: handle exception
//									System.out.println(e);
//								}
//							}
//							
//						};
//						thread.start();
//						pr.dismiss();
//						
//
//				    }
//				});
//				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//				    @Override
//				    public void onClick(DialogInterface dialog, int which) {
//				        dialog.cancel();
//				    }
//				});
//
//				builder.show();
//
//				
//								
//				
		}
//

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressDialog = new ProgressDialog(Login.this);
			progressDialog.setMessage("Đang xử lý");
			progressDialog.setIndeterminate(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(true);
			progressDialog.show();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Util.listQuyenUser = new ArrayList<Integer>();
		

		bttLogin = (Button) findViewById(R.id.bttLogin);
		userEditText = (EditText) findViewById(R.id.user);
		passEditText = (EditText) findViewById(R.id.pass);
		checkPass = (CheckBox) findViewById(R.id.checkPass);
		try {
			userEditText.setText(PreferenceConnector.readString(Login.this,
					PreferenceConnector.USER, null));
			passEditText.setText(PreferenceConnector.readString(Login.this,
					PreferenceConnector.PASS, null));
		} catch (Exception e) {
			// TODO: handle exceptions
			System.out.println(e);

		}
		// Util.enterEdittext(userEditText, passEditText);
		passEditText.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					TraCuu();

				}
				return false;
			}
		});
		passEditText
				.setTransformationMethod(new PasswordTransformationMethod());
		checkPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked == false) {
					passEditText
							.setTransformationMethod(new PasswordTransformationMethod());
				} else {
					passEditText.setTransformationMethod(null);
				}

			}
		});

		bttLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TraCuu();

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		Util.exit(Login.this);
	}

	public void TraCuu() {

		USER = userEditText.getText().toString();
		PASS = passEditText.getText().toString();
		PreferenceConnector.writeString(Login.this, PreferenceConnector.USER,
				USER);
		PreferenceConnector.writeString(Login.this, PreferenceConnector.PASS,
				PASS);
		if (USER.length() == 0 || PASS.length() == 0) {
			AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
			alert.setTitle("Thông báo!");
			alert.setMessage("Nhập đủ user và pass ");
			alert.setPositiveButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					// System.exit(0);

				}
			});
			alert.show();

		} else {
			WebSevice we = new WebSevice();
			we.execute();
		}
	}
	
	
	

}
