package state1;

import java.util.Stack;

public class PCB {
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
	
	public void Create_Process(CPU cpu)//进程创建原语，包括了申请空白PCB，为新建进程分配资源，初始化PCB和将进程插入就绪队列
	{
		
	}
}
