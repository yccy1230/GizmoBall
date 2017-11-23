package controller;

import constant.Constant;
import entity.*;
import entity.base.AbstractCustomBody;
import listener.UiListener;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import utils.Box2DUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
* @description 游戏控制器
* @author Jack Chen
* @date 2017/11/21
*/
public class GameController implements UiListener{
    private World world;
    private List<AbstractCustomBody> components;
    
    public GameController() {
        components = new ArrayList<>();
        //创建 重力加速度为10 的世界
        world = new World(new Vec2(0.0f,10.0f));
        //创建边界
        Box2DUtil.createBoarder(Constant.GRID_COUNT,Constant.GRID_COUNT,world);
    }

    /**
     * 判断当前位置是否为空
     * @param x 坐标x
     * @param y 坐标y
     * @param size size大小
     * @return
     */
    public boolean isEmpty(int x, int y, int size) {
        for (int i = x; i < x + size; i++) {
            for (int j = y; j < y + size; j++) {
                if (getComponent(i, j) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 根据坐标获取组件（第一个被添加的）
     * @param x 坐标x
     * @param y 坐标y
     * @return
     */
    private AbstractCustomBody getComponent(int x,int y) {
        for (int i = 0; i < components.size(); i++) {
            AbstractCustomBody comp = components.get(i);
            float size = comp.getSize()*Constant.RATE;
            float x1 = comp.getBody().getPosition().x*Constant.RATE-size;
            float y1 = comp.getBody().getPosition().y*Constant.RATE-size;
            if (x >= x1 && x < x1 + size*2 && y >= y1 && y < y1 + size*2){
                return comp;
            }
        }
        return null;
    }

    @Override
    public List<AbstractCustomBody> onItemAdd(Point point, int currentType,int size) {
        if(isEmpty(point.x, point.y,size)) {
            switch (currentType) {
                case Constant.COMPONENT_CIRCLE:
                    CircleBody circleBody = Box2DUtil.createCircle(point.x,point.y,size,true,world,Constant.COLOR_SQUARE);
                    components.add(circleBody);
                    break;
                case Constant.COMPONENT_TRIANGLE:
                    TriangleBody triangleBody = Box2DUtil.createTriangle(point.x,point.y,size,true,world,Constant.COLOR_SQUARE);
                    components.add(triangleBody);
                    break;
                case Constant.COMPONENT_SQUARE:
                    SquareBody squareBody = Box2DUtil.createSquare(point.x,point.y,size,world,Constant.COLOR_SQUARE);
                    components.add(squareBody);
                    break;
                case Constant.COMPONENT_TRAPEZOID:
                    TrapezoidBody trapezoidBody = Box2DUtil.createTrapezoidBody(point.x,point.y,size,true,world,Constant.COLOR_SQUARE);
                    components.add(trapezoidBody);
                    break;
                case Constant.COMPONENT_BALL:
                    Ball ball = Box2DUtil.createBall(point.x,point.y,world,Constant.COLOR_SQUARE);
                    components.add(ball);
                    break;
                case Constant.COMPONENT_ADVANCED_SQUARE:
                    AdvanceSquareBody advanceSquareBody = Box2DUtil.createAdvanceSquareBody(point.x,point.y,size,world,Constant.COLOR_SQUARE);
                    components.add(advanceSquareBody);
                    break;
                case Constant.COMPONENT_ELASTIC_PLATE:
                    ElasticPlateBody elasticPlateBody = Box2DUtil.createElasticPlateBody(point.x,point.y,size,world,Constant.COLOR_SQUARE);
                    components.add(elasticPlateBody);
                    break;
                case Constant.COMPONENT_LEFT_BAFFLE:
                    BaffleBody leftBaffleBody = Box2DUtil.createBaffleBody(point.x,point.y,size,Constant.COMPONENT_LEFT_BAFFLE,world,Constant.COLOR_SQUARE);
                    components.add(leftBaffleBody);
                    break;
                case Constant.COMPONENT_RIGHT_BAFFLE:
                    BaffleBody rightBaffleBody = Box2DUtil.createBaffleBody(point.x,point.y,size,Constant.COMPONENT_RIGHT_BAFFLE,world,Constant.COLOR_SQUARE);
                    components.add(rightBaffleBody);
                    break;
                case Constant.COMPONENT_ABSORBER:
                    AbsorberBody absorberBody = Box2DUtil.createAbsorber(point.x,point.y,size,world,Constant.COLOR_SQUARE);
                    components.add(absorberBody);
                    break;
                default:
                    break;
            }
        }
        return components;
    }

    @Override
    public World onOperationClicked(int type) {
        if(type == Constant.OPERATION_PLAY){
            Constant.DRAW_THREAD_FLAG = true;
        }else if(type == Constant.OPERATION_PAUSE){
            Constant.DRAW_THREAD_FLAG = false;
        }
        return world;
    }

    @Override
    public List<AbstractCustomBody> componentInfoProvider() {
        return components;
    }
}
