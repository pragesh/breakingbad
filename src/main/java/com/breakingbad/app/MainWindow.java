package com.breakingbad.app;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tmatesoft.svn.core.SVNException;

public class MainWindow {
	private Shell shell;
	private Display display;

	private int gridHorizontalSpacing = 10;
	private int gridVerticalSpacing = 4;
	private int gridMarginBottom = 5;
	private int gridMarginTop = 5;
	private int gridPadding = 2;

	private Label selectProjectLabel;

	private Text workspaceTextInput;
	private List projectList;
	private List sourceList;
	private StyledText sourceDesc;
	private StyledText updateStatusDesc;

	private JSONArray globalInfo;

	private Table featureSelectorTable;
	private Button featureSelectorButtons[];

	private Slider fitnessSlider;
	private Label fitnessSliderLabel;
	private Button runButton;

	private Slider crossoverSlider;
	private Label crossoverSliderLabel;

	private Slider mutationSlider;
	private Label mutationSliderLabel;

	private Table trainingSamplesTable = null;
	private Button discretizeCheckBox;
	private Table testingSamplesTable = null;

	private StyledText accuracyTextArea;

	private Table userSamplesTable = null;
	private Button classifyButton;
	private Label classifyResultLabel;

	private Button saveDTButton;

	private Tree graphicalDecisionTree = null;
	private Label sourceLabel;
	private Control infoLabel;
	private Button revertButton;
	private Button updateButton;
	private Label updateStatusLabel;
	private Label feedbackLabel;
	private Button feedbackYesButton;
	private Button feedbackNoButton;
	private Label selectWorkspaceLabel;
	private Label commentsLabel;
	private Text comments;
	private Button dontRevertButton;

	/*
	 * A constructor that takes in a display parameter and initializes the shell
	 * and other components of the UI
	 */
	public MainWindow(Display display) {
		this.display = display;

		shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
		shell.setText("Breaking Bad B");
		shell.setImage(new Image(display, "icons/statistics.png"));

//		Color col = new Color(display, 100, 200, 100);
//		shell.setBackground(col);
//		col.dispose();
		 

		centerShell();
		initUI();

		shell.setSize(720, 800);
		shell.setLocation(480, 100);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void getInfoFromBackend(String path) {
		RevisionProvider provider = new RevisionProvider();
		globalInfo = new JSONArray();
		try {
			globalInfo = provider.initiateProcess(path);
		} catch (IOException | SVNException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(globalInfo);
	}

	/*
	 * Centers the shell on the screen.... Doesn't work
	 */
	private void centerShell() {
		Rectangle bds = shell.getDisplay().getBounds();

		Point p = shell.getSize();

		int nLeft = (bds.width - p.x) / 2;
		int nTop = (bds.height - p.y) / 2;

		shell.setBounds(nLeft, nTop, p.x, p.y);
	}

	/*
	 * Initializes the UI
	 */
	private void initUI() {
		// Initialize Grid Layout parameters
		GridLayout gridLayout = new GridLayout(gridHorizontalSpacing, true);
		gridLayout.horizontalSpacing = gridHorizontalSpacing;
		gridLayout.verticalSpacing = gridVerticalSpacing;
		gridLayout.marginBottom = gridMarginBottom;
		gridLayout.marginTop = gridMarginTop;
		shell.setLayout(gridLayout);

		// Adding Widgets
		addWorkspaceSelector();
		addBreak(2);
		addProjectSelectionListBox();
		addBreak(2);
		addRevisionSourceListBox();
		addBreak(2);
		addRevisionSourceDescriptionBox();
		addBreak(2);
		addUpdateButton();
		addBreak(5);
		addUpdateStatusBox();
		addBreak(3);
		addCommentsSection();
		addBreak(3);
		addFeedbackButtons();
		addBreak(3);
		addRevertButton();
		addDontRevertButton();

		addFeatureSelectorTable();

		addBreak(gridHorizontalSpacing);
		// addTrainingSamplesTable(false, false);
		// addTestingSamplesTable(false, false);
		addDiscretizeCheckbox();

		addResultDisplay();

		addEditableSamplesTable();

		saveDTButton = addSaveDTButton();
		addBreak(2);
		classifyButton = addClassifyButton();
		classifyButton.setVisible(false);

		classifyResultLabel = addLabel("Result: ", gridHorizontalSpacing / 4,
				SWT.RIGHT);
		classifyResultLabel.setVisible(false);

		addBreak(gridHorizontalSpacing / 4);

	}

	/*
	 * Adds a run button, to start Machine Learning
	 */
	private Button addRunButton() {
		Button button = new Button(shell, SWT.PUSH | SWT.CENTER);
		button.setText("Run the Engine");

		addToGrid(button, gridHorizontalSpacing / 2);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		return button;
	}

	/*
	 * Creates a button, which, when clicked, will save the decision tree that
	 * has just been generated
	 */
	private Button addSaveDTButton() {
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Save To File");
		button.setVisible(false);

		addToGrid(button, gridHorizontalSpacing / 3);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Save to File Button Clicked");
			}
		});

		return button;
	}

	/*
	 * This method can be used to insert 'num' blank labels to create spaces in
	 * the Grid Layout
	 */
	private void addBreak(int num) {
		final Label label = new Label(shell, SWT.LEFT);
		label.setText("");

		GridData gridData = new GridData();
		gridData.horizontalSpan = num;
		gridData.horizontalAlignment = GridData.FILL;
		label.setLayoutData(gridData);
	}

	/*
	 * Adds a combo box to select the name of the data samples file
	 */
	private void addWorkspaceSelector() {
		selectWorkspaceLabel = addLabel("Select Workspace: ", 2, SWT.RIGHT);

		workspaceTextInput = new Text(shell, SWT.BORDER);
		workspaceTextInput.setText("C:\\hack_workspace");
		workspaceTextInput.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String path = dialog.open();
				if (path == null) {
					path = workspaceTextInput.getText();
				}
				System.out.println("Workspace Path: " + path);
				if (path != null) {
					workspaceTextInput.setText(path);
					getInfoFromBackend(path);
					updateProjectSelectionListBox();
				}
				show(selectProjectLabel, true);
				show(projectList, true);
			}

			private void updateProjectSelectionListBox() {
				projectList.remove(0, projectList.getItemCount() - 1);
				if (globalInfo != null) {
					for (int i = 0; i < globalInfo.length(); i++) {
						try {
							JSONObject projectInfo = globalInfo
									.getJSONObject(i);
							String directoryName = projectInfo
									.getString("name");
							projectList.add(directoryName);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});

		addToGrid(workspaceTextInput, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);
	}

	private <T> void show(T widget, boolean isVisible) {
		((Control) widget).setVisible(isVisible);
	}

	private <T> void enable(T widget, boolean isEnabled) {
		((Control) widget).setEnabled(isEnabled);
	}

	/*
	 * Adds a slider to vary the fitness threshold for the fitness function of
	 * the genetic algorithm Returns the initialized slider
	 */
	private Slider addFitnessThresholdSlider() {
		final double sliderRange = 1000;

		fitnessSliderLabel = addLabel("Fitness Threshold --->  ",
				gridHorizontalSpacing / 2, SWT.RIGHT);
		fitnessSliderLabel.setVisible(false);

		final Slider slider = new Slider(shell, SWT.HORIZONTAL);
		slider.setMaximum((int) sliderRange);
		addToGrid(slider, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);

		fitnessSliderLabel.setText(fitnessSliderLabel.getText()
				+ slider.getSelection() / sliderRange);

		slider.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

			}
		});

		slider.setVisible(false);

		return slider;
	}

	/*
	 * Adds a slider to vary the Crossover Rate for the natural selection
	 * process of the genetic algorithm Returns the initialized slider
	 */
	private Slider addCrossoverRateSlider() {
		final double sliderRange = 10000;

		crossoverSliderLabel = addLabel("Crossover Rate --->  ",
				gridHorizontalSpacing / 2, SWT.RIGHT);
		crossoverSliderLabel.setVisible(false);

		final Slider slider = new Slider(shell, SWT.HORIZONTAL);
		slider.setMaximum((int) sliderRange);
		addToGrid(slider, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);

		crossoverSliderLabel.setText(crossoverSliderLabel.getText()
				+ slider.getSelection() / sliderRange);

		slider.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

			}
		});

		slider.setVisible(false);

		return slider;
	}

	/*
	 * Adds a slider to vary the Mutation Rate for Genome Mutation of the
	 * genetic algorithm Returns the initialized slider
	 */
	private Slider addMutationRateSlider() {
		final double sliderRange = 10000;

		mutationSliderLabel = addLabel("Mutation Rate --->  ",
				gridHorizontalSpacing / 2, SWT.RIGHT);
		mutationSliderLabel.setVisible(false);

		final Slider slider = new Slider(shell, SWT.HORIZONTAL);
		slider.setMaximum((int) sliderRange);
		addToGrid(slider, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);

		mutationSliderLabel.setText(mutationSliderLabel.getText()
				+ slider.getSelection() / sliderRange);

		slider.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

			}
		});

		slider.setVisible(false);

		return slider;
	}

	/*
	 * Adds a list box to select an appropriate algorithm for Machine Learning.
	 * The UI changes based on the algorithm selected
	 */
	private void addProjectSelectionListBox() {
		selectProjectLabel = addLabel("Select Project", 2, SWT.RIGHT);
		selectProjectLabel.setVisible(false);

		projectList = new List(shell, SWT.BORDER);
		projectList.add("");
		projectList.add("");
		projectList.add("");
		projectList.add("");
		projectList.setVisible(false);

		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.heightHint = 200;
		projectList.setLayoutData(gridData);

		projectList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String[] selections = projectList.getSelection();
				if (selections != null && selections.length > 0) {
					updateRevisionSourceDesc(selections[0]);
				}
				show(sourceLabel, true);
				show(sourceList, true);
			}
		});

		addToGrid(projectList, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);
	}

	private void addRevisionSourceListBox() {
		sourceLabel = addLabel("Select Source", 2, SWT.RIGHT);
		sourceLabel.setVisible(false);

		sourceList = new List(shell, SWT.BORDER);
		sourceList.add("QACAND");
		sourceList.add("Jenkins");
		//sourceList.add("pjagnani");
		sourceList.setVisible(false);

		GridData gridData = new GridData();
		gridData.widthHint = 200;
		gridData.heightHint = 200;
		sourceList.setLayoutData(gridData);

		sourceList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String[] selections = projectList.getSelection();
				if (selections != null && selections.length > 0) {
					updateRevisionSourceDesc(selections[0]);
				}
				show(infoLabel, true);
				show(sourceDesc, true);
				show(updateButton, true);
			}
		});

		addToGrid(sourceList, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);
	}

	private void addRevisionSourceDescriptionBox() {
		infoLabel = addLabel("Info", 2, SWT.RIGHT);
		infoLabel.setVisible(false);

		sourceDesc = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		sourceDesc.setEditable(false);
		sourceDesc.setVisible(false);

		GridData gridData = new GridData();
		gridData.horizontalSpan = gridHorizontalSpacing / 2;
		gridData.heightHint = 200;
		gridData.horizontalAlignment = GridData.FILL;
		sourceDesc.setLayoutData(gridData);

		// addToGrid(sourceDesc, gridHorizontalSpacing / 2 - gridPadding);
		addBreak(gridPadding);
	}

	private Button addRevertButton() {
		addLabel("", 2, SWT.RIGHT);
		revertButton = new Button(shell, SWT.PUSH);
		revertButton.setText("Revert");
		revertButton.setVisible(false);

		addToGrid(revertButton, gridHorizontalSpacing / 4);

		revertButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String directoryName = projectList.getSelection()[0];
				String revisionSource = sourceList.getSelection()[0];

				JSONObject selectedProjectInfo = getSelectedProjectInfo(
						directoryName, revisionSource);

				UpdateService updateSer = new UpdateService();
				JSONArray updateStatus = new JSONArray();
				try {
					updateStatus = updateSer.revert(
							workspaceTextInput.getText(),
							new String[] { selectedProjectInfo
									.getString("name") },
							new String[] { selectedProjectInfo
									.getString("localrevision") },
							new String[] { selectedProjectInfo.getString("url") });

					StringBuilder sb = new StringBuilder();
					if (updateStatus != null) {
						for (int i = 0; i < updateStatus.length(); i++) {
							JSONObject jsonObj = updateStatus.getJSONObject(i);
							sb.append("Project Name: "
									+ jsonObj.getString("name"));
							sb.append("\nURL: " + jsonObj.getString("url"));
							sb.append("\nSuccessfully updated to revision "
									+ jsonObj.getString("revision"));
							sb.append("\nConflicted Files: ");
							JSONArray conflictedFiles = new JSONArray();
							if (jsonObj.has("conflictFiles")) {
								conflictedFiles = jsonObj
										.getJSONArray("conflictFiles");
							}
							String conflictedFilesString = getString(conflictedFiles);
							if (!conflictedFilesString.isEmpty()) {
								sb.append(conflictedFilesString);
							} else {
								sb.append("None");
							}
						}
					} else {
						sb.append("No data available");
					}
					updateStatusDesc.setText(sb.toString());
				} catch (SVNException | JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					updateStatusDesc.setText(e1.toString());
				}
				System.out.println(updateStatus);

				enableBreakingBadWidgets(true);
				show(revertButton, false);
				show(dontRevertButton, false);
			}
		});

		return revertButton;
	}
	
	private Button addDontRevertButton() {
		dontRevertButton = new Button(shell, SWT.PUSH);
		dontRevertButton.setText("Don't Revert");
		dontRevertButton.setVisible(false);

		addToGrid(dontRevertButton, gridHorizontalSpacing / 4);

		dontRevertButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				enableBreakingBadWidgets(true);
				show(revertButton, false);
				show(dontRevertButton, false);
			}
		});

		return dontRevertButton;
	}

	private Button addUpdateButton() {
		updateButton = new Button(shell, SWT.PUSH);
		updateButton.setText("Update");
		updateButton.setVisible(false);

		addToGrid(updateButton, gridHorizontalSpacing / 4);

		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String directoryName = projectList.getSelection()[0];
				String revisionSource = sourceList.getSelection()[0];

				String revisionSourceKey = (revisionSource.equals("QACAND") ? "qa"
						: "j");
				JSONObject selectedProjectInfo = getSelectedProjectInfo(
						directoryName, revisionSource);

				UpdateService updateSer = new UpdateService();
				JSONArray updateStatus = new JSONArray();
				try {
					updateStatus = updateSer.update(
							workspaceTextInput.getText(),
							new String[] { selectedProjectInfo
									.getString("name") },
							new String[] { selectedProjectInfo
									.getString(revisionSourceKey + "revision") },
							new String[] { selectedProjectInfo.getString("url") });

					StringBuilder sb = new StringBuilder();
					if (updateStatus != null) {
						for (int i = 0; i < updateStatus.length(); i++) {
							JSONObject jsonObj = updateStatus.getJSONObject(i);
							sb.append("Project Name: "
									+ jsonObj.getString("name"));
							sb.append("\nURL: " + jsonObj.getString("url"));
							sb.append("\nSuccessfully updated to revision "
									+ jsonObj.getString("revision"));
							sb.append("\nConflicted Files: ");
							JSONArray conflictedFiles = new JSONArray();
							if (jsonObj.has("conflictFiles")) {
								conflictedFiles = jsonObj
										.getJSONArray("conflictFiles");
							}
							String conflictedFilesString = getString(conflictedFiles);
							if (!conflictedFilesString.isEmpty()) {
								sb.append(conflictedFilesString);
							} else {
								sb.append("None");
							}
						}
					} else {
						sb.append("No data available");
					}
					updateStatusDesc.setText(sb.toString());
				} catch (SVNException | JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					updateStatusDesc.setText(e1.toString());
				}
				System.out.println(updateStatus);

				show(updateStatusLabel, true);
				show(updateStatusDesc, true);
				show(feedbackLabel, true);
				show(feedbackYesButton, true);
				show(feedbackNoButton, true);
				show(commentsLabel, true);
				show(comments, true);

				enableBreakingBadWidgets(false);
			}
		});

		return updateButton;
	}

	private void addUpdateStatusBox() {
		updateStatusLabel = addLabel("Update Status", 2, SWT.RIGHT);
		updateStatusLabel.setVisible(false);

		updateStatusDesc = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		updateStatusDesc.setEditable(false);
		updateStatusDesc.setVisible(false);

		GridData gridData = new GridData();
		gridData.horizontalSpan = gridHorizontalSpacing / 2;
		gridData.heightHint = 100;
		gridData.horizontalAlignment = GridData.FILL;
		updateStatusDesc.setLayoutData(gridData);
	}

	private void addFeedbackButtons() {
		feedbackLabel = addLabel("Did this revision work? ", 2, SWT.RIGHT);
		feedbackLabel.setVisible(false);

		feedbackYesButton = new Button(shell, SWT.PUSH);
		feedbackYesButton.setText("Yes");
		feedbackYesButton.setVisible(false);

		addToGrid(feedbackYesButton, gridHorizontalSpacing / 4);

		feedbackYesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String directoryName = projectList.getSelection()[0];
				String revisionSource = sourceList.getSelection()[0];

				String revisionSourceKey = (revisionSource.equals("QACAND") ? "qa"
						: "j");
				JSONObject selectedProjectInfo = getSelectedProjectInfo(
						directoryName, revisionSource);

				FeedBackTracker feedback = new FeedBackTracker();
				JSONArray jArray3 = new JSONArray();
				try {
					jArray3 = feedback.saveFeedback(
							new String[] { selectedProjectInfo
									.getString("name") },
							new String[] { selectedProjectInfo
									.getString(revisionSourceKey + "revision") },
							new String[] { selectedProjectInfo.getString("url") },
							"true", comments.getText());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(jArray3);
				enableBreakingBadWidgets(true);
				showPrisonBreakWidgets(false);
			}
		});

		feedbackNoButton = new Button(shell, SWT.PUSH);
		feedbackNoButton.setText("No");
		feedbackNoButton.setVisible(false);

		addToGrid(feedbackNoButton, gridHorizontalSpacing / 4);

		feedbackNoButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String directoryName = projectList.getSelection()[0];
				String revisionSource = sourceList.getSelection()[0];

				String revisionSourceKey = (revisionSource.equals("QACAND") ? "qa"
						: "j");
				JSONObject selectedProjectInfo = getSelectedProjectInfo(
						directoryName, revisionSource);

				FeedBackTracker feedback = new FeedBackTracker();
				JSONArray jArray3 = new JSONArray();
				try {
					jArray3 = feedback.saveFeedback(
							new String[] { selectedProjectInfo
									.getString("name") },
							new String[] { selectedProjectInfo
									.getString(revisionSourceKey + "revision") },
							new String[] { selectedProjectInfo.getString("url") },
							"false", comments.getText());
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println(jArray3);
				show(revertButton, true);
				show(dontRevertButton, true);
				showPrisonBreakWidgets(false);
			}
		});
	}
	
	private void addCommentsSection() {
		commentsLabel = addLabel("Comments: ", 2, SWT.RIGHT);
		commentsLabel.setVisible(false);

		comments = new Text(shell, SWT.BORDER);
		comments.setVisible(false);
		addToGrid(comments, gridHorizontalSpacing / 2);
	}

	private void showPrisonBreakWidgets(boolean isVisible) {
		feedbackLabel.setVisible(isVisible);
		feedbackYesButton.setVisible(isVisible);
		feedbackNoButton.setVisible(isVisible);
		comments.setVisible(isVisible);
		commentsLabel.setVisible(isVisible);
	}

	private void updateRevisionSourceDesc(String directoryName) {
		if (sourceList.getSelection().length > 0) {
			String revisionSource = sourceList.getSelection()[0];
			updateRevisionSourceDesc(directoryName, revisionSource);
		}
	}

	private void updateRevisionSourceDesc(String directoryName,
			String revisionSource) {
		if(revisionSource.equals("pjagnani")) {
			updateUserRevisionSourceDesc(directoryName, revisionSource);
			return;
		}
		System.out.println("Selected Project = " + directoryName);
		System.out.println("Revision Source = " + revisionSource);
		JSONObject selectedProjectInfo = getSelectedProjectInfo(directoryName,
				revisionSource);

		String revisionSourceKey = (revisionSource.equals("QACAND") ? "qa"
				: "j");

		System.out.println("Found: " + selectedProjectInfo);
		if (selectedProjectInfo != null) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("URL : " + selectedProjectInfo.getString("url"));
				sb.append("\nYour Revision: ")
						.append(selectedProjectInfo.getString("localrevision"))
						.append("; \n\tLast Modified on ")
						.append(selectedProjectInfo
								.getString("localrevisiondate"));
				sb.append("\nSource Revision: ")
						.append(selectedProjectInfo.getString(revisionSourceKey
								+ "revision"))
						.append("; \n\tLast Modified on ")
						.append(selectedProjectInfo.getString(revisionSourceKey
								+ "revisiondate"));

				if (selectedProjectInfo.has(revisionSourceKey
						+ "revisionpercentage")) {
					String health = selectedProjectInfo
							.getString(revisionSourceKey + "revisionpercentage");
					JSONArray positiveUsers = selectedProjectInfo
							.getJSONArray(revisionSourceKey
									+ "revisionpositive");
					JSONArray negativeUsers = selectedProjectInfo
							.getJSONArray(revisionSourceKey
									+ "revisionnegative");
					JSONArray inProgressUsers = selectedProjectInfo
							.getJSONArray(revisionSourceKey
									+ "revisioninprogress");
					String positiveUsersString = getString(positiveUsers);
					String negativeUsersString = getString(negativeUsers);
					String inProgressUsersString = getString(inProgressUsers);

					sb.append("\n-----\nHealth: "
							+ health
							+ " % "
							+ " ("
							+ positiveUsers.length()
							+ " / "
							+ selectedProjectInfo.getString(revisionSourceKey
									+ "revisiontotalcount") + ")");
					sb.append("\nUsers for whom this revision works: "
							+ (positiveUsersString.isEmpty() ? "None"
									: positiveUsersString));
					sb.append("\nUsers for whom this revision doesn't work: "
							+ (negativeUsersString.isEmpty() ? "None"
									: negativeUsersString));
					sb.append("\nUsers who are currently evaluating this revision: "
							+ (inProgressUsersString.isEmpty() ? "None"
									: inProgressUsersString));
				} else {
					sb.append("\n---\nHealth Information Currently Unavailable");
				}
				sourceDesc.setText(sb.toString());
			} catch (JSONException e) {
				System.err.println(e);
			}
		}
	}
	
	private void updateUserRevisionSourceDesc(String directoryName,
			String revisionSource) {
		System.out.println("Selected Project = " + directoryName);
		System.out.println("Revision Source = " + revisionSource);
		JSONObject selectedProjectInfo = getSelectedProjectInfo(directoryName,
				revisionSource);

		String revisionSourceKey = (revisionSource.equals("QACAND") ? "qa"
				: "j");

		System.out.println("Found: " + selectedProjectInfo);
		if (selectedProjectInfo != null) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("URL : " + selectedProjectInfo.getString("url"));
				sb.append("\nYour Revision: ")
						.append(selectedProjectInfo.getString("localrevision"))
						.append("; \n\tLast Modified on ")
						.append(selectedProjectInfo
								.getString("localrevisiondate"));
				sb.append("\nSource Revision: ")
						.append(selectedProjectInfo.getString(revisionSourceKey
								+ "revision"))
						.append("; \n\tLast Modified on ")
						.append(selectedProjectInfo.getString(revisionSourceKey
								+ "revisiondate"));
				sb.append("\n---\nHealth: ").append("Working perfectly");
				sourceDesc.setText(sb.toString());
			} catch (JSONException e) {
				System.err.println(e);
			}
		}
	}

	private String getString(JSONArray jsonArr) throws JSONException {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < jsonArr.length(); i++) {
			sb.append("\n\t").append(jsonArr.getString(i));
		}
		return sb.toString();
	}

	private JSONObject getSelectedProjectInfo(String directoryName,
			String revisionSource) {
		if (globalInfo != null) {
			for (int i = 0; i < globalInfo.length(); i++) {
				try {
					JSONObject projectInfo = globalInfo.getJSONObject(i);
					if (directoryName.equals(projectInfo.getString("name"))) {
						return projectInfo;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private void enableBreakingBadWidgets(boolean isEnabled) {
		enable(selectWorkspaceLabel, isEnabled);
		enable(workspaceTextInput, isEnabled);
		enable(selectProjectLabel, isEnabled);
		enable(projectList, isEnabled);
		enable(sourceLabel, isEnabled);
		enable(sourceList, isEnabled);
		enable(updateButton, isEnabled);
	}

	private void addDiscretizeCheckbox() {
		discretizeCheckBox = new Button(shell, SWT.CHECK);
		discretizeCheckBox.setText("Show Discretized Values");
		discretizeCheckBox.setSelection(false);
		discretizeCheckBox.setVisible(false);

		addBreak(5);
		addToGrid(discretizeCheckBox, 2);

		discretizeCheckBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (discretizeCheckBox.getSelection()) {
					addTrainingSamplesTable(true, true);
					addTestingSamplesTable(true, true);
				} else {
					addTrainingSamplesTable(false, true);
					addTestingSamplesTable(false, true);
				}
			}
		});
	}

	/*
	 * Initializes the trainingSamplesTable and selects the appropriate
	 * SampleCollection, based on the value of isDiscretize
	 */
	private void addTrainingSamplesTable(boolean isDiscretize,
			boolean highlightIncorrectItems) {
		Table previousTable = trainingSamplesTable; // Required for replacement
													// in Grid Layout
		trainingSamplesTable = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);

		// addTable(trainingSamplesTable, samplesCollection, previousTable);

		if (highlightIncorrectItems)
			highlightIncorrectlyClassifiedSamples();
	}

	/*
	 * Initializes the testingSamplesTable and selects the appropriate
	 * SampleCollection, based on the value of isDiscretize
	 */
	private void addTestingSamplesTable(boolean isDiscretize,
			boolean highlightIncorrectItems) {
		Table previousTable = testingSamplesTable; // Required for replacement
													// in Grid Layout
		testingSamplesTable = new Table(shell, SWT.MULTI | SWT.BORDER
				| SWT.FULL_SELECTION);

		// addTable(testingSamplesTable, samplesCollection, previousTable);

		if (highlightIncorrectItems)
			highlightIncorrectlyClassifiedSamples();
	}

	/*
	 * This method creates an Excel-type table to display all the samples of the
	 * selected SampleCollection in a samplesTable, replacing any
	 * previousSamplesTable that was drawn previously, if any.
	 */
	private void addTable(Table samplesTable, Table previousSamplesTable) {
		// samplesTable.setLinesVisible(true);
		// samplesTable.setHeaderVisible(true);
		//
		// GridData data = new GridData(); // SWT.FILL, SWT.FILL, false, false);
		// data.heightHint = 200;
		// data.widthHint = 200;
		// data.horizontalSpan = gridHorizontalSpacing / 2;
		// data.horizontalAlignment = GridData.FILL;
		// samplesTable.setLayoutData(data);
		//
		// // This block is executed if a previous table has to be overwritten
		// with
		// // a new table
		// if (previousSamplesTable != null) {
		// samplesTable.moveAbove(previousSamplesTable);
		// previousSamplesTable.dispose();
		// samplesTable.getParent().layout();
		// }
		//
		// ArrayList<String> featureList = samplesCollection.getfeatureList();
		// for (String feature : featureList) {
		// TableColumn column = new TableColumn(samplesTable, SWT.NONE);
		// column.setText(feature);
		// }
		//
		// // For the last column - classification
		// TableColumn column = new TableColumn(samplesTable, SWT.NONE);
		// column.setText("Class");
		//
		// ArrayList<Sample> samplesList = samplesCollection
		// .getSampleAsArrayList();
		//
		// for (Sample sample : samplesList) {
		// // sample.display();
		// // System.out.println();
		// TableItem item = new TableItem(samplesTable, SWT.NONE);
		//
		// int featureIndex = 0;
		// for (String feature : featureList)
		// item.setText(featureIndex++, ""
		// + sample.getFeature(feature).getValue());
		// item.setText(featureIndex, sample.getClassification());
		// }
		//
		// for (int i = 0; i < featureList.size() + 1; i++) {
		// samplesTable.getColumn(i).pack();
		// }
	}

	/*
	 * This method is used to highlight all the samples in the graphical tables
	 * that have been classified incorrectly by the chosen decision tree.
	 */
	private void highlightIncorrectlyClassifiedSamples() {
		// if (currentException == null)
		// return;
		//
		// ArrayList<Integer> trainingErrorIndices = currentException
		// .getTrainingErrorIndices();
		// for (int index : trainingErrorIndices) {
		// trainingSamplesTable.getItem(index).setBackground(
		// display.getSystemColor(SWT.COLOR_RED));
		// trainingSamplesTable.getItem(index).setForeground(
		// display.getSystemColor(SWT.COLOR_YELLOW));
		// }
		//
		// ArrayList<Integer> testErrorIndices = currentException
		// .getTestErrorIndices();
		// for (int index : testErrorIndices) {
		// testingSamplesTable.getItem(index).setBackground(
		// display.getSystemColor(SWT.COLOR_RED));
		// testingSamplesTable.getItem(index).setForeground(
		// display.getSystemColor(SWT.COLOR_YELLOW));
		// }
	}

	/*
	 * Adds a table for manual feature selection by the user.
	 */
	private void addFeatureSelectorTable() {
		// Table previousTable = featureSelectorTable;
		//
		// featureSelectorTable = new Table(shell, SWT.MULTI | SWT.BORDER
		// | SWT.FULL_SELECTION | SWT.V_SCROLL);
		// featureSelectorTable.setLinesVisible(true);
		// featureSelectorTable.setHeaderVisible(true);
		// featureSelectorTable.setVisible(false);
		//
		// GridData data = new GridData(); // SWT.FILL, SWT.FILL, false, false);
		//
		// data.heightHint = 25;
		// if (comboDatasetBox.getText().startsWith("WHINE")) // Special
		// // case...can't
		// // resize row height
		// // later, due to bug
		// // https://bugs.eclipse.org/bugs/show_bug.cgi?id=154341
		// data.heightHint = 50;
		// if (comboDatasetBox.getText().startsWith("HORSE"))
		// data.heightHint = 35;
		//
		// data.widthHint = 200;
		// data.horizontalSpan = gridHorizontalSpacing;
		// data.horizontalAlignment = GridData.FILL;
		// featureSelectorTable.setLayoutData(data);
		//
		// // This block is executed if a previous table has to be overwritten
		// with
		// // a new table
		// if (previousTable != null) {
		// featureSelectorTable.moveAbove(previousTable);
		// previousTable.dispose();
		// featureSelectorTable.getParent().layout();
		// }
		//
		// ArrayList<String> featureList = Genome.getSamples().getfeatureList();
		// for (String feature : featureList) {
		// TableColumn column = new TableColumn(featureSelectorTable, SWT.NONE);
		// column.setMoveable(true);
		// column.setText(feature);
		// }
		//
		// double minWidth = 0;
		// TableItem item = new TableItem(featureSelectorTable, SWT.NONE);
		//
		// featureSelectorButtons = new Button[featureList.size()];
		//
		// for (int i = 0; i < featureList.size(); i++) {
		// featureSelectorButtons[i] = new Button(featureSelectorTable,
		// SWT.CHECK);
		// featureSelectorButtons[i].pack();
		// TableEditor editor = new TableEditor(featureSelectorTable);
		// Point size = featureSelectorButtons[i].computeSize(SWT.DEFAULT,
		// SWT.DEFAULT);
		// editor.minimumWidth = size.x;
		// minWidth = Math.max(size.x, minWidth);
		// editor.minimumHeight = size.y;
		// editor.horizontalAlignment = SWT.CENTER;
		// editor.verticalAlignment = SWT.CENTER;
		// editor.setEditor(featureSelectorButtons[i], item, i);
		// }
		//
		// for (int i = 0; i < featureList.size(); i++) {
		// featureSelectorTable.getColumn(i).pack();
		// }
		//
		// TableItem item1 = featureSelectorTable.getItem(0);
		// System.out.println(item1);
	}

	/*
	 * Creates a text area where the results of the classification process can
	 * be displayed
	 */
	private void addResultDisplay() {
		accuracyTextArea = new StyledText(shell, SWT.BORDER);
		accuracyTextArea.setVisible(false);

		GridData gridData = new GridData();
		gridData.horizontalSpan = gridHorizontalSpacing / 2;
		gridData.heightHint = 70;
		gridData.horizontalAlignment = GridData.FILL;
		(accuracyTextArea).setLayoutData(gridData);
	}

	/*
	 * This creates a table of 1 row, that accepts user-input for classification
	 */
	private void addEditableSamplesTable() {
		// Table tempTable = userSamplesTable; // Required for replacement in
		// Grid
		// // Layout
		//
		// userSamplesTable = new Table(shell, SWT.MULTI | SWT.BORDER
		// | SWT.FULL_SELECTION);
		// userSamplesTable.setLinesVisible(true);
		// userSamplesTable.setHeaderVisible(true);
		// userSamplesTable.setVisible(false);
		//
		// GridData data = new GridData(); // SWT.FILL, SWT.FILL, false, false);
		// data.heightHint = 60;
		// data.widthHint = 100;
		// data.horizontalSpan = gridHorizontalSpacing / 2;
		// data.horizontalAlignment = GridData.FILL;
		// userSamplesTable.setLayoutData(data);
		//
		// // This block is executed if a previous table has to be overwritten
		// with
		// // a new table
		// if (tempTable != null) {
		// userSamplesTable.moveAbove(tempTable);
		// tempTable.dispose();
		// userSamplesTable.getParent().layout();
		// }
		//
		// SampleCollection samplesCollection = new SampleCollection(
		// DataHolder.getTestingSamplesFileName(),
		// DataHolder.getAttributesFileName());
		//
		// ArrayList<String> featureList = samplesCollection.getfeatureList();
		// for (String feature : featureList) {
		// TableColumn column = new TableColumn(userSamplesTable, SWT.NONE);
		// column.setText(feature);
		// }
		//
		// // Fill initial table with dummy values that can be modified
		// TableItem item = new TableItem(userSamplesTable, SWT.NONE);
		// ArrayList<Sample> samplesList = samplesCollection
		// .getSampleAsArrayList();
		// int featureIndex = 0;
		// for (String feature : featureList)
		// item.setText(featureIndex++,
		// "" + samplesList.get(4).getFeature(feature).getValue());
		//
		// for (int i = 0; i < featureList.size(); i++) {
		// userSamplesTable.getColumn(i).pack();
		// }
		//
		// // Editor
		// final TableEditor editor = new TableEditor(userSamplesTable);
		// editor.horizontalAlignment = SWT.LEFT;
		// editor.grabHorizontal = true;
		// userSamplesTable.addListener(SWT.MouseDown, new Listener() {
		// public void handleEvent(Event event) {
		// Rectangle clientArea = userSamplesTable.getClientArea();
		// Point pt = new Point(event.x, event.y);
		// int index = userSamplesTable.getTopIndex();
		// while (index < userSamplesTable.getItemCount()) {
		// boolean visible = false;
		// final TableItem item = userSamplesTable.getItem(index);
		// for (int i = 0; i < userSamplesTable.getColumnCount(); i++) {
		// Rectangle rect = item.getBounds(i);
		// if (rect.contains(pt)) {
		// final int column = i;
		// final Text text = new Text(userSamplesTable,
		// SWT.NONE);
		// Listener textListener = new Listener() {
		// public void handleEvent(final Event e) {
		// switch (e.type) {
		// case SWT.FocusOut:
		// item.setText(column, text.getText());
		// text.dispose();
		// break;
		// case SWT.Traverse:
		// switch (e.detail) {
		// case SWT.TRAVERSE_RETURN:
		// item.setText(column, text.getText());
		// // FALL THROUGH
		// case SWT.TRAVERSE_ESCAPE:
		// text.dispose();
		// e.doit = false;
		// }
		// break;
		// }
		// }
		// };
		// text.addListener(SWT.FocusOut, textListener);
		// text.addListener(SWT.Traverse, textListener);
		// editor.setEditor(text, item, i);
		// text.setText(item.getText(i));
		// text.selectAll();
		// text.setFocus();
		// return;
		// }
		// if (!visible && rect.intersects(clientArea)) {
		// visible = true;
		// }
		// }
		// if (!visible)
		// return;
		// index++;
		// }
		// }
		// });
	}

	/*
	 * Adds a "classify" button to classify the sample entered by the user in
	 * the Editable Table.
	 */
	private Button addClassifyButton() {
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Classify");
		button.setVisible(false);

		addToGrid(button, gridHorizontalSpacing / 3);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		return button;
	}

	/*
	 * This method creates a label widget with the lyrics as the text parameter,
	 * and size = horizontalSpacing and style as parameter. Returns the
	 * Initialized label
	 */
	private Label addLabel(String lyrics, int horizontalSpacing, int style) {
		Label label = new Label(shell, style);
		label.setText(lyrics);
		addToGrid(label, horizontalSpacing);
		return label;
	}

	/*
	 * A Generic Method to attach a generic widget to the grid, initialized with
	 * horizontal span
	 */
	private <T> void addToGrid(T widget, int horizantalSpan) {
		GridData gridData = new GridData();
		gridData.horizontalSpan = horizantalSpan;
		gridData.horizontalAlignment = GridData.FILL;
		((Control) widget).setLayoutData(gridData);
	}

	/*
	 * Shows/Hides all widgets that shouldn't be displayed in the GUI because
	 * algorithm hasn't been selected (Step 1)
	 */
	private void toggleIllegalWidgetsForStep1(boolean flag) {
		fitnessSliderLabel.setVisible(flag);
		fitnessSlider.setVisible(flag);
		crossoverSliderLabel.setVisible(flag);
		crossoverSlider.setVisible(flag);
		mutationSliderLabel.setVisible(flag);
		mutationSlider.setVisible(flag);
		runButton.setVisible(flag);
	}

	/*
	 * Shows/Hides all widgets that shouldn't be displayed before the Genetic
	 * Algorithm is run
	 */
	private void toggleIllegalWidgetsForStep2(boolean flag) {
		userSamplesTable.setVisible(flag);
		classifyButton.setVisible(flag);
		saveDTButton.setVisible(flag);
		classifyResultLabel.setVisible(flag);
		graphicalDecisionTree.setVisible(flag);
		discretizeCheckBox.setVisible(flag);
	}
}
