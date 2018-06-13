package TestDemo;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.Date;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import Temperature.TemperaturePoint;
import Temperature.TemperaturePoints;



public class Main extends JFrame {

    private JPanel contentPane;
    private JTable table;
    private String head[]=null;
    private Object [][]data=null;
    private TemperaturePoint current=new TemperaturePoint();
    

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main frame = new Main();
                    frame.queryData();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public Main() {
        setResizable(false);
        
        setTitle("光纤传感温度监测v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 300);
        Dimension   us=this.getSize();
        Dimension them=Toolkit.getDefaultToolkit().getScreenSize();
              
              int   x=(them.width-us.width)/2;  
              int   y=(them.height-us.height)/2;   
              
              this.setLocation(x, y);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0,0,700,250);
        
        table = new JTable();
         
        table.setBorder(new LineBorder(new Color(0, 0, 0)));
        head=new String[] {
            "传感器", "时间", "波长", "温度", "是否超过阈值", "是否升温过快"
        };
        
        DefaultTableModel tableModel=new DefaultTableModel(queryData(),head){
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };
        table.setModel(tableModel);

        scrollPane.setViewportView(table);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
        );
        gl_contentPane.setVerticalGroup(
            gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
                    .addGap(66))
        );
        contentPane.setLayout(gl_contentPane);
        

    }
    
    //生成表格数据
    /**
     * @return
     */
    public Object[][] queryData(){
       TemperaturePoints tps=new TemperaturePoints(60);
       int len=60;//tps.buffer.size();
        data=new Object[len][head.length];

        for(int i=0;i<len;i++){
            for(int j=0;j<head.length;j++){
                data[i][0]=i;
                data[i][1]=(new Date()).toString();//tps.buffer.get(i).getTime();
                data[i][2]=30;//tps.buffer.get(i).getTemperature();
                data[i][3]="";
                data[i][4]="";
                data[i][5]="";
            }
        }
        return data;
    }

}