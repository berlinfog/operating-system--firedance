package state1;

public class JobQueue {
	protected JCB front;//��ͷ
	protected JCB rear;//��β	
	protected int Queue_Size;
	
	public JobQueue()//��ʼ������
	{	
		this.Queue_Size = 0;
		this.front = null;
		this.rear = this.front;
	}		
	public boolean IsEmpty()//�ж��Ƿ�Ϊ��
	{		
		if(this.rear == this.front && this.front == null) return true;
		else {
			return false;
		}
	}	
		
	public void join(JCB e)//���
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
	
	public void quit()//����
	{
		if(this.front == this.rear) System.out.println("����Ϊ�ն���");
		else {
			this.front = this.front.next;
		}
		this.Queue_Size--;
	}
	
	public JCB front()//��ͷ��ȡ
	{
		return this.front;
	}
	
	public JCB rear()//��β��ȡ
	{
		return this.rear;
	}
	
	public int size()//���ش�С
	{
		return this.Queue_Size;
	}
	
	public void Show_JCB()//��ʾJCB����
	{
		JCB jcb = this.front;
		while(jcb != null)
		{
			System.out.println(jcb.JCB_ID);
			jcb = jcb.next;
		}
	}
}
