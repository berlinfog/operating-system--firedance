import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Stack;

public class CPU {

	protected boolean DispatchInfor;//调度信号，由时钟中断发出,true表示产生时钟中断，需要进行调度，false表示不需要调度
	
	private Clock clock;//时钟
	protected boolean IsEnd;//所有进程是否已经运行完毕
	
	public CPU()//构造函数
	{	
		
	}
	
	public void Start()//系统开始工作
	{
		clock = new Clock(this);
		Dispatch jobdispatch = new Dispatch();
		clock.start();
		jobdispatch.start();
	}
	
	
	class Dispatch extends Thread{//作业调度线程
	
		private int flag = 0;
		public Dispatch()//构造函数
		{
		
		}
		public void run()//重写run方法
		{
			while(true)
			{
				
				synchronized (clock) {
	                if(!DispatchInfor) {//线程同步，确保每次时钟中断都进行了调度
	                    try {
							clock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
	                }
	                
	                if(clock.flag) 
	                {
	                	System.out.println(clock.GetTime() + ":第" + (++flag) + "次调度");
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
		
		