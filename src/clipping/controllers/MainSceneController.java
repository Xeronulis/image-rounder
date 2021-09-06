package clipping.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador principal de la escena, encargado de ajustar el clipping
 * 
 * @author Xeronulis
 *
 */

//TODO terminar las opciones extra

public class MainSceneController<T extends BaseController<?>> extends BaseController<T> {
	
	private boolean selectedImage;
	
	private File inputPath;
	private File outputPath;
	
	@FXML
	private ExtraOptionsController<MainSceneController<?>> extraOptionsController;
	private GridPane extraOptionsPane;
	private FadeTransition extraOptionsShow;
	private FadeTransition extraOptionsHide;
	
	private boolean playingExtraOptionsAnim;
	@FXML
	private Label msgLbl;
	private TranslateTransition moveUpLbl;
	private TranslateTransition moveDownLbl;
	private FadeTransition showLbl;
	private FadeTransition hideLbl;
	
	
	@FXML
	private AnchorPane pane;
	
	@FXML
	private Circle clipRef;
	private Circle clipRefBorder;
	private double clipRefRelx;
	private double clipRefRely;
	private double minRadius = 30;
	
	private Shape clip;
	private double clipMultiplier;
	
	@FXML
	private Circle clipRadius;
	private double clipRadRelx;
	private double clipRadRely;
	
	
	@FXML
	private FlowPane bottomMenu;
	
	@FXML
	private ImageView imageView;
	
	@FXML
	private Rectangle toClip;
	
	@FXML
	private Button okBtn;
	
	@FXML
	private Button cancelBtn;
	
	@FXML
	private Button configBtn;
	
	@FXML
	private Button extraOptionsBtn;
	
	@FXML
	public void initialize() {
		
		extraOptionsPane = extraOptionsController.getPane();
		
		inputPath = null;
		outputPath = null;
		
		selectedImage = false;
		
		clipRefBorder = new Circle();
		clipRefBorder.setStroke(Color.GHOSTWHITE);
		clipRefBorder.setFill(Color.TRANSPARENT);
		clipRefBorder.setId("clipRefBorder");
		
		pane.getChildren().add(2, clipRefBorder);
		
		updateAnimations();
		changeClipVisibility(false);
		
	}
	
	private void updateAnimations() {
		
		playingExtraOptionsAnim = false;
		
		extraOptionsShow = new FadeTransition();
		extraOptionsShow.setNode(extraOptionsPane);
		extraOptionsShow.setDuration(Duration.millis(500));
		extraOptionsShow.setInterpolator(Interpolator.EASE_BOTH);
		extraOptionsShow.setFromValue(0);
		extraOptionsShow.setToValue(extraOptionsPane.getOpacity());
		extraOptionsShow.setOnFinished(w->{
			playingExtraOptionsAnim = false;
		});
		
		extraOptionsHide = new FadeTransition();
		extraOptionsHide.setNode(extraOptionsPane);
		extraOptionsHide.setDuration(Duration.millis(500));
		extraOptionsHide.setInterpolator(Interpolator.EASE_BOTH);
		extraOptionsHide.setFromValue(extraOptionsPane.getOpacity());
		extraOptionsHide.setToValue(0);
		extraOptionsHide.setOnFinished(w-> {
			extraOptionsPane.setVisible(false);
			playingExtraOptionsAnim = false;
		});
		
		
		moveUpLbl = new TranslateTransition();
		moveUpLbl.setNode(msgLbl);
		moveUpLbl.setToY(-extraOptionsPane.getPrefHeight()-20);
		moveUpLbl.setInterpolator(Interpolator.EASE_BOTH);
		moveUpLbl.setDuration(Duration.millis(500));
		
		
		moveDownLbl = new TranslateTransition();
		moveDownLbl.setNode(msgLbl);
		moveDownLbl.setToY(0);
		moveDownLbl.setInterpolator(Interpolator.EASE_BOTH);
		moveDownLbl.setDuration(Duration.millis(500));
		
		
		hideLbl = new FadeTransition();
		hideLbl.setNode(msgLbl);
		hideLbl.setDuration(Duration.millis(500));
		hideLbl.setInterpolator(Interpolator.EASE_BOTH);
		hideLbl.setOnFinished(w-> msgLbl.setVisible(false));
		hideLbl.setDelay(Duration.millis(1000));
		hideLbl.setFromValue(bottomMenu.getOpacity());
		hideLbl.setToValue(0);
		
		showLbl = new FadeTransition();
		showLbl.setNode(msgLbl);
		showLbl.setDuration(Duration.millis(500));
		showLbl.setInterpolator(Interpolator.EASE_BOTH);
		showLbl.setOnFinished(w-> hideLbl.play());
		showLbl.setFromValue(0);
		showLbl.setToValue(bottomMenu.getOpacity());
	}
	
	
	/**
	 * cambia la visibilidad de los circulos relacionados al clipping
	 * @param visible
	 */
	private void changeClipVisibility(boolean visible) {
		
		clipRef.setVisible(visible);
		clipRadius.setVisible(visible);
		clipRefBorder.setVisible(visible);
		
		
	}
	
	/**
	 * actualiza el clip con la posicion y el tamaño del clipRef
	 */
	private void updateClip() {
		
		clipRefBorder.setRadius(clipRef.getRadius());
		clipRefBorder.setLayoutX(clipRef.getLayoutX());
		clipRefBorder.setLayoutY(clipRef.getLayoutY());
		
		clip = Shape.subtract(toClip, clipRef);
		toClip.setClip(clip);
		
		
		
	}
	
	/**
	 * Ajusta la imagen al tamaño de la escena, asumiendo que la escena tiene la misma proporcion que la imagen
	 */
	
	private void adjustImage() {
		
		double width = pane.getPrefWidth();
		double height = pane.getPrefHeight();
		
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		
		imageView.setLayoutX(0);
		imageView.setLayoutY(0);
		
	}
	
	
	
	/**
	 * Ajusta la ventana al tamaño de la pantalla * 0.8, manteniendo la proporcion que tiene la imagen;
	 * 
	 */
	private void setSceneSize() {
		
		Stage stage = (Stage) (pane.getScene().getWindow());
		
		Image img = imageView.getImage();
		
		double imgx = img.getWidth();
		double imgy = img.getHeight();
		
		
		double relation = imgx / imgy;
		
		Rectangle2D screenSize = Screen.getPrimary().getBounds();
		
		double maxx = screenSize.getWidth()*0.8;
		double maxy = screenSize.getHeight()*0.8;
		
		boolean yLower = maxy < maxx ? true : false;
		
		pane.setMaxSize(maxx, maxy);
		
		
		double prefx = 1;
		double prefy = 1;
		
		
		if(yLower) {
				prefy = maxy;
				prefx = prefy * relation;
		}else {
				prefx = maxx;
				prefy = prefx * relation;

		}
		
		clipMultiplier = imgx / prefx;
		
		
		pane.setPrefSize(prefx, prefy);
		toClip.setHeight(prefy);
		toClip.setWidth(prefx);
		
		clipRef.setRadius(minRadius);
		clipRef.setLayoutX(prefx/2);
		clipRef.setLayoutY(prefy/2);
		
		clipRadius.setRadius(10);
		clipRadius.setLayoutX(clipRef.getLayoutX());
		clipRadius.setLayoutY(clipRef.getLayoutY()-minRadius);
		
		stage.sizeToScene();
		stage.centerOnScreen();
	}
	
	
	/**
	 * Actualiza la posicion del circulo pequeño usando la distancia que ha recorrido el circulo mayor
	 * 
	 * @param e MouseEvent, por si es que en un futuro se necesita 
	 * @param distancex la distancia en el eje x que ha recorrido el circulo mayor
	 * @param distancey la distancia en el eje y que ha recorrido el circulo mayor
	 */
	
	private void updateClipRadiusPos(MouseEvent e, double distancex, double distancey) {
		
		clipRadius.setLayoutX(clipRadius.getLayoutX()-distancex);
		clipRadius.setLayoutY(clipRadius.getLayoutY()-distancey);
		
	}
	
	
	/**
	 * funcion que verifica la posicion del circulo mayor usando la funcion vinculada {@link #checkClipRefAction(MouseEvent)}
	 * 
	 * @param e MouseEvent, por si acaso
	 * @see #checkClipRefAction(MouseEvent)
	 */
	private void checkClipRefPos(MouseEvent e) {
		checkClipRefAction(e);
	}
	
	
	/**
	 * funcion que verifica el tamaño del circulo mayor usando la funcion vinculada {@link #checkClipRefAction(MouseEvent)}
	 * y tambien ajusta la posicion del circulo menor para que siga el radio del circulo mayor
	 * 
	 * @param e MouseEvent, por si acaso
	 * @see #checkClipRefAction(MouseEvent)
	 */
	private void checkClipRefSize(MouseEvent e) {
		checkClipRefAction(e);
		updateClipRadiusSize(e);
		angleDragClipRadius(e);
	}
	
	
	
	/**
	 * funcion que es ejecutada por {@link #checkClipRefPos(MouseEvent)} y {@link #checkClipRefSize(MouseEvent)}
	 * comprueba tanto el tamaño como la posicion del circulo mayor;
	 * si la posicion sobrepasa los limites de la escena, se mueve al circulo mayor devuelta a la misma;
	 * si el tamaño sobrepasa ya sea el alto o ancho de la escena, se le aplica el tamaño del eje menor de la escena
	 * 
	 * @param e MouseEvent, por si acaso
	 */
	private void checkClipRefAction(MouseEvent e) {
		
		double sceneWidth = pane.getWidth();
		double sceneHeight = pane.getHeight();
		
		double radius = clipRef.getRadius();
		double circunference = radius*2;
		
		double clipRefTopPos = clipRef.getLayoutY()-radius;
		double clipRefBottomPos = clipRef.getLayoutY()+radius;
		double clipRefRightPos = clipRef.getLayoutX()+radius;
		double clipRefLeftPos = clipRef.getLayoutX()-radius;
		
		
		if(circunference > sceneWidth || circunference > sceneHeight) clipRef.setRadius(Math.min(sceneHeight, sceneWidth)/2);
		else {
			if(clipRefLeftPos < 0) {
				clipRef.setLayoutX(radius); 
			}
			if(clipRefRightPos > sceneWidth) {
				clipRef.setLayoutX(sceneWidth-radius); 
			}
			if(clipRefTopPos < 0) {
				clipRef.setLayoutY(radius);
			}
			if(clipRefBottomPos > sceneHeight) {
				clipRef.setLayoutY(sceneHeight-radius);
			}
			
		}
		
		updateClip();
		
		
	}
	
	/**
	 * actualiza el tamaño del circulo menor en proporcion al tamaño del circulo mayor, aplicando un limite para
	 * evitar que el circulo menor sea demasiado pequeño
	 * 
	 * @param e MouseEvent, por si acaso
	 */
	private void updateClipRadiusSize(MouseEvent e) {
		
		double radius = clipRef.getRadius()/6;
		radius = radius > 10? radius : 10;
		
		clipRadius.setRadius(radius);
		
	}
	
	/**
	 * mueve al circulo menor siguiendo al mouse, pero solo por el radio del circulo mayor
	 * @param e
	 */
	
	private void angleDragClipRadius(MouseEvent e) {
		
		double hypotenuse = clipRef.getRadius();
		
		double refx = clipRef.getLayoutX();
		double refy = clipRef.getLayoutY();
		
		double mousex = e.getSceneX()-refx;
		double mousey = e.getSceneY()-refy;
		double mouseHypo = Math.hypot(mousex, mousey);
		
		double radians = Math.acos(mousex/mouseHypo);
		
		if(mousey<0) radians = Math.PI*2-(radians);
		
		System.out.println(radians);
		
		double catetY = (Math.sin(radians) * hypotenuse);
		double catetX = (Math.cos(radians) * hypotenuse);
		
		clipRadius.setLayoutX(catetX+refx);
		clipRadius.setLayoutY(catetY+refy);
		
		
	}
	
	/**
	 * actualiza el tamaño del circulo mayor dependiendo de la distancia del circulo menor al centro del mayor,
	 * se le aplica un limite al tamaño del circulo mayor para evitar que sea muy pequeño
	 * 
	 * @param e MouseEvent, por si acaso
	 */
	private void updateClipRefRadius(MouseEvent e) {
		double tarX = Math.abs(clipRadius.getLayoutX() - clipRef.getLayoutX());
		double tarY = Math.abs(clipRadius.getLayoutY() - clipRef.getLayoutY());
		
		double hypotenuse = Math.hypot(tarX, tarY);
		
		if(hypotenuse <=minRadius) {
			hypotenuse = minRadius;
			
			angleDragClipRadius(e);
			
			
		}
		
		clipRef.setRadius(hypotenuse);
		
		updateClipRadiusSize(e);
		checkClipRefSize(e);
		
	}
	
	
	/**
	 * mueve al circulo menor siguiendo el mouse
	 * 
	 * @param e MouseEvent, de el se obtiene la informacion de las dimensiones de la escena
	 */
	public void dragClipRad(MouseEvent e) {
		//scene x and scene y
		double sx = e.getSceneX();
		double sy = e.getSceneY();
	
		clipRadius.setLayoutX(sx-clipRadRelx);
		clipRadius.setLayoutY(sy-clipRadRely);
	
		updateClipRefRadius(e);
		
	}
	
	/**
	 * mueve al circulo mayor siguiendo al mouse
	 * 
	 * @param e MouseEvent, de el se obtiene la informacion de las dimensiones de la escena
	 */
	public void dragClipRef(MouseEvent e) {
		//scene x and scene y
		double sx = e.getSceneX();
		double sy = e.getSceneY();
		
		double disx = clipRef.getLayoutX();
		double disy = clipRef.getLayoutY();
		
		clipRef.setLayoutX(sx-clipRefRelx);
		clipRef.setLayoutY(sy-clipRefRely);
		
		
		checkClipRefPos(e);
		

		disx-= clipRef.getLayoutX();
		disy-= clipRef.getLayoutY();
		updateClipRadiusPos(e, disx, disy);
		
		
	}

	/**
	 * se usa con el circulo mayor
	 * actualiza la posicion de donde se origino el evento del mouse relativo al objeto que lo origino
	 * 
	 * 
	 * @param e MouseEvent, de el se extrae la posicion relativa
	 */
	public void updateRelRefPos(MouseEvent e) {
		clipRefRelx = e.getX();
		clipRefRely = e.getY();
		
	}
	
	
	/**
	 * se usa con el circulo menor
	 * actualiza la posicion de donde se origino el evento del mouse relativo al objeto que lo origino
	 * 
	 * @param e MouseEvent, de el se extrae la posicion relativa
	 */
	public void updateRelRadPos(MouseEvent e) {
		clipRadRelx = e.getX();
		clipRadRely = e.getY();
		
	}

	
	
	/**
	 * se abre el explorador de windows para seleccionar la carpeta donde se guardara la imagen recortada
	 * @param e
	 */
	public void okBtnAction(ActionEvent e) {
		
		if(selectedImage) {
			DirectoryChooser chooser = new DirectoryChooser();
			
			chooser.setTitle("Guardar imagen");
			
			if(outputPath != null) chooser.setInitialDirectory(outputPath);
			
			Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
			
			File selectedDirectory = chooser.showDialog(stage);
			
			if(selectedDirectory != null) {
				outputPath = selectedDirectory;
				
				saveImage(selectedDirectory);
			}
		}
		
		
	}
	
	/**
	 * guarda la imagen en el directorio seleccionado, con el nombre conformado por "img" seguido de la fecha
	 * actual y la hora en milisegundos para evitar conflictos de nombre
	 * 
	 * @param directory
	 */
	private void saveImage(File directory) {
		SnapshotParameters snap = new SnapshotParameters();
		snap.setFill(Color.TRANSPARENT);
		
		WritableImage wimg = null;
		
		Circle circle = new Circle();
		circle.setRadius(clipRef.getRadius()*clipMultiplier);
		circle.setLayoutX(clipRef.getLayoutX()*clipMultiplier);
		circle.setLayoutY(clipRef.getLayoutY()*clipMultiplier);
		
		
		ImageView fullImageView = new ImageView(imageView.getImage());
		
		fullImageView.setClip(circle);
		
		ImageView outputImgView = new ImageView(fullImageView.snapshot(snap, wimg));
		
		String outputSize = extraOptionsController.getLabelValue();
		
		if(!outputSize.contentEquals("Auto")){
			int size = Integer.parseInt(outputSize);
			outputImgView.setFitHeight(size);
			outputImgView.setFitWidth(size);
		}
		
		
		
		Image img = outputImgView.snapshot(snap, wimg);
		
		String location = directory.getAbsolutePath().concat("\\");
		String extension = "png";
		String name = "img"+java.time.LocalDate.now().toString() +" - " + String.valueOf(System.currentTimeMillis());
		
		
		File outputFile = new File(location.concat(name.concat("."+extension)));
		
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", outputFile);
			msgLbl.setText("Imagen guardada");
			showMsg();
			
			
		} catch (IOException ex) {
			msgLbl.setText("Error al guardar la imagen");
			showMsg();
			ex.printStackTrace();
		}
		
	}
	
	/**
	 * forma alternativa de cerrar la aplicacion
	 * @param e
	 */
	public void cancelBtnAction(ActionEvent e) {
		
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		
		stage.close();
		
		
	}
	/**
	 * se abre el explorador de windows para seleccionar una imagen
	 * 
	 * @param e
	 */
	public void configBtnAction(ActionEvent e) {
		
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Cargar imagen");
		
		List<String> filterList = new ArrayList<>();
		filterList.add("*.bmp");
		filterList.add("*.gif");
		filterList.add("*.jpg");
		filterList.add("*.jpeg");
		filterList.add("*.png");
		
		
		ExtensionFilter exFilter = new ExtensionFilter("Imagenes", filterList);
		
		chooser.getExtensionFilters().add(exFilter);
		if(inputPath != null) chooser.setInitialDirectory(inputPath);
		
		Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
		
		File imageSel = chooser.showOpenDialog(stage);
		
		
		if(imageSel != null) {
			
			String str = imageSel.getAbsolutePath();
			String regex = "\\\\(?!.*\\\\).*";
			str = str.replaceAll(regex, "");
			inputPath = new File(str);
			selectImage(imageSel);
			
		}
	}
	
	/**
	 * 
	 * 
	 * @param e
	 */
	public void showHideExtraOptions(ActionEvent e) {
		
		if(!playingExtraOptionsAnim) {
			playingExtraOptionsAnim = true;
			
			if(!extraOptionsPane.isVisible()) {
				extraOptionsPane.setVisible(true);
				extraOptionsShow.play();
				moveUpLbl.play();
			}else {
				extraOptionsHide.play();
				moveDownLbl.play();
			}
			
		}
		
		
		
	}
	
	/**
	 * se selecciona la imagen a recortar
	 * 
	 * @param directory
	 */
	private void selectImage(File directory) {
		
		selectedImage = true;
		String imgPath = "file:\\"+directory.getAbsolutePath();
		Image img = new Image(imgPath);
		imageView.setImage(img);
		
		changeClipVisibility(true);
		
		setSceneSize();
		adjustImage();
		updateClip();
		
	}
	
	/**
	 * muestra el label con el mensaje
	 */
	private void showMsg() {
		
		 msgLbl.setVisible(true);
		 showLbl.play();
		 
		
	}
	
	
	
}
