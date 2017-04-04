package cpp.edu.cs480.project06;
/**
 * Created by xinyuan_wang on 4/3/17.
 */


import javafx.animation.TranslateTransition;
import javafx.application.Application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GUI extends Application {

    private Scene mainScene;

    private double mainPaneWidth= 1200;

    private double mainPaneHeight = 600;

    private Button inserButton,
            deleButton,
            fixButton,
            saveButton,
            loadButton;

    /**
     * This is the main Pane where the animation of RB Tree happens
     */
    private Pane mainPane;


    private TextField inputValue;

    private BorderPane rootPane;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        initialize();
        primaryStage.setTitle("RedBlackTreeVisualization");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        test();

    }

    /**
     * This method initialize the user interface of this application
     */
    private void initialize()
    {
        rootPane=new BorderPane();

        mainScene = new Scene(rootPane);


        inputValue=new TextField();
        inputValue.setPrefWidth(50f);

        inserButton=new Button("Add");
        inserButton.setOnMouseClicked((event -> addNode()));
        // add an action on add button
        deleButton=new Button("Delete");
        fixButton= new Button("Fix");
        fixButton.setDisable(true);
        saveButton=new Button("Save tree");
        loadButton=new Button("Load tree");

        Label inputLabel= new Label("Integer input:");
        inputLabel.setFont(Font.font(15f));


        HBox leftPane = new HBox(20f);
        leftPane.setPadding(new Insets(20f,20f,20f,20f));
        leftPane.setAlignment(Pos.BASELINE_LEFT);
        HBox rightPane = new HBox(20f);
        rightPane.setPadding(new Insets(20f,20f,20f,20f));
        rightPane.setAlignment(Pos.BASELINE_RIGHT);

        BorderPane topPane = new BorderPane();
        topPane.setLeft(leftPane);
        topPane.setRight(rightPane);

        leftPane.getChildren().addAll(inputLabel,inputValue,inserButton,
                deleButton,fixButton);
        rightPane.getChildren().addAll(saveButton,loadButton);

        rootPane.setTop(topPane);
        //done setting up the top part of the UI

        mainPane=new Pane();
        mainPane.setPadding(new Insets(20,20,20,20));


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(mainPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(mainPaneHeight);
        scrollPane.setPrefViewportWidth(mainPaneWidth);


        rootPane.setCenter(scrollPane);
    }

    /**
     * This a temporary method for testing
     */
    private void test()
    {

    }


    private void addNode()
    {
        inserButton.setDisable(true);
        deleButton.setDisable(true);
        // the add button and delete button is disable before the animation ends
        try{
            int key=Integer.parseInt(inputValue.getText());
            GraphicNode newNode = new GraphicNode(40f,40f,key);
            mainPane.getChildren().addAll(newNode.circle,newNode.keyText);
            //the Node is ready on the left top corner

            //here inform the backend to do the insertion and ask a node to return

            insertNodeAnimation(newNode);
            //here begins the animation



        }
        catch (NumberFormatException e)
        {
            System.out.println("Invalid Input");
        }

    }


    private void insertNodeAnimation(GraphicNode currentNode)
    {
        boolean animationDone=false;
        TranslateTransition tt = new TranslateTransition(Duration.seconds(3f),currentNode.circle);
        tt.setToX(mainPane.getWidth()/2);
        tt.setOnFinished(event -> {fixButton.setDisable(false);});
        tt.play();


    }

}
