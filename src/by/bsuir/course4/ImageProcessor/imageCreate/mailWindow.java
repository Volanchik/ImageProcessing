package by.bsuir.course4.ImageProcessor.imageCreate;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;

import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import java.awt.SystemColor;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class mailWindow {

	private JFrame frmStereogramCreator;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public mailWindow() {
		initialize();
		frmStereogramCreator.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmStereogramCreator = new JFrame();
		frmStereogramCreator.getContentPane().setBackground(new Color(255, 255, 255));
		frmStereogramCreator.setTitle("Stereogram creator");
		frmStereogramCreator.setBounds(100, 100, 453, 560);
		frmStereogramCreator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStereogramCreator.getContentPane().setLayout(null);
		
		JLabel lblDistanceBetweenEyes = new JLabel("Distance between eyes (mandatory)");
		lblDistanceBetweenEyes.setBounds(10, 14, 251, 14);
		frmStereogramCreator.getContentPane().add(lblDistanceBetweenEyes);
		
		textField_1 = new JTextField();
		textField_1.setBounds(313, 11, 111, 20);
		frmStereogramCreator.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("Create stereogram");
		btnNewButton.setForeground(new Color(0, 0, 0));
		btnNewButton.setBackground(new Color(0, 128, 128));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(10, 158, 414, 65);
		frmStereogramCreator.getContentPane().add(btnNewButton);
		
		textField_2 = new JTextField();
		textField_2.setBounds(313, 39, 111, 20);
		frmStereogramCreator.getContentPane().add(textField_2);
		textField_2.setColumns(10);
		
		JLabel lblNumberOfDepth = new JLabel("Number of depth map (mandatory)");
		lblNumberOfDepth.setBounds(10, 42, 293, 14);
		frmStereogramCreator.getContentPane().add(lblNumberOfDepth);
		
		JTextPane txtpnRulesForStereogram = new JTextPane();
		txtpnRulesForStereogram.setText("Rules for stereogram maker creation:\r\n1. You will see image if distance between eyes is less than pattern width.\r\n2. If you use custom pattern, check its width and then input distance between eyes.\r\n3. Optional, distance between eyes is less than 1,5 times of pattern width.\r\n4. Use pattern width at least 10 times less than width of depth map.\r\n\r\nTo choose files:\r\nroot folder consists of 3 subfolders:\r\n1. Stereogram - for result files.\r\n2. DepthMap - for input depth maps.\r\n3. Pattern - for input patterns.\r\n\r\nName conventions:\r\n1. All files are *.png.\r\n2. Names of input files are numbers. So, if you want to input file - put its number in appropriate field.\r\n\r\nIf you want random color generation for stereogram - pattern width field is mandatory");
		txtpnRulesForStereogram.setBounds(10, 234, 417, 276);
		frmStereogramCreator.getContentPane().add(txtpnRulesForStereogram);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.info);
		panel.setForeground(SystemColor.info);
		panel.setBounds(0, 67, 205, 80);
		frmStereogramCreator.getContentPane().add(panel);
		panel.setLayout(null);
		
		JCheckBox chckbxRandomPattern = new JCheckBox("random pattern");
		chckbxRandomPattern.setBounds(20, 27, 165, 23);
		chckbxRandomPattern.setBackground(SystemColor.info);
		panel.add(chckbxRandomPattern);
		
		JLabel lblPatternWidth = new JLabel("Pattern width:");
		lblPatternWidth.setBounds(20, 52, 119, 14);
		panel.add(lblPatternWidth);
		
		textField = new JTextField();
		textField.setBounds(139, 49, 46, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		JLabel lblRandomBackroundGeneration = new JLabel("Random backround");
		lblRandomBackroundGeneration.setHorizontalAlignment(SwingConstants.CENTER);
		lblRandomBackroundGeneration.setFont(new Font("Verdana", Font.PLAIN, 15));
		lblRandomBackroundGeneration.setBounds(0, 0, 195, 20);
		panel.add(lblRandomBackroundGeneration);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(240, 255, 240));
		panel_1.setBounds(204, 67, 233, 80);
		frmStereogramCreator.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblCustomPattern = new JLabel("Custom pattern");
		lblCustomPattern.setBounds(39, 0, 118, 20);
		lblCustomPattern.setFont(new Font("Verdana", Font.PLAIN, 15));
		panel_1.add(lblCustomPattern);
		
		textField_3 = new JTextField();
		textField_3.setBounds(59, 31, 86, 20);
		panel_1.add(textField_3);
		textField_3.setColumns(10);
		
		JLabel label = new JLabel("\u2116");
		label.setBounds(10, 34, 46, 14);
		panel_1.add(label);
		
		btnNewButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int patt_width = -1;
				int btw_eyes = (int)Integer.parseInt(textField_1.getText());
				int selected_pattern = -1;
				int selected_depthmap = (int)Integer.parseInt(textField_2.getText());
				
				if(chckbxRandomPattern.isSelected()){	
					
					patt_width = (int)Integer.parseInt(textField.getText());
					Stereogram stereo = new Stereogram(btw_eyes,patt_width);
					stereo.process(selected_depthmap);
				}
				else{
					selected_pattern = (int)Integer.parseInt(textField_3.getText());
					Stereogram stereo = new Stereogram(btw_eyes);
					stereo.process(selected_depthmap,selected_pattern);
				}
				
			}
		});
	}
}
