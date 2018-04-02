package com.example.hucc;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.undo.UndoManager;

public class NoteBook extends JFrame implements ActionListener, DocumentListener{

	//������������������(�볷�������й�)
	protected UndoManager undo = new UndoManager();
	protected UndoableEditListener undoHandler = new UndoHandler();
	
	//�˵�
	JMenu fileMenu, editMenu, formatMenu, viewMenu, helpMenu; 
	// �Ҽ������˵���
	JPopupMenu popupMenu;
	JMenuItem popupMenu_Undo,popupMenu_Cut,popupMenu_Copy,popupMenu_Paste,
	        popupMenu_Delete,popupMenu_SelectAll;
	//"�ļ�"�˵���
	JMenuItem fileMenu_New,fileMenu_Open,fileMenu_Save,fileMenu_SaveAs,
	        fileMenu_PageSetUp,fileMenu_Print,fileMenu_Exit;
	//"�༭"�˵���
	JMenuItem editMenu_Undo,editMenu_Cut,editMenu_Copy,editMenu_Paste,
	        editMenu_Delete,editMenu_Find,editMenu_FindNext,editMenu_Replace,
	        editMenu_GoTo,editMenu_SelectAll,editMenu_TimeDate;
	//"��ʽ"�˵���
	JCheckBoxMenuItem formatMenu_LineWrap;
	JMenuItem  formatMenu_Font;
	// "�鿴"�˵���
	JCheckBoxMenuItem viewMenu_Status;
	// "����"�˵���
	JMenuItem helpMenu_HelpTopics,helpMenu_AboutNotePad;
	//"�ı�"�༭����
	JTextArea editArea;
	//״̬����ǩ
	JLabel statusLabel;
	//ϵͳ���а�
	Toolkit toolkit =Toolkit.getDefaultToolkit();
	Clipboard clipboard = toolkit.getSystemClipboard();
	
	// ��������
	String oldValue; //��ű༭��ԭ�������ݣ����ڱȽ��ı��Ƿ��иĶ�
	boolean isNewFile = true; //�Ƿ����ļ�(δ�������)
	File currentFile; //��ǰ�ļ���
	
	public NoteBook() {
		super("���±�");
		//�ı�ϵͳĬ������
		Font font = new Font("Dialog", Font.PLAIN, 12);
		Enumeration keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, font);
			}
		}
		//�����˵���
		JMenuBar menuBar = new JMenuBar();
		//�����ļ��˵����˵��ע���¼�����
		fileMenu = new JMenu("�ļ�(F)");
		fileMenu.setMnemonic('F'); //���ÿ�ݼ�ALT+F
		
		fileMenu_New = new JMenuItem("�½�(N)");
		fileMenu_New.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
	    fileMenu_New.addActionListener(this);
		
	    fileMenu_Open = new JMenuItem("��(O)");
		fileMenu_Open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
	    fileMenu_Open.addActionListener(this);
	    
	    fileMenu_Save = new JMenuItem("����(S)");
	    fileMenu_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
	    fileMenu_Save.addActionListener(this);
	    
	    fileMenu_SaveAs = new JMenuItem("���Ϊ(A)...");
	    fileMenu_SaveAs.addActionListener(this);
	    
	    fileMenu_PageSetUp = new JMenuItem("ҳ������(U)...");
	    fileMenu_PageSetUp.addActionListener(this);
	    
	    fileMenu_Print = new JMenuItem("��ӡ(P)...");
	    fileMenu_Print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
	    fileMenu_Print.addActionListener(this);
	    
	    fileMenu_Exit = new JMenuItem("�˳�(X)");
	    fileMenu_Exit.addActionListener(this);
	    
	   //�����༭�˵����˵��ע���¼�����
	    editMenu = new JMenu("�༭(E)"); 
	    editMenu.setMnemonic('E'); //���ÿ�ݼ�ALT+E
	  //��ѡ��༭�˵�ʱ�����ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
	    editMenu.addMenuListener(new  MenuListener() {
			
			public void menuSelected(MenuEvent e) { //ѡ��ĳ���˵�ʱ����
				checkMenuItemEnabled(); //���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}
			
			public void menuDeselected(MenuEvent e) {//ȡ��ѡ��ĳ���˵�ʱ����
				checkMenuItemEnabled(); //���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}
			
			public void menuCanceled(MenuEvent e) {  //ȡ���˵�ʱ����
				checkMenuItemEnabled(); //���ü��С����ơ�ճ����ɾ���ȹ��ܵĿ�����
			}
		});
	    
	    editMenu_Undo = new JMenuItem("����(U)");
	    editMenu_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,InputEvent.CTRL_MASK));
	    editMenu_Undo.addActionListener(this);
	    editMenu_Undo.setEnabled(false);
	    
	    editMenu_Cut = new JMenuItem("����(T)");
	    editMenu_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
	    editMenu_Cut.addActionListener(this);
	    
	    editMenu_Copy = new JMenuItem("����(C)");
	    editMenu_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_MASK));
	    editMenu_Copy.addActionListener(this);
	    
	    editMenu_Paste = new JMenuItem("ճ��(P)");
	    editMenu_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,InputEvent.CTRL_MASK));
	    editMenu_Paste.addActionListener(this);
	    
	    editMenu_Delete = new JMenuItem("ɾ��");
	    editMenu_Delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,0));
	    editMenu_Delete.addActionListener(this);
	    
	    editMenu_Find = new JMenuItem("����(F)...");
	    editMenu_Find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,InputEvent.CTRL_MASK));
	    editMenu_Find.addActionListener(this);
	    
	    editMenu_FindNext = new JMenuItem("������һ��(N)");
	    editMenu_FindNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
	    editMenu_FindNext.addActionListener(this);
	    
	    editMenu_Replace = new JMenuItem("�滻(R)...",'R');
	    editMenu_Replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
	    editMenu_Replace.addActionListener(this);
	    
	    editMenu_GoTo = new JMenuItem("ת��(G)...",'G');
	    editMenu_GoTo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
	    editMenu_GoTo.addActionListener(this);
	    
	    editMenu_SelectAll = new JMenuItem("ȫѡ", 'A');
	    editMenu_SelectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,InputEvent.CTRL_MASK));
	    editMenu_SelectAll.addActionListener(this);
	    
	    editMenu_TimeDate = new JMenuItem("ʱ��/����(D)",'D');
	    editMenu_TimeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0));
	    editMenu_TimeDate.addActionListener(this);
	    
	    //������ʽ�˵����˵��ע���¼�����
	    formatMenu = new JMenu("�˵�(O)");
	    formatMenu.setMnemonic('O');//���ÿ�ݼ�ALT+O
	    
	    formatMenu_LineWrap = new JCheckBoxMenuItem("�Զ�����(W)");
	    formatMenu_LineWrap.setMnemonic('W');//���ÿ�ݼ�ALT+W
	    formatMenu_LineWrap.setState(true);
	    formatMenu_LineWrap.addActionListener(this);
	    
	    formatMenu_Font = new JMenuItem("����(F)...");
	    formatMenu_Font.addActionListener(this);
	    
	    //�����鿴�˵����˵��ע���¼�����
	    viewMenu = new JMenu("�鿴(V)");
	    viewMenu.setMnemonic('V'); //���ÿ�ݼ�ALT+V
	    
	    viewMenu_Status = new JCheckBoxMenuItem("״̬��(S)");
	    viewMenu_Status.setMnemonic('S'); //���ÿ�ݼ�ALT+S
	    viewMenu_Status.setState(true);
	    viewMenu_Status.addActionListener(this);
	    
	  //���������˵����˵��ע���¼�����
	    helpMenu = new JMenu("����(H)");
	    helpMenu.setMnemonic('H');//���ÿ�ݼ�ALT+H
	    
	    helpMenu_HelpTopics = new JMenuItem("��������(H)");
	    helpMenu_HelpTopics.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,0));
	    helpMenu_HelpTopics.addActionListener(this);
	    
	    helpMenu_AboutNotePad = new JMenuItem("���ڼ��±�(A)");
	    helpMenu_AboutNotePad.addActionListener(this);
	    
	  //��˵������"�ļ�"�˵����˵���
	    menuBar.add(fileMenu);
	    fileMenu.add(fileMenu_New);
	    fileMenu.add(fileMenu_Open);
	    fileMenu.add(fileMenu_Save);
	    fileMenu.add(fileMenu_SaveAs);
	    fileMenu.addSeparator(); //�ָ���
	    fileMenu.add(fileMenu_PageSetUp);
	    fileMenu.add(fileMenu_Print);
	    fileMenu.addSeparator(); //�ָ���
	    fileMenu.add(fileMenu_Exit);
	    
	  //��˵������"�༭"�˵����˵��� 
	    menuBar.add(editMenu);
	    editMenu.add(editMenu_Undo);
	    editMenu.addSeparator(); //�ָ���
	    editMenu.add(editMenu_Cut);
	    editMenu.add(editMenu_Copy);
	    editMenu.add(editMenu_Paste);
	    editMenu.add(editMenu_Delete);
	    editMenu.addSeparator(); //�ָ���
	    editMenu.add(editMenu_Find);
	    editMenu.add(editMenu_FindNext);
	    editMenu.add(editMenu_Replace);
	    editMenu.add(editMenu_GoTo);
	    editMenu.addSeparator();//�ָ���
	    editMenu.add(editMenu_SelectAll);
	    editMenu.add(editMenu_TimeDate);
	    
	  //��˵������"��ʽ"�˵����˵���	
	    menuBar.add(formatMenu);
	    formatMenu.add(formatMenu_LineWrap);
	    formatMenu.add(formatMenu_Font);
	    
	    
	  //��˵������"�鿴"�˵����˵��� 
	    menuBar.add(viewMenu);
	    viewMenu.add(viewMenu_Status);
	    
	  //��˵������"����"�˵����˵���
	    menuBar.add(helpMenu);
	    helpMenu.add(helpMenu_HelpTopics);
	    helpMenu.addSeparator();
	    helpMenu.add(helpMenu_AboutNotePad);
	    
	  //�򴰿���Ӳ˵���
	    this.setJMenuBar(menuBar);
	    
	  //�����ı��༭������ӹ�����
	    editArea = new JTextArea(20, 50);
	    JScrollPane scroller = new JScrollPane(editArea);
	    scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    this.add(scroller, BorderLayout.CENTER); //�򴰿�����ı��༭��
	    editArea.setWrapStyleWord(true);  //���õ�����һ�в�������ʱ����
	    editArea.setLineWrap(true); //�����ı��༭���Զ�����Ĭ��Ϊtrue,����"�Զ�����"
	    oldValue = editArea.getText(); //��ȡԭ�ı��༭��������
	 
	  //�༭��ע���¼�����(�볷�������й�)
	    editArea.getDocument().addUndoableEditListener(undoHandler);
	    editArea.getDocument().addDocumentListener(this);
	    
	  //�����Ҽ������˵�
	    popupMenu = new JPopupMenu();
	    popupMenu_Undo = new JMenuItem("����(U)");
	    popupMenu_Cut = new JMenuItem("����(T)");
	    popupMenu_Copy = new JMenuItem("����(C)");
	    popupMenu_Paste = new JMenuItem("ճ��(P)");
	    popupMenu_Delete = new JMenuItem("ɾ��(D)");
	    popupMenu_SelectAll = new JMenuItem("ȫѡ(A)");
	    
	    popupMenu_Undo.setEnabled(false);
	    
	  //���Ҽ��˵���Ӳ˵���ͷָ���
	    popupMenu.add(popupMenu_Undo);
	    popupMenu.addSeparator();
	    popupMenu.add(popupMenu_Cut);
	    popupMenu.add(popupMenu_Copy);
	    popupMenu.add(popupMenu_Paste);
	    popupMenu.add(popupMenu_Delete);
	    popupMenu.addSeparator();
	    popupMenu.add(popupMenu_SelectAll);
	    
	  //�ı��༭��ע���Ҽ��˵��¼�
	    popupMenu_Undo.addActionListener(this);
	    popupMenu_Cut.addActionListener(this);
	    popupMenu_Copy.addActionListener(this);
	    popupMenu_Paste.addActionListener(this);
	    popupMenu_Delete.addActionListener(this);
	    popupMenu_SelectAll.addActionListener(this);
	    
	  //�ı��༭��ע���Ҽ��˵��¼�
	    editArea.addMouseListener(new MouseAdapter() {
	    	public void mousePressed(MouseEvent e) {
	    		if (e.isPopupTrigger()) { //���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					//����������ߵ�����ռ��е�λ�� X��Y ��ʾ�����˵�
				}
	    		checkMenuItemEnabled();//���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
	    		editArea.requestFocus();//�༭����ȡ����
	    	}
	    	public void mouseReleased(MouseEvent e) {
	    		if (e.isPopupTrigger()) { //���ش�����¼��Ƿ�Ϊ��ƽ̨�ĵ����˵������¼�
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
					//����������ߵ�����ռ��е�λ�� X��Y ��ʾ�����˵�
				}
	    		checkMenuItemEnabled();//���ü��У����ƣ�ճ����ɾ���ȹ��ܵĿ�����
	    		editArea.requestFocus();//�༭����ȡ����
	    	}
		});//�ı��༭��ע���Ҽ��˵��¼�����
	    
	  //���������״̬��
	   statusLabel = new JLabel("��F1��ȡ����"); 
	    this.add(statusLabel,BorderLayout.SOUTH);
	    
	  //���ô�������Ļ�ϵ�λ�á���С�Ϳɼ���
	    this.setLocation(100, 100);
	    this.setSize(650, 650);
	    this.setVisible(true);
	  //��Ӵ��ڼ�����
	    addWindowListener(new  WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		exitWindowChoose();
	    	}
		});
	    
	    checkMenuItemEnabled();
	    editArea.requestFocus();
	}//���캯��Notepad����
	
	//�رմ���ʱ����
	protected void exitWindowChoose() {
		editArea.requestFocus();
		String currentValue = editArea.getText();
		if (currentValue.equals(oldValue)==true) {
			System.exit(0);
		}else
		{
			int exitChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬�Ƿ񱣴棿",
					"�˳���ʾ",JOptionPane.YES_NO_CANCEL_OPTION);
			if (exitChoose == JOptionPane.YES_OPTION) {
				if (isNewFile) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setApproveButtonText("ȷ��");
					fileChooser.setDialogTitle("���Ϊ");
					
					int result = fileChooser.showSaveDialog(this);
					
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel.setText("��û�б����ļ�");
						return;
					}
					
					File saveFileName = fileChooser.getSelectedFile();
					
					if (saveFileName == null || saveFileName.getName().equals("")) {
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
					}else{
						try {
							FileWriter fw = new FileWriter(saveFileName);
							BufferedWriter bfw = new BufferedWriter(fw);
							bfw.write(editArea.getText(), 0, editArea.getText().length());
							bfw.flush();
							fw.close();
							
							isNewFile = false;
							currentFile = saveFileName;
							oldValue = editArea.getText();
							
							this.setTitle(saveFileName.getName()+" -���±�");
							statusLabel.setText("��ǰ���ļ�:" + saveFileName.getAbsolutePath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{
					try {
						FileWriter fw = new FileWriter(currentFile);
						BufferedWriter bfw = new BufferedWriter(fw);
						bfw.write(editArea.getText(), 0, editArea.getText().length());
						bfw.flush();
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					
				}
				System.exit(0);
			}else if(exitChoose == JOptionPane.NO_OPTION){
				System.exit(0);
			}else{
				return;
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		// �½�
		if (e.getSource() == fileMenu_New) {
			editArea.requestFocus();
			String currentValue = editArea.getText();
			boolean isTextChange = (currentValue.equals(oldValue))?false : true;
			if (isTextChange) {
				int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬�Ƿ񱣴棿","��ʾ",JOptionPane.YES_NO_CANCEL_OPTION);
				if (saveChoose == JOptionPane.YES_OPTION) {
				    String str = null;
				    JFileChooser fileChooser = new JFileChooser();
				    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				    fileChooser.setDialogTitle("���Ϊ");
				    int result = fileChooser.showSaveDialog(this);
				    if (result==JFileChooser.CANCEL_OPTION) {
						statusLabel.setText("��û��ѡ���κ��ļ�");
						return;
					}
				    File saveFileName = fileChooser.getSelectedFile();
				    if (saveFileName== null || saveFileName.getName().equals("")) {
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���",JOptionPane.ERROR_MESSAGE);
					}else {
						try {
							FileWriter fw = new FileWriter(saveFileName);
							BufferedWriter bfw = new BufferedWriter(fw);
							bfw.write(editArea.getText(), 0, editArea.getText().length());
							bfw.flush();//ˢ�¸����Ļ���
							bfw.close();
							isNewFile = false;
							currentFile = saveFileName;
							oldValue = editArea.getText();
							this.setTitle(saveFileName.getName()+"-���±�");
							statusLabel.setText("��ǰ���ļ���"+saveFileName.getAbsolutePath());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}else if (saveChoose == JOptionPane.NO_OPTION) {
					editArea.replaceRange("", 0, editArea.getText().length());
					statusLabel.setText("�½��ļ�");
					this.setTitle("�ޱ���-���±�");
					isNewFile = true;
					undo.discardAllEdits();//�������е�"����"����
					editMenu_Undo.setEnabled(true);
					oldValue = editArea.getText();
				}else if (saveChoose == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}else{
				editArea.replaceRange("", 0, editArea.getText().length());
				statusLabel.setText("�½��ļ�");
				this.setTitle("�ޱ���-���±�");
				isNewFile = true;
				undo.discardAllEdits();//�������е�"����"����
				editMenu_Undo.setEnabled(false);
				oldValue=editArea.getText();
			}	
		}else if (e.getSource() == fileMenu_Open) {
			editArea.requestFocus();
			String currentValue = editArea.getText();
			boolean isTextChange = (currentValue.equals(oldValue))?false:true;
			if (isTextChange) {
				int saveChoose = JOptionPane.showConfirmDialog(this, "�����ļ���δ���棬�Ƿ񱣴棿","��ʾ",JOptionPane.YES_NO_CANCEL_OPTION);
				if (saveChoose == JOptionPane.YES_OPTION) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setDialogTitle("���Ϊ");
					int result = fileChooser.showSaveDialog(this);
					if (result==JFileChooser.CANCEL_OPTION) {
						statusLabel.setText("��û��ѡ���κ��ļ�");
						return;
					}
					File saveFileName = fileChooser.getSelectedFile();
					if (saveFileName==null || saveFileName.getName().equals("")) {
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���",JOptionPane.ERROR_MESSAGE);
					}else{
						try {
							FileWriter fw = new FileWriter(saveFileName);
							BufferedWriter bfw = new BufferedWriter(fw);
							bfw.write(editArea.getText(), 0, editArea.getText().length());
							bfw.flush(); //ˢ�¸����Ļ���
							bfw.close();
							isNewFile=false;
							currentFile=saveFileName;
							oldValue=editArea.getText();
							this.setTitle(saveFileName.getName()+"-���±�");
							statusLabel.setText("��ǰ���ļ�:"+saveFileName.getAbsolutePath());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}			
				}else if (saveChoose == JOptionPane.NO_OPTION) {
					String str = null;
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					fileChooser.setDialogTitle("���ļ�");
					int result = fileChooser.showOpenDialog(this);
					if (result == JFileChooser.CANCEL_OPTION) {
						statusLabel.setText("��û��ѡ���κ��ļ�");
						return;
					}
					File fileName = fileChooser.getSelectedFile();
					if (fileName==null || fileName.getName().equals("")) {
						JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���",JOptionPane.ERROR_MESSAGE);
					}else{
						try {
							FileReader fr = new FileReader(fileName);
							BufferedReader bfr = new BufferedReader(fr);
							editArea.setText("");
							while ((str=bfr.readLine())!=null) {
								editArea.append(str);
							}
							this.setTitle(fileName.getName()+"- ���±�");
							statusLabel.setText("��ǰ���ļ�:" +fileName.getAbsolutePath());
							fr.close();
							isNewFile=false;
							currentFile=fileName;
							oldValue=editArea.getText();
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}else{
					return;
				}			
			}else{
				String str = null;
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogTitle("���ļ�");
				int result = fileChooser.showOpenDialog(this);
				if (result==JFileChooser.CANCEL_OPTION) {
					statusLabel.setText("��û��ѡ���κ��ļ�");
					return;
				}
				File fileName = fileChooser.getSelectedFile();
				if (fileName==null || fileName.getName().equals("")) {
					JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���",JOptionPane.ERROR_MESSAGE);
				}else{
					try {
						FileReader fr = new FileReader(fileName);
						BufferedReader bfr = new BufferedReader(fr);
						editArea.setText("");
						while ((str=bfr.readLine())!=null) {
							editArea.append(str);
						}
						this.setTitle(fileName.getName()+"- ���±�");
						statusLabel.setText("��ǰ���ļ�:"+fileName.getAbsolutePath());
						fr.close();
						isNewFile = false;
						currentFile=fileName;
						oldValue=editArea.getText();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}					
				}				
			}
		}else if (e.getSource()== fileMenu_Save) {
			editArea.requestFocus();
			if (isNewFile) {
			    String str = null;
			    JFileChooser fileChooser = new JFileChooser();
			    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    fileChooser.setDialogTitle("����");
			    int result = fileChooser.showSaveDialog(this);
			    if (result==JFileChooser.CANCEL_OPTION) {
					statusLabel.setText("��û��ѡ���κ��ļ�");
					return;
				}
			    File saveFileName = fileChooser.getSelectedFile();
			    if (saveFileName == null || saveFileName.getName().equals("")) {
					JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���",JOptionPane.ERROR_MESSAGE);			
				}else{
					try {
						FileWriter fw = new FileWriter(saveFileName);
						BufferedWriter bfw = new BufferedWriter(fw);
						bfw.write(editArea.getText(), 0, editArea.getText().length());
						bfw.flush();
						bfw.close();
						isNewFile=false;
						currentFile=saveFileName;
						oldValue=editArea.getText();
						this.setTitle(saveFileName.getName()+"-���±�");
						statusLabel.setText("��ǰ���ļ�:"+saveFileName.getAbsolutePath());
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			    
			}else{
				try {
					FileWriter fw = new FileWriter(currentFile);
					BufferedWriter bfw = new BufferedWriter(fw);
					bfw.write(editArea.getText(), 0, editArea.getText().length());
					bfw.flush();
					fw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}else if (e.getSource()==fileMenu_SaveAs) {
			editArea.requestFocus();
			String str= null;
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("���Ϊ");
			int result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.CANCEL_OPTION) {
				statusLabel.setText("��û��ѡ���κ��ļ�");
				return;
			}
			File saveFileName = fileChooser.getSelectedFile();
			if (saveFileName==null ||saveFileName.getName().equals("")) {
				JOptionPane.showMessageDialog(this, "���Ϸ����ļ���","���Ϸ����ļ���", JOptionPane.ERROR_MESSAGE);
			}else{
				try {
					FileWriter fw = new FileWriter(saveFileName);
					BufferedWriter bfw = new BufferedWriter(fw);
					bfw.write(editArea.getText(), 0, editArea.getText().length());
					bfw.flush();
					fw.close();
					oldValue=editArea.getText();
					this.setTitle(saveFileName.getName()+"-���±�");
					statusLabel.setText("��ǰ���ļ�:"+saveFileName.getAbsolutePath());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}else if (e.getSource()==fileMenu_PageSetUp) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�Բ��𣬴˹�����δʵ�֣�","��ʾ",JOptionPane.WARNING_MESSAGE);
		}else if (e.getSource()==fileMenu_Print) {
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "�Բ��𣬴˹�����δʵ�֣�","��ʾ",JOptionPane.WARNING_MESSAGE);
		}else if (e.getSource()==fileMenu_Exit) {
			int exitChoose = JOptionPane.showConfirmDialog(this, "ȷ��Ҫ�˳���?","�˳���ʾ",JOptionPane.OK_CANCEL_OPTION);
			if (exitChoose==JOptionPane.OK_OPTION) {
				System.exit(0);
			}else{
				return;
			}
			
		}else if (e.getSource()==editMenu_Undo || e.getSource() == popupMenu_Undo) {
			editArea.requestFocus();
			if (undo.canUndo()) {		   
			   try {
				   undo.undo();
			} catch (Exception e2) {
				System.out.println("Unable to undo:" + e2);
			}
			}
			if (!undo.canUndo()) {
				editMenu_Undo.setEnabled(false);
			}
		}else if (e.getSource()==editMenu_Cut || e.getSource() == popupMenu_Cut) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipboard.setContents(selection, null);
			editArea.replaceRange("", editArea.getSelectionStart(),editArea.getSelectionEnd());
			checkMenuItemEnabled();//���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
		}else if (e.getSource()==editMenu_Copy || e.getSource()==popupMenu_Copy) {
			editArea.requestFocus();
			String text = editArea.getSelectedText();
			StringSelection selection = new StringSelection(text);
			clipboard.setContents(selection, null);
			checkMenuItemEnabled();//���ü��У����ƣ�ճ����ɾ�����ܵĿ�����
		}else if (e.getSource()==editMenu_Paste || e.getSource()== popupMenu_Paste) {
			editArea.requestFocus();
			Transferable contents = clipboard.getContents(this);
			if (contents==null) return;
				String text="";
				try {
					text=(String) contents.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e2) {
					
				}
			editArea.replaceRange(text, editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled();
		}else if (e.getSource()==editMenu_Delete || e.getSource() == popupMenu_Delete) {
			editArea.requestFocus();
			editArea.replaceRange("", editArea.getSelectionStart(), editArea.getSelectionEnd());
			checkMenuItemEnabled();
		}else if (e.getSource()==editMenu_Find) {
			editArea.requestFocus();//���ҽ���
			find();
		}else if (e.getSource()==editMenu_FindNext) {
			editArea.requestFocus(); //������һ������
			find();
		}else if (e.getSource()==editMenu_Replace) {
			editArea.requestFocus(); //�滻
			replace();
		}else if (e.getSource()==editMenu_GoTo) { //ת��
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this,"�Բ��𣬴˹�����δʵ�֣�","��ʾ",JOptionPane.WARNING_MESSAGE);
		}else if (e.getSource()==editMenu_TimeDate) {
			editArea.requestFocus();
			Calendar rightNow=Calendar.getInstance();
			Date date=rightNow.getTime();
			editArea.insert(date.toString(), editArea.getCaretPosition());
		}else if (e.getSource()==editMenu_SelectAll || e.getSource()==popupMenu_SelectAll) {
			//ȫѡ
			editArea.selectAll();
		}else if (e.getSource()==formatMenu_LineWrap) {
			//�Զ�����(����ǰ������)
			if(formatMenu_LineWrap.getState())
			   editArea.setLineWrap(true);
			else	
				editArea.setLineWrap(false);
		}else if (e.getSource()==formatMenu_Font) {
			//��������
			editArea.requestFocus();
			font();
		}else if (e.getSource()==viewMenu_Status) {
			//����״̬���ɼ���
			if(viewMenu_Status.getState())
			   statusLabel.setVisible(true);
			else
				statusLabel.setVisible(false);
		}else if (e.getSource()==helpMenu_HelpTopics) {
			//��������
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "·��������Զ�⣬�Ὣ���¶�������","��������", JOptionPane.INFORMATION_MESSAGE);
		}else if (e.getSource()==helpMenu_AboutNotePad) {
			//����
			editArea.requestFocus();
			JOptionPane.showMessageDialog(this, "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n"+
					" ��д�ߣ���������ѧ��Ϣ����ѧԺ ���� \n"+
					" ��дʱ�䣺���������ڼ�                          \n"+
					" ����QQ��605063215                           \n"+
					" e-mail��huun82972277@163.com                \n"+
					" ��ѧ�ߣ�һЩ�ط�������ˣ�����֮��ϣ���������������лл��  \n"+
					"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n",
					"���±�",JOptionPane.INFORMATION_MESSAGE);
		}
	}//����actionPerformed()����
	
	//"����"����
	private void font() {
		final JDialog fontDialog = new JDialog(this, "��������", false);
		Container con = fontDialog.getContentPane();
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel fontLabel = new JLabel("����(F):");
		fontLabel.setPreferredSize(new Dimension(100, 20));//����һ��Dimension���������ʼ��Ϊָ����Ⱥ͸߶�
		JLabel styleLabel = new JLabel("����(Y)��");
		styleLabel.setPreferredSize(new Dimension(100, 20));
		JLabel sizeLabel = new JLabel("��С(S)��");
		sizeLabel.setPreferredSize(new Dimension(100, 20));
		final JLabel sample = new JLabel("��ѡ�ٵļ��±�-ZXZ's Notepad");
		final JTextField fontText = new JTextField(9);
		fontText.setPreferredSize(new Dimension(200, 20));
		final JTextField styleText = new JTextField(8);
		styleText.setPreferredSize(new Dimension(200, 20));
		final int style[] = {Font.PLAIN, Font.BOLD, Font.ITALIC, Font.BOLD+Font.ITALIC};
		final JTextField sizeText = new JTextField(5);
		sizeText.setPreferredSize(new Dimension(200,20));
		JButton okButton = new JButton("ȷ��");
		JButton cancel = new JButton("ȡ��");
		cancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				fontDialog.dispose();
			}
		});
		Font currentFont = editArea.getFont();
		fontText.setText(currentFont.getFontName());
		fontText.selectAll();
		
		if (currentFont.getStyle() == Font.PLAIN) 
			styleText.setText("����");
		else if(currentFont.getStyle() == Font.BOLD)
			styleText.setText("����");
		else if(currentFont.getStyle() == Font.ITALIC)
			styleText.setText("б��");
		else if(currentFont.getStyle()==(Font.BOLD+Font.ITALIC))
			styleText.setText("��б��");
			
		styleText.selectAll();
		String str = String.valueOf(currentFont.getSize());
		sizeText.setText(str);
		sizeText.selectAll();
		final JList fontList;
		final JList styleList;
		final JList sizeList;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		final String fontName[] = ge.getAvailableFontFamilyNames();
		fontList = new JList(fontName);
		fontList.setFixedCellWidth(86);
		fontList.setFixedCellHeight(20);
		fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final String fontStyle[] = {"����","����","б��","��б��"};
		styleList = new JList(fontStyle);
		styleList.setFixedCellWidth(86);
		styleList.setFixedCellHeight(20);
		styleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		if(currentFont.getStyle()==Font.PLAIN)
			styleList.setSelectedIndex(0);
		else if(currentFont.getStyle()==Font.BOLD)	
			styleList.setSelectedIndex(1);
		else if(currentFont.getStyle()==Font.ITALIC)	
			styleList.setSelectedIndex(2);
		else if(currentFont.getStyle()==(Font.BOLD + Font.ITALIC))
			styleList.setSelectedIndex(3);
		final String fontSize[] = {"8","9","10","11","12","14","16","18","20","22","24","26","28","36","48","72"};
		sizeList = new JList(fontSize);
		sizeList.setFixedCellWidth(43);
		sizeList.setFixedCellHeight(20);
		sizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		fontList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent event) {
				fontText.setText(fontName[fontList.getSelectedIndex()]);
				fontText.selectAll();
				Font sampleFontl = new Font(fontText.getText(), style[styleList.getSelectedIndex()], Integer.parseInt(sizeText.getText()));
			    sample.setFont(sampleFontl);
			}
		});
		
		styleList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent event) {
				int s = style[styleList.getSelectedIndex()];
				styleText.setText(fontStyle[s]);
				styleText.selectAll();
				Font sampleFont2 = new Font(fontText.getText(), style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
			    sample.setFont(sampleFont2);
			}
		});
		
		sizeList.addListSelectionListener(new ListSelectionListener() {
			
			public void valueChanged(ListSelectionEvent event) {
				sizeText.setText(fontSize[sizeList.getSelectedIndex()]);
				sizeText.selectAll();
				Font sampleFont3 = new Font(fontText.getText(), style[styleList.getSelectedIndex()],Integer.parseInt(sizeText.getText()));
			    sample.setFont(sampleFont3);
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Font okFont = new Font(fontText.getText(),style[styleList.getSelectedIndex()],Integer.parseInt(fontText.getText()));
				editArea.setFont(okFont);
				fontDialog.dispose();
			}
		});
		
		JPanel samplePanel = new JPanel();
		samplePanel.setBorder(BorderFactory.createTitledBorder("ʾ��"));
		
		samplePanel.add(sample);
		JPanel panel1 = new JPanel();
		JPanel panel2 =new JPanel();
		JPanel panel3 = new JPanel();
		panel2.add(fontText);
		panel2.add(styleText);
		panel2.add(sizeText);
		panel3.add(new JScrollPane(fontList));
		panel3.add(new JScrollPane(styleList));
		panel3.add(new JScrollPane(sizeList));
		panel3.add(cancel);
		con.add(panel1);
		con.add(panel2);
		con.add(panel3);
		con.add(samplePanel);
		fontDialog.setSize(350, 340);
		fontDialog.setLocation(200, 200);
		fontDialog.setResizable(false);
		fontDialog.setVisible(true);
	}

	//�滻����
	private void replace() {
		final JDialog replaceDialog = new JDialog(this,"�滻",false);//falseʱ������������ͬʱ���ڼ���״̬(����ģʽ)
		Container con = replaceDialog.getContentPane();//���ش˶Ի����contentPane����
		con.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel findContentLabel=new JLabel("��������(N)��");
		final JTextField findText=new JTextField(15);
		JButton findNextButton=new JButton("������һ��(F):");
		JLabel replaceLabel=new JLabel("�滻Ϊ(P)��");
		final JTextField replaceText=new JTextField(15);
		JButton replaceButton=new JButton("�滻(R)");
		JButton replaceAllButton=new JButton("ȫ���滻(A)");
		JButton cancel=new JButton("ȡ��");
		cancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				replaceDialog.dispose();
			}
		});
		final JCheckBox matchCheckBox=new JCheckBox("���ִ�Сд(C)");
		ButtonGroup bGroup = new ButtonGroup();
		final JRadioButton upButton=new JRadioButton("����(U)");
		final JRadioButton downButton=new JRadioButton("����(D)");
		downButton.setSelected(true);
		bGroup.add(upButton);
		bGroup.add(downButton);
		/*ButtonGroup��������Ϊһ�鰴ť����һ����⣨multiple-exclusion��������
		ʹ����ͬ�� ButtonGroup ���󴴽�һ�鰴ť��ζ�š�����������һ����ťʱ�����ر����е��������а�ť��*/
		/*JRadioButton����ʵ��һ����ѡ��ť���˰�ť��ɱ�ѡ���ȡ��ѡ�񣬲���Ϊ�û���ʾ��״̬��
		�� ButtonGroup �������ʹ�ÿɴ���һ�鰴ť��һ��ֻ��ѡ�����е�һ����ť��
		������һ�� ButtonGroup �������� add ������ JRadioButton ��������ڴ����С���*/
		
		//"������һ��"��ť����
		findNextButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//"���ִ�Сд(C)"��JCheckBox�Ƿ�ѡ��
				int k=0,m=0;
				String str1,str2,str3,str4,strA,strB;
				str1=editArea.getText();
				str2=findText.getText();
				str3=str1.toUpperCase();
				str4=str2.toUpperCase();
				if (matchCheckBox.isSelected()) {//���ִ�Сд
					strA=str1;
					strB=str2;
				}else{ //�����ִ�Сд,��ʱ����ѡ����ȫ�����ɴ�д(��Сд)���Ա��ڲ��� 
					strA=str3;
					strB=str4;
				}
				if (upButton.isSelected()) {
					if(editArea.getSelectedText()==null)
					   k=strA.lastIndexOf(strB, editArea.getCaretPosition()-1);
					else
						k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);
			        if (k>-1) {
						editArea.setCaretPosition(k);
						editArea.select(k, k+strB.length());
					}else{
						JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ����ݣ�","����",JOptionPane.INFORMATION_MESSAGE);
					}
				}else if (downButton.isSelected()) {
					if(editArea.getSelectedText()==null)
						k=strA.indexOf(strB, editArea.getCaretPosition()+1);
					else	
						k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);
				    if(k>-1){
				    	editArea.setCaretPosition(k);
				    	editArea.select(k, k+strB.length());
				    }else{
				    	JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ����ݣ�","����",JOptionPane.INFORMATION_MESSAGE);
				    }
				}
			}
		});//"������һ��"��ť��������
		
		//"�滻"��ť����
		replaceButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(replaceText.getText().length()==0&&editArea.getSelectedText()!=null)
				   editArea.replaceSelection("");
			    if(replaceText.getText().length()>0 && editArea.getSelectedText()!=null)		
					editArea.replaceSelection(replaceText.getText());
			}
		});
		//"ȫ���滻"��ť����
		replaceAllButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				editArea.setCaretPosition(0);//�����ŵ��༭����ͷ
				int k=0,m=0,replaceCount=0;
				if (findText.getText().length()==0) {
					JOptionPane.showMessageDialog(replaceDialog, "����д��������!","��ʾ",JOptionPane.WARNING_MESSAGE);
				    findText.requestFocus(true);
					return;
				}
				while(k>-1)//���ı��������ݱ�ѡ��ʱ(k>-1��ѡ��)�����滻�����򲻽���whileѭ��
				{//"���ִ�Сд(C)"��JCheckBox�Ƿ�ѡ��
					String str1,str2,str3,str4,strA,strB;
					str1=editArea.getText();
					str2=findText.getText();
					str3=str1.toUpperCase();
					str4=str2.toUpperCase();
					if (matchCheckBox.isSelected()) {//���ִ�Сд
						strA=str1;
						strB=str2;
					}else{//�����ִ�Сд,��ʱ����ѡ����ȫ�����ɴ�д(��Сд)���Ա��ڲ���
						strA=str3;
						strB=str2;
					}
					if (upButton.isSelected()) {
						if(editArea.getSelectedText()==null)
						  k=strA.lastIndexOf(strB, editArea.getCaretPosition()-1);
						else	
							k=strA.lastIndexOf(strB, editArea.getCaretPosition()-findText.getText().length()-1);
					    if(k>-1){
					       editArea.setCaretPosition(k);
					       editArea.select(k, k+strB.length());
					    }else{
					    	if(replaceCount==0){
					    		JOptionPane.showMessageDialog(replaceDialog, "�Ҳ��������ҵ�����!", "���±�",JOptionPane.INFORMATION_MESSAGE);
					    	}else{
					    		JOptionPane.showMessageDialog(replaceDialog,"�ɹ��滻"+replaceCount+"��ƥ������!","�滻�ɹ�",JOptionPane.INFORMATION_MESSAGE);
					    	}
					    }					   	
					}else if (downButton.isSelected()) {
						if(editArea.getSelectedText()==null)
							k=strA.indexOf(strB, editArea.getCaretPosition()+1);
						else	
							k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length()+1);
					
					    if(k>-1){
					    	editArea.setCaretPosition(k);
					    	editArea.select(k, k+strB.length());
					    }else{
					    	if (replaceCount==0) {
								JOptionPane.showMessageDialog(replaceDialog, "�Ҳ��������ҵ�����!", "���±�",JOptionPane.INFORMATION_MESSAGE);
							}else{
								JOptionPane.showMessageDialog(replaceDialog,"�ɹ��滻"+replaceCount+"��ƥ������!","�滻�ɹ�",JOptionPane.INFORMATION_MESSAGE);
							}
					    }
					}
					if (replaceText.getText().length()==0 && editArea.getSelectedText()!=null) {
						editArea.replaceSelection("");
						replaceCount++;
					}
					if (replaceText.getText().length()>0 && editArea.getSelectedText()!=null) {
						editArea.replaceSelection(replaceText.getText());
						replaceCount++;
					}
				}//whileѭ������
			}
		});
		
		//����"�滻"�Ի���Ľ���
		JPanel directionPanel=new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder("����"));
		//����directionPanel����ı߿�;
				//BorderFactory.createTitledBorder(String title)����һ���±���߿�ʹ��Ĭ�ϱ߿򣨸��񻯣���Ĭ���ı�λ�ã�λ�ڶ����ϣ���Ĭ�ϵ��� (leading) �Լ��ɵ�ǰ���ȷ����Ĭ��������ı���ɫ����ָ���˱����ı���
		directionPanel.add(upButton);
		directionPanel.add(downButton);
		JPanel pandl1=new JPanel();
		JPanel pandl2=new JPanel();
		JPanel pandl3=new JPanel();
		JPanel pandl4=new JPanel();
		pandl4.setLayout(new GridLayout(2, 1));
		pandl1.add(findContentLabel);
		pandl1.add(findText);
		pandl1.add(findNextButton);
		pandl2.add(replaceLabel);
		pandl2.add(replaceText);
		pandl2.add(pandl4);
		pandl3.add(matchCheckBox);
		pandl3.add(directionPanel);
		pandl3.add(cancel);
		pandl4.add(replaceButton);
		pandl4.add(replaceAllButton);
		con.add(pandl1);
		con.add(pandl2);
		con.add(pandl3);
		replaceDialog.setSize(420, 220);
		replaceDialog.setResizable(false);//���ɵ�����С
		replaceDialog.setLocation(230, 280);
		replaceDialog.setVisible(true);
	}

	//���ҷ���
	private void find() {
		final JDialog findDialog = new JDialog(this,"����",false);//falseʱ������������ͬʱ���ڼ���״̬(����ģʽ)
		Container con = findDialog.getContentPane();//���ش˶Ի����contentPane����	
		con.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel findContentLabel = new JLabel("��������(N)��");
		final JTextField findText = new JTextField(15);
		JButton findNextButton = new JButton("������һ��(F)��");
		final JCheckBox matchCheckBox=new JCheckBox("���ִ�Сд(C)");
		ButtonGroup bGroup=new ButtonGroup();
		final JRadioButton upButton=new JRadioButton("����(U)");
		final JRadioButton downButton=new JRadioButton("����(U)");
		downButton.setSelected(true);
		bGroup.add(upButton);
		bGroup.add(downButton);
		/*ButtonGroup��������Ϊһ�鰴ť����һ����⣨multiple-exclusion��������
		ʹ����ͬ�� ButtonGroup ���󴴽�һ�鰴ť��ζ�š�����������һ����ťʱ�����ر����е��������а�ť��*/
		/*JRadioButton����ʵ��һ����ѡ��ť���˰�ť��ɱ�ѡ���ȡ��ѡ�񣬲���Ϊ�û���ʾ��״̬��
		�� ButtonGroup �������ʹ�ÿɴ���һ�鰴ť��һ��ֻ��ѡ�����е�һ����ť��
		������һ�� ButtonGroup �������� add ������ JRadioButton ��������ڴ����С���*/
		JButton cancel=new JButton("ȡ��");
		//ȡ����ť�¼�����
		cancel.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				findDialog.dispose();
			}
		});
		//"������һ��"��ť����
		findNextButton.addActionListener(new  ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//"���ִ�Сд(C)"��JCheckBox�Ƿ�ѡ��
				int k=0,m=0;
				String str1,str2,str3,str4,strA,strB;
				str1=editArea.getText();
				str2=findText.getText();
				str3=str1.toUpperCase();
				str4=str2.toUpperCase();
				if (matchCheckBox.isSelected()) {//���ִ�Сд
					strA=str1;
					strB=str2;
				}else{ //�����ִ�Сд,��ʱ����ѡ����ȫ�����ɴ�д(��Сд)���Ա��ڲ��� 
					strA=str3;
					strB=str4;
				}
				if (upButton.isSelected()) {
					//k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
					if(editArea.getSelectedText()==null)
					  k=strA.lastIndexOf(strB,editArea.getCaretPosition()-1);
					else
					   k=strA.lastIndexOf(strB,editArea.getCaretPosition()-findText.getText().length()-1);
				    if(k>-1){
				    	editArea.setCaretPosition(k);
				    	editArea.select(k, k+strB.length());
				    }else{
				    	JOptionPane.showMessageDialog(null,"�Ҳ��������ҵ����ݣ�","����",JOptionPane.INFORMATION_MESSAGE);
				    }	
				}else if (downButton.isSelected()) {
					if(editArea.getSelectedText()==null)
						k=strA.indexOf(strB, editArea.getCaretPosition()+1);
					else
						k=strA.indexOf(strB, editArea.getCaretPosition()-findText.getText().length());
					if(k>-1){
						editArea.setCaretPosition(k);
						editArea.select(k, k+strB.length());
					}else{
						JOptionPane.showMessageDialog(null, "�Ҳ��������ҵ����ݣ�","����",JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});//"������һ��"��ť��������
		//����"����"�Ի���Ľ���
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		JPanel directionPanel=new JPanel();
		directionPanel.setBorder(BorderFactory.createTitledBorder("����"));
		directionPanel.add(upButton);
		directionPanel.add(downButton);
		panel1.setLayout(new GridLayout(2, 1));
		panel1.add(findNextButton);
		panel1.add(cancel);
		panel2.add(findContentLabel);
		panel2.add(findText);
		panel2.add(panel1);
		panel3.add(matchCheckBox);
		panel3.add(directionPanel);
		con.add(panel2);
		con.add(panel3);
		findDialog.setSize(410, 180);
		findDialog.setResizable(false);//���ɵ�����С
		findDialog.setLocation(230, 280);
		findDialog.setVisible(true);
	}//���ҷ�������

	//���ò˵���Ŀ����ԣ����У����ƣ�ճ����ɾ������
	protected void checkMenuItemEnabled() {
		String selectText=editArea.getSelectedText();
		if (selectText==null) {
			editMenu_Cut.setEnabled(false);
			popupMenu_Cut.setEnabled(false);
			editMenu_Copy.setEnabled(false);
			popupMenu_Copy.setEnabled(false);
			editMenu_Delete.setEnabled(false);
			popupMenu_Delete.setEnabled(false);
		}else{
			editMenu_Cut.setEnabled(true);
			popupMenu_Cut.setEnabled(true);
			editMenu_Copy.setEnabled(true);
			popupMenu_Copy.setEnabled(true);
			editMenu_Delete.setEnabled(true);
			popupMenu_Delete.setEnabled(true);
		}
		//ճ�����ܿ������ж�
		Transferable contents=clipboard.getContents(this);
		if (contents==null) {
			editMenu_Paste.setEnabled(false);
			popupMenu_Paste.setEnabled(false);
		}else{
			editMenu_Paste.setEnabled(true);
			popupMenu_Paste.setEnabled(true);
		}
	}
	
	public void changedUpdate(DocumentEvent e) {
		editMenu_Undo.setEnabled(true);
	}

	public void insertUpdate(DocumentEvent e) {
		editMenu_Undo.setEnabled(true);
	}

	public void removeUpdate(DocumentEvent e) {
		editMenu_Undo.setEnabled(true);
	}
	
	public class UndoHandler implements UndoableEditListener{

		public void undoableEditHappened(UndoableEditEvent uee) {
			undo.addEdit(uee.getEdit());
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NoteBook noteBook = new NoteBook();
		//ʹ�� System exit �����˳�Ӧ�ó���
        noteBook.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
