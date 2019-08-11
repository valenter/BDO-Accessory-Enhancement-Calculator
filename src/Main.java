/*  BDO Accessory enhancement cost and chance calculator.
    Copyright (C) 2019  Valenter

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.MaskFormatter;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class Main extends JFrame implements ActionListener {
	public static final String title = new String("BDO Accessory Enhancement Failed 1.1");
	
	//Enhancement chances
	public static final JLabel PRIchance = new JLabel("% PRI");
	public static final JLabel DUOchance = new JLabel("% DUO");
	public static final JLabel TRIchance = new JLabel("% TRI");
	public static final JLabel TETchance = new JLabel("% TET ");
	public static final JLabel PENchance = new JLabel("% PEN");
	
	//Menubar (Failstack calculator tool)
	public static final JMenuBar menubar = new JMenuBar();
	public static final JMenu tools = new JMenu("Tools");
	public static final JMenuItem fscalc = new JMenuItem("Failstack calculator");
	

    static MaskFormatter createFormatter(String format) {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter(format);
            formatter.setValidCharacters("0123456789\0");
            formatter.setPlaceholderCharacter('0');
        } catch (java.text.ParseException exc) {
            System.err.println("formatter is bad: " + exc.getMessage());
        }
        return formatter;
    }

    
    public static final MaskFormatter mask = createFormatter("##.##");
    public static final MaskFormatter marketmask = createFormatter("");

    
	public static final JFormattedTextField PRItxt = new JFormattedTextField(mask);
	public static final JFormattedTextField DUOtxt = new JFormattedTextField(mask);
	public static final JFormattedTextField TRItxt = new JFormattedTextField(mask);
	public static final JFormattedTextField TETtxt = new JFormattedTextField(mask);
	public static final JFormattedTextField PENtxt = new JFormattedTextField(mask);
	
	//Calculate button
	public static final JButton calculate = new JButton("Calculate");
	
	//Panels
	public static final JPanel toppanel = new JPanel();
	public static final JPanel centerpanel = new JPanel();
	public static final JPanel bottompanel = new JPanel();
	
	//Marketplace price
	public static final JLabel marketprice = new JLabel("Marketplace price ");
	
	public static final JTextField markettxt = new JTextField(10);
	
	public static final JLabel acclabel = new JLabel("# Accessori");
	public static final JTextField accsnumber = new JTextField(4);
	
	//Results text area
	public static final JTextArea resulttxt = new JTextArea(8,40);
	
	//Layouts
	public static final BorderLayout botborder = new BorderLayout();
	public static final FlowLayout flowchance = new FlowLayout();
	public static final FlowLayout flow2 = new FlowLayout();
	public static final BorderLayout border = new BorderLayout();
	
	
	public Main() {
		super(title);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		
		toppanel.setLayout(flowchance);
		toppanel.add(PRIchance);
		toppanel.add(PRItxt);
		toppanel.add(DUOchance);
		toppanel.add(DUOtxt);
		toppanel.add(TRIchance);
		toppanel.add(TRItxt);
		toppanel.add(TETchance);
		toppanel.add(TETtxt);
		toppanel.add(PENchance);
		toppanel.add(PENtxt);
		
		//txt.setSize
		PRItxt.setColumns(4);
		DUOtxt.setColumns(4);
		TRItxt.setColumns(4);
		TETtxt.setColumns(4);
		PENtxt.setColumns(4);
		
		centerpanel.setLayout(flow2);
		centerpanel.add(marketprice);
		centerpanel.add(markettxt);
		centerpanel.add(acclabel);
		centerpanel.add(accsnumber);
		
		//txtarea si allarga con la finestra
		bottompanel.setLayout(botborder);
		final JScrollPane scroll = new JScrollPane(resulttxt);
		bottompanel.add(scroll);
		
		//pannello che contiene i tre pannelli sopra(label/txt, txt e button)
		final JPanel panel2 = new JPanel();
		panel2.add(calculate);
		
		//Menubar
		tools.add(fscalc);
		menubar.add(tools);
		this.setJMenuBar(menubar);
		
		//tooltip
		accsnumber.setToolTipText("Numero di accessori che volete enhanceare, N.B: non considerare anche il numero di accessori necessari per enhancearli, i.e. se hai 10 accessori che vuoi provare a fare PRI, scrivi 10 e non 20");
		markettxt.setToolTipText("Costo dell'accessorio base sul marketplace");
		ToolTipManager.sharedInstance().setInitialDelay(30);
		ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		

		
		final JPanel panel1 = new JPanel();
		final BorderLayout panelborder = new BorderLayout();
		panel1.setLayout(panelborder);
		panel1.add(toppanel, panelborder.NORTH);
		panel1.add(centerpanel, panelborder.CENTER);
		panel1.add(panel2, panelborder.SOUTH);
		
		
		this.getContentPane().setLayout(border);
		this.getContentPane().add(panel1, border.NORTH);
		this.getContentPane().add(bottompanel, border.CENTER);
		
		//markettxt accetta solo int
		((AbstractDocument)markettxt.getDocument()).setDocumentFilter(new DocumentFilter(){
	        Pattern regEx = Pattern.compile("\\d*");

	        @Override
	        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {          
	            Matcher matcher = regEx.matcher(text);
	            if(!matcher.matches()){
	                return;
	            }
	            super.replace(fb, offset, length, text, attrs);
	        }
	    });
		
		//accsnumber accetta solo int
				((AbstractDocument)accsnumber.getDocument()).setDocumentFilter(new DocumentFilter(){
			        Pattern regEx = Pattern.compile("\\d*");

			        @Override
			        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {          
			            Matcher matcher = regEx.matcher(text);
			            if(!matcher.matches()){
			                return;
			            }
			            super.replace(fb, offset, length, text, attrs);
			        }
			    });
		
		resulttxt.setEditable(false);
		this.setSize(800,400);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                           
		
		calculate.addActionListener(this);
		fscalc.addActionListener(this);
	}
	

	public static void openWebpage(String urlString) {
		try {
			Desktop.getDesktop().browse(new URL(urlString).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == calculate) {
			float prichance, duochance, trichance, tetchance, penchance;
			
			prichance = Float.parseFloat(PRItxt.getText())/100;
			duochance = Float.parseFloat(DUOtxt.getText())/100;
			trichance = Float.parseFloat(TRItxt.getText())/100;
			tetchance = Float.parseFloat(TETtxt.getText())/100;
			penchance = Float.parseFloat(PENtxt.getText())/100;
			
			float n, npri, nduo, ntri, ntet, npen;
			int enhanceaccspen, enhanceaccstet;
			
			//Necessario solo per la media su 100
			float nn, nnpri, nnduo, nntri, nntet, nnpen;
			int average100enhanceaccspen;
			nn = 100;
			nnpri = nn - (nn*(1-prichance));
			nnduo = nnpri - (nnpri * (1-duochance));
			nntri = nnduo - (nnduo * (1-trichance));
			nntet = nntri - (nntri * (1-tetchance));
			nnpen = nntet - (nntet * (1-penchance));
			average100enhanceaccspen = (int) Math.round(nn + nnpri + nnduo + nntri + nntet);
			
			//media con numero inserito
			n = Float.parseFloat(accsnumber.getText());
			npri = n - (n*(1-prichance));
			nduo = npri - (npri * (1-duochance));
			ntri = nduo - (nduo * (1-trichance));
			ntet = ntri - (ntri * (1-tetchance));
			npen = ntet - (ntet * (1-penchance));
			enhanceaccspen = (int) Math.round(n + npri + nduo + ntri + ntet);
			enhanceaccstet = (int) Math.round(n + npri + nduo + ntri);
			
			System.out.println("npen: " + npen);
			System.out.println("ntet: " + ntet);
			System.out.println("ntri: " + ntri);
			System.out.println("nduo: " + nduo);
			
			//Equazioni per ottenere il numero di accessori di partenza per avere 1 pen o 1 tet
			//Non calcola gli accessori usati per enhanceare quelli di partenza
			//NON toccare
			String averageaccsequation = new String("Solve(((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) - ((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) * (1-"+tetchance+"))) - (((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) - ((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) * (1-"+tetchance+"))) * (1-"+penchance+"))==1,x)");
			String averageaccsequation2 = new String("Solve((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) - ((((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) - (((x - (x*(1-"+prichance+"))) - ((x - (x*(1-"+prichance+"))) * (1-"+duochance+"))) * (1-"+trichance+"))) * (1-"+tetchance+")) == 1,x)");
			
			
			ExprEvaluator util = new ExprEvaluator();
	        IExpr result = util.evaluate(averageaccsequation);
	        System.out.println(result.toString());
	        int accsforpen;
	        if(result.toString().equals("{}")) {
	        	accsforpen = 0;
	        }
	        else {
	        	String[] parsedarray = result.toString().split(">");
	 	        accsforpen = (int) Math.round(Float.parseFloat(parsedarray[1].toString().substring(0, 5)));
	        }
	        
	        ExprEvaluator util2 = new ExprEvaluator();
	        IExpr resulttet = util2.evaluate(averageaccsequation2);
	        System.out.println(resulttet.toString());
	        int accsfortet;
	        if(resulttet.toString().equals("{}")) {
	        	accsfortet = 0;
	        }
	        else {
	        	String[] parsedarray2 = resulttet.toString().split(">");
		        accsfortet = (int) Math.round(Float.parseFloat(parsedarray2[1].toString().substring(0, 5)));
	        }
	        
	        double marketcostpen = (enhanceaccspen+n)*Double.parseDouble((markettxt.getText()));
	        double marketcosttet = (enhanceaccstet+n)*Double.parseDouble((markettxt.getText()));
	        System.out.println(markettxt.getText());
	        System.out.println(marketcostpen);
	        
	        //calcoli costi medi 1 accessorio tet e 1 pen
	        float nav, navpri, navduo, navtri, navtet, navpen, navi, navipri, naviduo, navitri, navitet, navipen;
	        double averageenhanceaccs1pen, averageenhanceaccs1tet;
	        nav = accsforpen;
			navpri = nav - (nav*(1-prichance));
			navduo = navpri - (navpri * (1-duochance));
			navtri = navduo - (navduo * (1-trichance));
			navtet = navtri - (navtri * (1-tetchance));
			navpen = navtet - (navtet * (1-penchance));
			
	        navi = accsfortet;
			navipri = navi - (navi*(1-prichance));
			naviduo = navipri - (navipri * (1-duochance));
			navitri = naviduo - (naviduo * (1-trichance));
			navitet = navitri - (navitri * (1-tetchance));
			navipen = navitet - (navitet * (1-penchance));
			
			averageenhanceaccs1pen = (int) Math.round(nav + navpri + navduo + navtri + navtet);
			averageenhanceaccs1tet = (int) Math.round(navi + navipri + naviduo + navitri);
	        double averagecost1pen = (averageenhanceaccs1pen+nav)*Double.parseDouble((markettxt.getText()));
	        double averagecost1tet = (averageenhanceaccs1tet+navi)*Double.parseDouble((markettxt.getText()));
			
			resulttxt.setText("La chance di ottenere un DUO senza fallire è del: " + 100*prichance*duochance+"%" + "\n" + "La chance di ottenere un TRI senza fallire è del: " + 100*prichance*duochance*trichance+"%" 
			+ "\n" + "La chance di ottenere un TET senza fallire è del: " + 100*prichance*duochance*trichance*tetchance+"%" 
					+ "\n" + "La chance di ottenere un PEN senza fallire è del: " + 100*prichance*duochance*trichance*tetchance*penchance+"%" + "\n" +
					"\n" + "In media, per ottenere un PEN, serviranno " + accsforpen + " accessori base di partenza (senza considerare quelli necessari per enhancearli)" + "\n"
					+ "In media, per ottenere un TET, serviranno " + accsfortet + " accessori base di partenza (senza considerare quelli necessari per enhancearli)" 
					+ "\n" + "\n" + "Partendo da " + nn + " accessori, in media vi ritroverete con " +((int)Math.round(nntri))+ " accessori/o TRI, " + ((int)Math.round(nntet))+ " accessori/o TET, e " + ((int)Math.round(nnpen)) + " accessori/o PEN, e"
					+ " vi serviranno " + average100enhanceaccspen + " accessori extra per enhancearli"
					+ "\n" + "\n" + "Partendo con " + accsnumber.getText() + " accessori, vi ritroverete con " + ((int)Math.round(npen)) + " accessori/o PEN, " +
					"che vi costerà/nno in media " + String.format("%.0f", marketcostpen) + " silver, considerando tutti gli accessori base necessari per enhancearli"
					+ "\n" + "O " + ((int)Math.round(ntet)) + " accessori/o TET, che vi costerà/nno in media " + String.format("%.0f", marketcosttet) + " silver, considerando tutti gli accessori base necessari per enhancearli"
					+ "\n" + "\n" + "Il costo medio per 1 accessorio PEN con queste chance, considerando tutto eccetto il costo delle failstacks, è di " +String.format("%.0f",averagecost1pen)+ " silver" + "\n" +
					"Il costo medio per 1 accessorio TET con queste chance, considerando tutto eccetto il costo delle failstacks, è di " +String.format("%.0f", averagecost1tet)+ " silver");
		}
		else if(e.getSource() == fscalc) {
			openWebpage("https://failstack-calculator.netlify.com/");
		}
	}
	
	
	public static void main(String[] args) {
		Main ciao = new Main();
	}

}


//TODO: commentare via i system.out.print
//		cambiare i costi e la parte "vi ritroverete con x accessori tet e pen" utilizzando float invece di int approssimati per dei calcoli più precisi
//      link all'enhancement chance calculator?
//		formatting?(maybe) 
