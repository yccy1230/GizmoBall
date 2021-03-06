package ui;

import constant.Constant;
import entity.base.AbstractCustomBody;
import listener.OperationListener;
import utils.ScreenUtils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.util.List;

/**
 * @description UI主界面
 * @author Jack Chen
 * @date 2017/11/21
 *
 */
public class MainFrame extends JFrame {

	private JPanel contentPane;
	private BoardPanel boardPanel;
	private MenuBar menuBar;
	private OperationPanel operationPanel;

	public MainFrame(OperationListener listener) {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(Constant.WINDOW_MAIN_WIDTH, Constant.WINDOW_MAIN_HEIGHT);
		Dimension midDimension = ScreenUtils.getScreenMidDimension(this);
		setLocation(midDimension.width, midDimension.height);

		menuBar = new MenuBar(listener);
		setJMenuBar(menuBar);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		boardPanel = new BoardPanel(listener);
		boardPanel.setLocation(10, 10);
		contentPane.add(boardPanel);

		operationPanel = new OperationPanel(listener);
		contentPane.add(operationPanel);
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_F5,0);
        InputMap inputMap = contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, KeyEvent.VK_F5);
        contentPane.getActionMap().put(KeyEvent.VK_F5, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("UP...UP");
                listener.onKeyPressed();
            }
        });
    }

	public void repaintBoardPanel(List<AbstractCustomBody> components){
		boardPanel.repaintBoard(components);
	}

	public int getComponentSize() {
		return operationPanel.getComponentSize();
	}
}
