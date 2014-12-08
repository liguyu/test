package com.aofeng.wellbeing.modelview;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.aofeng.utils.RemoteReader;
import com.aofeng.utils.RemoteReaderListener;
import com.aofeng.utils.Util;
import com.aofeng.utils.Vault;
import com.aofeng.wellbeing.R;
import com.aofeng.wellbeing.activity.SetupActivity;

import gueei.binding.Command;
import gueei.binding.converters.FALSE;
import gueei.binding.observables.StringObservable;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class SetupModel {
	private SetupActivity mContext;

	public SetupModel(SetupActivity context) {
		this.mContext = context;
		UseName.set(Util.getSharedPreference(mContext, Vault.USER_NAME));
	}
	
	//执行参数更新命令
	public Command UpdateParam = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "请等待上次操作完成。", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			if (!(Util.fileExists(mContext.getDatabasePath("safecheck.db")))) 
				GetRepairManList(true);
			else
				GetRepairManList(false);
		}
	};

	//执行系统初始化命令
	public Command Init = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "请等待上次操作完成。", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			GetRepairManList(true);
		}
	};

	private void GetRepairManList(final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select ID, NAME, ENAME from t_user where instr(roles,(select id from t_role where NAME='维修人员'),1)>0");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				ArrayList<RepairMan> RepairManList =new ArrayList<RepairMan>(); 
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					RepairMan rm = new RepairMan();
					rm.name = (String)aMap.get("col1");
					rm.id = (String)aMap.get("col0");
					RepairManList.add(rm);
				}
				GetParamList(RepairManList, toCreateDB);
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				mContext.isBusy = false;
				Toast.makeText(mContext, "获取维修人员失败，请检查网络连接。", Toast.LENGTH_SHORT).show();
			}

		});
		reader.start();
	}

	/**
	 * 得到参数列表
	 * @param repairManList
	 */
	private void GetParamList(final ArrayList<RepairMan> repairManList, final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select v.id id, p.Name code, v.Name name from T_PARAMETER p, T_PARAMVALUE v where p.ID = v.PROCESSID"
				+ " union select id, Value code, Name name from T_SINGLEVALUE");
		reader.setRemoteReaderListener(new RemoteReaderListener() {

			@Override
			public void onSuccess(List<Map<String, Object>> map) {
				super.onSuccess(map);
				if(toCreateDB)
					CreateDatabase(repairManList, map);
				else
					UpdateParam(repairManList, map);
				mContext.isBusy = false;
			}

			@Override
			public void onFailure(String errMsg) {
				super.onFailure(errMsg);
				Toast.makeText(mContext, "获取系统参数失败，请检查网络连接。", Toast.LENGTH_SHORT).show();
				mContext.isBusy = false;
			}

		});
		reader.start();		
	}
	
	private void CreateDatabase(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
			try {
				//建立数据库
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				db.execSQL("DROP TABLE IF EXISTS t_version");
				String   sql = "CREATE TABLE t_version (" +
						"id VARCHAR PRIMARY KEY, " +
						"ver integer )";
				db.execSQL(sql);
				sql = "insert into t_version values('1', " + Util.getVersionCode(mContext) + ")";
				db.execSQL(sql);
				//创建计划表
				db.execSQL("DROP TABLE IF EXISTS t_checkplan");
				sql = "CREATE TABLE t_checkplan (" +
						"id VARCHAR PRIMARY KEY, " +
						"f_date varchar," +            //安检计划开始执行日期
						"f_name VARCHAR," +            //安检计划名字
						"f_enddate VARCHAR," +         //安检计划结束日期
						"f_checkman VARCHAR)";         //安检员名字
				db.execSQL(sql);
				//创建安检单
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_PAPAER");
				sql = "CREATE TABLE T_IC_SAFECHECK_PAPAER (" +
						"id VARCHAR(80) PRIMARY KEY, " +
						" CONDITION            varchar(80)                    null," +             //检查情况
						"HasNotified				varchar(80)						null,"+				//到访不遇卡
						" USER_NAME            varchar(80)                    null," +             	 	//用户名称
						" TELPHONE             varchar(60)                   	 null," +          		//电话
						"ARRIVAL_TIME       varchar(80)                    	 null,"+			       //到达时间
						"DEPARTURE_TIME   varchar(80)             	    null,"+		   		   //离开时间
//						" ROAD                 varchar(80)                    		null," +                  	//街道
//						" UNIT_NAME            varchar(80)                    null," +          			//小区
//						" CUS_DOM              varchar(20)                    null," +           			//楼号
//						" CUS_DY               varchar(20)                 		   null," +               			//单元
//						" CUS_FLOOR            varchar(20)                    null," +        		//楼层
//						" CUS_ROOM             varchar(20)                    null," +       		//房号
						" DISTRICTNAME             varchar(20)                    null," +         //单位
						" ADDRESS             varchar(20)                    null," +              //地址
						" OLD_ADDRESS          varchar(500)                 null," +  			//用户档案地址

					  " ROOM_STRUCTURE       varchar(80)                    null," +   	//房屋结构
					  " WARM                 varchar(80)                    null," +                		 //供暖方式
					  " SAVE_PEOPLE          varchar(20)                    null," +     			//安检员

					  " IC_METER_NAME        varchar(20)                    null," +        //IC卡表厂名称
					  " JB_METER_NAME        varchar(20)                    null," +       //基表厂家名称
					  "METER_TYPE                varchar(80)                      null,"+			//表型
					  "  CARD_ID            varchar(80)                    null," +             	 	//卡号
					  " JB_NUMBER            integer                        null," +          		//基表数
					  " SURPLUS_GAS          integer                        null," +              //剩余气量

					  " RQB_AROUND           varchar(80)                    null," +		//燃气表左右表
					  " RQB_JBCODE           varchar(80)                    null," +			//燃气表基表号
					  "METERMADEYEAR   varchar(80)              null,"	+			//燃气表生产年份
					  " RQB                  varchar(80)                    null," +						//	燃气表

					  " STANDPIPE            varchar(80)                    null," +				//立管
					  " RIGIDITY             varchar(80)                    null," +					//严密性测试
					  " STATIC               varchar(80)                    null," +					//静止压力
					  " STATIC_DATA          varchar(80)                    null," +			//静止压力值
					  " TABLE_TAP            varchar(80)                    null," +				//表前阀
					  " COOK_TAP             varchar(80)                    null," +				//灶前阀
					  " CLOSE_TAP            varchar(80)                    null," +				//自闭阀
					  " INDOOR               varchar(80)                    null," +				//户内管
					  "LEAKAGE_COOKER                       varchar(80)                   null,"+					//灶具漏气
					  "LEAKAGE_HEATER                       varchar(80)                   null,"+					//热水器漏气
					  "LEAKAGE_BOILER                      varchar(80)                   null,"+					//壁挂炉漏气
					  "LEAKAGE_NOTIFIED                     varchar(80)                   null,"+					//安检告知书
					  "LEAKGEPLACE    varchar(80)						null,"+					//漏气位置

					  " COOK_BRAND           varchar(80)                    null," +			//灶具品牌
					  "COOK_TYPE        			varchar(80)					null,"	+			//灶具型号
					  "COOKPIPE_NORMAL               	varchar(80)                 null," 	+ 			//灶具软管
					  "COOKERPIPECLAMPCOUNT             varchar(80)  					null, "	+			//安装管卡个数
					  "COOKERPIPYLENGTH				varchar(80)					null,"  +			//更换软管长度
					  "COOK_DATE            varchar(80)                    null," +			//灶具购置日期

					  "WATER_BRAND          varchar(80)                    null," +		//热水器品牌
					  "WATER_TYPE          varchar(80)                    null,"+				//热水器型号
					  "WATER_PIPE          varchar(80)                    null,"+	 			//热水器软管
					  "WATER_FLUE          varchar(80)                    null,"+				//热水器烟道
					  "WATER_NUME          varchar(80)                    null,"+				//更换管卡数
					  "WATER_DATE           varchar(80)                    null," +			//热水器购置日期
					  "WATER_HIDDEN         varchar(80)                    null," +		//热水器隐患

					  " WHE_BRAND            varchar(80)                    null," +			//	壁挂炉品牌
					  " WHE_TYPE            varchar(80)                    null," +	  		//壁挂炉型号
					  " WHE_DATE             varchar(80)                    null," +			//壁挂炉购置日期
					  " WHE_HIDDEN         varchar(80)                    null," +		//壁挂楼隐患

 					 " USER_SUGGESTION             varchar(80)         null," +			//客户意见
 					 " USER_SATISFIED             varchar(80)                 null,"	+			//客户满意度
 					 " USER_SIGN             varchar(80)                    		null," +			//客户签名

					  "THREAT            	  varchar(80)                    					null,"	+			//隐患
					  "PHOTO_FIRST           	  varchar(80)                    		null,"	+	  			//照片1
					  "PHOTO_SECOND           	  varchar(80)                    	null,"	+			//照片2
					  "PHOTO_THIRD           	  varchar(80)                    		null,"	+				//照片3
					  "PHOTO_FOUTH           	  varchar(80)                    	null,"	+			//照片4
					  "PHOTO_FIFTH        	  varchar(80)                    		null,"	+	 			//照片5
					  "NEEDS_REPAIR        	  varchar(80)                    		null,"	+	 			//是否需要维修
					  "REPAIRMAN        	  varchar(80)                    		null,"	+	 			//维修人
					  "REPAIRMAN_ID        	  varchar(80)                    		null,"	+	 			//维修人ID
					  "REPAIR_STATE	     varchar(80)                  null," +              //维修状态
					  "CHECKPLAN_ID VARCHAR(80) null)";                                    //安检计划ID
				db.execSQL(sql);
				//入户安检表
				db.execSQL("DROP TABLE IF EXISTS T_INSPECTION");
				sql = "CREATE TABLE T_INSPECTION ("		+
						"ID  					TEXT(255) 				NOT NULL,"	+
						"f_securityid			TEXT(255)						,"	+			//安检单编号
						"f_userid				TEXT(255)						,"	+			//用户ID号
						"f_username				TEXT(255)						,"	+			//用户姓名
						"f_huifangtelephone		TEXT(255)						,"	+			//回访电话
						"f_address				TEXT(255)						,"	+			//住址
						"f_securitydate			TEXT(20)						,"	+			//安检日期
						"f_opratedate			TEXT(20)						,"	+			//操作日期
						"f_kitchen				REAL(1)							,"	+			//灶具
						"f_kitchenbrand			TEXT(255)						,"	+			//灶具品牌
						"f_kitchensize			TEXT(20)						,"	+			//灶具容量
						"f_waterheater			REAL(1)							,"	+			//热水器
						"f_waterheaterbrand		TEXT(255)						,"	+			//热水器品牌
						"f_heatersize			TEXT(20)						,"	+			//热水器容量
						"f_wallhangboiler		REAL(1)							,"	+			//壁挂炉
						"f_wallhangboilerbrand	TEXT(255)						,"	+			//壁挂炉品牌
						"f_handboilersize		TEXT(10)						,"	+			//壁挂炉容量
						"f_metertype			TEXT(20)						,"	+			//气表型号
						"f_aroundmeter			TEXT(10)						,"	+			//左右表
						"f_meternumber			TEXT(20)						,"	+			//表号
						"f_gasmeterstyle		TEXT(20)						,"	+			//气表类型
						"f_gaswatchbrand		TEXT(20)						,"	+			//气表厂家
						"f_isbtlq				REAL(1)							,"	+			//表体漏气
						"f_isfmlq				REAL(1)							,"	+			//阀门漏气
						"f_isgdjkclq			REAL(1)							,"	+			//管道接口处漏气
						"f_ishnczlqxx			REAL(1)							,"	+			//如户内存在漏气现象，安检人员立即关闭表前阀，拨打96777报修
						"f_issghnrqgd			REAL(1)							,"	+			//私改户内燃气管道
						"f_issqgg				REAL(1)							,"	+			//拨打96777申请改管
						"f_isbb					REAL(1)							,"	+			//包表
						"f_isbg					REAL(1)							,"	+			//包管
						"f_isbf					REAL(1)							,"	+			//包阀
						"f_isktfk				REAL(1)							,"	+			//开通风孔，确保自然通风
						"f_isbtxs				REAL(1)							,"	+			//表体锈蚀
						"f_isgdxs				REAL(1)							,"	+			//管道锈蚀
						"f_isbx					REAL(1)							,"	+			//拨打96777报修
						"f_isbqrlj				REAL(1)							,"	+			//表前软连接
						"f_isbhrlj				REAL(1)							,"	+			//表后软连接
						"f_islxwgs				REAL(1)							,"	+			//联系我公司燃气具服务中心整改，电话：029-33560008
						"f_isrglh				REAL(1)							,"	+			//软管老化
						"f_isrgcc				REAL(1)							,"	+			//软管超长
						"f_isrgcqdmc			REAL(1)							,"	+			//软管穿墙、地、门窗
						"f_isrgwgk				REAL(1)							,"	+			//软管无管卡
						"f_islgwgk				REAL(1)							,"	+			//立杠无管卡
						"f_isghrg				REAL(1)							,"	+			//更换软管
						"f_isydrqj				REAL(1)							,"	+			//移动燃气具
						"f_iszxzg1				REAL(1)							,"	+			//自行整改
						"f_isrggkyff			REAL(1)							,"	+			//软管管卡已发放
						"f_issjrsq				REAL(1)							,"	+			//私接热水器
						"f_issjbgl				REAL(1)							,"	+			//私接壁挂炉
						"f_isyjf				REAL(1)							,"	+			//私接羊角阀
						"f_issjrst				REAL(1)							,"	+			//私接软三通
						"f_islxwgs1				REAL(1)							,"	+			//联系我公司燃气具服务中心整改，电话：02933560008
						"f_isgzzj				REAL(1)							,"	+			//改装灶具
						"f_isgzrsq				REAL(1)							,"	+			//改装热水器
						"f_isccghrqj			REAL(1)							,"	+			//拆除、更换燃气具
						"f_isrsqwyd				REAL(1)							,"	+			//热水器无烟道或烟道未伸出户外
						"f_isrsqgyyd			REAL(1)							,"	+			//热水器和油烟机共用烟道
						"f_iszxzg2				REAL(1)							,"	+			//自行整改
						"f_isbglwyd				REAL(1)							,"	+			//壁挂炉无烟道或烟道未伸出户外
						"f_isbglgyyd			REAL(1)							,"	+			//壁挂炉和油烟机公用烟道
						"f_iszxzg3				REAL(1)							,"	+			//自行整改
						"f_isczlzyshyhy			REAL(1)							,"	+			//存在两种以上火源混用
						"f_isycqthy				REAL(1)							,"	+			//移动其他火源
						"f_issykfscf			REAL(1)							,"	+			//属于开放式厨房、厨房无隔断
						"f_iscfdfzw				REAL(1)							,"	+			//厨房堆放杂物
						"f_isjiageduan			REAL(1)							,"	+			//在厨房与客厅加装隔断
						"f_iszxzg6				REAL(1)							,"	+			//自行整改
						"f_isgbcfyt				REAL(1)							,"	+			//改变厨房用途
						"f_isgbyqxz				REAL(1)							,"	+			//改变用气性质
						"f_ishfcfyt				REAL(1)							,"	+			//恢复厨房用途
						"f_isdgsdtsq			REAL(1)							,"	+			//到公司大厅申请
						"f_issymbkj				REAL(1)							,"	+			//厨房属于密闭空间或自然通风差
						"f_isxyswlm				REAL(1)							,"	+			//电源与燃气管道净距离小于15cm
						"f_isczdxcr				REAL(1)							,"	+			//存在电线缠绕
						"f_isqbzrtf				REAL(1)							,"	+			//自行整改,确保自然通风
						"f_iszxzg4				REAL(1)							,"	+			//自行整改
						"f_iszjxysslm			REAL(1)							,"	+			//灶具立杠与燃器具水平净距离小于30cm
						"f_isrsqxysslm			REAL(1)							,"	+			//热水器立杠与燃器具水平净距离小于30cm
						"f_iszxzg5				REAL(1)							,"	+			//自行整改
						"f_islxwgs2				REAL(1)							,"	+			//联系燃气具服务中心整改33560008
						"f_isjxsb				REAL(1)							,"	+			//机表死表
						"f_issqbj				REAL(1)							,"	+			//输气不进
						"f_isqlbf				REAL(1)							,"	+			//气量不符
						"f_iscfck				REAL(1)							,"	+			//重复插卡
						"f_isddbgf				REAL(1)							,"	+			//断电不关阀
						"f_iszndq				REAL(1)							,"	+			//智能掉气
						"f_isyjxsgz				REAL(1)							,"	+			//液晶显示故障
						"f_isznbjq				REAL(1)							,"	+			//智能不减气
						"f_isqbczgz				REAL(1)							,"	+			//如存在故障，请保持电话畅通，我公司将在一周内进行维护
						"f_qtyh					TEXT(255)						,"	+			//其他隐患
						"oughtamount			REAL							,"	+			//用气量
						"f_cumulativepurchase	REAL							,"	+			//累购气量
						"f_yejingdushu			REAL							,"	+			//液晶读数(卡表)
						"lastrecord				REAL							,"	+			//本次抄表指数
						"lastinputgasnum		REAL							,"	+			//上次抄表指数
						"oughtfee				REAL							,"	+			//用气金额
						"f_qiliangcha			REAL							,"	+			//气量差
						"f_gaspricetype			REAL							,"	+			//气价类型
						"f_zhyegas				REAL							,"	+			//上期结余气量
						"mustpaygasfee			REAL							,"	+			//上期欠费金额
						"mustpaygascount		REAL							,"	+			//上期欠费气量
						"f_gasprice				REAL							,"	+			//气价
						"f_bczhyegas			REAL							,"	+			//本期结余气量
						"yjfee					REAL							,"	+			//应交金额
						"yjgas					REAL							,"	+			//应交气量
						"f_gasmeteraccomodations REAL							,"	+			//气表底数
						"f_bcrhrq				TEXT(20)						,"	+			//本次入户日期
						"f_isjjrh				REAL(1)							,"	+			//拒入
						"f_isjjqz				REAL(1)							,"	+			//拒签
						"f_beizhu				TEXT(255)						,"	+			//备注
						"f_isaqcsxc				REAL(1)							,"	+			//1、是否对您家中进行以下安全常识宣传？1）人不离火； 2）随手关阀； 3）常开窗户；4）告知天然气服务电话号码96777；
						"f_iszxjc				REAL(1)							,"	+			//2、是否对您家中天然气软管进行仔细检查?
						"f_isjlyhfzsjxjl		REAL(1)							,"	+			//3、是否用检漏仪或肥皂水对所有的燃气阀们和各管道接口处及其它燃气设施进行检漏，并教会您燃气泄漏后正确处理办法?
						"f_isffxczl				REAL(1)							,"	+			//4、是否对您发放宣传资料？
						"f_isajyzxjc			REAL(1)							,"	+			//5、安检人员是否按照上述“序号1、2、3、4、5、6、7”七项检查内容进行细致检查，并对发现的问题及时告知您？
						"f_ismanyi				TEXT(255)						,"	+			//评价：满意/不满意
						"f_isfzgd				REAL(1)							,"	+			//是否发整改单
						"f_anjianprivious		TEXT(20)						,"	+			//安全等级
						"f_yinhuan				TEXT(255)						,"	+			//隐患
						"f_guzhang				TEXT(255)						,"	+			//故障
						"f_reconmegent			TEXT(255)						,"	+			//用户建议及意见
	 					"USER_SIGN              varchar(80)                 null,"  +			//客户签名
						"PHOTO_FIRST            varchar(80)                 null,"	+	  		//照片1
						"PHOTO_SECOND           varchar(80)                 null,"	+			//照片2
						"PHOTO_THIRD            varchar(80)                 null,"	+			//照片3
						"PHOTO_FOUTH            varchar(80)                 null,"	+			//照片4
						"PHOTO_FIFTH        	varchar(80)                 null,"	+	 		//照片5
						"CHECKPLAN_ID 		    VARCHAR(80)					null,"  +           //安检计划ID
						"CHECKPAPER_ID 		    varchar(80)    				null,"  +           //安检单编号
						"CONDITION              varchar(80)                 null,"  +           //状态
						"PRIMARY KEY (ID))";
				db.execSQL(sql);
				//隐患表
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_HIDDEN");
				sql = "create table T_IC_SAFECHECK_HIDDEN (" +
						"id VARCHAR(80) not null," +
						"equipment            varchar(80)                    not null,"+     //设备
						"param              varchar(80)                    not null,"+        //参数
						"value       varchar(80)                    not null,"+      //值
						"INSPECTION_ID  varchar(80)   not null ," +
						"NAME  varchar(80)   not null ," +
						"BZ                   varchar(80)                    null," +
						" PRIMARY KEY  (id, param, value))";
				db.execSQL(sql);

		
				//维修安检单表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_TASK");
				sql = "create table T_REPAIR_TASK as select * from T_INSPECTION ";
				db.execSQL(sql);
				//维修隐患
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_ITEM");
				sql = "create table T_REPAIR_ITEM as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//保存安检临时表
				db.execSQL("DROP TABLE IF EXISTS T_INP");
				sql = "create table T_INP as select * from T_INSPECTION ";
				db.execSQL(sql);
				//保存安检临时表
				db.execSQL("DROP TABLE IF EXISTS T_INP_LINE");
				sql = "create table T_INP_LINE as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//保存维修参数
				db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
				sql = "create table T_PARAMS (" +
						"ID                  varchar(80)                      null,"+  //参数编号
						"NAME             varchar(80)                      null,"+  //参数名称
						"CODE           varchar(80)                      null,"+  //参数代码
						"PRIMARY KEY  (ID, CODE))";
				db.execSQL(sql);
				for(RepairMan rm : RepairManList)
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"维修人员", rm.id, rm.name});
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
				}
				//维修结果，把维修选项放到此表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT");
				sql = "create table T_REPAIR_RESULT (" +
						"ID                  varchar(80)                      null,"+  //安检编号
						"CONTENT             varchar(200)                      null,"+  //维修内容
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				//维修结果临时表
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT2");
				sql = "create table T_REPAIR_RESULT2 (" +
						"ID                  varchar(80)                      null,"+  //安检编号
						"CONTENT             varchar(200)                      null,"+  //维修内容
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				db.close();
				
				//提示创建成功
				Toast toast = Toast.makeText(mContext, "初始化完成。", Toast.LENGTH_SHORT);
				toast.show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, "初始化失败！", Toast.LENGTH_SHORT).show();
			}
		}

	private void UpdateParam(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
		try {
			//建立数据库
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			//保存维修参数
			db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
			String sql = "create table T_PARAMS (" +
					"ID                  varchar(80)                      null,"+  //参数编号
					"NAME             varchar(80)                      null,"+  //参数名称
					"CODE           varchar(80)                      null,"+  //参数代码
					"PRIMARY KEY  (ID, CODE))";
			db.execSQL(sql);
			for(RepairMan rm : RepairManList)
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"维修人员", rm.id, rm.name});
			//安检维修选项
			for(int i=0; i<map.size(); i++)
			{
				Map<String, Object> aMap = map.get(i);
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
			}
			db.close();
			
			//提示创建成功
			Toast toast = Toast.makeText(mContext, "参数提取完成。", Toast.LENGTH_SHORT);
			toast.show();
		} catch(Exception e) {
			Toast.makeText(mContext, "参数提取失败！", Toast.LENGTH_SHORT).show();
		}
	}
	
	//用户姓名
	public StringObservable UseName = new StringObservable("");

	// 旧密码
	public StringObservable OldPassword = new StringObservable("");
	// 新密码
	public StringObservable NewPassword = new StringObservable("");
	// 再次输入新密码
	public StringObservable NewPasswordAgain = new StringObservable("");

	public Command ChangePassword = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//输入验证
			if (CheckPassword()) {
				//调用服务
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							HttpPost httpPost = new HttpPost(Vault.AUTH_URL + "entity");
							StringEntity entity = new StringEntity("[{data:{id:'" + Util.getSharedPreference(mContext, Vault.USER_ID) + "',password:'" + NewPassword.get() + "'}}]" );
							httpPost.setEntity(entity);
							
							HttpClient httpClient = new DefaultHttpClient();
							HttpContext httpContext = new BasicHttpContext();
							HttpResponse response = httpClient.execute(httpPost, httpContext);
							int code = response.getStatusLine().getStatusCode();

							// 数据下载完成
							if (code == 200) {
								String strResult = EntityUtils.toString(response
										.getEntity());
								Message msg = new Message();
								msg.obj = strResult;
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
							else 
							{
								Message msg = new Message();
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}
						catch(Exception e)
						{
							Message msg = new Message();
							msg.what = 0;
							mHandler.sendMessage(msg);
						}
					}
				});
				th.start();
			}
		}
	};

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (1 == msg.what)
			{
				Toast.makeText(mContext, "密码修改成功！", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "密码修改失败！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean CheckPassword() {
		if ((OldPassword.get()).equals(Util.getSharedPreference(mContext, Vault.PASSWORD))) {
			if ((NewPassword).get().equals(NewPasswordAgain.get()) && (!(NewPassword.get().equals("")))) {
				return true;
			} else {
				Toast.makeText(mContext, "新密码输入有误，请重输！", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			Toast.makeText(mContext, "原密码输入错误！", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}