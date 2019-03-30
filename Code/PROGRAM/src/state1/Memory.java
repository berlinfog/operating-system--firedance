
package state1;

public class Memory {
	public final static int MenorySize = 64*512;//仿真内存大小32KB
	public final static int PageFrameNum = 64;//内存中页框数目，共64个
	public final static int PageFrameSize = 512;//每个页框的大小，为512B
	
	public static int UsedPageFrameNum = 0;//已经被占用的页框数
	public static int FreePageNum = 64;//空闲的页框数
	
	protected static PageFrame pageframe[];//页框类对象数组
	
	
	public Memory()//构造函数
	{
		
	}
	
	public static void Init_Page()//初始化内存
	{
		pageframe = new PageFrame[PageFrameNum];//构造64个页框
		for(int i=0;i<PageFrameNum;i++)//初始化每一个页框
		{
			pageframe[i] = new PageFrame();//内存划分为64个页面
		}
	}
	
	public static void Visit(int pageframeid) //访问内存
	{
		if(pageframe[pageframeid].Re < 8 )//老化算法
		{
			pageframe[pageframeid].Re += 8;//将多位寄存器最高位置1
		}
	}
	
	public static void Update()//更新每个页框的多为寄存器
	{
		for(int i=0;i<PageFrameNum;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true)//如果该页框被占用
			{
				pageframe[i].Update();//更新该页框的多位寄存器
			}
		}
	}
	
	public static int Find() //找到最久未被访问的页框号，老化算法
	{
		int min = 16;//设置最小值的初值，大于二进制的1111
		int k = -1;//记录最久未被访问的页框号的下标
		for(int i=0;i<PageFrameNum;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true)//如果该页框被占用
			{
				if(min > pageframe[i].Re)//如果该页框的多位寄存器中的数值更小
				{
					min = pageframe[i].Re;//记录最小值
					k = i;//更新标志位
				}
			}
		}
		return k;//返回要被替换出去的页面所在的页框号
	}
	
	public static void Allocate(int pageframeid , int proid , int pageid)//分配资源
	{
		pageframe[pageframeid].Occupy(proid, pageid);//分配页框
		if(pageframe[pageframeid].Re < 8 )//老化算法
		{
			pageframe[pageframeid].Re += 8;//将多位寄存器最高位置1
		}
	}
	
	public static int Allocate(int proid , int pageid)//分配资源
	{
		for(int i=0;i<PageFrameNum;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == false)//如果该页框为空
			{
				pageframe[i].Occupy(proid , pageid);//分配资源
				return i;//返回分配的页框号
			}
		}
		return -1;//分配不成功返回-1
	}
	public static void recover(int proid)//回收资源
	{
		for(int i=0;i<PageFrameNum;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true && pageframe[i].OccupyProID == proid)//找到要回收的页框号
			{
				pageframe[i].IsOccupy = false;//置该页框的状态为空
			}
		}
		
	}
	
	public static int[] show()//显示某时刻内存情况
	{
		int[] a = new int[64];//记录每个页框中的进程的进程号
		for(int i=0;i<64;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true)//如果该页框非空
			{
				a[i] = pageframe[i].OccupyProID;//赋值
			}else {//如果该页框为空
				a[i] = -1;///赋-1
			}
		}
		return a;//返回该数组
	}
	
	public static int[] show1()//显示某时刻内存情况
	{
		int[] a = new int[64];//记录每一个页框中的进程的页面号
		for(int i=0;i<64;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true)//如果该页框非空
			{
				a[i] = pageframe[i].OccpyPageID;//赋值
			}else {//如果该页框为空
				a[i] = -1;//赋-1
			}
		}
		return a;//返回该数组
	}
	
	public static boolean judge()//判断该时刻是否内存中有空的页框
	{
		for(int i=0;i<64;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == false)//若发现有空的页框
			{
				return true;//返回true
			}
		}
		return false;//返回false
	}
	
	public static int howmanypro()//统计该时刻内存中有多少进程
	{
		int a[] = new int[30];//记录进程编号
		int flag = 0;//记录进程个数
		for(int i=0;i<64;i++)//遍历每一个页框
		{
			if(pageframe[i].IsOccupy == true)//若该页框被占用
			{
				int k=0;//标志位
				for(int j=0;j<flag;j++)//循环
				{
					if(pageframe[i].OccupyProID == a[j])//检测该进程被统计过
					{
						k=1;//修改标志位
						break;//退出循环
					}
				}
				if(k == 0)//如果该进程没有被统计过
				{
					a[flag] = pageframe[i].OccupyProID;//统计
					flag++;//进程数+1
				}
			}
		}
		return flag;//返回该时刻内存中的进程数
	}
	
}
