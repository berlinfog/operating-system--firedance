package state1;

public class PCBTable {
	protected PCB RunQueue;//ָ���������еĽ���
	protected MyQueue ReadyQueue;//��������
	protected MyQueue WaitQueue;//�ȴ�����
	protected MyQueue FinishQueue;//����ɶ���
	
	public PCBTable()
	{
		this.RunQueue = new PCB();
		this.RunQueue = null;
		this.ReadyQueue = new MyQueue();
		this.WaitQueue = new MyQueue();
		this.FinishQueue = new MyQueue();
	}
}
