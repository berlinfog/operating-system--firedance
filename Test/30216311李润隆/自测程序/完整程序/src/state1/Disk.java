package state1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk {
	private int Track_Number;//�ŵ���
	private int Sector_Number;//������
	private long Sector_Length;//�����Ĵ�С
	
	protected boolean[][] peek;//��¼�������Ƿ�ռ��
	
	private String TrackName = new String("Track_");//·����
	private String SectorName = new String("Sector_");//·����
	
	
	public Disk(int t_number,int s_number,long s_length)//���캯��
	{
		this.Track_Number = t_number;//�ŵ���
		this.Sector_Number = s_number;//������
		this.Sector_Length = s_length;//�����Ĵ�С
	}
	
	public void Disk_Initial() throws IOException//���̳�ʼ��
	{
		peek = new boolean[this.Track_Number][this.Sector_Number];//��־λ
		for(int i=0;i<this.Track_Number;i++)//ÿ���ŵ�
		{
			for(int j=0;j<this.Sector_Number;j++)//ÿ������
			{
				peek[i][j] = false;//��ʼ�����趨����������Ϊ��
			}
		}
		
		File diskfile = new File("Disk");//����Ĵ����ļ���
		if(!diskfile.exists())//������ļ��в�����
		{
			diskfile.mkdirs();//���������ļ���
		}
		
		File cylinderfile = new File("Disk\\Cylinder");//����������ļ���
		if(!cylinderfile.exists())//������ļ��в�����
		{
			cylinderfile.mkdirs();//���������ļ���
		
		
		for(int i=0;i<this.Track_Number;i++)//����ŵ�
		{
			File trackfile = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i));//�ŵ���
			if(!trackfile.exists())//������ļ��в�����
			{
				trackfile.mkdirs();//�����ŵ��ļ���
			}
			
		}
		
		for(int i=0;i<this.Track_Number;i++)//��������
		{
			for(int j=0;j<this.Sector_Number;j++)//ÿ������
			{
				File file = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i) + "\\" 
						+ this.SectorName + Integer.toString(j) + ".txt");//ÿ��������һ��512B���ı��ļ���ʾ
				RandomAccessFile r = new RandomAccessFile(file, "rw");  //�����ļ�
				r.setLength(this.Sector_Length);  //�趨�ļ��Ĵ�СΪ�����Ĵ�С512B
				r.close();//�ر�
			}	
		}
	}
	}
}
