package state1;

public class PCBTable {
	protected PCB RunQueue;//指向正在运行的进程
	protected MyQueue ReadyQueue;//就绪队列
	protected MyQueue WaitQueue;//等待队列
	protected MyQueue FinishQueue;//已完成队列
	
	public PCBTable()
	{
		this.RunQueue = new PCB();
		this.RunQueue = null;
		this.ReadyQueue = new MyQueue();
		this.WaitQueue = new MyQueue();
		this.FinishQueue = new MyQueue();
	}
}
