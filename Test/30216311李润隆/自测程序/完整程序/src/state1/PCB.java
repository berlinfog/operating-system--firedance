package state1;

import java.util.Stack;

public class PCB {
	protected int Job_ID;//进程所属的作业ID
	protected int Pro_ID;//进程ID
	protected int Pro_Priority;//进程优先级
	
	protected int Pro_ArriveTime;//进程到达时间
	protected int Pro_InTime;//进程创建时间
	
	protected int Pro_State;//进程状态，1运行，2就绪，3等待
	protected int WaitState;//等待标志，0表示因I/O操作而等待，1表示因进程同步而等待，0表示因互斥访问临界资源而等待
	protected int synchronousflag;//同步标志位，-1表示不同步其他进程，为正值i则表示同步第i个进程，当前进程执行完四百条指令后才能开始运行第i个进程
	protected int bechargedflag;//被同步标志位，-1表示不被其他进程同步，为正值i则表示被第i个进程同步，当i进程执行完四百条指令后才该进程
	
	protected int Pro_RunTime;//进程运行时间
	protected int Pro_EndTime;//进程结束时间
	
	protected int timeflag = 0;//在cpu中运行的时间
	
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
		if(this.PCB_Length == this.PCB_Number) return true;//已满返回true
		else {
			return false;//未满返回false
		}
	}
	
	public PCB(int pid)//构造函数
	{
		this.Pro_ID = pid;//赋值
	}
	
	public PCB(int jid , int pid , int priority , int arrivetime , int intime, int instrnum , int pagenum , int flag)//重载构造函数
	{
		this.Job_ID = jid;//赋值
		this.Pro_ID = pid;//赋值
		this.Pro_Priority = priority;//赋值
		this.Pro_ArriveTime = arrivetime;//赋值
		this.Pro_InTime = intime;//赋值
		this.PageNum = pagenum;//赋值
		
		this.Pro_State = 0;//初始化状态
		this.Pro_RunTime = 0;//运行时间
		this.Pro_EndTime = 0;//初始化结束时间
		this.PSW = 0;//初始化PSW
		this.WaitState = -1;//等待标志位
		
		if(flag == 1)//如果需要同步其他进程
		{
			this.synchronousflag = pid+1;//修改同步标志位
			this.bechargedflag = -1;//被同步标志位置为-1
		}else if(flag == -1){//如果该进程被其他进程同步
			this.synchronousflag = -1;//同步标志位置为-1
			this.bechargedflag = pid-1;//修改被同步标志位
		}else {//若没有同步信息
			this.synchronousflag = -1;//修改同步标志位
			this.bechargedflag = -1;//修改被同步标志位
		}
		
		
		this.Pro_InstrNum = instrnum; //赋值
		//this.instruction = new Instruction[InstrNum];
	}
	
	public void Create_Process(CPU cpu)//进程创建原语，包括了申请空白PCB，为新建进程分配资源，初始化PCB和将进程插入就绪队列
	{
		
	}
}
