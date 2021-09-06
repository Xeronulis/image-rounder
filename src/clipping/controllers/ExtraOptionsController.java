package clipping.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class ExtraOptionsController<T extends BaseController<?>> extends BaseController<T> {

	@FXML
	private GridPane pane;
	
	@FXML
    private Label label;

    @FXML
    private Slider slider;
    
    @FXML
    public void initialize() {
    	pane.setVisible(false);
    }
    
    @FXML
    public void sliderActionKey(KeyEvent e) {
    	sliderAction();
    }
    
    @FXML
    public void sliderActionMouse(MouseEvent e) {
    	sliderAction();
    }
    
    private void sliderAction() {
    	
    	int value = (int) slider.getValue();
    	
    	label.setText(value <= slider.getMin() ? "Auto" : String.valueOf(value) );
    	
    }
	
    public String getLabelValue() {
    	return label.getText();
    }
    
    public GridPane getPane() {
    	return pane;
    }
	
}
