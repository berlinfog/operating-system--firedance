package state1;

public class PageList {//ҳ����

	protected final int ItemNumber = 10;//����ÿһ��ҳ���ҳ������Ϊ10��
	
	protected PageListItem pagelistitem[];//ҳ����Ķ�������
	
	protected int flag;//��־Ҫ�����������±�
	//protected boolean buy;
	
	public PageList()//���캯��
	{
		this.pagelistitem = new PageListItem[this.ItemNumber];//����ռ�
		for(int i=0;i<this.ItemNumber;i++)//ÿһ��ҳ����
		{
			this.pagelistitem[i] = new PageListItem();//��ʼ��ÿһ��ҳ����
			this.pagelistitem[i].IsEmpty = true;//����ÿ��ҳ����Ϊ��
		}
		//this.buy = true;
	}
	
	public void Replace(int pageid,int pageframeid)//����ҳ��
	{
		this.pagelistitem[flag].PageID = pageid;//��ֵ
		this.pagelistitem[flag].PageFrameID = pageframeid;//��ֵ
	}
	
	public void Delete()//ɾ���Ѽ��������
	{
		this.pagelistitem[flag].IsEmpty = true;//ɾ���Ѿ��������ҳ����
	}
	
	public boolean Insert(int pageid,int pageframeid)//�����µ�ҳ����
	{
		for(int i=0;i<this.ItemNumber;i++)//����ÿһ��ҳ����
		{
			if(this.pagelistitem[i].IsEmpty == true)//���ֿյ�λ��
			{
				//System.out.print("(" + i + ")");
				this.pagelistitem[i].IsEmpty = false;//�ø���Ϊ�ǿ�
				this.pagelistitem[i].PageID = pageid;//��ֵ
				this.pagelistitem[i].PageFrameID = pageframeid;//��ֵ
				this.pagelistitem[i].PageState = true;//�ý������ڴ���
				this.pagelistitem[i].PageChangeState = false;//���ó�ʼֵ
				return true;//����true
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
				this.pagelistitem[i].IsEmpty = false;//�ø���Ϊ�ǿ�
				this.pagelistitem[i].PageID = pageid;//��ֵ
				this.pagelistitem[i].PageState = false;//�ý��̲����ڴ���
				this.pagelistitem[i].PageChangeState = false;//���ó�ʼֵ
				return true;//����true
			}
		}
		return false;//ҳ��û�п���λ�ã�����false
	}
	
	public int Visit(int pageid)//����ҳ��
	{
		for(int i=0;i<this.ItemNumber;i++)//����ÿһ��ҳ����
		{
			if(this.pagelistitem[i].IsEmpty == false && pageid == this.pagelistitem[i].PageID)//��ҳ��������
			{
				this.UpdateOthers(i);//������ҳ����ľ���һ��ʹ��ʱ��+1
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
		for(int j=0;j<this.ItemNumber;j++)//����ÿһ��ҳ����
		{
			if(this.pagelistitem[j].IsEmpty == false && i != j)//�ҵ�����ҳ����
			{
				this.pagelistitem[j].PageUseState++;//ʹ��λ+1
			}
		}
	}
	
	public void UpdateAll()//������ҳ����ľ���һ��ʹ��ʱ��+1
	{
		for(int j=0;j<this.ItemNumber;j++)//��������ҳ����
		{
			if(this.pagelistitem[j].IsEmpty == false)//�ҵ�����ҳ����
			{
				this.pagelistitem[j].PageUseState++; //ʹ��λ+1
			}
		}
	}
	
	public int Choose()//lru�㷨���������δʹ�õ�ҳ������±�
	{
		int max = 0;//��¼���ʹ��λ��ֵ
		int flag;//��¼���ʹ��λ���±�
		for(int i=0;i<this.ItemNumber;i++)//����ÿһ��ҳ����
		{
			if(this.pagelistitem[i].IsEmpty == false)//�ҵ��ǿ�ҳ����
			{
				if(this.pagelistitem[i].PageUseState > max)//����ҳ����ʹ��λ����
				{
					max = this.pagelistitem[i].PageUseState;//��ֵ
					flag = i;//��Ǹ��±�
				}
			}
		}
		return max;
	}                                                        
	
	public void show()//��ʾ��ҳ��
	{
		//System.out.println("��ǰ���н��̵�ҳ��:");
		for(int i=0;i<this.ItemNumber;i++)//��������ҳ����
		{
			if(this.pagelistitem[i].IsEmpty == false)//�ҵ��ǿ�ҳ����
			{
				System.out.print(this.pagelistitem[i].PageID);//���
				if(this.pagelistitem[i].PageState == true)//�����ڴ���
				{
					System.out.println("  " + this.pagelistitem[i].PageFrameID);//��ʾ��Ӧ��ҳ���
				}else {
					System.out.println("  �����ڴ���");//��������ڴ��е���Ϣ
				}
			}
		}
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
