
public class Clock extends Thread{
	
	protected int NowTime;//当前机器时间
	CPU cpu;
	
	protected boolean flag = true;
	
	public Clock(CPU cpu) //构造函数
	{
		NowTime = 0;
		this.cpu = cpu;
	}
	
	public void run()//仿真时钟中断
	{
		while(true)
		{
			if(cpu.IsEnd)
			{
				System.out.println("所有作业运行完毕，时钟中断已经停止，现在时间：" + this.NowTime);
				flag = false;
				break;
			}
			
			synchronized(this) {
			
			
			try {
				Thread.sleep(100);//sleep 10ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//发出时钟中断，等待cpu进行调度
			{
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			this.NowTime = this.NowTime + 100;//机器时间+10ms
			cpu.DispatchInfor = true;
			notifyAll();
			
			}
		}
	}
	
	public int GetTime()//获取当前机器时间
	{
		return this.NowTime;
	}

}
