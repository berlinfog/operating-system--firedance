package state1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JCB {
	public static int Job_Number;
	
	protected int JCB_ID;//��ҵ��
	protected int InTime;//��ҵ����ʱ��
	protected int PRO_Number;//��ҵ�����Ľ�����
	protected JCB next;//ָ����һ����ҵ�ڵ�
	protected int pro[][];//���������ɵĽ��̵���ϸ��Ϣ
	protected int pro_position[];//���ÿ������ÿ��ҳ������λ��
	
	protected PrintWriter pw;//��������������д��ҳ��
	protected PrintWriter px;//��������������дÿ�����̵���ϸ��Ϣ
	
	public static int flag = 0;
	
	public static int flag1 = 2;
	public static int flag2 = 0;
	
	private String str = "Disk\\Cylinder\\";//·��
	
	public JCB(int JID,int time)//���캯��
	{
		this.JCB_ID = JID;
		this.InTime = time;
	}
	
	
	public void Create()//������ҵ
	{
		this.PRO_Number = (int)(2 + 2 * Math.random());//ÿ����ҵ��2-3������		
		this.pro = new int[this.PRO_Number][5];
		for (int i=0;i<this.PRO_Number;i++)
		{
			pro[i][0] = i+1;//�������
			pro[i][1] = (int)(1 + 100 * Math.random());//�������ȼ�1-100
			pro[i][2] = (int)(500 + 301 * Math.random());//ÿ������ָ������500-800��
			pro[i][3] = (int)(3 + 3 * Math.random());//���ݶεĴ�С,ռ3-5��ҳ��
			if(pro[i][2] % 256 == 0) {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3];//������ռҳ����
			}else {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3] + 1;//������ռҳ����
			}
				
			
		}
		
		this.pro_position = new int[this.PRO_Number];
		for(int i=0;i<this.PRO_Number;i++)
		{
			this.pro_position[i] = flag++;//�ý��̵���ҳ��洢�ڵ�(int)(flag/64)�ŵ�����(flag%64)����
		}
	}
	
	public void WriteOutPageList() throws FileNotFoundException//��������д��ҳ��ÿ������һ����ҳ��
	{
		
		for(int i=0;i<this.PRO_Number;i++)
		{
			int k = 0;
			int track = (int)(this.pro_position[i] / 64) ;//�ŵ���
			int sector = this.pro_position[i] % 64;//������
			
			File file = new File(str + "Track_" + Integer.toString(track) + "\\Sector_" + Integer.toString(sector) + ".txt");
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)
									)));
			for(int j=0;j<this.pro[i][4];j++)
			{
				pw.print(Integer.toString(this.JCB_ID));//��ҵ��
				pw.print("\t");
				pw.print(Integer.toString(i+1));//����ҵ�ĵڼ�������
				pw.print("\t");
				pw.print(Integer.toString(j+1));//�ý��̵ĵڼ���ҳ��
				pw.print("\t");
				pw.print(Integer.toString(flag1));//��ҳ���ŵĴŵ���
				pw.print("\t");
				pw.print(Integer.toString(flag2));//��ҳ���ŵ�������
				pw.print("\r\n");
				
				if(j < this.pro[i][4] - this.pro[i][3] -1)
				{
					this.WritePage(-1,flag1,flag2,k);
				}
				else if(j == this.pro[i][4] - this.pro[i][3] -1)
				{
					this.WritePage(this.pro[i][2] % 256,flag1,flag2,k);
				}
				k++;
				
				flag2++;
				if(flag2 > 63)
				{
					flag1 ++;
					flag2 -= 64;
				}
			}
			pw.close();
			
		}
	}
	
	public void WritePage(int a,int x,int y,int k)//��������дÿ��ҳ�����ϸ��Ϣ
	{
		File file = new File(str + "Track_" + Integer.toString(x) + "\\Sector_" + Integer.toString(y) + ".txt");
		try {
			px = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)
									)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(a == -1)
		{
			for(int i=0;i<256;i++)
			{
				px.print(Integer.toString(k * 256 + i));//ָ����
				px.print("\t");
				px.print(Integer.toString((int)(4 * Math.random())));//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV������3��ʾI/O����
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//ָ��ʱ��20-50ms
				px.print("\r\n");
			}
		}else {
			for(int i=0;i<a;i++)
			{
				px.print(Integer.toString(k * 256 + i));//ָ����
				px.print("\t");
				px.print(Integer.toString((int)(3 * Math.random())));//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//ָ��ʱ��20-50ms
				px.print("\r\n");
			}
		}
		px.close();
	}
}