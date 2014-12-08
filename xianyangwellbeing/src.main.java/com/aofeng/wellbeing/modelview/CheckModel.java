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
 * 入户安检model
 * 
 * @author yjs
 * 
 */
public class CheckModel {
	private final CheckActivity mContext;
	// 入户安检计划ID
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
	 * 隐藏除paneId外的所有LinearLayout
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
	 * 本地保存安检记录
	 */
	public Command saveInspectionRecord = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Upload(false);
		}
	};

	/**
	 * 上传安检记录
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
	 * 是否需要维修
	 * @return
	 */
	private boolean NeedsRepair() {
		return true;
	}
	
	/**
	 * 签名
	 */
	public Command sign = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			Intent intent = new Intent();
			// 利用包袱传递参数给Activity
			Bundle bundle = new Bundle();
			bundle.putString("ID", mContext.uuid + "_sign");
			intent.setClass(mContext, AutographActivity.class);
			intent.putExtras(bundle);
			mContext.startActivityForResult(intent, 6);
		}
	};

	/**
	 * 本地保存并上传
	 */
	private void Upload(boolean trueUpload) {
		boolean needsRepair = NeedsRepair();
		// 上传图片
		ArrayList<String> imgs = new ArrayList<String>();
		if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png")) {
			imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_sign.png");
			imgs.add("签名图片");
			imgs.add(mContext.uuid + "_sign");
		}
		for (int i = 1; i < 6; i++) {
			if (Util.fileExists(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i + ".jpg")) {
				imgs.add(Util.getSharedPreference(mContext, "FileDir") + mContext.uuid + "_" + i	+ ".jpg");
				imgs.add("隐患照片" + i);
				imgs.add(mContext.uuid + "_" + i);
			}
		}

		//本地保存
		String row = mContext.SaveToJSONString(needsRepair, true);
		if(!mContext.Save(row, "T_INSPECTION",  false))
		{
			mContext.localSaved = false;
			Toast.makeText(mContext, "保存安检记录到平板失败!", Toast.LENGTH_SHORT).show();
			return;
		}
		else
		{
			mContext.localSaved = true;
			Toast.makeText(mContext, "保存安检记录到平板成功!", Toast.LENGTH_SHORT).show();
		}
		imgs.add(row);
		//如果是保存本地，则不去校验
		boolean needsValidation = trueUpload;
		//如果是无人或拒检，也不需要验证
		needsValidation = true;
		String validationURL = Vault.IIS_URL + "CAValidate"; 
		HttpMultipartPost poster = new HttpMultipartPost(mContext, trueUpload, validationURL, needsValidation);
		poster.execute(imgs.toArray(new String[imgs.size()]));
	}
	
	
//用户基本信息绑定
	//编号
	public StringObservable f_securityid = new StringObservable("");
	//用户编号
	public StringObservable f_userid = new StringObservable("");
	//用户姓名
	public StringObservable f_username = new StringObservable("");
	//回访电话
	public StringObservable f_huifangtelephone = new StringObservable("");
	//单位(与f_addressDB拼接为f_address)
	public String f_districtname = new String("");
	//地址(与f_districtname拼接为f_address)
	public String f_addressDB = new String("");
	//住址
	public StringObservable f_address = new StringObservable("");
	//安检日期
	public StringObservable f_securitydate = new StringObservable();
	//灶具
	public BooleanObservable f_kitchen = new BooleanObservable(false);
	//灶具品牌
	public StringObservable f_kitchenbrand = new StringObservable("");
	//灶具容量
	public ArrayListObservable<String> f_kitchensize = new ArrayListObservable<String>(String.class, new String[]{"", "单", "双"});
	//热水器
	public BooleanObservable f_waterheater = new BooleanObservable(false);
	//热水器品牌
	public StringObservable f_waterheaterbrand = new StringObservable("");
	//热水器容量
	public StringObservable f_heatersize = new StringObservable("");
	//壁挂炉
	public BooleanObservable f_wallhangboiler = new BooleanObservable(false);
	//壁挂炉品牌
	public StringObservable f_wallhangboilerbrand = new StringObservable("");
	//壁挂炉容量
	public StringObservable f_handboilersize = new StringObservable("");
	//气表型号
	public StringObservable f_metertype = new StringObservable("");
	//左右表
	public StringObservable f_aroundmeter = new StringObservable("");
	//表号
	public StringObservable f_meternumber = new StringObservable("");
	//气表类型
	public StringObservable f_gasmeterstyle = new StringObservable("");
	//气表厂家
	public StringObservable f_gaswatchbrand = new StringObservable("");
	//表体漏气
	public BooleanObservable f_isbtlq = new BooleanObservable(false);
	//阀门漏气
	public BooleanObservable f_isfmlq = new BooleanObservable(false);
	//管道接口处漏气
	public BooleanObservable f_isgdjkclq = new BooleanObservable(false);
	//如户内存在漏气现象，安检人员立即关闭表前阀，拨打96777报修
	public BooleanObservable f_ishnczlqxx = new BooleanObservable(false);
	//私改户内燃气管道
	public BooleanObservable f_issghnrqgd = new BooleanObservable(false);
	//拨打96777申请改管
	public BooleanObservable f_issqgg = new BooleanObservable(false);
	//包表
	public BooleanObservable f_isbb = new BooleanObservable(false);
	//包管
	public BooleanObservable f_isbg = new BooleanObservable(false);
	//包阀
	public BooleanObservable f_isbf = new BooleanObservable(false);
	//开通风孔，确保自然通风
	public BooleanObservable f_isktfk = new BooleanObservable(false);
	//表体锈蚀
	public BooleanObservable f_isbtxs = new BooleanObservable(false);
	//管道锈蚀
	public BooleanObservable f_isgdxs = new BooleanObservable(false);
	//拨打96777报修
	public BooleanObservable f_isbx = new BooleanObservable(false);
	//表前软连接
	public BooleanObservable f_isbqrlj = new BooleanObservable(false);
	//表后软连接
	public BooleanObservable f_isbhrlj = new BooleanObservable(false);
	//联系我公司燃气具服务中心整改，电话：029-33560008
	public BooleanObservable f_islxwgs = new BooleanObservable(false);
	//软管老化
	public BooleanObservable f_isrglh = new BooleanObservable(false);
	//软管超长
	public BooleanObservable f_isrgcc = new BooleanObservable(false);
	//软管穿墙、地、门窗
	public BooleanObservable f_isrgcqdmc = new BooleanObservable(false);
	//软管无管卡
	public BooleanObservable f_isrgwgk = new BooleanObservable(false);
	//立杠无管卡
	public BooleanObservable f_islgwgk = new BooleanObservable(false);
	//更换软管
	public BooleanObservable f_isghrg = new BooleanObservable(false);
	//移动燃气具
	public BooleanObservable f_isydrqj = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg1 = new BooleanObservable(false);
	//软管管卡已发放
	public BooleanObservable f_isrggkyff = new BooleanObservable(false);
	//私接热水器
	public BooleanObservable f_issjrsq = new BooleanObservable(false);
	//私接壁挂炉
	public BooleanObservable f_issjbgl = new BooleanObservable(false);
	//私接羊角阀
	public BooleanObservable f_isyjf = new BooleanObservable(false);
	//私接软三通
	public BooleanObservable f_issjrst = new BooleanObservable(false);
	//联系我公司燃气具服务中心整改，电话：02933560008
	public BooleanObservable f_islxwgs1 = new BooleanObservable(false);
	//改装灶具
	public BooleanObservable f_isgzzj = new BooleanObservable(false);
	//改装热水器
	public BooleanObservable f_isgzrsq = new BooleanObservable(false);
	//拆除、更换燃气具
	public BooleanObservable f_isccghrqj = new BooleanObservable(false);
	//热水器无烟道或烟道未伸出户外
	public BooleanObservable f_isrsqwyd = new BooleanObservable(false);
	//热水器和油烟机共用烟道
	public BooleanObservable f_isrsqgyyd = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg2 = new BooleanObservable(false);
	//壁挂炉无烟道或烟道未伸出户外
	public BooleanObservable f_isbglwyd = new BooleanObservable(false);
	//壁挂炉和油烟机共用烟道
	public BooleanObservable f_isbglgyyd = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg3 = new BooleanObservable(false);
	//存在两种以上火源混用
	public BooleanObservable f_isczlzyshyhy = new BooleanObservable(false);
	//移除其他火源
	public BooleanObservable f_isycqthy = new BooleanObservable(false);
	//属于开发式厨房，厨房无隔断
	public BooleanObservable f_issykfscf = new BooleanObservable(false);
	//厨房堆放杂物
	public BooleanObservable f_iscfdfzw = new BooleanObservable(false);
	//在厨房与客厅加装隔断
	public BooleanObservable f_isjiageduan = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg6 = new BooleanObservable(false);
	//改变厨房用途
	public BooleanObservable f_isgbcfyt = new BooleanObservable(false);
	//改变用气性质
	public BooleanObservable f_isgbyqxz = new BooleanObservable(false);
	//恢复厨房用途
	public BooleanObservable f_ishfcfyt = new BooleanObservable(false);
	//到公司大厅申请
	public BooleanObservable f_isdgsdtsq = new BooleanObservable(false);
	//厨房属于密闭空间或自然通风差
	public BooleanObservable f_issymbkj = new BooleanObservable(false);
	//电源与燃气管道净距离小于15cm
	public BooleanObservable f_isxyswlm = new BooleanObservable(false);
	//存在电线缠绕
	public BooleanObservable f_isczdxcr = new BooleanObservable(false);
	//自行整改，确保自然通风
	public BooleanObservable f_isqbzrtf = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg4 = new BooleanObservable(false);
	//灶具立杠与燃器具水平净距离小于30cm
	public BooleanObservable f_iszjxysslm = new BooleanObservable(false);
	//热水器立杠与燃器具水平净距离小于30cm
	public BooleanObservable f_isrsqxysslm = new BooleanObservable(false);
	//自行整改
	public BooleanObservable f_iszxzg5 = new BooleanObservable(false);
	//联系燃气具服务中心整改33560008
	public BooleanObservable f_islxwgs2 = new BooleanObservable(false);
	//机表死表
	public BooleanObservable f_isjxsb = new BooleanObservable(false);
	//输气不进
	public BooleanObservable f_issqbj = new BooleanObservable(false);
	//气量不符
	public BooleanObservable f_isqlbf = new BooleanObservable(false);
	//重复插卡
	public BooleanObservable f_iscfck = new BooleanObservable(false);
	//断电不关阀
	public BooleanObservable f_isddbgf = new BooleanObservable(false);
	//智能掉气
	public BooleanObservable f_iszndq = new BooleanObservable(false);
	//液晶显示故障
	public BooleanObservable f_isyjxsgz = new BooleanObservable(false);
	//智能不减气
	public BooleanObservable f_isznbjq = new BooleanObservable(false);
	//如存在故障，请保持电话畅通，我公司将在一周内进行维护
	public BooleanObservable f_isqbczgz = new BooleanObservable(false);
	//其他隐患
	public StringObservable f_qtyh = new StringObservable("");
	//用气量
	public StringObservable oughtamount = new StringObservable("");
	//累购气量
	public StringObservable f_cumulativepurchase = new StringObservable("");
	//液晶读数(卡表)
	public StringObservable f_yejingdushu = new StringObservable("");
	//本次抄表指数
	public StringObservable lastrecord = new StringObservable("");
	//上次抄表指数
	public StringObservable lastinputgasnum = new StringObservable("");
	//用气金额
	public StringObservable oughtfee = new StringObservable("");
	//气量差
	public StringObservable f_qiliangcha = new StringObservable("");
	//气价类型
	public StringObservable f_gaspricetype = new StringObservable("");
	//上期结余气量
	public StringObservable f_zhyegas = new StringObservable("");
	//上期欠费金额
	public StringObservable mustpaygasfee = new StringObservable("");
	//上期欠费气量
	public StringObservable mustpaygascount = new StringObservable("");
	//气价
	public StringObservable f_gasprice = new StringObservable("");
	//本期结余气量
	public StringObservable f_bczhyegas = new StringObservable("");
	//应交金额
	public StringObservable yjfee = new StringObservable("");
	//应交气量
	public StringObservable yjgas = new StringObservable("");
	//气表底数
	public StringObservable f_gasmeteraccomodations = new StringObservable("");
	//本次入户日期
	public StringObservable f_bcrhrq = new StringObservable("");
	//拒入
	public BooleanObservable f_isjjrh = new BooleanObservable(false);
	//拒签
	public BooleanObservable f_isjjqz = new BooleanObservable(false);
	//备注
	public StringObservable f_beizhu = new StringObservable("");
	//1、是否对您家中进行以下安全常识宣传？1）人不离火； 2）随手关阀； 3）常开窗户；4）告知天然气服务电话号码96777；
	public BooleanObservable f_isaqcsxc = new BooleanObservable(false);
	//2、是否对您家中天然气软管进行仔细检查?
	public BooleanObservable f_iszxjc = new BooleanObservable(false);
	//3、是否用检漏仪或肥皂水对所有的燃气阀们和各管道接口处及其它燃气设施进行检漏，并教会您燃气泄漏后正确处理办法?
	public BooleanObservable f_isjlyhfzsjxjl = new BooleanObservable(false);
	//4、是否对您发放宣传资料？
	public BooleanObservable f_isffxczl = new BooleanObservable(false);
	//5、安检人员是否按照上述“序号1、2、3、4、5、6、7”七项检查内容进行细致检查，并对发现的问题及时告知您？
	public BooleanObservable f_isajyzxjc = new BooleanObservable(false);
	//满意
	public BooleanObservable f_ismanyi = new BooleanObservable(false);
	//不满意
	public BooleanObservable f_isbumanyi = new BooleanObservable(false);
	//是否发整改单
	public BooleanObservable f_isfzgd = new BooleanObservable(false);
	//安全等级
	public StringObservable f_anjianprivious = new StringObservable("");
	//隐患
	public StringObservable f_yinhuan = new StringObservable("");
	//故障
	public StringObservable f_guzhang = new StringObservable("");
	//用户建议及意见
	public StringObservable f_reconmegent = new StringObservable("");
	
}