
package state1;

public class Memory {
	public final static int MenorySize = 64*512;//�����ڴ��С32KB
	public final static int PageFrameNum = 64;//�ڴ���ҳ����Ŀ����64��
	public final static int PageFrameSize = 512;//ÿ��ҳ��Ĵ�С��Ϊ512B
	
	public static int UsedPageFrameNum = 0;//�Ѿ���ռ�õ�ҳ����
	public static int FreePageNum = 64;//���е�ҳ����
	
	protected static PageFrame pageframe[];//ҳ�����������
	
	
	public Memory()//���캯��
	{
		
	}
	
	public static void Init_Page()//��ʼ���ڴ�
	{
		pageframe = new PageFrame[PageFrameNum];//����64��ҳ��
		for(int i=0;i<PageFrameNum;i++)//��ʼ��ÿһ��ҳ��
		{
			pageframe[i] = new PageFrame();//�ڴ滮��Ϊ64��ҳ��
		}
	}
	
	public static void Visit(int pageframeid) //�����ڴ�
	{
		if(pageframe[pageframeid].Re < 8 )//�ϻ��㷨
		{
			pageframe[pageframeid].Re += 8;//����λ�Ĵ������λ��1
		}
	}
	
	public static void Update()//����ÿ��ҳ��Ķ�Ϊ�Ĵ���
	{
		for(int i=0;i<PageFrameNum;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true)//�����ҳ��ռ��
			{
				pageframe[i].Update();//���¸�ҳ��Ķ�λ�Ĵ���
			}
		}
	}
	
	public static int Find() //�ҵ����δ�����ʵ�ҳ��ţ��ϻ��㷨
	{
		int min = 16;//������Сֵ�ĳ�ֵ�����ڶ����Ƶ�1111
		int k = -1;//��¼���δ�����ʵ�ҳ��ŵ��±�
		for(int i=0;i<PageFrameNum;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true)//�����ҳ��ռ��
			{
				if(min > pageframe[i].Re)//�����ҳ��Ķ�λ�Ĵ����е���ֵ��С
				{
					min = pageframe[i].Re;//��¼��Сֵ
					k = i;//���±�־λ
				}
			}
		}
		return k;//����Ҫ���滻��ȥ��ҳ�����ڵ�ҳ���
	}
	
	public static void Allocate(int pageframeid , int proid , int pageid)//������Դ
	{
		pageframe[pageframeid].Occupy(proid, pageid);//����ҳ��
		if(pageframe[pageframeid].Re < 8 )//�ϻ��㷨
		{
			pageframe[pageframeid].Re += 8;//����λ�Ĵ������λ��1
		}
	}
	
	public static int Allocate(int proid , int pageid)//������Դ
	{
		for(int i=0;i<PageFrameNum;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == false)//�����ҳ��Ϊ��
			{
				pageframe[i].Occupy(proid , pageid);//������Դ
				return i;//���ط����ҳ���
			}
		}
		return -1;//���䲻�ɹ�����-1
	}
	public static void recover(int proid)//������Դ
	{
		for(int i=0;i<PageFrameNum;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true && pageframe[i].OccupyProID == proid)//�ҵ�Ҫ���յ�ҳ���
			{
				pageframe[i].IsOccupy = false;//�ø�ҳ���״̬Ϊ��
			}
		}
		
	}
	
	public static int[] show()//��ʾĳʱ���ڴ����
	{
		int[] a = new int[64];//��¼ÿ��ҳ���еĽ��̵Ľ��̺�
		for(int i=0;i<64;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true)//�����ҳ��ǿ�
			{
				a[i] = pageframe[i].OccupyProID;//��ֵ
			}else {//�����ҳ��Ϊ��
				a[i] = -1;///��-1
			}
		}
		return a;//���ظ�����
	}
	
	public static int[] show1()//��ʾĳʱ���ڴ����
	{
		int[] a = new int[64];//��¼ÿһ��ҳ���еĽ��̵�ҳ���
		for(int i=0;i<64;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true)//�����ҳ��ǿ�
			{
				a[i] = pageframe[i].OccpyPageID;//��ֵ
			}else {//�����ҳ��Ϊ��
				a[i] = -1;//��-1
			}
		}
		return a;//���ظ�����
	}
	
	public static boolean judge()//�жϸ�ʱ���Ƿ��ڴ����пյ�ҳ��
	{
		for(int i=0;i<64;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == false)//�������пյ�ҳ��
			{
				return true;//����true
			}
		}
		return false;//����false
	}
	
	public static int howmanypro()//ͳ�Ƹ�ʱ���ڴ����ж��ٽ���
	{
		int a[] = new int[30];//��¼���̱��
		int flag = 0;//��¼���̸���
		for(int i=0;i<64;i++)//����ÿһ��ҳ��
		{
			if(pageframe[i].IsOccupy == true)//����ҳ��ռ��
			{
				int k=0;//��־λ
				for(int j=0;j<flag;j++)//ѭ��
				{
					if(pageframe[i].OccupyProID == a[j])//���ý��̱�ͳ�ƹ�
					{
						k=1;//�޸ı�־λ
						break;//�˳�ѭ��
					}
				}
				if(k == 0)//����ý���û�б�ͳ�ƹ�
				{
					a[flag] = pageframe[i].OccupyProID;//ͳ��
					flag++;//������+1
				}
			}
		}
		return flag;//���ظ�ʱ���ڴ��еĽ�����
	}
	
}
