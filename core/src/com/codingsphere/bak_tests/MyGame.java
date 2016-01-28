package com.codingsphere.bak_tests;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class MyGame implements ApplicationListener {

    private static String TAG = MyGame.class.toString();
    private static float PLATFORM_VELOCITY = 5f;

    OrthographicCamera cam;
    World world;
    Box2DDebugRenderer dDebugRenderer;

    Body ground;
    Body platform;

    Body circle;
    Body box;

    boolean spawnBoxes;

    @Override
    public void create() {
        cam = new OrthographicCamera(48, 32);
        cam.position.set(0, 15, 0);
        cam.update();

        Box2D.init();
        world = new World(new Vector2(0, -9.8f), true);
        dDebugRenderer = new Box2DDebugRenderer();

        spawnBoxes = false;
        //GROUND
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0,0);

        ground = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15, 1);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0f;

        ground.createFixture(fixtureDef);


        //CIRCLE
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 10);

        circle = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1);

        fixtureDef.shape = circleShape;
        fixtureDef.density = 10;
        fixtureDef.restitution = 1;

        circle.createFixture(fixtureDef);

        //BOX
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 15);

        box = world.createBody(bodyDef);

        bodyDef.bullet = false;
        shape.setAsBox(1, 1);

        fixtureDef.friction = 100f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.shape = shape;
        fixtureDef.density = 2;


        box.createFixture(fixtureDef);

        //PLATFORM
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(-10, 6);

        platform = world.createBody(bodyDef);

        shape.setAsBox(3, .5f);

        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.friction = 7;
        fixtureDef.restitution = 0;

        platform.createFixture(fixtureDef);

        Gdx.input.setInputProcessor(new MyInputProcessor());

        platform.setLinearVelocity(PLATFORM_VELOCITY, 0);
    }

    @Override
    public void render() {

        if(platform.getPosition().x > 10){
            platform.setLinearVelocity(-PLATFORM_VELOCITY, 0);
        }else if(platform.getPosition().x < -10){
            platform.setLinearVelocity(PLATFORM_VELOCITY, 0);
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        world.step(1/60f, 6, 2);
        dDebugRenderer.render(world, cam.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private void createGeometryAtPos(Vector2 pos) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(pos);

        Body body = world.createBody(bodyDef);
        if(spawnBoxes){
            createBoxAtPos(body);
        }else {
            createCircleAtPos(body);
        }

    }

    private void createBoxAtPos(Body body) {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 1f);
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.restitution = 0.2f;

        Gdx.app.log(TAG, "creating body with pos: " + body.getPosition());
        body.createFixture(fixtureDef);
    }

    private void createCircleAtPos(Body body){

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(1f);
        fixtureDef.shape = shape;
        fixtureDef.density = 5f;
        fixtureDef.restitution = 0.8f;

        Gdx.app.log(TAG, "creating body with pos: " + body.getPosition());
        body.createFixture(fixtureDef);
    }

    @Override
    public void resize(int width, int height) {

    }




    @Override
    public void dispose() {

    }

    public class MyInputProcessor implements InputProcessor{

        @Override
        public boolean keyDown(int keycode) {
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if(keycode == Input.Keys.S){
                spawnBoxes = !spawnBoxes;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            if(button == Input.Buttons.LEFT){
                Gdx.app.log(TAG, "onLeftUp");
                Gdx.app.log(TAG, "Pos:" + screenX + "/" + screenY);
                Vector3 worldPos = cam.unproject(new Vector3(screenX, screenY, 0));

                createGeometryAtPos(new Vector2(worldPos.x, worldPos.y));

                return true;
            }

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }


}
