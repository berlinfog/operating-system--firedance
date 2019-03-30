package state1;

public class Clock extends Thread{
	
	protected int NowTime;//当前机器时间
	CPU cpu;
	
	public Clock(CPU cpu) //构造函数
	{
		NowTime = 0;
		this.cpu = cpu;
	}
	
	public void run()//仿真时钟中断
	{
		while(true)
		{
			if(cpu.IsEnd)//所有任务结束
			{
				System.out.println("所有作业运行完毕，时钟中断已经停止，现在时间：" + this.NowTime);
				break;
			}
			
			synchronized(this) {//同步
			try {
				Thread.sleep(10);//sleep 10ms
			} catch (InterruptedException e) {//中断异常
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//发出时钟中断，等待CPU进行调度
			{
				try {
					wait();//进入等待状态
				} catch (InterruptedException e) {//中断异常
					e.printStackTrace();
				}
			}
			this.NowTime = this.NowTime + 10;//机器时间+10ms
			cpu.DispatchInfor = true;//调度信号，true表示产生时钟中断，需要进行调度
			notifyAll();//唤醒所有进程
			
			}
		}
	}
	
	public int GetTime()//获取当前机器时间
	{
		return this.NowTime;//返回时间
	}

}
