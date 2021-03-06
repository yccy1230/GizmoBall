package controller;

import constant.Constant;
import entity.*;
import entity.base.AbstractCustomBody;
import listener.UiListener;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import utils.Box2DUtil;
import utils.FileUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
* @description 游戏控制器
* @author Jack Chen
* @date 2017/11/21
*/
public class GameController implements UiListener, ContactListener {
    private World world;
    private List<AbstractCustomBody> components;
    private List<Body> destroyBodys;

    public GameController() {
        components = new ArrayList<>();
        destroyBodys= new ArrayList<>();
        //创建 重力加速度为10 的世界
        world = new World(new Vec2(0.0f,10.0f));
        world.setContactListener(this);
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
    public AbstractCustomBody isEmpty(int x, int y, int size) {
        for (int i = x; i < x + size; i++) {
            for (int j = y; j < y + size; j++) {
                AbstractCustomBody body = getComponent(i, j);
                if (body != null) {
                    return body;
                }
            }
        }
        return null;
    }

    /**
     * 清除component和屏幕图案
     */
    private void clearScreen(){
        for (AbstractCustomBody ab : components) {
            ab.destroy(world);
        }
        this.components.clear();
    }

    /**
     * 根据文件对象初始化screen及数据
     * @param comps
     */
    private void initScreen(List<SerializableObject> comps){
        clearScreen();
        for (SerializableObject obj : comps) {
            Point point = new Point((int) obj.getY(), (int) obj.getX());
            int type = obj.getType();
            generateComponent(point,type, (int) obj.getSize());
        }
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
            Body body = comp.getBody();
            if(body!=null){
                BodyData bd = (BodyData) body.getUserData();
                if(bd.getType()==Constant.COMPONENT_BALL) {
                    continue;
                }
                float x1 = comp.getBody().getPosition().x*Constant.RATE-size;
                float y1 = comp.getBody().getPosition().y*Constant.RATE-size;
                if (x >= x1 && x < x1 + size*2 && y >= y1 && y < y1 + size*2){
                    return comp;
                }
            }
        }
        return null;
    }

    @Override
    public List<AbstractCustomBody> onItemAdd(Point point, int currentType,int size) {
        AbstractCustomBody body = isEmpty(point.x, point.y,size);
        if (body != null) {
            switch (currentType) {
                case Constant.OPERATION_ROTATION:
                    body.rotation(world);
                    components.set(components.indexOf(body),body);
                    break;
                case Constant.OPERATION_DELETE:
                    body.destroy(world);
                    components.remove(body);
                    break;
                default:
                    break;
            }
        } else {
            generateComponent(point, currentType, size);
        }
        return components;
    }

    private void removeBody(Body bodyParam){
        if(bodyParam.getUserData()==null) { return; }
        BodyData bd = (BodyData) bodyParam.getUserData();
        long id = bd.getId();
        for (AbstractCustomBody body : components) {
            BodyData tmp = (BodyData) body.getBody().getUserData();
            if(tmp.getId()==id){
                components.remove(body);
                break;
            }
        }
    }

    /**
     * 根据控件类型生成控件（图案+模拟环境）
     * @param point 点坐标（左上角）
     * @param currentType 类型
     * @param size 大小（以单元格为单位）
     */
    private void generateComponent(Point point, int currentType, int size) {
        switch (currentType) {
            case Constant.COMPONENT_CIRCLE:
                CircleBody circleBody = Box2DUtil.createCircle(point.x,point.y,size,true,world,Constant.COLOR_SQUARE);
                components.add(circleBody);
                break;
            case Constant.COMPONENT_TRIANGLE:
                TriangleBody triangleBody = Box2DUtil.createTriangle(point.x,point.y,size,0,true,world,Constant.COLOR_SQUARE);
                components.add(triangleBody);
                break;
            case Constant.COMPONENT_SQUARE:
                SquareBody squareBody = Box2DUtil.createSquare(point.x,point.y,size,world,Constant.COLOR_SQUARE);
                components.add(squareBody);
                break;
            case Constant.COMPONENT_TRAPEZOID:
                TrapezoidBody trapezoidBody = Box2DUtil.createTrapezoidBody(point.x,point.y,size,0,true,world,Constant.COLOR_SQUARE);
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
                ElasticPlateBody elasticPlateBody = Box2DUtil.createElasticPlateBody(point.x,point.y,size,0,world,Constant.COLOR_SQUARE);
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

    @Override
    public World onOperationClicked(int type) {
        switch(type){
            case Constant.OPERATION_PLAY:
                Constant.DRAW_THREAD_FLAG = true;
                break;
            case Constant.OPERATION_PAUSE:
                Constant.DRAW_THREAD_FLAG = false;
                break;
            default:
                break;
        }
        return world;
    }

    @Override
    public List<AbstractCustomBody> componentInfoProvider() {
        return components;
    }

    @Override
    public void onMenuClicked(int type){
        if(type == Constant.MENUBAR_FILE_NEW){
            clearScreen();
        }else if(type == Constant.MENUBAR_FILE_EXIT){
            System.exit(0);
        }else if(type == Constant.MENUBAR_FILE_SAVE){
            FileUtils.writeToFile(components);
        }else if(type == Constant.MENUBAR_FILE_OPEN){
            initScreen(FileUtils.readFromFile());
        }
    }

    @Override
    public void onKeyPressed() {
        //编辑状态不做响应
        if(!Constant.DRAW_THREAD_FLAG) {
            return;
        }
        for (AbstractCustomBody body:components) {
            Integer type = body.getBodyType();
            if(type==null){ continue; }
            if(type==Constant.COMPONENT_LEFT_BAFFLE
                    ||type==Constant.COMPONENT_RIGHT_BAFFLE){
                body.applyAngularImpulse();
            }
        }
    }

    @Override
    public void onDestroy() {
        for (Body body: destroyBodys) {
            world.destroyBody(body);
            removeBody(body);
        }
        destroyBodys.clear();
    }

    @Override
    public void beginContact(Contact contact) {
        //吸收器
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        if(bodyA==null||bodyA.getUserData()==null
                ||bodyB==null||bodyB.getUserData()==null) {
            return;
        }
        BodyData bdb = (BodyData) bodyB.getUserData();
        BodyData bda = (BodyData) bodyA.getUserData();
        if(bda.getType()==Constant.COMPONENT_ABSORBER
                &&bdb.getType()==Constant.COMPONENT_BALL) {
                    destroyBodys.add(bodyB);
        }else if((bdb.getType()==Constant.COMPONENT_ABSORBER
                &&(bda.getType()==Constant.COMPONENT_BALL))){
            destroyBodys.add(bodyB);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

    }
}
