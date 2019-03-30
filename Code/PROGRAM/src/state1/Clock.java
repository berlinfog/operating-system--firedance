package state1;

public class Clock extends Thread{
	
	protected int NowTime;//��ǰ����ʱ��
	CPU cpu;//CPU�����
	
	public Clock(CPU cpu) //���캯��
	{
		NowTime = 0;//ϵͳ��ʼ����ʱ��0
		this.cpu = cpu;//��ֵ
	}
	
	public void run()//����ʱ���ж�
	{
		while(true)
		{
			if(cpu.IsEnd)//�����н�����־λ�ı�
			{
				System.out.println("������ҵ������ϣ�ʱ���ж��Ѿ�ֹͣ������ʱ�䣺" + this.NowTime);//���������Ϣ
				break;//�˳���ѭ��
			}
			synchronized(this) {//������ʶ���clock
			
			try {
				Thread.sleep(10);//sleep 10ms
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(cpu.DispatchInfor)//�������߳�û�����һ�ε���
			{
				try {
					wait();//���̵߳ȴ��������߳����һ�ε��Ⱥ����notifyall���Ѹ��߳�
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			this.NowTime = this.NowTime + 10;//����ʱ��+10ms
			cpu.DispatchInfor = true;//���ĵ��ȱ�־λ
			notifyAll();//���������ȴ��û������ĵ����߳�
			
			}
		}
	}
	
	public int GetTime()//��ȡ��ǰ����ʱ��
	{
		return this.NowTime;//���ص�ǰ����ʱ��
	}

}
