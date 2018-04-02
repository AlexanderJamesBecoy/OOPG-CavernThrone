package nl.han.ica.waterworld;

import java.io.UnsupportedEncodingException;

import com.sun.prism.image.ViewPort;
import nl.han.ica.OOPDProcessingEngineHAN.Dashboard.Dashboard;
import nl.han.ica.OOPDProcessingEngineHAN.Engine.GameEngine;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.*;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.SpriteObject;
import nl.han.ica.OOPDProcessingEngineHAN.Objects.TextObject;
import nl.han.ica.OOPDProcessingEngineHAN.Persistence.FilePersistence;
import nl.han.ica.OOPDProcessingEngineHAN.Persistence.IPersistence;
import nl.han.ica.OOPDProcessingEngineHAN.Sound.Sound;
import nl.han.ica.OOPDProcessingEngineHAN.Tile.TileMap;
import nl.han.ica.OOPDProcessingEngineHAN.Tile.TileType;
import nl.han.ica.OOPDProcessingEngineHAN.View.EdgeFollowingViewport;
import nl.han.ica.OOPDProcessingEngineHAN.View.View;
import nl.han.ica.cavernthrone.hud.*;
import nl.han.ica.waterworld.tiles.*;
import processing.core.PApplet;

/**
 * @author Ralph Niels
 */
@SuppressWarnings("serial")
public class WaterWorld extends GameEngine {
	
	private TextObject dashboardTextWeapon;
	private TextObject dashboardTextAmmo;
    private HudIcon dashboardIconHP;
    private HudIcon dashboardIconWeapon;
    private HudAnimatedIcon dashboardAnimatedIconHP;

    private Sound backgroundSound;
    private Sound bubblePopSound;
    
    private BubbleSpawner bubbleSpawner;
    private int bubblesPopped;
    private IPersistence persistence;
    private Player player;


    public static void main(String[] args) {
        WaterWorld w = new WaterWorld();
        w.runSketch();
    }

    /**
     * In deze methode worden de voor het spel
     * noodzakelijke zaken geïnitialiseerd
     */
    @Override
    public void setupGame() {

        int worldWidth=2560;
        int worldHeight=2560;

        initializeSound();
        createDashboard(640, 100);
        initializeTileMap();
        initializePersistence();

        createObjects();
        createBubbleSpawner();

        createViewWithoutViewport(worldWidth, worldHeight);
        createViewWithViewport(worldWidth, worldHeight, 640, 480, 1.0f);

    }

    /**
     * Creeërt de view zonder viewport
     * @param screenWidth Breedte van het scherm
     * @param screenHeight Hoogte van het scherm
     */
    private void createViewWithoutViewport(int screenWidth, int screenHeight) {
        View view = new View(screenWidth,screenHeight);
        //view.setBackground(loadImage("src/main/java/nl/han/ica/waterworld/media/background.jpg"));
        view.setBackground(loadImage("src/main/java/nl/han/ica/waterworld/media/Dark_Background.png"));
        setView(view);
        size(screenWidth, screenHeight);
    }

    /**
     * Creeërt de view met viewport
     * @param worldWidth Totale breedte van de wereld
     * @param worldHeight Totale hoogte van de wereld
     * @param screenWidth Breedte van het scherm
     * @param screenHeight Hoogte van het scherm
     * @param zoomFactor Factor waarmee wordt ingezoomd
     */
    private void createViewWithViewport(int worldWidth,int worldHeight,int screenWidth,int screenHeight,float zoomFactor) {
        EdgeFollowingViewport viewPort = new EdgeFollowingViewport(player, (int)Math.ceil(screenWidth/zoomFactor),(int)Math.ceil(screenHeight/zoomFactor), 1, 1);
        viewPort.setTolerance(240, 160, 240, 160);
        View view = new View(viewPort, worldWidth,worldHeight);
        setView(view);
        size(screenWidth, screenHeight);
        //view.setBackground(loadImage("src/main/java/nl/han/ica/waterworld/media/background.jpg"));
        view.setBackground(loadImage("src/main/java/nl/han/ica/waterworld/media/Dark_Background.png"));
    }

    /**
     * Initialiseert geluid
     */
    private void initializeSound() {
        //backgroundSound = new Sound(this, "src/main/java/nl/han/ica/waterworld/media/Waterworld.mp3");
        //backgroundSound.loop(-1);
        bubblePopSound = new Sound(this, "src/main/java/nl/han/ica/waterworld/media/pop.mp3");
    }


    /**
     * Maakt de spelobjecten aan
     */
    private void createObjects() {
        player = new Player(this);
        addGameObject(player, 100, 100);
        //Swordfish sf=new Swordfish(this);
        //addGameObject(sf,200,200);
    }

    /**
     * Maakt de spawner voor de bellen aan
     */
    public void createBubbleSpawner() {
        bubbleSpawner=new BubbleSpawner(this, bubblePopSound, 2);
    }

    /**
     * Maakt het dashboard aan
     * @param dashboardWidth Gewenste breedte van dashboard
     * @param dashboardHeight Gewenste hoogte van dashboard
     */
    private void createDashboard(int dashboardWidth,int dashboardHeight) {
    	int offset = 16;
    	int maxColor = 255;
    	String infinitySymbol = null;
    	try {
    	   infinitySymbol = new String(String.valueOf(Character.toString('\u221E')).getBytes("UTF-8"), "UTF-8");
    	} catch (UnsupportedEncodingException ex) {
    	    infinitySymbol = "?";
    	}
    	
        Dashboard dashboard = new Dashboard(0,0, dashboardWidth, dashboardHeight);
        dashboardTextWeapon = new TextObject("SMG", offset);
        dashboardTextWeapon.setForeColor(maxColor, maxColor, maxColor, maxColor);
        dashboardTextAmmo = new TextObject(infinitySymbol + "/" + infinitySymbol, offset);
        dashboardTextAmmo.setForeColor(maxColor, maxColor, maxColor, maxColor);
        
        Sprite spriteIconHP = new Sprite("src/main/java/nl/han/ica/waterworld/media/dashboard/Dashboard_Icon_HP.png");
        Sprite spriteIconWeapon = new Sprite("src/main/java/nl/han/ica/waterworld/media/dashboard/Dashboard_Icon_SMG.png");
        Sprite spriteAnimatedIconHP = new Sprite("src/main/java/nl/han/ica/waterworld/media/dashboard/Dashboard_Animated_Icon_HP.png");
        int HPOffset = spriteIconHP.getWidth() + offset;
        int weaponXOffset = dashboardWidth - spriteIconWeapon.getWidth() - offset;
        int weaponYOffset = spriteIconWeapon.getHeight() + offset;
        dashboardIconHP = new HudIcon(spriteIconHP);
        dashboardIconWeapon = new HudIcon(spriteIconWeapon);
        dashboardAnimatedIconHP = new HudAnimatedIcon(spriteAnimatedIconHP, 11);
        
        dashboard.addGameObject(dashboardIconHP, offset, offset);
        dashboard.addGameObject(dashboardAnimatedIconHP, HPOffset + offset, offset);
        dashboard.addGameObject(dashboardIconWeapon, weaponXOffset, offset);
        dashboard.addGameObject(dashboardTextWeapon, weaponXOffset, offset);
        dashboard.addGameObject(dashboardTextAmmo, weaponXOffset, weaponYOffset + offset);
        addDashboard(dashboard);
    }

    /**
     * Initialiseert de opslag van de bellenteller
     * en laadt indien mogelijk de eerder opgeslagen
     * waarde
     */
    private void initializePersistence() {
        persistence = new FilePersistence("main/java/nl/han/ica/waterworld/media/bubblesPopped.txt");
        if (persistence.fileExists()) {
            bubblesPopped = Integer.parseInt(persistence.loadDataString());
            refreshDasboardText();
        }
    }

    /** 
     * Initialiseert de tilemap
     */
    private void initializeTileMap() {
        /* TILES */
        Sprite floorsSprite = new Sprite("src/main/java/nl/han/ica/waterworld/media/tiles/Tile_Ground.png");
        Sprite wallsSprite = new Sprite("src/main/java/nl/han/ica/waterworld/media/tiles/Tile_Wall.png");
        Sprite floorShadowsSprite = new Sprite("src/main/java/nl/han/ica/waterworld/media/tiles/Tile_Ground_Shadow.png");
        Sprite wallAbovesSprite = new Sprite("src/main/java/nl/han/ica/waterworld/media/tiles/Tile_Wall_Above.png");
        Sprite cratesSprite = new Sprite("src/main/java/nl/han/ica/waterworld/media/tiles/Tile_Crate.png");       
        TileType<FloorsTile> FloorTileType = new TileType<>(FloorsTile.class, floorsSprite);
        TileType<WallsTile> WallTileType = new TileType<>(WallsTile.class, wallsSprite);
        TileType<FloorShadowsTile> FloorShadowTileType = new TileType<>(FloorShadowsTile.class, floorShadowsSprite);
        TileType<WallAbovesTile> WallAbovesTileType = new TileType<>(WallAbovesTile.class, wallAbovesSprite);
        TileType<CratesTile> CrateTileType = new TileType<>(CratesTile.class, cratesSprite);

        TileType[] tileTypes = { FloorTileType, WallTileType, FloorShadowTileType, WallAbovesTileType, CrateTileType };
        int tileSize = 16;
        int tilesMap[][] = {
                { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                { 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
        tileMap = new TileMap(tileSize, tileTypes, tilesMap);
    }

    @Override
    public void update() {
    	//initializeTileMap();
    }

    /**
     * Vernieuwt het dashboard
     */
    private void refreshDasboardText() {
    	dashboardAnimatedIconHP.setCurrentFrameIndex(0);
    }

    /**
     * Verhoogt de teller voor het aantal
     * geknapte bellen met 1
     */
    public void increaseBubblesPopped() {
        bubblesPopped++;
        persistence.saveData(Integer.toString(bubblesPopped));
        refreshDasboardText();
    }
}
