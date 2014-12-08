package com.aofeng.wellbeing.modelview;

import gueei.binding.Command;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;
import gueei.binding.validation.validators.MaxLength;
import gueei.binding.validation.validators.RegexMatch;
import gueei.binding.validation.validators.Required;

import java.io.ObjectOutputStream.PutField;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aofeng.Zxing.Demo.CaptureActivity;
import com.aofeng.utils.HttpMultipartPost;
import com.aofeng.utils.Pair;
import com.aofeng.utils.ScrubblePane;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.activity.AutographActivity;
import com.aofeng.wellbeing.activity.CheckActivity;
import com.aofeng.wellbeing.activity.PurchaseHistoryActivity;
import com.aofeng.wellbeing.activity.ShootActivity;

/**
 * �뻧����model
 * 
 * @author yjs
 * 
 */
public class CheckModel {
	private final CheckActivity mContext;
	// �뻧����ƻ�ID
	private String indoorInpsectPlanID = "test";
	
	public CheckModel(CheckActivity Context) {
		this.mContext = Context;
		Bundle bundle = mContext.getIntent().getExtras();
		if (bundle != null)
			indoorInpsectPlanID = bundle.getString("ID");
	}	

	public Command UserInfoPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.userInfoPane);
		}
	};
	public Command GasAppliancesPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.gasAppliancesPane);
		}
	};
	public Command TightnessPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.tightnessPane);
		}
	};
	public Command IndoorFacilitiesPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.indoorFacilitiesPane);
		}
	};
	public Command GasEnvironmentPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.gasEnvironmentPane);
		}
	};
	public Command FaultPane = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			muteOthers(R.id.faultPane);
		}
	};
	public Command MeasurePane = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			muteOthers(R.id.measurePane);
		}
	};
	public Command AppraisalPane = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			muteOthers(R.id.appraisalPane);
		}
	};
	public Command CameraPane = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			muteOthers(R.id.cameraPane);
		}
	};
	public Command SignaturePane = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			muteOthers(R.id.signaturePane);
		}
	};
	public Command back = new Command() {
		
		@Override
		public void Invoke(View arg0, Object... arg1) {
			muteOthers(R.id.checkMenuPane);
		}
	};

	/**
	 * ���س�paneId�������LinearLayout
	 * 
	 * @param paneId
	 */
	public void muteOthers(int paneId) {
		int[] panes = {R.id.checkMenuPane, R.id.userInfoPane, R.id.gasAppliancesPane, R.id.tightnessPane, R.id.indoorFacilitiesPane, R.id.gasEnvironmentPane, R.id.faultPane, R.id.measurePane, R.id.appraisalPane, R.id.cameraPane, R.id.signaturePane};
		for(int i=0; i<panes.length; i++)
			if(paneId == panes[i])
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.VISIBLE);
			else
				mContext.findViewById(panes[i]).setVisibility(LinearLayout.GONE);
	}
	
	/**
	 * ���ر��氲���¼
	 */
	public Command saveInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Upload(false);
		}
	};

	/**
	 * �ϴ������¼
	 */
	public Command UploadInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Upload(true);
		}

	};
	
	public Command SearchByScan = new Command() {
		@Override
		public void Invoke(View arg0, Object... arg1) {
			Intent intent = new Intent(mContext, CaptureActivity.class);
			Bundle bundle = new Bundle();
			//bundle.putString("username", username);
			//TODO
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 130);
		}
	};

	/**
	 * �Ƿ���Ҫά��
	 * @return
	 */
	private boolean NeedsRepair() {
		return true;
	}
	
	/**
	 * ǩ��
	 */
	public Command sign = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent();
			// ���ð������ݲ�����Activity
			Bundle bundle = new Bundle();
			bundle.putString("ID", mContext.uuid + "_sign");
			intent.setClass(mContext, AutographActivity.class);
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 6);
		}
	};

	/**
	 * ���ر��沢�ϴ�
	 */
	private void Upload(boolean trueUpload) {
		boolean needsRepair = NeedsRepair();
		// �ϴ�ͼƬ
		ArrayList<String> imgs = new ArrayList<String>();
		if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png")) {
			imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png");
			imgs.add("ǩ��ͼƬ");
			imgs.add(mContext.uuid + "_sign");
		}
		for (int i = 1; i < 6; i++) {
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i + ".jpg")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i	+ ".jpg");
				imgs.add("������Ƭ" + i);
				imgs.add(mContext.uuid + "_" + i);
			}
		}

		//���ر���
		String row = mContext.SaveToJSONString(needsRepair, true);
		if(!mContext.Save(row, "T_INSPECTION",  false))
		{
			mContext.localSaved = false;
			Toast.makeText(mContext, "���氲���¼��ƽ��ʧ��!", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			mContext.localSaved = true;
			Toast.makeText(mContext, "���氲���¼��ƽ��ɹ�!", Toast.LENGTH_SHORT).show();
		}
		imgs.add(row);
		//����Ǳ��汾�أ���ȥУ��
		boolean needsValidation = trueUpload;
		//��������˻�ܼ죬Ҳ����Ҫ��֤
		needsValidation = true;
		String validationURL = Vault.IIS_URL + "CAValidate"; 
		HttpMultipartPost poster = new HttpMultipartPost(mContext, trueUpload, validationURL, needsValidation);
		poster.execute(imgs.toArray(new String[imgs.size()]));
	}
	
	
//�û�������Ϣ��
	//���
	public StringObservable f_securityid = new StringObservable("");
	//�û����
	public StringObservable f_userid = new StringObservable("");
	//�û�����
	public StringObservable f_username = new StringObservable("");
	//�طõ绰
	public StringObservable f_huifangtelephone = new StringObservable("");
	//��λ(��f_addressDBƴ��Ϊf_address)
	public String f_districtname = new String("");
	//��ַ(��f_districtnameƴ��Ϊf_address)
	public String f_addressDB = new String("");
	//סַ
	public StringObservable f_address = new StringObservable("");
	//��������
	public StringObservable f_securitydate = new StringObservable();
	//���
	public BooleanObservable f_kitchen = new BooleanObservable(false);
	//���Ʒ��
	public StringObservable f_kitchenbrand = new StringObservable("");
	//�������
	public ArrayListObservable<String> f_kitchensize = new ArrayListObservable<String>(String.class, new String[]{"", "��", "˫"});
	//��ˮ��
	public BooleanObservable f_waterheater = new BooleanObservable(false);
	//��ˮ��Ʒ��
	public StringObservable f_waterheaterbrand = new StringObservable("");
	//��ˮ������
	public StringObservable f_heatersize = new StringObservable("");
	//�ڹ�¯
	public BooleanObservable f_wallhangboiler = new BooleanObservable(false);
	//�ڹ�¯Ʒ��
	public StringObservable f_wallhangboilerbrand = new StringObservable("");
	//�ڹ�¯����
	public StringObservable f_handboilersize = new StringObservable("");
	//�����ͺ�
	public StringObservable f_metertype = new StringObservable("");
	//���ұ�
	public StringObservable f_aroundmeter = new StringObservable("");
	//���
	public StringObservable f_meternumber = new StringObservable("");
	//��������
	public StringObservable f_gasmeterstyle = new StringObservable("");
	//������
	public StringObservable f_gaswatchbrand = new StringObservable("");
	//����©��
	public BooleanObservable f_isbtlq = new BooleanObservable(false);
	//����©��
	public BooleanObservable f_isfmlq = new BooleanObservable(false);
	//�ܵ��ӿڴ�©��
	public BooleanObservable f_isgdjkclq = new BooleanObservable(false);
	//�继�ڴ���©�����󣬰�����Ա�����رձ�ǰ��������96777����
	public BooleanObservable f_ishnczlqxx = new BooleanObservable(false);
	//˽�Ļ���ȼ���ܵ�
	public BooleanObservable f_issghnrqgd = new BooleanObservable(false);
	//����96777����Ĺ�
	public BooleanObservable f_issqgg = new BooleanObservable(false);
	//����
	public BooleanObservable f_isbb = new BooleanObservable(false);
	//����
	public BooleanObservable f_isbg = new BooleanObservable(false);
	//����
	public BooleanObservable f_isbf = new BooleanObservable(false);
	//��ͨ��ף�ȷ����Ȼͨ��
	public BooleanObservable f_isktfk = new BooleanObservable(false);
	//������ʴ
	public BooleanObservable f_isbtxs = new BooleanObservable(false);
	//�ܵ���ʴ
	public BooleanObservable f_isgdxs = new BooleanObservable(false);
	//����96777����
	public BooleanObservable f_isbx = new BooleanObservable(false);
	//��ǰ������
	public BooleanObservable f_isbqrlj = new BooleanObservable(false);
	//���������
	public BooleanObservable f_isbhrlj = new BooleanObservable(false);
	//��ϵ�ҹ�˾ȼ���߷����������ģ��绰��029-33560008
	public BooleanObservable f_islxwgs = new BooleanObservable(false);
	//����ϻ�
	public BooleanObservable f_isrglh = new BooleanObservable(false);
	//��ܳ���
	public BooleanObservable f_isrgcc = new BooleanObservable(false);
	//��ܴ�ǽ���ء��Ŵ�
	public BooleanObservable f_isrgcqdmc = new BooleanObservable(false);
	//����޹ܿ�
	public BooleanObservable f_isrgwgk = new BooleanObservable(false);
	//�����޹ܿ�
	public BooleanObservable f_islgwgk = new BooleanObservable(false);
	//�������
	public BooleanObservable f_isghrg = new BooleanObservable(false);
	//�ƶ�ȼ����
	public BooleanObservable f_isydrqj = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg1 = new BooleanObservable(false);
	//��ܹܿ��ѷ���
	public BooleanObservable f_isrggkyff = new BooleanObservable(false);
	//˽����ˮ��
	public BooleanObservable f_issjrsq = new BooleanObservable(false);
	//˽�ӱڹ�¯
	public BooleanObservable f_issjbgl = new BooleanObservable(false);
	//˽����Ƿ�
	public BooleanObservable f_isyjf = new BooleanObservable(false);
	//˽������ͨ
	public BooleanObservable f_issjrst = new BooleanObservable(false);
	//��ϵ�ҹ�˾ȼ���߷����������ģ��绰��02933560008
	public BooleanObservable f_islxwgs1 = new BooleanObservable(false);
	//��װ���
	public BooleanObservable f_isgzzj = new BooleanObservable(false);
	//��װ��ˮ��
	public BooleanObservable f_isgzrsq = new BooleanObservable(false);
	//���������ȼ����
	public BooleanObservable f_isccghrqj = new BooleanObservable(false);
	//��ˮ�����̵����̵�δ�������
	public BooleanObservable f_isrsqwyd = new BooleanObservable(false);
	//��ˮ�������̻������̵�
	public BooleanObservable f_isrsqgyyd = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg2 = new BooleanObservable(false);
	//�ڹ�¯���̵����̵�δ�������
	public BooleanObservable f_isbglwyd = new BooleanObservable(false);
	//�ڹ�¯�����̻������̵�
	public BooleanObservable f_isbglgyyd = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg3 = new BooleanObservable(false);
	//�����������ϻ�Դ����
	public BooleanObservable f_isczlzyshyhy = new BooleanObservable(false);
	//�Ƴ�������Դ
	public BooleanObservable f_isycqthy = new BooleanObservable(false);
	//���ڿ���ʽ�����������޸���
	public BooleanObservable f_issykfscf = new BooleanObservable(false);
	//�����ѷ�����
	public BooleanObservable f_iscfdfzw = new BooleanObservable(false);
	//�ڳ����������װ����
	public BooleanObservable f_isjiageduan = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg6 = new BooleanObservable(false);
	//�ı������;
	public BooleanObservable f_isgbcfyt = new BooleanObservable(false);
	//�ı���������
	public BooleanObservable f_isgbyqxz = new BooleanObservable(false);
	//�ָ�������;
	public BooleanObservable f_ishfcfyt = new BooleanObservable(false);
	//����˾��������
	public BooleanObservable f_isdgsdtsq = new BooleanObservable(false);
	//���������ܱտռ����Ȼͨ���
	public BooleanObservable f_issymbkj = new BooleanObservable(false);
	//��Դ��ȼ���ܵ�������С��15cm
	public BooleanObservable f_isxyswlm = new BooleanObservable(false);
	//���ڵ��߲���
	public BooleanObservable f_isczdxcr = new BooleanObservable(false);
	//�������ģ�ȷ����Ȼͨ��
	public BooleanObservable f_isqbzrtf = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg4 = new BooleanObservable(false);
	//���������ȼ����ˮƽ������С��30cm
	public BooleanObservable f_iszjxysslm = new BooleanObservable(false);
	//��ˮ��������ȼ����ˮƽ������С��30cm
	public BooleanObservable f_isrsqxysslm = new BooleanObservable(false);
	//��������
	public BooleanObservable f_iszxzg5 = new BooleanObservable(false);
	//��ϵȼ���߷�����������33560008
	public BooleanObservable f_islxwgs2 = new BooleanObservable(false);
	//��������
	public BooleanObservable f_isjxsb = new BooleanObservable(false);
	//��������
	public BooleanObservable f_issqbj = new BooleanObservable(false);
	//��������
	public BooleanObservable f_isqlbf = new BooleanObservable(false);
	//�ظ��忨
	public BooleanObservable f_iscfck = new BooleanObservable(false);
	//�ϵ粻�ط�
	public BooleanObservable f_isddbgf = new BooleanObservable(false);
	//���ܵ���
	public BooleanObservable f_iszndq = new BooleanObservable(false);
	//Һ����ʾ����
	public BooleanObservable f_isyjxsgz = new BooleanObservable(false);
	//���ܲ�����
	public BooleanObservable f_isznbjq = new BooleanObservable(false);
	//����ڹ��ϣ��뱣�ֵ绰��ͨ���ҹ�˾����һ���ڽ���ά��
	public BooleanObservable f_isqbczgz = new BooleanObservable(false);
	//��������
	public StringObservable f_qtyh = new StringObservable("");
	//������
	public StringObservable oughtamount = new StringObservable("");
	//�۹�����
	public StringObservable f_cumulativepurchase = new StringObservable("");
	//Һ������(����)
	public StringObservable f_yejingdushu = new StringObservable("");
	//���γ���ָ��
	public StringObservable lastrecord = new StringObservable("");
	//�ϴγ���ָ��
	public StringObservable lastinputgasnum = new StringObservable("");
	//�������
	public StringObservable oughtfee = new StringObservable("");
	//������
	public StringObservable f_qiliangcha = new StringObservable("");
	//��������
	public StringObservable f_gaspricetype = new StringObservable("");
	//���ڽ�������
	public StringObservable f_zhyegas = new StringObservable("");
	//����Ƿ�ѽ��
	public StringObservable mustpaygasfee = new StringObservable("");
	//����Ƿ������
	public StringObservable mustpaygascount = new StringObservable("");
	//����
	public StringObservable f_gasprice = new StringObservable("");
	//���ڽ�������
	public StringObservable f_bczhyegas = new StringObservable("");
	//Ӧ�����
	public StringObservable yjfee = new StringObservable("");
	//Ӧ������
	public StringObservable yjgas = new StringObservable("");
	//�������
	public StringObservable f_gasmeteraccomodations = new StringObservable("");
	//�����뻧����
	public StringObservable f_bcrhrq = new StringObservable("");
	//����
	public BooleanObservable f_isjjrh = new BooleanObservable(false);
	//��ǩ
	public BooleanObservable f_isjjqz = new BooleanObservable(false);
	//��ע
	public StringObservable f_beizhu = new StringObservable("");
	//1���Ƿ�������н������°�ȫ��ʶ������1���˲���� 2�����ֹط��� 3������������4����֪��Ȼ������绰����96777��
	public BooleanObservable f_isaqcsxc = new BooleanObservable(false);
	//2���Ƿ����������Ȼ����ܽ�����ϸ���?
	public BooleanObservable f_iszxjc = new BooleanObservable(false);
	//3���Ƿ��ü�©�ǻ����ˮ�����е�ȼ�����Ǻ͸��ܵ��ӿڴ�������ȼ����ʩ���м�©�����̻���ȼ��й©����ȷ����취?
	public BooleanObservable f_isjlyhfzsjxjl = new BooleanObservable(false);
	//4���Ƿ���������������ϣ�
	public BooleanObservable f_isffxczl = new BooleanObservable(false);
	//5��������Ա�Ƿ������������1��2��3��4��5��6��7�����������ݽ���ϸ�¼�飬���Է��ֵ����⼰ʱ��֪����
	public BooleanObservable f_isajyzxjc = new BooleanObservable(false);
	//����
	public BooleanObservable f_ismanyi = new BooleanObservable(false);
	//������
	public BooleanObservable f_isbumanyi = new BooleanObservable(false);
	//�Ƿ����ĵ�
	public BooleanObservable f_isfzgd = new BooleanObservable(false);
	//��ȫ�ȼ�
	public StringObservable f_anjianprivious = new StringObservable("");
	//����
	public StringObservable f_yinhuan = new StringObservable("");
	//����
	public StringObservable f_guzhang = new StringObservable("");
	//�û����鼰���
	public StringObservable f_reconmegent = new StringObservable("");
	
}