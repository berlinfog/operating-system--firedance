package state1;

public class MyQueue
{		
	protected PCB front;//��ͷ
	protected PCB rear;//��β	
	protected int Queue_Size;//���нڵ����
	
	public MyQueue()//���캯��
	{	
		this.Queue_Size = 0;//��ʼ���ڵ����
		this.front = null;//��ʼ��ͷ���
		this.rear = this.front;//��ʼ��β�ڵ�
	}		
	public boolean IsEmpty()//�ж��Ƿ�Ϊ��
	{		
		if(this.rear == this.front && this.front == null) return true;//����Ϊ��
		else {
			return false;//���ز�Ϊ��
		}
	}	
		
	public void join(PCB e)//���
	{		
		if(this.front == null)//�������Ϊ��
		{
			this.front = e;//���
			this.rear = e;//���
		}
		else {
			this.rear.next = e;//������в�Ϊ�����
			this.rear = e;//���
		}
		this.Queue_Size++;//���нڵ���+1
	}	
	
	public void quit()//����
	{
		if(this.front == this.rear) {this.front = null;}//���ֻ��һ���ڵ�
		else {
			this.front = this.front.next;//�����ֻһ���ڵ�
		}
		this.Queue_Size--;//�ڵ����-1
	}
	
	public PCB front()//���ض�ͷ
	{
		return this.front;//���ض�ͷ
	}
	
	public PCB rear()//���ض�β
	{
		return this.rear;//���ض�β
	}
	
	public int size()//���ض����нڵ����
	{
		return this.Queue_Size;//���ض����нڵ����
	}
	
	public void Show_PCB()//��ʾPCB����
	{
		if(this.size() == 0 )//�������Ϊ��
		{
			System.out.println("null");//��ʾΪ��
		}
		PCB pcb = this.front;//ָ��ͷָ��
		for(int i=0;i<this.size();i++)
		{
			System.out.print(pcb.Pro_ID + " ");//������̺�
			pcb = pcb.next;//ָ����һ���ڵ�
		}
	}
}



