package game;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

class CubeRunner extends Graphics3DEngine implements KeyListener, ActionListener{
     private Timer t;
     int count = 0;
     boolean removeNegZ;
     boolean moveLeft;
     boolean moveRight;
     double maxRot = 0.25;
     double sideSlideRate = 1.2;
     double rotRate = 0.04;
     double moveRate = 1.4;
     double spawnRate = 2;
     boolean rightFirst = false;
     boolean gameOver;
     boolean invertedColors;
     long lastFrame;
     double fps;
     int score;
     int highScore;
     static JFrame window;
     public static void main(String[] args){
          window = new JFrame("Cube Runner");
          CubeRunner board = new CubeRunner();
          window.add(board);
          window.pack();
          window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          window.setLocationRelativeTo(null);
          window.setVisible(true);
          window.setResizable(false);
          window.addKeyListener(board);
          board.t = new Timer(20, board);
          board.removeNegZ = true;
          board.start();
          board.repaint();
     }
     CubeRunner(){
          super(780,580);
          highScore=0;
     }
     public void start(){
          count = 0;
          ztheta = 0;
          score = 0;
          gameOver=false;
          edges = new ArrayList<Line3D>();
          faces = new ArrayList<Face3D>();
          t.start();
          lastFrame = System.currentTimeMillis();
     }
     public void actionPerformed(ActionEvent e){
          score+=moveRate*10;
          if(score>highScore)highScore=score;
          count++;
          moveAll(0,0,-moveRate);
          if(moveLeft&& !moveRight)rightFirst = false;
          if(!moveLeft&& moveRight)rightFirst = true;
          if(moveLeft && moveRight){
               if(rightFirst){
                    moveAll(-sideSlideRate,0,0);
                    if(ztheta > -0.25)ztheta-=rotRate;
               }else{
                    moveAll(sideSlideRate,0,0);
                    if(ztheta < .25)ztheta+=rotRate;
               }
          }
          else if(moveLeft){
               moveAll(sideSlideRate,0,0);
               if(ztheta < .25)ztheta+=rotRate;
          }
          else if(moveRight){
               moveAll(-sideSlideRate,0,0);
               if(ztheta > -0.25)ztheta-=rotRate;
          }
          else{
               if(Math.abs(ztheta)<rotRate)ztheta=0;
               else if(ztheta>0)ztheta-=rotRate/2;
               else if(ztheta<0)ztheta+=rotRate/2;
          }
          if(count % spawnRate == 0){
               createCube(Math.random() * 600.0 - 300,-40,300,10 + 2 * count/1000);
          }
     }
     protected void paintComponent(Graphics g){
          invertedColors = (count%2000>1000);
          if(removeNegZ){
               for(int e = faces.size()-1; e >=0; e--){
                    Point3D[] a = faces.get(e).pts;
                    inner: for(int x = 0; x < a.length;x++){
                         if(a[x].z < 0)faces.remove(e);
                         break inner;
                    }
               }
               for(int e = edges.size()-1; e >= 0; e--){
                    if(edges.get(e).p1.z < 0 || edges.get(e).p2.z < 0)
                         edges.remove(e);
                    if(edges.get(e).p1.z<30 && edges.get(e).p1.z > 25){
                         if(Math.abs((edges.get(e).p1.rotate(0.0,0.0,ztheta).x + edges.get(e).p2.rotate(0.0,0.0,ztheta).x) / 2.0) < 3){
                              t.stop();
                              gameOver=true;
                         }
                    }
               }
          }
          sortFaces();
          if(!invertedColors)
               g.setColor(new Color(178,223,224));
          else g.setColor(Color.black);
          g.fillRect(0,0,800,600);
          Polygon p = new Polygon();
          double cos = Math.cos(-ztheta);
          double sin = Math.sin(-ztheta);
          double x2 = cos * (-100 - 400) + 400;
          double x1 = cos * (900 - 400) + 400;
          double x3 = 0;
          double x4 = 800;
          double y2 = -sin * (-100 - 400) + 320;
          double y1 = -sin * (900 - 400) + 320;
          double y3 = 600;
          double y4 = 600;
          p.addPoint((int)x1,(int)y1);
          p.addPoint((int)x2,(int)y2);
          p.addPoint((int)x3,(int)y3);
          p.addPoint((int)x4,(int)y4);
          if(!invertedColors)
               g.setColor(new Color(78,123,124));
          else g.setColor(new Color(15,15,15));
          g.fillPolygon(p);
          if(invertedColors){
               g.setColor(Color.green);
               for(int a = 0; a < edges.size();a++){
                    draw3DLine(g, edges.get(a));
               }
          }else{
               for(int a = faces.size()-1; a >= 0;a--){
                    draw3DFace(g, faces.get(a), Color.red, Color.black);
               }
          }
          drawAirPlane(g);
          if(count%20==0){
               long temp = lastFrame;
               lastFrame = System.currentTimeMillis();
               fps = ((int)((10000000.0 / (lastFrame-temp))))/500.0;
          }
          if(!invertedColors)g.setColor(Color.black);
          else g.setColor(Color.white);
          g.drawString("Score: " + score, 10, 20);
          g.drawString("High Score: " + highScore, 10, 35);
          g.drawString("Goal: 80,000", 10, 50);
          g.drawString("FPS: " + fps, 710,20);
          if(count > 150 && count%1000 < 150){
               g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, g.getFont().getSize()+15)); 
               g.drawString("Block Size Increase!!",280,50);
          }
          if(gameOver){
               g.setColor(Color.RED);
               g.setFont(new Font(g.getFont().getFontName(), Font.PLAIN, 18)); 
               g.drawString("Press Space to try again", 310, 235);
          }
     }
     public void drawAirPlane(Graphics g){
          Polygon p = new Polygon();
          p.addPoint(400,550);
          p.addPoint(430,570);
          p.addPoint(400,560);
          p.addPoint(370,570);
          g.setColor(Color.orange);
          g.fillPolygon(p);
          g.setColor(Color.black);
          g.drawPolygon(p);
     }
     public void keyPressed(KeyEvent e){
          switch(e.getKeyCode()){
               case KeyEvent.VK_LEFT: moveLeft=true; break;
               case KeyEvent.VK_RIGHT: moveRight=true; break;
               case KeyEvent.VK_SPACE: if(gameOver)start();break;
          }
     }
     public void keyReleased(KeyEvent e){
          switch(e.getKeyCode()){
               case KeyEvent.VK_LEFT: moveLeft=false; break;
               case KeyEvent.VK_RIGHT: moveRight=false; break;
          }
     }
     public void keyTyped(KeyEvent e){}
}