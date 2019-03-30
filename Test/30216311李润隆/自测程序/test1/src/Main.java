import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Stack;

public class Main {
	public static void main (String [] args) throws Exception
	{
		Disk disk = new Disk(32,64,512);
		disk.Disk_Initial();
		Create_Job jobcreator = new Create_Job();//创建工作
		PCBTable pcbtable = new PCBTable();//创建pcbtable实例
		int Job_Num = 0;
		int Process_Num = 0;
		
		 while(jobcreator.jobqueue.size() > 0)//若该时刻有作业到达
         {
         	JCB jcb = jobcreator.jobqueue.front;
         	System.out.println("第" + Job_Num + "个作业到达" + "  包含" + jcb.PRO_Number + "个进程 ");	                	
         	for(int i=0;i<jcb.PRO_Number;i++) 
         	{	
         		System.out.println("进程号:" + Process_Num + "  指令数:" + jcb.pro[i][2] + "  总页面数:" + jcb.pro[i][4]);
         		try {
         			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,0,jcb.pro[i][2],jcb.pro[i][4]);//创建进程
        			
        			pcb.Pro_InstrNum = jcb.pro[i][2];
        			pcb.instruction = new Instruction[jcb.pro[i][2]];
        			
        			
        			File file1 = new File("Disk\\Cylinder\\Track_" + Integer.toString((int)(jcb.pro_position[i] / 64)) + 
        					"\\Sector_" + Integer.toString((int)(jcb.pro_position[i] % 64)) + ".txt");
        			BufferedReader bf1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
        			String str1;
        			String strs1[] = new String[5];
        			int j = pcb.Pro_InstrNum;
        			int flagx = 1;
        			int c = 0;
        			while((str1 = bf1.readLine()) != null)
        			{
        				if(flagx == 0) break;
        				strs1 = str1.split("\t");
        				File file2  = new File("Disk\\Cylinder\\Track_" + Integer.valueOf(strs1[3]) + 
        					"\\Sector_" + Integer.valueOf(strs1[4]) + ".txt");//根据外页表找到对应的页
        				//System.out.println(strs1[3] + "--------" +strs1[4]);
        				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
        				if(j <= 256 && j > 0)
        				{
        					flagx = 0;
        					for(int l=0;l<j;l++)
        					{
        						try {
        						String strs2[] = bf2.readLine().split("\t");//报nullpointexception
        						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));	
        						}catch (Exception e)
        						{}
        						
        					}
        					
        				}else if(j > 256)
        				{
        					j -= 256;
        					for(int l=0;l<256;l++)
        					{
        						String strs2[] = bf2.readLine().split("\t");
        						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));
        					}
        				}
        			}
        			
					pcbtable.ReadyQueue.join(pcb);//创建该作业的每一个进程并将进程加入就绪队列
					Process_Num++;
				} catch (Exception e) {
					e.printStackTrace();
				}	 	
         	}
         	jobcreator.jobqueue.quit();//将已经调度成进程的作业出队
         	Thread.sleep(1000);
         	Job_Num++;
         }
		 System.out.println("所有作业都已经到达");
		
	}
}

class Create_Job {
	protected int Job_Number;
	protected int time = 0;
	protected JobQueue jobqueue;
	

	public Create_Job() throws FileNotFoundException//构造函数
	{
		this.jobqueue = new JobQueue();
		this.Job_Number = 5 ;// (int)(2 + 3 * Math.random());//2-4个作业，测试使用固定5
		for(int i=0;i<this.Job_Number;i++)
		{
			JCB jcb = new JCB(i+1,time);
			jcb.Create();//生成进程以及其信息比如占页面多少，一个作业有多少进程
			
			jcb.WriteOutPageList();
			
			jobqueue.join(jcb);
			time += (int)(100 * (2 + 4 * Math.random()));//每隔200-600毫秒产生一个作业请求
		}
	}
}

class JCB {
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
		this.PRO_Number = (int)(3 + 2 * Math.random());//每个作业有3-4个进程		
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
				px.print(Integer.toString((int)(4 * Math.random())));//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作,3表示io操作
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

class JobQueue {
	protected JCB front;//队头
	protected JCB rear;//队尾	
	protected int Queue_Size;
	
	public JobQueue()
	{	
		this.Queue_Size = 0;
		this.front = null;
		this.rear = this.front;
	}		
	public boolean IsEmpty()//判断是否为空
	{		
		if(this.rear == this.front && this.front == null) return true;
		else {
			return false;
		}
	}	
		
	public void join(JCB e)//入队
	{		
		if(this.front == null)
		{
			this.front = e;
			this.rear = e;
		}
		else {
			this.rear.next = e;
			this.rear = e;
		}
		this.Queue_Size++;
	}	
	
	public void quit()//出队
	{
		if(this.front == this.rear) {this.front = null;}
		else {
			this.front = this.front.next;
		}
		this.Queue_Size--;
	}
	
	public JCB front()
	{
		return this.front;
	}
	
	public JCB rear()
	{
		return this.rear;
	}
	
	public int size()
	{
		return this.Queue_Size;
	}
	
	public void Show_JCB()//显示JCB队列
	{
		JCB jcb = this.front;
		while(jcb != null)
		{
			System.out.println(jcb.JCB_ID);
			jcb = jcb.next;
		}
	}
}
class Disk {
	
	private int Track_Number;//磁道数
	private int Sector_Number;//扇区数
	private long Sector_Length;//扇区的大小
	
	protected boolean[][] peek;//记录该扇区是否被占用
	
	private String TrackName = new String("Track_");
	private String SectorName = new String("Sector_");
	
	
	public Disk(int t_number,int s_number,long s_length)//构造函数
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
				peek[i][j] = false;//初始化，设定所有扇区都为空
			}
		}
		
		File diskfile = new File("Disk");//仿真的磁盘文件夹
		if(!diskfile.exists())
		{
			diskfile.mkdirs();//创建磁盘文件架
		}
		
		File cylinderfile = new File("Disk\\Cylinder");//仿真的柱面文件夹
		if(!cylinderfile.exists())
		{
			cylinderfile.mkdirs();//创建柱面文件夹
		}
		
		for(int i=0;i<this.Track_Number;i++)//仿真磁道
		{
			File trackfile = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i));//磁道名
			if(!trackfile.exists())
			{
				trackfile.mkdirs();//创建磁道文件夹
			}
			
		}
		
		for(int i=0;i<this.Track_Number;i++)//仿真扇区
		{
			for(int j=0;j<this.Sector_Number;j++)
			{
				File file = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i) + "\\" 
						+ this.SectorName + Integer.toString(j) + ".txt");//每个扇区用一个512B的文本文件表示
				RandomAccessFile r = new RandomAccessFile(file, "rw");  
				r.setLength(this.Sector_Length);  
				r.close();
			}	
		}
	}
}

class PCB {
	protected int Job_ID;//进程所属的作业ID
	protected int Pro_ID;//进程ID
	protected int Pro_Priority;//进程优先级
	
	protected int Pro_ArriveTime;//进程到达时间
	protected int Pro_InTime;//进程创建时间
	
	protected int Pro_State;//进程状态，1运行，2就绪，3等待
	
	protected int Pro_RunTime;//进程运行时间
	protected int Pro_EndTime;//进程结束时间
	
	protected int timeflag = 0;//
	
	protected int Pro_TotalTime;//进程周转时间
	protected int PSW;//进程当前执行的指令编号
	protected int Pro_InstrNum;//进程包含的指令数目
	protected int PageNum;//进程所占的页面数
	protected Instruction[] instruction;//指令链表
	
	protected PCB next;//指向下一个PCB节点
	
	protected Stack<Integer> stack;//现场保护用到的栈
	protected boolean Protect_Flag;
	
	protected final static int PCB_Length = 20;//PCB链表的最大长度
	protected static int PCB_Number;//PCB链表当前节点数
	
	public boolean IsFull()//判断PCB链表是否已满
	{
		if(this.PCB_Length == this.PCB_Number) return true;
		else {
			return false;
		}
	}
	
	public PCB()
	{

	}
	
	public PCB(int jid , int pid , int priority , int arrivetime , int intime, int instrnum , int pagenum)//构造函数
	{
		this.Job_ID = jid;
		this.Pro_ID = pid;
		this.Pro_Priority = priority;
		this.Pro_ArriveTime = arrivetime;
		this.Pro_InTime = intime;
		this.PageNum = pagenum;
		
		this.Pro_State = 0;
		this.Pro_RunTime = 0;
		this.Pro_EndTime = 0;
		this.PSW = 0;
		
		this.Pro_InstrNum = instrnum;  
		//this.instruction = new Instruction[InstrNum];
	}
}

class PCBTable {
	protected PCB RunQueue;//指向正在运行的进程
	protected MyQueue ReadyQueue;//就绪队列
	protected MyQueue WaitQueue;//等待队列
	protected MyQueue FinishQueue;//已完成队列
	protected int[][] proinfo;
	
	public PCBTable()
	{
		this.RunQueue = new PCB();
		this.RunQueue = null;
		this.ReadyQueue = new MyQueue();
		this.WaitQueue = new MyQueue();
		this.FinishQueue = new MyQueue();
		this.proinfo = new int[34][5];
	}
}

class Instruction {
	protected int Instr_ID;//指令序号
	protected int Instr_State;//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作
	protected int Need_Data;//指令是否需要访问数据段，1表示需要，0表示不需要
	protected int Instr_TotalTime;//指令总时间
	protected int Instr_RunTime;//指令已经运行的时间
	protected int Instr_Addr;//指令的地址

	
	public Instruction(int ID , int State , int NeedData , int TotalTime)//构造函数
	{ 
		this.Instr_ID = ID;
		this.Instr_State = State;
		this.Need_Data = NeedData;
		this.Instr_TotalTime = TotalTime;
		this.Instr_RunTime = 0; 
	}
}


class MyQueue
{		
	private PCB front;//队头
	private PCB rear;//队尾	
	private int Queue_Size;
	
	public MyQueue()
	{	
		this.Queue_Size = 0;
		this.front = null;
		this.rear = this.front;
	}		
	public boolean IsEmpty()//判断是否为空
	{		
		if(this.rear == this.front && this.front == null) return true;
		else {
			return false;
		}
	}	
		
	public void join(PCB e)//入队
	{		
		if(this.front == null)
		{
			this.front = e;
			this.rear = e;
		}
		else {
			this.rear.next = e;
			this.rear = e;
		}
		this.Queue_Size++;
	}	
	
	public void quit()//出队
	{
		if(this.front == this.rear) {this.front = null;} //System.out.println("队列为空队列");
		else {
			this.front = this.front.next;
		}
		this.Queue_Size--;
	}
	
	public PCB front()
	{
		return this.front;
	}
	
	public PCB rear()
	{
		return this.rear;
	}
	
	public int size()
	{
		return this.Queue_Size;
	}
	
	public void Show_PCB()//显示PCB队列
	{
		PCB pcb = this.front;
		for(int i=0;i<this.size();i++)
		{
			System.out.print(pcb.Pro_ID + " ");
			pcb = pcb.next;
		}
	}
	
	public void test()
	{
		PCB pcb = this.front;
		while(pcb != null)
		{
			for(int i=0;i<pcb.Pro_InstrNum;i++)
			{
				if(pcb.instruction[i].Instr_TotalTime < 10)
				{
					System.out.println(pcb.Pro_ID + "bad");
				}
			}
			pcb = pcb.next;
		}
	}	
}


