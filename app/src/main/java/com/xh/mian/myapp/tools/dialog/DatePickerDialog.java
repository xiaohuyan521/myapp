package com.xh.mian.myapp.tools.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;


import com.xh.mian.myapp.R;
import com.xh.mian.myapp.tools.other.Tools;
import com.xh.mian.myapp.tools.uitl.DateUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class DatePickerDialog {
	private static final String DatePickerDialogTAG = "DatePickerDialog";
	private static volatile DatePickerDialog aDialog=null;
	private Button positiveButton;
	private ImageButton negativeButton;
	private OnOkButtonFireListener okListener;
	private NumberPicker numY=null;
	private NumberPicker numM=null;
	private NumberPicker numD=null;
	private NumberPicker numHH=null;
	private NumberPicker numMM=null;
	private NumberPicker numSS=null;

	private List<EditText> edtListY;
	private List<EditText> edtListM;
	private List<EditText> edtListD;

	private List<EditText> edtListHH;
	private List<EditText> edtListMM;
	private List<EditText> edtListSS;

	private Date mDate=null;
	private String titleStr="";

	private Dialog mDialog=null;
	private View dlgContent=null;
	private Context mContext=null;

	public interface OnOkButtonFireListener{
		public void onOk(String resultStr);
	}
	public void setOkListener(OnOkButtonFireListener okListener) {
		this.okListener = okListener;
	}
	public DatePickerDialog(){}

	public static DatePickerDialog newInstance(Context ctx, String title){
		if(null==aDialog){
            aDialog=new DatePickerDialog();
		}
		aDialog.mContext=ctx;
		aDialog.titleStr=title;
		return aDialog;
	}

	public void showDialog(){
		if(!mDialog.isShowing()){
			mDialog.show();
			//positiveButton.requestFocus();
		}
	}
	public void initAndShow(String initVal){
		init(initVal);
		showDialog();
	}

	public void init(String initVal){
		final Calendar cal=Calendar.getInstance();
		final Calendar calNow=Calendar.getInstance();
		if(null==initVal||"".equals(initVal)){
			mDate=new Date();
		}else{
			mDate= DateUtil.parseDate(initVal, DateUtil.FORMAT_DATETIME);
			if(null==mDate)mDate=new Date();
		}
		cal.setTime(mDate);

		LayoutInflater inflater = LayoutInflater.from(mContext);
		if(null==dlgContent)
			dlgContent=inflater.inflate(R.layout.date_ymd_dialog, null);

		TextView txtTitle=(TextView)dlgContent.findViewById(R.id.title);
		if (!"".equals(titleStr)) {
			txtTitle.setText(titleStr);
		}
		positiveButton=(Button)dlgContent.findViewById(R.id.positiveButton);
		negativeButton=(ImageButton)dlgContent.findViewById(R.id.negativeButton);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Tools.hideKeyboard(mContext, dlgContent);
				mDialog.dismiss();
				okListener.onOk(getDateStr());
			}
		});
		negativeButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Tools.hideKeyboard(mContext, dlgContent);
				mDialog.dismiss();
			}
		});
		
		numY=(NumberPicker)dlgContent.findViewById(R.id.numberYear);
		//设置滑动监听
		numY.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			//当NunberPicker的值发生改变时，将会激发该方法
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				//String toast = "oldVal：" + oldVal + "   newVal：" + newVal;
				if(newVal>=calNow.get(Calendar.YEAR)){
					setNumberPicker(numM, 1, calNow.get(Calendar.MONTH)+ 1, cal.get(Calendar.MONTH) + 1);
					setNumberPicker(numD, 1, calNow.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_MONTH));
				}else{
					setNumberPicker(numM, 1, 12, cal.get(Calendar.MONTH) + 1);
					setNumberPicker(numD, 1, 31, cal.get(Calendar.DAY_OF_MONTH));
				}
			}
		});

		numM=(NumberPicker)dlgContent.findViewById(R.id.numberMonth);
		numD=(NumberPicker)dlgContent.findViewById(R.id.numberDay);

		//setNumberPickerDividerColor(numY);
		//setNumberPickerDividerColor(numM);
		//setNumberPickerDividerColor(numD);

		setNumberPicker(numY, calNow.get(Calendar.YEAR), calNow.get(Calendar.YEAR)+1, cal.get(Calendar.YEAR));
		setNumberPicker(numM, 1, 12, cal.get(Calendar.MONTH)+1);
		setNumberPicker(numD, 1, 31, cal.get(Calendar.DAY_OF_MONTH));


		numHH=(NumberPicker)dlgContent.findViewById(R.id.numberHH);
		numMM=(NumberPicker)dlgContent.findViewById(R.id.numberMM);
		numSS=(NumberPicker)dlgContent.findViewById(R.id.numberSS);
		//setNumberPickerDividerColor(numHH);
		//setNumberPickerDividerColor(numMM);
		setNumberPicker(numHH, 1, 24, cal.get(Calendar.HOUR_OF_DAY));
		setNumberPicker(numMM, 1, 59, cal.get(Calendar.MINUTE));
		setNumberPicker(numSS, 0, 59, cal.get(Calendar.SECOND));

		initNumberPicker();

        if(null==mDialog)
            mDialog = new Dialog(mContext, R.style.transparent);

        mDialog.setContentView(dlgContent, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
        mDialog.setCanceledOnTouchOutside(false);
    }
	private void setNumberPicker(NumberPicker picker,int minValue,int maxValue,int curValue){
		picker.setMaxValue(maxValue);
		picker.setMinValue(minValue);
		picker.setValue(curValue);
	}
	private void initNumberPicker(){
		edtListY=new ArrayList<EditText>();
		edtListM=new ArrayList<EditText>();
		edtListD=new ArrayList<EditText>();

		edtListHH=new ArrayList<EditText>();
		edtListMM=new ArrayList<EditText>();
		edtListSS=new ArrayList<EditText>();

		Tools.appendEditTextListRec(numY, edtListY);
		Tools.appendEditTextListRec(numM, edtListM);
		Tools.appendEditTextListRec(numD, edtListD);

		Tools.appendEditTextListRec(numHH, edtListHH);
		Tools.appendEditTextListRec(numMM, edtListMM);
		Tools.appendEditTextListRec(numSS, edtListSS);

		View.OnClickListener clickLinsten=new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v instanceof EditText){
					EditText edt=(EditText)v;
					edt.selectAll();
					Log.v("numberPicker", "onClick selectAll");
				}
			}
		};
		initNumberPickerImpl(numY, edtListY, clickLinsten, 
				EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		initNumberPickerImpl(numM, edtListM, clickLinsten, 
				EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		initNumberPickerImpl(numD, edtListD, clickLinsten, 
				EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);

		initNumberPickerImpl(numHH, edtListHH, clickLinsten,
				EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		initNumberPickerImpl(numMM, edtListMM, clickLinsten,
				EditorInfo.IME_ACTION_NEXT|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		initNumberPickerImpl(numSS, edtListSS, clickLinsten,
				EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI);
	}
	private void initNumberPickerImpl(
			final NumberPicker picker,
			List<EditText> edtList,
			View.OnClickListener clickLinsten,
			int imeOptions){
		for(EditText edt:edtList){
			edt.setImeOptions(imeOptions);
			if((EditorInfo.IME_ACTION_DONE|EditorInfo.IME_FLAG_NO_EXTRACT_UI)==imeOptions){
				edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						if(EditorInfo.IME_ACTION_DONE==actionId){
							EditText edt=(EditText)v;
							String valStr=edt.getText().toString();
							if("".equals(valStr))valStr="0";
							picker.setValue(Integer.parseInt(valStr));
							positiveButton.performClick();
							return true;
						}
						return false;
					}
				});
			}
		}
	}

	private String getDateStr(){
		
		
		StringBuilder bld=new StringBuilder();
		bld.append(getOneNumber(edtListY)).append("-")
				.append(getOneNumber(edtListM)).append("-")
				.append(getOneNumber(edtListD)).append(" ")
				.append(getOneNumber(edtListHH)).append(":")
				.append(getOneNumber(edtListMM)).append(":")
				.append(getOneNumber(edtListSS));
		Log.v("DatePicker", "getDateStr=" +bld.toString());

		String strDate = bld.toString();
		Date resultDate = DateUtil.parseDate(strDate, DateUtil.FORMAT_DATETIME);
		strDate = DateUtil.formatDate(resultDate, DateUtil.FORMAT_DATETIME);
		return strDate;
	}
	private String getOneNumber(List<EditText> edtList){
		String rt="";
		for(EditText edt:edtList){
			String strVal=edt.getText().toString();
			if(!"".equals(strVal)){
				rt=strVal;
				break;
			}
		}
		return rt;
	}
	
}
