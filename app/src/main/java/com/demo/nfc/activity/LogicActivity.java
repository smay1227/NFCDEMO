package com.demo.nfc.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.nfc.R;
import com.demo.nfc.utils.CardUtil;
import com.demo.nfc.utils.IcCard;
import com.demo.nfc.utils.MifareControl;

public class LogicActivity extends Activity {
	private static final String TAG = LogicActivity.class.getSimpleName();

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private TextView mText;
	private int mFrom = -1;
	private String cardNo, cardType, cardCount, cardDate;
	private Intent mIntent = null;

	@Override
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_logic);
		mText = (TextView) findViewById(R.id.text);
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		// Create a generic PendingIntent that will be deliver
		// to this activity. The NFC stack
		// will fill in the intent with the details of the
		// discovered tag before delivering to
		// this activity.
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Setup an intent filter for all MIME based dispatches
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[]{ndef,};
		// Setup a tech list for all MifareClassic tags
		mTechLists = new String[][]{new String[]{MifareClassic.class
				.getName()}};

		mFrom = getIntent().getIntExtra(CardUtil.TAG_FROM, -1);
		cardNo = getIntent().getStringExtra(CardUtil.TAG_CARD_NO);
		cardType = getIntent().getStringExtra(CardUtil.TAG_CARD_TYPE);
		cardCount = getIntent().getStringExtra(CardUtil.TAG_CARD_COUNT);
		cardDate = getIntent().getStringExtra(CardUtil.TAG_CARD_DATE);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
				mTechLists);

		processIntent(mIntent);
	}

	@Override
	public void onNewIntent(Intent intent) {
		mIntent = intent;
	}

	private void processIntent(Intent intent){
		if (mFrom == -1){
			finish();
			return;
		}
		if (mIntent == null){
			return;
		}
		//取出封装在intent中的TAG
		Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		for (String tech : tagFromIntent.getTechList()) {
			System.out.println(tech);
		}

		MifareControl mc = new MifareControl(tagFromIntent);

		try {
			Intent intentResult = new Intent();
			switch (mFrom){
				case CardUtil.TAG_CHARGE:
					Log.i("LogicActivity", "TAG_CHARGE");

					mc.updateRemainCount(cardNo, cardCount);
					intentResult.putExtra(CardUtil.TAG_CARD_CHARGE, "0");
					setResult(CardUtil.TAG_CHARGE, intentResult);
					finish();

					break;
				case CardUtil.TAG_PAY:
					Log.i("LogicActivity", "TAG_PAY");

					IcCard icCard = mc.readIcCard();

					intentResult.putExtra(CardUtil.TAG_CARD_NO, icCard.card_no);
					intentResult.putExtra(CardUtil.TAG_CARD_COUNT, icCard.card_remain);
					intentResult.putExtra(CardUtil.TAG_CARD_DATE, icCard.card_date);
					setResult(CardUtil.TAG_PAY, intentResult);
					finish();

					break;
				case CardUtil.TAG_READ_CARD_NO:
					Log.i("LogicActivity", "TAG_READ_CARD_NO");

					if (!mc.isBlankCard()){
						intentResult.putExtra(CardUtil.TAG_BLANK_CARD, false);
						setResult(CardUtil.TAG_READ_CARD_NO, intentResult);
						finish();
						break;
					}

					String defaultCarNo = mc.readDefaultCarCardNo();
					Log.i("LogicActivity", ">>>>>>>>>>defaultCarNo: " + defaultCarNo);

					intentResult.putExtra(CardUtil.TAG_CARD_DEFAULT_NO, defaultCarNo);
					setResult(CardUtil.TAG_READ_CARD_NO, intentResult);
					finish();

					break;
				case CardUtil.TAG_SELL:
					Log.i("LogicActivity", "TAG_SELL");

					IcCard icCardSell = new IcCard();
					icCardSell.card_no = cardNo;
					icCardSell.type = cardType;
					icCardSell.card_remain = cardCount;
					icCardSell.card_date = cardDate;

					mc.makeCarCard(icCardSell);

					intentResult.putExtra(CardUtil.TAG_CARD_MAKE, "0");
					setResult(CardUtil.TAG_SELL, intentResult);
					finish();

					break;
				case -1:
					Log.i("LogicActivity", "-1");
					finish();
					break;
			}

			mc.close();

		} catch (Exception e) {
			Toast.makeText(LogicActivity.this, "读取车主卡失败", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mAdapter.disableForegroundDispatch(this);
	}
}
