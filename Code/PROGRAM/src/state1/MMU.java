 package state1;

public class MMU {
	protected TLB tlb;//���
	protected PageList[] Pagelist;//ҳ��
	protected int pagelistnumber;//��ǰ�ж�����ҳ��
	
	public MMU()//���캯��
	{
		tlb = new TLB();//��ʼ�����
		Pagelist = new PageList[30];//����ҳ��Ŀռ�
		for(int i=0;i<30;i++)//ÿһ��ҳ��
		{
			Pagelist[i] = new PageList();//��ʼ��ÿһ��ҳ��pagelist[i]��ʾ��i�����̵�ҳ��
		}
		this.pagelistnumber = 0;//����ҳ��������
		System.out.println("MMU�Ѿ���ʼ���ɹ�!");
	}
	
	public void AddPagelist()//Ϊ�½��̷���ҳ��
	{
		this.Pagelist[pagelistnumber] = new PageList();//��ʼ���ý��̵�ҳ��
		pagelistnumber++;//����ҳ����+1
	}
	
	public int StartJob(int proid , int pageid)//MMU��ʼ����
	{
		if(this.JudgeOutofBound(pageid))//�������Խ��
		{
			return -1;//����Խ�緵��-1
		}else {//δ����Խ��
			int pageframeid = tlb.VisitTLB(proid,pageid);//���ʿ��
			if(pageframeid != -1)//���������
			{
				return pageframeid;//���������ַ
			}
			else {//�����û�����У�����ҳ��
				pageframeid = Pagelist[proid].Visit(pageid);//��ֵ
				if(pageframeid != -1)//�������ҳ������
				{
					if(tlb.AlterTLB(proid,pageid,pageframeid))//���¿���ҿ���п���λ��
					{
						this.Pagelist[proid].Delete();//��ҳ����ɾ����Ӧҳ����
					}else {//����޿���λ��
						this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//��������滻��������д��ҳ��
						this.Pagelist[proid].Delete();//��ҳ����ɾ����Ӧҳ����
					}
					return pageframeid;//���������ַ
				}
				else {//����ȱҳ
					return -2;//����ȱҳ����-2
				}
			}
				
		}
	}
	
	public boolean JudgeOutofBound(int pageid)//�жϳ����Ƿ�Խ��
	{
		return false;
	}
	
	public int LostDealing(int proid , int pageid)//ȱҳ�жϴ���
	{
		if(!Memory.judge())//����ǰ�ڴ���û�п���ҳ��
		{
			int pageframeid = Memory.Find();//Ҫ���滻������ҳ���
			int oldproid = Memory.pageframe[pageframeid].OccupyProID;//Ҫ���滻������ҳ�������Ľ��̺�
			int oldpageid = Memory.pageframe[pageframeid].OccpyPageID;//Ҫ���滻������ҳ�������Ľ��̵�ҳ���
			
			for(int i=0;i<this.Pagelist[oldproid].ItemNumber;i++)//���ʸý���ҳ���ÿһ��ҳ����
			{
				if(this.Pagelist[oldproid].pagelistitem[i].PageID == oldpageid)//�ҵ�Ҫ���滻��ҳ��
				{
					this.Pagelist[oldproid].pagelistitem[i].PageState = false;//��Ҫ���滻��ҳ�������Ľ���ҳ���н���һҳ��״̬��Ϊ�����ڴ���
				}
			}
			
			Memory.Allocate(pageframeid , proid , pageid);//����ռ�
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)//�����ý��̵�ҳ��
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)//�ҵ���һҳ��Ӧ��ҳ����
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;//����ҳ�����Ƴ�ҳ��
				}	
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//����޿���λ��
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//���¿��
				//this.Pagelist[proid].Delete();
			}
			return pageframeid;
		}else {//����п�ҳ��
			int pageframeid = Memory.Allocate(proid , pageid);//����ռ�
			for(int i=0;i<this.Pagelist[proid].ItemNumber;i++)//�����ý��̵�ҳ��
			{
				if(this.Pagelist[proid].pagelistitem[i].PageID == pageid)//�ҵ���ҳ
				{
					this.Pagelist[proid].pagelistitem[i].IsEmpty = true;//����ҳ�����Ƴ�ҳ��
				}
			}
			if(!tlb.AlterTLB(proid,pageid,pageframeid))//����޿���λ��
			{
				this.Pagelist[tlb.oldproid].Insert(tlb.oldpageid,tlb.oldpageframeid);//���¿��
				//this.Pagelist[proid].Delete();
			}
			return pageframeid;
		}
		
		
	}
	
	public void OutDealing()//Խ���жϴ���
	{
		
	}
}
