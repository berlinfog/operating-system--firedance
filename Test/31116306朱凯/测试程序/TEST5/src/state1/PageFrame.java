package state1;

public class PageFrame {//ҳ����
	
	protected int PageFrame_ID;//ҳ���
	protected boolean IsOccupy;//�жϸ�ҳ��ǰ�Ƿ񱻽���ռ��
	protected int OccupyProID;//��ռ�õĽ��̺�
	protected int OccpyPageID;//��ռ�õĽ��̵���һҳ
	
	protected int Re;//��λ�Ĵ���������ʵ��LRU
	
	
	public PageFrame()//���캯��
	{
		this.IsOccupy = false;//�ÿ�
		Re = 0;//����
	}
	
	public void Occupy(int proid , int pageid)//ռ�ø�ҳ��
	{
		this.IsOccupy = true;//���ø�ҳ��ռ��
		this.OccupyProID = proid;//��ֵ
		this.OccpyPageID = pageid;//��ֵ
	}
	
	public void Update() //����ÿһ��ҳ��Ķ�λ�Ĵ���
	{
		this.Re = this.Re>>>1;//λ���㽫��λ�Ĵ�������ֵ�޷���λ����һλ
	}
	
	public void Page_Initial()//ҳ���ʼ��
	{
		
	}
}
