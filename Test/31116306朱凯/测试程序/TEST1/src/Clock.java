
public class Clock extends Thread{
	
	protected int NowTime;//��ǰ����ʱ��
	CPU cpu;
	
	protected boolean flag = true;
	
	public Clock(CPU cpu) //���캯��
	{
		NowTime = 0;
		this.cpu = cpu;
	}
	
	public void run()//����ʱ���ж�
	{
		while(true)
		{
			if(cpu.IsEnd)
			{
				System.out.println("������ҵ������ϣ�ʱ���ж��Ѿ�ֹͣ������ʱ�䣺" + this.NowTime);
				flag = false;
				break;
			}
			
			synchronized(this) {
			
			
			try {
				Thread.sleep(100);//sleep 10ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//����ʱ���жϣ��ȴ�cpu���е���
			{
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			this.NowTime = this.NowTime + 100;//����ʱ��+10ms
			cpu.DispatchInfor = true;
			notifyAll();
			
			}
		}
	}
	
	public int GetTime()//��ȡ��ǰ����ʱ��
	{
		return this.NowTime;
	}

}
