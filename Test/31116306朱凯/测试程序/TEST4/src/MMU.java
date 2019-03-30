
public class MMU {
	protected TLB tlb;//快表
	protected PageList[] Pagelist;//页表
	protected int pagelistnumber;//当前有多少张页表
	
	public MMU()//构造函数
	{
		tlb = new TLB();
		Pagelist = new PageList[30];
		for(int i=0;i<30;i++)
		{
			Pagelist[i] = new PageList();//初始化每一张页表，pagelist[i]表示第i个进程的页表
		}
		this.pagelistnumber = 0;
		System.out.println("MMU已经初始化成功!");
	}
	
	public void AddPagelist()//为新进程分配页表
	{
		this.Pagelist[pagelistnumber] = new PageList();
		pagelistnumber++;
	}
	
	public int StartJob(int proid , int pageid)//MMU开始工作
	{
		if(this.JudgeOutofBound(proid,pageid))//如果产生越界
		{
			return -1;//产生越界返回-1
		}else {//未产生越界
			int pageframeid = tlb.VisitTLB(proid,pageid);//访问快表
			if(pageframeid != -1)//若快表命中
			{
				return pageframeid;//返回物理地址
			}
			else {//若快表没有命中，访问页表
				pageframeid = Pagelist[proid].Visit(pageid);
				if(pageframeid != -1)//如果访问页表命中
				{
					if(tlb.AlterTLB(proid,pageid,pageframeid))//更新快表且快表有空余位置
					{
						this.Pagelist[proid].Delete();//在页表中删除对应页表项
					}else {//快表无空余位置
						this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
						this.Pagelist[proid].Delete();
					}
					return pageframeid;//返回物理地址
				}
				else {//产生缺页
					return -2;//产生缺页返回-2
				}
			}
				
		}
	}
	
	public boolean JudgeOutofBound(int proid,int pageid)//判断长度是否越界
	{
		if (this.Pagelist[proid].size() < pageid) return true;
		else return false;
	}
	
	public void LostDealing(int proid , int pageid)//缺页中断处理
	{
		if(!Memory.judge())//若当前内存中没有空闲页框
		{
			int pageframeid = Memory.Find();//要被替换出外存的页框号
			int oldproid = Memory.pageframe[pageframeid].OccupyProID;//要被替换出外存的页面所属的进程号
			int oldpageid = Memory.pageframe[pageframeid].OccpyPageID;//要被替换出外存的页面所属的进程的页面号
			
			for(int i=0;i<this.Pagelist[oldproid].ItemNumber;i++)
			{
				if(this.Pagelist[oldproid].pagelistitem[i].PageID == oldpageid)
				{
					this.Pagelist[oldproid].pagelistitem[i].PageState = false;//在要被替换的页面所属的进程页表中将那一页的状态置为不在内存中
				}
			}
			
			Memory.Allocate(pageframeid , proid , pageid);//分配空间
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;
				}	
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//快表无空余位置
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
				//this.Pagelist[proid].Delete();
			}
		}else {//如果有空页框
			int pageframeid = Memory.Allocate(proid , pageid);//分配空间
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;
				}
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//快表无空余位置
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
				//this.Pagelist[proid].Delete();
			}
		}
		
	}
	
	public void OutDealing()//越界中断处理
	{
		
	}
}

class PageList {//页表类

	protected final int ItemNumber = 10;
	
	protected PageListItem pagelistitem[];
	
	protected int flag;
	
	public PageList()//构造函数
	{
		this.pagelistitem = new PageListItem[this.ItemNumber];
		for(int i=0;i<this.ItemNumber;i++)
		{
			this.pagelistitem[i] = new PageListItem();//初始化每一个页表项
			this.pagelistitem[i].IsEmpty = true;//设置每个页表项为空
		}
	}
	
	public void Replace(int pageid,int pageframeid)//更新页表
	{
		this.pagelistitem[flag].PageID = pageid;
		this.pagelistitem[flag].PageFrameID = pageframeid;
	}
	
	public void Delete()//删除已加入快表的项
	{
		this.pagelistitem[flag].IsEmpty = true;//删除已经调入快表的页表项
	}
	
	public boolean Insert(int pageid,int pageframeid)//加入新的页表项
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == true)//发现空的位置
			{
				//System.out.print("(" + i + ")");
				this.pagelistitem[i].IsEmpty = false;
				this.pagelistitem[i].PageID = pageid;
				this.pagelistitem[i].PageFrameID = pageframeid;
				this.pagelistitem[i].PageState = true;
				this.pagelistitem[i].PageChangeState = false;
				return true;
			}
		}
		return false;//页表没有空余位置，返回false
	}
	
	public boolean Insert(int pageid)//加入新的页表项
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == true)//发现空的位置
			{
				this.pagelistitem[i].IsEmpty = false;
				this.pagelistitem[i].PageID = pageid;
				this.pagelistitem[i].PageState = false;
				this.pagelistitem[i].PageChangeState = false;
				return true;
			}
		}
		return false;//页表没有空余位置，返回false
	}
	
	public int Visit(int pageid)//访问页表
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false && pageid == this.pagelistitem[i].PageID)//在页表中命中
			{
				this.UpdateOthers(i);
				if(this.pagelistitem[i].PageState == false)//如果该页面当前不在内存中
				{
					return -1;//发出缺页异常
				}
				else {
					this.flag = i;//标记下标，在更新快表中会使用到
					return this.pagelistitem[i].PageFrameID;//返回页框号
				}
			}
		}
		this.UpdateAll();
		return -2;//返回缺页异常
	}
	
	public void UpdateOthers(int i)//将其他页表项的距上一次使用时间+1
	{
		for(int j=0;j<this.ItemNumber;j++)
		{
			if(this.pagelistitem[j].IsEmpty == false && i != j)
			{
				this.pagelistitem[j].PageUseState++;
			}
		}
	}
	
	public void UpdateAll()//将所有页表项的距上一次使用时间+1
	{
		for(int j=0;j<this.ItemNumber;j++)
		{
			if(this.pagelistitem[j].IsEmpty == false)
			{
				this.pagelistitem[j].PageUseState++; 
			}
		}
	}
	
	public int Choose()//lru算法，返回最久未使用的页表项的下标
	{
		int max = 0;
		int flag;
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false)
			{
				if(this.pagelistitem[i].PageUseState > max)
				{
					max = this.pagelistitem[i].PageUseState;
					flag = i;
				}
			}
		}
		return max;
	}                                                        
	
	public void show()
	{
		//System.out.println("当前运行进程的页表:");
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false)
			{
				System.out.print(this.pagelistitem[i].PageID);
				if(this.pagelistitem[i].PageState == true)
				{
					System.out.println("  " + this.pagelistitem[i].PageFrameID);
				}else {
					System.out.println("  不在内存中");
				}
			}
		}
	}
	
	public int size()
	{
		int n = 0;
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false)
			{
				n++;
			}
		}
		return n;
	}
}

class PageListItem {//页表项类
	
	protected int PageID;//页号
	protected int PageFrameID;//在内存中的页框号
	
	protected boolean PageState;//驻留标志位，true表示当前在内存中
	protected int PageUseState;//使用位，记录多久未被访问
	protected boolean PageChangeState;//修改位，为true表示已被修改，需要重新写回外存
	
	protected boolean IsEmpty;//判断该页表项是否为空
	
	public PageListItem()//构造函数
	{		
		
	}
}

class TLB {//快表类
	protected final static int Number = 5;//假设能存储五个快表项
	
	protected static boolean IsEmpty[];
	
	protected static int ProID[];//进程号
	protected static int PageID[];//页号
	protected static int PageFrameID[];//页框号
	protected static int Flag[];//标志位，记录该快表项多久未被命中
	
	protected static int ItemNumber;//当前有多少快表项
	
	protected int oldproid;
	protected int oldpageid;
	protected int oldpageframeid;
	
	public TLB()//构造函数
	{
		this.IsEmpty = new boolean[Number];
		for(int i=0;i<Number;i++)
		{
			this.IsEmpty[i] = true;
		}
		
		this.ProID = new int[Number]; 
		this.PageID = new int[Number];
		this.PageFrameID = new int[Number];
		this.Flag = new int[Number];
		
		this.ItemNumber = 0;
	}
	
	public int VisitTLB(int proid , int pageid)//访问快表
	{
		for(int i=0;i<Number;i++)
		{
			if(this.IsEmpty[i] == false && this.ProID[i] == proid && this.PageID[i] == pageid)//若快表命中
			{
				for(int j=0;j<Number;j++)
				{
					if(this.IsEmpty[j] == false && j != i)//将其他快表项的flag++
					{
						this.Flag[j]++;
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
			this.ProID[ItemNumber] = proid;
			this.PageID[ItemNumber] = pageid;
			this.PageFrameID[ItemNumber] = pageframeid;
			this.IsEmpty[ItemNumber] = false;
			this.Flag[ItemNumber] = 0;
			for(int j=0;j<ItemNumber;j++)//将其他快表项的flag++
			{
				this.Flag[j]++;
			}	
			ItemNumber++;
			return true;
		}
		else {//快表无空余位置，需要更新快表
			int old = Calculate();
			//System.out.print(old + "/");
			
			this.oldproid = this.ProID[old];
			this.oldpageid = this.PageID[old];
			this.oldpageframeid = this.PageFrameID[old];
			
			
			this.ProID[old] = proid;
			this.PageID[old] = pageid;
			this.PageFrameID[old] = pageframeid;
			this.IsEmpty[old] = false;
			this.Flag[old] = 0;
			
			for(int i=0;i<Number;i++)
			{
				if(i != old)
				{
					this.Flag[i]++;
				}
			}
			return false;
		}
	}
	
	public int Calculate()//返回最久未被访问的快表项的下标
	{
		int max = -1;
		int maxflag = 0;
		for(int i=0;i<Number;i++)
		{
			if(max <= this.Flag[i])
			{
				max = this.Flag[i];
				maxflag = i;
			}
		}
		return maxflag;
	}
	
	public int[][] gettlb()
	{
		int a[][] = new int[5][5];
		for(int i=0;i<5;i++)
		{
			a[i][0] = i;
			a[i][1] = this.ProID[i];
			a[i][2] = this.PageID[i];
			a[i][3] = this.PageFrameID[i];
			if (this.IsEmpty[i] == true)
			{
				a[i][4] = 0;
			}else {
				a[i][4] = 1;
			}
		}
		return a;
	}
	
	public void show()
	{
		for(int i=0;i<5;i++)
		{
			if(this.IsEmpty[i])
			{
				System.out.println("快表项" + i + "，当前为空");
			}else {
				System.out.println("快表项" + i + "，存储的进程号" + this.ProID[i] + ",存储的页号" + this.PageID[i] + ",存储的页框号" + this.PageFrameID[i]);
			}
		}
	}
}
