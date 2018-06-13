package realtimecharts_java;
import ChartDirector.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;


public class realtimesweep  extends JDialog  //实时扫描图
{
	//
	// The main method to allow this demo to run as a standalone program.
	//
	public static void main(String args[]) 
	{
		new realtimesweep().setVisible(true);
		System.exit(0); 
	} 


    // The random data source
    private RandomWalk dataSource;

    // A thread-safe queue with minimal read/write contention
    private class DataPacket
    {
        public double elapsedTime;
        public double series0;
      
    };
    private DoubleBufferedQueue<DataPacket> buffer = new DoubleBufferedQueue<DataPacket>();

    // The data arrays that store the realtime data. The data arrays are updated in realtime. 
    // In this demo, we store at most 10000 values. 
    private final int sampleSize = 10000;
    private double[] timeStamps = new double[sampleSize];
    private double[] channel1 = new double[sampleSize];
  

    // The index of the array position to which new data values are added.
    private int currentIndex = 0;

    // The time range of the sweep chart
    private int timeRange = 60;
	
	//
	// Controls
	//
	private ChartViewer chartViewer1;
	private javax.swing.Timer chartUpdateTimer;

	//
	// Constructor
	//
	realtimesweep() 
	{
		// Set dialog to modal and resizable
		setModal(true);
		setResizable(true);
		
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
	
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		
		JLabel topLabel = new JLabel("武汉理工大学光纤传感技术国家工程实验室");
		topLabel.setForeground(new java.awt.Color(255, 255, 51));
		topLabel.setBackground(new java.awt.Color(0, 0, 128));
		topLabel.setBorder(new javax.swing.border.EmptyBorder(2, 0, 2, 5));
		topLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		topLabel.setOpaque(true);
		labelPanel.add(topLabel, java.awt.BorderLayout.NORTH);
		
		JLabel titleLabel = new JLabel("实时温度曲线");
		
		titleLabel.setFont(new Font("宋体", Font.PLAIN, 22));
		titleLabel.setBackground(new java.awt.Color(255, 255, 255));
		titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		titleLabel.setOpaque(true);
		labelPanel.add(titleLabel, java.awt.BorderLayout.SOUTH);
	
		// Chart Viewer
		chartViewer1 = new ChartViewer();
		chartViewer1.setBackground(new java.awt.Color(255, 255, 255));
		chartViewer1.setOpaque(true);
		chartViewer1.setHorizontalAlignment(SwingConstants.LEFT);
		chartViewer1.setVerticalAlignment(SwingConstants.TOP);
		chartViewer1.addViewPortListener(new ViewPortAdapter() {
			public void viewPortChanged(ViewPortChangedEvent e) {
				chartViewer1_ViewPortChanged(e);
			}
		});
		chartViewer1.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	chartViewer1_componentResized();       
		    }
		});

		getContentPane().add(labelPanel, java.awt.BorderLayout.NORTH);
		getContentPane().add(chartViewer1, java.awt.BorderLayout.CENTER);
			
		// The chart update timer
		chartUpdateTimer = new javax.swing.Timer(100, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				chartUpdateTimer_Tick();
			}
		});

		// Layout the window
		setSize(700, 400);
				
		//
		// At this point, the user interface layout has been completed. 
		// Can load data and plot chart now.
		//
		
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
    // Handles realtime data from RandomWalk. The RandomWalk will call this method from its own thread.
    //
    private void dataSource_OnData(double elapsedTime, double series0)
    {
        DataPacket p = new DataPacket();
        p.elapsedTime = elapsedTime;
        p.series0 = series0;
       
        buffer.put(p);
    }

    //
    // Update the chart and the viewport periodically
    //
    private void chartUpdateTimer_Tick()
    {
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

            // Remove data older than the time range to leave space for new data. The data removed must 
            // be at least equal to the packet count.
            int originalIndex = currentIndex;
            if (currentIndex > 0)
                currentIndex -= (int)(Chart.bSearch(timeStamps, 0, currentIndex, timeStamps[currentIndex - 1] - timeRange));
            if (currentIndex > sampleSize - packets.size())
                currentIndex = sampleSize - packets.size();

            for (int i = 0; i < currentIndex; ++i)
            {
                int srcIndex = i + originalIndex - currentIndex;
                timeStamps[i] = timeStamps[srcIndex];
                channel1[i] = channel1[srcIndex];
               
            }
        }

        // Append the data from the queue to the data arrays
        for (int n = 0; n < packets.size(); ++n)
        {          
            DataPacket p = packets.get(n);
            timeStamps[currentIndex] = p.elapsedTime;
            channel1[currentIndex] = p.series0;
           
            ++currentIndex;
        }

        chartViewer1.updateViewPort(true, false);
    }

    //
    // Update the chart if the winChartViewer size is changed
    //
    private void chartViewer1_componentResized()
    {
        chartViewer1.updateViewPort(true, false);
    }

    //
    // The ViewPortChanged event handler
    //
    private void chartViewer1_ViewPortChanged(ViewPortChangedEvent e)
    {
        // Update the chart if necessary
        if (e.needUpdateChart())
            drawChart(chartViewer1);
    }

    //
    // Draw the chart
    //
    private void drawChart(ChartViewer viewer)
    {
        if (currentIndex <= 0)
            return;
        
        // The start time is equal to the latest time minus the time range of the chart
        double startTime = timeStamps[currentIndex - 1] - timeRange;
        int startIndex = (int)Math.ceil(Chart.bSearch(timeStamps, 0, currentIndex, startTime) - 0.1);

        // For a sweep chart, if the line goes beyond the right border, it will wrap back to 
        // the left. We need to determine the wrap position (the right border).
        double wrapTime = Math.floor(startTime / timeRange + 1) * timeRange;
        double wrapIndex = Chart.bSearch(timeStamps, 0, currentIndex, wrapTime);
        int wrapIndexA = (int)Math.ceil(wrapIndex);
        int wrapIndexB = (int)Math.floor(wrapIndex);

        // The data arrays and the colors and names of the data series
        double[][] allArrays = new double[][] { timeStamps, channel1 };
        int[] colors = { 0xff0000, 0x00cc00 };
        String[] names = { "FBG Sensor" };

        // Split all data arrays into two parts A and B at the wrap position. The B part is the 
        // part that is wrapped back to the left.
        double[][] allArraysA = new double[allArrays.length][];
        double[][] allArraysB = new double[allArrays.length][];
        for (int i = 0; i < allArrays.length; ++i)
        {
            allArraysA[i] = (double[])Chart.arraySlice(allArrays[i], startIndex, wrapIndexA - startIndex + 1);
            allArraysB[i] = (double[])Chart.arraySlice(allArrays[i], wrapIndexB, currentIndex - wrapIndexB);
        }

        // Normalize the plotted timeStamps (the first element of allArrays) to start from 0
        for (int i = 0; i < allArraysA[0].length; ++i)
            allArraysA[0][i] -= wrapTime - timeRange;
        for (int i = 0; i < allArraysB[0].length; ++i)
            allArraysB[0][i] -= wrapTime;

        //
        // Now we have prepared all the data and can plot the chart.
        //

        //================================================================================
        // Configure overall chart appearance.
        //================================================================================

        // Create an XYChart object the same size as WinChartViewer, with a minimum of 300 x 150 
        //XYChart c = new XYChart(Math.Max(300, viewer.Width), Math.Max(150, viewer.Height));
        XYChart c = new XYChart(Math.max(300, viewer.getWidth()), Math.max(150, viewer.getHeight()));

        // Set the plotarea at (0, 0) with width 1 pixel less than chart width, and height 20 pixels
        // less than chart height. Use a vertical gradient from light blue (f0f6ff) to sky blue (a0c0ff)
        // as background. Set border to transparent and grid lines to white (ffffff).
        c.setPlotArea(0, 0, c.getWidth() - 1, c.getHeight() - 20, c.linearGradientColor(0, 0, 0,
            c.getHeight() - 20, 0xf0f6ff, 0xa0c0ff), -1, Chart.Transparent, 0xffffff, 0xffffff);

        // In our code, we can overdraw the line slightly, so we clip it to the plot area.
        c.setClipping();

        // Add a legend box at the right side using horizontal layout. Use 10pt Arial Bold as font. Set
        // the background and border color to Transparent and use line style legend key.
        LegendBox b = c.addLegend(c.getWidth() - 1, 10, false, "Arial Bold", 10);
        b.setBackground(Chart.Transparent);
        b.setAlignment(Chart.Right);
        b.setLineStyleKey();

        // Set the x and y axis stems to transparent and the label font to 10pt Arial
        c.xAxis().setColors(Chart.Transparent);
        c.yAxis().setColors(Chart.Transparent);
        c.xAxis().setLabelStyle("Arial", 10);
        c.yAxis().setLabelStyle("Arial", 10, 0x336699);

        // Configure the y-axis label to be inside the plot area and above the horizontal grid lines
        c.yAxis().setLabelGap(-1);
        c.yAxis().setMargin(20);
        c.yAxis().setLabelAlignment(1);

        // Configure the x-axis labels to be to the left of the vertical grid lines
        c.xAxis().setLabelAlignment(1);

        //================================================================================
        // Add data to chart
        //================================================================================

        // Draw the lines, which consists of A segments and B segments (the wrapped segments)
        for (double[][] dataArrays: new double[][][] { allArraysA, allArraysB })
        {
            LineLayer layer = c.addLineLayer2();
            layer.setLineWidth(2);
            layer.setFastLineMode();

            // The first element of dataArrays is the timeStamp, and the rest are the data.
            layer.setXData(dataArrays[0]);
            for (int i = 1; i < dataArrays.length; ++i)
                layer.addDataSet(dataArrays[i], colors[i - 1], names[i - 1]);

            // Disable legend entries for the B lines to avoid duplication with the A lines
            if (dataArrays == allArraysB)
                layer.setLegend(Chart.NoLegend);
        }

        // The B segments contain the latest data. We add a vertical line at the latest position. 
        int lastIndex = allArraysB[0].length - 1;
        Mark m = c.xAxis().addMark(allArraysB[0][lastIndex], -1);
        m.setMarkColor(0x0000ff, Chart.Transparent, Chart.Transparent);
        m.setDrawOnTop(false);

        // We also add a symbol and a label for each data series at the latest position
        for (int i = 1; i < allArraysB.length; ++i)
        {
            // Add the symbol
            Layer layer = c.addScatterLayer(new double[] { allArraysB[0][lastIndex] }, new double[] {
                allArraysB[i][lastIndex] }, "", Chart.CircleSymbol, 9, colors[i - 1], colors[i - 1]);
            layer.moveFront();

            // Add the label
            String label = "<*font,bgColor=" + Integer.toHexString(colors[i - 1]) + "*> {value|P4} <*/font*>";
            layer.setDataLabelFormat(label);

            // The label style               
            ChartDirector.TextBox t = layer.setDataLabelStyle("Arial Bold", 10, 0xffffff);
            boolean isOnLeft = allArraysB[0][lastIndex] <= timeRange / 2;
            t.setAlignment(isOnLeft ? Chart.Left : Chart.Right);
            t.setMargin(isOnLeft ? 5 : 0, isOnLeft ? 0 : 5, 0, 0);
        }

        //================================================================================
        // Configure axis scale and labelling
        //================================================================================

        c.xAxis().setLinearScale(0, timeRange);

        // For the automatic axis labels, set the minimum spacing to 75/40 pixels for the x/y axis.
        c.xAxis().setTickDensity(75);
        c.yAxis().setTickDensity(40);

        // Set the auto-scale margin to 0.05, and the zero affinity to 0.6
        c.yAxis().setAutoScale(0.05, 0.05, 0.6);

        //================================================================================
        // Output the chart
        //================================================================================

        viewer.setChart(c);
    }
}
