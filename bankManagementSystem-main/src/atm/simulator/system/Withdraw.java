package atm.simulator.system;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Withdraw  extends JFrame implements ActionListener{
	
	JButton withdraw1, withdraw2, back;
	String ssn, pin;
	JTextField amountTF;
	JLabel text, label;
	JPasswordField pinTF;
	
	public Withdraw(String ssn, String pin) {
		
		setLayout(null);
		
		this.ssn = ssn;
		this.pin = pin;
		
		ImageIcon img1 = new ImageIcon(ClassLoader.getSystemResource("icons/atm.jpg"));
		Image img2 = img1.getImage().getScaledInstance(900, 915, Image.SCALE_DEFAULT);
		img1 =  new ImageIcon(img2);
		label = new JLabel(img1);
		label.setBounds(0, 0, 900, 850);
		add(label);
		
		text = new JLabel("Please enter amount to Withdraw:");
		text.setBounds(187, 290, 700, 35);
		text.setFont(new Font("System", Font.BOLD, 18));
		text.setForeground(Color.GREEN);
		label.add(text);
		
		amountTF = new JTextField();
		amountTF.setBounds(175, 350, 320, 25);
		amountTF.setFont(new Font("Raleway", Font.BOLD, 22));
		label.add(amountTF);
		
		pinTF = new JPasswordField();
		pinTF.setBounds(200, 350, 220, 25);
		pinTF.setFont(new Font("Raleway", Font.BOLD, 22));
		label.add(pinTF);
		
		withdraw1 = new JButton("CONTINUE");
		withdraw1.setBounds(355, 465, 150, 24);
		withdraw1.addActionListener(this);
		label.add(withdraw1);
		
		withdraw2 = new JButton("WITHDRAW");
		withdraw2.setBounds(355, 465, 150, 24);
		withdraw2.addActionListener(this);
		withdraw2.hide();
		label.add(withdraw2);
		
		back = new JButton("MAIN MENU");
		back.setBounds(355, 500, 150, 24);
		back.addActionListener(this);
		label.add(back);
		
		setSize(900, 900);
		setLocation(300, 0);
		setUndecorated(true);
		setVisible(true);
		
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		String amount = "";
		if (ae.getSource() == withdraw1) {
			
			amount = amountTF.getText();
			if(amount.equals("")) 
			{
				JOptionPane pane = new JOptionPane("Enter amount to withdraw!");
				JDialog d = pane.createDialog((JFrame)null, "Message");
				d.setLocation(500,300);
			    d.setVisible(true);

				amountTF.requestFocus();
				return;
			}
			else
			{				
				text.setText("Enter PIN:");
				amountTF.hide();
				pinTF.show();
				withdraw1.hide();
				withdraw2.show();
			}
			
		}
		else if (ae.getSource() == withdraw2) {
			String testpin = pinTF.getText();
			if(testpin.equals(pin)) {
				try {
					Float balance;
					amount = amountTF.getText();
					Conn conn = new Conn();
					String query = "SELECT balance FROM Accounts WHERE ssn = '" +ssn+ "'";
					ResultSet result = conn.s.executeQuery(query);
					if(result.next()) {
						balance = Float.parseFloat(result.getString(1));
						if(balance - Float.parseFloat(amount) >= 0) {
							balance -= Float.parseFloat(amount);
							query = "UPDATE Accounts SET balance = '"+balance.toString()+"' WHERE ssn = '" +ssn+ "'";
							conn.s.executeUpdate(query);
							Date date = new Date();
							query = "INSERT INTO Transactions(ssn, amount, type, date) VALUES ('" + ssn + "', '" +amount+ "', 'Withdraw', '" + date + "')";
							conn.s.executeUpdate(query);
							conn.s.close();
							
							JOptionPane pane = new JOptionPane(amount + "$ successfully withdrawn from your account!");
							JDialog d = pane.createDialog((JFrame)null, "Message");
							d.setLocation(500,300);
						    d.setVisible(true);
							
							setVisible(false);
							new Transactions(ssn, pin);	
						}
						else 
						{
							JOptionPane pane = new JOptionPane("Insufficient balance! Please try with a smaller amount!");
							JDialog d = pane.createDialog((JFrame)null, "Message");
							d.setLocation(500,300);
						    d.setVisible(true);
							
							text.setText("Please enter amount to withdraw:");
							amountTF.show();
							amountTF.setText("");
							pinTF.hide();
							withdraw1.show();
							withdraw2.hide();
						}
											
					}
					else 
					{
						JOptionPane pane = new JOptionPane("Unable to complete transaction at the moment. Please try again later!");
						JDialog d = pane.createDialog((JFrame)null, "Message");
						d.setLocation(500,300);
					    d.setVisible(true);
						
					    text.setText("Please enter amount to withdraw:");
						amountTF.show();
						amountTF.setText("");
						pinTF.hide();
						withdraw1.show();
						withdraw2.hide();
					}
				}
				catch(Exception e) {
					System.out.println(e);
				}
			}
			else 
			{	
				JOptionPane pane = new JOptionPane("Invalid PIN!");
				JDialog d = pane.createDialog((JFrame)null, "Message");
				d.setLocation(500,300);
			    d.setVisible(true);
				
				text.setText("Please enter amount to withdraw:");
				amountTF.show();
				amountTF.setText("");
				pinTF.hide();
				withdraw1.show();
				withdraw2.hide();
			}
		}
		else if (ae.getSource() == back) {
			setVisible(false);
			new Transactions(ssn, pin);
		}
		
	}
	
	public static void main(String[] args) {
		new Withdraw("1234567890", "2968");
	}

}
