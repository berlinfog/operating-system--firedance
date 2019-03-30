package state1;

public class PageList {//页表类

	protected final int ItemNumber = 10;//设置每一个页表的页表项数为10个
	
	protected PageListItem pagelistitem[];//页表项的对象数组
	
	protected int flag;//标志要加入快表的项的下标
	//protected boolean buy;
	
	public PageList()//构造函数
	{
		this.pagelistitem = new PageListItem[this.ItemNumber];//分配空间
		for(int i=0;i<this.ItemNumber;i++)//每一个页表项
		{
			this.pagelistitem[i] = new PageListItem();//初始化每一个页表项
			this.pagelistitem[i].IsEmpty = true;//设置每个页表项为空
		}
		//this.buy = true;
	}
	
	public void Replace(int pageid,int pageframeid)//更新页表
	{
		this.pagelistitem[flag].PageID = pageid;//赋值
		this.pagelistitem[flag].PageFrameID = pageframeid;//赋值
	}
	
	public void Delete()//删除已加入快表的项
	{
		this.pagelistitem[flag].IsEmpty = true;//删除已经调入快表的页表项
	}
	
	public boolean Insert(int pageid,int pageframeid)//加入新的页表项
	{
		for(int i=0;i<this.ItemNumber;i++)//遍历每一个页表项
		{
			if(this.pagelistitem[i].IsEmpty == true)//发现空的位置
			{
				//System.out.print("(" + i + ")");
				this.pagelistitem[i].IsEmpty = false;//置该项为非空
				this.pagelistitem[i].PageID = pageid;//赋值
				this.pagelistitem[i].PageFrameID = pageframeid;//赋值
				this.pagelistitem[i].PageState = true;//该进程在内存中
				this.pagelistitem[i].PageChangeState = false;//设置初始值
				return true;//返回true
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
				this.pagelistitem[i].IsEmpty = false;//置该项为非空
				this.pagelistitem[i].PageID = pageid;//赋值
				this.pagelistitem[i].PageState = false;//该进程不在内存中
				this.pagelistitem[i].PageChangeState = false;//设置初始值
				return true;//返回true
			}
		}
		return false;//页表没有空余位置，返回false
	}
	
	public int Visit(int pageid)//访问页表
	{
		for(int i=0;i<this.ItemNumber;i++)//遍历每一个页表项
		{
			if(this.pagelistitem[i].IsEmpty == false && pageid == this.pagelistitem[i].PageID)//在页表中命中
			{
				this.UpdateOthers(i);//将其他页表项的距上一次使用时间+1
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
		for(int j=0;j<this.ItemNumber;j++)//遍历每一个页表项
		{
			if(this.pagelistitem[j].IsEmpty == false && i != j)//找到其他页表项
			{
				this.pagelistitem[j].PageUseState++;//使用位+1
			}
		}
	}
	
	public void UpdateAll()//将所有页表项的距上一次使用时间+1
	{
		for(int j=0;j<this.ItemNumber;j++)//遍历所有页表项
		{
			if(this.pagelistitem[j].IsEmpty == false)//找到所有页表项
			{
				this.pagelistitem[j].PageUseState++; //使用位+1
			}
		}
	}
	
	public int Choose()//lru算法，返回最久未使用的页表项的下标
	{
		int max = 0;//记录最大使用位的值
		int flag;//记录最大使用位的下标
		for(int i=0;i<this.ItemNumber;i++)//遍历每一个页表项
		{
			if(this.pagelistitem[i].IsEmpty == false)//找到非空页表项
			{
				if(this.pagelistitem[i].PageUseState > max)//若该页表项使用位更大
				{
					max = this.pagelistitem[i].PageUseState;//赋值
					flag = i;//标记该下标
				}
			}
		}
		return max;
	}                                                        
	
	public void show()//显示该页表
	{
		//System.out.println("当前运行进程的页表:");
		for(int i=0;i<this.ItemNumber;i++)//遍历所有页表项
		{
			if(this.pagelistitem[i].IsEmpty == false)//找到非空页表项
			{
				System.out.print(this.pagelistitem[i].PageID);//输出
				if(this.pagelistitem[i].PageState == true)//若在内存中
				{
					System.out.println("  " + this.pagelistitem[i].PageFrameID);//显示对应的页框号
				}else {
					System.out.println("  不在内存中");//输出不在内存中的信息
				}
			}
		}
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
