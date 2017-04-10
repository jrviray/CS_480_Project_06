package cpp.edu.cs480.project06;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by wxy03 on 4/9/2017.
 */
public class Animator {

    private Pane mainPane;

    private GraphicNode[] hashTable;

    private final double UNIT_DISTANCE = GraphicNode.RADIUS * 3;

    private final double NULL_UNIT_DISTANCE = GraphicNode.RADIUS * 1.5;


    /**
     * default constructor
     * @param mainPane
     */
    public Animator(Pane mainPane)
    {
        this.mainPane=mainPane;
        hashTable=new GraphicNode[50];
    }


    /**
     * This method is used to generate a new Node with its unique ID#.
     * @param nodeValue
     *          the data value of the new node
     * @return
     *          the assigned ID# of the new node
     */
    public int generateNode(int nodeValue)
    {
        GraphicNode newNode = new GraphicNode(2*GraphicNode.RADIUS,2*GraphicNode.RADIUS,
                String.format("%04d",nodeValue));
        newNode.setColor(GraphicNode.RED);
        drawOnCanvas(newNode);
        int ID;
        for(ID=0;ID<hashTable.length;ID++)
        {
            if(getNode(ID)==null)
                break;
        }

        hashTable[ID]=newNode;
        if(ID==hashTable.length)
        {
            hashTable= Arrays.copyOf(hashTable,hashTable.length*2);
        }
        return ID;

    }

    /**
     * This method is used to draw an element on the canvas
     * @param node
     */
    private void drawOnCanvas(Node node)
    {
        mainPane.getChildren().add(node);
        node.toFront();
    }

    /**
     * This method is used to remove an element from the canvas
     * @param node
     */
    private void removeFromCanvas(Node node)
    {
        mainPane.getChildren().remove(node);
    }


    /**
     * This is an aiding method for generate an animation for movement of a {@link Circle} to the target position
     * @param target
     * @param x
     * @param y
     * @param onFinish
     * @return
     */
    private TranslateTransition movementTo(Circle target, double x, double y, EventHandler<ActionEvent> onFinish)
    {
        TranslateTransition movementTo = new TranslateTransition(Duration.seconds(1),target);
        movementTo.setToX(x);
        movementTo.setToY(y);
        movementTo.setOnFinished(onFinish);
        return movementTo;
    }

    /**
     * This is an aiding method for generate an animation for movement of a {@link GraphicNode} to the target position
     * @param target
     * @param x
     * @param y
     * @param onFinish
     * @return
     */
    private TranslateTransition movementTo(GraphicNode target,double x,double y,EventHandler<ActionEvent> onFinish)
    {
        return movementTo(target.getCircle(),x,y,onFinish);
    }

    /**
     * This is an aiding method for generate an animation for movement of a {@link GraphicNode} by a certain amount
     * of distance
     * @param target
     * @param x
     * @param y
     * @param onFinish
     * @return
     */
    private TranslateTransition movementBy(GraphicNode target, double x, double y, EventHandler<ActionEvent> onFinish)
    {
        TranslateTransition movementBy = new TranslateTransition(Duration.seconds(1),target.getCircle());
        movementBy.setByX(x);
        movementBy.setByY(y);
        movementBy.setOnFinished(onFinish);
        return movementBy;
    }


    /**
     * This is the aiding method thar creates an animation for position adjustment, it push
     * all the parents node that are less than this node move to left
     * @param parentNodeID
     * @return
     */
    private ParallelTransition leftAdjustment(int parentNodeID) {
        //make all the parents that are less than move to left

        ParallelTransition mainAnimation = new ParallelTransition();
        GraphicNode currentNode = getNode(parentNodeID);
        LinkedList<GraphicNode> movingQueue = new LinkedList<>();
        LinkedList<GraphicNode> unbindQueue = new LinkedList<>();

        while(true)
        {
            Boolean isCurrentNodeLeft = currentNode.isLeftChild();
            if(isCurrentNodeLeft==null)
                break;  //hit to the root node
            else if(!isCurrentNodeLeft) {
                unbindQueue.add(currentNode);
                movingQueue.add(currentNode.getParentNode());
            }
            currentNode=currentNode.getParentNode();
        }

        while (!movingQueue.isEmpty()) {
            GraphicNode movingNode=movingQueue.poll();
            GraphicNode unbindNode=unbindQueue.poll();
            movingNode.unbindParent();  //unbind the parent
            unbindNode.unbindParent();

            mainAnimation.getChildren().add(
                    movementBy(movingNode, -UNIT_DISTANCE, 0,
                            event -> {
                                movingNode.bindToParent(movingNode.getParentNode()); //rebind the parent
                                unbindNode.bindToParent(movingNode); //rebind the leftChild
                            }
                    ));
        }
        return mainAnimation;
    }

    /**
     * This is the aiding method thar creates an animation for position adjustment, it push
     * all the parents node that are greater than this node to the right
     * @param parentNodeID
     * @return
     */
    private ParallelTransition rightAdjustment(int parentNodeID) {

        ParallelTransition mainAnimation = new ParallelTransition();
        GraphicNode currentNode = getNode(parentNodeID);
        LinkedList<GraphicNode> movingQueue = new LinkedList<>();
        LinkedList<GraphicNode> unbindQueue = new LinkedList<>();

        while(true)
        {
            Boolean isCurrentNodeLeft = currentNode.isLeftChild();
            if(isCurrentNodeLeft==null)
                break;  //hit to the root node
            else if(isCurrentNodeLeft) {
                unbindQueue.add(currentNode);
                movingQueue.add(currentNode.getParentNode());
            }
            currentNode=currentNode.getParentNode();
        }

        while (!movingQueue.isEmpty()) {
            GraphicNode movingNode=movingQueue.poll();
            GraphicNode unbindNode=unbindQueue.poll();
            movingNode.unbindParent();  //unbind the parent
            unbindNode.unbindParent();

            mainAnimation.getChildren().add(
                    movementBy(movingNode, UNIT_DISTANCE, 0,
                            event -> {
                                movingNode.bindToParent(movingNode.getParentNode()); //rebind the parent
                                unbindNode.bindToParent(movingNode); //rebind the leftChild
                            }
                    ));
        }
        return mainAnimation;
    }



    /**
     * This method is to generate an animation for inserting root node
     * @param newNodeID
     * @return
     */
    public SequentialTransition insertRootAnimation(int newNodeID)
    {
        SequentialTransition mainAnimation = new SequentialTransition();
        GraphicNode newNode = getNode(newNodeID);
        newNode.setColor(GraphicNode.BLACK);
        TranslateTransition movementAnimation = movementTo(newNode,mainPane.getWidth()/2, newNode.getY(),
                event->{addNullNode(newNodeID,mainPane.getWidth()/2,newNode.getY());});
        mainAnimation.getChildren().add(movementAnimation);
        return mainAnimation;
    }

    /**
     * This method is to generate an animation for inserting node left
     * @param newNodeID
     * @param parentNodeID
     * @return
     */
    public SequentialTransition insertLeftAnimation(int newNodeID, int parentNodeID)
    {
        SequentialTransition mainAnimation = new SequentialTransition();
        ParallelTransition adjustmentAnimation = this.leftAdjustment(parentNodeID);
        GraphicNode parentNode=getNode(parentNodeID),
                    newNode=getNode(newNodeID);
        adjustmentAnimation.setOnFinished(actionEvent->{this.removeFromCanvas(parentNode.getLeftChild());  //remove the null node
            parentNode.setLeftChild(newNode); });  //create the link
        double targetX=parentNode.getX()-UNIT_DISTANCE,
                targetY=parentNode.getY()+UNIT_DISTANCE;
        mainAnimation.getChildren().addAll(
                                    this.highlightTraversal(parentNodeID), //add the highlight animation
                                    adjustmentAnimation, //add adjustment animation
                                    //add the movement animation
                                    this.movementTo(newNode,targetX,targetY,
                        actionEvent->{newNode.bindToParent(parentNode);  //when finished, bind with parent
                                    this.addNullNode(newNodeID,targetX,targetY);}   //add null children
                ));
        return mainAnimation;
    }

    /**
     * This method is to generate an animation for inserting node right
     * @param newNodeID
     * @param parentNodeID
     * @return
     */
    public SequentialTransition insertRightAnimation(int newNodeID, int parentNodeID)
    {
        SequentialTransition mainAnimation = new SequentialTransition();
        ParallelTransition adjustmentAnimation = this.rightAdjustment(parentNodeID);
        GraphicNode parentNode=getNode(parentNodeID),
                newNode=getNode(newNodeID);
        adjustmentAnimation.setOnFinished(actionEvent->{this.removeFromCanvas(parentNode.getRightChild());  //remove the null node
                                                parentNode.setRightChild(newNode); });  //create the link

        double targetX=parentNode.getX()+UNIT_DISTANCE,
                targetY=parentNode.getY()+UNIT_DISTANCE;
        PauseTransition addNullNode = new PauseTransition(Duration.ONE);
        addNullNode.setOnFinished(event -> addNullNode(newNodeID,targetX,targetY));
        mainAnimation.getChildren().addAll(
                this.highlightTraversal(parentNodeID), //add the highlight animation
                adjustmentAnimation, //add adjustment animation
                //add the movement animation
                this.movementTo(newNode, targetX, targetY,
                        actionEvent->{newNode.bindToParent(parentNode);}), //when finished, bind with parent
                addNullNode
                );
        return mainAnimation;

    }



    /**
     * This is an aiding method for insertion animation
     * @param parentNodeID
     * @return
     */
    private SequentialTransition highlightTraversal(int parentNodeID)
    {
        SequentialTransition mainAnimation = new SequentialTransition();
        GraphicNode currentNode = getNode(parentNodeID);

        // create a path from root to the parentNode in a stack
        Stack<GraphicNode> traversalStack = new Stack<>();
        while(currentNode!=null)
        {
            traversalStack.push(currentNode);
            currentNode=currentNode.getParentNode();
        }

        //get the root node
        GraphicNode nextNode = traversalStack.pop();
        //initialize the highlight circle to the root node
        Circle highlightCircle = createHighlightCircle(nextNode.getX(),nextNode.getY());

        //display the highlight circle
        PauseTransition drawCircle = new PauseTransition(Duration.ONE);
        drawCircle.setOnFinished(actionEvent->{drawOnCanvas(highlightCircle);});
        mainAnimation.getChildren().add(drawCircle);

        //create a sequence of animation to get to the target node
        while(!traversalStack.isEmpty()){
            nextNode=traversalStack.pop();
            mainAnimation.getChildren().addAll( new PauseTransition(Duration.seconds(.5f)),
                    //when reach a node, wait for a while
                    this.movementTo(highlightCircle,nextNode.getX(),nextNode.getY(),null)
                    //move th the next node
                   );
        }

        //remove the highlight circle from the canvas
        PauseTransition removeCircle = new PauseTransition(Duration.seconds(.5f));
        removeCircle.setOnFinished(actionEvent->{removeFromCanvas(highlightCircle);});
        mainAnimation.getChildren().add(removeCircle);

        return mainAnimation;

    }

    /**
     * This is an aiding method to create an Highlight circle but not put it on the canvas yet
     * @param x
     * @param y
     * @return
     */
    private Circle createHighlightCircle(double x,double y)
    {
        Circle highlightCircle = new Circle(GraphicNode.RADIUS);
        highlightCircle.setFill(new Color(0,0,0,0));
        highlightCircle.setStroke(GraphicNode.HIGHTLIGHT);
        highlightCircle.setStrokeType(StrokeType.OUTSIDE);
        highlightCircle.setStrokeWidth(5);
        highlightCircle.setTranslateX(x);
        highlightCircle.setTranslateY(y);
        return highlightCircle;
    }

    /**
     * This is an aiding method to add the null node for a specific node, and this
     * is not an animation
     * @param nodeID
     * @param startX
     * @param startY
     */
    private void addNullNode(int nodeID,double startX,double startY)
    {
        GraphicNode thisNode = getNode(nodeID);
        if(thisNode.getLeftChild()==null)
        {
            GraphicNode nullNode = new GraphicNode(startX-NULL_UNIT_DISTANCE,startY+UNIT_DISTANCE,"NULL");
            nullNode.setColor(GraphicNode.BLACK);
            nullNode.bindToParent(thisNode);
            thisNode.setLeftChild(nullNode);
            drawOnCanvas(nullNode);
        }
        if(thisNode.getRightChild()==null)
        {
            GraphicNode nullNode = new GraphicNode(startX+NULL_UNIT_DISTANCE,startY+UNIT_DISTANCE,"NULL");
            nullNode.setColor(GraphicNode.BLACK);
            nullNode.bindToParent(thisNode);
            thisNode.setRightChild(nullNode);
            drawOnCanvas(nullNode);

        }
    }

    /**
     * This method is to generate an animation for rotating node left
     * @param rotateNodeID
     * @return
     */
    public SequentialTransition rotateLeftAnimation(int rotateNodeID)
    {
        GraphicNode topNode = getNode(rotateNodeID);
        boolean isLeftChild = topNode.isLeftChild();
        GraphicNode parentNode = topNode.getParentNode();
        GraphicNode bottomNode = topNode.getRightChild();
        GraphicNode exchangeNode = bottomNode.getLeftChild();

        SequentialTransition mainAnimation = new SequentialTransition();
        topNode.highlightRightLink();
        PauseTransition highlight = new PauseTransition(Duration.seconds(2));
        highlight.setOnFinished(event -> {topNode.unhighlightRightLink();
        topNode.unbindParent();
        bottomNode.unbindParent();
        topNode.setRightChild(exchangeNode);
        bottomNode.setLeftChild(topNode);
        exchangeNode.unbindParent();
            if(parentNode!=null)
            {
                if(isLeftChild)
                    parentNode.setLeftChild(bottomNode);
                else
                    parentNode.setRightChild(bottomNode);
            }
            resetOverlay(topNode);
        });
        ParallelTransition rotate = new ParallelTransition();

        rotate.getChildren().addAll(movementBy(bottomNode,0,-UNIT_DISTANCE,event -> {bottomNode.bindToParent(parentNode);})
                                    ,movementBy(topNode,0,UNIT_DISTANCE,event -> {topNode.bindToParent(bottomNode);
                                                                                        exchangeNode.bindToParent(topNode);}));
        mainAnimation.getChildren().addAll(highlight,rotate);
        return mainAnimation;
    }

    private void resetOverlay(GraphicNode node)
    {
        node.toFront();
        if(node.getLeftChild()!=null)
            resetOverlay(node.getLeftChild());
        if(node.getRightChild()!=null)
            resetOverlay(node.getRightChild());

    }

    /**
     * This method is to generate an animation for rotating node right
     * @param rotateNodeID
     * @return
     */
    public SequentialTransition rotateRightAnimation(int rotateNodeID)
    {

        GraphicNode topNode = getNode(rotateNodeID);
        Boolean isLeftChild = topNode.isLeftChild();
        GraphicNode parentNode = topNode.getParentNode();
        GraphicNode bottomNode = topNode.getLeftChild();
        GraphicNode exchangeNode = bottomNode.getRightChild();

        SequentialTransition mainAnimation = new SequentialTransition();
        topNode.highlightLeftLink();
        PauseTransition highlight = new PauseTransition(Duration.seconds(2));
        highlight.setOnFinished(event -> {topNode.unhighlightLeftLink();
            topNode.unbindParent();
            bottomNode.unbindParent();
            topNode.setLeftChild(exchangeNode);
            bottomNode.setRightChild(topNode);
            exchangeNode.unbindParent();
            if(parentNode!=null)
            {
                if(isLeftChild)
                    parentNode.setLeftChild(bottomNode);
                else
                    parentNode.setRightChild(bottomNode);
            }
            resetOverlay(topNode);
        });
        ParallelTransition rotate = new ParallelTransition();

        rotate.getChildren().addAll(movementBy(bottomNode,0,-UNIT_DISTANCE,event -> {bottomNode.bindToParent(parentNode);})
                ,movementBy(topNode,0,UNIT_DISTANCE,event -> {topNode.bindToParent(bottomNode);
                    exchangeNode.bindToParent(topNode);}));
        mainAnimation.getChildren().addAll(highlight,rotate);
        return mainAnimation;
    }
//
//    /**
//     * This method is to generate an animation for recoloring
//     * @param nodeID
//     * @param color
//     * @return
//     */
//    public SequentialTransition recolorAnimation(Queue<Integer> nodeID, Queue<Boolean> color)
//    {
//
//    }
//
//    /**
//     * This method is to generate an animation for deletion
//     * @param deleteNodeID
//     * @return
//     */
//    public SequentialTransition deleteAnimation(int deleteNodeID)
//    {
//
//    }
//
//    /**
//     * This method is to generate an animation for swap the data
//     * @param swapNodeID_1
//     * @param swapNodeID_2
//     * @return
//     */
//    public SequentialTransition dataSwap(int swapNodeID_1,int swapNodeID_2)
//    {
//
//    }

    private GraphicNode getNode(int nodeID)
    {
        return hashTable[nodeID];
    }

}
