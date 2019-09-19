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

	public static int currentRunningStatus = 1;

	public static int RUNNING_JSON_EXPORT_ONLY = 1;
	public static int RUNNING_JSON_AND_CSV_EXPORT = 2;
	public static int RUNNING_CSV_EXPORT_ONLY = 3;

	public static String databaseDumpFilesDirectory = "";

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
	public static JTextArea statusTextArea = new JTextArea("Browse the files you want to convert and press start");
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
			// If not resized, make the screen 50% of the total size.
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
			// This is when the user manually resizes the app. In this case, don't do any
			// resizing manually.
			totalWidth = maxWidth;
			totalHeight = maxHeight;
		}

	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		/*-
		 * In this program we mainly have 3 options.
		 * the options should be given as -s1 -s2 -s3 if no option is provided the system will
		 * automatically assume option 1 is chosen.
		 * 1) Change event data to routine and export to json only.
		 * 		This process is just exporting the event data and then import it to this app
		 * 		and this app will output a json file that can be imported to the main DHIS app.
		 * 		Note that the matching uses the 2 json files found in this package and no 
		 * 		additional input is required.
		 * 2) Change event data to routine and export to json and csv (database).
		 * 		This process is as process 1 but will also output a csv file that can be
		 * 		automatically inserted into the database of DHIS2 (datavalue table).
		 * 		For these feature to operate, we need to provide a folder with five files in it
		 * 		(categoryoptioncombo.csv ,dataelement.csv ,organisationunit.csv,period.csv)
		 * 		These files should be a direct export of the four tables in the DHIS instance that 
		 * 		we are going to import the output to. This folder should be provided from terminal 
		 * 		when opening this app using the argument -i/home/... Notice that there should be
		 * 		no space after -i.
		 * 3)	Change a dataValue Json export to csv(database).
		 * 		These option just changes a given datavalue file in json to a csv file that can be
		 * 		automatically imported to the database. as option 2 this option also requires an input
		 * 		folder containing the 4 files which are database dumps of the DHIS2 instance that we
		 * 		plan to insert the datavalues into.
		 */

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-s")) {
					switch (Integer.parseInt(args[i].substring(2, 3))) {
					case 1:
						currentRunningStatus = RUNNING_JSON_EXPORT_ONLY;
						break;
					case 2:
						currentRunningStatus = RUNNING_JSON_AND_CSV_EXPORT;
						break;
					case 3:
						currentRunningStatus = RUNNING_CSV_EXPORT_ONLY;
						break;
					default:
						// Show wrong switch selected message on terminal and exit.
						System.out.println("You have entered a wrong input for option '-s' only {1,2,3} are allowed");
						System.exit(-1);
					}
				}

				if (args[i].startsWith("-i")) {
					databaseDumpFilesDirectory = args[i].substring(2);
				}
			}
		} else {
			// If no argument is provided, default is JSON export only.
			currentRunningStatus = RUNNING_JSON_EXPORT_ONLY;
		}
		if ((currentRunningStatus == RUNNING_CSV_EXPORT_ONLY || currentRunningStatus == RUNNING_JSON_AND_CSV_EXPORT)
				&& databaseDumpFilesDirectory.equals("")) {
			// If dump file folder location is not provided and the task requires expects a
			// csv import,
			// display a message on terminal and exit.
			System.out.println("Please provide the directories for Database dump file with the flag \"-i/home/...\"");
			System.exit(-1);
		}

		// Initialize the gui zise.
		initializeSizes(screenSize.getWidth(), screenSize.getHeight(), false);

		// Create a new thread and use that thread for better performance and for UI not
		// to lag during processing of data.
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
					// add a window resize listener.
					window.frame.addComponentListener(new ComponentAdapter() {
						// add window resize listener so that the window should update its resize
						// automatically.
						public void componentResized(ComponentEvent event) {
							Rectangle size = event.getComponent().getBounds();

							// resize the window size not respecting the aspect ratio.
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
				inputFileChooser.addChoosableFileFilter(
						new FileNameExtensionFilter("All supported File Types: ZIP, JSON", "zip", "json"));
				inputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Zip compressed", "zip"));
				inputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JSON file", "json"));

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
						String fileName = outputFileChooser.getSelectedFile().toString();
						if (!fileName.endsWith("json")) {
							fileName = fileName + ".json";
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
							// file doesn't exist so no worries just pass
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

				// start doing the processing in background so that Process won't lag the UI.
				// And status message should be displayed with no lagging.
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
	
	static public void statusOfAllButtons(boolean status) {
		browseInputFileButton.setEnabled(status);
		browseOutputFileButton.setEnabled(status);
		startButton.setEnabled(status);
	}
}
