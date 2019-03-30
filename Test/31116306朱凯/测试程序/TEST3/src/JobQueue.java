
public class JobQueue {
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
		if(this.front == this.rear) System.out.println("队列为空队列");
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
