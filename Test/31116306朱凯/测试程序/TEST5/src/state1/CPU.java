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
	private boolean[] buy;
	
	private PCBTable pcbtable;//pcb队列，包含等待，就绪，完成队列
	private Clock clock;//时钟
	private Memory memory;//内存
	private MMU mmu;//MMU
	private Disk disk;//外存
	
	private Interface inter;//界面
	//private Watch wat;
	
	public static boolean pauseflag = false;//暂停标志位
	public static boolean startflag = true;//开始运行标志位
	public static boolean xxstartfalg = true;
	public static boolean initialflag = true;//系统初始化标志位
	public static boolean jobflag = true;//作业获取标志位
	public static int jobway;//作业生成方式标志位
	
	public JobQueue jq;//作业队列
	
	protected boolean IsEnd;//所有进程是否已经运行完毕
	
	protected boolean Is_PV_Occupy;//判断有无进程在临界区
	protected int PV_ProID;//储存占用临界区的进程ID
	
	public CPU()//构造函数
	{	
		inter = new Interface();//初始化界面
		//wat = new Watch();
	}
	
	public void InitialSystem()//初始化系统
	{
		while(this.initialflag)//等待界面上“初始化系统”按钮事件
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("系统初始化中.....");
		this.Init_Disk();//初始化磁盘
		this.Init_Memory();//初始化内存
		this.Init_Register();//初始化寄存器
		this.Init_PCBTable();//初始化队列
		this.Init_Flag();//初始化自己定义的一些标志位
		this.mmu = new MMU();//初始化MMU
		this.JudgeJobCreateWay();//作业生成方式
	}
	
	public void JudgeJobCreateWay()
	{
		while(this.jobflag)//等待界面上按钮事件
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(this.jobway == 1)//使用已经生成好的作业
		{
			try {
				this.getjob();//获取每个作业的详细信息，包括到达时间，作业号，进程数等等
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {//随机生成新的作业
			try {
				this.Form_Job();//随机生成新的作业
				this.getjob();//获取每个作业的详细信息，包括到达时间，作业号，进程数等等
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.Start();//系统开始工作
	}
	
	public void Start()//系统开始工作
	{
		while(this.startflag)//等待界面上按钮事件
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		clock = new Clock(this);//初始化时钟线程
		Dispatch dispatch = new Dispatch();//初始化调度线程
		clock.start();//时钟线程开始运行
		dispatch.start();//调度线程开始运行
	}
	
	public void Init_Memory()//初始化内存
	{
		memory = new Memory();//初始化内存
		memory.Init_Page();//初始化每一个页框
		System.out.println("内存初始化成功！");
	}
	
	public void Init_Disk()//初始化磁盘
	{
		disk = new Disk(32,64,512);//初始化磁盘
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
		System.out.println("寄存器初始化成功！");
	}
	
	public void Init_Flag()//初始化自己定义的一些标志位
	{
		this.IsEnd = false;//初始化标志位
		this.Process_Num = 0;//初始进程数目为0
		this.Job_Num = 0;//初始到达作业数为0
		buy = new boolean[32];
	}
	
	public void Init_PCBTable()//初始化队列
	{
		jq = new JobQueue();//初始化作业队列
		pcbtable = new PCBTable();//初始化队列类
		System.out.println("PCB队列初始化成功！");
	}
	
	public void Form_Job() throws Exception//随机生成作业
	{
		this.jobcreator = new Create_Job();//初始化作业生成类
		System.out.println("所有作业已经生成完毕，一共有" + this.jobcreator.Job_Number + "个作业！");
	}
	
	public void Show_Job()//显示生成的作业
	{
		this.jobcreator.jobqueue.Show_JCB();//显示生成的作业
	}

	public void Protect(PCB pcb)//进程的现场保护
	{
		if(!pcb.Protect_Flag)//如果之前没有进行现场保护
		{
			pcb.stack.push(this.PC);//入栈
			pcb.stack.push(this.IR);//入栈
			pcb.stack.push(this.PSW);//入栈
			pcb.Protect_Flag = true;//修改标志位
		}else {//如果之前进行了现场保护
			for(int j=0;j<3;j++)
			{
				pcb.stack.pop();//出栈
			}
			pcb.stack.push(this.PC);//入栈
			pcb.stack.push(this.IR);//入栈
			pcb.stack.push(this.PSW);//入栈
		}
	}
	
	public void Recover(PCB pcb)//进程的现场恢复
	{
		this.PSW = pcb.stack.peek();
		pcb.stack.pop();//出栈
		this.IR = pcb.stack.peek();
		pcb.stack.pop();//出栈
		this.PC = pcb.stack.peek();
		pcb.stack.pop();//出栈
	}

	public void InitSet()//重新初始化标志位
	{
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
	}
	
	public void getjob() throws Exception//从文件中读取作业信息
	{
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Job\\JobNumber.txt"))));//打开文件
		int jobnum = Integer.valueOf(bff.readLine());//读取本次调度的作业数
		for(int i=0;i<jobnum;i++)//读取每一个作业的详细信息
		{
			BufferedReader bfff = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Job\\Job" + Integer.toString(i) + ".txt"))));//打开文件
			int jid = Integer.valueOf(bfff.readLine());//读取作业号
			//System.out.println(jid);
			int intime = Integer.valueOf(bfff.readLine());//读取作业到达时间
			//System.out.println(intime);
			JCB jcb = new JCB(jid,intime);//创建jcb节点
			int pron = Integer.valueOf(bfff.readLine());//读取该作业的进程数
			jcb.PRO_Number = pron;
			//System.out.println(pron);
			String[] str1;
			jcb.pro = new int[pron][6];//存放每个进程的详细信息
			jcb.pro_position = new int[pron];//存放每个进程外页表的位置
			for(int j=0;j<pron;j++)//获取每个进程的详细信息
			{
				String s = bfff.readLine();
				//System.out.println(s);
				str1 = s.split("\t");
				jcb.pro[j][0] = Integer.valueOf(str1[0]);//进程号
				jcb.pro[j][1] = Integer.valueOf(str1[1]);//优先级
				jcb.pro[j][2] = Integer.valueOf(str1[2]);//指令条数
				jcb.pro[j][3] = Integer.valueOf(str1[3]);//数据段所占的页面数
				jcb.pro[j][4] = Integer.valueOf(str1[4]);//进程所占的总页面上数
				jcb.pro[j][5] = Integer.valueOf(str1[5]);//进程是否需要同步其他进程或被其他进程同步
			}
			for(int j=0;j<pron;j++)
			{
				jcb.pro_position[j] = Integer.valueOf(bfff.readLine());//该进程外页表的位置
			}
			this.jq.join(jcb);//加入作业队列
		}
	}
	
	
	class Dispatch extends Thread{//作业调度线程
	
		public Dispatch()//构造函数
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
				
				synchronized (clock) {//互斥使用Clock类的对象clock
	                if(!DispatchInfor) {//若时钟中断没有到来
	                    try {
							clock.wait();//阻塞自身
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                System.out.print(clock.GetTime() + ":");//显示当前的时间
	                
	                if(clock.GetTime() % 1000 == 0)//每隔1秒更新每一个页框的多位寄存器
	                {
	                	memory.Update();//更新多位寄存器
	                }	
	                
	               /*if(xxstartfalg)
	                {
	                	pauseflag = true;
	                }*/
	                 
	                while(pauseflag)//接受到暂停信号，死循环
	                {
	                	 try {
							Thread.sleep(1);
						} catch (InterruptedException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
	                }
	                
	                //作业调度
	                if( jq.size() > 0 && clock.GetTime() >= jq.front.InTime)//若作业队列非空且该时刻有作业到达
	                {
	                	Job_Num++;//系统中的作业数目+1
	                	JCB jcb = jq.front;//获取作业队列的头队列
	                	System.out.println(clock.GetTime() + ":第" + Job_Num + "个作业到达" + "  包含" + jcb.PRO_Number + "个进程 ");//显示	                	
	                	for(int i=0;i<jcb.PRO_Number;i++)//为每一个进程分配资源和创建pcb节点
	                	{	
	                		System.out.println("进程号:" + Process_Num + "  指令数:" + jcb.pro[i][2] + "  总页面数:" + jcb.pro[i][4]);
	                		try {
	                			PCB pcb = this.createpcb(jcb,i);//创建jcb的第i个进程
								pcbtable.ReadyQueue.join(pcb);//创建该作业的每一个进程并将进程加入就绪队列
								
								mmu.AddPagelist();//为新进程分配页表

								for(int j=0;j<jcb.pro[i][4];j++)//为每个进程的每个页分配内存
								{
									int k = Memory.Allocate(Process_Num , j);
									//System.out.print(k + "|");
									if(k == -1)//未分配成功
									{
										mmu.Pagelist[Process_Num].Insert(j);//更新该进程的页表
									}else {//分配成功
										mmu.Pagelist[Process_Num].Insert(j , k);//更新该进程的页表
										/*Memory.FreePageNum -= jcb.pro[i][4];
										Memory.UsedPageFrameNum += jcb.pro[i][4];	*/
									}				
								}
								System.out.println("");//换行
								
								Process_Num++;//系统中进程数+1
							} catch (Exception e) {
								e.printStackTrace();
							}	 
							
	                	}
	                	jq.quit();//将已经调度成进程的作业出队
	                }
	                //中级调度
	                
	                //低级调度
	                if(pcbtable.RunQueue == null && pcbtable.ReadyQueue.size() == 0 && pcbtable.WaitQueue.size() == 0 && jq.size() ==0)//若所有队列都为空
	                {
	                	IsEnd = true;//若所有进程已经执行完，更改标志位，退出循环
	                	int a[] = Memory.show();//获取每一个页框中占用的进程号
	                	int b[] = Memory.show1();//获取每一个页框中占用的进程的页号
	                	inter.altermemory(a,b);//更新内存显示界面
	                	//wat.update(0,0,0);
	                	System.out.println("all job done!");//输出所有作业完成的信息
	                	break;//退出大循环
	                }
	                
	                //判断等待队列是否有进程可以唤醒
	               /* while((pcbtable.WaitQueue.size() > 0) && (pcbtable.WaitQueue.front().WaitState == 0) && (clock.GetTime() - pcbtable.WaitQueue.front().timeflag) >= pcbtable.WaitQueue.front().instruction[pcbtable.WaitQueue.front().PSW].Instr_TotalTime)
	                {
	                	pcbtable.WaitQueue.front().PSW++;//执行完该I/O指令
	                	pcbtable.WaitQueue.front().WaitState = -1;//修改标志位
	                	System.out.print("{" + pcbtable.WaitQueue.front().Pro_ID + "stop wait}");
	                	pcbtable.proinfo[pcbtable.WaitQueue.front().Pro_ID][2] = 1;
	                	pcbtable.proinfo[pcbtable.WaitQueue.front().Pro_ID][3]++;
	                	pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());//加入就绪队列
	                	pcbtable.WaitQueue.quit();//退出等待队列
	                }*/
	               
	               if(pcbtable.WaitQueue.size() > 0)//如果等待队列非空，寻找可以唤醒的进程
	                {
	                	int size = pcbtable.WaitQueue.size();//获取等待队列里节点的个数
	                	PCB [] xy= new PCB[size];//存放不能被唤醒的进程
	                	int num = 0;//记录有多少进程不能被唤醒
	                	PCB p = pcbtable.WaitQueue.front();
	                	for(int i=0;i<size;i++)//检查每一个节点是否可以被唤醒
	                	{
	                		if((p.WaitState == 0) && (clock.GetTime() -p.timeflag) >= p.instruction[p.PSW].Instr_TotalTime)//如果该进程因I/O操作中断且I/O操作结束
	                		{
	                			p.PSW++;//运行完该条I/O指令
	                			if(p.PSW > 200 && p.synchronousflag != -1)//如果该进程同步其他进程且该该进程已运行了200条指令
	                			{
	                				buy[p.Pro_ID] = true;//修改该进程的同步标志位
	                			}
	                			p.WaitState = -1;//修改该进程的等待标志位为没有等待
	                			System.out.print("{" + p.Pro_ID + "stop wait}");
	                			pcbtable.proinfo[p.Pro_ID][2] = 1;//更新进程状态为就绪态
	    	                	pcbtable.proinfo[p.Pro_ID][3]++;//更新进程已运行的指令数
	    	                	pcbtable.ReadyQueue.join(p);//加入就绪队列
	                		}else if((p.WaitState == 1) && buy[p.bechargedflag] == false)//如果有被同步的进程且同步该进程的进程已经运行完200条指令
	                		{
	                			p.WaitState = -1;//修改该进程的等待标志位为没有等待
	                			p.bechargedflag = -1;//修改该进程被同步标志位为不被其他进程同步
	                			pcbtable.proinfo[p.Pro_ID][2] = 1;//更新进程状态为就绪态
	                			pcbtable.ReadyQueue.join(p);//加入就绪队列
	                		}
	                		else {//无法被唤醒，也就不需要出队
	                			xy[num] = p;
	                			num++;//记录不需要出队的进程数目
	                		}
	                		p = p.next;//检查下一个节点
	                	}
	                	
	                	pcbtable.WaitQueue.front = null;//赋值
	                	pcbtable.WaitQueue.rear = null;//赋值
	                	pcbtable.WaitQueue.Queue_Size = 0;//重置等待队列
	                	for(int j=0;j<num;j++)
	                	{
	                		pcbtable.WaitQueue.join(xy[j]);//将无法被唤醒的进程加入等待队列
	                	}
	                	
	                }
	             
	                
	                if(pcbtable.RunQueue != null)//如果当前有进程在运行
	                {
	                	if(pcbtable.RunQueue.bechargedflag != -1 && buy[pcbtable.RunQueue.bechargedflag])//如果该进程需要被同步，且同步它的那个进程还没执行完200条指令
	                	{
	                		pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 2;//记录进程状态为等待态
                			pcbtable.RunQueue.WaitState = 1;//更新等待标志位
                			
                			//System.out.print("{" + pcbtable.RunQueue.Pro_ID + "wait}");
                			pcbtable.WaitQueue.join(pcbtable.RunQueue);//加入等待队列
                			pcbtable.RunQueue = null;//运行态指针改为空指针
                			if(pcbtable.ReadyQueue.size() > 0)//如果就绪队列非空
                			{
                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//将就绪队列队头置为运行态
                				pcbtable.ReadyQueue.quit();//出队
                				PC = pcbtable.RunQueue.PSW;//PC改为该进程的PSW标志位
                				pcbtable.RunQueue.timeflag = clock.GetTime();//设置开始运行时间
                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//修改进程状态信息为运行态
                			}
	                	}
	                	
	                	else{
	                	switch(pcbtable.RunQueue.instruction[PC].Instr_State) {//判断是当前运行态进程要运行哪种指令
	                		case 0://系统调用指令
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                			{
	                				PC++;//PC自增，指向该进程下一条指令;
	                				if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//如果该进程需要同步其他进程且已经运行完200条指令
	                				{
	                					buy[pcbtable.RunQueue.Pro_ID] = false;//修改同步标志位
	                				}
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//已经运行的指令+1
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
	                				{
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;//修改进程状态为完成态
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列
	                					Memory.recover(pcbtable.RunQueue.Pro_ID);//回收其资源
	                					pcbtable.RunQueue = null;//设置空值
	                				}else {
	                					this.Run_to_Ready();//进程切换
	                				}
	                			}
	                			break;
	                		case 1://用户态计算指令
	                			if(pcbtable.RunQueue.instruction[PC].Need_Data == 1)//如果该进程需要访问数据
	                			{
	                				int o;
	                				if(pcbtable.RunQueue.Pro_InstrNum % 256 == 0)
	                				{
	                					o = pcbtable.RunQueue.Pro_InstrNum / 256;
	                				}else {
	                					o = (int)(pcbtable.RunQueue.Pro_InstrNum / 256) + 1;
	                				}
	                		 		for(int y=o;y<pcbtable.RunQueue.PageNum;y++)//该指令需要存放数据段的页面
	                		 		{
	                		 			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , y);//MMU开始工作
	                					System.out.print("[" + y + "," + pageframeid + "]");
		    	                		switch (pageframeid){
		    	                			case -1:
		    	                				mmu.OutDealing();//越界中断处理
		    	                				break;
		    	                			case -2:
		    	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , y);//缺页中断处理，将缺页调入内存
		    	                				break;
		    	                			default://正常访问
		    	                				break;
		    	                		}
	                		 		}
	                			}
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                			{
	                				//System.out.println("***");
	                				PC++;//PC自增，指向该进程下一条指令;
	                				if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//如果该进程需要同步其他进程且已经运行完200条指令
	                				{
	                					buy[pcbtable.RunQueue.Pro_ID] = false;//修改同步标志位
	                				}
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//已经运行的指令+1
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
	                				{
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;//已经运行的指令+1
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列
	                					Memory.recover(pcbtable.RunQueue.Pro_ID);//回收其资源
	                					pcbtable.RunQueue = null;//设置空值
	                				}
	                				else if(clock.GetTime() - pcbtable.RunQueue.timeflag > 1000)//时间片到
	                				{
	                					this.Run_to_Ready();//进程切换
	                				}
	                			}
	                			break;
	                		case 2://PV操作指令
	                			if(pcbtable.RunQueue.instruction[PC].Need_Data == 1)//如果该进程需要访问数据
	                			{
	                				int o;
	                				if(pcbtable.RunQueue.Pro_InstrNum % 256 == 0)
	                				{
	                					o = pcbtable.RunQueue.Pro_InstrNum / 256;
	                				}else {
	                					o = (int)(pcbtable.RunQueue.Pro_InstrNum / 256) + 1;
	                				}
	                		 		for(int y=o;y<pcbtable.RunQueue.PageNum;y++)//该指令需要存放数据段的页面
	                		 		{
	                		 			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , y);//MMU开始工作
	                					System.out.print("[" + y + "," + pageframeid + "]");
		    	                		switch (pageframeid){
		    	                			case -1:
		    	                				mmu.OutDealing();//越界中断处理
		    	                				break;
		    	                			case -2:
		    	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , y);//缺页中断处理，将缺页调入内存
		    	                				break;
		    	                			default://正常访问
		    	                				break;
		    	                		}
	                		 		}
	                			}
	                			if(Is_PV_Occupy == false || Is_PV_Occupy == true && PV_ProID == pcbtable.RunQueue.Pro_ID)//可以访问临界区
	                			{
	                				Is_PV_Occupy = true;//设置临界资源被占用标志位
	                				PV_ProID = pcbtable.RunQueue.Pro_ID;
	                				
	                				pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//指令运行10ms
	                				if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//当前指令运行完毕
	                				{
	                					PC++;//PC自增，指向该进程下一条指令;
	                					if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//如果该进程需要同步其他进程且已经运行完200条指令
		                				{
		                					buy[pcbtable.RunQueue.Pro_ID] = false;//修改同步标志位
		                				}
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//已经运行的指令+1
	                					Is_PV_Occupy = false;//其他进程可以访问临界资源
	                					if(PC == pcbtable.RunQueue.Pro_InstrNum)//如果该进程所有指令全部运行完毕
		                				{
	                						pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;
		                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//将该进程加入完成队列
		                					Memory.recover(pcbtable.RunQueue.Pro_ID);//回收其资源
		                					pcbtable.RunQueue = null;//设置空值
		                				}	
	                				}	               
	                			}else {
	                				this.Run_to_Wait();//进程切换
	                			}
	      	                    break;
	                		case 3://I/O操作指令
	                			pcbtable.RunQueue.PSW = PC;//现场保存
	                			pcbtable.RunQueue.timeflag = clock.GetTime();//获取当前时间
	                			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 2;//记录进程状态
	                			pcbtable.RunQueue.WaitState = 0;//更新等待标志位
	                			
	                			System.out.print("{" + pcbtable.RunQueue.Pro_ID + "wait}");//显示
	                			pcbtable.WaitQueue.join(pcbtable.RunQueue);//加入等待队列
	                			
	                			pcbtable.RunQueue = null;//运行态指针置为空指针
	                			
	                			if(pcbtable.ReadyQueue.size() > 0)//如果就绪队列非空
	                			{
	                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//将就绪队列队头置为运行态
	                				pcbtable.ReadyQueue.quit();//出队
	                				PC = pcbtable.RunQueue.PSW;//现场恢复
	                				pcbtable.RunQueue.timeflag = clock.GetTime();//获取当前时间
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//修改状态标志位为运行态
	                			}
	               
	                			break;
	                		default:
	                			break;
	    		          }
	                	}
	                	
	                }else {//如果当前没有进程在运行
	                	if(pcbtable.ReadyQueue.size() != 0)//若就绪队列非空
	                	{
	                		pcbtable.RunQueue = pcbtable.ReadyQueue.front();//将就绪队列队头置为运行态
	                		pcbtable.ReadyQueue.quit();//出队
	                		PC = pcbtable.RunQueue.PSW;//现场恢复
	                		pcbtable.RunQueue.timeflag = clock.GetTime();//获取当前时间
	                		
	                		pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//更新进程状态为运行态
	                		
	                		int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , PC / 256);//MMU开始工作
	                		switch (pageframeid){
	                			case -1:
	                				mmu.OutDealing();//越界中断处理
	                				//System.out.println("x");
	                				break;
	                			case -2:
	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , PC / 256);//缺页中断处理，将缺页调入内存
	                				//System.out.println("y");
	                				break;
	                			default:
	                				//System.out.println("z");
	                				Memory.Visit(pageframeid);	                				
	                				break;
	                		}
	                	}
	                }
	                
	                
	                if(pcbtable.RunQueue != null)//如果当前有进程在运行
	                {
	                	System.out.print("当前运行进程:" + pcbtable.RunQueue.Pro_ID);//显示
	                }else {//若当前没有进程在运行
	                	System.out.print("当前运行进程:null\t");//输出null
	                }
	                
	                System.out.print("当前就绪队列:");//显示
	                if(pcbtable.ReadyQueue.size() > 0)//若当前就绪队列非空
	                {
	                	pcbtable.ReadyQueue.Show_PCB();//显示就绪队列
	                }else {
	                	System.out.print("null\t");//输出null;
	                }
	                
	                System.out.print("当前等待队列:");//显示
	                if(pcbtable.WaitQueue.size() > 0)//若当前等待队列非空
	                {
	                	pcbtable.WaitQueue.Show_PCB();//显示等待队列
	                }else {
	                	System.out.print("null\t");//输出null
	                }
	                
	                System.out.print("当前完成队列:");//显示
	                if(pcbtable.FinishQueue.size() > 0)//若当前完成队列非空
	                {
	                	pcbtable.FinishQueue.Show_PCB();//显示完成队列
	                }else {
	                	System.out.print("null\t");//输出null
	                }
	               
	                //mmu.Pagelist[pcbtable.RunQueue.Pro_ID].show();
	                 System.out.println("");//换行
	          
	                int a[] = Memory.show();//获取每一个页框被占用的进程号
	                int b[]= Memory.show1();//获取每一个页框被占用的进程的页号
	                inter.altermemory(a,b);//更新界面上内存分配信息
	             
	                inter.alterpro(pcbtable.proinfo,Process_Num);//更新页面上进程信息
	                inter.altertlb(mmu.tlb.gettlb());//修改页面上快表信息
	                
	                DispatchInfor = false;//调度标志位置为false
	                clock.notifyAll();//唤醒其他等待线程
				}
			}
		}
		
		public PCB createpcb(JCB jcb,int i)throws Exception//创建jcb号作业的第i个进程，返回pcb
		{
			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,clock.GetTime(),jcb.pro[i][2],jcb.pro[i][4],jcb.pro[i][5]);//调用构造函数创建进程
			buy[Process_Num] = true;
			pcbtable.proinfo[Process_Num][0] = Process_Num;//记录进程号
			pcbtable.proinfo[Process_Num][1] = jcb.JCB_ID;//记录所属的作业号
			pcbtable.proinfo[Process_Num][2] = 1;//记录进程状态为就绪态
			pcbtable.proinfo[Process_Num][3] = 0;//记录进程已经运行的指令数
			pcbtable.proinfo[Process_Num][4] = jcb.pro[i][2];//记录进程总指令数
			
			pcb.Pro_InstrNum = jcb.pro[i][2];//指令数目
			pcb.instruction = new Instruction[jcb.pro[i][2]];//建立指令链表
						
			File file1 = new File("Disk\\Cylinder\\Track_" + Integer.toString((int)(jcb.pro_position[i] / 64)) + 
					"\\Sector_" + Integer.toString((int)(jcb.pro_position[i] % 64)) + ".txt");//打开存放该进程外页表的文件
			BufferedReader bf1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));//打开文件
			String str1;//存放每次在外页表中读取的一行数据
			String strs1[] = new String[6];//存放读取的一行数据分解后的数据
			int j = pcb.Pro_InstrNum;//赋值
			int flagx = 1;//标志位
			int c = 0;//记录已经读入的指令条数
			while((str1 = bf1.readLine()) != null)//如果未读完
			{
				if(flagx == 0) break;
				strs1 = str1.split("\t");//将读入的一行字符串分解
				File file2  = new File("Disk\\Cylinder\\Track_" + Integer.valueOf(strs1[3]) + 
					"\\Sector_" + Integer.valueOf(strs1[4]) + ".txt");//根据外页表找到对应的页
				//System.out.println(strs1[3] + "--------" +strs1[4]);
				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));//打开进程存放指令的每一页
				if(j <= 256 && j > 0)//若剩下未读入的指令数小于等于256条
				{
					flagx = 0;//修改标志位
					for(int l=0;l<j;l++)//读取每一行指令
					{
						try{
							String strs2[] = bf2.readLine().split("\t");//分解每一行的字符串
							pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));//增加一个指令节点
						}catch(Exception e) {}
					}
					
				}else if(j > 256)//若未读的指令大于256条
				{
					j -= 256;//读256条指令
					for(int l=0;l<256;l++)//读每一条指令
					{
						String strs2[] = bf2.readLine().split("\t");//分解每一行的字符串
						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));//增加一个指令节点
					}
				}
			}
			return pcb;//返回创建好的进程
		}
		
		public void Run_to_Ready()//进程切换，运行态进程加入就绪队列，就绪队列队首进入运行态
		{
			pcbtable.RunQueue.PSW = PC;//现场保护
			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 1;//更新进程状态为就绪态
			pcbtable.ReadyQueue.join(pcbtable.RunQueue);//加入就绪队列
			pcbtable.RunQueue = pcbtable.ReadyQueue.front();//进程切换
			pcbtable.ReadyQueue.quit();//出队
			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//更新新的进程的状态为运行态
			PC = pcbtable.RunQueue.PSW;//现场恢复
			pcbtable.RunQueue.timeflag = clock.GetTime();//记录开始运行时间
			
			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , PC / 256);//MMU开始工作
			System.out.print("pc = " + PC + "[" + PC / 256 + "," + pageframeid + "]");
    		switch (pageframeid){
    			case -1:
    				mmu.OutDealing();//越界中断处理
    				break;
    			case -2:
    				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , PC / 256);//缺页中断处理，将缺页调入内存
    				break;
    			default://正常访问
    				break;
    		}
		}
		
		public void Run_to_Wait()////进程切换，运行态进程加入阻塞队列，就绪队列队首进入运行态
		{
			pcbtable.RunQueue.PSW = PC;//现场保护
			pcbtable.WaitQueue.join(pcbtable.RunQueue);//加入等待队列
			if(pcbtable.ReadyQueue.size() > 0)//如果就绪队列非空
			{
				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//进程切换
				pcbtable.ReadyQueue.quit();//出队
				PC = pcbtable.RunQueue.PSW;//现场恢复
				pcbtable.RunQueue.timeflag = clock.GetTime();//记录开始运行时间
			}
		}
		
		/*public void Wake()//唤醒处于阻塞队列的
		{
			if(pcbtable.WaitQueue.size() > 0)//
			{
				pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
				pcbtable.WaitQueue.quit();
			}
		}*/
	}
}


