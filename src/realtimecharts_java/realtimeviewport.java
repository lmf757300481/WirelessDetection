package realtimecharts_java;
import ChartDirector.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import javax.swing.filechooser.*;


public class realtimeviewport extends JDialog //实时图表，带有视口控制。
{
	//
	// The main method to allow this demo to run as a standalone program.
	//
	public static void main(String args[]) 
	{
		new realtimeviewport().setVisible(true);
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

    // The full range is initialized to 180 seconds. It will extend when more data are available.
    private int initialFullRange = 180;

    // The visible range is initialized to 30 seconds.
    private int initialVisibleRange = 30;

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
	private ViewPortControl viewPortControl1;
	private javax.swing.Timer chartUpdateTimer;
	private JFileChooser saveDialog;

	//
	// Constructor
	//
	realtimeviewport() 
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
		setTitle("往复式压缩机十字头轴瓦温度光纤传感在线监测系统");

		// Top label bar
		JLabel topLabel = new JLabel("武汉理工大学光纤传感技术国家工程实验室");
		topLabel.setForeground(new java.awt.Color(255, 255, 51));
		topLabel.setBackground(new java.awt.Color(0, 0, 128));
		topLabel.setBorder(new javax.swing.border.EmptyBorder(2, 0, 2, 5));
		topLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		topLabel.setOpaque(true);
		getContentPane().add(topLabel, java.awt.BorderLayout.NORTH);

		// Left panel
		JPanel leftPanel = new JPanel(null);
		leftPanel.setBorder(javax.swing.BorderFactory.createRaisedBevelBorder());
		
		// Pointer push button
		pointerPB = new JButton("指 针", loadImageIcon("pointer.gif"));
		pointerPB.setHorizontalAlignment(SwingConstants.CENTER);
		pointerPB.setMargin(new Insets(5, 5, 5, 5));
		pointerPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				pointerPB_Clicked();
			}});
		leftPanel.add(pointerPB).setBounds(1, 0, 118, 24);
        
		// Zoom In push button
		zoomInPB = new JButton("放 大", loadImageIcon("zoomin.gif"));
		zoomInPB.setHorizontalAlignment(SwingConstants.CENTER);
		zoomInPB.setMargin(new Insets(5, 5, 5, 5));
		zoomInPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				zoomInPB_Clicked();
			}});		
		leftPanel.add(zoomInPB).setBounds(1, 24, 118, 24);

		// Zoom out push button
		zoomOutPB = new JButton("缩 小", loadImageIcon("zoomout.gif"));
		zoomOutPB.setHorizontalAlignment(SwingConstants.CENTER);
		zoomOutPB.setMargin(new Insets(5, 5, 5, 5));
		zoomOutPB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt)	{
				zoomOutPB_Clicked();
			}});		
		leftPanel.add(zoomOutPB).setBounds(1, 48, 118, 24);

		// Save push button
		JButton savePB = new JButton("保 存", loadImageIcon("save.gif"));
		savePB.setHorizontalAlignment(SwingConstants.CENTER);
		savePB.setMargin(new Insets(5, 5, 5, 5));
		savePB.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				savePB_Clicked();
			}});        
		leftPanel.add(savePB).setBounds(1, 96, 118, 24);

		// Total expected panel size
		leftPanel.setPreferredSize(new Dimension(120, 300));
		
		// Chart Viewer
		chartViewer1 = new ChartViewer();
		chartViewer1.setBackground(new java.awt.Color(255, 255, 255));
		chartViewer1.setOpaque(true);
		chartViewer1.setPreferredSize(new Dimension(640, 350));
		chartViewer1.setHorizontalAlignment(SwingConstants.CENTER);
		chartViewer1.setHotSpotCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		chartViewer1.addViewPortListener(new ViewPortAdapter() {
			public void viewPortChanged(ViewPortChangedEvent e) {
				chartViewer1_ViewPortChanged(e);
			}
		});
		chartViewer1.addTrackCursorListener(new TrackCursorAdapter() {
			public void mouseMovedPlotArea(MouseEvent e) {
				chartViewer1_MouseMovedPlotArea(e);
			}
		});
		
		
		// ViewPortControl
		viewPortControl1 = new ViewPortControl();
		viewPortControl1.setPreferredSize(new Dimension(640, 60));
		viewPortControl1.setHorizontalAlignment(SwingConstants.CENTER);
		// Bind the ChartViewer to the ViewPortControl
		viewPortControl1.setViewer(chartViewer1);
		
		// Put the ChartViewer and the scroll bars in the right panel
		JPanel rightPanel = new JPanel(null);
		rightPanel.setBackground(Color.WHITE);
		rightPanel.add(chartViewer1).setBounds(0, 0, 640, 350);
		rightPanel.add(viewPortControl1).setBounds(0, 355, 640, 60);
		rightPanel.setPreferredSize(new Dimension(640, 430));
		
		// Put the leftPanel and rightPanel on the content pane
		getContentPane().add(leftPanel, java.awt.BorderLayout.WEST);
		getContentPane().add(rightPanel, java.awt.BorderLayout.CENTER);
		
		// Set all UI fonts (except labels)
		Font uiFont = new Font("Dialog", Font.PLAIN, 11);
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
				
		//
		// At this point, the user interface layout has been completed. 
		// Can load data and plot chart now.
		//

		// Initialize the ChartViewer
		initChartViewer(chartViewer1);

		// It is safe to handle events now.
		hasFinishedInitialization = true;
			
        // Start the random data generator
        dataSource = new RandomWalk(new RandomWalk.DataHandler() {
        	public void onData(double elapsedTime, double series0) {
           		dataSource_OnData(elapsedTime, series0);
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
        // Enable mouse wheel zooming
        viewer.setMouseWheelZoomRatio(1.1);

        // Configure the initial viewport 
        viewer.setViewPortWidth(initialVisibleRange / (double)initialFullRange);

		// Initially set the mouse usage to "Pointer" mode (Drag to Scroll mode)
		pointerPB.doClick();
	}
	
    //
    // Handles realtime data from RandomWave. The RandomWave will call this method from its own thread.
    //
	public void dataSource_OnData(double elapsedTime, double series0)
	{
		DataPacket p = new DataPacket();
        p.elapsedTime = elapsedTime;
        p.series0 = series0;
        
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
	// The ViewPortChanged event handler. This event occurs if the user scrolls or zooms in
	// or out the chart by dragging or clicking on the chart. It can also be triggered by
	// calling WinChartViewer.updateViewPort.
	//
	private void chartViewer1_ViewPortChanged(ViewPortChangedEvent e)
	{
		// Update the chart if necessary
		if (e.needUpdateChart())
			drawChart(chartViewer1);
		
        // Update the full chart
        drawFullChart(viewPortControl1);
	}

	//
	// Draw the chart.
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
     

        if (currentIndex > 0)
        {
            // Get the array indexes that corresponds to the visible start and end dates
            int startIndex = (int)Math.floor(Chart.bSearch2(timeStamps, 0, currentIndex, viewPortStartDate));
            int endIndex = (int)Math.ceil(Chart.bSearch2(timeStamps, 0, currentIndex, viewPortEndDate));

            // Extract the visible data
            if (timeStamps[endIndex] >= viewPortStartDate)
            {
                int noOfPoints = endIndex - startIndex + 1;
                viewPortTimeStamps = (double[])Chart.arraySlice(timeStamps, startIndex, noOfPoints);
                viewPortDataSeriesA = (double[])Chart.arraySlice(dataSeriesA, startIndex, noOfPoints);
            
            }

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

        // Set the plotarea at (20, 30) with width 41 pixels less than chart width, and height 50 pixels
        // less than chart height. Use a vertical gradient from light blue (f0f6ff) to sky blue (a0c0ff)
        // as background. Set border to transparent and grid lines to white (ffffff).
        c.setPlotArea(20, 30, c.getWidth() - 41, c.getHeight() - 50, c.linearGradientColor(0, 30, 0,
            c.getHeight() - 20, 0xf0f6ff, 0xa0c0ff), -1, Chart.Transparent, 0xffffff, 0xffffff);       

        // As the data can lie outside the plotarea in a zoomed chart, we need enable clipping.
        c.setClipping();

        // Add a title to the chart using 18 pts Times New Roman Bold Italic font
        c.addTitle("   实时温度曲线", "楷体", 18);

        // Add a legend box at (55, 25) using horizontal layout. Use 8pts Arial Bold as font. Set the
        // background and border color to Transparent and use line style legend key.
        LegendBox b = c.addLegend(55, 25, false, "Arial Bold", 10);
        b.setBackground(Chart.Transparent);
        b.setLineStyleKey();

        // Set the x and y axis stems to transparent and the label font to 10pt Arial
        c.xAxis().setColors(Chart.Transparent);
        c.yAxis().setColors(Chart.Transparent);
        c.xAxis().setLabelStyle("Arial", 10);
        c.yAxis().setLabelStyle("Arial", 10, 0x336699);

        // Add axis title using 10pts Arial Bold Italic font
        c.yAxis().setTitle("温度(℃)", "宋体", 11);
        c.yAxis().setTitlePos(Chart.Left, 1);

        // Configure the y-axis label to be inside the plot area and above the horizontal grid lines
        c.yAxis().setLabelGap(-1);
        c.yAxis().setLabelAlignment(1);
        c.yAxis().setMargin(20);

        // Configure the x-axis labels to be to the left of the vertical grid lines
        c.xAxis().setLabelAlignment(1);
        
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
        layer.addDataSet(viewPortDataSeriesA, 0xff0000, "Crosshead bearing"); //红线 0xff0000   绿线 0x00cc00
      //  layer.addDataSet(viewPortDataSeriesB, 0x00cc00, "Beta"); //绿线 0x00cc00

        //================================================================================
        // Configure axis scale and labelling
        //================================================================================

        if (currentIndex > 0)
            c.xAxis().setDateScale(viewPortStartDate, viewPortEndDate);

        // For the automatic axis labels, set the minimum spacing to 75/30 pixels for the x/y axis.
        c.xAxis().setTickDensity(75);
       
      c.yAxis().setTickDensity(40);

        // We use "hh:nn:ss" as the axis label format.
        c.xAxis().setLabelFormat("{value|nn:ss}");

        // We make sure the tick increment must be at least 1 second.
        c.xAxis().setMinTickInc(0.1);

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

        viewer.setChart(c);
	}
	
	private void drawFullChart(ViewPortControl vpc)
	{
        // Create an XYChart object of size 640 x 50 pixels   
        XYChart c = new XYChart(640, 60);

        // Set the plotarea with the same horizontal position as that in the main chart for alignment.
        c.setPlotArea(20, 0, c.getWidth() - 41, c.getHeight() - 1, 0xc0d8ff, -1, 0xcccccc, 
            Chart.Transparent, 0xffffff);

        // Set the x axis stem to transparent and the label font to 10pt Arial
        c.xAxis().setColors(Chart.Transparent);
        c.xAxis().setLabelStyle("Arial", 10);

        // Put the x-axis labels inside the plot area by setting a negative label gap. Use
        // setLabelAlignment to put the label at the right side of the tick.
        c.xAxis().setLabelGap(-1);
        c.xAxis().setLabelAlignment(1);

        // Set the y axis stem and labels to transparent (that is, hide the labels)
        c.yAxis().setColors(Chart.Transparent, Chart.Transparent);

        // Add a line layer for the lines with fast line mode enabled
        LineLayer layer = c.addLineLayer();
        layer.setFastLineMode();

        // Now we add the 3 data series to a line layer, using the color red (0xff3333), green
        // (0x008800) and blue (0x3333cc)
        layer.setXData((double[])Chart.arraySlice(timeStamps, 0, currentIndex));
        layer.addDataSet((double[])Chart.arraySlice(dataSeriesA, 0, currentIndex), 0xff3333);
        layer.addDataSet((double[])Chart.arraySlice(dataSeriesB, 0, currentIndex), 0x008800);

        // The x axis scales should reflect the full range of the view port
        c.xAxis().setDateScale(vpc.getViewer().getValueAtViewPort("x", 0), vpc.getViewer().getValueAtViewPort("x", 1));
        c.xAxis().setLabelFormat("{value|nn:ss}");

        // For the automatic x-axis labels, set the minimum spacing to 75 pixels.
        c.xAxis().setTickDensity(75);

        // For the auto-scaled y-axis, as we hide the labels, we can disable axis rounding. This can
        // make the axis scale fit the data tighter.
        c.yAxis().setRounding(false, false);

        // Output the chart
        vpc.setChart(c);
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
			SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
			String datetime = tempDate.format(new java.util.Date());   
			saveDialog.setSelectedFile(new java.io.File("温度数据"+datetime));
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
		t.draw(xLabelPos, plotArea.getBottomY() + 1, 0xffffff);

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