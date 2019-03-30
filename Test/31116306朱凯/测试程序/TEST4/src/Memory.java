
public class Memory {
	public final static int MenorySize = 64*512;//仿真内存大小32KB
	public final static int PageFrameNum = 64;//内存中页框数目，共64个
	public final static int PageFrameSize = 512;//每个页框的大小，为512B
	
	public static int UsedPageFrameNum = 0;//已经被占用的页框数
	public static int FreePageNum = 64;//空闲的页框数
	public static int high2 = 0;
	public static int high1 = 0;
	public static int high0 = 0;
	
	protected static PageFrame pageframe[];
	
	
	public Memory()//构造函数
	{
		
	}
	
	public static void Init_Page()//初始化内存
	{
		pageframe = new PageFrame[PageFrameNum];
		for(int i=0;i<PageFrameNum;i++)
		{
			pageframe[i] = new PageFrame();//内存划分为64个页面
		}
	}
	
	public static void Visit(int pageframeid) //访问内存
	{

	}
	
	public static void Update()//更新每个页框的多为寄存器
	{
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				pageframe[i].Update();
			}
		}
	}
	
	public static int Find() //找到最久未被访问的页框号
	{
		int min = 16;
		int k = -1;
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				if(min > pageframe[i].Re)
				{
					min = pageframe[i].Re;
					k = i;
				}
			}
		}
		return k;
	}
	
	public static void Allocate(int pageframeid , int proid , int pageid)//分配资源
	{
		pageframe[pageframeid].Occupy(proid, pageid);
		if(pageframe[pageframeid].Re < 8 )
		{
			pageframe[pageframeid].Re += 8;
		}
	}
	
	public static int Allocate(int proid , int pageid)//分配资源
	{
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == false)
			{
				pageframe[i].Occupy(proid , pageid);
				return i;
			}
		}
		return -1;
	}
	public static void recover(int proid)//回收资源
	{
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == true && pageframe[i].OccupyProID == proid)
			{
				pageframe[i].IsOccupy = false;
			}
		}
		
	}
	
	public static int[] show()//显示某时刻内存情况
	{
		int[] a = new int[64];
		for(int i=0;i<64;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				a[i] = pageframe[i].OccupyProID;
			}else {
				a[i] = -1;
			}
		}
		return a;
	}
	
	public static int[] show1()//显示某时刻内存情况
	{
		int[] a = new int[64];
		for(int i=0;i<64;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				a[i] = pageframe[i].OccpyPageID;
			}else {
				a[i] = -1;
			}
		}
		return a;
	}
	
	public static boolean judge()
	{
		for(int i=0;i<64;i++)
		{
			if(pageframe[i].IsOccupy == false)
			{
				return true;
			}
		}
		return false;
	}
	
	public static int howmanypro()
	{
		int a[] = new int[30];
		int flag = 0;
		for(int i=0;i<64;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				int k=0;
				for(int j=0;j<flag;j++)
				{
					if(pageframe[i].OccupyProID == a[j])
					{
						k=1;
						break;
					}
				}
				if(k == 0)
				{
					a[flag] = pageframe[i].OccupyProID;
					flag++;
				}
			}
		}
		return flag;
	}
	
}

class PageFrame {//页框类
	
	protected int PageFrame_ID;//页框号
	protected boolean IsOccupy;//判断该页框当前是否被进程占用
	protected int OccupyProID;//被占用的进程号
	protected int OccpyPageID;//被占用的进程的哪一页
	
	protected int Re;//多位寄存器，用于实现LRU
	
	
	public PageFrame()//构造函数
	{
		this.IsOccupy = false;
		Re = 0;//置零
	}
	
	public void Occupy(int proid , int pageid)
	{
		this.IsOccupy = true;
		this.OccupyProID = proid;
		this.OccpyPageID = pageid;
	}
	
	public void Update() 
	{
		this.Re = this.Re>>>1;
	}
	
	public void Page_Initial()//页框初始化
	{
		
	}
}
