import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1550691097823471818L;
    public static final int WIDTH = 720, HEIGHT = 540;

    private Thread thread;
    private boolean isRunning = false;
    private Random r;
    private Handler handler;
    private HUD hud;
    private Spawn spawner;

    //private BufferedImage level = null;


    public Game() {

        handler = new Handler();
        this.addKeyListener(new KeyInput(handler));

        new Window(WIDTH, HEIGHT, "Raft", this);
        //start();
        hud = new HUD();
        spawner = new Spawn(handler, hud);
        r = new Random();
        /*
        Setting up map
         */

        handler.addObject(new Raft(WIDTH/2-20, HEIGHT/2-30, ID.Raft, handler));
        handler.addObject(new Raft(WIDTH/2-20, HEIGHT/2-10, ID.Raft, handler));
        handler.addObject(new Raft(WIDTH/2, HEIGHT/2-30, ID.Raft, handler));
        handler.addObject(new Raft(WIDTH/2, HEIGHT/2-10 , ID.Raft, handler));
        handler.addObject(new Player(WIDTH/2, HEIGHT/2-10, ID.Player, handler));
        handler.addObject(new Shark(WIDTH / 2 - 20, HEIGHT -200, ID.Shark));


        for(int i=0; i<=(Game.HEIGHT-60);i=i+20){
            int count=0;
            if(r.nextInt(100)<32 && count<=3){
                handler.addObject(new Wood((r.nextInt(WIDTH))/20*20-40, i, ID.Wood, handler));
                count++;
            }
            if(r.nextInt(100)<32 && count<=3){
                handler.addObject(new Leaf((r.nextInt(WIDTH))/20*20-40, i, ID.Leaf, handler));
                count++;
            }
            if(r.nextInt(100)<32 && count<=3){
                handler.addObject(new Waste((r.nextInt(WIDTH))/20*20-40, i, ID.Waste, handler));
                count++;
            }
            if(r.nextInt(100)<4 && count<=3){
                handler.addObject(new Barrell((r.nextInt(WIDTH))/20*20-40, i, ID.Barrell, handler));
                count++;
            }
        }


        //for (int i=0; i<50; i++){
        // handler.addObject(new Player(r.nextInt(WIDTH),r.nextInt(HEIGHT),ID.Player));
        //}

//        BufferedImageLoader loader = new BufferedImageLoader();
//        level = loader.loadImage("/level.png");
//
//        loadLevel(level);


    }

    public synchronized void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();

    }

    public void stop() {
        isRunning = false;
        try {
            thread.join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.start();
    }

    public void run() {
        this.requestFocus();

        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                //System.out.println("FPS: "+frames);
                frames = 0;

            }
        }
        stop();
    }

    public void tick() {

        handler.tick();
        hud.tick();
        spawner.tick();
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.blue);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        handler.render(g);
        hud.render(g);
        g.dispose();
        bs.show();

    }

    public static int clamp(int var, int min, int max){
        if(var>=max)
            return var=max;
        else if (var<=min)
            return var=min;
        else return var;
    }

    public static void main(String[] args) {
        new Game();
    }
}
