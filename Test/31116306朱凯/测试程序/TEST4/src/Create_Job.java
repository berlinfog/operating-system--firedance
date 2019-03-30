import java.io.FileNotFoundException;
import java.util.Scanner;

public class Create_Job {
		protected int Job_Number;
		protected int time = 0;
		protected JobQueue jobqueue;
		public Create_Job() throws FileNotFoundException//���캯��
		{
			this.jobqueue = new JobQueue();
			this.Job_Number = 5;
			for(int i=0;i<this.Job_Number;i++)
			{
				JCB jcb = new JCB(i+1,time);
				jcb.Create();
				jobqueue.join(jcb);
				time += (int)(100 * (2 + 4 * Math.random()));//ÿ��200-600�������һ����ҵ����
			}
		}
		
		public static void main (String [] args) throws Exception
		{	
			int Process_Num = 0;
			Create_Job cj = new Create_Job();
			Memory memory = new Memory();
			memory.Init_Page();
			MMU mmu = new MMU();
			
			while(cj.jobqueue.size() > 0)
			{
				JCB jcb = cj.jobqueue.front();
				for(int i=0;i<jcb.PRO_Number;i++)
            	{	
					mmu.AddPagelist();//Ϊ�½��̷���ҳ��
					for(int j=0;j<jcb.pro[i][4];j++)//Ϊÿ�����̵�ÿ��ҳ�����ڴ�
					{
						int k = Memory.Allocate(Process_Num , j);
						if(k == -1)//δ����ɹ�
						{
							System.out.println("��" + Process_Num + "�����̵�" + j + "��ǰ�ڴ�ҳ����������ҳ���Դ���������");
							mmu.Pagelist[Process_Num].Insert(j);//���¸ý��̵�ҳ��
						}else {//����ɹ�
							System.out.println("��" + Process_Num + "�����̵�" + j + "��ҳ�����ڴ��е�ҳ���Ϊ��" + k);
							mmu.Pagelist[Process_Num].Insert(j , k);//���¸ý��̵�ҳ��
						}				
					}
					Process_Num++;
            	}
				cj.jobqueue.quit();
			}
			System.out.println("������ҵ�Ѿ��������");
			for(int i=0;i<Process_Num;i++)
			{
				System.out.println("��" + i + "�����̵�ҳ��");
				mmu.Pagelist[i].show();
			}
			System.out.println("��ǰ�Ŀ��");
			mmu.tlb.show();
			
			while(true)
			{
				System.out.println("������Ҫ���ʵĽ��̺ź�ҳ��,����-1 -1�˳�ѭ��");
				Scanner sc = new Scanner(System.in);
				int pronum = sc.nextInt();
				int pagenum = sc.nextInt();
				if(pronum == -1 && pagenum ==-1) break;
				if(pronum >= Process_Num) System.out.println("û�иý���");
				else {
					int pageframeid = mmu.StartJob(pronum,pagenum);//MMU��ʼ����
					switch (pageframeid){
	    				case -1:
	    					System.out.println("����Խ���ж�");
	    					mmu.OutDealing();//Խ����Խ���жϽ��жϴ���
	    					break;
	    				case -2:
	    					System.out.println("����ȱҳ�ж�");
	    					mmu.LostDealing(pronum,pagenum);//ȱҳ�жϴ�����ȱҳ�����ڴ�
	    					break;
	    				default:
	    					System.out.println("�ڴ����и�ҳ��");
	    					break;
					}
					System.out.println("�ý��̵�ҳ��");
					mmu.Pagelist[pronum].show();
					System.out.println("��ǰ���");
					mmu.tlb.show();
				}
			}
		}
}

class JCB {
	public static int Job_Number;
	
	protected int JCB_ID;//��ҵ��
	protected int InTime;//��ҵ����ʱ��
	protected int PRO_Number;//��ҵ�����Ľ�����
	protected JCB next;//ָ����һ����ҵ�ڵ�
	protected int pro[][];//���������ɵĽ��̵���ϸ��Ϣ

	public JCB(int JID,int time)//���캯��
	{
		this.JCB_ID = JID;
		this.InTime = time;
	}
	
	
	public void Create()//������ҵ
	{
		this.PRO_Number = (int)(3 + 2 * Math.random());//ÿ����ҵ��3-4������		
		this.pro = new int[this.PRO_Number][5];
		for (int i=0;i<this.PRO_Number;i++)
		{
			pro[i][0] = i+1;//�������
			pro[i][1] = (int)(1 + 100 * Math.random());//�������ȼ�1-100
			pro[i][2] = (int)(500 + 301 * Math.random());//ÿ������ָ������500-800��
			pro[i][3] = (int)(3 + 3 * Math.random());//���ݶεĴ�С,ռ3-5��ҳ��
			if(pro[i][2] % 256 == 0) {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3];//������ռҳ����
			}else {
				pro[i][4] = (int)(pro[i][2] / 256) + pro[i][3] + 1;//������ռҳ����
			}	
		}
	}	
}

class JobQueue {
	protected JCB front;//��ͷ
	protected JCB rear;//��β	
	protected int Queue_Size;
	
	public JobQueue()
	{	
		this.Queue_Size = 0;
		this.front = null;
		this.rear = this.front;
	}		
	public boolean IsEmpty()//�ж��Ƿ�Ϊ��
	{		
		if(this.rear == this.front && this.front == null) return true;
		else {
			return false;
		}
	}	
		
	public void join(JCB e)//���
	{		
		if(this.front == null)
		{
			this.front = e;
			this.rear = e;
		}
		else {
			this.rear.next = e;
			this.rear = e;
		}
		this.Queue_Size++;
	}	
	
	public void quit()//����
	{
		if(this.front == this.rear) {this.front = null;}
		else {
			this.front = this.front.next;
		}
		this.Queue_Size--;
	}
	
	public JCB front()
	{
		return this.front;
	}
	
	public JCB rear()
	{
		return this.rear;
	}
	
	public int size()
	{
		return this.Queue_Size;
	}
	
	public void Show_JCB()//��ʾJCB����
	{
		JCB jcb = this.front;
		while(jcb != null)
		{
			System.out.println(jcb.JCB_ID);
			jcb = jcb.next;
		}
	}
}
