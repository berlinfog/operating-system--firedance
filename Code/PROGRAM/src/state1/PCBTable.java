package state1;

public class PCBTable {
	protected PCB RunQueue;//ָ���������еĽ���
	protected MyQueue ReadyQueue;//��������
	protected MyQueue WaitQueue;//�ȴ�����
	protected MyQueue FinishQueue;//����ɶ���
	protected int[][] proinfo;//���ÿ�����̵���ϸ��Ϣ�����½���ʱ��
	
	public PCBTable()
	{
		//this.RunQueue = new PCB();
		this.RunQueue = null;//��ʼ�����нڵ�
		this.ReadyQueue = new MyQueue();//��ʼ���ȴ�����
		this.WaitQueue = new MyQueue();//��ʼ���ȴ�����
		this.FinishQueue = new MyQueue();//��ʼ����ɶ���
		this.proinfo = new int[34][5];//��ʼ����ά����
	}
}
