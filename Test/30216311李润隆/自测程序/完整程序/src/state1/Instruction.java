package state1;

public class Instruction {
	protected int Instr_ID;//ָ�����
	protected int Instr_State;//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����
	protected int Need_Data;//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
	protected int Instr_TotalTime;//ָ����ʱ��
	protected int Instr_RunTime;//ָ���Ѿ����е�ʱ��
	protected int Instr_Addr;//ָ��ĵ�ַ

	
	public Instruction(int ID , int State , int NeedData , int TotalTime)//���캯��
	{ 
		this.Instr_ID = ID;//ָ�����
		this.Instr_State = State;//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����
		this.Need_Data = NeedData;//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
		this.Instr_TotalTime = TotalTime;//ָ����ʱ��
		this.Instr_RunTime = 0; //ָ���Ѿ����е�ʱ��
	}

}
