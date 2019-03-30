package state1;

public class JobQueue {
	protected JCB front;//��ͷ
	protected JCB rear;//��β	
	protected int Queue_Size;//�����нڵ����
	
	public JobQueue()//���캯��
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
		
	public void join(JCB e)//���
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
		this.Queue_Size++;//�ڵ����+1
	}	
	
	public void quit()//����
	{
		if(this.front == this.rear) {this.front = null;}//���ֻ��һ���ڵ�
		else {
			this.front = this.front.next;//�����ֻһ���ڵ�
		}
		this.Queue_Size--;//�ڵ����-1
	}
	
	public JCB front()//���ض�ͷ
	{
		return this.front;//���ض�ͷ
	}
	
	public JCB rear()//���ض�β
	{
		return this.rear;//���ض�β
	}
	
	public int size()//���ض����нڵ����
	{
		return this.Queue_Size;//���ض����нڵ����
	}
	
	public void Show_JCB()//��ʾJCB����
	{
		JCB jcb = this.front;//ָ��ͷ���
		while(jcb != null)//���ýڵ�ǿ�
		{
			System.out.println(jcb.JCB_ID);//��ʾ�ýڵ���ҵ��
			jcb = jcb.next;//ָ����һ���ڵ�
		}
	}
}
