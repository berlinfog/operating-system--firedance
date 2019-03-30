import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class CPU {

	protected boolean DispatchInfor;//�����źţ���ʱ���жϷ���,true��ʾ����ʱ���жϣ���Ҫ���е��ȣ�false��ʾ����Ҫ����
	
	private Clock clock;//ʱ��
	protected boolean IsEnd;//���н����Ƿ��Ѿ��������
	
	public CPU()//���캯��
	{	
		
	}
	
	public void Start()//ϵͳ��ʼ����
	{
		clock = new Clock(this);
		Dispatch jobdispatch = new Dispatch();
		clock.start();
		jobdispatch.start();
	}
	
	
	class Dispatch extends Thread{//��ҵ�����߳�
	
		private int flag = 0;
		public Dispatch()//���캯��
		{
		
		}
		public void run()//��дrun����
		{
			while(true)
			{
				
				synchronized (clock) {
	                if(!DispatchInfor) {//�߳�ͬ����ȷ��ÿ��ʱ���ж϶������˵���
	                    try {
							clock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                if(clock.flag) 
	                {
	                	System.out.println(clock.GetTime() + ":��" + (++flag) + "�ε���");
	                }
	                
	                if(flag == 10)
	                IsEnd = true;
	                
	                DispatchInfor = false;
	                clock.notifyAll();
				}
			}
		}
	}
	
	public static void main (String [] args) throws Exception
	{	
		CPU cpu = new CPU();
		cpu.Start();
	}
}
		
		