package realtimecharts_java;
import ChartDirector.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;

 
class realtimemultithread extends JDialog  //Multithreading Real-Time Chart 多线程实时图表
{
	//
	// The main method to allow this demo to run as a standalone program.
	//
	public static void main(String args[]) 
	{
		new realtimemultithread().setVisible(true);
		System.exit(0); 
	} 
		
    // The random data source
    private RandomWalk dataSource;

    // A thread-safe queue with minimal read/write contention
    private class DataPacket
    {
        public double elapsedTime;
        public double series0;
        public double series1;
    };
    private DoubleBufferedQueue<DataPacket> buffer = new DoubleBufferedQueue<DataPacket>();
	
    // The data arrays that store the realtime data. The data arrays are updated in realtime. 
    // In this demo, we store at most 10000 values. 
	private final int sampleSize = 10000;
	private double[] timeStamps = new double[sampleSize];
	private double[] dataSeriesA = new double[sampleSize];
	private double[] dataSeriesB = new double[sampleSize];

	// The index of the array position to which new data values are added.
	private int currentIndex = 0;

	// The full range is initialized to 60 seconds of data. It can be extended when more data
	// are available.
	private int initialFullRange = 60;

	// The maximum zoom in is 5 seconds.
	private int zoomInLimit = 5;

    // If the track cursor is at the end of the data series, we will automatic move the track
    // line when new data arrives.
    private double trackLineEndPos;
    private boolean trackLineIsAtEnd;
    
	// This flag is used to suppress event handlers before complete initialization
	private boolean hasFinishedInitialization;

	//
	// Controls
	//
	private JButton pointerPB;
	private JButton zoomInPB;
	private JButton zoomOutPB;
	private ChartViewer chartViewer1;
	private JScrollBar hScrollBar1;
	private javax.swing.Timer chartUpdateTimer;
	private JFileChooser saveDialog;

	
	//
	// Constructor
	//
	realtimemultithread() 
	{
		// Set dialog to modal and non-resizable
		setModal(true);
		setResizable(false);
		
		// Clean up and exit on close
		addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (null != dataSource)
                    dataSource.stop();
            	System.exit(0);
            } 
        });
		
		// Set title to name of this demo program
		setTitle("Multithreading Real-Time Chart with Zoom/Scroll and Track Line");

	   // Font to use for user interface elements
		Font uiFont = new Font("Dialog", Font.PLAIN, 11);

		// Top label bar
		JLabel topLabel = new JLabel("Advanced Software Engineering");
		topLabel.setForeground(new Color(255, 255, 51));
		topLabel.setBackground(new Color(0, 0, 128));
		topLabel.setBorder(new javax.swing.border.EmptyBorder(2, 0, 2, 5));
		topLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		topLabel.setOpaque(true);
		getContentPane().add(topLabel, BorderLayout.NORTH);

		// Left panel
		JPanel leftPanel = new JPanel(null);
		leftPanel.setBorder(BorderFactory.createRaisedBevelBorder());

		// Pointer push button
		pointerPB = new JButton("Pointer", loadImageIcon("pointer.gif"));
		pointerPB.setHorizontalAlignment(SwingConstants.LEFT);
		pointerPB.setMargin(new Insets(5, 5, 5, 5));
		pointerPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				pointerPB_Clicked();
			}});
		leftPanel.add(pointerPB).setBounds(1, 0, 118, 24);
        
		// Zoom In push button
		zoomInPB = new JButton("Zoom In", loadImageIcon("zoomin.gif"));
		zoomInPB.setHorizontalAlignment(SwingConstants.LEFT);
		zoomInPB.setMargin(new Insets(5, 5, 5, 5));
		zoomInPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				zoomInPB_Clicked();
			}});		
		leftPanel.add(zoomInPB).setBounds(1, 24, 118, 24);

		// Zoom out push button
		zoomOutPB = new JButton("Zoom Out", loadImageIcon("zoomout.gif"));
		zoomOutPB.setHorizontalAlignment(SwingConstants.LEFT);
		zoomOutPB.setMargin(new Insets(5, 5, 5, 5));
		zoomOutPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				zoomOutPB_Clicked();
			}});		
		leftPanel.add(zoomOutPB).setBounds(1, 48, 118, 24);

		// Save push button
		JButton savePB = new JButton("Save", loadImageIcon("save.gif"));
		savePB.setHorizontalAlignment(SwingConstants.LEFT);
		savePB.setMargin(new Insets(5, 5, 5, 5));
		savePB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savePB_Clicked();
			}});        
		leftPanel.add(savePB).setBounds(1, 96, 118, 24);
	
		// Total expected panel size
		leftPanel.setPreferredSize(new Dimension(120, 360));

		// Chart Viewer
		chartViewer1 = new ChartViewer();
		chartViewer1.setBackground(new Color(255, 255, 255));
		chartViewer1.setOpaque(true);
		chartViewer1.setPreferredSize(new Dimension(640, 350));
		chartViewer1.setHorizontalAlignment(SwingConstants.CENTER);
		chartViewer1.addViewPortListener(new ViewPortAdapter() {
			public void viewPortChanged(ViewPortChangedEvent e) {
				chartViewer1_viewPortChanged(e);
			}
		});	
		chartViewer1.addTrackCursorListener(new TrackCursorAdapter() {
			public void mouseMovedPlotArea(MouseEvent e) {
				chartViewer1_MouseMovedPlotArea(e);
			}
		});

		// Horizontal Scroll bar
		hScrollBar1 = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100000000, 0, 1000000000);
		hScrollBar1.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				hScrollBar1_ValueChanged();		 
			}
		});

		// Put the ChartViewer and the scroll bars in the right panel
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(chartViewer1, java.awt.BorderLayout.CENTER);
		rightPanel.add(hScrollBar1, java.awt.BorderLayout.SOUTH);
		
		// Put the leftPanel and rightPanel on the content pane
		getContentPane().add(leftPanel, java.awt.BorderLayout.WEST);
		getContentPane().add(rightPanel, java.awt.BorderLayout.CENTER);
		
		// Set all UI fonts (except labels) to uiFont
		for (int i = 0; i < leftPanel.getComponentCount(); ++i)
		{
			Component c = leftPanel.getComponent(i);
			if (!(c instanceof JLabel))
				c.setFont(uiFont);
		}

		// The chart update timer
		chartUpdateTimer = new javax.swing.Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chartUpdateTimer_Tick();
			}
		});
		
		// Layout the window
		pack();
		
		// Initialize the ChartViewer
		initChartViewer(chartViewer1);
		
		// It is safe to handle events now.
		hasFinishedInitialization = true;
		
        // Start the random data generator
        dataSource = new RandomWalk(new RandomWalk.DataHandler() {
        	public void onData(double elapsedTime, double series0, double series1) {
        		dataSource_OnData(elapsedTime, series0, series1);
        	}
        });
		
		// Start collecting and plotting data
        dataSource.start();
		chartUpdateTimer.start();
	}
	
	//
	// A utility to load an image icon from the Java class path
	//
	private ImageIcon loadImageIcon(String path)
	{
		try { return new ImageIcon(getClass().getClassLoader().getResource(path)); }
		catch (Exception e) { return null; }
	}

	//
	// Initialize the WinChartViewer
	//
	private void initChartViewer(ChartViewer viewer)
	{
		// Enable mouse wheel zooming by setting the zoom ratio to 1.1 per wheel event
		viewer.setMouseWheelZoomRatio(1.1);
	
		// Initially set the mouse usage to "Pointer" mode (Drag to Scroll mode)
		pointerPB.doClick();
	}

    //
    // Handles realtime data from RandomWave. The RandomWave will call this method from its own thread.
    //
	public void dataSource_OnData(double elapsedTime, double series0, double series1)
	{
		DataPacket p = new DataPacket();
        p.elapsedTime = elapsedTime;
        p.series0 = series0;
        p.series1 = series1;
        buffer.put(p);
	}
		
	//
	// The chartUpdateTimer Tick event - this updates the chart periodicially by raising
	// viewPortChanged events.
	//
	private void chartUpdateTimer_Tick()
	{
		ChartViewer viewer = chartViewer1;
        
        // Enables auto scroll if the viewport is showing the latest data before the update
        boolean autoScroll = (currentIndex > 0) && (0.01 + viewer.getValueAtViewPort("x",
            viewer.getViewPortLeft() + viewer.getViewPortWidth()) >= timeStamps[currentIndex - 1]);

        // Get new data from the queue and append them to the data arrays
        java.util.List<DataPacket> packets = buffer.get();
        if (packets.size() <= 0)
            return;
        
        // if data arrays have insufficient space, we need to remove some old data.
        if (currentIndex + packets.size() >= sampleSize)
        {
            // For safety, we check if the queue contains too much data than the entire data arrays. If
            // this is the case, we only use the latest data to completely fill the data arrays.
        	if (packets.size() > sampleSize)
            	packets = packets.subList(packets.size() - sampleSize, packets.size());

            // Remove oldest data to leave space for new data. To avoid frequent removal, we ensure at
            // least 5% empty space available after removal.
            int originalIndex = currentIndex;
            currentIndex = sampleSize * 95 / 100 - 1;
            if (currentIndex > sampleSize - packets.size())
                currentIndex = sampleSize - packets.size();

            for (int i = 0; i < currentIndex; ++i)
            {
                int srcIndex = i + originalIndex - currentIndex;
                timeStamps[i] = timeStamps[srcIndex];
                dataSeriesA[i] = dataSeriesA[srcIndex];
                dataSeriesB[i] = dataSeriesB[srcIndex];
            }
        }

        // Append the data from the queue to the data arrays
        for (int n = 0; n < packets.size(); ++n)
        {
            DataPacket p = packets.get(n);
            timeStamps[currentIndex] = p.elapsedTime;
            dataSeriesA[currentIndex] = p.series0;
            dataSeriesB[currentIndex] = p.series1;
            ++currentIndex;
        }

        //
        // As we added more data, we may need to update the full range. 
        //

        double startDate = timeStamps[0];
        double endDate = timeStamps[currentIndex - 1];
        
        // Use the initialFullRange (which is 60 seconds in this demo) if this is sufficient.
        double duration = endDate - startDate;
        if (duration < initialFullRange)
            endDate = startDate + initialFullRange;

        // Update the new full data range to include the latest data
        boolean axisScaleHasChanged = viewer.updateFullRangeH("x", startDate, endDate,
            Chart.KeepVisibleRange);

        if (autoScroll)
        {
            // Scroll the viewport if necessary to display the latest data
            double viewPortEndPos = viewer.getViewPortAtValue("x", timeStamps[currentIndex - 1]);
            if (viewPortEndPos > viewer.getViewPortLeft() + viewer.getViewPortWidth())
            {
                viewer.setViewPortLeft(viewPortEndPos - viewer.getViewPortWidth());
                axisScaleHasChanged = true;
            }
        }

        // Set the zoom in limit as a ratio to the full range
        viewer.setZoomInWidthLimit(zoomInLimit / (viewer.getValueAtViewPort("x", 1) -
            viewer.getValueAtViewPort("x", 0)));

        // Trigger the viewPortChanged event. Updates the chart if the axis scale has changed
        // (scrolling or zooming) or if new data are added to the existing axis scale.
        viewer.updateViewPort(axisScaleHasChanged || (duration < initialFullRange), false);
	}

	//
	// The viewPortChanged event handler. In this example, it just updates the chart. If you
	// have other controls to update, you may also put the update code here.
	//
	private void chartViewer1_viewPortChanged(ViewPortChangedEvent e)
	{
		// In addition to updating the chart, we may also need to update other controls that
		// changes based on the view port.
		updateControls(chartViewer1);

		// Update the chart if necessary
		if (e.needUpdateChart())
			drawChart(chartViewer1);
	}

	//
	// Update other controls when the view port changed
	//
	private void updateControls(ChartViewer viewer)
	{
		// Update the scroll bar to reflect the view port position and width of the view port.
		hScrollBar1.setEnabled(chartViewer1.getViewPortWidth() < 1);
		hScrollBar1.setVisibleAmount((int)Math.ceil(chartViewer1.getViewPortWidth() * 
			(hScrollBar1.getMaximum() - hScrollBar1.getMinimum())));
		hScrollBar1.setBlockIncrement(hScrollBar1.getVisibleAmount());
		hScrollBar1.setUnitIncrement((int)Math.ceil(hScrollBar1.getVisibleAmount() * 0.1));
		hScrollBar1.setValue((int)Math.round(chartViewer1.getViewPortLeft() * 
			(hScrollBar1.getMaximum() - hScrollBar1.getMinimum())) + hScrollBar1.getMinimum());
	}

	//
	// Draw the chart and display it in the given viewer.
	//
	private void drawChart(ChartViewer viewer)
	{
		// Get the start date and end date that are visible on the chart.
		double viewPortStartDate = viewer.getValueAtViewPort("x", viewer.getViewPortLeft());
		double viewPortEndDate = viewer.getValueAtViewPort("x", viewer.getViewPortLeft() +
			viewer.getViewPortWidth());

		// Extract the part of the data arrays that are visible.
		double[] viewPortTimeStamps = null;
		double[] viewPortDataSeriesA = null;
		double[] viewPortDataSeriesB = null;

		if (currentIndex > 0)
		{
			// Get the array indexes that corresponds to the visible start and end dates
			int startIndex = (int)Math.floor(Chart.bSearch2(timeStamps, 0, currentIndex, viewPortStartDate));
			int endIndex = (int)Math.ceil(Chart.bSearch2(timeStamps, 0, currentIndex, viewPortEndDate));
			int noOfPoints = endIndex - startIndex + 1;
                
			// Extract the visible data
			viewPortTimeStamps = (double[])Chart.arraySlice(timeStamps, startIndex, noOfPoints);
			viewPortDataSeriesA = (double[])Chart.arraySlice(dataSeriesA, startIndex, noOfPoints);
			viewPortDataSeriesB = (double[])Chart.arraySlice(dataSeriesB, startIndex, noOfPoints);
			
	        // Keep track of the latest available data at chart plotting time
	        trackLineEndPos = timeStamps[currentIndex - 1];
		}

		//
		// At this stage, we have extracted the visible data. We can use those data to plot the chart.
		//

		//================================================================================
		// Configure overall chart appearance.
		//================================================================================

		// Create an XYChart object of size 640 x 350 pixels
		XYChart c = new XYChart(640, 350);

		// Set the plotarea at (55, 50) with width 80 pixels less than chart width, and height 85 pixels
		// less than chart height. Use a vertical gradient from light blue (f0f6ff) to sky blue (a0c0ff)
		// as background. Set border to transparent and grid lines to white (ffffff).
		c.setPlotArea(55, 50, c.getWidth() - 85, c.getHeight() - 80, c.linearGradientColor(0, 50, 0,
			c.getHeight() - 35, 0xf0f6ff, 0xa0c0ff), -1, Chart.Transparent, 0xffffff, 0xffffff);

		// As the data can lie outside the plotarea in a zoomed chart, we need enable clipping.
		c.setClipping();

		// Add a title to the chart using 18 pts Times New Roman Bold Italic font
		c.addTitle("   Multithreading Real-Time Chart", "Arial", 18);

		// Add a legend box at (55, 25) using horizontal layout. Use 8pts Arial Bold as font. Set the
		// background and border color to Transparent and use line style legend key.
		LegendBox b = c.addLegend(55, 25, false, "Arial Bold", 10);
		b.setBackground(Chart.Transparent);
		b.setLineStyleKey();

		// Set the x and y axis stems to transparent and the label font to 10pt Arial
		c.xAxis().setColors(Chart.Transparent);
		c.yAxis().setColors(Chart.Transparent);
		c.xAxis().setLabelStyle("Arial", 10);
		c.yAxis().setLabelStyle("Arial", 10);

		// Add axis title using 12pts Arial Bold Italic font
		c.yAxis().setTitle("Ionic Temperature (C)", "Arial Bold", 12);

		//================================================================================
		// Add data to chart
		//================================================================================

		//
		// In this example, we represent the data by lines. You may modify the code below to use other
		// representations (areas, scatter plot, etc).
		//

		// Add a line layer for the lines, using a line width of 2 pixels
		LineLayer layer = c.addLineLayer2();
		layer.setLineWidth(2);
		layer.setFastLineMode();

		// Now we add the 3 data series to a line layer, using the color red (ff0000), green (00cc00)
		// and blue (0000ff)
		layer.setXData(viewPortTimeStamps);
		layer.addDataSet(viewPortDataSeriesA, 0xff0000, "Alpha");
		layer.addDataSet(viewPortDataSeriesB, 0x00cc00, "Beta");

		//================================================================================
		// Configure axis scale and labelling
		//================================================================================

		if (currentIndex > 0)
			c.xAxis().setDateScale(viewPortStartDate, viewPortEndDate);

		// For the automatic axis labels, set the minimum spacing to 75/30 pixels for the x/y axis.
		c.xAxis().setTickDensity(75);
		c.yAxis().setTickDensity(30);

	    // We use "hh:nn:ss" as the axis label format.
	    c.xAxis().setLabelFormat("{value|hh:nn:ss}");

	    // We make sure the tick increment must be at least 1 second.
	    c.xAxis().setMinTickInc(1);

	    // Set the auto-scale margin to 0.05, and the zero affinity to 0.6
	    c.yAxis().setAutoScale(0.05, 0.05, 0.6);

		//================================================================================
		// Output the chart
		//================================================================================

		// We need to update the track line too. If the mouse is moving on the chart (eg. if 
		// the user drags the mouse on the chart to scroll it), the track line will be updated
		// in the MouseMovePlotArea event. Otherwise, we need to update the track line here.
		if (!viewer.isInMouseMoveEvent())
	        trackLineLabel(c, trackLineIsAtEnd ? c.getWidth() : viewer.getPlotAreaMouseX());
                        
		// Set the chart image to the ChartViewer
		chartViewer1.setChart(c);
	}

	//
	// Click event for the pointerPB.
	//
	private void pointerPB_Clicked()
	{
		pointerPB.setBackground(new Color(0x80, 0xff, 0x80));
		zoomInPB.setBackground(null);
		zoomOutPB.setBackground(null);
		chartViewer1.setMouseUsage(Chart.MouseUsageScrollOnDrag);
	}

	//
	// Click event for the zoomInPB.
	//
	private void zoomInPB_Clicked()
	{
		pointerPB.setBackground(null);
		zoomInPB.setBackground(new Color(0x80, 0xff, 0x80));
		zoomOutPB.setBackground(null);
		chartViewer1.setMouseUsage(Chart.MouseUsageZoomIn);
	}

	//
	// Click event for the zoomOutPB.
	//
	private void zoomOutPB_Clicked()
	{
		pointerPB.setBackground(null);
		zoomInPB.setBackground(null);
		zoomOutPB.setBackground(new Color(0x80, 0xff, 0x80));
		chartViewer1.setMouseUsage(Chart.MouseUsageZoomOut);
	}
	
	//
	// A utility class to be used with JFileChooser to filter files with certain extensions.
	// This is to maintain compatibility with older versions of Java that does not built-in
	// extension filtering class.
	//
	private static class SimpleExtensionFilter extends FileFilter 
	{
		public String ext;
		public SimpleExtensionFilter(String extension) { this.ext = "." + extension; }
		public String getDescription() { return ext.substring(1);	}
		public boolean accept(java.io.File file) 
		{ return file.isDirectory() || file.getName().endsWith(ext); }
	}

	//
	// Save button event handler
	//
	private void savePB_Clicked()
	{
		String[] extensions = { "png", "jpg", "gif", "bmp", "svg", "pdf" };

		// The File Save dialog
		if (null == saveDialog)
		{
			saveDialog = new JFileChooser();
			for (int i = 0; i < extensions.length; ++i)
				saveDialog.addChoosableFileFilter(new SimpleExtensionFilter(extensions[i]));		
			saveDialog.setAcceptAllFileFilterUsed(false);
			saveDialog.setFileFilter(saveDialog.getChoosableFileFilters()[0]);
			saveDialog.setSelectedFile(new java.io.File("chartdirector_demo"));
		}
		
		int status = saveDialog.showSaveDialog(null);
		if ((status == JFileChooser.APPROVE_OPTION) && (null != chartViewer1.getChart()))
		{
			// Add extension if the pathName does not already have one
			String pathName = saveDialog.getSelectedFile().getAbsolutePath();
			boolean hasExtension = false;
			for (int i = 0; i < extensions.length; ++i)
				if (hasExtension = pathName.endsWith("." + extensions[i]))
					break;
			if ((!hasExtension) && (saveDialog.getFileFilter() instanceof SimpleExtensionFilter))
				pathName += ((SimpleExtensionFilter)saveDialog.getFileFilter()).ext;
			
			// Issue an overwrite confirmation dialog if the file already exists
			if (new java.io.File(pathName).exists())
			{
				if (JOptionPane.YES_OPTION != JOptionPane.showOptionDialog(this, 
					"File \"" + pathName + "\" already exists, confirm overwrite?", 
					"Existing File - Confirm Overwrite", 
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, 
					null, new String[] { "Yes", "No" }, "No"))
					return;	
			}

			chartViewer1.getChart().makeChart(pathName);					
		}
	}
    
	//
	// Horizontal ScrollBar ValueChanged event handler
	//
	private void hScrollBar1_ValueChanged()
	{
		if (hasFinishedInitialization && !chartViewer1.isInViewPortChangedEvent())
		{
			// Get the view port left as according to the scroll bar
			double newViewPortLeft = ((double)(hScrollBar1.getValue() - hScrollBar1.getMinimum())) 
				/ (hScrollBar1.getMaximum() - hScrollBar1.getMinimum());

			// Check if view port has really changed - sometimes the scroll bar may issue redundant
			// value changed events when value has not actually changed.
			if (Math.abs(chartViewer1.getViewPortLeft() - newViewPortLeft) > 
				0.00001 * chartViewer1.getViewPortWidth())
			{
				// Set the view port based on the scroll bar
				chartViewer1.setViewPortLeft(newViewPortLeft);
	
				// Update the chart display without updating the image maps. We delay updating
				// the image map because the chart may still be unstable (still scrolling).
				chartViewer1.updateViewPort(true, false);
			}
		}
	}
    
	//
	// Draw track cursor when mouse is moving over plotarea
	//
	private void chartViewer1_MouseMovedPlotArea(MouseEvent e)
	{
		ChartViewer viewer = (ChartViewer)e.getSource();
        double trackLinePos = trackLineLabel((XYChart)viewer.getChart(), viewer.getPlotAreaMouseX());
        trackLineIsAtEnd = (currentIndex <= 0) || (trackLinePos == trackLineEndPos);
		viewer.updateDisplay();
	}

	//
	// Draw track line with data labels
	//
	private double trackLineLabel(XYChart c, int mouseX)
	{
		// Clear the current dynamic layer and get the DrawArea object to draw on it.
		DrawArea d = c.initDynamicLayer();

		// The plot area object
		PlotArea plotArea = c.getPlotArea();

		// Get the data x-value that is nearest to the mouse, and find its pixel coordinate.
		double xValue = c.getNearestXValue(mouseX);
		int xCoor = c.getXCoor(xValue);
		if (xCoor < plotArea.getLeftX())
			return xValue;

		// Draw a vertical track line at the x-position
		d.vline(plotArea.getTopY(), plotArea.getBottomY(), xCoor, 0x888888);

		// Draw a label on the x-axis to show the track line position.
		String xlabel = "<*font,bgColor=000000*> " + c.xAxis().getFormattedLabel(xValue, "hh:nn:ss.ff") +
			" <*/font*>";
		TTFText t = d.text(xlabel, "Arial Bold", 10);

		// Restrict the x-pixel position of the label to make sure it stays inside the chart image.
		int xLabelPos = Math.max(0, Math.min(xCoor - t.getWidth() / 2, c.getWidth() - t.getWidth()));
		t.draw(xLabelPos, plotArea.getBottomY() + 4, 0xffffff);

		// Iterate through all layers to draw the data labels
		for (int i = 0; i < c.getLayerCount(); ++i)
		{
			Layer layer = c.getLayerByZ(i);

			// The data array index of the x-value
			int xIndex = layer.getXIndexOf(xValue);

			// Iterate through all the data sets in the layer
			for (int j = 0; j < layer.getDataSetCount(); ++j)
			{
				ChartDirector.DataSet dataSet = layer.getDataSetByZ(j);

				// Get the color and position of the data label
				int color = dataSet.getDataColor();
				int yCoor = c.getYCoor(dataSet.getPosition(xIndex), dataSet.getUseYAxis());
				String name = dataSet.getDataName();

				// Draw a track dot with a label next to it for visible data points in the plot area
				if ((yCoor >= plotArea.getTopY()) && (yCoor <= plotArea.getBottomY()) && (color !=
					Chart.Transparent) && (null != name) && (0 < name.length()))
				{
					d.circle(xCoor, yCoor, 4, 4, color, color);

					String label = "<*font,bgColor=" + Integer.toHexString(color) + "*> " + c.formatValue(
						dataSet.getValue(xIndex), "{value|P4}") + " <*/font*>";
					t = d.text(label, "Arial Bold", 10);

					// Draw the label on the right side of the dot if the mouse is on the left side the
					// chart, and vice versa. This ensures the label will not go outside the chart image.
					if (xCoor <= (plotArea.getLeftX() + plotArea.getRightX()) / 2)
						t.draw(xCoor + 5, yCoor, 0xffffff, Chart.Left);
					else
						t.draw(xCoor - 5, yCoor, 0xffffff, Chart.Right);
				}
			}
		}
		
        return xValue;
	}
}
