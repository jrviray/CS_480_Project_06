package cpp.edu.cs480.project06;/**
 * Created by wxy03 on 4/9/2017.
 *
 * This class is only for testing the animator!!!
 */

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.LinkedList;

public class TestAnimationUI extends Application {

    private Scene mainScene;

    private Pane mainPane;

    private double mainPaneWidth= 1200;

    private double mainPaneHeight = 600;

    private Animator animator;

    private boolean isNullVisible;

    private DoubleProperty playRate;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
        primaryStage.setTitle("TestUI");
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }

    private void initialize()
    {
        BorderPane rootPane= new BorderPane();
        mainPane = new Pane();
        rootPane.setCenter(mainPane);
        mainPane.setPrefWidth(mainPaneWidth);
        mainPane.setPrefHeight(mainPaneHeight);
        mainScene = new Scene(rootPane);

        Button testButton = new Button("test");
        rootPane.setBottom(testButton);
        testButton.setOnMouseClicked(event -> test());
        Button booleanButton = new Button("boolean");

        isNullVisible=false;
        booleanButton.setOnMouseClicked(event -> {isNullVisible=!isNullVisible;animator.setNullNodeVisible(isNullVisible);});

        Slider rate = new Slider();
        rate.setMin(0);
        rate.setMax(1.5f);
        rate.setValue(1f);
        rate.setShowTickLabels(true);
        rootPane.setTop(booleanButton);
        rootPane.setLeft(rate);

        playRate=rate.valueProperty();
        animator = new Animator(mainPane,isNullVisible);


    }

    private void test()
    {
           testQueue = new LinkedList<>();
           testQueue.add(1);
           testQueue.add(2);
           testQueue.add(3);
           testQueue.add(4);
           testQueue.add(5);
           testQueue.add(6);
           testQueue.add(7);
        testQueue.add(8);
        testQueue.add(9);
           playAnimation(testQueue.poll());
    }



   LinkedList<Integer> testQueue;
    private void playAnimation(int type)
    {
        Animation thisAnimation = new PauseTransition(Duration.ZERO);
        switch (type)
        {
            case 1:
                animator.generateNode(type-1);
                thisAnimation=animator.insertRootAnimation(0);
                break;
            case 2:
                animator.generateNode(type-1);
                thisAnimation=animator.insertLeftAnimation(type-1,0);

                break;
            case 3:
                animator.generateNode(type-1);
                thisAnimation=animator.insertLeftAnimation(type-1,1);

                break;
            case 4:
                animator.generateNode(type-1);
                thisAnimation=animator.insertRightAnimation(type-1,0);

                break;
           case 5:
                animator.generateNode(type-1);
                thisAnimation=animator.insertRightAnimation(type-1,3);
                break;
            case 6:

                thisAnimation=animator.dataSwap(3,4);
                break;
            case 7:
                thisAnimation=animator.deleteAnimation(4);
                break;
            case 8:
                thisAnimation=animator.rotateLeftAnimation(0);
                break;
            case 9:
                thisAnimation=animator.rotateRightAnimation(0);
                break;


        }
        thisAnimation.rateProperty().bind(playRate);

        if(testQueue.isEmpty()) {
            thisAnimation.play();
            return;
        }
        else {
            Integer nextType = testQueue.poll();
            thisAnimation.setOnFinished(event -> playAnimation(nextType));
            thisAnimation.play();
        }
    }
}