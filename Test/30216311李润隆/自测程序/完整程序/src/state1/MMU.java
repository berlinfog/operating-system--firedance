 package state1;

public class MMU {
	protected TLB tlb;//快表
	protected PageList[] Pagelist;//页表
	protected int pagelistnumber;//当前有多少张页表
	
	public MMU()//构造函数
	{
		tlb = new TLB();//初始化快表
		Pagelist = new PageList[30];//分配页表的空间
		for(int i=0;i<30;i++)//每一张页表
		{
			Pagelist[i] = new PageList();//初始化每一张页表，pagelist[i]表示第i个进程的页表
		}
		this.pagelistnumber = 0;//已有页表数置零
		System.out.println("MMU已经初始化成功!");
	}
	
	public void AddPagelist()//为新进程分配页表
	{
		this.Pagelist[pagelistnumber] = new PageList();//初始化该进程的页表
		pagelistnumber++;//已有页表数+1
	}
	
	public int StartJob(int proid , int pageid)//MMU开始工作
	{
		if(this.JudgeOutofBound(pageid))//如果产生越界
		{
			return -1;//产生越界返回-1
		}else {//未产生越界
			int pageframeid = tlb.VisitTLB(proid,pageid);//访问快表
			if(pageframeid != -1)//若快表命中
			{
				return pageframeid;//返回物理地址
			}
			else {//若快表没有命中，访问页表
				pageframeid = Pagelist[proid].Visit(pageid);//赋值
				if(pageframeid != -1)//如果访问页表命中
				{
					if(tlb.AlterTLB(proid,pageid,pageframeid))//更新快表且快表有空余位置
					{
						this.Pagelist[proid].Delete();//在页表中删除对应页表项
					}else {//快表无空余位置
						this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//将快表中替换下来的项写回页表
						this.Pagelist[proid].Delete();//在页表中删除对应页表项
					}
					return pageframeid;//返回物理地址
				}
				else {//产生缺页
					return -2;//产生缺页返回-2
				}
			}
				
		}
	}
	
	public boolean JudgeOutofBound(int pageid)//判断长度是否越界
	{
		return false;
	}
	
	public int LostDealing(int proid , int pageid)//缺页中断处理
	{
		if(!Memory.judge())//若当前内存中没有空闲页框
		{
			int pageframeid = Memory.Find();//要被替换出外存的页框号
			int oldproid = Memory.pageframe[pageframeid].OccupyProID;//要被替换出外存的页面所属的进程号
			int oldpageid = Memory.pageframe[pageframeid].OccpyPageID;//要被替换出外存的页面所属的进程的页面号
			
			for(int i=0;i<this.Pagelist[oldproid].ItemNumber;i++)//访问该进程页表的每一个页表项
			{
				if(this.Pagelist[oldproid].pagelistitem[i].PageID == oldpageid)//找到要被替换的页面
				{
					this.Pagelist[oldproid].pagelistitem[i].PageState = false;//在要被替换的页面所属的进程页表中将那一页的状态置为不在内存中
				}
			}
			
			Memory.Allocate(pageframeid , proid , pageid);//分配空间
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)//遍历该进程的页表
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)//找到那一页对应的页表项
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;//将该页表项移除页表
				}	
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//快表无空余位置
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//更新快表
				//this.Pagelist[proid].Delete();
			}
			return pageframeid;
		}else {//如果有空页框
			int pageframeid = Memory.Allocate(proid , pageid);//分配空间
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)//遍历该进程的页表
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)//找到该页
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;//将该页表项移除页表
				}
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//快表无空余位置
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//更新快表
				//this.Pagelist[proid].Delete();
			}
			return pageframeid;
		}
		
		
	}
	
	public void OutDealing()//越界中断处理
	{
		
	}
}
