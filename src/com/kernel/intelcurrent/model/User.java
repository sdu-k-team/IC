package com.kernel.intelcurrent.model;

/**
 * classname:User.java
 * @author ������
 * */
public class User 
{
  private static final String TAG=User.class.getSimpleName();
  /**
   * �û�Ψһid
   * */
  public long id;
  
  /**
   * �û��ǳ�
   * */
  public String nick;
  
  /**
   * ����ʡ�ݴ���
   * */
  public int province;
  
  /**
   * ���ڳ��д���
   * */
  public int city;
  
  /**
   * ���ڵ�
   * */
  public String location;
  
  /**
   * ���˼��
   * */
  public String description;
  
  /**
   * ������ҳ
   * */
  public String homepage;
  
  /**
   * ͷ��url
   * */
  public String head;
  
  /**
   * �Ա�0-λ�ã�1-���ԣ�2-Ů��
   * */
  public int gender;
  
  /**
   * ��˿��
   * */
  public int fansnum;
  
  /**
   * ��ע��
   * */
  public int idolnum;
  
  /**
   * �ղ���
   * */
  public int favnum;
  
  /**
   * ΢����
   * */
  public int statusnum;
  
  /**
   * ע��ʱ��
   * */
  public String regTime;
  
  /**
   * �Ƿ��ҵĹ�ע
   * */
  public boolean ismyidol;
  
  /**
   *�Ƿ��ע�� 
   * */
  public boolean ismyfan;
  
  /**
   * �û�����ƽ̨
   * */
  public String platform;
  
  /**
   * ����΢��
   * */
  public Status newest;
}
