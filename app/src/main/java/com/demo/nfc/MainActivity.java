package com.demo.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.nfc.activity.LogicActivity;
import com.demo.nfc.utils.CardUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	private static final String TAG = MainActivity.class.getSimpleName();

	Button btn_readCarNo, btn_sell, btn_pay, btn_charge;
	TextView tv_text;

	private String cardNo = null;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_main);
		btn_readCarNo = (Button) findViewById(R.id.btn_readCarNo);
		btn_sell = (Button) findViewById(R.id.btn_sell);
		btn_pay = (Button) findViewById(R.id.btn_pay);
		btn_charge = (Button) findViewById(R.id.btn_charge);
		tv_text = (TextView) findViewById(R.id.tv_text);

		btn_readCarNo.setOnClickListener(this);
		btn_sell.setOnClickListener(this);
		btn_pay.setOnClickListener(this);
		btn_charge.setOnClickListener(this);

	}

	@Override
	public void onClick(View view) {
		int flag = -1;
		Intent intent = new Intent(MainActivity.this, LogicActivity.class);
		switch (view.getId()){
			case R.id.btn_readCarNo:
				flag = CardUtil.TAG_READ_CARD_NO;
				break;
			case R.id.btn_sell:
				flag = CardUtil.TAG_SELL;

				intent.putExtra(CardUtil.TAG_CARD_NO, "ZTE2201611081557");
				intent.putExtra(CardUtil.TAG_CARD_TYPE, "00");
				intent.putExtra(CardUtil.TAG_CARD_COUNT, "100");
				intent.putExtra(CardUtil.TAG_CARD_DATE, "2016110816");

				break;
			case R.id.btn_pay:
				flag = CardUtil.TAG_PAY;
				break;
			case R.id.btn_charge:
				if (cardNo != null){
					flag = CardUtil.TAG_CHARGE;
					intent.putExtra(CardUtil.TAG_CARD_NO, cardNo);
					intent.putExtra(CardUtil.TAG_CARD_COUNT, "120");
				}else {
					Toast.makeText(MainActivity.this, "请先刷卡获取卡号！", Toast.LENGTH_SHORT).show();
				}
				break;
		}

		intent.putExtra(CardUtil.TAG_FROM, flag);
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null){
			String metoInfo = "";
			switch (resultCode){
				case CardUtil.TAG_CHARGE:
					if (data.getStringExtra(CardUtil.TAG_CARD_CHARGE).equals("0")){
						metoInfo = "充值成功！";
					}else {
						metoInfo = "充值失败！";
					}
					break;
				case CardUtil.TAG_READ_CARD_NO:
					if (!data.getBooleanExtra(CardUtil.TAG_BLANK_CARD, true)){
						metoInfo = "不是白卡！";
						break;
					}
					metoInfo = "cardDefaultNo：" + data.getStringExtra(CardUtil.TAG_CARD_DEFAULT_NO);
					break;
				case CardUtil.TAG_PAY:
					cardNo = data.getStringExtra(CardUtil.TAG_CARD_NO);

					metoInfo += "cardNo: " + cardNo + "\n";
					metoInfo += "cardCount: " + data.getStringExtra(CardUtil.TAG_CARD_COUNT) + "\n";
					metoInfo += "cardDate: " + data.getStringExtra(CardUtil.TAG_CARD_DATE) + "\n";

					break;
				case CardUtil.TAG_SELL:
					if (data.getStringExtra(CardUtil.TAG_CARD_MAKE).equals("0")){
						metoInfo = "制卡成功！";
					}else {
						metoInfo = "制卡失败！";
					}
					break;
			}
			Log.i(TAG, metoInfo);
			tv_text.setText(metoInfo);
		}
	}
}
