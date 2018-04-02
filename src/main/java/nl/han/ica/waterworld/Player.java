package nl.han.ica.waterworld;

import nl.han.ica.OOPDProcessingEngineHAN.Collision.CollidedTile;
import nl.han.ica.OOPDProcessingEngineHAN.Collision.ICollidableWithTiles;
import nl.han.ica.OOPDProcessingEngineHAN.Exceptions.TileNotFoundException;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.AnimatedSpriteObject;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.Sprite;
import nl.han.ica.waterworld.tiles.BoardsTile;
import processing.core.PVector;

import java.util.List;

/**
 * @author Ralph Niels
 * De spelerklasse (het paarse visje)
 */
public class Player extends AnimatedSpriteObject implements ICollidableWithTiles {

	private long previousMillis;
    final int size = 16;
    private int HP = 8;
    private final WaterWorld world;

    /**
     * Constructor
     * @param world Referentie naar de wereld
     */
    public Player(WaterWorld world) {
        super(new Sprite("src/main/java/nl/han/ica/waterworld/media/player.png"), 4);
        this.world = world;
        setCurrentFrameIndex(1);
        setFriction(0.15f);
    }

    @Override
    public void update() {
        if (getX()<=0) {
            setxSpeed(0);
            setX(0);
        }
        if (getY()<=0) {
            setySpeed(0);
            setY(0);
        }
        if (getX()>=world.getWidth()-size) {
            setxSpeed(0);
            setX(world.getWidth() - size);
        }
        if (getY()>=world.getHeight()-size) {
            setySpeed(0);
            setY(world.getHeight() - size);
        }

    }
    
    @Override
    public void keyPressed(int keyCode, char key) {
        final int speed = 8;
        /*
        if (keyCode == world.LEFT) {
            setDirectionSpeed(270, speed);
            setCurrentFrameIndex(0);
        }
        if (keyCode == world.UP) {
            setDirectionSpeed(0, speed);
        }
        if (keyCode == world.RIGHT) {
            setDirectionSpeed(90, speed);
            setCurrentFrameIndex(1);
        }
        if (keyCode == world.DOWN) {
            setDirectionSpeed(180, speed);
        }
        */
        if (key == 'a') {
            setDirectionSpeed(270, speed);
            animate(key);
            //setCurrentFrameIndex(0);
        }
        if (key == 'w') {
            setDirectionSpeed(0, speed);
        }
        if (key == 'd') {
            setDirectionSpeed(90, speed);
            animate(key);
           // setCurrentFrameIndex(1);
        }
        if (key == 's') {
            setDirectionSpeed(180, speed);
        }
        if (key == ' ') {
            System.out.println("Spatie!");
        }
    }


    @Override
    public void tileCollisionOccurred(List<CollidedTile> collidedTiles)  {
        PVector vector;
        int size;

        for (CollidedTile ct : collidedTiles) {
            if (ct.theTile instanceof BoardsTile) {
                if (ct.collisionSide == ct.TOP) {
                    try {
                        vector = world.getTileMap().getTilePixelLocation(ct.theTile);
                        setY(vector.y - getHeight());
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if (ct.collisionSide == ct.BOTTOM) {
                    try {
                        vector = world.getTileMap().getTilePixelLocation(ct.theTile);
                        size = world.getTileMap().getTileSize();
                        setY(vector.y + size);
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if (ct.collisionSide == ct.LEFT) {
                    try {
                        vector = world.getTileMap().getTilePixelLocation(ct.theTile);
                        setX(vector.x - getWidth());
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if (ct.collisionSide == ct.RIGHT) {
                    try {
                        vector = world.getTileMap().getTilePixelLocation(ct.theTile);
                        size = world.getTileMap().getTileSize();
                        setX(vector.x + size);
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                /*
                if (ct.collisionSide == ct.RIGHT) {
                    try {
                        vector = world.getTileMap().getTilePixelLocation(ct.theTile);
                        world.getTileMap().setTile((int) vector.x / 50, (int) vector.y / 50, -1);
                    } catch (TileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                */
            }
        }
    }
    
    private void animate(char key) {
    	if(key == 'a') {
    		//if(currentFrameIndex = )
    			setCurrentFrameIndex(1);
    	} else
    	if(key == 'd') {
    		setCurrentFrameIndex(2);
    	}
    }

	public int getHP() {
		return HP;
	}

	public void setHP(int hP) {
		HP = hP;
	}
    
    
}
