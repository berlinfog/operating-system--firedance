package state1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class CPU {

	private int PC;//程序计数器
	private int IR;//指令寄存器
	private int PSW;//程序状态字寄存器
	private int State;//CPU状态，0为核心态，1为用户态
	protected boolean DispatchInfor;//调度信号，由时钟中断发出,true表示产生时钟中断，需要进行调度，false表示不需要调度
	
	private int RunTime;//系统已经运行的时间
	private int EndTime;//系统结束运行时间

	Create_Job jobcreator;//用于创建作业
	private int Job_Num;//记录已经到达的作业数
	private int Process_Num;//记录当前已经创建的进程数目
	
	private PCBTable pcbtable;
	private Clock clock;//时钟
	private Disk disk;//外存
	
	protected boolean IsEnd;//所有进程是否已经运行完毕
	
	protected boolean Is_PV_Occupy;//判断有无进程在临界区
	protected int PV_ProID;//储存占用临界区的进程ID
	
	public CPU()//构造函数
	{
		this.Init_Disk();//初始化磁盘
		//this.Init_Register();//初始化寄存器
		this.Init_PCBTable();//初始化队列
		//this.Init_Flag();//初始化自己定义的一些标志位

	}
	
	public void Start()//系统开始工作
	{
		clock = new Clock(this);
		Job_Dispatch jobdispatch = new Job_Dispatch();
		clock.start();
		jobdispatch.start();
	}
	
	public void Init_Disk()//初始化磁盘
	{
		disk = new Disk(32,64,512);
		try {
			disk.Disk_Initial();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("磁盘已初始化成功!");
	}
	
	public void Init_Register()//初始化寄存器
	{
		PC = 0;
		IR = 0;
		PSW = 0;
	}
	
	public void Init_Flag()//初始化自己定义的一些标志位
	{
		this.IsEnd = false;//
		this.Process_Num = 0;//初始进程数目为0
		this.Job_Num = 0;//初始到达作业数为0
	}
	
	public void Init_PCBTable()//初始化队列
	{
		pcbtable = new PCBTable();
	}
	
	public void Form_Job() throws Exception//事先生成作业
	{
		this.jobcreator = new Create_Job();
		System.out.println("所有作业已经生成完毕，一共有" + this.jobcreator.Job_Number + "个作业！");
	}
	
	public void Show_Job()//显示生成的作业
	{
		this.jobcreator.jobqueue.Show_JCB();
	}

	public void Protect(PCB pcb)//进程的现场保护
	{
		if(!pcb.Protect_Flag)//
		{
			pcb.stack.push(this.PC);
			pcb.stack.push(this.IR);
			pcb.stack.push(this.PSW);
			pcb.Protect_Flag = true;
		}else {
			for(int j=0;j<3;j++)
			{
				pcb.stack.pop();
			}
			pcb.stack.push(this.PC);
			pcb.stack.push(this.IR);
			pcb.stack.push(this.PSW);
		}
	}
	
	public void Recover(PCB pcb)//进程的现场恢复
	{
		this.PSW = pcb.stack.peek();
		pcb.stack.pop();
		this.IR = pcb.stack.peek();
		pcb.stack.pop();
		this.PC = pcb.stack.peek();
		pcb.stack.pop();
	}

	public void InitSet()
	{
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
	}
	
	class Job_Dispatch extends Thread{//作业调度线程
	
		public Job_Dispatch()//构造函数
		{
		
		}
		public void run()//重写run方法
		{
			while(true)
			{
				/*if(jobcreator.jobqueue.size() == 0)//若再无进程到达
				{
					show();
					test();
					break;//结束作业调度线程
				}*/
				
				synchronized (clock) {
	                if(!DispatchInfor) {//线程同步，确保每次时钟中断都进行了调度
	                    try {
							clock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                System.out.print(clock.GetTime() + ":");
	                
	                //作业调度
	                if(clock.GetTime() >= jobcreator.jobqueue.front.InTime && jobcreator.jobqueue.size() != 0)//若该时刻有作业到达
	                {
	                	Job_Num++;
	                	JCB jcb = jobcreator.jobqueue.front;
	                	System.out.println(clock.GetTime() + ":第" + Job_Num + "个作业到达" + "  " + jcb.PRO_Number + "个进程     " + jcb.pro[0][2] + " " + jcb.pro[0][3] + " " + jcb.pro[0][4]);	                	
	                	for(int i=0;i<jcb.PRO_Number;i++)
	                	{	
	                		
	                		try {
	                			PCB pcb = this.createpcb(jcb,i);
								pcbtable.ReadyQueue.join(pcb);//创建该作业的每一个进程并将进程加入就绪队列
								Process_Num++;
							} catch (Exception e) {
								e.printStackTrace();
							}	 
							
	                	}
	                	jobcreator.jobqueue.quit();//将已经调度成进程的作业出队
	                }
	                //中级调度
	                
	                while((pcbtable.WaitQueue.size() > 0) && (clock.GetTime() - pcbtable.WaitQueue.front().timeflag) >= pcbtable.WaitQueue.front().instruction[pcbtable.WaitQueue.front().PSW].Instr_TotalTime)
                	{
                		pcbtable.WaitQueue.front().PSW++;                    
                		pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
                		pcbtable.WaitQueue.quit();
                	}
	                //低级调度
	                if(pcbtable.RunQueue == null && pcbtable.ReadyQueue.size() == 0 && pcbtable.WaitQueue.size() == 0 && jobcreator.jobqueue.size() ==0)
	                {
	                	IsEnd = true;//若所有进程已经执行完，更改标志位，退出循环
	                	System.out.println("all job done!");
	                	break;
	                }
	                if(pcbtable.RunQueue != null)//如果当前有进程在运行
	                {
	                	switch(pcbtable.RunQueue.instruction[PC].Instr_State) {
	                		case 0://系统调用指令
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                			{
	                				PC++;//PC自增，指向该进程下一条指令;
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
	                				{
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列	                					
	                					pcbtable.RunQueue = null;//设置空值
	                				}else {
	                					this.Run_to_Ready();
	                				}
	                			}
	                			break;
	                		case 1://用户态计算指令
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                			{
	                				PC++;//PC自增，指向该进程下一条指令;
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
	                				{
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列
	                					pcbtable.RunQueue = null;//设置空值
	                				}
	                				else if(clock.GetTime() - pcbtable.RunQueue.timeflag > 1000)//时间片到
	                				{
	                					this.Run_to_Ready();
	                				}
	                			}
	                			break;
	                		case 2://PV操作指令
	                			if(Is_PV_Occupy == false || Is_PV_Occupy == true && PV_ProID == pcbtable.RunQueue.Pro_ID)//可以访问临界区
	                			{
	                				Is_PV_Occupy = true;
	                				PV_ProID = pcbtable.RunQueue.Pro_ID;
	                				pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                				if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                				{
	                					PC++;//PC自增，指向该进程下一条指令;
	                					Is_PV_Occupy = false;//其他进程可以访问临界资源
	                					if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
		                				{
		                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列
		                					pcbtable.RunQueue = null;//设置空值
		                				}	
	                				}	               
	                			}else {
	                				this.Run_to_Wait();
	                			}
	      	                    break;
	                		case 3:
	                			pcbtable.RunQueue.PSW = PC;
	                			pcbtable.RunQueue.timeflag = clock.GetTime();	              	                				                	
	                			pcbtable.WaitQueue.join(pcbtable.RunQueue);	                			
	                			pcbtable.RunQueue = null;	                			
	                			if(pcbtable.ReadyQueue.size() > 0)
	                			{
	                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();
	                				pcbtable.ReadyQueue.quit();
	                				PC = pcbtable.RunQueue.PSW;
	                				pcbtable.RunQueue.timeflag = clock.GetTime();
	                			}	                				     
	                			break;
	    
	                		default:
	                			break;
	    		          }
	                	
	                }else {//如果当前没有进程在运行
	                	if(pcbtable.ReadyQueue.size() != 0)
	                	{
	                		pcbtable.RunQueue = pcbtable.ReadyQueue.front();
	                		pcbtable.ReadyQueue.quit();
	                		PC = pcbtable.RunQueue.PSW;
	                		pcbtable.RunQueue.timeflag = clock.GetTime();
	                	}
	                }
	           
	                
	                if(pcbtable.RunQueue != null)
	                {
	                	System.out.print("当前运行进程:" + pcbtable.RunQueue.Pro_ID);
	                }else {
	                	System.out.print("null\t");
	                }
	                
	                System.out.print("当前就绪队列:");
	                if(pcbtable.ReadyQueue.size() > 0)
	                {
	                	pcbtable.ReadyQueue.Show_PCB();
	                }else {
	                	System.out.print("null\t");
	                }
	                
	                System.out.print("当前等待队列:");
	                if(pcbtable.WaitQueue.size() > 0)
	                {
	                	pcbtable.WaitQueue.Show_PCB();
	                }else {
	                	System.out.print("null\t");
	                }
	                System.out.println("");
	  
	                DispatchInfor = false;
	                clock.notifyAll();
				}
			}
		}
		
		public PCB createpcb(JCB jcb,int i)throws Exception//创建jcb号作业的第i个进程，返回pcb
		{
			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,clock.GetTime(),jcb.pro[i][2],jcb.pro[i][4]);//创建进程
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
				System.out.println(strs1[3] + "--------" +strs1[4]);
				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
				if(j <= 256 && j > 0)
				{
					flagx = 0;
					for(int l=0;l<j;l++)
					{
						try{
							String strs2[] = bf2.readLine().split("\t");//报nullpointexception
							pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));	
						}catch(Exception e) {}
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
			return pcb;//返回创建好的进程
		}
		
		public void Run_to_Ready()//进程切换，运行态进程加入就绪队列，就绪队列队首进入运行态
		{
			pcbtable.RunQueue.PSW = PC;
			pcbtable.ReadyQueue.join(pcbtable.RunQueue);
			pcbtable.RunQueue = pcbtable.ReadyQueue.front();
			pcbtable.ReadyQueue.quit();
			PC = pcbtable.RunQueue.PSW;
			pcbtable.RunQueue.timeflag = clock.GetTime();
		}
		
		public void Run_to_Wait()////进程切换，运行态进程加入阻塞队列，就绪队列队首进入运行态
		{
			pcbtable.RunQueue.PSW = PC;
			pcbtable.WaitQueue.join(pcbtable.RunQueue);
			if(pcbtable.ReadyQueue.size() > 0)
			{
				pcbtable.RunQueue = pcbtable.ReadyQueue.front();
				pcbtable.ReadyQueue.quit();
				PC = pcbtable.RunQueue.PSW;
				pcbtable.RunQueue.timeflag = clock.GetTime();
			}
			
		}
		
		public void Wake()//唤醒处于阻塞队列的
		{
			if(pcbtable.WaitQueue.size() > 0)
			{
				pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
				pcbtable.WaitQueue.quit();
			}
		}
		
	
	}
	
	public void show()
	{
		//System.out.println("当前运行进程:" + pcbtable.RunQueue.Pro_ID);
		System.out.print("就绪队列:");
		pcbtable.ReadyQueue.Show_PCB();
		System.out.print("阻塞队列:");
		pcbtable.WaitQueue.Show_PCB();
	}
	
	public void test()
	{
		pcbtable.ReadyQueue.test();
	}
	
}


