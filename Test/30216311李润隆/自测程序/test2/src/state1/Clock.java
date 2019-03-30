package state1;

public class Clock extends Thread{
	
	protected int NowTime;//��ǰ����ʱ��
	CPU cpu;
	
	public Clock(CPU cpu) //���캯��
	{
		NowTime = 0;
		this.cpu = cpu;
	}
	
	public void run()//����ʱ���ж�
	{
		while(true)
		{
			if(cpu.IsEnd)//�����������
			{
				System.out.println("������ҵ������ϣ�ʱ���ж��Ѿ�ֹͣ������ʱ�䣺" + this.NowTime);
				break;
			}
			
			synchronized(this) {//ͬ��
			try {
				Thread.sleep(10);//sleep 10ms
			} catch (InterruptedException e) {//�ж��쳣
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//����ʱ���жϣ��ȴ�CPU���е���
			{
				try {
					wait();//����ȴ�״̬
				} catch (InterruptedException e) {//�ж��쳣
					e.printStackTrace();
				}
			}
			this.NowTime = this.NowTime + 10;//����ʱ��+10ms
			cpu.DispatchInfor = true;//�����źţ�true��ʾ����ʱ���жϣ���Ҫ���е���
			notifyAll();//�������н���
			
			}
		}
	}
	
	public int GetTime()//��ȡ��ǰ����ʱ��
	{
		return this.NowTime;//����ʱ��
	}

}
