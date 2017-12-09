package javaapplication13;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
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
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.JWindow;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;

public class PwdPRO extends JFrame {

	private Container contentPane;
	private Component me=this;
	private final JMenuBar menuBar=new JMenuBar();
	private final JPopupMenu popupMenu=new JPopupMenu();
	private final JToolBar toolBar=new JToolBar();
	private ButtonGroup bruteOrList;
	private static Timer splashTimer = null;
	private Thread crackThread = null;
	private JList hashList;
	private JList wordList;
	private JList crackedResultList;
        private JList userList;
	private JScrollPane hashScrollList;
	private JScrollPane wordScrollList;
	private final ArrayList hashesList=new ArrayList();
	private final ArrayList wordsList=new ArrayList();
	private final ArrayList crackedList=new ArrayList();
        private final ArrayList usersList=new ArrayList();
	private JButton addHash;
        private JButton user;
	private JButton selectHashFile;
	private JButton addWordListFile;
	private JButton startCracking;
	private JButton stopCracking;
	private JFileChooser fileChooser;
	private File hashFile;
	private final JTextField minLen=new JTextField();
	private final JTextField maxLen=new JTextField();
	private final JComboBox algorithm=new JComboBox(new String[]{"SHA-1"});
	private final JComboBox strength=new JComboBox(new String[] {"Alphabets [a-z]","Alpha Numeric [a-z][0-9]", "Alpha Numeric Symbols [a-z][0-9][!-~]"});
	private JLabel status;
	private JProgressBar statusProgressBar;
	private MessageDigest md;
	private byte buffer[]=new byte[8192];
	private byte digest[]=new byte[8192];
	private boolean bruteforce=false;
        private boolean swapUsr=false;
	private boolean started=false;

        public PwdPRO()
	{
		setTitle("PwdPRO");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(650,430);
		setLocation( (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()-getWidth())/2 , (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()-getHeight())/2 );
		setResizable(false);

		contentPane=getContentPane();

		setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
		addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}
			public void windowClosing(WindowEvent arg0) {
				exit();
			}
			public void windowClosed(WindowEvent arg0) {
			}
			public void windowDeactivated(WindowEvent arg0) {
			}
			public void windowDeiconified(WindowEvent arg0) {
			}
			public void windowIconified(WindowEvent arg0) {
			}
			public void windowOpened(WindowEvent arg0) {
			}
		});

		setMyMenuBar();
		
		setCenterPanel();
		setMyContextMenu();

		JPanel bottom=new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));

		JPanel row=new JPanel();
		JLabel answer=new JLabel();
		answer.setText("Cracked Hash");
		answer.setHorizontalAlignment(JTextField.CENTER);
		answer.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
		row.add(answer);

		crackedResultList=new JList();
		crackedResultList.setVisibleRowCount(6);
		crackedResultList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
				JPanel cell=new JPanel();
				cell.setLayout(new BorderLayout());
				JLabel count=new JLabel((arg2+1)+". ");
				JLabel text=new JLabel(arg1.toString());
				cell.setToolTipText(arg1.toString());
				cell.setBackground(arg3?new Color(170,190,210):Color.white);
				cell.add(count, BorderLayout.WEST);
				cell.add(text,BorderLayout.CENTER);
				return cell;
			}
		});
		JScrollPane cr=new JScrollPane(crackedResultList);
		cr.setPreferredSize(new Dimension(256,96));
		row.add(cr);

		JPanel controlButtons=new JPanel();
		controlButtons.setLayout(new BoxLayout(controlButtons, BoxLayout.Y_AXIS));
		stopCracking = new JButton("Stop Cracking");
		stopCracking.setVisible(false);
		stopCracking.setCursor(Cursor.getDefaultCursor());
		stopCracking.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopHashCracking();
			}
		});

		startCracking = new JButton("Start");
		startCracking.setCursor(Cursor.getDefaultCursor());
		startCracking.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				startHashCracking();
			}
		});
		controlButtons.add(startCracking);
		controlButtons.add(stopCracking);
		row.add(controlButtons);
		bottom.add(row);

		JPanel statusBar=new JPanel();
		statusBar.setLayout(new BoxLayout(statusBar,BoxLayout.X_AXIS));

		status=new JLabel();
		status.setHorizontalAlignment(JTextField.CENTER);
		status.setFont(new Font("Arial",Font.BOLD,12));
		status.setText("PwdPRO ");

		statusProgressBar=new JProgressBar();
		statusProgressBar.setMinimum(0);
		statusProgressBar.setMaximum(100);
		statusProgressBar.setStringPainted(true);
		statusProgressBar.setToolTipText("Indicates Progress of Cracking Hash");

		JSplitPane spl=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,status,statusProgressBar);
		spl.setDividerSize(1);
		spl.setDividerLocation(520);
		spl.setEnabled(false);
		statusBar.add(spl);
		bottom.add(statusBar);

		contentPane.add(bottom);

		showSplash(this);
	}

	
	private void stopHashCracking()
	{
		statusProgressBar.setValue(0);
		statusProgressBar.setString("");
		status.setText("PwdPRO ");
		startCracking.setText("Start");
		((JMenuItem)popupMenu.getSubElements()[0]).setText("Start");
		((JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[4]).setText("Start");
		

		stopCracking.setVisible(false);
		((JMenuItem)popupMenu.getSubElements()[1]).setEnabled(false);			
		((JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[5]).setEnabled(false); 

		getRootPane().setCursor(Cursor.getDefaultCursor());
		started=false;
		crackThread.stop();
		crackThread=null;
	}

	
	private void startHashCracking()
	{
		stopCracking.setVisible(true);
		((JMenuItem)popupMenu.getSubElements()[1]).setEnabled(true);
		((JMenuItem)popupMenu.getSubElements()[0]).setText("Pause");
		((JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[5]).setEnabled(true); 
		((JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[4]).setText("Pause");
		
		startCracking.setText("Pause");
		getRootPane().setCursor(Cursor.getDefaultCursor());

		started=!started;

		if(started)
		{
			if(crackThread==null)
				crack();
			else
			{
				getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				status.setText("Cracking please wait...");
				crackThread.resume();
			}
		}
		else
		{
			startCracking.setText("Resume Cracking");
			((JMenuItem)popupMenu.getSubElements()[0]).setText("Resume Cracking");
			((JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[4]).setText("Resume Cracking");
			status.setText("Click on resume to resmue cracking or stop to stop cracking.");
			crackThread.suspend();
		}
	}

        private void getUsername()
        {
            String usr=JOptionPane.showInputDialog(me,"Please enter username","Enter username",JOptionPane.QUESTION_MESSAGE);
            if((usr==null)||(usr.equals("")))
			return;
		usersList.add(usr);
                userList.setListData(usersList.toArray());
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
        }

	
	private void getHash()
	{
		String hash=JOptionPane.showInputDialog(me,"Please enter the hash","Enter Hash",JOptionPane.QUESTION_MESSAGE);
		if(hash==null||hash.equals(""))
			return;
		hashesList.add(hash);
		hashList.setListData(hashesList.toArray());
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
	}

	
	private void getHashFile()
	{
		fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File f)
			{
				return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
			}
			public String getDescription()
			{
				return "Text Files";
			}
		});
		int option=fileChooser.showOpenDialog(null);
		if(option==JFileChooser.APPROVE_OPTION)
		{
			hashFile=fileChooser.getSelectedFile();
			try {
				DataInputStream fc=new DataInputStream(new FileInputStream(hashFile));
				while(fc.available()>0)
				{
					hashesList.add(fc.readLine());
				}
				hashList.setListData(hashesList.toArray());
				fc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
	}

	
	private void getWordListFile()
	{
		fileChooser=new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setFileFilter(new FileFilter() {
			public boolean accept(File f)
			{
		       	return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
			}
			public String getDescription()
			{
				return "Text Files";
			}
		});
		int option=fileChooser.showOpenDialog(null);
		if(option==JFileChooser.APPROVE_OPTION)
		{
			wordsList.add(fileChooser.getSelectedFile().getAbsolutePath());
			wordList.setListData(wordsList.toArray());
		}
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
	}

	
	private void crack()
	{
		crackedList.clear();
		crackedResultList.setListData(crackedList.toArray());
		crackThread=new Thread() {
			public void run()
			{
				try {
					buffer = new byte[8192];
					digest = new byte[8192];
					statusProgressBar.setValue(0);
					status.getGraphics().clearRect(status.getX(),status.getY(),status.getWidth(),status.getHeight());
					status.setText("Cracking, Please wait...");
					status.update(status.getGraphics());
					getRootPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					md=MessageDigest.getInstance("SHA-1");
					if((!bruteforce&&wordsList.isEmpty()&&!swapUsr)&&!hashesList.isEmpty())
					{
						status.setText("Please select word lists");
					}
					else if(hashesList.isEmpty()&&!wordsList.isEmpty())
					{
						status.setText("Please input the hash or select hash lists");
					}
					else if(hashesList.isEmpty()&&wordsList.isEmpty())
					{
						status.setText("Please input the hash or select hash lists"+(bruteforce?"":"and the word lists"));
					}
					else if((!bruteforce) && (!swapUsr))	
					{
						String hex="";
						String word="";
						DataInputStream f=null;
						boolean flag=false;
						int fsize=0,rsize=0;
						for(int j=0;j<hashesList.size();j++)
						{
							for(int i=0;i<wordsList.size();i++)
							{
								f=new DataInputStream(new FileInputStream(wordsList.get(i).toString()));
								fsize=0;
								while(f.available()>0)
								{
									f.readLine();
									fsize++;
								}
								rsize=0;
								f.close();
								f=new DataInputStream(new FileInputStream(wordsList.get(i).toString()));
								while(f.available()>0) 
								{
									word=f.readLine();
									buffer=word.getBytes();
								    md.reset();
								    md.update(buffer);
							    	digest=md.digest();
							    	hex="";
							    	for (int k = 0; k < digest.length; k++)
									{
										int b = digest[k] & 0xff;
										if (Integer.toHexString(b).length() == 1) hex = hex + "0";
										hex  = hex + Integer.toHexString(b);
									}
									if(hex.equals(hashesList.get(j).toString()))
									{
										crackedList.add(word);
										flag=true;
										break;
									}
									statusProgressBar.setValue( ((++rsize) * 100)/fsize);
									
								}
							}
							if(flag==false)
							{
								status.setText("The word for the hash "+hashesList.get(j).toString()+" doesn't exist in the selected word lists");
								word="Couldn't crack "+hashesList.get(j).toString();
								crackedList.add(word);
							}
						}
						f.close();
						crackedResultList.setListData(crackedList.toArray());
					}

					else if((minLen.getText().equals("")||maxLen.getText().equals(""))&& !swapUsr) 
					{
						status.setText("Please enter the min & max word length range");
					}
					else if((bruteforce) && (!swapUsr))
					{
						int min=Integer.parseInt(minLen.getText());
						int max=Integer.parseInt(maxLen.getText());
						int i=min;
						String word="";
						boolean flag=false;
						for(int j=0;j<hashesList.size();j++)
						{
							i=min;
							while(i<=max)
							{
								if(strength.getSelectedIndex()==0)
									word=bruteCrackAlpha(i,hashesList.get(j).toString());
								else if(strength.getSelectedIndex()==1)
									word=bruteCrackAlphaNum(i,hashesList.get(j).toString());
								else
									word=bruteCrackAlphaNumSymbols(i,hashesList.get(j).toString());
								if(word==null)
								{
									flag=false;
								}
								else
								{
									status.setText("Cracked!");
									crackedList.add(word);
									flag=true;
									break;
								}
								i++;
							}
							if(flag==false)
							{
								status.setText("The word for the hash "+hashesList.get(j).toString()+" doesn't exist in the given range");
								word="Couldn't Hash this one...";
								crackedList.add(word);
							}
						}
						crackedResultList.setListData(crackedList.toArray());
					}
                                        else
                                        {
                                            boolean flag=false;
                                            String word;
                                            for(int j=0;j<hashesList.size();j++)
                                            {
                                                flag=false;
                                                for(int i=0;i<usersList.size();i++)
                                                {
                                                    while(!flag)
                                                    {
                                                        word= shuffle((usersList.get(i)).toString(),(hashesList.get(j)).toString());
                                                        if(word==null)
                                                            flag=false;
                                                        else
                                                        {
                                                            status.setText("Cracked!");
                                                            crackedList.add(word);
                                                            flag=true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if(flag==false)
                                                {
                                                    status.setText("The word for the hash " + hashesList.get(j).toString() + " doesn't exist in the given range");
                                                    word="Couldn't Hash this one...";
                                                    crackedList.add(word);
                                                }
                                            }
                                            crackedResultList.setListData(crackedList.toArray());
					}

					setMyMenuBarEnabled();
					setMyContextMenuEnabled();
					
				} 
				catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ThreadDeath e) {
					e.printStackTrace();
				}
				startCracking.getGraphics().clearRect(status.getX(),status.getY(),status.getWidth(),status.getHeight());
				startCracking.setText("Start");
				startCracking.update(startCracking.getGraphics());
				getRootPane().setCursor(Cursor.getDefaultCursor());
				statusProgressBar.setValue(100);
				stopCracking.setVisible(false);
				
			}
		};

		try {
			crackThread.setDaemon(true);
			crackThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setExtendedState(JFrame.NORMAL);
		toFront();
	}

	
	private void setWordListContextMenu()
	{
		JPopupMenu menu=new JPopupMenu();
		JMenuItem item=new JMenuItem("Delete");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				deleteHash();
			}
		});
		menu.add(item);
		item=new JMenuItem("Select dictionary file");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				getHashFile();
			}
		});
		menu.add(item);
		wordList.setComponentPopupMenu(menu);
	}

	
	

	
	private void setMyContextMenuEnabled()
	{
		JMenuItem item=(JMenuItem)popupMenu.getSubElements()[2];
		item.setEnabled(!hashesList.isEmpty()||!wordsList.isEmpty());
		item=(JMenuItem)popupMenu.getSubElements()[3];
		item.setEnabled(!crackedList.isEmpty());
		item=(JMenuItem)popupMenu.getSubElements()[5];
		item.setEnabled(!bruteforce);
	}

	private void setMyContextMenu()
	{
		popupMenu.setCursor(Cursor.getDefaultCursor());
		JMenuItem item=new JMenuItem("Start");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				startHashCracking();
			}
		});
		popupMenu.add(item);
		item=new JMenuItem("Stop");
		item.setEnabled(false);
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				stopHashCracking();
			}
		});
		popupMenu.add(item);
		popupMenu.addSeparator();

		item = new JMenuItem("Clear");
		item.setEnabled(false);
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				clear();
			}
		});
		popupMenu.add(item);

		
		//popupMenu.add(item);
		popupMenu.addSeparator();

                JMenu subMenu=new JMenu("Username");
		item=new JMenuItem("Add username");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getUsername();
			}
		});
		subMenu.add(item);
                popupMenu.add(subMenu);

		subMenu=new JMenu("Hash");
		item=new JMenuItem("Add hash");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHash();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Delete hash");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteHash();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Select hash file");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHashFile();
			}
		});
		subMenu.add(item);
		popupMenu.add(subMenu);

		subMenu=new JMenu("Dictionary");
		item=new JMenuItem("Delete dictionary");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteWordList();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Select dictionary file");
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getWordListFile();
			}
		});
		subMenu.add(item);
		popupMenu.add(subMenu);
		popupMenu.addSeparator();

		item = new JMenuItem("Exit");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae)
			{
				exit();
			}
		});
		popupMenu.add(item);

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) { checkPopup(e); }
			public void mouseClicked(MouseEvent e) { checkPopup(e); }
			public void mouseReleased(MouseEvent e) { checkPopup(e); }
			private void checkPopup(MouseEvent e) {
				setMyMenuBarEnabled();
				if (e.isPopupTrigger(  )) {
					setMyMenuBarEnabled();
					setMyContextMenuEnabled();
					
					popupMenu.show(e.getComponent(  ), e.getX(  ), e.getY(  ));
				}
		  	}
		});
	}

	private void setMyMenuBarEnabled()
	{
		JMenuItem item=(JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[0]; 
		item.setEnabled(!wordsList.isEmpty()||!hashesList.isEmpty());
		item=(JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[1];
		item.setEnabled(!crackedList.isEmpty());
		item=(JMenuItem)menuBar.getSubElements()[0].getSubElements()[0].getSubElements()[3];
		item.setEnabled(!bruteforce);
	}

	private void setMyMenuBar()
	{
		menuBar.setCursor(Cursor.getDefaultCursor());
		JMenu menu=new JMenu("File");
		menu.setMnemonic('F');
		JMenuItem item=new JMenuItem("Clear");
		item.setMnemonic('C');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				clear();
			}
		});
		menu.add(item);
		
		JMenu subMenu=new JMenu("Hash");
		subMenu.setMnemonic('H');
		item=new JMenuItem("Add hash");
		item.setMnemonic('A');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHash();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Delete hash");
		item.setMnemonic('D');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteHash();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Select hash file");
		item.setMnemonic('S');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHashFile();
			}
		});
		subMenu.add(item);
		menu.add(subMenu);
		subMenu=new JMenu("Dictionary");
		subMenu.setMnemonic('W');
		item=new JMenuItem("Delete Dictionary");
		item.setMnemonic('D');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteWordList();
			}
		});
		subMenu.add(item);
		item=new JMenuItem("Select dictionary file");
		item.setMnemonic('S');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getWordListFile();
			}
		});
		subMenu.add(item);
		menu.add(subMenu);
		menu.addSeparator();
		item=new JMenuItem("Start");
		item.setMnemonic('t');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				startHashCracking();
			}
		});
		menu.add(item);
		item=new JMenuItem("Stop");
		item.setMnemonic('o');
		item.setEnabled(false);
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				stopHashCracking();
			}
		});
		menu.add(item);
		menu.addSeparator();
		item=new JMenuItem("Exit");
		item.setMnemonic('X');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				exit();
			}
		});
		menu.add(item);
		menuBar.add(menu);

		setJMenuBar(menuBar);
	}

	
	private void setCenterPanel()
	{
		JPanel center = new JPanel();
		center.setBorder(BorderFactory.createEmptyBorder(1,0,0,0));

		JPanel hashes = new JPanel();
		hashes.setLayout(new BoxLayout(hashes,BoxLayout.Y_AXIS));
		hashes.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,0,0),"Hash", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Calibri", Font.BOLD, 10), Color.BLACK));

		JPanel hashbtns=new JPanel();
		hashbtns.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
		hashList = new JList();
		hashList.setToolTipText("Hashes");
		hashList.setVisibleRowCount(10);
		hashList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
				JPanel cell=new JPanel();
				cell.setLayout(new BorderLayout());
				JLabel count=new JLabel((arg2+1)+". ");
				JLabel text=new JLabel(arg1.toString());
				cell.setToolTipText(arg1.toString());
				cell.setBackground(arg3?new Color(170,190,210):Color.white);
				cell.add(count, BorderLayout.WEST);
				cell.add(text,BorderLayout.CENTER);
				return cell;
			}
		});
		hashList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_DELETE)
				{
					deleteHash();
				}
				
			}
			public void keyReleased(KeyEvent arg0) {
			}
			public void keyTyped(KeyEvent arg0) {

			}
		});
		
		hashScrollList=new JScrollPane(hashList);
		hashScrollList.setPreferredSize(new Dimension(256,160));
		hashbtns.add(hashScrollList);

		

		JPanel btns=new JPanel();
		btns.setLayout(new BoxLayout(btns,BoxLayout.X_AXIS));
                user = new JButton("Add Name");
		user.setMargin(new Insets(0,0,0,0));
		user.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getUsername();
			}
		});
		btns.add(user);
		addHash = new JButton("Add Hashes");
		addHash.setMargin(new Insets(0,0,0,0));
		addHash.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHash();
			}
		});
		btns.add(addHash);
		JButton delete=new JButton("Delete Hash");
		delete.setMargin(new Insets(0,0,0,0));
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteHash();
			}
		});
		btns.add(delete);
		selectHashFile=new JButton("Select Hashes File");
		selectHashFile.setMargin(new Insets(0,0,0,0));
		selectHashFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getHashFile();
			}
		});
		btns.add(selectHashFile);

		hashes.add(btns);
		center.add(hashes);

		final JPanel cracking = new JPanel();
		cracking.setLayout(new BoxLayout(cracking, BoxLayout.Y_AXIS));

		final JPanel wordListPanel=new JPanel();
		wordListPanel.setLayout(new BoxLayout(wordListPanel, BoxLayout.Y_AXIS));
		wordListPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0,0,1,0),"Word Lists", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Arial", Font.BOLD, 10), Color.BLACK));
		wordListPanel.setVisible(!bruteforce);
		JPanel wordbtns=new JPanel();
		wordList=new JList();
		wordList.setVisibleRowCount(8);
		wordList.setCellRenderer(new ListCellRenderer() {
			public Component getListCellRendererComponent(JList arg0, Object arg1, int arg2, boolean arg3, boolean arg4) {
				JPanel cell=new JPanel();
				cell.setLayout(new BorderLayout());
				JLabel count=new JLabel((arg2+1)+". ");
				JLabel text=new JLabel(arg1.toString().substring(arg1.toString().lastIndexOf("\\")+1));
				cell.setToolTipText(arg1.toString());
				cell.setBackground(arg3?new Color(170,190,210):Color.white);
				cell.add(count, BorderLayout.WEST);
				cell.add(text,BorderLayout.CENTER);
				return cell;
			}
		});
		wordList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode()==KeyEvent.VK_DELETE)
				{
					deleteWordList();
				}
				
			}
			public void keyReleased(KeyEvent arg0) {
			}
			public void keyTyped(KeyEvent arg0) {
			}
		});
		setWordListContextMenu();
		wordScrollList=new JScrollPane(wordList);
		wordScrollList.setPreferredSize(new Dimension(259,131));
		wordbtns.add(wordScrollList);
		
		wordListPanel.add(wordbtns);
		JPanel btn=new JPanel();
		btn.setLayout(new BoxLayout(btn,BoxLayout.X_AXIS));
		addWordListFile=new JButton("Select dictionary");
		addWordListFile.setMargin(new Insets(0,0,0,0));
		addWordListFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				getWordListFile();
			}
		});
		btn.add(addWordListFile);
		JButton del=new JButton("Delete");
		del.setMargin(new Insets(0,0,0,0));
		del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				deleteWordList();
			}
		});
		btn.add(del);
		wordListPanel.add(btn);

		final JPanel brutePanel=new JPanel();
		brutePanel.setLayout(new BoxLayout(brutePanel, BoxLayout.Y_AXIS));
		brutePanel.setVisible(bruteforce);
		JPanel sel=new JPanel();
		JLabel stn=new JLabel("Strength");
		sel.add(stn);

		strength.setPreferredSize(new Dimension(240,30));
		sel.add(strength);
		brutePanel.add(sel);

		
		JPanel minmax=new JPanel();
		minmax.setLayout(new BoxLayout(minmax,BoxLayout.Y_AXIS));
		JPanel min=new JPanel();
		min.setLayout(new BoxLayout(min,BoxLayout.X_AXIS));
		min.setBorder(BorderFactory.createEmptyBorder(5,50,5,30));
		JLabel mn=new JLabel("Minimum Word Length");
		mn.setBorder(BorderFactory.createEmptyBorder(1,1,1,45));
		min.add(mn);
		minLen.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
			public void keyTyped(KeyEvent arg0) {
				if(minLen.getText().length()>2) arg0.consume();
				String chars="0123456789";
				char arg=arg0.getKeyChar();
				int i=0;
				for(i=0;i<chars.length();i++)
				{
					if(chars.charAt(i)==arg)break;
				}
				if(i==chars.length()) arg0.consume();
			}
		});
		min.add(minLen);

		minmax.add(min);
		JPanel max=new JPanel();
		max.setLayout(new BoxLayout(max,BoxLayout.X_AXIS));
		max.setBorder(BorderFactory.createEmptyBorder(5,50,5,30));
		JLabel mx=new JLabel("Maximum Word Length");
		mx.setBorder(BorderFactory.createEmptyBorder(1,1,1,40));
		max.add(mx);
		maxLen.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent arg0) {
			}
			public void keyTyped(KeyEvent arg0) {
				if(maxLen.getText().length()>2) arg0.consume();
				String chars="0123456789";
				char arg=arg0.getKeyChar();
				int i=0;
				for(i=0;i<chars.length();i++)
				{
					if(chars.charAt(i)==arg)break;
				}
				if(i==chars.length()) arg0.consume();
			}
		});
		max.add(maxLen);
		minmax.add(max);
		brutePanel.add(minmax);

		JPanel a=new JPanel();
		a.setLayout(new BoxLayout(a,BoxLayout.X_AXIS));
		a.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
		bruteOrList=new ButtonGroup();
		final JRadioButton brute=new JRadioButton("Brute Force");
		brute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				if(brute.isSelected())
                                {
					bruteforce=true;
                                        swapUsr=false;
                                }
				wordListPanel.setVisible(!bruteforce);
				brutePanel.setVisible(bruteforce);
				setMyMenuBarEnabled();
				setMyContextMenuEnabled();
				
			}
		});
		bruteOrList.add(brute);
		a.add(brute);
		final JRadioButton list=new JRadioButton("Word List",true);
		list.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				if(list.isSelected())
                                {
                                    bruteforce = false;
                                    swapUsr=false;
                                 }
				wordListPanel.setVisible(!bruteforce);
				brutePanel.setVisible(bruteforce);
				setMyMenuBarEnabled();
				setMyContextMenuEnabled();
				
			}
		});
                bruteOrList.add(list);
                a.add(list);
                final JRadioButton swap=new JRadioButton("Shuffle",true);
		swap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae)
			{
				if(swap.isSelected())
                                {
                                    swapUsr = true;
                                    bruteforce=false;
                                }
				wordListPanel.setVisible(false);
				brutePanel.setVisible(false);
				setMyMenuBarEnabled();
				setMyContextMenuEnabled();
				
			}
		});
		bruteOrList.add(swap);
		a.add(swap);
		cracking.add(a);
		cracking.add(brutePanel);
		cracking.add(wordListPanel);
		center.add(cracking);
		contentPane.add(center);
	}

	
	private void deleteHash()
	{
		if(hashList.isSelectionEmpty())
			return;
		hashesList.remove(hashList.getSelectedIndex());
		hashList.setListData(hashesList.toArray());
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
	}

	
	private void deleteWordList()
	{
		if(wordList.isSelectionEmpty())
			return;
		wordsList.remove(wordList.getSelectedIndex());
		wordList.setListData(wordsList.toArray());
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
		
	}

        private void clear()
	{
		crackedList.clear();
		crackedResultList.setListData(crackedList.toArray());
		hashesList.clear();
		hashList.setListData(hashesList.toArray());
		wordsList.clear();
		wordList.setListData(wordsList.toArray());
                usersList.clear();
                userList.setListData(usersList.toArray());
		setMyMenuBarEnabled();
		setMyContextMenuEnabled();
	}
		
	private void exit()
	{
		int option=JOptionPane.showConfirmDialog(me,"Are you sure you want to exit?","Confirmation",JOptionPane.OK_CANCEL_OPTION);
		if(option==JOptionPane.OK_OPTION)
		{	dispose();System.exit(0); }
		else
			status.setText("Cancelled Exit");
	}

	
	private void showSplash(final PwdPRO hc)
	{
		final JWindow splashWin=new JWindow();
		splashWin.setAlwaysOnTop(true);
		splashWin.setSize(280,160);
		splashWin.setLocationRelativeTo(null);

		final JPanel splashPanel=new JPanel();
		splashPanel.setSize(250,130);
		splashPanel.setLayout(new BorderLayout());
		splashPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		final JPanel splash=new JPanel();
		splash.setLayout(new GridBagLayout());

		GridBagConstraints gbc=new GridBagConstraints();

		gbc.gridx=0;gbc.gridy=0;
		JLabel iconLabel=new JLabel("",SwingConstants.CENTER);
		iconLabel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
		iconLabel.setHorizontalTextPosition(JLabel.CENTER);
		iconLabel.setVerticalTextPosition(JLabel.CENTER);
		splash.add(iconLabel,gbc);

		gbc.gridx++;
		JLabel h=new JLabel("PwdPRO by : Binary 01");
		splash.add(h,gbc);

		gbc.gridx=0;gbc.gridy++;
		final JTextField text=new JTextField("Loading",7);
		text.setEditable(false);
		text.setBorder(BorderFactory.createEmptyBorder());
		text.setFont(new Font("Calibri",Font.BOLD,12));
		splash.add(text,gbc);

		gbc.gridx++;
		final JProgressBar pb=new JProgressBar();
		pb.setMinimum(0);
		pb.setMaximum(100);
		pb.setSize(110,10);
		splash.add(pb,gbc);
		splashPanel.add(splash, BorderLayout.CENTER);



		splashTimer = new Timer(500,new ActionListener() {
			int count=0;
			int progress=0;
			long start = System.currentTimeMillis(),end=0;
			public void actionPerformed(ActionEvent e)
			{
				pb.setValue(progress);
				text.setText(text.getText()+".");
				count++;
				if(count==4)
				{
					text.setText("Loading");
					count=0;
				}
				end = System.currentTimeMillis();
				progress+=(end-start)/400;
				if(end-start > 6000)
				{
					hc.setVisible(true);
					hc.setEnabled(false);
				}
				if(end-start > 9000)
				{
					endTimer();
					splashWin.dispose();
					hc.setEnabled(true);
				}
			}
		});
		splashTimer.start();
		splashWin.getContentPane().add(splashPanel);
		splashWin.setVisible(true);
	}

	private static void endTimer()
	{
		splashTimer.stop();
	}

	private String bruteCrackAlpha(int size, String hexVal) throws NoSuchAlgorithmException
	{
		int h=0,start=0,end=(int)Math.pow(26, size);
		String word="",out="",hex="";
		for(int i=0;i<size;i++)
			word+="a";
		byte w[]=word.getBytes(),o[]=word.getBytes();
		byte j[]=new byte[size],buffer[]=new byte[1024],digest[]=new byte[1024];
		md=MessageDigest.getInstance("SHA-1");
		while(h<w.length)
		{
			for(int l=0;l<w.length;l++)
				o[l]=(byte)(w[l]+j[l]);
			j[w.length-1]++;
			h=0;
			for(int l=0;l<w.length;l++)
				h=(o[l]=='z')?h+1:h-1;
			for(int l=1;l<=w.length;l++)
			{
				if(j[w.length-l]==26)
				{
					j[w.length-l]=0;
					if(l<w.length)	j[w.length-l-1]+=1;
				}
			}
			out="";
			for(int l=0;l<w.length;l++)
				out=out+(char)o[l];

			buffer=out.getBytes();
			md.update(buffer);
			digest=md.digest();
			hex="";
			for (int i = 0; i < digest.length; i++)
			{
				int b = digest[i] & 0xff;
				if (Integer.toHexString(b).length() == 1) hex = hex + "0";
				hex  = hex + Integer.toHexString(b);
			}
			statusProgressBar.setValue(((++start)*100)/end);
			statusProgressBar.setString(out);
			if(hexVal.equals(hex))
				return out;
		}
		return null;
	}

	private String bruteCrackAlphaNum(int size, String hexVal) throws NoSuchAlgorithmException
	{
		int h=0,start=0,end=(int)Math.pow(26, size);
		String word="",out="",hex="";
		for(int i=0;i<size;i++)
			word+="a";
		byte w[]=word.getBytes(),o[]=word.getBytes();
		byte j[]=new byte[size],buffer[]=new byte[1024],digest[]=new byte[1024];
		md=MessageDigest.getInstance("SHA-1");
		while(h<w.length)
		{
			statusProgressBar.setValue(0);
			for(int l=0;l<w.length;l++)
				o[l]=(byte)(w[l]+j[l]);
			j[w.length-1]++;
			h=0;
			for(int l=0;l<w.length;l++)
				h=(o[l]=='9')?h+1:h-1;
			for(int l=1;l<=w.length;l++)
			{
				if(j[w.length-l]==26)
					j[w.length-l]=-49;
				else if(j[w.length-l]==-39)
				{
					j[w.length-l]=0;
					if(l<w.length)	j[w.length-l-1]+=1;
				}
			}
			out="";
			for(int l=0;l<w.length;l++)
				out=out+(char)o[l];

			buffer=out.getBytes();
			md.update(buffer);
			digest=md.digest();
			hex="";
			for (int i = 0; i < digest.length; i++)
			{
				int b = digest[i] & 0xff;
				if (Integer.toHexString(b).length() == 1) hex = hex + "0";
				hex  = hex + Integer.toHexString(b);
			}
			statusProgressBar.setValue(((++start)*100)/end);
			statusProgressBar.setString(out);
			if(hexVal.equals(hex))
				return out;
		}
		return null;
	}

	private String bruteCrackAlphaNumSymbols(int size, String hexVal) throws NoSuchAlgorithmException
	{
		int h=0,start=0,end=(int)Math.pow(26, size);
		String word="",out="",hex="";
		for(int i=0;i<size;i++)
			word+="[";
		byte w[]=word.getBytes(),o[]=word.getBytes();
		byte j[]=new byte[size],buffer[]=new byte[1024],digest[]=new byte[1024];
		md=MessageDigest.getInstance("SHA-1");
		while(h<w.length)
		{
			statusProgressBar.setValue(0);
			for(int l=0;l<w.length;l++)
				o[l]=(byte)(w[l]+j[l]);
			j[w.length-1]++;
			h=0;
			for(int l=0;l<w.length;l++)
				h=(o[l]=='@')?h+1:h-1;
			for(int l=1;l<=w.length;l++)
			{
				if(j[w.length-l]==36)
					j[w.length-l]=-58;
				else if(j[w.length-l]==-26)
				{
					j[w.length-l]=0;
					if(l<w.length)	j[w.length-l-1]+=1;
				}
			}
			out="";
			for(int l=0;l<w.length;l++)
				out=out+(char)o[l];

			buffer=out.getBytes();
			md.update(buffer);
			digest=md.digest();
			hex="";
			for (int i = 0; i < digest.length; i++)
			{
				int b = digest[i] & 0xff;
				if (Integer.toHexString(b).length() == 1) hex = hex + "0";
				hex  = hex + Integer.toHexString(b);
			}
			statusProgressBar.setValue(((++start)*100)/end);
			statusProgressBar.setString(out);
			if(hexVal.equals(hex))
				return out;
		}
		return null;
	}

        private String shuffle(String input,String hexVal)throws NoSuchAlgorithmException{
            ArrayList<Character> characters = new ArrayList<Character>();

            String hex="";
            byte[] as=new byte[8192];
            for(char c:input.toCharArray()){
                characters.add(c);
            }
            StringBuilder output = new StringBuilder(input.length());
            while(characters.size()!=0){
                int randPicker = (int)(Math.random()*characters.size());
                output.append(characters.remove(randPicker));
            }
            String asdf=output.toString();
            md=MessageDigest.getInstance("SHA-1");
            as=asdf.getBytes();
            md.reset();
            md.update(as);
            digest=md.digest();
            for (int i = 0; i < digest.length; i++)
            {
                int b = digest[i] & 0xff;
		if (Integer.toHexString(b).length() == 1) hex = hex + "0";
                    hex  = hex + Integer.toHexString(b);
            }
            statusProgressBar.setString(asdf);
            if(hexVal.equals(hex))
		return input;

           return null;
        }



	public static void main(String[] args) {
		new PwdPRO();
	}
}