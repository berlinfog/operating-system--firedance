package state1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class CPU {

	private int PC;//���������
	private int IR;//ָ��Ĵ���
	private int PSW;//����״̬�ּĴ���
	private int State;//CPU״̬��0Ϊ����̬��1Ϊ�û�̬
	protected boolean DispatchInfor;//�����źţ���ʱ���жϷ���,true��ʾ����ʱ���жϣ���Ҫ���е��ȣ�false��ʾ����Ҫ����
	
	private int RunTime;//ϵͳ�Ѿ����е�ʱ��
	private int EndTime;//ϵͳ��������ʱ��

	Create_Job jobcreator;//���ڴ�����ҵ
	private int Job_Num;//��¼�Ѿ��������ҵ��
	private int Process_Num;//��¼��ǰ�Ѿ������Ľ�����Ŀ
	
	private PCBTable pcbtable;
	private Clock clock;//ʱ��
	private Disk disk;//���
	
	protected boolean IsEnd;//���н����Ƿ��Ѿ��������
	
	protected boolean Is_PV_Occupy;//�ж����޽������ٽ���
	protected int PV_ProID;//����ռ���ٽ����Ľ���ID
	
	public CPU()//���캯��
	{
		this.Init_Disk();//��ʼ������
		//this.Init_Register();//��ʼ���Ĵ���
		this.Init_PCBTable();//��ʼ������
		//this.Init_Flag();//��ʼ���Լ������һЩ��־λ

	}
	
	public void Start()//ϵͳ��ʼ����
	{
		clock = new Clock(this);
		Job_Dispatch jobdispatch = new Job_Dispatch();
		clock.start();
		jobdispatch.start();
	}
	
	public void Init_Disk()//��ʼ������
	{
		disk = new Disk(32,64,512);
		try {
			disk.Disk_Initial();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�����ѳ�ʼ���ɹ�!");
	}
	
	public void Init_Register()//��ʼ���Ĵ���
	{
		PC = 0;
		IR = 0;
		PSW = 0;
	}
	
	public void Init_Flag()//��ʼ���Լ������һЩ��־λ
	{
		this.IsEnd = false;//
		this.Process_Num = 0;//��ʼ������ĿΪ0
		this.Job_Num = 0;//��ʼ������ҵ��Ϊ0
	}
	
	public void Init_PCBTable()//��ʼ������
	{
		pcbtable = new PCBTable();
	}
	
	public void Form_Job() throws Exception//����������ҵ
	{
		this.jobcreator = new Create_Job();
		System.out.println("������ҵ�Ѿ�������ϣ�һ����" + this.jobcreator.Job_Number + "����ҵ��");
	}
	
	public void Show_Job()//��ʾ���ɵ���ҵ
	{
		this.jobcreator.jobqueue.Show_JCB();
	}

	public void Protect(PCB pcb)//���̵��ֳ�����
	{
		if(!pcb.Protect_Flag)//
		{
			pcb.stack.push(this.PC);
			pcb.stack.push(this.IR);
			pcb.stack.push(this.PSW);
			pcb.Protect_Flag = true;
		}else {
			for(int j=0;j<3;j++)
			{
				pcb.stack.pop();
			}
			pcb.stack.push(this.PC);
			pcb.stack.push(this.IR);
			pcb.stack.push(this.PSW);
		}
	}
	
	public void Recover(PCB pcb)//���̵��ֳ��ָ�
	{
		this.PSW = pcb.stack.peek();
		pcb.stack.pop();
		this.IR = pcb.stack.peek();
		pcb.stack.pop();
		this.PC = pcb.stack.peek();
		pcb.stack.pop();
	}

	public void InitSet()
	{
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
	}
	
	class Job_Dispatch extends Thread{//��ҵ�����߳�
	
		public Job_Dispatch()//���캯��
		{
		
		}
		public void run()//��дrun����
		{
			while(true)
			{
				/*if(jobcreator.jobqueue.size() == 0)//�����޽��̵���
				{
					show();
					test();
					break;//������ҵ�����߳�
				}*/
				
				synchronized (clock) {
	                if(!DispatchInfor) {//�߳�ͬ����ȷ��ÿ��ʱ���ж϶������˵���
	                    try {
							clock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                System.out.print(clock.GetTime() + ":");
	                
	                //��ҵ����
	                if(clock.GetTime() >= jobcreator.jobqueue.front.InTime && jobcreator.jobqueue.size() != 0)//����ʱ������ҵ����
	                {
	                	Job_Num++;
	                	JCB jcb = jobcreator.jobqueue.front;
	                	System.out.println(clock.GetTime() + ":��" + Job_Num + "����ҵ����" + "  " + jcb.PRO_Number + "������     " + jcb.pro[0][2] + " " + jcb.pro[0][3] + " " + jcb.pro[0][4]);	                	
	                	for(int i=0;i<jcb.PRO_Number;i++)
	                	{	
	                		
	                		try {
	                			PCB pcb = this.createpcb(jcb,i);
								pcbtable.ReadyQueue.join(pcb);//��������ҵ��ÿһ�����̲������̼����������
								Process_Num++;
							} catch (Exception e) {
								e.printStackTrace();
							}	 
							
	                	}
	                	jobcreator.jobqueue.quit();//���Ѿ����ȳɽ��̵���ҵ����
	                }
	                //�м�����
	                
	                while((pcbtable.WaitQueue.size() > 0) && (clock.GetTime() - pcbtable.WaitQueue.front().timeflag) >= pcbtable.WaitQueue.front().instruction[pcbtable.WaitQueue.front().PSW].Instr_TotalTime)
                	{
                		pcbtable.WaitQueue.front().PSW++;                    
                		pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
                		pcbtable.WaitQueue.quit();
                	}
	                //�ͼ�����
	                if(pcbtable.RunQueue == null && pcbtable.ReadyQueue.size() == 0 && pcbtable.WaitQueue.size() == 0 && jobcreator.jobqueue.size() ==0)
	                {
	                	IsEnd = true;//�����н����Ѿ�ִ���꣬���ı�־λ���˳�ѭ��
	                	System.out.println("all job done!");
	                	break;
	                }
	                if(pcbtable.RunQueue != null)//�����ǰ�н���������
	                {
	                	switch(pcbtable.RunQueue.instruction[PC].Instr_State) {
	                		case 0://ϵͳ����ָ��
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                			{
	                				PC++;//PC������ָ��ý�����һ��ָ��;
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
	                				{
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���	                					
	                					pcbtable.RunQueue = null;//���ÿ�ֵ
	                				}else {
	                					this.Run_to_Ready();
	                				}
	                			}
	                			break;
	                		case 1://�û�̬����ָ��
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                			{
	                				PC++;//PC������ָ��ý�����һ��ָ��;
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
	                				{
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���
	                					pcbtable.RunQueue = null;//���ÿ�ֵ
	                				}
	                				else if(clock.GetTime() - pcbtable.RunQueue.timeflag > 1000)//ʱ��Ƭ��
	                				{
	                					this.Run_to_Ready();
	                				}
	                			}
	                			break;
	                		case 2://PV����ָ��
	                			if(Is_PV_Occupy == false || Is_PV_Occupy == true && PV_ProID == pcbtable.RunQueue.Pro_ID)//���Է����ٽ���
	                			{
	                				Is_PV_Occupy = true;
	                				PV_ProID = pcbtable.RunQueue.Pro_ID;
	                				pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                				if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                				{
	                					PC++;//PC������ָ��ý�����һ��ָ��;
	                					Is_PV_Occupy = false;//�������̿��Է����ٽ���Դ
	                					if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
		                				{
		                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���
		                					pcbtable.RunQueue = null;//���ÿ�ֵ
		                				}	
	                				}	               
	                			}else {
	                				this.Run_to_Wait();
	                			}
	      	                    break;
	                		case 3:
	                			pcbtable.RunQueue.PSW = PC;
	                			pcbtable.RunQueue.timeflag = clock.GetTime();	              	                				                	
	                			pcbtable.WaitQueue.join(pcbtable.RunQueue);	                			
	                			pcbtable.RunQueue = null;	                			
	                			if(pcbtable.ReadyQueue.size() > 0)
	                			{
	                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();
	                				pcbtable.ReadyQueue.quit();
	                				PC = pcbtable.RunQueue.PSW;
	                				pcbtable.RunQueue.timeflag = clock.GetTime();
	                			}	                				     
	                			break;
	    
	                		default:
	                			break;
	    		          }
	                	
	                }else {//�����ǰû�н���������
	                	if(pcbtable.ReadyQueue.size() != 0)
	                	{
	                		pcbtable.RunQueue = pcbtable.ReadyQueue.front();
	                		pcbtable.ReadyQueue.quit();
	                		PC = pcbtable.RunQueue.PSW;
	                		pcbtable.RunQueue.timeflag = clock.GetTime();
	                	}
	                }
	           
	                
	                if(pcbtable.RunQueue != null)
	                {
	                	System.out.print("��ǰ���н���:" + pcbtable.RunQueue.Pro_ID);
	                }else {
	                	System.out.print("null\t");
	                }
	                
	                System.out.print("��ǰ��������:");
	                if(pcbtable.ReadyQueue.size() > 0)
	                {
	                	pcbtable.ReadyQueue.Show_PCB();
	                }else {
	                	System.out.print("null\t");
	                }
	                
	                System.out.print("��ǰ�ȴ�����:");
	                if(pcbtable.WaitQueue.size() > 0)
	                {
	                	pcbtable.WaitQueue.Show_PCB();
	                }else {
	                	System.out.print("null\t");
	                }
	                System.out.println("");
	  
	                DispatchInfor = false;
	                clock.notifyAll();
				}
			}
		}
		
		public PCB createpcb(JCB jcb,int i)throws Exception//����jcb����ҵ�ĵ�i�����̣�����pcb
		{
			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,clock.GetTime(),jcb.pro[i][2],jcb.pro[i][4]);//��������
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
				System.out.println(strs1[3] + "--------" +strs1[4]);
				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
				if(j <= 256 && j > 0)
				{
					flagx = 0;
					for(int l=0;l<j;l++)
					{
						try{
							String strs2[] = bf2.readLine().split("\t");//��nullpointexception
							pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));	
						}catch(Exception e) {}
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
			return pcb;//���ش����õĽ���
		}
		
		public void Run_to_Ready()//�����л�������̬���̼���������У��������ж��׽�������̬
		{
			pcbtable.RunQueue.PSW = PC;
			pcbtable.ReadyQueue.join(pcbtable.RunQueue);
			pcbtable.RunQueue = pcbtable.ReadyQueue.front();
			pcbtable.ReadyQueue.quit();
			PC = pcbtable.RunQueue.PSW;
			pcbtable.RunQueue.timeflag = clock.GetTime();
		}
		
		public void Run_to_Wait()////�����л�������̬���̼����������У��������ж��׽�������̬
		{
			pcbtable.RunQueue.PSW = PC;
			pcbtable.WaitQueue.join(pcbtable.RunQueue);
			if(pcbtable.ReadyQueue.size() > 0)
			{
				pcbtable.RunQueue = pcbtable.ReadyQueue.front();
				pcbtable.ReadyQueue.quit();
				PC = pcbtable.RunQueue.PSW;
				pcbtable.RunQueue.timeflag = clock.GetTime();
			}
			
		}
		
		public void Wake()//���Ѵ����������е�
		{
			if(pcbtable.WaitQueue.size() > 0)
			{
				pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
				pcbtable.WaitQueue.quit();
			}
		}
		
	
	}
	
	public void show()
	{
		//System.out.println("��ǰ���н���:" + pcbtable.RunQueue.Pro_ID);
		System.out.print("��������:");
		pcbtable.ReadyQueue.Show_PCB();
		System.out.print("��������:");
		pcbtable.WaitQueue.Show_PCB();
	}
	
	public void test()
	{
		pcbtable.ReadyQueue.test();
	}
	
}


