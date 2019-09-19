package com.hispet;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class ProcessWorker extends SwingWorker<Void, String> {
	ActionEvent event;

	public ProcessWorker(ActionEvent e) {
		event = e;
	}

	@Override
	protected Void doInBackground() throws Exception {
		gui.outputFileName = gui.outputFileNameTextField.getText().toString();
		gui.inputFileName = gui.inputFileNameTextField.getText().toString();

		// check if the fileNames are empty. if they are, show status message and exit.
		if (gui.outputFileName.equals("") || gui.inputFileName.equals("")) {
			publish("\nUser didn't provide both input and output files.");
			JOptionPane.showMessageDialog(null, "Please provide both input and output files.");
			return null;
		}

		// check if the fileNames are the same, if they are report back to the user..
		if (gui.outputFileName.equals(gui.inputFileName)) {
			publish("\nInput file and output file can not be the same.");
			JOptionPane.showMessageDialog(null, "Input and output files can not be the same.");
			return null;
		}
		// Re-check the output file. If it exists, ask the user again for
		// confirmation to overwrite.
		File outputFile = new File(gui.outputFileName);
		if (outputFile.exists()) {
			// file exists re-check with user for rewrite.
			int result = JOptionPane.showConfirmDialog((Component) event.getSource(),
					"File " + outputFile + " exists, are you sure you want to overwrite?", "Overwrite?",
					JOptionPane.YES_NO_OPTION);
			if (result != JOptionPane.YES_OPTION) {
				publish("\nConversion canceled by user.");
				return null;
			}
		}

		// If control reaches here you can write on the file.
		/**
		 * Start processing here.
		 */
		File newFile = null;
		if (gui.inputFileName.endsWith("zip")) {
			// the file is a zip file so first uncompress the file before processing.
			try {

				// The output dir to use as a temporary uncompressing location
				File outputDir = new File(outputFile.getParent());

				ZipInputStream zis = new ZipInputStream(new FileInputStream(gui.inputFileName));
				ZipEntry zipEntry = zis.getNextEntry();

				if (!zipEntry.getName().equals("metadata.json")) {
					// The file in the zip is not metadata.json so display error message.
					publish("\nERROR : The compressed file " + gui.inputFileName
							+ " should contain only one file named \"metadata.json\"");
					JOptionPane.showMessageDialog(null, "ERROR : The compressed file " + gui.inputFileName
							+ " should contain only one file named \"metadata.json\"");
					zis.close();
					return null;
				}

				newFile = new File(outputDir, zipEntry.getName());
				if (newFile.exists()) {
					// If the file "metadata.json" exists in the uncompressing folder, rename the
					// file to something new that is highly unlikely to exist.
					newFile = new File(outputDir, "tempMetadata" + System.currentTimeMillis() + ".json");
				}
				
				/**
				 * From here on, disable the start, and the browse buttons.
				 */
				gui.statusOfAllButtons(false);
				publish("\nUncompressing input file...");
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				byte[] buffer = new byte[1024];
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				publish("\nFinished uncompressing file successfully");
				fos.close();

				zis.closeEntry();
				zis.close();
				// https://www.baeldung.com/java-compress-and-uncompress
			} catch (FileNotFoundException ex) {
				// If it reached here the File doesn't exist or something happens.
				publish("\nInput File doesn't exist please provide a proper file\nEROR : " + ex.getMessage());
				ex.printStackTrace();
				gui.statusOfAllButtons(true);
			} catch (IOException ex) {
				ex.printStackTrace();
				publish("\nIO exception on input File\nEROR : " + ex.getMessage());
				gui.statusOfAllButtons(true);
			}

		}
		int returnVal;
		if (newFile == null) {
			// this means that the input is a json and not compressed
			gui.statusOfAllButtons(false);
			returnVal = Main.startProcessing(gui.inputFileName, gui.outputFileName, this);
		} else {
			// this means the input file is a compressed file.
			gui.statusOfAllButtons(false);
			returnVal = Main.startProcessing(newFile.getAbsolutePath(), gui.outputFileName, this);
			// Delete the extracted file.
			if (newFile.delete()) {
				System.out.println("Successfully deleted the temporary uncompressed file");
			}
		}
		if (returnVal == 1) {
			publish("\nFinished successfully!!!");
			gui.statusOfAllButtons(true);
		} else {
			gui.statusOfAllButtons(true);
			publish("\nERROR : Conversion unsuccessful.");
		}
		return null;
	}

	public void showMessage(String message) {
		publish(message);
	}

	@Override
	protected void process(List<String> chunks) {
		for (String message : chunks) {
			gui.statusTextArea.append(message);
		}
	}

}
