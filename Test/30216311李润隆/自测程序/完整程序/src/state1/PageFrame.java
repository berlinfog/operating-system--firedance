package state1;

public class PageFrame {//页框类
	
	protected int PageFrame_ID;//页框号
	protected boolean IsOccupy;//判断该页框当前是否被进程占用
	protected int OccupyProID;//被占用的进程号
	protected int OccpyPageID;//被占用的进程的哪一页
	
	protected int Re;//多位寄存器，用于实现LRU
	
	
	public PageFrame()//构造函数
	{
		this.IsOccupy = false;//置空
		Re = 0;//置零
	}
	
	public void Occupy(int proid , int pageid)//占用该页框
	{
		this.IsOccupy = true;//设置该页框被占用
		this.OccupyProID = proid;//赋值
		this.OccpyPageID = pageid;//赋值
	}
	
	public void Update() //更新每一个页框的多位寄存器
	{
		this.Re = this.Re>>>1;//位运算将多位寄存器的数值无符号位右移一位
	}
	
	public void Page_Initial()//页框初始化
	{
		
	}
}
