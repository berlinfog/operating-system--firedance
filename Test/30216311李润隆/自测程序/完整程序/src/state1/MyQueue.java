package state1;

public class MyQueue
{		
	protected PCB front;//队头
	protected PCB rear;//队尾	
	protected int Queue_Size;//队列节点个数
	
	public MyQueue()//构造函数
	{	
		this.Queue_Size = 0;//初始化节点个数
		this.front = null;//初始化头结点
		this.rear = this.front;//初始化尾节点
	}		
	public boolean IsEmpty()//判断是否为空
	{		
		if(this.rear == this.front && this.front == null) return true;//返回为空
		else {
			return false;//返回不为空
		}
	}	
		
	public void join(PCB e)//入队
	{		
		if(this.front == null)//如果队列为空
		{
			this.front = e;//入队
			this.rear = e;//入队
		}
		else {
			this.rear.next = e;//如果队列不为空入队
			this.rear = e;//入队
		}
		this.Queue_Size++;//队列节点数+1
	}	
	
	public void quit()//出队
	{
		if(this.front == this.rear) {this.front = null;}//如果只有一个节点
		else {
			this.front = this.front.next;//如果不只一个节点
		}
		this.Queue_Size--;//节点个数-1
	}
	
	public PCB front()//返回队头
	{
		return this.front;//返回队头
	}
	
	public PCB rear()//返回队尾
	{
		return this.rear;//返回队尾
	}
	
	public int size()//返回队列中节点个数
	{
		return this.Queue_Size;//返回队列中节点个数
	}
	
	public void Show_PCB()//显示PCB队列
	{
		if(this.size() == 0 )//如果队列为空
		{
			System.out.println("null");//显示为空
		}
		PCB pcb = this.front;//指向头指针
		for(int i=0;i<this.size();i++)
		{
			System.out.print(pcb.Pro_ID + " ");//输出进程号
			pcb = pcb.next;//指向下一个节点
		}
	}
}



