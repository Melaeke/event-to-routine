package com.hispet;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;

public class gui {

	private JFrame frame;
	private JTextField inputFileNameTextField;
	private JTextField outputFileNameTextField;

	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	static double totalWidth;
	static double totalHeight;

	static double aspectRatio = 16.0 / 9.0; // aspect ratio should be 4 by 3 so preserve the maximum in the two. aspect
											// ratio is height/ width.

	private static double quantizedPartsx = 30;// this is like a grid system where the sizes are grided into 30 parts.
	private static double oneGridx; // one quantized grid in the x axis.

	private static double quantizedPartsy = 30;
	private static double oneGridy;

	private static int buttonWidthInGrid = 5;
	private static int textFieldWidthInGrid = 20;
	private static int labelWidthInGrid = 10;

	/**
	 * These values are offsets for all the data we are going to use.
	 */
	int firstOffsetX = 10;
	int firstOffsetY = 10;

	int heightTextFields = 20;
	int heightLabels = 20;

	int spaceBetweenComponentsX = 10;
	int spaceBetweenComponentsY = 10;

	private static void initializeSizes(double maxWidth, double maxHeight) {
		totalWidth = maxWidth * 0.2;
		totalHeight = maxHeight * 0.2;

		// preserve aspect ratio using the minimum value. aspect ratio is width /
		// height.
		if (totalWidth > aspectRatio * totalHeight) {
			// height is small so decrease the width to accomodate the limitation of height.
			totalWidth = aspectRatio * totalHeight;
		} else {
			// width is small so preserve the aspect ratio by decreasing the width.
			totalHeight = totalWidth / aspectRatio;
		}

		oneGridx = totalWidth / quantizedPartsx;
		oneGridy = totalHeight / quantizedPartsy;

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		initializeSizes(screenSize.getWidth(), screenSize.getHeight());
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		int numberOfYGridsTaken = 0;
		frame = new JFrame();

		frame.setBounds(10, 10, (int) totalWidth, (int) totalHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		/**
		 * input file area
		 */
		JLabel inputFileLabel = new JLabel("Input File");
		inputFileLabel.setBounds((int) oneGridx, (int) oneGridy, (int) (labelWidthInGrid * oneGridx), (int) oneGridy);
		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.
		frame.getContentPane().add(inputFileLabel);

		JButton browseInputFileButton = new JButton("Browse");
		browseInputFileButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(browseInputFileButton);

		inputFileNameTextField = new JTextField();
		inputFileNameTextField.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (textFieldWidthInGrid * oneGridx), (int) (2 * oneGridy));
		frame.getContentPane().add(inputFileNameTextField);
		inputFileNameTextField.setColumns(10);

		numberOfYGridsTaken += 3;

		// add separator here.
		JSeparator separator = new JSeparator();
		separator.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 2) * oneGridx), 1);
		frame.add(separator);

		numberOfYGridsTaken += 1;

		/**
		 * output file area
		 */
		JLabel outputFileLabel = new JLabel("Output File");
		outputFileLabel.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (labelWidthInGrid * oneGridx), (int) (oneGridy));
		frame.getContentPane().add(outputFileLabel);

		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.

		outputFileNameTextField = new JTextField();
		outputFileNameTextField.setBounds((int) (oneGridx), (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (textFieldWidthInGrid * oneGridx), (int) (2 * oneGridy));
		frame.getContentPane().add(outputFileNameTextField);
		outputFileNameTextField.setColumns(10);

		JButton browseOutputFileButton = new JButton("Browse");
		browseOutputFileButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(browseOutputFileButton);

		numberOfYGridsTaken += 3;

		// add separator here.
		JSeparator separator2 = new JSeparator();
		separator2.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 2) * oneGridx), 1);
		frame.add(separator2);

		numberOfYGridsTaken += 1;

		/**
		 * Status area
		 */
		JLabel statusFileLabel = new JLabel("Status");
		statusFileLabel.setBounds((int) (((quantizedPartsx / 2) - 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (labelWidthInGrid * oneGridx), (int) (oneGridy));
		frame.getContentPane().add(statusFileLabel);

		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.

		System.out.println(numberOfYGridsTaken);
		JTextArea textArea = new JTextArea("Browse the file you want to convert \nAnd press start");
		textArea.setBounds((int) (2 * oneGridx), (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 4) * oneGridx),
				(int) ((quantizedPartsy - numberOfYGridsTaken - 5) * oneGridy));
		frame.getContentPane().add(textArea);

		numberOfYGridsTaken += quantizedPartsy - numberOfYGridsTaken - 5 + 1;
		JButton startButton = new JButton("Start");
		startButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(startButton);

	}
}
