package state1;

import java.util.Stack;

public class PCB {
	protected int Job_ID;//������������ҵID
	protected int Pro_ID;//����ID
	protected int Pro_Priority;//�������ȼ�
	
	protected int Pro_ArriveTime;//���̵���ʱ��
	protected int Pro_InTime;//���̴���ʱ��
	
	protected int Pro_State;//����״̬��1���У�2������3�ȴ�
	
	protected int Pro_RunTime;//��������ʱ��
	protected int Pro_EndTime;//���̽���ʱ��
	
	protected int timeflag = 0;//
	
	protected int Pro_TotalTime;//������תʱ��
	protected int PSW;//���̵�ǰִ�е�ָ����
	protected int Pro_InstrNum;//���̰�����ָ����Ŀ
	protected int PageNum;//������ռ��ҳ����
	
	
	
	protected Instruction[] instruction;//ָ������
	
	protected PCB next;//ָ����һ��PCB�ڵ�
	
	protected Stack<Integer> stack;//�ֳ������õ���ջ
	protected boolean Protect_Flag;
	
	protected final static int PCB_Length = 20;//PCB�������󳤶�
	protected static int PCB_Number;//PCB����ǰ�ڵ���
	
	public boolean IsFull()//�ж�PCB�����Ƿ�����
	{
		if(this.PCB_Length == this.PCB_Number) return true;
		else {
			return false;
		}
	}
	
	public PCB()
	{

	}
	
	public PCB(int jid , int pid , int priority , int arrivetime , int intime, int instrnum , int pagenum)//���캯��
	{
		this.Job_ID = jid;
		this.Pro_ID = pid;
		this.Pro_Priority = priority;
		this.Pro_ArriveTime = arrivetime;
		this.Pro_InTime = intime;
		this.PageNum = pagenum;
		
		this.Pro_State = 0;
		this.Pro_RunTime = 0;
		this.Pro_EndTime = 0;
		this.PSW = 0;
		
		this.Pro_InstrNum = instrnum;  
		//this.instruction = new Instruction[InstrNum];
	}
	
	public void Create_Process(CPU cpu)//���̴���ԭ�����������հ�PCB��Ϊ�½����̷�����Դ����ʼ��PCB�ͽ����̲����������
	{
		
	}
}
