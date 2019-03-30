package state1;

public class Clock extends Thread{
	
	protected int NowTime;//当前机器时间
	CPU cpu;//CPU类对象
	
	public Clock(CPU cpu) //构造函数
	{
		NowTime = 0;//系统开始机器时间0
		this.cpu = cpu;//赋值
	}
	
	public void run()//仿真时钟中断
	{
		while(true)
		{
			if(cpu.IsEnd)//当运行结束标志位改变
			{
				System.out.println("所有作业运行完毕，时钟中断已经停止，现在时间：" + this.NowTime);//输出结束信息
				break;//退出大循环
			}
			synchronized(this) {//互斥访问对象clock
			
			try {
				Thread.sleep(10);//sleep 10ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//若调度线程没有完成一次调度
			{
				try {
					wait();//该线程等待，调度线程完成一次调度后调用notifyall唤醒该线程
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			this.NowTime = this.NowTime + 10;//机器时间+10ms
			cpu.DispatchInfor = true;//更改调度标志位
			notifyAll();//唤醒其他等待该互斥锁的调度线程
			
			}
		}
	}
	
	public int GetTime()//获取当前机器时间
	{
		return this.NowTime;//返回当前机器时间
	}

}
