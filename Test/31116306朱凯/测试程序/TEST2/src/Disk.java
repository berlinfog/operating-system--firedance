import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk {
	
	
	private int Track_Number;//�ŵ���
	private int Sector_Number;//������
	private long Sector_Length;//�����Ĵ�С
	
	protected boolean[][] peek;//��¼�������Ƿ�ռ��
	
	private String TrackName = new String("Track_");
	private String SectorName = new String("Sector_");
	
	
	public Disk(int t_number,int s_number,long s_length)//���캯��
	{
		this.Track_Number = t_number;
		this.Sector_Number = s_number;
		this.Sector_Length = s_length;
	}
	
	public void Disk_Initial() throws IOException
	{
		peek = new boolean[this.Track_Number][this.Sector_Number];
		for(int i=0;i<this.Track_Number;i++)
		{
			for(int j=0;j<this.Sector_Number;j++)
			{
				peek[i][j] = false;//��ʼ�����趨����������Ϊ��
			}
		}
		
		File diskfile = new File("Disk");//����Ĵ����ļ���
		if(!diskfile.exists())
		{
			diskfile.mkdirs();//���������ļ���
		}
		
		File cylinderfile = new File("Disk\\Cylinder");//����������ļ���
		if(!cylinderfile.exists())
		{
			cylinderfile.mkdirs();//���������ļ���
		}
		
		for(int i=0;i<this.Track_Number;i++)//����ŵ�
		{
			File trackfile = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i));//�ŵ���
			if(!trackfile.exists())
			{
				trackfile.mkdirs();//�����ŵ��ļ���
			}
			
		}
		
		for(int i=0;i<this.Track_Number;i++)//��������
		{
			for(int j=0;j<this.Sector_Number;j++)
			{
				File file = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i) + "\\" 
						+ this.SectorName + Integer.toString(j) + ".txt");//ÿ��������һ��512B���ı��ļ���ʾ
				RandomAccessFile r = new RandomAccessFile(file, "rw");  
				r.setLength(this.Sector_Length);  
				r.close();
			}	
		}
	}
	
	public static void main (String [] args) throws Exception
	{	
		Disk disk = new Disk(32,64,512);
		disk.Disk_Initial();
		System.out.println("���̳�ʼ���ɹ���");
	}
}
