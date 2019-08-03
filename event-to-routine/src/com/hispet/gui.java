package com.hispet;

import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

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
	private JTextField inputFileNameTextField = new JTextField();
	private JTextField outputFileNameTextField = new JTextField();
	JLabel inputFileLabel = new JLabel("Input File");
	JButton browseInputFileButton = new JButton("Browse");
	JSeparator separator = new JSeparator();
	JLabel outputFileLabel = new JLabel("Output File");
	JButton browseOutputFileButton = new JButton("Browse");
	JSeparator separator2 = new JSeparator();
	JLabel statusFileLabel = new JLabel("Status");
	JTextArea textArea = new JTextArea("Browse the file you want to convert And press start");
	JButton startButton = new JButton("Start");

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

	private static void initializeSizes(double maxWidth, double maxHeight, boolean resized) {
		if (!resized) {
			// If not resized, make the screen 20% of the total size.
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
		} else {
			totalWidth = maxWidth;
			totalHeight = maxHeight;
		}

		oneGridx = totalWidth / quantizedPartsx;
		oneGridy = totalHeight / quantizedPartsy;

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		initializeSizes(screenSize.getWidth(), screenSize.getHeight(), false);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
					// add a window resize listener.
					window.frame.addComponentListener(new ComponentAdapter() {
						public void componentResized(ComponentEvent event) {
							// here reinitialize the window size.
							Rectangle size = event.getComponent().getBounds();
							System.out.println(size);
							initializeSizes(size.getWidth(), size.getHeight(), true);
							window.initialize();
						}
					});
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
		frame = new JFrame();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		int numberOfYGridsTaken = 0;

		frame.setBounds(10, 10, (int) totalWidth, (int) totalHeight);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		/**
		 * input file area
		 */

		inputFileLabel.setBounds((int) oneGridx, (int) oneGridy, (int) (labelWidthInGrid * oneGridx), (int) oneGridy);
		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.
		frame.getContentPane().add(inputFileLabel);

		browseInputFileButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(browseInputFileButton);

		inputFileNameTextField.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (textFieldWidthInGrid * oneGridx), (int) (2 * oneGridy));
		frame.getContentPane().add(inputFileNameTextField);
		inputFileNameTextField.setColumns(10);

		numberOfYGridsTaken += 3;

		// add separator here.

		separator.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 2) * oneGridx), 1);
		frame.add(separator);

		numberOfYGridsTaken += 1;

		/**
		 * output file area
		 */

		outputFileLabel.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (labelWidthInGrid * oneGridx), (int) (oneGridy));
		frame.getContentPane().add(outputFileLabel);

		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.

		outputFileNameTextField.setBounds((int) (oneGridx), (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) (textFieldWidthInGrid * oneGridx), (int) (2 * oneGridy));
		frame.getContentPane().add(outputFileNameTextField);
		outputFileNameTextField.setColumns(10);

		browseOutputFileButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(browseOutputFileButton);

		numberOfYGridsTaken += 3;

		// add separator here.

		separator2.setBounds((int) oneGridx, (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 2) * oneGridx), 1);
		frame.add(separator2);

		numberOfYGridsTaken += 1;

		/**
		 * Status area
		 */

		statusFileLabel.setBounds((int) (((quantizedPartsx / 2) - 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (labelWidthInGrid * oneGridx), (int) (oneGridy));
		frame.getContentPane().add(statusFileLabel);

		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.

		System.out.println(numberOfYGridsTaken);
		textArea.setBounds((int) (2 * oneGridx), (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 4) * oneGridx),
				(int) ((quantizedPartsy - numberOfYGridsTaken - 5) * oneGridy));
		frame.getContentPane().add(textArea);

		numberOfYGridsTaken += quantizedPartsy - numberOfYGridsTaken - 5 + 1;

		startButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(startButton);
	}
}
