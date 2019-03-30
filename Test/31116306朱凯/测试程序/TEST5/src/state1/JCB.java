package state1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JCB {
	public static int Job_Number;
	
	protected int JCB_ID;//��ҵ��
	protected int InTime;//��ҵ����ʱ��
	protected int PRO_Number;//��ҵ�����Ľ�����
	protected JCB next;//ָ����һ����ҵ�ڵ�
	protected int pro[][];//���������ɵĽ��̵���ϸ��Ϣ
	protected int pro_position[];//���ÿ��������ҳ���λ��
	
	protected PrintWriter pw;//��������������д��ҳ��
	protected PrintWriter px;//��������������дÿ�����̵���ϸ��Ϣ
	
	public static int flag = 0;//��¼��ŵ���ҳ���λ��
	
	public static int flag1 = 2;//��¼ҳ��������һ���ŵ�
	public static int flag2 = 0;//��¼ҳ��������һ������
	
	private String str = "Disk\\Cylinder\\";//·��
	
	public JCB(int JID,int time)//���캯��
	{
		this.JCB_ID = JID;//��ֵ
		this.InTime = time;//��ֵ
	}
	
	
	public void Create() throws IOException//������ҵ
	{
		PrintWriter ps = new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File("Job\\Job" + Integer.toString(this.JCB_ID) + ".txt"))//���ļ�
								)));
		ps.println(Integer.toString(this.JCB_ID));//д����ҵ��
		ps.println(Integer.toString(this.InTime));//д����ҵ����ʱ��
		
		this.PRO_Number = (int)(4 + 2 * Math.random());//ÿ����ҵ��4-5������	
		ps.println(Integer.toString(this.PRO_Number));//д�����ҵ�����Ľ�����
		this.pro = new int[this.PRO_Number][6];//��ά������ÿ�����̵���ϸ��Ϣ
		for (int i=0;i<this.PRO_Number;i++)//ÿһ������
		{
			pro[i][0] = i+1;//�������		
			pro[i][1] = (int)(1 + 100 * Math.random());//�������ȼ�1-100	
			pro[i][2] = (int)(550 + 201 * Math.random());//ÿ������ָ������550-750��	
			pro[i][3] = (int)(3 + 3 * Math.random());//���ݶεĴ�С,ռ3-5��ҳ��
			if(pro[i][2] % 256 == 0) {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3];//������ռҳ����
			}else {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3] + 1;//������ռҳ����
			}
			pro[i][5] = 0;//ͬ����־λ
		}
		int k = (int)(1 + (this.PRO_Number - 2) * Math.random());//���������һ��������ͬ��
		pro[k][5] = 1;//k����ͬ��k+1�Ž���
		pro[k+1][5] = -1;//k+1�Ž��̱�k����ͬ��
		for (int i=0;i<this.PRO_Number;i++)//д��Ϣ
		{
			ps.print(Integer.toString(pro[i][0]));//�������	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][1]));//�������ȼ�1-100	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][2]));//ÿ������ָ������550-750��	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][3]));//���ݶεĴ�С,ռ3-5��ҳ��
			ps.print("\t");
			ps.print(Integer.toString(pro[i][4]));//������ռҳ����
			ps.print("\t");
			ps.print(Integer.toString(pro[i][5]));//���������һ��������ͬ��
			ps.print("\r\n");
		}
		
		
		this.pro_position = new int[this.PRO_Number];//��¼ÿһ�����̵���ҳ������ʲô�ط�
		for(int i=0;i<this.PRO_Number;i++)//ÿһ������
		{
			this.pro_position[i] = flag++;//�ý��̵���ҳ��洢�ڵ�(int)(flag/64)�ŵ�����(flag%64)����
			ps.println(Integer.toString(pro_position[i]));//дλ��
		}
		
		ps.close();//�ر�
	}
	
	public void WriteOutPageList() throws FileNotFoundException//��������д��ҳ��ÿ������һ����ҳ��
	{
		
		for(int i=0;i<this.PRO_Number;i++)//ÿһ������
		{
			int k = 0;//��¼ÿһ������ָ���Ѿ�ռ�˶���ҳ�棬��0��ʼ
			int track = (int)(this.pro_position[i] / 64) ;//�ŵ���
			int sector = this.pro_position[i] % 64;//������
			
			File file = new File(str + "Track_" + Integer.toString(track) + "\\Sector_" + Integer.toString(sector) + ".txt");//�򿪸ý�����ҳ���Ӧ���ļ�
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)//���ļ�
									)));
			for(int j=0;j<this.pro[i][4];j++)//ÿһ��ҳ��
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
				/*pw.print("\t");
				pw.print(Integer.toString(this.pro[i][5]));//��ҳ���ŵĴŵ���*/
				pw.print("\r\n");
				
				if(j < this.pro[i][4] - this.pro[i][3] -1)//�����ҳ��ȫ��ָ��
				{
					this.WritePage(-1,flag1,flag2,k);//д��ҳ�����ϸ��Ϣ����ҳ���ڵ�flag1�ŵ�����flag2����
				}
				else if(j == this.pro[i][4] - this.pro[i][3] -1)//����ҳ�沢��ȫ��ָ��
				{
					this.WritePage(this.pro[i][2] % 256,flag1,flag2,k);//д��ҳ�����ϸ��Ϣ����ҳ���ڵ�flag1�ŵ�����flag2����
				}
				k++;//��һ��ָ��ҳ��
				
				flag2++;//ָ����һ������
				if(flag2 > 63)//���һ���ŵ�����ȫ��д��
				{
					flag1 ++;//����һ���ŵ�
					flag2 -= 64;//���µĴŵ���0��������ʼд
				}
			}
			pw.close();//�ر�
			
		}
	}
	
	public void WritePage(int a,int x,int y,int k)//��������дÿ��ҳ�����ϸ��Ϣ
	{
		File file = new File(str + "Track_" + Integer.toString(x) + "\\Sector_" + Integer.toString(y) + ".txt");//�򿪶�Ӧ�ļ�
		try {
			px = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)//�򿪶�Ӧ�ļ�
									)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(a == -1)//�жϱ�־λ
		{
			for(int i=0;i<256;i++)//д256��ָ��
			{
				px.print(Integer.toString(k * 256 + i));//ָ����
				px.print("\t");
				px.print(Integer.toString((int)(4 * Math.random())));//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����,3��ʾio����
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//ָ��ʱ��20-50ms
				px.print("\r\n");
			}
		}else {//�жϱ�־λ
			for(int i=0;i<a;i++)//дa��ָ��
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
		px.close();//�ر�
	}
}