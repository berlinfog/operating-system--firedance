
public class MMU {
	protected TLB tlb;//���
	protected PageList[] Pagelist;//ҳ��
	protected int pagelistnumber;//��ǰ�ж�����ҳ��
	
	public MMU()//���캯��
	{
		tlb = new TLB();
		Pagelist = new PageList[30];
		for(int i=0;i<30;i++)
		{
			Pagelist[i] = new PageList();//��ʼ��ÿһ��ҳ��pagelist[i]��ʾ��i�����̵�ҳ��
		}
		this.pagelistnumber = 0;
		System.out.println("MMU�Ѿ���ʼ���ɹ�!");
	}
	
	public void AddPagelist()//Ϊ�½��̷���ҳ��
	{
		this.Pagelist[pagelistnumber] = new PageList();
		pagelistnumber++;
	}
	
	public int StartJob(int proid , int pageid)//MMU��ʼ����
	{
		if(this.JudgeOutofBound(proid,pageid))//�������Խ��
		{
			return -1;//����Խ�緵��-1
		}else {//δ����Խ��
			int pageframeid = tlb.VisitTLB(proid,pageid);//���ʿ��
			if(pageframeid != -1)//���������
			{
				return pageframeid;//���������ַ
			}
			else {//�����û�����У�����ҳ��
				pageframeid = Pagelist[proid].Visit(pageid);
				if(pageframeid != -1)//�������ҳ������
				{
					if(tlb.AlterTLB(proid,pageid,pageframeid))//���¿���ҿ���п���λ��
					{
						this.Pagelist[proid].Delete();//��ҳ����ɾ����Ӧҳ����
					}else {//����޿���λ��
						this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
						this.Pagelist[proid].Delete();
					}
					return pageframeid;//���������ַ
				}
				else {//����ȱҳ
					return -2;//����ȱҳ����-2
				}
			}
				
		}
	}
	
	public boolean JudgeOutofBound(int proid,int pageid)//�жϳ����Ƿ�Խ��
	{
		if (this.Pagelist[proid].size() < pageid) return true;
		else return false;
	}
	
	public void LostDealing(int proid , int pageid)//ȱҳ�жϴ���
	{
		if(!Memory.judge())//����ǰ�ڴ���û�п���ҳ��
		{
			int pageframeid = Memory.Find();//Ҫ���滻������ҳ���
			int oldproid = Memory.pageframe[pageframeid].OccupyProID;//Ҫ���滻������ҳ�������Ľ��̺�
			int oldpageid = Memory.pageframe[pageframeid].OccpyPageID;//Ҫ���滻������ҳ�������Ľ��̵�ҳ���
			
			for(int i=0;i<this.Pagelist[oldproid].ItemNumber;i++)
			{
				if(this.Pagelist[oldproid].pagelistitem[i].PageID == oldpageid)
				{
					this.Pagelist[oldproid].pagelistitem[i].PageState = false;//��Ҫ���滻��ҳ�������Ľ���ҳ���н���һҳ��״̬��Ϊ�����ڴ���
				}
			}
			
			Memory.Allocate(pageframeid , proid , pageid);//����ռ�
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;
				}	
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//����޿���λ��
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
				//this.Pagelist[proid].Delete();
			}
		}else {//����п�ҳ��
			int pageframeid = Memory.Allocate(proid , pageid);//����ռ�
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;
				}
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//����޿���λ��
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);
				//this.Pagelist[proid].Delete();
			}
		}
		
	}
	
	public void OutDealing()//Խ���жϴ���
	{
		
	}
}

class PageList {//ҳ����

	protected final int ItemNumber = 10;
	
	protected PageListItem pagelistitem[];
	
	protected int flag;
	
	public PageList()//���캯��
	{
		this.pagelistitem = new PageListItem[this.ItemNumber];
		for(int i=0;i<this.ItemNumber;i++)
		{
			this.pagelistitem[i] = new PageListItem();//��ʼ��ÿһ��ҳ����
			this.pagelistitem[i].IsEmpty = true;//����ÿ��ҳ����Ϊ��
		}
	}
	
	public void Replace(int pageid,int pageframeid)//����ҳ��
	{
		this.pagelistitem[flag].PageID = pageid;
		this.pagelistitem[flag].PageFrameID = pageframeid;
	}
	
	public void Delete()//ɾ���Ѽ��������
	{
		this.pagelistitem[flag].IsEmpty = true;//ɾ���Ѿ��������ҳ����
	}
	
	public boolean Insert(int pageid,int pageframeid)//�����µ�ҳ����
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == true)//���ֿյ�λ��
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
		return false;//ҳ��û�п���λ�ã�����false
	}
	
	public boolean Insert(int pageid)//�����µ�ҳ����
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == true)//���ֿյ�λ��
			{
				this.pagelistitem[i].IsEmpty = false;
				this.pagelistitem[i].PageID = pageid;
				this.pagelistitem[i].PageState = false;
				this.pagelistitem[i].PageChangeState = false;
				return true;
			}
		}
		return false;//ҳ��û�п���λ�ã�����false
	}
	
	public int Visit(int pageid)//����ҳ��
	{
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false && pageid == this.pagelistitem[i].PageID)//��ҳ��������
			{
				this.UpdateOthers(i);
				if(this.pagelistitem[i].PageState == false)//�����ҳ�浱ǰ�����ڴ���
				{
					return -1;//����ȱҳ�쳣
				}
				else {
					this.flag = i;//����±꣬�ڸ��¿���л�ʹ�õ�
					return this.pagelistitem[i].PageFrameID;//����ҳ���
				}
			}
		}
		this.UpdateAll();
		return -2;//����ȱҳ�쳣
	}
	
	public void UpdateOthers(int i)//������ҳ����ľ���һ��ʹ��ʱ��+1
	{
		for(int j=0;j<this.ItemNumber;j++)
		{
			if(this.pagelistitem[j].IsEmpty == false && i != j)
			{
				this.pagelistitem[j].PageUseState++;
			}
		}
	}
	
	public void UpdateAll()//������ҳ����ľ���һ��ʹ��ʱ��+1
	{
		for(int j=0;j<this.ItemNumber;j++)
		{
			if(this.pagelistitem[j].IsEmpty == false)
			{
				this.pagelistitem[j].PageUseState++; 
			}
		}
	}
	
	public int Choose()//lru�㷨���������δʹ�õ�ҳ������±�
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
		//System.out.println("��ǰ���н��̵�ҳ��:");
		for(int i=0;i<this.ItemNumber;i++)
		{
			if(this.pagelistitem[i].IsEmpty == false)
			{
				System.out.print(this.pagelistitem[i].PageID);
				if(this.pagelistitem[i].PageState == true)
				{
					System.out.println("  " + this.pagelistitem[i].PageFrameID);
				}else {
					System.out.println("  �����ڴ���");
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

class PageListItem {//ҳ������
	
	protected int PageID;//ҳ��
	protected int PageFrameID;//���ڴ��е�ҳ���
	
	protected boolean PageState;//פ����־λ��true��ʾ��ǰ���ڴ���
	protected int PageUseState;//ʹ��λ����¼���δ������
	protected boolean PageChangeState;//�޸�λ��Ϊtrue��ʾ�ѱ��޸ģ���Ҫ����д�����
	
	protected boolean IsEmpty;//�жϸ�ҳ�����Ƿ�Ϊ��
	
	public PageListItem()//���캯��
	{		
		
	}
}

class TLB {//�����
	protected final static int Number = 5;//�����ܴ洢��������
	
	protected static boolean IsEmpty[];
	
	protected static int ProID[];//���̺�
	protected static int PageID[];//ҳ��
	protected static int PageFrameID[];//ҳ���
	protected static int Flag[];//��־λ����¼�ÿ������δ������
	
	protected static int ItemNumber;//��ǰ�ж��ٿ����
	
	protected int oldproid;
	protected int oldpageid;
	protected int oldpageframeid;
	
	public TLB()//���캯��
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
	
	public int VisitTLB(int proid , int pageid)//���ʿ��
	{
		for(int i=0;i<Number;i++)
		{
			if(this.IsEmpty[i] == false && this.ProID[i] == proid && this.PageID[i] == pageid)//���������
			{
				for(int j=0;j<Number;j++)
				{
					if(this.IsEmpty[j] == false && j != i)//������������flag++
					{
						this.Flag[j]++;
					}
				}
				return this.PageFrameID[i];//������з���ҳ���
			}
		}
		return -1;//���δ���з���-1
	}
		
	public boolean AlterTLB(int proid , int pageid , int pageframeid)//���¿��
	{
		if(this.ItemNumber < Number)//�������п���λ�ã�����Ҫ�滻,ֱ�����
		{
			this.ProID[ItemNumber] = proid;
			this.PageID[ItemNumber] = pageid;
			this.PageFrameID[ItemNumber] = pageframeid;
			this.IsEmpty[ItemNumber] = false;
			this.Flag[ItemNumber] = 0;
			for(int j=0;j<ItemNumber;j++)//������������flag++
			{
				this.Flag[j]++;
			}	
			ItemNumber++;
			return true;
		}
		else {//����޿���λ�ã���Ҫ���¿��
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
	
	public int Calculate()//�������δ�����ʵĿ������±�
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
				System.out.println("�����" + i + "����ǰΪ��");
			}else {
				System.out.println("�����" + i + "���洢�Ľ��̺�" + this.ProID[i] + ",�洢��ҳ��" + this.PageID[i] + ",�洢��ҳ���" + this.PageFrameID[i]);
			}
		}
	}
}
