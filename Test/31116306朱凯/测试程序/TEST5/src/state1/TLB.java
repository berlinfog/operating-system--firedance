package state1;

public class TLB {//快表类
	protected final static int Number = 5;//假设能存储五个快表项
	
	protected static boolean IsEmpty[];//该快表项是否为空
	
	protected static int ProID[];//进程号
	protected static int PageID[];//页号
	protected static int PageFrameID[];//页框号
	protected static int Flag[];//标志位，记录该快表项多久未被命中
	
	protected static int ItemNumber;//当前有多少快表项
	
	protected int oldproid;//记录要被替换出去的项的进程号
	protected int oldpageid;//记录要被替换出去的项的页号
	protected int oldpageframeid;//记录要被替换出去的项的页框号
	
	public TLB()//构造函数
	{
		this.IsEmpty = new boolean[Number];//分配空间
		for(int i=0;i<Number;i++)
		{
			this.IsEmpty[i] = true;//每一项置为空
		}
		
		this.ProID = new int[Number]; //分配空间
		this.PageID = new int[Number];//分配空间
		this.PageFrameID = new int[Number];//分配空间
		this.Flag = new int[Number];//分配空间
		this.ItemNumber = 0;//当前已经有的快表项数目
	}
	
	public int VisitTLB(int proid , int pageid)//访问快表
	{
		for(int i=0;i<Number;i++)//遍历每一个快表项
		{
			if(this.IsEmpty[i] == false && this.ProID[i] == proid && this.PageID[i] == pageid)//若快表命中
			{
				for(int j=0;j<Number;j++)//遍历每一个快表项
				{
					if(this.IsEmpty[j] == false && j != i)//访问其他快表项
					{
						this.Flag[j]++;//将其他快表项的访问位+1
					}
				}
				return this.PageFrameID[i];//快表命中返回页框号
			}
		}
		return -1;//快表未命中返回-1
	}
		
	public boolean AlterTLB(int proid , int pageid , int pageframeid)//更新快表
	{
		if(this.ItemNumber < Number)//如果快表有空余位置，不需要替换,直接添加
		{
			this.ProID[ItemNumber] = proid;//赋值
			this.PageID[ItemNumber] = pageid;//赋值
			this.PageFrameID[ItemNumber] = pageframeid;//赋值
			this.IsEmpty[ItemNumber] = false;//修改标志位
			this.Flag[ItemNumber] = 0;//修改访问标志位
			for(int j=0;j<ItemNumber;j++)//将其他快表项的flag++
			{
				this.Flag[j]++;//将其他快表项访问标志位+1
			}	
			ItemNumber++;//快表项数+1
			return true;//若快表有空余位置返回 true
		}
		else {//快表无空余位置，需要更新快表
			int old = Calculate();
			//System.out.print(old + "/");
			
			this.oldproid = this.ProID[old];//记录要被替换出去的快表项的进程号
			this.oldpageid = this.PageID[old];//记录要被替换出去的快表项的进程的页号
			this.oldpageframeid = this.PageFrameID[old];//记录要被替换出去的快表项的进程的页框号
		
			this.ProID[old] = proid;//更新信息
			this.PageID[old] = pageid;//更新信息
			this.PageFrameID[old] = pageframeid;//更新信息
			this.IsEmpty[old] = false;//修改标志位
			this.Flag[old] = 0;//更新访问标志位
			
			for(int i=0;i<Number;i++)//遍历
			{
				if(i != old)
				{
					this.Flag[i]++;//其他快表项的访问标志位+1
				}
			}
			return false;//返回false
		}
	}
	
	public int Calculate()//返回最久未被访问的快表项的下标
	{
		int max = -1;//记录访问标志位的最大值
		int maxflag = 0;//记录访问标志位最大的快表项的下标
		for(int i=0;i<Number;i++)//遍历每一个快表项
		{
			if(max <= this.Flag[i])//找到更大的访问标志位值
			{
				max = this.Flag[i];//赋值
				maxflag = i;//赋值
			}
		}
		return maxflag;//返回访问标志位最大值的快表项下标
	}
	
	public int[][] gettlb()//获取当前时刻tlb的存放信息，用于更新快表界面
	{
		int a[][] = new int[5][5];//定义数组，存放tlb信息
		for(int i=0;i<5;i++)//循环
		{
			a[i][0] = i;//快表项下标
			a[i][1] = this.ProID[i];///存放的进程号
			a[i][2] = this.PageID[i];//存放的进程的页号
			a[i][3] = this.PageFrameID[i];//存放的进程的页框号
			if (this.IsEmpty[i] == true)//该项是否为空
			{
				a[i][4] = 0;//为空赋值0
			}else {
				a[i][4] = 1;//不为空赋值1
			}
		}
		return a;//返回该二维数组
	}
}
