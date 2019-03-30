import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Stack;

public class Main {
	public static void main (String [] args) throws Exception
	{
		Disk disk = new Disk(32,64,512);
		disk.Disk_Initial();
		Create_Job jobcreator = new Create_Job();//��������
		PCBTable pcbtable = new PCBTable();//����pcbtableʵ��
		int Job_Num = 0;
		int Process_Num = 0;
		
		 while(jobcreator.jobqueue.size() > 0)//����ʱ������ҵ����
         {
         	JCB jcb = jobcreator.jobqueue.front;
         	System.out.println("��" + Job_Num + "����ҵ����" + "  ����" + jcb.PRO_Number + "������ ");	                	
         	for(int i=0;i<jcb.PRO_Number;i++) 
         	{	
         		System.out.println("���̺�:" + Process_Num + "  ָ����:" + jcb.pro[i][2] + "  ��ҳ����:" + jcb.pro[i][4]);
         		try {
         			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,0,jcb.pro[i][2],jcb.pro[i][4]);//��������
        			
        			pcb.Pro_InstrNum = jcb.pro[i][2];
        			pcb.instruction = new Instruction[jcb.pro[i][2]];
        			
        			
        			File file1 = new File("Disk\\Cylinder\\Track_" + Integer.toString((int)(jcb.pro_position[i] / 64)) + 
        					"\\Sector_" + Integer.toString((int)(jcb.pro_position[i] % 64)) + ".txt");
        			BufferedReader bf1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
        			String str1;
        			String strs1[] = new String[5];
        			int j = pcb.Pro_InstrNum;
        			int flagx = 1;
        			int c = 0;
        			while((str1 = bf1.readLine()) != null)
        			{
        				if(flagx == 0) break;
        				strs1 = str1.split("\t");
        				File file2  = new File("Disk\\Cylinder\\Track_" + Integer.valueOf(strs1[3]) + 
        					"\\Sector_" + Integer.valueOf(strs1[4]) + ".txt");//������ҳ���ҵ���Ӧ��ҳ
        				//System.out.println(strs1[3] + "--------" +strs1[4]);
        				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
        				if(j <= 256 && j > 0)
        				{
        					flagx = 0;
        					for(int l=0;l<j;l++)
        					{
        						try {
        						String strs2[] = bf2.readLine().split("\t");//��nullpointexception
        						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));	
        						}catch (Exception e)
        						{}
        						
        					}
        					
        				}else if(j > 256)
        				{
        					j -= 256;
        					for(int l=0;l<256;l++)
        					{
        						String strs2[] = bf2.readLine().split("\t");
        						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));
        					}
        				}
        			}
        			
					pcbtable.ReadyQueue.join(pcb);//��������ҵ��ÿһ�����̲������̼����������
					Process_Num++;
				} catch (Exception e) {
					e.printStackTrace();
				}	 	
         	}
         	jobcreator.jobqueue.quit();//���Ѿ����ȳɽ��̵���ҵ����
         	Thread.sleep(1000);
         	Job_Num++;
         }
		 System.out.println("������ҵ���Ѿ�����");
		
	}
}

class Create_Job {
	protected int Job_Number;
	protected int time = 0;
	protected JobQueue jobqueue;
	

	public Create_Job() throws FileNotFoundException//���캯��
	{
		this.jobqueue = new JobQueue();
		this.Job_Number = 5 ;// (int)(2 + 3 * Math.random());//2-4����ҵ������ʹ�ù̶�5
		for(int i=0;i<this.Job_Number;i++)
		{
			JCB jcb = new JCB(i+1,time);
			jcb.Create();//���ɽ����Լ�����Ϣ����ռҳ����٣�һ����ҵ�ж��ٽ���
			
			jcb.WriteOutPageList();
			
			jobqueue.join(jcb);
			time += (int)(100 * (2 + 4 * Math.random()));//ÿ��200-600�������һ����ҵ����
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
	protected int pro_position[];//���ÿ������ÿ��ҳ������λ��
	
	protected PrintWriter pw;//��������������д��ҳ��
	protected PrintWriter px;//��������������дÿ�����̵���ϸ��Ϣ
	
	public static int flag = 0;
	
	public static int flag1 = 2;
	public static int flag2 = 0;
	
	private String str = "Disk\\Cylinder\\";//·��
	
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
		
		this.pro_position = new int[this.PRO_Number];
		for(int i=0;i<this.PRO_Number;i++)
		{
			this.pro_position[i] = flag++;//�ý��̵���ҳ��洢�ڵ�(int)(flag/64)�ŵ�����(flag%64)����
		}
	}
	
	public void WriteOutPageList() throws FileNotFoundException//��������д��ҳ��ÿ������һ����ҳ��
	{
		
		for(int i=0;i<this.PRO_Number;i++)
		{
			int k = 0;
			int track = (int)(this.pro_position[i] / 64) ;//�ŵ���
			int sector = this.pro_position[i] % 64;//������
			
			File file = new File(str + "Track_" + Integer.toString(track) + "\\Sector_" + Integer.toString(sector) + ".txt");
			pw = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)
									)));
			for(int j=0;j<this.pro[i][4];j++)
			{
				pw.print(Integer.toString(this.JCB_ID));//��ҵ��
				pw.print("\t");
				pw.print(Integer.toString(i+1));//����ҵ�ĵڼ�������
				pw.print("\t");
				pw.print(Integer.toString(j+1));//�ý��̵ĵڼ���ҳ��
				pw.print("\t");
				pw.print(Integer.toString(flag1));//��ҳ���ŵĴŵ���
				pw.print("\t");
				pw.print(Integer.toString(flag2));//��ҳ���ŵ�������
				pw.print("\r\n");
				
				if(j < this.pro[i][4] - this.pro[i][3] -1)
				{
					this.WritePage(-1,flag1,flag2,k);
				}
				else if(j == this.pro[i][4] - this.pro[i][3] -1)
				{
					this.WritePage(this.pro[i][2] % 256,flag1,flag2,k);
				}
				k++;
				
				flag2++;
				if(flag2 > 63)
				{
					flag1 ++;
					flag2 -= 64;
				}
			}
			pw.close();
			
		}
	}
	
	public void WritePage(int a,int x,int y,int k)//��������дÿ��ҳ�����ϸ��Ϣ
	{
		File file = new File(str + "Track_" + Integer.toString(x) + "\\Sector_" + Integer.toString(y) + ".txt");
		try {
			px = new PrintWriter(
					new BufferedWriter(
							new OutputStreamWriter(
									new FileOutputStream(file)
									)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(a == -1)
		{
			for(int i=0;i<256;i++)
			{
				px.print(Integer.toString(k * 256 + i));//ָ����
				px.print("\t");
				px.print(Integer.toString((int)(4 * Math.random())));//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����,3��ʾio����
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//ָ��ʱ��20-50ms
				px.print("\r\n");
			}
		}else {
			for(int i=0;i<a;i++)
			{
				px.print(Integer.toString(k * 256 + i));//ָ����
				px.print("\t");
				px.print(Integer.toString((int)(3 * Math.random())));//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����
				px.print("\t");
				px.print(Integer.toString((int)(2 * Math.random())));//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
				px.print("\t");
				px.print(Integer.toString(10 * (int)(2 + 4 * Math.random())));//ָ��ʱ��20-50ms
				px.print("\r\n");
			}
		}
		px.close();
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
class Disk {
	
	private int Track_Number;//�ŵ���
	private int Sector_Number;//������
	private long Sector_Length;//�����Ĵ�С
	
	protected boolean[][] peek;//��¼�������Ƿ�ռ��
	
	private String TrackName = new String("Track_");
	private String SectorName = new String("Sector_");
	
	
	public Disk(int t_number,int s_number,long s_length)//���캯��
	{
		this.Track_Number = t_number;
		this.Sector_Number = s_number;
		this.Sector_Length = s_length;
	}
	
	public void Disk_Initial() throws IOException
	{
		peek = new boolean[this.Track_Number][this.Sector_Number];
		for(int i=0;i<this.Track_Number;i++)
		{
			for(int j=0;j<this.Sector_Number;j++)
			{
				peek[i][j] = false;//��ʼ�����趨����������Ϊ��
			}
		}
		
		File diskfile = new File("Disk");//����Ĵ����ļ���
		if(!diskfile.exists())
		{
			diskfile.mkdirs();//���������ļ���
		}
		
		File cylinderfile = new File("Disk\\Cylinder");//����������ļ���
		if(!cylinderfile.exists())
		{
			cylinderfile.mkdirs();//���������ļ���
		}
		
		for(int i=0;i<this.Track_Number;i++)//����ŵ�
		{
			File trackfile = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i));//�ŵ���
			if(!trackfile.exists())
			{
				trackfile.mkdirs();//�����ŵ��ļ���
			}
			
		}
		
		for(int i=0;i<this.Track_Number;i++)//��������
		{
			for(int j=0;j<this.Sector_Number;j++)
			{
				File file = new File("Disk\\Cylinder\\" + this.TrackName + Integer.toString(i) + "\\" 
						+ this.SectorName + Integer.toString(j) + ".txt");//ÿ��������һ��512B���ı��ļ���ʾ
				RandomAccessFile r = new RandomAccessFile(file, "rw");  
				r.setLength(this.Sector_Length);  
				r.close();
			}	
		}
	}
}

class PCB {
	protected int Job_ID;//������������ҵID
	protected int Pro_ID;//����ID
	protected int Pro_Priority;//�������ȼ�
	
	protected int Pro_ArriveTime;//���̵���ʱ��
	protected int Pro_InTime;//���̴���ʱ��
	
	protected int Pro_State;//����״̬��1���У�2������3�ȴ�
	
	protected int Pro_RunTime;//��������ʱ��
	protected int Pro_EndTime;//���̽���ʱ��
	
	protected int timeflag = 0;//
	
	protected int Pro_TotalTime;//������תʱ��
	protected int PSW;//���̵�ǰִ�е�ָ����
	protected int Pro_InstrNum;//���̰�����ָ����Ŀ
	protected int PageNum;//������ռ��ҳ����
	protected Instruction[] instruction;//ָ������
	
	protected PCB next;//ָ����һ��PCB�ڵ�
	
	protected Stack<Integer> stack;//�ֳ������õ���ջ
	protected boolean Protect_Flag;
	
	protected final static int PCB_Length = 20;//PCB�������󳤶�
	protected static int PCB_Number;//PCB����ǰ�ڵ���
	
	public boolean IsFull()//�ж�PCB�����Ƿ�����
	{
		if(this.PCB_Length == this.PCB_Number) return true;
		else {
			return false;
		}
	}
	
	public PCB()
	{

	}
	
	public PCB(int jid , int pid , int priority , int arrivetime , int intime, int instrnum , int pagenum)//���캯��
	{
		this.Job_ID = jid;
		this.Pro_ID = pid;
		this.Pro_Priority = priority;
		this.Pro_ArriveTime = arrivetime;
		this.Pro_InTime = intime;
		this.PageNum = pagenum;
		
		this.Pro_State = 0;
		this.Pro_RunTime = 0;
		this.Pro_EndTime = 0;
		this.PSW = 0;
		
		this.Pro_InstrNum = instrnum;  
		//this.instruction = new Instruction[InstrNum];
	}
}

class PCBTable {
	protected PCB RunQueue;//ָ���������еĽ���
	protected MyQueue ReadyQueue;//��������
	protected MyQueue WaitQueue;//�ȴ�����
	protected MyQueue FinishQueue;//����ɶ���
	protected int[][] proinfo;
	
	public PCBTable()
	{
		this.RunQueue = new PCB();
		this.RunQueue = null;
		this.ReadyQueue = new MyQueue();
		this.WaitQueue = new MyQueue();
		this.FinishQueue = new MyQueue();
		this.proinfo = new int[34][5];
	}
}

class Instruction {
	protected int Instr_ID;//ָ�����
	protected int Instr_State;//ָ�����ͣ�0��ʾϵͳ���ã�1��ʾ�û�̬���������2��ʾPV����
	protected int Need_Data;//ָ���Ƿ���Ҫ�������ݶΣ�1��ʾ��Ҫ��0��ʾ����Ҫ
	protected int Instr_TotalTime;//ָ����ʱ��
	protected int Instr_RunTime;//ָ���Ѿ����е�ʱ��
	protected int Instr_Addr;//ָ��ĵ�ַ

	
	public Instruction(int ID , int State , int NeedData , int TotalTime)//���캯��
	{ 
		this.Instr_ID = ID;
		this.Instr_State = State;
		this.Need_Data = NeedData;
		this.Instr_TotalTime = TotalTime;
		this.Instr_RunTime = 0; 
	}
}


class MyQueue
{		
	private PCB front;//��ͷ
	private PCB rear;//��β	
	private int Queue_Size;
	
	public MyQueue()
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
		
	public void join(PCB e)//���
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
		if(this.front == this.rear) {this.front = null;} //System.out.println("����Ϊ�ն���");
		else {
			this.front = this.front.next;
		}
		this.Queue_Size--;
	}
	
	public PCB front()
	{
		return this.front;
	}
	
	public PCB rear()
	{
		return this.rear;
	}
	
	public int size()
	{
		return this.Queue_Size;
	}
	
	public void Show_PCB()//��ʾPCB����
	{
		PCB pcb = this.front;
		for(int i=0;i<this.size();i++)
		{
			System.out.print(pcb.Pro_ID + " ");
			pcb = pcb.next;
		}
	}
	
	public void test()
	{
		PCB pcb = this.front;
		while(pcb != null)
		{
			for(int i=0;i<pcb.Pro_InstrNum;i++)
			{
				if(pcb.instruction[i].Instr_TotalTime < 10)
				{
					System.out.println(pcb.Pro_ID + "bad");
				}
			}
			pcb = pcb.next;
		}
	}	
}


