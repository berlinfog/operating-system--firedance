package state1;

import java.util.Stack;

public class PCB {
	protected int Job_ID;//������������ҵID
	protected int Pro_ID;//����ID
	protected int Pro_Priority;//�������ȼ�
	
	protected int Pro_ArriveTime;//���̵���ʱ��
	protected int Pro_InTime;//���̴���ʱ��
	
	protected int Pro_State;//����״̬��1���У�2������3�ȴ�
	protected int WaitState;//�ȴ���־��0��ʾ��I/O�������ȴ���1��ʾ�����ͬ�����ȴ���0��ʾ�򻥳�����ٽ���Դ���ȴ�
	protected int synchronousflag;//ͬ����־λ��-1��ʾ��ͬ���������̣�Ϊ��ֵi���ʾͬ����i�����̣���ǰ����ִ�����İ���ָ�����ܿ�ʼ���е�i������
	protected int bechargedflag;//��ͬ����־λ��-1��ʾ������������ͬ����Ϊ��ֵi���ʾ����i������ͬ������i����ִ�����İ���ָ���Ÿý���
	
	protected int Pro_RunTime;//��������ʱ��
	protected int Pro_EndTime;//���̽���ʱ��
	
	protected int timeflag = 0;//��cpu�����е�ʱ��
	
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
		if(this.PCB_Length == this.PCB_Number) return true;//��������true
		else {
			return false;//δ������false
		}
	}
	
	public PCB(int pid)//���캯��
	{
		this.Pro_ID = pid;//��ֵ
	}
	
	public PCB(int jid , int pid , int priority , int arrivetime , int intime, int instrnum , int pagenum , int flag)//���ع��캯��
	{
		this.Job_ID = jid;//��ֵ
		this.Pro_ID = pid;//��ֵ
		this.Pro_Priority = priority;//��ֵ
		this.Pro_ArriveTime = arrivetime;//��ֵ
		this.Pro_InTime = intime;//��ֵ
		this.PageNum = pagenum;//��ֵ
		
		this.Pro_State = 0;//��ʼ��״̬
		this.Pro_RunTime = 0;//����ʱ��
		this.Pro_EndTime = 0;//��ʼ������ʱ��
		this.PSW = 0;//��ʼ��PSW
		this.WaitState = -1;//�ȴ���־λ
		
		if(flag == 1)//�����Ҫͬ����������
		{
			this.synchronousflag = pid+1;//�޸�ͬ����־λ
			this.bechargedflag = -1;//��ͬ����־λ��Ϊ-1
		}else if(flag == -1){//����ý��̱���������ͬ��
			this.synchronousflag = -1;//ͬ����־λ��Ϊ-1
			this.bechargedflag = pid-1;//�޸ı�ͬ����־λ
		}else {//��û��ͬ����Ϣ
			this.synchronousflag = -1;//�޸�ͬ����־λ
			this.bechargedflag = -1;//�޸ı�ͬ����־λ
		}
		
		
		this.Pro_InstrNum = instrnum; //��ֵ
		//this.instruction = new Instruction[InstrNum];
	}
	
	public void Create_Process(CPU cpu)//���̴���ԭ�����������հ�PCB��Ϊ�½����̷�����Դ����ʼ��PCB�ͽ����̲����������
	{
		
	}
}
