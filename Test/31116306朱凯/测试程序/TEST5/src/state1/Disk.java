package state1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Disk {
	private int Track_Number;//磁道数
	private int Sector_Number;//扇区数
	private long Sector_Length;//扇区的大小
	
	protected boolean[][] peek;//记录该扇区是否被占用
	
	private String TrackName = new String("Track_");//路径名
	private String SectorName = new String("Sector_");//路径名
	
	
	public Disk(int t_number,int s_number,long s_length)//构造函数
	{
		this.Track_Number = t_number;//磁道数
		this.Sector_Number = s_number;//扇区数
		this.Sector_Length = s_length;//扇区的大小
	}
	
	public void Disk_Initial() throws IOException//磁盘初始化
	{
		peek = new boolean[this.Track_Number][this.Sector_Number];//标志位
		for(int i=0;i<this.Track_Number;i++)//每个磁道
		{
			for(int j=0;j<this.Sector_Number;j++)//每个扇区
			{
				peek[i][j] = false;//初始化，设定所有扇区都为空
			}
		}
		
		File diskfile = new File("Disk");//仿真的磁盘文件夹
		if(!diskfile.exists())//如果该文件夹不存在
		{
			diskfile.mkdirs();//创建磁盘文件架
		}
		
		File cylinderfile = new File("Disk\\Cylinder");//仿真的柱面文件夹
		if(!cylinderfile.exists())//如果该文件夹不存在
		{
			cylinderfile.mkdirs();//创建柱面文件夹
		
		
		for(int i=0;i<this.Track_Number;i++)//仿真磁道
		{
			File trackfile = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i));//磁道名
			if(!trackfile.exists())//如果该文件夹不存在
			{
				trackfile.mkdirs();//创建磁道文件夹
			}
			
		}
		
		for(int i=0;i<this.Track_Number;i++)//仿真扇区
		{
			for(int j=0;j<this.Sector_Number;j++)//每个扇区
			{
				File file = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i) + "\\" 
						+ this.SectorName + Integer.toString(j) + ".txt");//每个扇区用一个512B的文本文件表示
				RandomAccessFile r = new RandomAccessFile(file, "rw");  //创建文件
				r.setLength(this.Sector_Length);  //设定文件的大小为扇区的大小512B
				r.close();//关闭
			}	
		}
	}
	}
}
