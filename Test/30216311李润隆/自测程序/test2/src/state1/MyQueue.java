package state1;

public class MyQueue
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
		if(this.front == this.rear) {this.front = null;} 
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



