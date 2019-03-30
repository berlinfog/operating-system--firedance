
public class Memory {
	public final static int MenorySize = 64*512;//�����ڴ��С32KB
	public final static int PageFrameNum = 64;//�ڴ���ҳ����Ŀ����64��
	public final static int PageFrameSize = 512;//ÿ��ҳ��Ĵ�С��Ϊ512B
	
	public static int UsedPageFrameNum = 0;//�Ѿ���ռ�õ�ҳ����
	public static int FreePageNum = 64;//���е�ҳ����
	public static int high2 = 0;
	public static int high1 = 0;
	public static int high0 = 0;
	
	protected static PageFrame pageframe[];
	
	
	public Memory()//���캯��
	{
		
	}
	
	public static void Init_Page()//��ʼ���ڴ�
	{
		pageframe = new PageFrame[PageFrameNum];
		for(int i=0;i<PageFrameNum;i++)
		{
			pageframe[i] = new PageFrame();//�ڴ滮��Ϊ64��ҳ��
		}
	}
	
	public static void Visit(int pageframeid) //�����ڴ�
	{

	}
	
	public static void Update()//����ÿ��ҳ��Ķ�Ϊ�Ĵ���
	{
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == true)
			{
				pageframe[i].Update();
			}
		}
	}
	
	public static int Find() //�ҵ����δ�����ʵ�ҳ���
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
	
	public static void Allocate(int pageframeid , int proid , int pageid)//������Դ
	{
		pageframe[pageframeid].Occupy(proid, pageid);
		if(pageframe[pageframeid].Re < 8 )
		{
			pageframe[pageframeid].Re += 8;
		}
	}
	
	public static int Allocate(int proid , int pageid)//������Դ
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
	public static void recover(int proid)//������Դ
	{
		for(int i=0;i<PageFrameNum;i++)
		{
			if(pageframe[i].IsOccupy == true && pageframe[i].OccupyProID == proid)
			{
				pageframe[i].IsOccupy = false;
			}
		}
		
	}
	
	public static int[] show()//��ʾĳʱ���ڴ����
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
	
	public static int[] show1()//��ʾĳʱ���ڴ����
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

class PageFrame {//ҳ����
	
	protected int PageFrame_ID;//ҳ���
	protected boolean IsOccupy;//�жϸ�ҳ��ǰ�Ƿ񱻽���ռ��
	protected int OccupyProID;//��ռ�õĽ��̺�
	protected int OccpyPageID;//��ռ�õĽ��̵���һҳ
	
	protected int Re;//��λ�Ĵ���������ʵ��LRU
	
	
	public PageFrame()//���캯��
	{
		this.IsOccupy = false;
		Re = 0;//����
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
	
	public void Page_Initial()//ҳ���ʼ��
	{
		
	}
}
