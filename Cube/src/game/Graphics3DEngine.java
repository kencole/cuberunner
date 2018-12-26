package game;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class Graphics3DEngine extends JComponent{
     int windowX;
     int windowY;
     double ztheta;
     double xtheta;
     double ytheta;
     ArrayList<Line3D> edges;
     ArrayList<Face3D> faces;
     Graphics3DEngine(){
          edges = new ArrayList<Line3D>();
          faces = new ArrayList<Face3D>();
     }
     Graphics3DEngine(int x, int y){
          this();
          windowX = x;
          windowY = y;
     }
     public void createRowCube(int num, double x, double y, double z, double size){
          for(int a = 0; a < num; a ++){
               createCube(x + a * size, y, z, size);
          }
     }
     public void createPlaneCube(int numx, int numz, double x, double y, double z, double size){
          for(int a = 0; a < numz; a++){
               createRowCube(numx, x, y, z + a * size, size);
          }
     }
     
     public void moveAll(double x, double y, double z){
          for(int e = 0; e < edges.size(); e++){
               edges.get(e).p1.x+=x;
               edges.get(e).p1.y+=y;
               edges.get(e).p1.z+=z;
               edges.get(e).p2.x+=x;
               edges.get(e).p2.y+=y;
               edges.get(e).p2.z+=z;
          }
          for(int a = 0; a < faces.size(); a++){
               faces.get(a).updateDistance();
          }
          repaint();
     }
//     public void keyPressed(KeyEvent e){
//          switch(e.getKeyCode()){
//               case KeyEvent.VK_LEFT: for(Line3D l : edges){l.p1.x--; l.p2.x--;}break;
//               case KeyEvent.VK_RIGHT: for(Line3D l : edges){l.p1.x++; l.p2.x++;}break;
//          }
//          repaint();
//     }
     public void createCube(double x, double y, double z, double size){
          Point3D A = new Point3D(x,y,z);
          Point3D B = new Point3D(x,y+size,z);
          Point3D C = new Point3D(x,y+size,z+size);
          Point3D D = new Point3D(x,y,z+size);
          Point3D E = new Point3D(x+size,y,z+size);
          Point3D F = new Point3D(x+size,y+size,z+size);
          Point3D G = new Point3D(x+size,y+size,z);
          Point3D H = new Point3D(x+size,y,z);
          edges.add(new Line3D(A,B));
          edges.add(new Line3D(B,C));
          edges.add(new Line3D(C,D));
          edges.add(new Line3D(D,A));
          edges.add(new Line3D(A,H));
          edges.add(new Line3D(D,E));
          edges.add(new Line3D(C,F));
          edges.add(new Line3D(B,G));
          edges.add(new Line3D(G,F));
          edges.add(new Line3D(F,E));
          edges.add(new Line3D(G,H));
          edges.add(new Line3D(H,E));
          Point3D[] f1 = {A,B,C,D};
          Point3D[] f2 = {E,F,G,H};
          Point3D[] f3 = {A,B,G,H};
          Point3D[] f4 = {B,C,F,G};
          Point3D[] f5 = {C,D,E,F};
          Point3D[] f6 = {A,D,E,H};
          faces.add(new Face3D(f1));
          faces.add(new Face3D(f2));
          faces.add(new Face3D(f3));
          faces.add(new Face3D(f4));
          faces.add(new Face3D(f5));
          faces.add(new Face3D(f6));
     }
     public Dimension getPreferredSize(){
          return new Dimension(windowX,windowY);
     }     
     public void draw3DLine(Graphics g, Line3D l){
          Line3D l1 = new Line3D(l.p1.rotate(xtheta,ytheta,ztheta), l.p2.rotate(xtheta,ytheta,ztheta));
          Line2D l2 = l1.convertTo2D();
          g.drawLine((int)(l2.p1.x  + windowX/2), 
                     (int)(l2.p1.y * -1 + windowY/2), 
                     (int)(l2.p2.x + windowX/2), 
                     (int)(l2.p2.y * -1 + windowY/2));
     }
     public void sortFaces(){
          if(faces.size() <1)return;
          int j, k;
          for (j = faces.size() - 1; j > 0; j--)
          {
               int pos = j;
               for (k=j-1; k>=0; k--)
               {
                    if (faces.get(k).distance > faces.get(pos).distance)
                    {
                         pos = k;
                    }
               }
               Collections.swap(faces, j, pos);
          }
     }
     public void draw3DFace(Graphics g, Face3D f, Color fill, Color outline){
          Point3D[] pts = new Point3D[f.pts.length];
          for(int x = 0; x < pts.length;x++){
               pts[x] = f.pts[x].rotate(xtheta,ytheta,ztheta);
          }
          Face2D f1 = new Face3D(pts).convertTo2D();
          int[] arx = new int[pts.length];
          int[] ary = new int[pts.length];
          for(int x = 0; x < pts.length;x++){
               arx[x] = (int) f1.pts[x].x + windowX/2;
               ary[x] = (int) f1.pts[x].y * -1 + windowY/2;
          }
          Polygon p = new Polygon(arx,ary,pts.length);
          g.setColor(fill);
          g.fillPolygon(p);
          g.setColor(outline);
          g.drawPolygon(p);
     }
     class Face3D{
          Point3D[] pts;
          double distance;
          Face3D(Point3D[] pts){
               this.pts = pts;
               updateDistance();
          }
          private void updateDistance(){
               double d = 0;
               for(Point3D p : pts){
                    d += p.x * p.x + p.y * p.y + p.z * p.z;
               }
               distance = d/pts.length;
          }
          public Face2D convertTo2D(){
               return new Face2D(pts);
          }
     }
     class Face2D{
          Point2D[] pts;
          Face2D(Point3D[] pts){
               this.pts = new Point2D[pts.length];
               for(int x = 0; x < pts.length;x++){
                    this.pts[x] = pts[x].convertTo2D();
               }
          }
     }
     class Point3D{
          double x;
          double y;
          double z;
          Point3D rotate(double xtheta, double ytheta, double ztheta){
               double cos = Math.cos(ztheta);
               double sin = Math.sin(ztheta);
               double nx = cos * x + sin * y;
               double ny = cos * y - sin * x;
               return new Point3D(nx, ny, z);
          }
          Point2D convertTo2D(){
               double z1 = z;
               if(z1<0)z1*=-1;
               double f = 250.0/z1;
               return new Point2D((x * f),(y * f));
          }
          Point3D(double x, double y, double z){
               this.x = x;
               this.y = y;
               this.z = z;
          }
     }
     class Point2D{
          double x;
          double y;
          Point2D(double x, double y){
               this.x = x;
               this.y = y;
          }
     }
     class Line3D{
          Point3D p1;
          Point3D p2;
          Line2D convertTo2D(){
               return new Line2D(p1.convertTo2D(), p2.convertTo2D());
          }
          Line3D(Point3D p1, Point3D p2){
               this.p1 = p1;
               this.p2 = p2;
          }
     }
     class Line2D{
          Point2D p1;
          Point2D p2;
          Line2D(Point2D p1, Point2D p2){
               this.p1 = p1;
               this.p2 = p2;
          }
     }
}

