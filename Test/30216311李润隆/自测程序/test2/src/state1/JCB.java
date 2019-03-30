package state1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JCB {
	public static int Job_Number;
	
	protected int JCB_ID;//作业号
	protected int InTime;//作业到达时间
	protected int PRO_Number;//作业包含的进程数
	protected JCB next;//指向下一个作业节点
	protected int pro[][];//存放随机生成的进程的详细信息
	protected int pro_position[];//存放每个进程每个页面的外存位置
	
	protected PrintWriter pw;//用于向仿真磁盘中写外页表
	protected PrintWriter px;//用于向仿真磁盘中写每个进程的详细信息
	
	public static int flag = 0;
	
	public static int flag1 = 2;
	public static int flag2 = 0;
	
	private String str = "Disk\\Cylinder\\";//路径
	
	public JCB(int JID,int time)//构造函数
	{
		this.JCB_ID = JID;
		this.InTime = time;
	}
	
	
	public void Create()//生成作业
	{
		this.PRO_Number = (int)(2 + 2 * Math.random());//每个作业有2-3个进程		
		this.pro = new int[this.PRO_Number][5];
		for (int i=0;i<this.PRO_Number;i++)
		{
			pro[i][0] = i+1;//进程序号
			pro[i][1] = (int)(1 + 100 * Math.random());//进程优先级1-100
			pro[i][2] = (int)(500 + 301 * Math.random());//每个进程指令条数500-800条
			pro[i][3] = (int)(3 + 3 * Math.random());//数据段的大小,占3-5个页面
			if(pro[i][2] % 256 == 0) {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3];//进程所占页面数
			}else {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3] + 1;//进程所占页面数
			}
				
			
		}
		
		this.pro_position = new int[this.PRO_Number];
		for(int i=0;i<this.PRO_Number;i++)
		{
			this.pro_position[i] = flag++;//该进程的外页表存储在第(int)(flag/64)磁道，第(flag%64)扇区
		}
	}
	
	public void WriteOutPageList() throws FileNotFoundException//往磁盘里写外页表，每个进程一张外页表
	{
		
		for(int i=0;i<this.PRO_Number;i++)
		{
			int k = 0;
			int track = (int)(this.pro_position[i] / 64) ;//磁道号
			int sector = this.pro_position[i] % 64;//扇区号
			
			File file = new File(str + "Track_" + Integer.toString(track) + "\\Sector_" + Integer.toString(sector) + ".txt");
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)
									)));
			for(int j=0;j<this.pro[i][4];j++)
			{
				pw.print(Integer.toString(this.JCB_ID));//作业号
				pw.print("\t");
				pw.print(Integer.toString(i+1));//该作业的第几个进程
				pw.print("\t");
				pw.print(Integer.toString(j+1));//该进程的第几个页面
				pw.print("\t");
				pw.print(Integer.toString(flag1));//该页面存放的磁道号
				pw.print("\t");
				pw.print(Integer.toString(flag2));//该页面存放的扇区号
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
	
	public void WritePage(int a,int x,int y,int k)//往磁盘里写每个页面的详细信息
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
				px.print(Integer.toString(k * 256 + i));//指令编号
				px.print("\t");
				px.print(Integer.toString((int)(4 * Math.random())));//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作，3表示I/O操作
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//指令是否需要访问数据段，1表示需要，0表示不需要
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//指令时间20-50ms
				px.print("\r\n");
			}
		}else {
			for(int i=0;i<a;i++)
			{
				px.print(Integer.toString(k * 256 + i));//指令编号
				px.print("\t");
				px.print(Integer.toString((int)(3 * Math.random())));//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//指令是否需要访问数据段，1表示需要，0表示不需要
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//指令时间20-50ms
				px.print("\r\n");
			}
		}
		px.close();
	}
}