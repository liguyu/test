package com.aofeng.utils;

public class Vault {
	/**
	 * ��¼�û���
	 */
	public static String USER_NAME = "USER_NAME";
	/**
	 * ��¼����
	 */
	public static String PASSWORD ="ENCRYPT";	
	/**
	 * ������Ա����,ȡ��½��Ϣ���û���
	 */
	public  static String CHECKER_NAME ="CHECKER_NAME";
	/**
	 * �û�ID
	 */
	public static String USER_ID = "USER_ID";
	
	
	/**
	 * �ϴ����
	 */
	public static int UPLOAD_FLAG = 1;
	
	/**
	 * �Ѽ���
	 */
	public static int INSPECT_FLAG = 2;
	
	/**
	 * �������
	 */
	public static int NEW_FLAG = 4;
	
	/**
	 * ɾ�����
	 */
	public static int DELETE_FLAG = 8;
	
	/**
	 * ά�ޱ��
	 */
	public static int REPAIR_FLAG = 16;
	
	/**
	 * ��ǩ���
	 */
	public static int DENIED_FLAG = 32;
	
	/**
	 * ������
	 */
	public static int NOANSWER_FLAG = 64;
	

	public static String REPAIRED_NOT="δά��";
	public static String REPAIRED_UNUPLOADED="δ�ϴ�";
	public static String REPAIRED_UPLOADED="���ϴ�";
	
	public static  String packageName= "com.aofeng.safecheck";
	/*// ���ݷ����ַ
=======
//	// ���ݷ����ַ
//		public static String  DB_URL = "http://113.140.20.122:9990/rs/db/";
//	//�뻧�������
//		public static String  IIS_URL = "http://113.140.20.122:9990/rs/iis/";
//	//��֤�����ַ
//		public static String AUTH_URL = "http://113.140.20.122:9992/rs/user/";
//	public static String TUNNEL_URL = "http://113.140.20.122:9990/rs/tunnel/http%3A%7C%7C192.168.2.41%7Crs%7Cdb%7C";
//	public static  String downloadURL = "http://113.140.20.122:9990/changansafecheck.apk";
//	public static  String checkVersionURL = "http://113.140.20.122:9990/rs/db/one/from%20t_singlevalue%20where%20name='safecheck�汾��'";
	
	// ���ݷ����ַ
>>>>>>> .r24922
	public static String  DB_URL = "http://192.168.10.125:8080/rs/db/";
	//�뻧�������
	public static String  IIS_URL = "http://192.168.10.125:8080/rs/iis/";
	//��֤�����ַ
	public static String AUTH_URL = "http://192.168.10.125:83/rs/user/";
	public static String TUNNEL_URL = "http://192.168.10.125:8080/rs/tunnel/http%3A%7C%7C192.168.2.41%7Crs%7Cdb%7C";
	public static  String downloadURL = "http://192.168.10.125:8080/changansafecheck.apk";
	public static  String checkVersionURL = "http://192.168.10.125:8080/rs/db/one/from%20t_singlevalue%20where%20name='safecheck�汾��'";
	*/
	// ���ݷ����ַ
	public static String  DB_URL = "http://192.168.1.105:8080/rs/db/";
	//�뻧�������
	public static String  IIS_URL = "http://192.168.1.105:8080/rs/iis/";
	//��֤�����ַ
	public static String AUTH_URL = "http://192.168.1.105:83/rs/user/";
	public static String TUNNEL_URL = "http://192.168.1.105:8080/rs/tunnel/http%3A%7C%7C192.168.1.132%7Crs%7Cdb%7C";
	public static  String downloadURL = "http://192.168.1.105:8080/xianyangsafecheck.apk";
	public static  String checkVersionURL = "http://192.168.1.105:8080/rs/db/one/from%20t_singlevalue%20where%20name='safecheck�汾��'";
	
	public static  String apkName ="download.apk";
	public static String appID="xysafecheck";
}