package state1;

public class Instruction {
	protected int Instr_ID;//指令序号
	protected int Instr_State;//指令类型，0表示系统调用，1表示用户态计算操作，2表示PV操作
	protected int Need_Data;//指令是否需要访问数据段，1表示需要，0表示不需要
	protected int Instr_TotalTime;//指令总时间
	protected int Instr_RunTime;//指令已经运行的时间
	protected int Instr_Addr;//指令的地址

	
	public Instruction(int ID , int State , int NeedData , int TotalTime)//构造函数
	{ 
		this.Instr_ID = ID;
		this.Instr_State = State;
		this.Need_Data = NeedData;
		this.Instr_TotalTime = TotalTime;
		this.Instr_RunTime = 0; 
	}
	
	/*public void Change_ID()
	{
		
	}
	
	public void Change_State()
	{
		
	}*/
}
