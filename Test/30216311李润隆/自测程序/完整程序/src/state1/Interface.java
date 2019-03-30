package state1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Interface extends JFrame{
	
	protected JPanel jbc;
	protected JPanel jbn;
	protected JPanel jbs;
	
	protected JFrame jf3 = new JFrame("�ڴ����");
	protected JFrame jf4 = new JFrame("������");
	
	protected JLabel jb1 = new JLabel();//���JLabel
	protected JLabel jb2 = new JLabel();//���JLabel
	protected JLabel jb3 = new JLabel();//���JLabel
	protected JLabel jb4 = new JLabel();//���JLabel
	protected JLabel jb5 = new JLabel();//���JLabel

	
	
	protected JPanel j1 = new JPanel();//�������
	protected JPanel j2 = new JPanel();//�������
	protected JPanel j3 = new JPanel();//�ڴ����
	protected JPanel j4 = new JPanel();//������
	protected JPanel j5 = new JPanel();//��ҵ���

	protected JButton Initial_Button = new JButton("��ʼ��");//��ʼ������ϵͳ��ť
	protected JButton Create_Button = new JButton("������ҵ");//������ҵ��ť
	protected JButton Open_Button = new JButton("����");//��ʼ���в���ϵͳ��ť
	protected JButton Close_Button = new JButton("�ر�");///�رղ���ϵͳ��ť
	protected JButton Pause_Button = new JButton("��ͣ");//��ͣ��ť
	protected JButton Continue_Button = new JButton("����");//������ť	
	
	protected JButton button1;//���̰�ť
	protected JButton button2;//���ܰ�ť
	protected JButton button3;//�ڴ水ť
	protected JButton button4;//���ť
	protected JButton button5;//��ҵ��ť
	
	String[] columnNames1 = { "���̺�","������ҵ��","��ǰ״̬","�����е�ָ����","��ָ����"}; 
	String[] columnNames3 = { "ҳ���","������̺�","ҳ��","ҳ���","������̺�","ҳ��"};
	String[] columnNames4 = { "������","���̺�","ҳ��","ҳ���","�Ƿ�Ϊ��"};
	String [][]tableVales1 = new String[34][5];
	String [][]tableVales3 = new String[32][6];
	String [][]tableVales4 = new String[5][5];
	
	public Interface()//���캯��
	{
		this.setTitle("�ҵĲ���ϵͳ");//�������
		this.setBounds(50,50,800,800);//�����С
		this.SetUI();//����UI
		this.SetActionListener();//���ü���
		this.setVisible(true);//����Ϊ�ɼ�
		jf3.setBounds(900, 50, 800, 800);
		jf3.setVisible(false);
		jf4.setSize(300,170);
		jf4.setVisible(false);
	}
	
	public void SetUI()
	{
		this.jbc = new JPanel();
		this.jbs = new JPanel();
		this.jbn = new JPanel();
			
		this.button1 = new JButton("����");
		this.button2 = new JButton("");
		this.button3 = new JButton("�ڴ�");
		this.button4 = new JButton("���");
		this.button5 = new JButton("");
		
		this.setLayout(new BorderLayout(30,30));	
		this.add(jbn,BorderLayout.NORTH);
		jbn.setPreferredSize(new Dimension(0, 30));
		jbn.setLayout(new GridLayout(1,8));
		//jbn.setBackground(new java.awt.Color(30,30,30));	
		jbn.add(button1);
		jbn.add(button3);
		jbn.add(button4);
		jbn.add(jb1);
		jbn.add(jb2);
		jbn.add(jb3);
		jbn.add(jb4);
		jbn.add(jb5);
		this.add(jbs,BorderLayout.SOUTH);
		jbs.setPreferredSize(new Dimension(0, 80));
		jbs.setLayout(new FlowLayout(FlowLayout.CENTER,10,15));
		jbs.add(this.Initial_Button);
		jbs.add(this.Create_Button);
		jbs.add(this.Open_Button);
		jbs.add(this.Close_Button);
		jbs.add(this.Pause_Button);
		jbs.add(this.Continue_Button);
		
		
		this.resetcolor();
		
		this.setui1();
		this.add(j1,BorderLayout.CENTER);
		
		this.setui3();
		jf3.add(j3);
		
		this.setui4();
		jf4.add(j4);
	}
	
	public void setui1()//����
	{
		JTable table = new JTable(tableVales1,columnNames1);
		table.setPreferredScrollableViewportSize(new Dimension(770,550));
		table.setSize(300, 400);
		j1.add(table);
		JScrollPane scrollPane=new JScrollPane(table);
		j1.add(scrollPane);
		table.setLocation(25, 25);
	}
	
	public void setui3()//�ڴ�
	{
		JTable table = new JTable(tableVales3,columnNames3);
		table.setPreferredScrollableViewportSize(new Dimension(770,650));
		table.setSize(300, 400);
		j3.add(table);
		JScrollPane scrollPane=new JScrollPane(table);
		j3.add(scrollPane);
		table.setLocation(25, 25);
	}
	public void setui4()//���
	{
		JTable table = new JTable(tableVales4,columnNames4);
		table.setPreferredScrollableViewportSize(new Dimension(260,85));
		table.setSize(300, 400);
		j4.add(table);
		JScrollPane scrollPane=new JScrollPane(table);
		j4.add(scrollPane);
		table.setLocation(25, 25);
	}
	
	public void altermemory(int[] a,int[] b)
	{
		for(int i=0;i<32;i++)
		{
			tableVales3[i][0] = Integer.toString(i*2);
			tableVales3[i][1] = Integer.toString(a[i*2]);
			tableVales3[i][2] = Integer.toString(b[i*2]);
			tableVales3[i][3] = Integer.toString(i*2+1);
			tableVales3[i][4] = Integer.toString(a[i*2+1]);
			tableVales3[i][5] = Integer.toString(b[i*2+1]);
		}
		jf3.add(j3);
	}
	
	public void alterpro(int[][] a,int n)
	{
		for(int i=0;i<n;i++)
		{
			tableVales1[i][0] = Integer.toString(a[i][0]);
			tableVales1[i][1] = Integer.toString(a[i][1]);
			if (a[i][2] == 0)
			{
				tableVales1[i][2] = "����̬";
			}else if(a[i][2] == 1) {
				tableVales1[i][2] = "����̬";
			}else if(a[i][2] == 2) {
				tableVales1[i][2] = "�ȴ�̬";
			}else if(a[i][2] == 3) {
				tableVales1[i][2] = "���̬";
			}
			tableVales1[i][3] = Integer.toString(a[i][3]);
			tableVales1[i][4] = Integer.toString(a[i][4]);						
		}
		
		this.add(j1);
	}
	
	public void altertlb(int[][] a)
	{
		for(int i=0;i<5;i++)
		{
			tableVales4[i][0] = Integer.toString(a[i][0]);
			tableVales4[i][1] = Integer.toString(a[i][1]);
			tableVales4[i][2] = Integer.toString(a[i][2]);
			tableVales4[i][3] = Integer.toString(a[i][3]);	
			if(a[i][4] == 0)
			{
				tableVales4[i][4] = "��";
			}else {
				tableVales4[i][4] = "�ǿ�";
			}
		}
		jf4.add(j4);
	}
	
	public void SetActionListener()
	{
		this.Initial_Button.addActionListener(//�¼�����
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				CPU.initialflag = false;
		 				}});
		this.Create_Button.addActionListener(//�¼�����
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				CPU.jobflag = false;
		  				CPU.jobway = 0;
		 				}});
		
		this.Open_Button.addActionListener(//�¼�����
	  		new ActionListener() {
	  			public void actionPerformed(ActionEvent e) {
	  				if(CPU.jobflag == true)
	  				{
	  					CPU.jobflag = false;
	  					CPU.jobway = 1;
	  				}
	  				CPU.startflag = false;
	 				}});
		this.Close_Button.addActionListener(//�¼�����
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				System.exit(0);
		 				}});
		this.Pause_Button.addActionListener(//�¼�����
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				CPU.pauseflag = true;
		 				}});
		this.Continue_Button.addActionListener(//�¼�����
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				CPU.pauseflag = false;
		 				}});
		
		
		this.button1.addActionListener(
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				reset();
		  				button1.setEnabled(false);
		  				resetcolor();
		  				button1.setBackground(new java.awt.Color(255,255,255));
		  				jf3.setVisible(false);
		  				jf4.setVisible(false);
		 				}});
		this.button2.addActionListener(
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				reset();
		  				button2.setEnabled(false);
		  				resetcolor();
		  				button2.setBackground(new java.awt.Color(255,255,255));	
		  				CPU.xxstartfalg = true;
		 				}});
		this.button3.addActionListener(
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				reset();
		  				button3.setEnabled(false);
		  				resetcolor();
		  				button3.setBackground(new java.awt.Color(255,255,255));
		  				jf3.setVisible(true);
		 				}});
		this.button4.addActionListener(
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				reset();
		  				button4.setEnabled(false);
		  				resetcolor();
		  				button4.setBackground(new java.awt.Color(255,255,255));
		  				jf4.setVisible(true);
		  				
		 				}});
		this.button5.addActionListener(
		  		new ActionListener() {
		  			public void actionPerformed(ActionEvent e) {
		  				reset();
		  				button5.setEnabled(false);
		  				resetcolor();
		  				button5.setBackground(new java.awt.Color(255,255,255));
		  				CPU.xxstartfalg = false;
		 				}});
	}
	
	public void reset()
	{
		this.button1.setEnabled(true);
		this.button2.setEnabled(true);
		this.button3.setEnabled(true);
		this.button4.setEnabled(true);
		this.button5.setEnabled(true);
	}
	
	public void resetcolor()
	{
		button1.setBackground(new java.awt.Color(211,211,211));
		button2.setBackground(new java.awt.Color(211,211,211));
		button3.setBackground(new java.awt.Color(211,211,211));
		button4.setBackground(new java.awt.Color(211,211,211));
		button5.setBackground(new java.awt.Color(211,211,211));
	}
}
