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
	private boolean[] buy;
	
	private PCBTable pcbtable;//pcb���У������ȴ�����������ɶ���
	private Clock clock;//ʱ��
	private Memory memory;//�ڴ�
	private MMU mmu;//MMU
	private Disk disk;//���
	
	private Interface inter;//����
	//private Watch wat;
	
	public static boolean pauseflag = false;//��ͣ��־λ
	public static boolean startflag = true;//��ʼ���б�־λ
	public static boolean xxstartfalg = true;
	public static boolean initialflag = true;//ϵͳ��ʼ����־λ
	public static boolean jobflag = true;//��ҵ��ȡ��־λ
	public static int jobway;//��ҵ���ɷ�ʽ��־λ
	
	public JobQueue jq;//��ҵ����
	
	protected boolean IsEnd;//���н����Ƿ��Ѿ��������
	
	protected boolean Is_PV_Occupy;//�ж����޽������ٽ���
	protected int PV_ProID;//����ռ���ٽ����Ľ���ID
	
	public CPU()//���캯��
	{	
		inter = new Interface();//��ʼ������
		//wat = new Watch();
	}
	
	public void InitialSystem()//��ʼ��ϵͳ
	{
		while(this.initialflag)//�ȴ������ϡ���ʼ��ϵͳ����ť�¼�
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("ϵͳ��ʼ����.....");
		this.Init_Disk();//��ʼ������
		this.Init_Memory();//��ʼ���ڴ�
		this.Init_Register();//��ʼ���Ĵ���
		this.Init_PCBTable();//��ʼ������
		this.Init_Flag();//��ʼ���Լ������һЩ��־λ
		this.mmu = new MMU();//��ʼ��MMU
		this.JudgeJobCreateWay();//��ҵ���ɷ�ʽ
	}
	
	public void JudgeJobCreateWay()
	{
		while(this.jobflag)//�ȴ������ϰ�ť�¼�
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(this.jobway == 1)//ʹ���Ѿ����ɺõ���ҵ
		{
			try {
				this.getjob();//��ȡÿ����ҵ����ϸ��Ϣ����������ʱ�䣬��ҵ�ţ��������ȵ�
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {//��������µ���ҵ
			try {
				this.Form_Job();//��������µ���ҵ
				this.getjob();//��ȡÿ����ҵ����ϸ��Ϣ����������ʱ�䣬��ҵ�ţ��������ȵ�
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.Start();//ϵͳ��ʼ����
	}
	
	public void Start()//ϵͳ��ʼ����
	{
		while(this.startflag)//�ȴ������ϰ�ť�¼�
		{
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		clock = new Clock(this);//��ʼ��ʱ���߳�
		Dispatch dispatch = new Dispatch();//��ʼ�������߳�
		clock.start();//ʱ���߳̿�ʼ����
		dispatch.start();//�����߳̿�ʼ����
	}
	
	public void Init_Memory()//��ʼ���ڴ�
	{
		memory = new Memory();//��ʼ���ڴ�
		memory.Init_Page();//��ʼ��ÿһ��ҳ��
		System.out.println("�ڴ��ʼ���ɹ���");
	}
	
	public void Init_Disk()//��ʼ������
	{
		disk = new Disk(32,64,512);//��ʼ������
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
		System.out.println("�Ĵ�����ʼ���ɹ���");
	}
	
	public void Init_Flag()//��ʼ���Լ������һЩ��־λ
	{
		this.IsEnd = false;//��ʼ����־λ
		this.Process_Num = 0;//��ʼ������ĿΪ0
		this.Job_Num = 0;//��ʼ������ҵ��Ϊ0
		buy = new boolean[32];
	}
	
	public void Init_PCBTable()//��ʼ������
	{
		jq = new JobQueue();//��ʼ����ҵ����
		pcbtable = new PCBTable();//��ʼ��������
		System.out.println("PCB���г�ʼ���ɹ���");
	}
	
	public void Form_Job() throws Exception//���������ҵ
	{
		this.jobcreator = new Create_Job();//��ʼ����ҵ������
		System.out.println("������ҵ�Ѿ�������ϣ�һ����" + this.jobcreator.Job_Number + "����ҵ��");
	}
	
	public void Show_Job()//��ʾ���ɵ���ҵ
	{
		this.jobcreator.jobqueue.Show_JCB();//��ʾ���ɵ���ҵ
	}

	public void Protect(PCB pcb)//���̵��ֳ�����
	{
		if(!pcb.Protect_Flag)//���֮ǰû�н����ֳ�����
		{
			pcb.stack.push(this.PC);//��ջ
			pcb.stack.push(this.IR);//��ջ
			pcb.stack.push(this.PSW);//��ջ
			pcb.Protect_Flag = true;//�޸ı�־λ
		}else {//���֮ǰ�������ֳ�����
			for(int j=0;j<3;j++)
			{
				pcb.stack.pop();//��ջ
			}
			pcb.stack.push(this.PC);//��ջ
			pcb.stack.push(this.IR);//��ջ
			pcb.stack.push(this.PSW);//��ջ
		}
	}
	
	public void Recover(PCB pcb)//���̵��ֳ��ָ�
	{
		this.PSW = pcb.stack.peek();
		pcb.stack.pop();//��ջ
		this.IR = pcb.stack.peek();
		pcb.stack.pop();//��ջ
		this.PC = pcb.stack.peek();
		pcb.stack.pop();//��ջ
	}

	public void InitSet()//���³�ʼ����־λ
	{
		this.PC = 0;
		this.IR = 0;
		this.PSW = 0;
	}
	
	public void getjob() throws Exception//���ļ��ж�ȡ��ҵ��Ϣ
	{
		BufferedReader bff = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Job\\JobNumber.txt"))));//���ļ�
		int jobnum = Integer.valueOf(bff.readLine());//��ȡ���ε��ȵ���ҵ��
		for(int i=0;i<jobnum;i++)//��ȡÿһ����ҵ����ϸ��Ϣ
		{
			BufferedReader bfff = new BufferedReader(new InputStreamReader(new FileInputStream(new File("Job\\Job" + Integer.toString(i) + ".txt"))));//���ļ�
			int jid = Integer.valueOf(bfff.readLine());//��ȡ��ҵ��
			//System.out.println(jid);
			int intime = Integer.valueOf(bfff.readLine());//��ȡ��ҵ����ʱ��
			//System.out.println(intime);
			JCB jcb = new JCB(jid,intime);//����jcb�ڵ�
			int pron = Integer.valueOf(bfff.readLine());//��ȡ����ҵ�Ľ�����
			jcb.PRO_Number = pron;
			//System.out.println(pron);
			String[] str1;
			jcb.pro = new int[pron][6];//���ÿ�����̵���ϸ��Ϣ
			jcb.pro_position = new int[pron];//���ÿ��������ҳ���λ��
			for(int j=0;j<pron;j++)//��ȡÿ�����̵���ϸ��Ϣ
			{
				String s = bfff.readLine();
				//System.out.println(s);
				str1 = s.split("\t");
				jcb.pro[j][0] = Integer.valueOf(str1[0]);//���̺�
				jcb.pro[j][1] = Integer.valueOf(str1[1]);//���ȼ�
				jcb.pro[j][2] = Integer.valueOf(str1[2]);//ָ������
				jcb.pro[j][3] = Integer.valueOf(str1[3]);//���ݶ���ռ��ҳ����
				jcb.pro[j][4] = Integer.valueOf(str1[4]);//������ռ����ҳ������
				jcb.pro[j][5] = Integer.valueOf(str1[5]);//�����Ƿ���Ҫͬ���������̻���������ͬ��
			}
			for(int j=0;j<pron;j++)
			{
				jcb.pro_position[j] = Integer.valueOf(bfff.readLine());//�ý�����ҳ���λ��
			}
			this.jq.join(jcb);//������ҵ����
		}
	}
	
	
	class Dispatch extends Thread{//��ҵ�����߳�
	
		public Dispatch()//���캯��
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
				
				synchronized (clock) {//����ʹ��Clock��Ķ���clock
	                if(!DispatchInfor) {//��ʱ���ж�û�е���
	                    try {
							clock.wait();//��������
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                System.out.print(clock.GetTime() + ":");//��ʾ��ǰ��ʱ��
	                
	                if(clock.GetTime() % 1000 == 0)//ÿ��1�����ÿһ��ҳ��Ķ�λ�Ĵ���
	                {
	                	memory.Update();//���¶�λ�Ĵ���
	                }	
	                
	               /*if(xxstartfalg)
	                {
	                	pauseflag = true;
	                }*/
	                 
	                while(pauseflag)//���ܵ���ͣ�źţ���ѭ��
	                {
	                	 try {
							Thread.sleep(1);
						} catch (InterruptedException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}
	                }
	                
	                //��ҵ����
	                if( jq.size() > 0 && clock.GetTime() >= jq.front.InTime)//����ҵ���зǿ��Ҹ�ʱ������ҵ����
	                {
	                	Job_Num++;//ϵͳ�е���ҵ��Ŀ+1
	                	JCB jcb = jq.front;//��ȡ��ҵ���е�ͷ����
	                	System.out.println(clock.GetTime() + ":��" + Job_Num + "����ҵ����" + "  ����" + jcb.PRO_Number + "������ ");//��ʾ	                	
	                	for(int i=0;i<jcb.PRO_Number;i++)//Ϊÿһ�����̷�����Դ�ʹ���pcb�ڵ�
	                	{	
	                		System.out.println("���̺�:" + Process_Num + "  ָ����:" + jcb.pro[i][2] + "  ��ҳ����:" + jcb.pro[i][4]);
	                		try {
	                			PCB pcb = this.createpcb(jcb,i);//����jcb�ĵ�i������
								pcbtable.ReadyQueue.join(pcb);//��������ҵ��ÿһ�����̲������̼����������
								
								mmu.AddPagelist();//Ϊ�½��̷���ҳ��

								for(int j=0;j<jcb.pro[i][4];j++)//Ϊÿ�����̵�ÿ��ҳ�����ڴ�
								{
									int k = Memory.Allocate(Process_Num , j);
									//System.out.print(k + "|");
									if(k == -1)//δ����ɹ�
									{
										mmu.Pagelist[Process_Num].Insert(j);//���¸ý��̵�ҳ��
									}else {//����ɹ�
										mmu.Pagelist[Process_Num].Insert(j , k);//���¸ý��̵�ҳ��
										/*Memory.FreePageNum -= jcb.pro[i][4];
										Memory.UsedPageFrameNum += jcb.pro[i][4];	*/
									}				
								}
								System.out.println("");//����
								
								Process_Num++;//ϵͳ�н�����+1
							} catch (Exception e) {
								e.printStackTrace();
							}	 
							
	                	}
	                	jq.quit();//���Ѿ����ȳɽ��̵���ҵ����
	                }
	                //�м�����
	                
	                //�ͼ�����
	                if(pcbtable.RunQueue == null && pcbtable.ReadyQueue.size() == 0 && pcbtable.WaitQueue.size() == 0 && jq.size() ==0)//�����ж��ж�Ϊ��
	                {
	                	IsEnd = true;//�����н����Ѿ�ִ���꣬���ı�־λ���˳�ѭ��
	                	int a[] = Memory.show();//��ȡÿһ��ҳ����ռ�õĽ��̺�
	                	int b[] = Memory.show1();//��ȡÿһ��ҳ����ռ�õĽ��̵�ҳ��
	                	inter.altermemory(a,b);//�����ڴ���ʾ����
	                	//wat.update(0,0,0);
	                	System.out.println("all job done!");//���������ҵ��ɵ���Ϣ
	                	break;//�˳���ѭ��
	                }
	                
	                //�жϵȴ������Ƿ��н��̿��Ի���
	               /* while((pcbtable.WaitQueue.size() > 0) && (pcbtable.WaitQueue.front().WaitState == 0) && (clock.GetTime() - pcbtable.WaitQueue.front().timeflag) >= pcbtable.WaitQueue.front().instruction[pcbtable.WaitQueue.front().PSW].Instr_TotalTime)
	                {
	                	pcbtable.WaitQueue.front().PSW++;//ִ�����I/Oָ��
	                	pcbtable.WaitQueue.front().WaitState = -1;//�޸ı�־λ
	                	System.out.print("{" + pcbtable.WaitQueue.front().Pro_ID + "stop wait}");
	                	pcbtable.proinfo[pcbtable.WaitQueue.front().Pro_ID][2] = 1;
	                	pcbtable.proinfo[pcbtable.WaitQueue.front().Pro_ID][3]++;
	                	pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());//�����������
	                	pcbtable.WaitQueue.quit();//�˳��ȴ�����
	                }*/
	               
	               if(pcbtable.WaitQueue.size() > 0)//����ȴ����зǿգ�Ѱ�ҿ��Ի��ѵĽ���
	                {
	                	int size = pcbtable.WaitQueue.size();//��ȡ�ȴ�������ڵ�ĸ���
	                	PCB [] xy= new PCB[size];//��Ų��ܱ����ѵĽ���
	                	int num = 0;//��¼�ж��ٽ��̲��ܱ�����
	                	PCB p = pcbtable.WaitQueue.front();
	                	for(int i=0;i<size;i++)//���ÿһ���ڵ��Ƿ���Ա�����
	                	{
	                		if((p.WaitState == 0) && (clock.GetTime() -p.timeflag) >= p.instruction[p.PSW].Instr_TotalTime)//����ý�����I/O�����ж���I/O��������
	                		{
	                			p.PSW++;//���������I/Oָ��
	                			if(p.PSW > 200 && p.synchronousflag != -1)//����ý���ͬ�����������Ҹøý�����������200��ָ��
	                			{
	                				buy[p.Pro_ID] = true;//�޸ĸý��̵�ͬ����־λ
	                			}
	                			p.WaitState = -1;//�޸ĸý��̵ĵȴ���־λΪû�еȴ�
	                			System.out.print("{" + p.Pro_ID + "stop wait}");
	                			pcbtable.proinfo[p.Pro_ID][2] = 1;//���½���״̬Ϊ����̬
	    	                	pcbtable.proinfo[p.Pro_ID][3]++;//���½��������е�ָ����
	    	                	pcbtable.ReadyQueue.join(p);//�����������
	                		}else if((p.WaitState == 1) && buy[p.bechargedflag] == false)//����б�ͬ���Ľ�����ͬ���ý��̵Ľ����Ѿ�������200��ָ��
	                		{
	                			p.WaitState = -1;//�޸ĸý��̵ĵȴ���־λΪû�еȴ�
	                			p.bechargedflag = -1;//�޸ĸý��̱�ͬ����־λΪ������������ͬ��
	                			pcbtable.proinfo[p.Pro_ID][2] = 1;//���½���״̬Ϊ����̬
	                			pcbtable.ReadyQueue.join(p);//�����������
	                		}
	                		else {//�޷������ѣ�Ҳ�Ͳ���Ҫ����
	                			xy[num] = p;
	                			num++;//��¼����Ҫ���ӵĽ�����Ŀ
	                		}
	                		p = p.next;//�����һ���ڵ�
	                	}
	                	
	                	pcbtable.WaitQueue.front = null;//��ֵ
	                	pcbtable.WaitQueue.rear = null;//��ֵ
	                	pcbtable.WaitQueue.Queue_Size = 0;//���õȴ�����
	                	for(int j=0;j<num;j++)
	                	{
	                		pcbtable.WaitQueue.join(xy[j]);//���޷������ѵĽ��̼���ȴ�����
	                	}
	                	
	                }
	             
	                
	                if(pcbtable.RunQueue != null)//�����ǰ�н���������
	                {
	                	if(pcbtable.RunQueue.bechargedflag != -1 && buy[pcbtable.RunQueue.bechargedflag])//����ý�����Ҫ��ͬ������ͬ�������Ǹ����̻�ûִ����200��ָ��
	                	{
	                		pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 2;//��¼����״̬Ϊ�ȴ�̬
                			pcbtable.RunQueue.WaitState = 1;//���µȴ���־λ
                			
                			//System.out.print("{" + pcbtable.RunQueue.Pro_ID + "wait}");
                			pcbtable.WaitQueue.join(pcbtable.RunQueue);//����ȴ�����
                			pcbtable.RunQueue = null;//����ָ̬���Ϊ��ָ��
                			if(pcbtable.ReadyQueue.size() > 0)//����������зǿ�
                			{
                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//���������ж�ͷ��Ϊ����̬
                				pcbtable.ReadyQueue.quit();//����
                				PC = pcbtable.RunQueue.PSW;//PC��Ϊ�ý��̵�PSW��־λ
                				pcbtable.RunQueue.timeflag = clock.GetTime();//���ÿ�ʼ����ʱ��
                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//�޸Ľ���״̬��ϢΪ����̬
                			}
	                	}
	                	
	                	else{
	                	switch(pcbtable.RunQueue.instruction[PC].Instr_State) {//�ж��ǵ�ǰ����̬����Ҫ��������ָ��
	                		case 0://ϵͳ����ָ��
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                			{
	                				PC++;//PC������ָ��ý�����һ��ָ��;
	                				if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//����ý�����Ҫͬ�������������Ѿ�������200��ָ��
	                				{
	                					buy[pcbtable.RunQueue.Pro_ID] = false;//�޸�ͬ����־λ
	                				}
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//�Ѿ����е�ָ��+1
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
	                				{
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;//�޸Ľ���״̬Ϊ���̬
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���
	                					Memory.recover(pcbtable.RunQueue.Pro_ID);//��������Դ
	                					pcbtable.RunQueue = null;//���ÿ�ֵ
	                				}else {
	                					this.Run_to_Ready();//�����л�
	                				}
	                			}
	                			break;
	                		case 1://�û�̬����ָ��
	                			if(pcbtable.RunQueue.instruction[PC].Need_Data == 1)//����ý�����Ҫ��������
	                			{
	                				int o;
	                				if(pcbtable.RunQueue.Pro_InstrNum % 256 == 0)
	                				{
	                					o = pcbtable.RunQueue.Pro_InstrNum / 256;
	                				}else {
	                					o = (int)(pcbtable.RunQueue.Pro_InstrNum / 256) + 1;
	                				}
	                		 		for(int y=o;y<pcbtable.RunQueue.PageNum;y++)//��ָ����Ҫ������ݶε�ҳ��
	                		 		{
	                		 			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , y);//MMU��ʼ����
	                					System.out.print("[" + y + "," + pageframeid + "]");
		    	                		switch (pageframeid){
		    	                			case -1:
		    	                				mmu.OutDealing();//Խ���жϴ���
		    	                				break;
		    	                			case -2:
		    	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , y);//ȱҳ�жϴ�����ȱҳ�����ڴ�
		    	                				break;
		    	                			default://��������
		    	                				break;
		    	                		}
	                		 		}
	                			}
	                			pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                			if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                			{
	                				//System.out.println("***");
	                				PC++;//PC������ָ��ý�����һ��ָ��;
	                				if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//����ý�����Ҫͬ�������������Ѿ�������200��ָ��
	                				{
	                					buy[pcbtable.RunQueue.Pro_ID] = false;//�޸�ͬ����־λ
	                				}
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//�Ѿ����е�ָ��+1
	                				if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
	                				{
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;//�Ѿ����е�ָ��+1
	                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���
	                					Memory.recover(pcbtable.RunQueue.Pro_ID);//��������Դ
	                					pcbtable.RunQueue = null;//���ÿ�ֵ
	                				}
	                				else if(clock.GetTime() - pcbtable.RunQueue.timeflag > 1000)//ʱ��Ƭ��
	                				{
	                					this.Run_to_Ready();//�����л�
	                				}
	                			}
	                			break;
	                		case 2://PV����ָ��
	                			if(pcbtable.RunQueue.instruction[PC].Need_Data == 1)//����ý�����Ҫ��������
	                			{
	                				int o;
	                				if(pcbtable.RunQueue.Pro_InstrNum % 256 == 0)
	                				{
	                					o = pcbtable.RunQueue.Pro_InstrNum / 256;
	                				}else {
	                					o = (int)(pcbtable.RunQueue.Pro_InstrNum / 256) + 1;
	                				}
	                		 		for(int y=o;y<pcbtable.RunQueue.PageNum;y++)//��ָ����Ҫ������ݶε�ҳ��
	                		 		{
	                		 			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , y);//MMU��ʼ����
	                					System.out.print("[" + y + "," + pageframeid + "]");
		    	                		switch (pageframeid){
		    	                			case -1:
		    	                				mmu.OutDealing();//Խ���жϴ���
		    	                				break;
		    	                			case -2:
		    	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , y);//ȱҳ�жϴ�����ȱҳ�����ڴ�
		    	                				break;
		    	                			default://��������
		    	                				break;
		    	                		}
	                		 		}
	                			}
	                			if(Is_PV_Occupy == false || Is_PV_Occupy == true && PV_ProID == pcbtable.RunQueue.Pro_ID)//���Է����ٽ���
	                			{
	                				Is_PV_Occupy = true;//�����ٽ���Դ��ռ�ñ�־λ
	                				PV_ProID = pcbtable.RunQueue.Pro_ID;
	                				
	                				pcbtable.RunQueue.instruction[PC].Instr_RunTime += 10;//ָ������10ms
	                				if(pcbtable.RunQueue.instruction[PC].Instr_RunTime >= pcbtable.RunQueue.instruction[PC].Instr_TotalTime)//��ǰָ���������
	                				{
	                					PC++;//PC������ָ��ý�����һ��ָ��;
	                					if(pcbtable.RunQueue.synchronousflag != -1 && PC > 200)//����ý�����Ҫͬ�������������Ѿ�������200��ָ��
		                				{
		                					buy[pcbtable.RunQueue.Pro_ID] = false;//�޸�ͬ����־λ
		                				}
	                					pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][3]++;//�Ѿ����е�ָ��+1
	                					Is_PV_Occupy = false;//�������̿��Է����ٽ���Դ
	                					if(PC == pcbtable.RunQueue.Pro_InstrNum)//����ý�������ָ��ȫ���������
		                				{
	                						pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 3;
		                					pcbtable.FinishQueue.join(pcbtable.RunQueue);//���ý��̼�����ɶ���
		                					Memory.recover(pcbtable.RunQueue.Pro_ID);//��������Դ
		                					pcbtable.RunQueue = null;//���ÿ�ֵ
		                				}	
	                				}	               
	                			}else {
	                				this.Run_to_Wait();//�����л�
	                			}
	      	                    break;
	                		case 3://I/O����ָ��
	                			pcbtable.RunQueue.PSW = PC;//�ֳ�����
	                			pcbtable.RunQueue.timeflag = clock.GetTime();//��ȡ��ǰʱ��
	                			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 2;//��¼����״̬
	                			pcbtable.RunQueue.WaitState = 0;//���µȴ���־λ
	                			
	                			System.out.print("{" + pcbtable.RunQueue.Pro_ID + "wait}");//��ʾ
	                			pcbtable.WaitQueue.join(pcbtable.RunQueue);//����ȴ�����
	                			
	                			pcbtable.RunQueue = null;//����ָ̬����Ϊ��ָ��
	                			
	                			if(pcbtable.ReadyQueue.size() > 0)//����������зǿ�
	                			{
	                				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//���������ж�ͷ��Ϊ����̬
	                				pcbtable.ReadyQueue.quit();//����
	                				PC = pcbtable.RunQueue.PSW;//�ֳ��ָ�
	                				pcbtable.RunQueue.timeflag = clock.GetTime();//��ȡ��ǰʱ��
	                				pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//�޸�״̬��־λΪ����̬
	                			}
	               
	                			break;
	                		default:
	                			break;
	    		          }
	                	}
	                	
	                }else {//�����ǰû�н���������
	                	if(pcbtable.ReadyQueue.size() != 0)//���������зǿ�
	                	{
	                		pcbtable.RunQueue = pcbtable.ReadyQueue.front();//���������ж�ͷ��Ϊ����̬
	                		pcbtable.ReadyQueue.quit();//����
	                		PC = pcbtable.RunQueue.PSW;//�ֳ��ָ�
	                		pcbtable.RunQueue.timeflag = clock.GetTime();//��ȡ��ǰʱ��
	                		
	                		pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//���½���״̬Ϊ����̬
	                		
	                		int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , PC / 256);//MMU��ʼ����
	                		switch (pageframeid){
	                			case -1:
	                				mmu.OutDealing();//Խ���жϴ���
	                				//System.out.println("x");
	                				break;
	                			case -2:
	                				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , PC / 256);//ȱҳ�жϴ�����ȱҳ�����ڴ�
	                				//System.out.println("y");
	                				break;
	                			default:
	                				//System.out.println("z");
	                				Memory.Visit(pageframeid);	                				
	                				break;
	                		}
	                	}
	                }
	                
	                
	                if(pcbtable.RunQueue != null)//�����ǰ�н���������
	                {
	                	System.out.print("��ǰ���н���:" + pcbtable.RunQueue.Pro_ID);//��ʾ
	                }else {//����ǰû�н���������
	                	System.out.print("��ǰ���н���:null\t");//���null
	                }
	                
	                System.out.print("��ǰ��������:");//��ʾ
	                if(pcbtable.ReadyQueue.size() > 0)//����ǰ�������зǿ�
	                {
	                	pcbtable.ReadyQueue.Show_PCB();//��ʾ��������
	                }else {
	                	System.out.print("null\t");//���null;
	                }
	                
	                System.out.print("��ǰ�ȴ�����:");//��ʾ
	                if(pcbtable.WaitQueue.size() > 0)//����ǰ�ȴ����зǿ�
	                {
	                	pcbtable.WaitQueue.Show_PCB();//��ʾ�ȴ�����
	                }else {
	                	System.out.print("null\t");//���null
	                }
	                
	                System.out.print("��ǰ��ɶ���:");//��ʾ
	                if(pcbtable.FinishQueue.size() > 0)//����ǰ��ɶ��зǿ�
	                {
	                	pcbtable.FinishQueue.Show_PCB();//��ʾ��ɶ���
	                }else {
	                	System.out.print("null\t");//���null
	                }
	               
	                //mmu.Pagelist[pcbtable.RunQueue.Pro_ID].show();
	                 System.out.println("");//����
	          
	                int a[] = Memory.show();//��ȡÿһ��ҳ��ռ�õĽ��̺�
	                int b[]= Memory.show1();//��ȡÿһ��ҳ��ռ�õĽ��̵�ҳ��
	                inter.altermemory(a,b);//���½������ڴ������Ϣ
	             
	                inter.alterpro(pcbtable.proinfo,Process_Num);//����ҳ���Ͻ�����Ϣ
	                inter.altertlb(mmu.tlb.gettlb());//�޸�ҳ���Ͽ����Ϣ
	                
	                DispatchInfor = false;//���ȱ�־λ��Ϊfalse
	                clock.notifyAll();//���������ȴ��߳�
				}
			}
		}
		
		public PCB createpcb(JCB jcb,int i)throws Exception//����jcb����ҵ�ĵ�i�����̣�����pcb
		{
			PCB pcb = new PCB(jcb.JCB_ID,Process_Num,jcb.pro[i][1],jcb.InTime,clock.GetTime(),jcb.pro[i][2],jcb.pro[i][4],jcb.pro[i][5]);//���ù��캯����������
			buy[Process_Num] = true;
			pcbtable.proinfo[Process_Num][0] = Process_Num;//��¼���̺�
			pcbtable.proinfo[Process_Num][1] = jcb.JCB_ID;//��¼��������ҵ��
			pcbtable.proinfo[Process_Num][2] = 1;//��¼����״̬Ϊ����̬
			pcbtable.proinfo[Process_Num][3] = 0;//��¼�����Ѿ����е�ָ����
			pcbtable.proinfo[Process_Num][4] = jcb.pro[i][2];//��¼������ָ����
			
			pcb.Pro_InstrNum = jcb.pro[i][2];//ָ����Ŀ
			pcb.instruction = new Instruction[jcb.pro[i][2]];//����ָ������
						
			File file1 = new File("Disk\\Cylinder\\Track_" + Integer.toString((int)(jcb.pro_position[i] / 64)) + 
					"\\Sector_" + Integer.toString((int)(jcb.pro_position[i] % 64)) + ".txt");//�򿪴�Ÿý�����ҳ����ļ�
			BufferedReader bf1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));//���ļ�
			String str1;//���ÿ������ҳ���ж�ȡ��һ������
			String strs1[] = new String[6];//��Ŷ�ȡ��һ�����ݷֽ�������
			int j = pcb.Pro_InstrNum;//��ֵ
			int flagx = 1;//��־λ
			int c = 0;//��¼�Ѿ������ָ������
			while((str1 = bf1.readLine()) != null)//���δ����
			{
				if(flagx == 0) break;
				strs1 = str1.split("\t");//�������һ���ַ����ֽ�
				File file2  = new File("Disk\\Cylinder\\Track_" + Integer.valueOf(strs1[3]) + 
					"\\Sector_" + Integer.valueOf(strs1[4]) + ".txt");//������ҳ���ҵ���Ӧ��ҳ
				//System.out.println(strs1[3] + "--------" +strs1[4]);
				BufferedReader bf2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));//�򿪽��̴��ָ���ÿһҳ
				if(j <= 256 && j > 0)//��ʣ��δ�����ָ����С�ڵ���256��
				{
					flagx = 0;//�޸ı�־λ
					for(int l=0;l<j;l++)//��ȡÿһ��ָ��
					{
						try{
							String strs2[] = bf2.readLine().split("\t");//�ֽ�ÿһ�е��ַ���
							pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));//����һ��ָ��ڵ�
						}catch(Exception e) {}
					}
					
				}else if(j > 256)//��δ����ָ�����256��
				{
					j -= 256;//��256��ָ��
					for(int l=0;l<256;l++)//��ÿһ��ָ��
					{
						String strs2[] = bf2.readLine().split("\t");//�ֽ�ÿһ�е��ַ���
						pcb.instruction[c++] = new Instruction(Integer.valueOf(strs2[0]),Integer.valueOf(strs2[1]),Integer.valueOf(strs2[2]),Integer.valueOf(strs2[3]));//����һ��ָ��ڵ�
					}
				}
			}
			return pcb;//���ش����õĽ���
		}
		
		public void Run_to_Ready()//�����л�������̬���̼���������У��������ж��׽�������̬
		{
			pcbtable.RunQueue.PSW = PC;//�ֳ�����
			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 1;//���½���״̬Ϊ����̬
			pcbtable.ReadyQueue.join(pcbtable.RunQueue);//�����������
			pcbtable.RunQueue = pcbtable.ReadyQueue.front();//�����л�
			pcbtable.ReadyQueue.quit();//����
			pcbtable.proinfo[pcbtable.RunQueue.Pro_ID][2] = 0;//�����µĽ��̵�״̬Ϊ����̬
			PC = pcbtable.RunQueue.PSW;//�ֳ��ָ�
			pcbtable.RunQueue.timeflag = clock.GetTime();//��¼��ʼ����ʱ��
			
			int pageframeid = mmu.StartJob(pcbtable.RunQueue.Pro_ID , PC / 256);//MMU��ʼ����
			System.out.print("pc = " + PC + "[" + PC / 256 + "," + pageframeid + "]");
    		switch (pageframeid){
    			case -1:
    				mmu.OutDealing();//Խ���жϴ���
    				break;
    			case -2:
    				mmu.LostDealing(pcbtable.RunQueue.Pro_ID , PC / 256);//ȱҳ�жϴ�����ȱҳ�����ڴ�
    				break;
    			default://��������
    				break;
    		}
		}
		
		public void Run_to_Wait()////�����л�������̬���̼����������У��������ж��׽�������̬
		{
			pcbtable.RunQueue.PSW = PC;//�ֳ�����
			pcbtable.WaitQueue.join(pcbtable.RunQueue);//����ȴ�����
			if(pcbtable.ReadyQueue.size() > 0)//����������зǿ�
			{
				pcbtable.RunQueue = pcbtable.ReadyQueue.front();//�����л�
				pcbtable.ReadyQueue.quit();//����
				PC = pcbtable.RunQueue.PSW;//�ֳ��ָ�
				pcbtable.RunQueue.timeflag = clock.GetTime();//��¼��ʼ����ʱ��
			}
		}
		
		/*public void Wake()//���Ѵ����������е�
		{
			if(pcbtable.WaitQueue.size() > 0)//
			{
				pcbtable.ReadyQueue.join(pcbtable.WaitQueue.front());
				pcbtable.WaitQueue.quit();
			}
		}*/
	}
}


