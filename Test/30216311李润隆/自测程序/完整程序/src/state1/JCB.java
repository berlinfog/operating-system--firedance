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
	
	protected int JCB_ID;//作业号
	protected int InTime;//作业到达时间
	protected int PRO_Number;//作业包含的进程数
	protected JCB next;//指向下一个作业节点
	protected int pro[][];//存放随机生成的进程的详细信息
	protected int pro_position[];//存放每个进程外页表的位置
	
	protected PrintWriter pw;//用于向仿真磁盘中写外页表
	protected PrintWriter px;//用于向仿真磁盘中写每个进程的详细信息
	
	public static int flag = 0;//记录存放的外页表的位置
	
	public static int flag1 = 2;//记录页面存放在哪一个磁道
	public static int flag2 = 0;//记录页面存放在哪一个扇区
	
	private String str = "Disk\\Cylinder\\";//路径
	
	public JCB(int JID,int time)//构造函数
	{
		this.JCB_ID = JID;//赋值
		this.InTime = time;//赋值
	}
	
	
	public void Create() throws IOException//生成作业
	{
		PrintWriter ps = new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								new FileOutputStream(new File("Job\\Job" + Integer.toString(this.JCB_ID) + ".txt"))//打开文件
								)));
		ps.println(Integer.toString(this.JCB_ID));//写入作业号
		ps.println(Integer.toString(this.InTime));//写入作业到达时间
		
		this.PRO_Number = (int)(4 + 2 * Math.random());//每个作业有4-5个进程	
		ps.println(Integer.toString(this.PRO_Number));//写入该作业包含的进程数
		this.pro = new int[this.PRO_Number][6];//二维数组存放每个进程的详细信息
		for (int i=0;i<this.PRO_Number;i++)//每一个进程
		{
			pro[i][0] = i+1;//进程序号		
			pro[i][1] = (int)(1 + 100 * Math.random());//进程优先级1-100	
			pro[i][2] = (int)(550 + 201 * Math.random());//每个进程指令条数550-750条	
			pro[i][3] = (int)(3 + 3 * Math.random());//数据段的大小,占3-5个页面
			if(pro[i][2] % 256 == 0) {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3];//进程所占页面数
			}else {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3] + 1;//进程所占页面数
			}
			pro[i][5] = 0;//同步标志位
		}
		int k = (int)(1 + (this.PRO_Number - 2) * Math.random());//随机生成哪一个进程有同步
		pro[k][5] = 1;//k进程同步k+1号进程
		pro[k+1][5] = -1;//k+1号进程被k进程同步
		for (int i=0;i<this.PRO_Number;i++)//写信息
		{
			ps.print(Integer.toString(pro[i][0]));//进程序号	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][1]));//进程优先级1-100	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][2]));//每个进程指令条数550-750条	
			ps.print("\t");
			ps.print(Integer.toString(pro[i][3]));//数据段的大小,占3-5个页面
			ps.print("\t");
			ps.print(Integer.toString(pro[i][4]));//进程所占页面数
			ps.print("\t");
			ps.print(Integer.toString(pro[i][5]));//随机生成哪一个进程有同步
			ps.print("\r\n");
		}
		
		
		this.pro_position = new int[this.PRO_Number];//记录每一个进程的外页表存放在什么地方
		for(int i=0;i<this.PRO_Number;i++)//每一个进程
		{
			this.pro_position[i] = flag++;//该进程的外页表存储在第(int)(flag/64)磁道，第(flag%64)扇区
			ps.println(Integer.toString(pro_position[i]));//写位置
		}
		
		ps.close();//关闭
	}
	
	public void WriteOutPageList() throws FileNotFoundException//往磁盘里写外页表，每个进程一张外页表
	{
		
		for(int i=0;i<this.PRO_Number;i++)//每一个进程
		{
			int k = 0;//记录每一个进程指令已经占了多少页面，从0开始
			int track = (int)(this.pro_position[i] / 64) ;//磁道号
			int sector = this.pro_position[i] % 64;//扇区号
			
			File file = new File(str + "Track_" + Integer.toString(track) + "\\Sector_" + Integer.toString(sector) + ".txt");//打开该进程外页表对应的文件
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)//打开文件
									)));
			for(int j=0;j<this.pro[i][4];j++)//每一个页面
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
				/*pw.print("\t");
				pw.print(Integer.toString(this.pro[i][5]));//该页面存放的磁道号*/
				pw.print("\r\n");
				
				if(j < this.pro[i][4] - this.pro[i][3] -1)//如果该页面全是指令
				{
					this.WritePage(-1,flag1,flag2,k);//写该页面的详细信息，该页面在第flag1磁道，第flag2扇区
				}
				else if(j == this.pro[i][4] - this.pro[i][3] -1)//若该页面并不全是指令
				{
					this.WritePage(this.pro[i][2] % 256,flag1,flag2,k);//写该页面的详细信息，该页面在第flag1磁道，第flag2扇区
				}
				k++;//下一个指令页面
				
				flag2++;//指向下一个扇区
				if(flag2 > 63)//如果一个磁道扇区全部写完
				{
					flag1 ++;//换下一个磁道
					flag2 -= 64;//从新的磁道第0个扇区开始写
				}
			}
			pw.close();//关闭
			
		}
	}
	
	public void WritePage(int a,int x,int y,int k)//往磁盘里写每个页面的详细信息
	{
		File file = new File(str + "Track_" + Integer.toString(x) + "\\Sector_" + Integer.toString(y) + ".txt");//打开对应文件
		try {
			px = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)//打开对应文件
									)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(a == -1)//判断标志位
		{
			for(int i=0;i<256;i++)//写256条指令
			{
				px.print(Integer.toString(k * 256 + i));//指令编号
				px.print("\t");
				px.print(Integer.toString((int)(4 * Math.random())));//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作,3表示io操作
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//指令是否需要访问数据段，1表示需要，0表示不需要
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//指令时间20-50ms
				px.print("\r\n");
			}
		}else {//判断标志位
			for(int i=0;i<a;i++)//写a条指令
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
		px.close();//关闭
	}
}