package state1;

public class PCBTable {
	protected PCB RunQueue;//指向正在运行的进程
	protected MyQueue ReadyQueue;//就绪队列
	protected MyQueue WaitQueue;//等待队列
	protected MyQueue FinishQueue;//已完成队列
	protected int[][] proinfo;//存放每个进程的详细信息，更新界面时用
	
	public PCBTable()
	{
		//this.RunQueue = new PCB();
		this.RunQueue = null;//初始化运行节点
		this.ReadyQueue = new MyQueue();//初始化等待队列
		this.WaitQueue = new MyQueue();//初始化等待队列
		this.FinishQueue = new MyQueue();//初始化完成队列
		this.proinfo = new int[34][5];//初始化二维数组
	}
}
