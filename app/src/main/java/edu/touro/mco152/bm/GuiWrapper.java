package edu.touro.mco152.bm;

import javax.swing.JOptionPane;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

public class GuiWrapper implements DiskUI {

	@Override
	public void addReadMark(DiskMark mark) {
		Gui.addReadMark(mark);
	}

	@Override
	public void addWriteMark(DiskMark mark) {
		Gui.addWriteMark(mark);

	}

	@Override
	public void resetTestData() {
		Gui.resetTestData();
	}

	@Override
	public void updateLegend() {
		Gui.updateLegend();
	}

	@Override
	public void initLegend(String titleText) {
		Gui.chartPanel.getChart().getTitle().setVisible(true);
		Gui.chartPanel.getChart().getTitle().setText(titleText);
	}

	@Override
	public void addRun(DiskRun run) {
		Gui.runPanel.addRun(run);
	}

	@Override
	public void sendPlainMessage(String msg, String title) {
		JOptionPane.showMessageDialog(Gui.mainFrame, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void sendErrorMessage(String msg, String title) {
		JOptionPane.showMessageDialog(Gui.mainFrame, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void adjustSensitivity() {
		Gui.mainFrame.adjustSensitivity();
	}
}
