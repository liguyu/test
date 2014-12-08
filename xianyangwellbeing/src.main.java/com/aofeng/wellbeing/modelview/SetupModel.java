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
	
	//ִ�в�����������
	public Command UpdateParam = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			if (!(Util.fileExists(mContext.getDatabasePath("safecheck.db")))) 
				GetRepairManList(true);
			else
				GetRepairManList(false);
		}
	};

	//ִ��ϵͳ��ʼ������
	public Command Init = new Command(){
		public void Invoke(View view, Object... args) {
			if(mContext.isBusy)
			{
				Toast.makeText(mContext, "��ȴ��ϴβ�����ɡ�", Toast.LENGTH_SHORT).show();
				return;
			}	
			mContext.isBusy = true;
			GetRepairManList(true);
		}
	};

	private void GetRepairManList(final boolean toCreateDB) {
		RemoteReader reader = new RemoteReader(Vault.DB_URL + "sql/",
				"select ID, NAME, ENAME from t_user where instr(roles,(select id from t_role where NAME='ά����Ա'),1)>0");
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
				Toast.makeText(mContext, "��ȡά����Աʧ�ܣ������������ӡ�", Toast.LENGTH_SHORT).show();
			}

		});
		reader.start();
	}

	/**
	 * �õ������б�
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
				Toast.makeText(mContext, "��ȡϵͳ����ʧ�ܣ������������ӡ�", Toast.LENGTH_SHORT).show();
				mContext.isBusy = false;
			}

		});
		reader.start();		
	}
	
	private void CreateDatabase(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
			try {
				//�������ݿ�
				SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);
				db.execSQL("DROP TABLE IF EXISTS t_version");
				String   sql = "CREATE TABLE t_version (" +
						"id VARCHAR PRIMARY KEY, " +
						"ver integer )";
				db.execSQL(sql);
				sql = "insert into t_version values('1', " + Util.getVersionCode(mContext) + ")";
				db.execSQL(sql);
				//�����ƻ���
				db.execSQL("DROP TABLE IF EXISTS t_checkplan");
				sql = "CREATE TABLE t_checkplan (" +
						"id VARCHAR PRIMARY KEY, " +
						"f_date varchar," +            //����ƻ���ʼִ������
						"f_name VARCHAR," +            //����ƻ�����
						"f_enddate VARCHAR," +         //����ƻ���������
						"f_checkman VARCHAR)";         //����Ա����
				db.execSQL(sql);
				//�������쵥
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_PAPAER");
				sql = "CREATE TABLE T_IC_SAFECHECK_PAPAER (" +
						"id VARCHAR(80) PRIMARY KEY, " +
						" CONDITION            varchar(80)                    null," +             //������
						"HasNotified				varchar(80)						null,"+				//���ò�����
						" USER_NAME            varchar(80)                    null," +             	 	//�û�����
						" TELPHONE             varchar(60)                   	 null," +          		//�绰
						"ARRIVAL_TIME       varchar(80)                    	 null,"+			       //����ʱ��
						"DEPARTURE_TIME   varchar(80)             	    null,"+		   		   //�뿪ʱ��
//						" ROAD                 varchar(80)                    		null," +                  	//�ֵ�
//						" UNIT_NAME            varchar(80)                    null," +          			//С��
//						" CUS_DOM              varchar(20)                    null," +           			//¥��
//						" CUS_DY               varchar(20)                 		   null," +               			//��Ԫ
//						" CUS_FLOOR            varchar(20)                    null," +        		//¥��
//						" CUS_ROOM             varchar(20)                    null," +       		//����
						" DISTRICTNAME             varchar(20)                    null," +         //��λ
						" ADDRESS             varchar(20)                    null," +              //��ַ
						" OLD_ADDRESS          varchar(500)                 null," +  			//�û�������ַ

					  " ROOM_STRUCTURE       varchar(80)                    null," +   	//���ݽṹ
					  " WARM                 varchar(80)                    null," +                		 //��ů��ʽ
					  " SAVE_PEOPLE          varchar(20)                    null," +     			//����Ա

					  " IC_METER_NAME        varchar(20)                    null," +        //IC��������
					  " JB_METER_NAME        varchar(20)                    null," +       //����������
					  "METER_TYPE                varchar(80)                      null,"+			//����
					  "  CARD_ID            varchar(80)                    null," +             	 	//����
					  " JB_NUMBER            integer                        null," +          		//������
					  " SURPLUS_GAS          integer                        null," +              //ʣ������

					  " RQB_AROUND           varchar(80)                    null," +		//ȼ�������ұ�
					  " RQB_JBCODE           varchar(80)                    null," +			//ȼ��������
					  "METERMADEYEAR   varchar(80)              null,"	+			//ȼ�����������
					  " RQB                  varchar(80)                    null," +						//	ȼ����

					  " STANDPIPE            varchar(80)                    null," +				//����
					  " RIGIDITY             varchar(80)                    null," +					//�����Բ���
					  " STATIC               varchar(80)                    null," +					//��ֹѹ��
					  " STATIC_DATA          varchar(80)                    null," +			//��ֹѹ��ֵ
					  " TABLE_TAP            varchar(80)                    null," +				//��ǰ��
					  " COOK_TAP             varchar(80)                    null," +				//��ǰ��
					  " CLOSE_TAP            varchar(80)                    null," +				//�Աշ�
					  " INDOOR               varchar(80)                    null," +				//���ڹ�
					  "LEAKAGE_COOKER                       varchar(80)                   null,"+					//���©��
					  "LEAKAGE_HEATER                       varchar(80)                   null,"+					//��ˮ��©��
					  "LEAKAGE_BOILER                      varchar(80)                   null,"+					//�ڹ�¯©��
					  "LEAKAGE_NOTIFIED                     varchar(80)                   null,"+					//�����֪��
					  "LEAKGEPLACE    varchar(80)						null,"+					//©��λ��

					  " COOK_BRAND           varchar(80)                    null," +			//���Ʒ��
					  "COOK_TYPE        			varchar(80)					null,"	+			//����ͺ�
					  "COOKPIPE_NORMAL               	varchar(80)                 null," 	+ 			//������
					  "COOKERPIPECLAMPCOUNT             varchar(80)  					null, "	+			//��װ�ܿ�����
					  "COOKERPIPYLENGTH				varchar(80)					null,"  +			//������ܳ���
					  "COOK_DATE            varchar(80)                    null," +			//��߹�������

					  "WATER_BRAND          varchar(80)                    null," +		//��ˮ��Ʒ��
					  "WATER_TYPE          varchar(80)                    null,"+				//��ˮ���ͺ�
					  "WATER_PIPE          varchar(80)                    null,"+	 			//��ˮ�����
					  "WATER_FLUE          varchar(80)                    null,"+				//��ˮ���̵�
					  "WATER_NUME          varchar(80)                    null,"+				//�����ܿ���
					  "WATER_DATE           varchar(80)                    null," +			//��ˮ����������
					  "WATER_HIDDEN         varchar(80)                    null," +		//��ˮ������

					  " WHE_BRAND            varchar(80)                    null," +			//	�ڹ�¯Ʒ��
					  " WHE_TYPE            varchar(80)                    null," +	  		//�ڹ�¯�ͺ�
					  " WHE_DATE             varchar(80)                    null," +			//�ڹ�¯��������
					  " WHE_HIDDEN         varchar(80)                    null," +		//�ڹ�¥����

 					 " USER_SUGGESTION             varchar(80)         null," +			//�ͻ����
 					 " USER_SATISFIED             varchar(80)                 null,"	+			//�ͻ������
 					 " USER_SIGN             varchar(80)                    		null," +			//�ͻ�ǩ��

					  "THREAT            	  varchar(80)                    					null,"	+			//����
					  "PHOTO_FIRST           	  varchar(80)                    		null,"	+	  			//��Ƭ1
					  "PHOTO_SECOND           	  varchar(80)                    	null,"	+			//��Ƭ2
					  "PHOTO_THIRD           	  varchar(80)                    		null,"	+				//��Ƭ3
					  "PHOTO_FOUTH           	  varchar(80)                    	null,"	+			//��Ƭ4
					  "PHOTO_FIFTH        	  varchar(80)                    		null,"	+	 			//��Ƭ5
					  "NEEDS_REPAIR        	  varchar(80)                    		null,"	+	 			//�Ƿ���Ҫά��
					  "REPAIRMAN        	  varchar(80)                    		null,"	+	 			//ά����
					  "REPAIRMAN_ID        	  varchar(80)                    		null,"	+	 			//ά����ID
					  "REPAIR_STATE	     varchar(80)                  null," +              //ά��״̬
					  "CHECKPLAN_ID VARCHAR(80) null)";                                    //����ƻ�ID
				db.execSQL(sql);
				//�뻧�����
				db.execSQL("DROP TABLE IF EXISTS T_INSPECTION");
				sql = "CREATE TABLE T_INSPECTION ("		+
						"ID  					TEXT(255) 				NOT NULL,"	+
						"f_securityid			TEXT(255)						,"	+			//���쵥���
						"f_userid				TEXT(255)						,"	+			//�û�ID��
						"f_username				TEXT(255)						,"	+			//�û�����
						"f_huifangtelephone		TEXT(255)						,"	+			//�طõ绰
						"f_address				TEXT(255)						,"	+			//סַ
						"f_securitydate			TEXT(20)						,"	+			//��������
						"f_opratedate			TEXT(20)						,"	+			//��������
						"f_kitchen				REAL(1)							,"	+			//���
						"f_kitchenbrand			TEXT(255)						,"	+			//���Ʒ��
						"f_kitchensize			TEXT(20)						,"	+			//�������
						"f_waterheater			REAL(1)							,"	+			//��ˮ��
						"f_waterheaterbrand		TEXT(255)						,"	+			//��ˮ��Ʒ��
						"f_heatersize			TEXT(20)						,"	+			//��ˮ������
						"f_wallhangboiler		REAL(1)							,"	+			//�ڹ�¯
						"f_wallhangboilerbrand	TEXT(255)						,"	+			//�ڹ�¯Ʒ��
						"f_handboilersize		TEXT(10)						,"	+			//�ڹ�¯����
						"f_metertype			TEXT(20)						,"	+			//�����ͺ�
						"f_aroundmeter			TEXT(10)						,"	+			//���ұ�
						"f_meternumber			TEXT(20)						,"	+			//���
						"f_gasmeterstyle		TEXT(20)						,"	+			//��������
						"f_gaswatchbrand		TEXT(20)						,"	+			//������
						"f_isbtlq				REAL(1)							,"	+			//����©��
						"f_isfmlq				REAL(1)							,"	+			//����©��
						"f_isgdjkclq			REAL(1)							,"	+			//�ܵ��ӿڴ�©��
						"f_ishnczlqxx			REAL(1)							,"	+			//�继�ڴ���©�����󣬰�����Ա�����رձ�ǰ��������96777����
						"f_issghnrqgd			REAL(1)							,"	+			//˽�Ļ���ȼ���ܵ�
						"f_issqgg				REAL(1)							,"	+			//����96777����Ĺ�
						"f_isbb					REAL(1)							,"	+			//����
						"f_isbg					REAL(1)							,"	+			//����
						"f_isbf					REAL(1)							,"	+			//����
						"f_isktfk				REAL(1)							,"	+			//��ͨ��ף�ȷ����Ȼͨ��
						"f_isbtxs				REAL(1)							,"	+			//������ʴ
						"f_isgdxs				REAL(1)							,"	+			//�ܵ���ʴ
						"f_isbx					REAL(1)							,"	+			//����96777����
						"f_isbqrlj				REAL(1)							,"	+			//��ǰ������
						"f_isbhrlj				REAL(1)							,"	+			//���������
						"f_islxwgs				REAL(1)							,"	+			//��ϵ�ҹ�˾ȼ���߷����������ģ��绰��029-33560008
						"f_isrglh				REAL(1)							,"	+			//����ϻ�
						"f_isrgcc				REAL(1)							,"	+			//��ܳ���
						"f_isrgcqdmc			REAL(1)							,"	+			//��ܴ�ǽ���ء��Ŵ�
						"f_isrgwgk				REAL(1)							,"	+			//����޹ܿ�
						"f_islgwgk				REAL(1)							,"	+			//�����޹ܿ�
						"f_isghrg				REAL(1)							,"	+			//�������
						"f_isydrqj				REAL(1)							,"	+			//�ƶ�ȼ����
						"f_iszxzg1				REAL(1)							,"	+			//��������
						"f_isrggkyff			REAL(1)							,"	+			//��ܹܿ��ѷ���
						"f_issjrsq				REAL(1)							,"	+			//˽����ˮ��
						"f_issjbgl				REAL(1)							,"	+			//˽�ӱڹ�¯
						"f_isyjf				REAL(1)							,"	+			//˽����Ƿ�
						"f_issjrst				REAL(1)							,"	+			//˽������ͨ
						"f_islxwgs1				REAL(1)							,"	+			//��ϵ�ҹ�˾ȼ���߷����������ģ��绰��02933560008
						"f_isgzzj				REAL(1)							,"	+			//��װ���
						"f_isgzrsq				REAL(1)							,"	+			//��װ��ˮ��
						"f_isccghrqj			REAL(1)							,"	+			//���������ȼ����
						"f_isrsqwyd				REAL(1)							,"	+			//��ˮ�����̵����̵�δ�������
						"f_isrsqgyyd			REAL(1)							,"	+			//��ˮ�������̻������̵�
						"f_iszxzg2				REAL(1)							,"	+			//��������
						"f_isbglwyd				REAL(1)							,"	+			//�ڹ�¯���̵����̵�δ�������
						"f_isbglgyyd			REAL(1)							,"	+			//�ڹ�¯�����̻������̵�
						"f_iszxzg3				REAL(1)							,"	+			//��������
						"f_isczlzyshyhy			REAL(1)							,"	+			//�����������ϻ�Դ����
						"f_isycqthy				REAL(1)							,"	+			//�ƶ�������Դ
						"f_issykfscf			REAL(1)							,"	+			//���ڿ���ʽ�����������޸���
						"f_iscfdfzw				REAL(1)							,"	+			//�����ѷ�����
						"f_isjiageduan			REAL(1)							,"	+			//�ڳ����������װ����
						"f_iszxzg6				REAL(1)							,"	+			//��������
						"f_isgbcfyt				REAL(1)							,"	+			//�ı������;
						"f_isgbyqxz				REAL(1)							,"	+			//�ı���������
						"f_ishfcfyt				REAL(1)							,"	+			//�ָ�������;
						"f_isdgsdtsq			REAL(1)							,"	+			//����˾��������
						"f_issymbkj				REAL(1)							,"	+			//���������ܱտռ����Ȼͨ���
						"f_isxyswlm				REAL(1)							,"	+			//��Դ��ȼ���ܵ�������С��15cm
						"f_isczdxcr				REAL(1)							,"	+			//���ڵ��߲���
						"f_isqbzrtf				REAL(1)							,"	+			//��������,ȷ����Ȼͨ��
						"f_iszxzg4				REAL(1)							,"	+			//��������
						"f_iszjxysslm			REAL(1)							,"	+			//���������ȼ����ˮƽ������С��30cm
						"f_isrsqxysslm			REAL(1)							,"	+			//��ˮ��������ȼ����ˮƽ������С��30cm
						"f_iszxzg5				REAL(1)							,"	+			//��������
						"f_islxwgs2				REAL(1)							,"	+			//��ϵȼ���߷�����������33560008
						"f_isjxsb				REAL(1)							,"	+			//��������
						"f_issqbj				REAL(1)							,"	+			//��������
						"f_isqlbf				REAL(1)							,"	+			//��������
						"f_iscfck				REAL(1)							,"	+			//�ظ��忨
						"f_isddbgf				REAL(1)							,"	+			//�ϵ粻�ط�
						"f_iszndq				REAL(1)							,"	+			//���ܵ���
						"f_isyjxsgz				REAL(1)							,"	+			//Һ����ʾ����
						"f_isznbjq				REAL(1)							,"	+			//���ܲ�����
						"f_isqbczgz				REAL(1)							,"	+			//����ڹ��ϣ��뱣�ֵ绰��ͨ���ҹ�˾����һ���ڽ���ά��
						"f_qtyh					TEXT(255)						,"	+			//��������
						"oughtamount			REAL							,"	+			//������
						"f_cumulativepurchase	REAL							,"	+			//�۹�����
						"f_yejingdushu			REAL							,"	+			//Һ������(����)
						"lastrecord				REAL							,"	+			//���γ���ָ��
						"lastinputgasnum		REAL							,"	+			//�ϴγ���ָ��
						"oughtfee				REAL							,"	+			//�������
						"f_qiliangcha			REAL							,"	+			//������
						"f_gaspricetype			REAL							,"	+			//��������
						"f_zhyegas				REAL							,"	+			//���ڽ�������
						"mustpaygasfee			REAL							,"	+			//����Ƿ�ѽ��
						"mustpaygascount		REAL							,"	+			//����Ƿ������
						"f_gasprice				REAL							,"	+			//����
						"f_bczhyegas			REAL							,"	+			//���ڽ�������
						"yjfee					REAL							,"	+			//Ӧ�����
						"yjgas					REAL							,"	+			//Ӧ������
						"f_gasmeteraccomodations REAL							,"	+			//�������
						"f_bcrhrq				TEXT(20)						,"	+			//�����뻧����
						"f_isjjrh				REAL(1)							,"	+			//����
						"f_isjjqz				REAL(1)							,"	+			//��ǩ
						"f_beizhu				TEXT(255)						,"	+			//��ע
						"f_isaqcsxc				REAL(1)							,"	+			//1���Ƿ�������н������°�ȫ��ʶ������1���˲���� 2�����ֹط��� 3������������4����֪��Ȼ������绰����96777��
						"f_iszxjc				REAL(1)							,"	+			//2���Ƿ����������Ȼ����ܽ�����ϸ���?
						"f_isjlyhfzsjxjl		REAL(1)							,"	+			//3���Ƿ��ü�©�ǻ����ˮ�����е�ȼ�����Ǻ͸��ܵ��ӿڴ�������ȼ����ʩ���м�©�����̻���ȼ��й©����ȷ����취?
						"f_isffxczl				REAL(1)							,"	+			//4���Ƿ���������������ϣ�
						"f_isajyzxjc			REAL(1)							,"	+			//5��������Ա�Ƿ������������1��2��3��4��5��6��7�����������ݽ���ϸ�¼�飬���Է��ֵ����⼰ʱ��֪����
						"f_ismanyi				TEXT(255)						,"	+			//���ۣ�����/������
						"f_isfzgd				REAL(1)							,"	+			//�Ƿ����ĵ�
						"f_anjianprivious		TEXT(20)						,"	+			//��ȫ�ȼ�
						"f_yinhuan				TEXT(255)						,"	+			//����
						"f_guzhang				TEXT(255)						,"	+			//����
						"f_reconmegent			TEXT(255)						,"	+			//�û����鼰���
	 					"USER_SIGN              varchar(80)                 null,"  +			//�ͻ�ǩ��
						"PHOTO_FIRST            varchar(80)                 null,"	+	  		//��Ƭ1
						"PHOTO_SECOND           varchar(80)                 null,"	+			//��Ƭ2
						"PHOTO_THIRD            varchar(80)                 null,"	+			//��Ƭ3
						"PHOTO_FOUTH            varchar(80)                 null,"	+			//��Ƭ4
						"PHOTO_FIFTH        	varchar(80)                 null,"	+	 		//��Ƭ5
						"CHECKPLAN_ID 		    VARCHAR(80)					null,"  +           //����ƻ�ID
						"CHECKPAPER_ID 		    varchar(80)    				null,"  +           //���쵥���
						"CONDITION              varchar(80)                 null,"  +           //״̬
						"PRIMARY KEY (ID))";
				db.execSQL(sql);
				//������
				db.execSQL("DROP TABLE IF EXISTS T_IC_SAFECHECK_HIDDEN");
				sql = "create table T_IC_SAFECHECK_HIDDEN (" +
						"id VARCHAR(80) not null," +
						"equipment            varchar(80)                    not null,"+     //�豸
						"param              varchar(80)                    not null,"+        //����
						"value       varchar(80)                    not null,"+      //ֵ
						"INSPECTION_ID  varchar(80)   not null ," +
						"NAME  varchar(80)   not null ," +
						"BZ                   varchar(80)                    null," +
						" PRIMARY KEY  (id, param, value))";
				db.execSQL(sql);

		
				//ά�ް��쵥��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_TASK");
				sql = "create table T_REPAIR_TASK as select * from T_INSPECTION ";
				db.execSQL(sql);
				//ά������
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_ITEM");
				sql = "create table T_REPAIR_ITEM as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP");
				sql = "create table T_INP as select * from T_INSPECTION ";
				db.execSQL(sql);
				//���氲����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_INP_LINE");
				sql = "create table T_INP_LINE as select * from T_IC_SAFECHECK_HIDDEN ";
				db.execSQL(sql);
				
				//����ά�޲���
				db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
				sql = "create table T_PARAMS (" +
						"ID                  varchar(80)                      null,"+  //�������
						"NAME             varchar(80)                      null,"+  //��������
						"CODE           varchar(80)                      null,"+  //��������
						"PRIMARY KEY  (ID, CODE))";
				db.execSQL(sql);
				for(RepairMan rm : RepairManList)
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"ά����Ա", rm.id, rm.name});
				for(int i=0; i<map.size(); i++)
				{
					Map<String, Object> aMap = map.get(i);
					db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
				}
				//ά�޽������ά��ѡ��ŵ��˱�
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT");
				sql = "create table T_REPAIR_RESULT (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				//ά�޽����ʱ��
				db.execSQL("DROP TABLE IF EXISTS T_REPAIR_RESULT2");
				sql = "create table T_REPAIR_RESULT2 (" +
						"ID                  varchar(80)                      null,"+  //������
						"CONTENT             varchar(200)                      null,"+  //ά������
						"PRIMARY KEY  (ID, CONTENT))";
				db.execSQL(sql);
				db.close();
				
				//��ʾ�����ɹ�
				Toast toast = Toast.makeText(mContext, "��ʼ����ɡ�", Toast.LENGTH_SHORT);
				toast.show();
			} catch(Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, "��ʼ��ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}

	private void UpdateParam(ArrayList<RepairMan> RepairManList, List<Map<String, Object>> map) {
		try {
			//�������ݿ�
			SQLiteDatabase db = mContext.openOrCreateDatabase("safecheck.db", Context.MODE_PRIVATE, null);		
			//����ά�޲���
			db.execSQL("DROP TABLE IF EXISTS T_PARAMS");
			String sql = "create table T_PARAMS (" +
					"ID                  varchar(80)                      null,"+  //�������
					"NAME             varchar(80)                      null,"+  //��������
					"CODE           varchar(80)                      null,"+  //��������
					"PRIMARY KEY  (ID, CODE))";
			db.execSQL(sql);
			for(RepairMan rm : RepairManList)
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{"ά����Ա", rm.id, rm.name});
			//����ά��ѡ��
			for(int i=0; i<map.size(); i++)
			{
				Map<String, Object> aMap = map.get(i);
				db.execSQL("INSERT INTO T_PARAMS(ID, CODE, NAME) VALUES(?,?,?)", new String[]{ (String)aMap.get("col0"),  (String)aMap.get("col1"), (String)aMap.get("col2")});
			}
			db.close();
			
			//��ʾ�����ɹ�
			Toast toast = Toast.makeText(mContext, "������ȡ��ɡ�", Toast.LENGTH_SHORT);
			toast.show();
		} catch(Exception e) {
			Toast.makeText(mContext, "������ȡʧ�ܣ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	//�û�����
	public StringObservable UseName = new StringObservable("");

	// ������
	public StringObservable OldPassword = new StringObservable("");
	// ������
	public StringObservable NewPassword = new StringObservable("");
	// �ٴ�����������
	public StringObservable NewPasswordAgain = new StringObservable("");

	public Command ChangePassword = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			//������֤
			if (CheckPassword()) {
				//���÷���
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

							// �����������
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
				Toast.makeText(mContext, "�����޸ĳɹ���", Toast.LENGTH_SHORT).show();
			}
			else
			{
				Toast.makeText(mContext, "�����޸�ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private boolean CheckPassword() {
		if ((OldPassword.get()).equals(Util.getSharedPreference(mContext, Vault.PASSWORD))) {
			if ((NewPassword).get().equals(NewPasswordAgain.get()) && (!(NewPassword.get().equals("")))) {
				return true;
			} else {
				Toast.makeText(mContext, "�������������������䣡", Toast.LENGTH_SHORT).show();
				return false; 
			}
		} else {
			Toast.makeText(mContext, "ԭ�����������", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
}