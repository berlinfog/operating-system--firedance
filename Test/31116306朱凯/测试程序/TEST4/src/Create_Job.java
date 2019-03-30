import java.io.FileNotFoundException;
import java.util.Scanner;

public class Create_Job {
		protected int Job_Number;
		protected int time = 0;
		protected JobQueue jobqueue;
		public Create_Job() throws FileNotFoundException//构造函数
		{
			this.jobqueue = new JobQueue();
			this.Job_Number = 5;
			for(int i=0;i<this.Job_Number;i++)
			{
				JCB jcb = new JCB(i+1,time);
				jcb.Create();
				jobqueue.join(jcb);
				time += (int)(100 * (2 + 4 * Math.random()));//每隔200-600毫秒产生一个作业请求
			}
		}
		
		public static void main (String [] args) throws Exception
		{	
			int Process_Num = 0;
			Create_Job cj = new Create_Job();
			Memory memory = new Memory();
			memory.Init_Page();
			MMU mmu = new MMU();
			
			while(cj.jobqueue.size() > 0)
			{
				JCB jcb = cj.jobqueue.front();
				for(int i=0;i<jcb.PRO_Number;i++)
            	{	
					mmu.AddPagelist();//为新进程分配页表
					for(int j=0;j<jcb.pro[i][4];j++)//为每个进程的每个页分配内存
					{
						int k = Memory.Allocate(Process_Num , j);
						if(k == -1)//未分配成功
						{
							System.out.println("第" + Process_Num + "个进程第" + j + "当前内存页框已满，该页面仍存放在外存中");
							mmu.Pagelist[Process_Num].Insert(j);//更新该进程的页表
						}else {//分配成功
							System.out.println("第" + Process_Num + "个进程第" + j + "个页面在内存中的页框号为：" + k);
							mmu.Pagelist[Process_Num].Insert(j , k);//更新该进程的页表
						}				
					}
					Process_Num++;
            	}
				cj.jobqueue.quit();
			}
			System.out.println("所有作业已经调度完毕");
			for(int i=0;i<Process_Num;i++)
			{
				System.out.println("第" + i + "个进程的页表：");
				mmu.Pagelist[i].show();
			}
			System.out.println("当前的快表：");
			mmu.tlb.show();
			
			while(true)
			{
				System.out.println("请输入要访问的进程号和页号,输入-1 -1退出循环");
				Scanner sc = new Scanner(System.in);
				int pronum = sc.nextInt();
				int pagenum = sc.nextInt();
				if(pronum == -1 && pagenum ==-1) break;
				if(pronum >= Process_Num) System.out.println("没有该进程");
				else {
					int pageframeid = mmu.StartJob(pronum,pagenum);//MMU开始工作
					switch (pageframeid){
	    				case -1:
	    					System.out.println("产生越界中断");
	    					mmu.OutDealing();//越产生越界中断界中断处理
	    					break;
	    				case -2:
	    					System.out.println("产生缺页中断");
	    					mmu.LostDealing(pronum,pagenum);//缺页中断处理，将缺页调入内存
	    					break;
	    				default:
	    					System.out.println("内存中有该页面");
	    					break;
					}
					System.out.println("该进程的页表：");
					mmu.Pagelist[pronum].show();
					System.out.println("当前快表：");
					mmu.tlb.show();
				}
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
