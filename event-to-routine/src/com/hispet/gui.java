package com.hispet;

import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class gui {

	JFrame frame;
	public static JTextField inputFileNameTextField = new JTextField();
	public static JTextField outputFileNameTextField = new JTextField();
	public static JLabel inputFileLabel = new JLabel("Input File");
	public static JButton browseInputFileButton = new JButton("Browse");
	public static JSeparator separator = new JSeparator();
	public static JLabel outputFileLabel = new JLabel("Output File");
	public static JButton browseOutputFileButton = new JButton("Browse");
	public static JSeparator separator2 = new JSeparator();
	public static JLabel statusLabel = new JLabel("Status");
	public static JTextArea statusTextArea = new JTextArea("Browse the file you want to convert And press start");
	public static JButton startButton = new JButton("Start");
	public static JFileChooser inputFileChooser = new JFileChooser();
	public static JFileChooser outputFileChooser = new JFileChooser();
	public static JScrollPane scrollPane = new JScrollPane(statusTextArea);

	public static String inputFileName = "";
	public static String outputFileName = "";

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
			totalWidth = maxWidth * 0.5;
			totalHeight = maxHeight * 0.5;

			// preserve aspect ratio using the minimum value. aspect ratio is width /
			// height.
			if (totalWidth > aspectRatio * totalHeight) {
				// height is small so decrease the width to accommodate the limitation of
				// height.
				totalWidth = aspectRatio * totalHeight;
			} else {
				// width is small so preserve the aspect ratio by decreasing the width.
				totalHeight = totalWidth / aspectRatio;
			}
		} else {
			totalWidth = maxWidth;
			totalHeight = maxHeight;
		}

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
		addActionListeners();
	}

	private void addActionListeners() {
		/**
		 * Add action Listener for browse input file button.
		 */
		browseInputFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				inputFileChooser.setAcceptAllFileFilterUsed(false);
				inputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Zip compressed", "zip"));
				inputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file", "json"));
				inputFileChooser
						.addChoosableFileFilter(new FileNameExtensionFilter("All supported File Types", "zip", "json"));
				int returnVal = inputFileChooser.showOpenDialog((Component) event.getSource());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = inputFileChooser.getSelectedFile();
					inputFileName = inputFileChooser.getSelectedFile().toString();
					inputFileNameTextField.setText(inputFileName);
					try {
						// check if the application can open the file. If it can't show status message
						BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
						reader.close();
					} catch (Exception ex) {
						statusTextArea.append("\nProblem Accessing file \n" + file.toString() + " : " + ex);
						ex.printStackTrace();
					}
				} else {
					statusTextArea.append("\nFile access cancelled by User");
				}

			}
		});

		/**
		 * Add action listener for save File browser.
		 */
		browseOutputFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				outputFileChooser.setAcceptAllFileFilterUsed(false);
				outputFileChooser.setFileFilter(new FileNameExtensionFilter("JSON File", "json"));
				boolean acceptable = false;
				do {
					int returnVal = outputFileChooser.showSaveDialog((Component) event.getSource());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						String fileName=outputFileChooser.getSelectedFile().toString();
						if(!fileName.endsWith("json")) {
							fileName=fileName+".json";
						}
						File file = new File(fileName);
						if (file.exists()) {// if the file exists ask the user if the file can be replaced.
							int result = JOptionPane.showConfirmDialog((Component) event.getSource(),
									"The file exists, are you sure you want to overwrite?", "Overwrite?",
									JOptionPane.YES_NO_CANCEL_OPTION);
							if (result == JOptionPane.YES_OPTION) {
								// User doens't mind in replacing the file.
								acceptable = true;
								outputFileName = fileName;
								outputFileNameTextField.setText(outputFileName);
							} else {
								acceptable = false;
							}
						} else {
							// file doesn't exist so no wories just pass
							acceptable = true;
							outputFileName = fileName;
							outputFileNameTextField.setText(outputFileName);
						}
					} else {
						// User canceled browsing for output file name.
						statusTextArea.append("\nFile access cancelled by User");
						acceptable = true;
					}
				} while (!acceptable);
			}
		});

		/**
		 * Add action listener for start converting Button..
		 */
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				
				statusTextArea.append("\nStarted conversion...");
				
				new ProcessWorker(event).execute();
				
			}
		});
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
		 * This is useful speciallly for windows, because on Windows, a title bar and
		 * other borders are inserted which need to be subtracted for proper addition of
		 * items.
		 */
		Insets insets = frame.getInsets();

		oneGridx = (totalWidth - insets.left - insets.right) / quantizedPartsx;
		oneGridy = (totalHeight - insets.top - insets.bottom) / quantizedPartsy;

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
		statusLabel.setBounds((int) (((quantizedPartsx / 2) - 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (labelWidthInGrid * oneGridx), (int) (oneGridy));
		frame.getContentPane().add(statusLabel);

		numberOfYGridsTaken += 2;// one for offset from above and one for the height itself.

		System.out.println(numberOfYGridsTaken);
		scrollPane.setBounds((int) (2 * oneGridx), (int) ((numberOfYGridsTaken + 1) * oneGridy),
				(int) ((quantizedPartsx - 4) * oneGridx),
				(int) ((quantizedPartsy - numberOfYGridsTaken - 5) * oneGridy));
		
		frame.getContentPane().add(scrollPane);

		numberOfYGridsTaken += quantizedPartsy - numberOfYGridsTaken - 5 + 1;

		startButton.setBounds((int) ((1 + textFieldWidthInGrid + 1) * oneGridx),
				(int) ((numberOfYGridsTaken + 1) * oneGridy), (int) (buttonWidthInGrid * oneGridx),
				(int) (2 * oneGridy));
		frame.getContentPane().add(startButton);
	}
}
