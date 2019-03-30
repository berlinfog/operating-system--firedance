package state1;

public class TLB {//�����
	protected final static int Number = 5;//�����ܴ洢��������
	
	protected static boolean IsEmpty[];//�ÿ�����Ƿ�Ϊ��
	
	protected static int ProID[];//���̺�
	protected static int PageID[];//ҳ��
	protected static int PageFrameID[];//ҳ���
	protected static int Flag[];//��־λ����¼�ÿ������δ������
	
	protected static int ItemNumber;//��ǰ�ж��ٿ����
	
	protected int oldproid;//��¼Ҫ���滻��ȥ����Ľ��̺�
	protected int oldpageid;//��¼Ҫ���滻��ȥ�����ҳ��
	protected int oldpageframeid;//��¼Ҫ���滻��ȥ�����ҳ���
	
	public TLB()//���캯��
	{
		this.IsEmpty = new boolean[Number];//����ռ�
		for(int i=0;i<Number;i++)
		{
			this.IsEmpty[i] = true;//ÿһ����Ϊ��
		}
		
		this.ProID = new int[Number]; //����ռ�
		this.PageID = new int[Number];//����ռ�
		this.PageFrameID = new int[Number];//����ռ�
		this.Flag = new int[Number];//����ռ�
		this.ItemNumber = 0;//��ǰ�Ѿ��еĿ������Ŀ
	}
	
	public int VisitTLB(int proid , int pageid)//���ʿ��
	{
		for(int i=0;i<Number;i++)//����ÿһ�������
		{
			if(this.IsEmpty[i] == false && this.ProID[i] == proid && this.PageID[i] == pageid)//���������
			{
				for(int j=0;j<Number;j++)//����ÿһ�������
				{
					if(this.IsEmpty[j] == false && j != i)//�������������
					{
						this.Flag[j]++;//�����������ķ���λ+1
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
			this.ProID[ItemNumber] = proid;//��ֵ
			this.PageID[ItemNumber] = pageid;//��ֵ
			this.PageFrameID[ItemNumber] = pageframeid;//��ֵ
			this.IsEmpty[ItemNumber] = false;//�޸ı�־λ
			this.Flag[ItemNumber] = 0;//�޸ķ��ʱ�־λ
			for(int j=0;j<ItemNumber;j++)//������������flag++
			{
				this.Flag[j]++;//�������������ʱ�־λ+1
			}	
			ItemNumber++;//�������+1
			return true;//������п���λ�÷��� true
		}
		else {//����޿���λ�ã���Ҫ���¿��
			int old = Calculate();
			//System.out.print(old + "/");
			
			this.oldproid = this.ProID[old];//��¼Ҫ���滻��ȥ�Ŀ����Ľ��̺�
			this.oldpageid = this.PageID[old];//��¼Ҫ���滻��ȥ�Ŀ����Ľ��̵�ҳ��
			this.oldpageframeid = this.PageFrameID[old];//��¼Ҫ���滻��ȥ�Ŀ����Ľ��̵�ҳ���
		
			this.ProID[old] = proid;//������Ϣ
			this.PageID[old] = pageid;//������Ϣ
			this.PageFrameID[old] = pageframeid;//������Ϣ
			this.IsEmpty[old] = false;//�޸ı�־λ
			this.Flag[old] = 0;//���·��ʱ�־λ
			
			for(int i=0;i<Number;i++)//����
			{
				if(i != old)
				{
					this.Flag[i]++;//���������ķ��ʱ�־λ+1
				}
			}
			return false;//����false
		}
	}
	
	public int Calculate()//�������δ�����ʵĿ������±�
	{
		int max = -1;//��¼���ʱ�־λ�����ֵ
		int maxflag = 0;//��¼���ʱ�־λ���Ŀ������±�
		for(int i=0;i<Number;i++)//����ÿһ�������
		{
			if(max <= this.Flag[i])//�ҵ�����ķ��ʱ�־λֵ
			{
				max = this.Flag[i];//��ֵ
				maxflag = i;//��ֵ
			}
		}
		return maxflag;//���ط��ʱ�־λ���ֵ�Ŀ�����±�
	}
	
	public int[][] gettlb()//��ȡ��ǰʱ��tlb�Ĵ����Ϣ�����ڸ��¿�����
	{
		int a[][] = new int[5][5];//�������飬���tlb��Ϣ
		for(int i=0;i<5;i++)//ѭ��
		{
			a[i][0] = i;//������±�
			a[i][1] = this.ProID[i];///��ŵĽ��̺�
			a[i][2] = this.PageID[i];//��ŵĽ��̵�ҳ��
			a[i][3] = this.PageFrameID[i];//��ŵĽ��̵�ҳ���
			if (this.IsEmpty[i] == true)//�����Ƿ�Ϊ��
			{
				a[i][4] = 0;//Ϊ�ո�ֵ0
			}else {
				a[i][4] = 1;//��Ϊ�ո�ֵ1
			}
		}
		return a;//���ظö�ά����
	}
}
